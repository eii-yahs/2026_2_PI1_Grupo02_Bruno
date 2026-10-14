import { MicromouseIcon } from './MicromouseIcon'

type Cell = { row: number; column: number; heading?: string }
type Props = { rows: number; columns: number; route: Cell[] }

export function MazeBoard({ rows, columns, route }: Props) {
  const validRoute = route.filter(({ row, column }) => row >= 0 && column >= 0 && row < rows && column < columns)
  const center = ({ row, column }: Cell) => ({ x: ((column + 0.5) / columns) * 100, y: ((row + 0.5) / rows) * 100 })
  const points = validRoute.map((cell) => {
    const { x, y } = center(cell)
    return `${x},${y}`
  }).join(' ')
  const start = validRoute[0]
  const current = validRoute[validRoute.length - 1]

  return <div className="maze-canvas" aria-label={`Labirinto com ${rows} linhas e ${columns} colunas; ${validRoute.length} posições percorridas`}>
    <div className="maze-grid" style={{ gridTemplateColumns: `repeat(${columns}, minmax(0, 1fr))`, aspectRatio: `${columns} / ${rows}` }}>
      {Array.from({ length: rows * columns }, (_, index) => <div className="maze-cell" key={index}/>) }
      <svg className="route-layer" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true">
        {validRoute.length > 1 && <polyline points={points} fill="none" stroke="var(--cyan)" strokeWidth="1.15" strokeLinecap="round" strokeLinejoin="round" vectorEffect="non-scaling-stroke"/>}
      </svg>
      <div className="marker-layer">
        {start && <span className="start-marker" style={{ left: `${center(start).x}%`, top: `${center(start).y}%` }} aria-hidden="true"/>}
        {current && <span className="robot-position" style={{ left: `${center(current).x}%`, top: `${center(current).y}%` }} aria-hidden="true">
          <MicromouseIcon heading={current.heading}/>
        </span>}
      </div>
    </div>
  </div>
}
