import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

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

const plotClass = 'relative touch-pan-y outline-none focus-visible:rounded focus-visible:outline-2 focus-visible:outline-solid focus-visible:outline-cyan focus-visible:outline-offset-3'

export function LineChart({
  title,
  points,
  color,
  unit,
  xTickFormat,
  yDigits = 2,
  zeroBaseline = true,
  integerX = false,
  xFromZero = false,
  height = 220,
  compact = false,
}: Props) {
  const figureClass = `m-0 min-w-0 rounded-[4px] border border-line bg-surface ${compact ? 'px-3.5 pt-2.5 pb-1.5' : 'px-4 pt-3.5 pb-2.5'}`
  const captionClass = `font-mono text-caption leading-[1.4] tracking-[.04em] text-muted ${compact ? 'mb-1' : 'mb-2'}`

  if (points.length === 0) {
    return (
      <figure className={figureClass}>
        <figcaption className={captionClass}>{title}</figcaption>
        <div className={plotClass} style={{ height }}>
              <p className="m-0 grid h-full place-items-center text-small text-muted">
            Sem dados suficientes para este gráfico.
          </p>
        </div>
      </figure>
    )
  }

  const xs = points.map(({ x }) => x)
  const ys = points.map(({ y }) => y)
  const xDomain = getXDomain(xs, xFromZero)
  const yTicks = niceTicks(zeroBaseline ? Math.min(0, ...ys) : Math.min(...ys), Math.max(...ys))
  const yDomain: [number, number] = [yTicks[0], yTicks[yTicks.length - 1]]

  return (
    <figure className={figureClass}>
      <figcaption className={captionClass}>{title}</figcaption>
      <div
        className={plotClass}
        style={{ height }}
        aria-label={`${title}: ${points.length} pontos, de ${formatValue(Math.min(...ys), yDigits)} a ${formatValue(Math.max(...ys), yDigits)} ${unit}. Use as setas para percorrer os pontos.`}
      >
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart
            data={points}
            margin={{ top: 14, right: 16, bottom: 8, left: 0 }}
            accessibilityLayer
          >
            <CartesianGrid stroke="#1f2f40" strokeWidth={1} vertical={false} />
            <XAxis
              dataKey="x"
              type="number"
              domain={xDomain}
              allowDataOverflow
              allowDecimals={!integerX}
              tickFormatter={xTickFormat}
              tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'var(--mono)' }}
              tickLine={false}
              axisLine={false}
              minTickGap={24}
            />
            <YAxis
              width={48}
              domain={yDomain}
              ticks={yTicks}
              tickFormatter={(value: number) => formatValue(value, tickDigits(yTicks))}
              tick={{ fill: 'var(--text-muted)', fontSize: 11, fontFamily: 'var(--mono)' }}
              tickLine={false}
              axisLine={false}
            />
            <Tooltip
              cursor={{ stroke: '#5d7389', strokeWidth: 1 }}
              content={({ active, payload }) => {
                const point = payload?.[0]?.payload as ChartPoint | undefined
                if (!active || !point) return null
                return (
                  <div className="grid grid-cols-[auto_auto] items-center gap-x-[7px] gap-y-0.5 whitespace-nowrap rounded-[3px] border border-line bg-page px-2.5 py-[7px] shadow-[0_8px_20px_#00000055]">
                    <span className="h-0.5 w-3 rounded-[1px]" style={{ background: color }} />
                    <strong className="font-mono text-small leading-[normal] font-semibold text-text">
                      {formatValue(point.y, yDigits)} {unit}
                    </strong>
                    <span className="col-start-2 text-caption text-secondary">{point.label}</span>
                  </div>
                )
              }}
            />
            <Area
              type="linear"
              dataKey="y"
              stroke={color}
              strokeWidth={2}
              strokeLinejoin="round"
              strokeLinecap="round"
              fill={color}
              fillOpacity={0.1}
              activeDot={{ r: 4, stroke: 'var(--surface)', strokeWidth: 2 }}
              dot={(props) => props.index === points.length - 1
                ? <circle cx={props.cx} cy={props.cy} r={4} fill={color} stroke="var(--surface)" strokeWidth={2} />
                : false}
              isAnimationActive={false}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </figure>
  )
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

function getXDomain(values: number[], fromZero: boolean): [number, number] {
  let min = fromZero ? Math.min(0, ...values) : Math.min(...values)
  let max = Math.max(...values)
  if (min === max) {
    const pad = Math.abs(min) * 0.05 || 1
    if (fromZero && min === 0) max += pad
    else {
      min -= pad
      max += pad
    }
  }
  return [min, max]
}
