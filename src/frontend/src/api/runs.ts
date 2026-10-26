import type { Run, TelemetrySample } from '../models/run'
import type { MazeSize } from '../types/maze'

async function postRunAction(path: string, body?: unknown): Promise<Run> {
  const response = await fetch(path, {
    method: 'POST',
    headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  if (!response.ok) throw new Error(`Erro ${response.status} ao solicitar ação da execução`)
  return response.json()
}

export function startRun(mazeType: MazeSize): Promise<Run> {
  return postRunAction('/api/runs', { mazeType })
}

export function interruptRun(runId: number): Promise<Run> {
  return postRunAction(`/api/runs/${runId}/interrupt`)
}

export async function fetchActiveRun(): Promise<Run | null> {
  const response = await fetch('/api/runs/active')
  if (response.status === 404) return null
  if (!response.ok) throw new Error(`Erro ${response.status} ao consultar a execução ativa`)
  return response.json()
}

// Sem mazeType: todas as execuções (consulta geral). Com mazeType: só as daquele labirinto (consulta por labirinto)
export async function fetchRuns(mazeType?: MazeSize): Promise<Run[]> {
  const response = await fetch(mazeType ? `/api/runs?mazeType=${mazeType}` : '/api/runs')
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
