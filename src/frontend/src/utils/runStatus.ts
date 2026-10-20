import type { RunResult } from '../types/telemetry'

export type RunStatusAppearance = 'success' | 'active' | 'failure' | 'neutral'

export function getRunStatusAppearance(result: RunResult): RunStatusAppearance {
  if (result === 'Sucesso') return 'success'
  if (result === 'Em andamento') return 'active'
  if (result === 'Interrompido' || result === 'Não concluído') return 'failure'

  return 'neutral'
}

export function getRunResultIcon(result: RunResult) {
  if (result === 'Sucesso') return '✓'
  if (result === 'Em andamento') return '↗'
  if (result === 'Interrompido' || result === 'Não concluído') return '×'

  return '—'
}
