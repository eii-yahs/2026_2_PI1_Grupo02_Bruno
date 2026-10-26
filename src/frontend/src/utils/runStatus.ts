import type { RunResult } from '../types/telemetry'
import { Check, CircleX, LoaderCircle, Minus, type LucideIcon } from 'lucide-react'

export type RunStatusAppearance = 'success' | 'active' | 'failure' | 'neutral'

export function getRunStatusAppearance(result: RunResult): RunStatusAppearance {
  if (result === 'Sucesso') return 'success'
  if (result === 'Em andamento' || result === 'Início solicitado' || result === 'Interrupção solicitada') return 'active'
  if (result === 'Interrompido' || result === 'Não concluído' || result === 'Falhou') return 'failure'

  return 'neutral'
}

export function getRunResultIcon(result: RunResult): LucideIcon {
  if (result === 'Sucesso') return Check
  if (result === 'Em andamento' || result === 'Início solicitado' || result === 'Interrupção solicitada') return LoaderCircle
  if (result === 'Interrompido' || result === 'Não concluído' || result === 'Falhou') return CircleX

  return Minus
}
