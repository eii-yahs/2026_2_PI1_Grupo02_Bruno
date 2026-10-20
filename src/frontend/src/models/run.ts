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

export const statusLabels: Record<RunStatus, string> = {
  START_REQUESTED: 'Início solicitado',
  IN_PROGRESS: 'Em andamento',
  INTERRUPT_REQUESTED: 'Interrupção solicitada',
  COMPLETED: 'Concluída',
  FAILED: 'Falhou',
  INTERRUPTED: 'Interrompida',
}

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
  if (total == null) return '—'
  const minutes = Math.floor(total / 60)
  const seconds = Math.floor(total % 60)
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

export function formatDateTime(iso: string | null) {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'medium' })
}

export function formatNumber(value: number | null, digits = 2) {
  return value == null ? '—' : value.toLocaleString('pt-BR', { minimumFractionDigits: digits, maximumFractionDigits: digits })
}

export function challengeLabel(run: Run) {
  if (run.challengeCompleted == null) return '—'
  return run.challengeCompleted ? 'Sim' : 'Não'
}
