import { useEffect, useRef, useState, type KeyboardEvent, type PointerEvent } from 'react'

export type ChartPoint = {
  x: number
  y: number
  label: string
}

type Props = {
  title: string
  points: ChartPoint[]
  color: string
  unit: string
  xTickFormat: (x: number) => string
  yDigits?: number
  zeroBaseline?: boolean
  integerX?: boolean
  xFromZero?: boolean
  height?: number
  compact?: boolean
}

const MARGIN = { top: 14, right: 16, bottom: 28, left: 48 }

const plotClass = 'relative outline-none touch-pan-y focus-visible:rounded focus-visible:outline-2 focus-visible:outline-solid focus-visible:outline-(--cyan) focus-visible:outline-offset-3'
const gridClass = 'stroke-[#1f2f40] stroke-1'
const tickClass = 'fill-(--text-muted) text-[11px] font-(family-name:--mono) tabular-nums'

export function LineChart({ title, points, color, unit, xTickFormat, yDigits = 2, zeroBaseline = true, integerX = false, xFromZero = false, height = 220, compact = false }: Props) {
  const wrapRef = useRef<HTMLDivElement>(null)
  const [width, setWidth] = useState(0)
  const [active, setActive] = useState<number | null>(null)

  useEffect(() => {
    const element = wrapRef.current
    if (!element) return
    const observer = new ResizeObserver(([entry]) => setWidth(entry.contentRect.width))
    observer.observe(element)
    return () => observer.disconnect()
  }, [])

  const figureClass = `m-0 min-w-0 border border-(--line) rounded-(--radius-card) bg-[linear-gradient(145deg,#111b28_0%,#0f1925_100%)] ${compact ? 'px-3.5 pt-2.5 pb-1.5' : 'px-4 pt-3.5 pb-2.5'}`
  const captionClass = `eyebrow ${compact ? 'mb-1!' : 'mb-2!'}`

  if (points.length === 0) {
    return <figure className={figureClass}>
      <figcaption className={captionClass}>{title}</figcaption>
      <div ref={wrapRef} className={plotClass} style={{ height }}>
        <p className="m-0 grid h-full place-items-center text-(length:--text-small) text-(--text-muted)">Sem dados suficientes para este gráfico.</p>
      </div>
    </figure>
  }

  const xs = points.map((point) => point.x)
  const ys = points.map((point) => point.y)
  const xMin = xFromZero ? Math.min(0, ...xs) : Math.min(...xs)
  const xMax = Math.max(...xs)
  const yTicks = niceTicks(zeroBaseline ? Math.min(0, ...ys) : Math.min(...ys), Math.max(...ys))
  const yMin = yTicks[0]
  const yMax = yTicks[yTicks.length - 1]
  const xTicks = xTickValues(xMin, xMax, Math.max(2, Math.floor(width / 90)), integerX)

  const plotWidth = Math.max(0, width - MARGIN.left - MARGIN.right)
  const plotHeight = height - MARGIN.top - MARGIN.bottom
  const scaleX = (x: number) => MARGIN.left + (xMax === xMin ? plotWidth / 2 : ((x - xMin) / (xMax - xMin)) * plotWidth)
  const scaleY = (y: number) => MARGIN.top + plotHeight - ((y - yMin) / (yMax - yMin)) * plotHeight

  const line = points.map((point, index) => `${index === 0 ? 'M' : 'L'}${scaleX(point.x)},${scaleY(point.y)}`).join(' ')
  const area = `${line} L${scaleX(points[points.length - 1].x)},${scaleY(yMin)} L${scaleX(points[0].x)},${scaleY(yMin)} Z`
  const last = points[points.length - 1]
  const activePoint = active == null ? null : points[active]

  function nearest(clientX: number) {
    const left = wrapRef.current?.getBoundingClientRect().left ?? 0
    const x = clientX - left
    let best = 0
    points.forEach((point, index) => {
      if (Math.abs(scaleX(point.x) - x) < Math.abs(scaleX(points[best].x) - x)) best = index
    })
    return best
  }

  function onKeyDown(event: KeyboardEvent) {
    if (event.key !== 'ArrowLeft' && event.key !== 'ArrowRight') return
    event.preventDefault()
    const step = event.key === 'ArrowRight' ? 1 : -1
    setActive((current) => Math.min(points.length - 1, Math.max(0, (current ?? (step > 0 ? -1 : points.length)) + step)))
  }

  return <figure className={figureClass}>
    <figcaption className={captionClass}>{title}</figcaption>
    <div
      ref={wrapRef}
      className={plotClass}
      style={{ height }}
      tabIndex={0}
      role="img"
      aria-label={`${title}: ${points.length} pontos, de ${formatValue(Math.min(...ys), yDigits)} a ${formatValue(Math.max(...ys), yDigits)} ${unit}. Use as setas para ler cada ponto.`}
      onPointerMove={(event: PointerEvent) => setActive(nearest(event.clientX))}
      onPointerLeave={() => setActive(null)}
      onKeyDown={onKeyDown}
      onBlur={() => setActive(null)}
    >
      {width > 0 && <svg width={width} height={height} aria-hidden="true">
        {yTicks.map((tick) => <g key={tick}>
          <line className={gridClass} x1={MARGIN.left} x2={width - MARGIN.right} y1={scaleY(tick)} y2={scaleY(tick)} />
          <text className={tickClass} x={MARGIN.left - 8} y={scaleY(tick)} textAnchor="end" dominantBaseline="middle">
            {formatValue(tick, tickDigits(yTicks))}
          </text>
        </g>)}
        {xTicks.map((tick) => <text key={tick} className={tickClass} x={scaleX(tick)} y={height - 8} textAnchor="middle">
          {xTickFormat(tick)}
        </text>)}

        <path d={area} fill={color} opacity={0.1} />
        <path d={line} fill="none" stroke={color} strokeWidth={2} strokeLinejoin="round" strokeLinecap="round" />

        {activePoint && <line className="stroke-[#5d7389] stroke-1" x1={scaleX(activePoint.x)} x2={scaleX(activePoint.x)} y1={MARGIN.top} y2={MARGIN.top + plotHeight} />}
        {(activePoint ? [activePoint] : [last]).map((point) => (
          <circle key="marker" cx={scaleX(point.x)} cy={scaleY(point.y)} r={4} fill={color} stroke="var(--surface)" strokeWidth={2} />
        ))}
      </svg>}

      {activePoint && <div
        className="pointer-events-none absolute grid -translate-x-1/2 -translate-y-full grid-cols-[auto_auto] items-center gap-x-[7px] gap-y-0.5 whitespace-nowrap rounded-(--radius-control) border border-[#304255] bg-[#0b131e] px-2.5 py-[7px] shadow-[0_8px_20px_#00000055]"
        style={{
          left: Math.min(Math.max(scaleX(activePoint.x), 70), width - 70),
          top: Math.max(scaleY(activePoint.y) - 12, 0),
        }}
      >
        <span className="h-0.5 w-3 rounded-[1px]" style={{ background: color }} />
        <strong className="font-(family-name:--mono) text-(length:--text-small) leading-[normal] font-semibold text-(--text)">{formatValue(activePoint.y, yDigits)} {unit}</strong>
        <span className="col-start-2 text-(length:--text-caption) text-(--text-secondary)">{activePoint.label}</span>
      </div>}
    </div>
  </figure>
}

function formatValue(value: number, digits: number) {
  return value.toFixed(digits)
}

function niceTicks(min: number, max: number, count = 4) {
  if (min === max) {
    const pad = Math.abs(min) * 0.1 || 1
    min -= pad
    max += pad
  }
  const rough = (max - min) / count
  const magnitude = 10 ** Math.floor(Math.log10(rough))
  const step = [1, 2, 2.5, 5, 10].map((factor) => factor * magnitude).find((candidate) => candidate >= rough)!
  const start = Math.floor(min / step) * step
  const end = Math.ceil(max / step) * step
  const ticks: number[] = []
  for (let tick = start; tick <= end + step / 2; tick += step) ticks.push(Number(tick.toFixed(10)))
  return ticks
}

function tickDigits(ticks: number[]) {
  const step = ticks.length > 1 ? Number((ticks[1] - ticks[0]).toFixed(10)) : 1
  const decimals = String(step).split('.')[1]?.length ?? 0
  return Math.min(3, decimals)
}

function xTickValues(min: number, max: number, count: number, integer: boolean) {
  if (min === max) return [min]
  if (integer) {
    const step = Math.max(1, Math.ceil((max - min) / count))
    const ticks: number[] = []
    for (let tick = Math.ceil(min); tick <= max; tick += step) ticks.push(tick)
    return ticks
  }
  return niceTicks(min, max, count).filter((tick) => tick >= min && tick <= max)
}
