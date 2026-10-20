import { Client, ReconnectionTimeMode } from '@stomp/stompjs'
import { useEffect, useState } from 'react'

const DESTINATIONS = [
  ['/topic/run-started', 'started'],
  ['/topic/telemetry', 'telemetry'],
  ['/topic/run-finished', 'finished'],
  ['/topic/run-interrupted', 'interrupted'],
] as const

export type RealtimeMessage = { kind: 'started' | 'telemetry' | 'finished' | 'interrupted'; payload: Record<string, unknown>; receivedAt: number }

function isValidTelemetry(payload: Record<string, unknown>) {
  const position = payload.position
  const validNumber = (value: unknown) => typeof value === 'number' && Number.isFinite(value) && value >= 0
  return validNumber(payload.runId) && validNumber(payload.sequence) &&
    typeof payload.timestamp === 'string' && Number.isFinite(Date.parse(payload.timestamp)) &&
    position !== null && typeof position === 'object' &&
    validNumber((position as Record<string, unknown>).row) && validNumber((position as Record<string, unknown>).column) &&
    validNumber(payload.distanceTravelledMeters) && validNumber(payload.currentSpeedMetersPerSecond) &&
    validNumber(payload.batteryVoltageVolts) && validNumber(payload.currentMilliAmps)
}

export function useTelemetryRealtime() {
  const [connected, setConnected] = useState(false)
  const [lastMessageAt, setLastMessageAt] = useState<number | null>(null)
  const [message, setMessage] = useState<RealtimeMessage | null>(null)

  useEffect(() => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const client = new Client({
      brokerURL: `${protocol}//${window.location.host}/ws`,
      connectionTimeout: 8_000,
      reconnectDelay: 1_000,
      reconnectTimeMode: ReconnectionTimeMode.EXPONENTIAL,
      maxReconnectDelay: 30_000,
      heartbeatIncoming: 10_000,
      heartbeatOutgoing: 10_000,
      onConnect: () => {
        setConnected(true)
        DESTINATIONS.forEach(([destination, kind]) => client.subscribe(destination, (frame) => {
          try {
            const payload: unknown = JSON.parse(frame.body)
            if (!payload || typeof payload !== 'object' || Array.isArray(payload)) return
            if (kind === 'telemetry' && !isValidTelemetry(payload as Record<string, unknown>)) return
            const receivedAt = Date.now()
            if (kind === 'telemetry') setLastMessageAt(receivedAt)
            if (kind === 'started') setLastMessageAt(null)
            setMessage({ kind, payload: payload as Record<string, unknown>, receivedAt })
          } catch (error) {
            console.error(`[realtime] invalid JSON from ${destination}`, error)
          }
        }))
      },
      onWebSocketClose: () => setConnected(false),
      onWebSocketError: () => setConnected(false),
      onStompError: (frame) => console.error('[realtime] broker error:', frame.headers.message, frame.body),
    })
    client.activate()
    return () => { void client.deactivate() }
  }, [])

  return { connected, lastMessageAt, message }
}
