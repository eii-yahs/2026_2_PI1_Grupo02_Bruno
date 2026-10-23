import { formatElapsedTime, formatNumber } from '../utils/format'

export type RunStatus = 'START_REQUESTED' | 'IN_PROGRESS' | 'INTERRUPT_REQUESTED' | 'COMPLETED' | 'FAILED' | 'INTERRUPTED'

// Espelha o RunResponse do backend (GET /api/runs e GET /api/runs/{id})
export type Run = {
  id: number
  status: RunStatus
  startedAt: string | null
  finishedAt: string | null
  duration: string | null
  mazeRows: number | null
  mazeColumns: number | null
  challengeCompleted: boolean | null
  distanceTravelledMeters: number | null
  averageSpeedMetersPerSecond: number | null
  chargeConsumedMilliampHours: number | null
  energyConsumedWattHours: number | null
}

// Espelha o TelemetrySampleResponse do backend (GET /api/runs/{id}/telemetry)
export type TelemetrySample = {
  sequence: number
  recordedAt: string
  row: number
  column: number
  heading: string
  distanceTravelledMeters: number
  currentSpeedMetersPerSecond: number | null
  batteryVoltageVolts: number | null
  currentMilliAmps: number | null
}

export const statusLabels: Record<RunStatus, string> = {
  START_REQUESTED: 'Início solicitado',
  IN_PROGRESS: 'Em andamento',
  INTERRUPT_REQUESTED: 'Interrupção solicitada',
  COMPLETED: 'Concluída',
  FAILED: 'Falhou',
  INTERRUPTED: 'Interrompida',
}

const finishedStatuses: RunStatus[] = ['COMPLETED', 'FAILED', 'INTERRUPTED']

// Mesmo padrão do seletor de labirinto: colunas × linhas (ex.: 8 × 4)
export function mazeLabel(run: Run) {
  if (run.mazeRows == null || run.mazeColumns == null) return '—'
  return `${run.mazeColumns} × ${run.mazeRows}`
}

// O backend envia a duração em ISO-8601 (ex.: PT2M1S)
export function durationSeconds(iso: string | null) {
  const match = iso?.match(/^PT(?:(\d+)H)?(?:(\d+)M)?(?:([\d.]+)S)?$/)
  if (!match) return null
  const [, h = '0', m = '0', s = '0'] = match
  return Number(h) * 3600 + Number(m) * 60 + Number(s)
}

export function formatDuration(iso: string | null) {
  const total = durationSeconds(iso)
  return total == null ? '—' : formatElapsedTime(Math.floor(total), true)
}

export function formatDateTime(iso: string | null) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'medium' })
}

export function challengeLabel(run: Run) {
  if (run.challengeCompleted == null) return '—'
  return run.challengeCompleted ? 'Sim' : 'Não'
}

export type RunSummary = {
  total: number
  successRatePercent: number | null
  averageChargeMilliampHours: number | null
  averageDurationSeconds: number | null
}

// Métricas agregadas da Consulta Geral e da Consulta por Labirinto. A taxa de sucesso considera só execuções encerradas
// (concluídas, com falha ou interrompidas) e conta como sucesso as que concluíram o desafio.
export function summarizeRuns(runs: Run[]): RunSummary {
  const finished = runs.filter((run) => finishedStatuses.includes(run.status))
  const succeeded = finished.filter((run) => run.challengeCompleted === true)
  const charges = runs
    .map((run) => run.chargeConsumedMilliampHours)
    .filter((charge): charge is number => charge != null)
  const durations = runs
    .map((run) => durationSeconds(run.duration))
    .filter((duration): duration is number => duration != null)

  return {
    total: runs.length,
    successRatePercent: finished.length === 0 ? null : (succeeded.length / finished.length) * 100,
    averageChargeMilliampHours: average(charges),
    averageDurationSeconds: average(durations),
  }
}

function average(values: number[]) {
  return values.length === 0 ? null : values.reduce((sum, value) => sum + value, 0) / values.length
}

// Valor com unidade para as tabelas de consulta (ex.: 260.00 mAh); sem valor mostra —
export function withUnit(value: number | null, unit: string) {
  const formatted = formatNumber(value ?? undefined)
  return formatted === '—' ? formatted : `${formatted} ${unit}`
}

// Busca livre da Consulta Geral: compara com o ID, a data de início e o labirinto
export function matchesSearch(run: Run, search: string) {
  const term = search.trim().toLowerCase()
  if (!term) return true
  const maze = mazeLabel(run)
  return [String(run.id), formatDateTime(run.startedAt), maze, maze.replaceAll(' ', '').replace('×', 'x')]
    .some((text) => text.toLowerCase().includes(term))
}
