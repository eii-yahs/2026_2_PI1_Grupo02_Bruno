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
  const resultClass = `result-${getRunStatusAppearance(result)}`
  const resultIcon = getRunResultIcon(result)
  const resultLabel = result === 'Sucesso' ? 'Concluído' : result
  const batteryVoltage = telemetry.batteryVoltageVolts

  return (
    <aside className="metrics-column" aria-label="Dados da execução">
      <div className={`battery-card ${isBatteryLow ? 'battery-low' : ''}`}>
        <div className="battery-metric">
          <span className="metric-label">Consumo da bateria</span>
          <span className="battery-value">
            {formatNumber(telemetry.chargeConsumedMilliampHours, 1)} <small>mAh</small>
          </span>
        </div>
        <div className="battery-metric">
          <span className="metric-label">Tensão da bateria</span>
          <span className="battery-value">
            {batteryVoltage == null ? '—' : formatNumber(batteryVoltage, 1)} <small>V</small>
          </span>
        </div>
        {isBatteryLow && (
          <p className="battery-warning">
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

      <div className={`result-card ${resultClass}`}>
        <span className="metric-label">Resultado do desafio</span>
        <div className="result-value">
          <span className="result-icon">{resultIcon}</span>
          {resultLabel}
        </div>
      </div>
    </aside>
  )
}
