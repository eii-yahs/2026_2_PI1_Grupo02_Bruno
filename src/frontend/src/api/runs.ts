import type { Run, TelemetrySample } from '../models/run'

export async function fetchRuns(): Promise<Run[]> {
  const response = await fetch('/api/runs')
  if (!response.ok) throw new Error(`Erro ${response.status} ao consultar as execuções`)
  return response.json()
}

// Retorna null quando a execução não existe (404)
export async function fetchRun(runId: number): Promise<Run | null> {
  const response = await fetch(`/api/runs/${runId}`)
  if (response.status === 404) return null
  if (!response.ok) throw new Error(`Erro ${response.status} ao consultar a execução ${runId}`)
  return response.json()
}

// Amostras de telemetria armazenadas da execução, na ordem de envio (sequence)
export async function fetchRunTelemetry(runId: number): Promise<TelemetrySample[]> {
  const response = await fetch(`/api/runs/${runId}/telemetry`)
  if (!response.ok) throw new Error(`Erro ${response.status} ao consultar a telemetria da execução ${runId}`)
  return response.json()
}
