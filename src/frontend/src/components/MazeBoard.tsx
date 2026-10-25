import { MicromouseIcon } from './MicromouseIcon'

type Cell = {
  row: number
  column: number
  heading?: string
}

type Props = {
  rows: number
  columns: number
  route: Cell[]
  compact?: boolean
}

export function MazeBoard({ rows, columns, route, compact = false }: Props) {
  const validRoute = route.filter(
    ({ row, column }) => row >= 0 && column >= 0 && row < rows && column < columns,
  )
  const getCenter = ({ row, column }: Cell) => ({
    x: ((column + 0.5) / columns) * 100,
    y: ((row + 0.5) / rows) * 100,
  })
  const points = validRoute
    .map((cell) => {
      const { x, y } = getCenter(cell)
      return `${x},${y}`
    })
    .join(' ')
  const start = validRoute[0]
  const current = validRoute[validRoute.length - 1]

  return (
    <div
      className={compact
        ? 'grid min-h-0 place-items-center overflow-hidden rounded-[2px] border border-[#353a35] bg-surface p-2.5'
        : 'grid min-h-[300px] place-items-center overflow-hidden rounded-[2px] border border-[#353a35] bg-[#191c1a] p-5 max-[960px]:min-h-[260px] max-[960px]:p-[15px] max-[700px]:min-h-[210px] max-[700px]:px-2'}
      aria-label={`Labirinto com ${rows} linhas e ${columns} colunas; ${validRoute.length} posições percorridas`}
    >
      <div
        className={compact
          ? 'relative grid w-full gap-[3px] rounded-[2px] border border-[#50564d] bg-[#262b26] p-2'
          : 'relative grid max-h-[405px] w-full max-w-[790px] gap-[3px] rounded-[2px] border border-[#50564d] bg-[#262b26] p-2 max-[960px]:gap-0.5 max-[960px]:p-1.5'}
        style={{
          gridTemplateColumns: `repeat(${columns}, minmax(0, 1fr))`,
          aspectRatio: `${columns} / ${rows}`,
        }}
      >
        {Array.from({ length: rows * columns }, (_, index) => (
          <div className="min-h-0 min-w-0 rounded-[1px] border border-[#3a403a] bg-[#222622]" key={index} />
        ))}

        <svg className="pointer-events-none absolute inset-2 h-[calc(100%-16px)] w-[calc(100%-16px)] overflow-visible max-[960px]:inset-1.5 max-[960px]:h-[calc(100%-12px)] max-[960px]:w-[calc(100%-12px)]" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true">
          {validRoute.length > 1 && (
            <polyline
              points={points}
              fill="none"
              stroke="#8fbaad"
              strokeWidth="1.15"
              strokeLinecap="round"
              strokeLinejoin="round"
              vectorEffect="non-scaling-stroke"
            />
          )}
        </svg>

        <div className="pointer-events-none absolute inset-2 max-[960px]:inset-1.5">
          {start && (
            <span
              className="absolute size-[11px] -translate-x-1/2 -translate-y-1/2 rounded-full border-2 border-[#252a25] bg-[#c2beb0] shadow-[0_0_0_1px_#c2beb077]"
              style={{ left: `${getCenter(start).x}%`, top: `${getCenter(start).y}%` }}
              aria-hidden="true"
            />
          )}
          {current && (
            <span
              className={`absolute aspect-square -translate-x-1/2 -translate-y-1/2 drop-shadow-[0_2px_2px_#000b] ${compact ? 'w-[18px]' : 'w-[clamp(19px,2.8vw,29px)]'}`}
              style={{ left: `${getCenter(current).x}%`, top: `${getCenter(current).y}%` }}
              aria-hidden="true"
            >
              <MicromouseIcon heading={current.heading} />
            </span>
          )}
        </div>
      </div>
    </div>
  )
}
