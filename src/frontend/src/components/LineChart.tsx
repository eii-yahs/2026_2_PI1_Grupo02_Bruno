import { useEffect, useRef, useState, type KeyboardEvent, type PointerEvent } from 'react'
import './LineChart.css'

export type ChartPoint = {
  x: number
  y: number
  // Texto secundário do tooltip (ex.: "#12 · 8 × 4" ou "12,5 s")
  label: string
}

type Props = {
  title: string
  points: ChartPoint[]
  color: string
  unit: string
  xTickFormat: (x: number) => string
  yDigits?: number
  // Magnitudes (consumo, tempo, velocidade) partem do zero; grandezas como tensão usam a faixa dos dados
  zeroBaseline?: boolean
  // Eixo X de contagem (ex.: número da tentativa): só marcações inteiras
  integerX?: boolean
  // Eixo X de tempo desde o início da execução: começa em 0
  xFromZero?: boolean
  height?: number
}

const MARGIN = { top: 14, right: 16, bottom: 28, left: 48 }

// Gráfico de linha de uma série, em SVG: linha de 2px, área a 10%, grade discreta,
// marcador no último ponto e cruz de leitura (mouse, toque ou setas do teclado) com tooltip.
export function LineChart({ title, points, color, unit, xTickFormat, yDigits = 2, zeroBaseline = true, integerX = false, xFromZero = false, height = 220 }: Props) {
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

  // O contêiner medido fica sempre montado, para a largura continuar certa quando um filtro esvazia e depois repõe os dados
  if (points.length === 0) {
    return <figure className="line-chart">
      <figcaption className="eyebrow">{title}</figcaption>
      <div ref={wrapRef} className="line-chart-plot" style={{ height }}>
        <p className="line-chart-empty">Sem dados suficientes para este gráfico.</p>
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

  // A cruz de leitura vai para o ponto mais próximo no eixo X
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

  return <figure className="line-chart">
    <figcaption className="eyebrow">{title}</figcaption>
    <div
      ref={wrapRef}
      className="line-chart-plot"
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
          <line className="line-chart-grid" x1={MARGIN.left} x2={width - MARGIN.right} y1={scaleY(tick)} y2={scaleY(tick)} />
          <text className="line-chart-tick" x={MARGIN.left - 8} y={scaleY(tick)} textAnchor="end" dominantBaseline="middle">
            {formatValue(tick, tickDigits(yTicks))}
          </text>
        </g>)}
        {xTicks.map((tick) => <text key={tick} className="line-chart-tick" x={scaleX(tick)} y={height - 8} textAnchor="middle">
          {xTickFormat(tick)}
        </text>)}

        <path d={area} fill={color} opacity={0.1} />
        <path d={line} fill="none" stroke={color} strokeWidth={2} strokeLinejoin="round" strokeLinecap="round" />

        {activePoint && <line className="line-chart-crosshair" x1={scaleX(activePoint.x)} x2={scaleX(activePoint.x)} y1={MARGIN.top} y2={MARGIN.top + plotHeight} />}
        {(activePoint ? [activePoint] : [last]).map((point) => (
          <circle key="marker" cx={scaleX(point.x)} cy={scaleY(point.y)} r={4} fill={color} stroke="var(--surface)" strokeWidth={2} />
        ))}
      </svg>}

      {activePoint && <div
        className="line-chart-tooltip"
        style={{
          left: Math.min(Math.max(scaleX(activePoint.x), 70), width - 70),
          top: Math.max(scaleY(activePoint.y) - 12, 0),
        }}
      >
        <span className="line-chart-tooltip-key" style={{ background: color }} />
        <strong>{formatValue(activePoint.y, yDigits)} {unit}</strong>
        <span>{activePoint.label}</span>
      </div>}
    </div>
  </figure>
}

function formatValue(value: number, digits: number) {
  return value.toFixed(digits)
}

// Divisões "redondas" (1, 2, 2,5, 5 × 10ⁿ) cobrindo o intervalo dos dados
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

// Casas decimais suficientes para mostrar o passo exato (0,025 → 3), sem arredondar as marcações
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
