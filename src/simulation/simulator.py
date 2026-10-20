#!/usr/bin/env python3
"""MQTT Micromouse simulator for the Rato Cego backend."""

from __future__ import annotations

import argparse
import json
import math
import os
import threading
import time
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import urlparse

import paho.mqtt.client as mqtt


MAZES = {
    "GRID_4X4": (4, 4),
    "GRID_8X4": (4, 8),
    "GRID_12X4": (4, 12),
}


def load_backend_env() -> dict[str, str]:
    """Read simple key/value settings from src/backend/.env, if present."""
    env_path = Path(__file__).resolve().parents[1] / "backend" / ".env"
    values: dict[str, str] = {}
    if not env_path.exists():
        return values
    for raw_line in env_path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        values[key.strip()] = value.strip().strip("\"'")
    return values


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat(timespec="milliseconds").replace("+00:00", "Z")


def heading_between(start: tuple[int, int], end: tuple[int, int]) -> str:
    row_delta = end[0] - start[0]
    column_delta = end[1] - start[1]
    if column_delta > 0:
        return "EAST"
    if column_delta < 0:
        return "WEST"
    return "SOUTH" if row_delta > 0 else "NORTH"


def serpentine_path(rows: int, columns: int) -> list[tuple[int, int]]:
    """Build a continuous route from the lower-left cell across the maze."""
    path: list[tuple[int, int]] = []
    for row_index, row in enumerate(range(rows - 1, -1, -1)):
        columns_in_order = range(columns) if row_index % 2 == 0 else range(columns - 1, -1, -1)
        path.extend((row, column) for column in columns_in_order)
    return path


class MicromouseSimulator:
    def __init__(self, args: argparse.Namespace) -> None:
        self.args = args
        self.prefix = args.topic_prefix.strip("/")
        self.lock = threading.Lock()
        self.active_run: dict | None = None
        self.event_id = time.time_ns() // 1_000_000 * 1000
        self.client = mqtt.Client(
            mqtt.CallbackAPIVersion.VERSION2,
            client_id=f"ratocego-simulator-{os.getpid()}",
            protocol=mqtt.MQTTv311,
        )
        if args.username:
            self.client.username_pw_set(args.username, args.password or "")
        self.client.reconnect_delay_set(min_delay=1, max_delay=15)
        self.client.on_connect = self.on_connect
        self.client.on_disconnect = self.on_disconnect
        self.client.on_message = self.on_message

    def topic(self, suffix: str) -> str:
        return f"{self.prefix}/{suffix.lstrip('/')}" if self.prefix else suffix.lstrip("/")

    def next_event_id(self) -> int:
        with self.lock:
            self.event_id += 1
            return self.event_id

    def publish(self, topic: str, payload: dict) -> None:
        info = self.client.publish(topic, json.dumps(payload, separators=(",", ":")), qos=1, retain=False)
        if info.rc != mqtt.MQTT_ERR_SUCCESS:
            print(f"[mqtt] publicação falhou em {topic}: {info.rc}", flush=True)

    def on_connect(self, client: mqtt.Client, _userdata, _flags, reason_code, _properties) -> None:
        if reason_code.is_failure:
            print(f"[mqtt] conexão recusada: {reason_code}", flush=True)
            return
        print(f"[mqtt] conectado em {self.args.host}:{self.args.port}; prefixo={self.prefix}", flush=True)
        client.subscribe([(self.topic("commands/run/start"), 1), (self.topic("commands/run/interrupt"), 1)])
        if self.args.start_maze:
            # The broker delivers this to the simulator's own subscription too.
            time.sleep(0.1)
            self.publish(self.topic("commands/run/start"), {
                "eventId": self.next_event_id(),
                "runId": self.next_event_id(),
                "mazeType": self.args.start_maze,
                "timestamp": utc_now(),
            })
            print(f"[run] comando de demonstração publicado: {self.args.start_maze}", flush=True)

    def on_disconnect(self, _client: mqtt.Client, _userdata, _flags, reason_code, _properties) -> None:
        print(f"[mqtt] desconectado: {reason_code}; tentando reconectar", flush=True)

    def on_message(self, _client: mqtt.Client, _userdata, message: mqtt.MQTTMessage) -> None:
        try:
            payload = json.loads(message.payload.decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError):
            print(f"[mqtt] JSON inválido em {message.topic}; ignorando", flush=True)
            return
        if not isinstance(payload, dict):
            print(f"[mqtt] payload inválido em {message.topic}; ignorando", flush=True)
            return

        if message.topic == self.topic("commands/run/start"):
            self.start_run(payload)
        elif message.topic == self.topic("commands/run/interrupt"):
            self.interrupt_run(payload)

    def start_run(self, command: dict) -> None:
        run_id = command.get("runId")
        maze_type = command.get("mazeType")
        if not isinstance(run_id, int) or run_id <= 0 or maze_type not in MAZES:
            print(f"[run] comando de início inválido: {command}", flush=True)
            return

        with self.lock:
            if self.active_run is not None:
                if self.active_run["runId"] == run_id:
                    print(f"[run {run_id}] comando duplicado; ignorando", flush=True)
                else:
                    print(f"[run] execução {self.active_run['runId']} ativa; ignorando runId={run_id}", flush=True)
                return
            run = {"runId": run_id, "mazeType": maze_type, "stop": threading.Event()}
            self.active_run = run

        threading.Thread(target=self.run_maze, args=(run,), daemon=True).start()

    def interrupt_run(self, command: dict) -> None:
        run_id = command.get("runId")
        with self.lock:
            run = self.active_run
            if run is None or run["runId"] != run_id:
                print(f"[run] interrupção ignorada para runId={run_id}; não é a execução ativa", flush=True)
                return
            run["stop"].set()
        print(f"[run {run_id}] interrupção solicitada", flush=True)

    def run_maze(self, run: dict) -> None:
        run_id = run["runId"]
        rows, columns = MAZES[run["mazeType"]]
        path = serpentine_path(rows, columns)
        started = time.monotonic()
        self.publish(self.topic(f"runs/{run_id}/started"), {
            "eventId": self.next_event_id(),
            "runId": run_id,
            "timestamp": utc_now(),
            "mazeRows": rows,
            "mazeColumns": columns,
        })
        print(f"[run {run_id}] iniciado: {run['mazeType']} ({rows}x{columns})", flush=True)

        interval = self.args.sample_interval
        cell_length = self.args.cell_length
        total_distance = max(0.0, (len(path) - 1) * cell_length)
        sequence = 0
        # Give the backend time to process run.started before the first sample.
        next_sample = time.monotonic() + interval

        while not run["stop"].is_set():
            delay = next_sample - time.monotonic()
            if delay > 0 and run["stop"].wait(delay):
                break

            elapsed = time.monotonic() - started
            distance = min(total_distance, elapsed * self.args.speed)
            position, heading = self.position_at(path, distance, cell_length, total_distance)
            sequence += 1
            battery_voltage = max(7.0, 8.2 - distance * 0.035)
            current = 260.0 + 30.0 * math.sin(elapsed * 1.7)
            self.publish(self.topic(f"runs/{run_id}/telemetry"), {
                "eventId": self.next_event_id(),
                "runId": run_id,
                "sequence": sequence,
                "timestamp": utc_now(),
                "position": {"row": position[0], "column": position[1], "heading": heading},
                "distanceTravelledMeters": round(distance, 4),
                "currentSpeedMetersPerSecond": round(self.args.speed if distance < total_distance else 0.0, 3),
                "batteryVoltageVolts": round(battery_voltage, 3),
                "currentMilliAmps": round(current, 2),
            })

            if distance >= total_distance:
                break
            next_sample += interval
            if next_sample < time.monotonic():
                next_sample = time.monotonic() + interval

        if run["stop"].is_set():
            self.publish(self.topic(f"runs/{run_id}/interrupted"), {
                "eventId": self.next_event_id(), "runId": run_id, "timestamp": utc_now(),
            })
            print(f"[run {run_id}] interrupção confirmada", flush=True)
        else:
            self.publish(self.topic(f"runs/{run_id}/finished"), {
                "eventId": self.next_event_id(),
                "runId": run_id,
                "timestamp": utc_now(),
                "status": "COMPLETED",
                "challengeCompleted": True,
            })
            print(f"[run {run_id}] concluído; {sequence} amostras enviadas", flush=True)

        with self.lock:
            if self.active_run is run:
                self.active_run = None

    @staticmethod
    def position_at(path: list[tuple[int, int]], distance: float, cell_length: float, total_distance: float):
        if len(path) == 1 or distance >= total_distance:
            return path[-1], heading_between(path[-2], path[-1]) if len(path) > 1 else "EAST"
        segment = min(int(distance / cell_length), len(path) - 2)
        return path[segment], heading_between(path[segment], path[segment + 1])

    def run(self) -> None:
        print("[mqtt] conectando... (Ctrl+C para sair)", flush=True)
        self.client.connect(self.args.host, self.args.port, keepalive=30)
        self.client.loop_forever(retry_first_connection=True)


def parse_args() -> argparse.Namespace:
    file_env = load_backend_env()
    parser = argparse.ArgumentParser(description="Simula um Micromouse conectado ao MQTT do backend.")
    parser.add_argument("--host", default=os.getenv("MQTT_HOST", "localhost"), help="host do broker (padrão: localhost)")
    parser.add_argument("--port", type=int, default=int(os.getenv("MQTT_PORT", "1883")), help="porta do broker")
    parser.add_argument("--username", default=os.getenv("MQTT_USERNAME", file_env.get("MQTT_USERNAME", "ratocego")))
    parser.add_argument("--password", default=os.getenv("MQTT_PASSWORD", file_env.get("MQTT_PASSWORD")))
    parser.add_argument("--topic-prefix", default=os.getenv("MQTT_TOPIC_PREFIX", file_env.get("MQTT_TOPIC_PREFIX", "ratocego")))
    parser.add_argument("--sample-interval", type=float, default=0.1, help="intervalo de telemetria em segundos (padrão: 0.1 / 10 Hz)")
    parser.add_argument("--speed", type=float, default=0.35, help="velocidade simulada em m/s")
    parser.add_argument("--cell-length", type=float, default=0.18, help="distância entre centros das células em metros")
    parser.add_argument("--start", dest="start_maze", choices=MAZES, help="publica um comando run.start de demonstração ao conectar")
    args = parser.parse_args()

    if args.sample_interval <= 0 or args.speed <= 0 or args.cell_length <= 0:
        parser.error("sample-interval, speed e cell-length devem ser maiores que zero")
    if not args.password:
        parser.error("senha MQTT ausente; use --password, MQTT_PASSWORD ou src/backend/.env")
    return args


if __name__ == "__main__":
    try:
        MicromouseSimulator(parse_args()).run()
    except KeyboardInterrupt:
        print("\n[simulator] encerrado", flush=True)
