import { Icon } from './Icon'
import { MetricCard } from './MetricCard'
import type { RunResult, Telemetry } from '../types/telemetry'
import { formatNumber } from '../utils/format'
import { getRunResultIcon, getRunStatusAppearance } from '../utils/runStatus'

type Props = {
  telemetry: Telemetry
  clock: string
  result: RunResult
}

export function ExecutionMetrics({ telemetry, clock, result }: Props) {
  const isBatteryLow = telemetry.batteryStatus === 'LOW'
  const appearance = getRunStatusAppearance(result)
  const resultTone = appearance === 'success'
    ? 'border-l-green text-green'
    : appearance === 'active'
      ? 'border-l-amber text-amber'
      : appearance === 'failure'
        ? 'border-l-danger text-danger'
        : 'border-l-[#6a7067] text-text'
  const ResultIcon = getRunResultIcon(result)
  const isResultActive = result === 'Em andamento' || result === 'Início solicitado' || result === 'Interrupção solicitada'
  const resultLabel = result === 'Sucesso' ? 'Concluído' : result
  const batteryVoltage = telemetry.batteryVoltageVolts

  return (
    <aside className="flex flex-col gap-2.5" aria-label="Dados da execução">
      <div className="rounded-[4px] border border-line bg-surface p-[13px]">
        <div className="flex items-baseline justify-between gap-3">
          <span className="text-caption text-secondary">Consumo da bateria</span>
          <span className={`whitespace-nowrap font-mono text-sm leading-[1.2] font-medium tabular-nums ${isBatteryLow ? 'text-danger' : 'text-green'}`}>
            {formatNumber(telemetry.chargeConsumedMilliampHours, 1)} <small className="text-caption font-normal text-secondary">mAh</small>
          </span>
        </div>
        <div className="mt-[9px] flex items-baseline justify-between gap-3 border-t border-line pt-[9px]">
          <span className="text-caption text-secondary">Tensão da bateria</span>
          <span className={`whitespace-nowrap font-mono text-sm leading-[1.2] font-medium tabular-nums ${isBatteryLow ? 'text-danger' : 'text-green'}`}>
            {batteryVoltage == null ? '—' : formatNumber(batteryVoltage, 1)} <small className="text-caption font-normal text-secondary">V</small>
          </span>
        </div>
        {isBatteryLow && (
          <p className="mt-2.5 mb-0 flex items-center gap-1.5 text-small text-danger">
            <Icon name="alert" /> Bateria em nível crítico
          </p>
        )}
      </div>

      <MetricCard
        label="Velocidade média"
        value={formatNumber(telemetry.averageSpeedMetersPerSecond)}
        unit="m/s"
        icon="speed"
      />
      <MetricCard label="Tempo de execução" value={clock} icon="time" mono />

      <div className={`rounded-[4px] border border-line border-l-[3px] bg-surface p-[13px] ${resultTone}`}>
        <span className="text-caption text-secondary">Resultado do desafio</span>
        <div className="mt-2 flex items-center gap-2 text-body font-semibold">
          <span className={`grid size-[21px] place-items-center rounded-full ${appearance === 'success' ? 'bg-green text-[#1b2118]' : appearance === 'active' ? 'bg-amber text-[#211c14]' : appearance === 'failure' ? 'bg-danger text-[#241715]' : 'bg-[#353026] text-amber'}`}><ResultIcon className={`size-[13px] stroke-[2.5] ${isResultActive ? 'animate-spin' : ''}`} aria-hidden="true" /></span>
          {resultLabel}
        </div>
      </div>
    </aside>
  )
}
