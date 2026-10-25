import { Client, ReconnectionTimeMode } from '@stomp/stompjs'
import { useEffect, useState } from 'react'

const DESTINATIONS = [
  ['/topic/run-started', 'started'],
  ['/topic/telemetry', 'telemetry'],
  ['/topic/run-finished', 'finished'],
  ['/topic/run-interrupted', 'interrupted'],
  ['/topic/run-status', 'status'],
] as const

// RNF31: heartbeat de aplicação a cada 2 s; conexão indisponível após 6 s sem heartbeat válido.
const HEARTBEAT_DESTINATION = '/topic/heartbeat'
const CLIENT_HEARTBEAT_DESTINATION = '/app/heartbeat'
const HEARTBEAT_INTERVAL_MS = 2_000
const HEARTBEAT_TIMEOUT_MS = 6_000

export type RealtimeMessage = { kind: 'started' | 'telemetry' | 'finished' | 'interrupted' | 'status'; payload: Record<string, unknown>; receivedAt: number }

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
    let lastHeartbeatAt = 0
    let heartbeatTimer: number | undefined
    let watchdogTimer: number | undefined
    const stopTimers = () => {
      window.clearInterval(heartbeatTimer)
      window.clearInterval(watchdogTimer)
    }
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
        lastHeartbeatAt = Date.now()
        stopTimers()
        client.subscribe(HEARTBEAT_DESTINATION, (frame) => {
          try {
            const payload: unknown = JSON.parse(frame.body)
            if (payload && typeof payload === 'object' && 'sequence' in payload) {
              lastHeartbeatAt = Date.now()
              setConnected(true)
            }
          } catch (error) {
            console.error('[realtime] invalid heartbeat', error)
          }
        })
        heartbeatTimer = window.setInterval(() => {
          client.publish({
            destination: CLIENT_HEARTBEAT_DESTINATION,
            body: JSON.stringify({ timestamp: new Date().toISOString() }),
          })
        }, HEARTBEAT_INTERVAL_MS)
        watchdogTimer = window.setInterval(() => {
          if (Date.now() - lastHeartbeatAt > HEARTBEAT_TIMEOUT_MS) {
            setConnected(false)
            client.webSocket?.close()
          }
        }, 1_000)
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
      onWebSocketClose: () => {
        stopTimers()
        setConnected(false)
      },
      onWebSocketError: () => setConnected(false),
      onStompError: (frame) => console.error('[realtime] broker error:', frame.headers.message, frame.body),
    })
    client.activate()
    return () => {
      stopTimers()
      void client.deactivate()
    }
  }, [])

  return { connected, lastMessageAt, message }
}
