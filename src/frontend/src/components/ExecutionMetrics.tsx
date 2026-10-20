import { Icon } from './Icon'
import { MetricCard } from './MetricCard'
import type { Telemetry } from '../types/telemetry'
import { formatNumber } from '../utils/format'

type Props = {
  telemetry: Telemetry
  clock: string
  result: string
}

export function ExecutionMetrics({ telemetry, clock, result }: Props) {
  const isBatteryLow = telemetry.batteryStatus === 'LOW'
  const resultClass = result === 'Sucesso' ? 'result-success' : ''
  const resultIcon = result === 'Sucesso' ? '✓' : result === 'Em andamento' ? '↗' : '—'
  const resultLabel = result === 'Sucesso' ? 'Concluído' : result
  const batteryVoltage = telemetry.batteryVoltageVolts

  return (
    <aside className="metrics-column" aria-label="Dados da execução">
      <div className={`battery-card ${isBatteryLow ? 'battery-low' : ''}`}>
        <div className="metric-label-row">
          <span>Consumo da bateria</span>
          <span className="battery-value">
            {formatNumber(telemetry.chargeConsumedMilliampHours, 1)} <small>mAh</small>
          </span>
        </div>
        <div className="battery-foot battery-voltage">
          <span>Tensão da bateria</span>
          <span>{batteryVoltage == null ? '— V' : `${formatNumber(batteryVoltage, 1)} V`}</span>
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
