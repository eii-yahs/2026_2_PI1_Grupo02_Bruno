import { Icon } from './Icon'

type Props = {
  label: string
  value: string
  unit?: string
  icon: 'speed' | 'time'
  mono?: boolean
}

export function MetricCard({ label, value, unit, icon, mono = false }: Props) {
  const numberClass = `metric-number ${mono ? 'mono' : ''}`

  return (
    <article className="metric-card">
      <div className="metric-card-top">
        <span className="metric-label">{label}</span>
        <span className="metric-icon">
          <Icon name={icon} />
        </span>
      </div>
      <div className={numberClass}>
        {value}
        {unit && <small>{unit}</small>}
      </div>
    </article>
  )
}
