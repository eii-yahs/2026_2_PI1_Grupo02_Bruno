import { Icon } from './Icon'

type Props = {
  label: string
  value: string
  unit?: string
  icon: 'speed' | 'time'
  mono?: boolean
}

export function MetricCard({ label, value, unit, icon, mono = false }: Props) {
  const isPlaceholder = value === '—' || value === '--:--'
  const numberClass = `mt-2 font-mono text-[25px] leading-[1.1] font-medium tracking-[-.04em] tabular-nums text-text max-[700px]:text-[21px] ${mono ? 'font-mono' : ''} ${isPlaceholder ? 'text-body font-normal tracking-normal text-muted' : ''}`

  return (
    <article className="rounded-[4px] border border-line bg-surface p-[13px]">
      <div className="flex items-center justify-between">
        <span className="text-caption text-secondary">{label}</span>
        <span className="text-secondary">
          <Icon name={icon} />
        </span>
      </div>
      <div className={numberClass}>
        {value}
        {unit && <small className="ml-[7px] font-mono text-caption tracking-normal text-secondary">{unit}</small>}
      </div>
    </article>
  )
}
