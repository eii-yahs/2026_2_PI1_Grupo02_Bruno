package br.edu.pi1.ratocego.dto.websocket;

public sealed interface WebSocketUpdate
        permits RunFinishedUpdate, RunInterruptedUpdate, RunStartedUpdate, TelemetryUpdate, RunCommandStatusUpdate {
}
