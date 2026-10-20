import { MazeBoard } from './MazeBoard'
import { Icon } from './Icon'
import type { MazeDimensions, Position } from '../types/maze'
import { formatNumber } from '../utils/format'

type Props = {
  dimensions: MazeDimensions
  route: Position[]
  distanceTravelledMeters?: number
}

export function MazePanel({ dimensions, route, distanceTravelledMeters }: Props) {
  const cellCount = dimensions.rows * dimensions.columns

  return (
    <section className="panel maze-panel">
      <div className="panel-heading">
        <div>
          <p className="eyebrow">VISUALIZAÇÃO DO PERCURSO</p>
          <h2>Mapa do labirinto</h2>
        </div>
        <span className="dimension-tag">
          {dimensions.label} <span>·</span> {cellCount} células
        </span>
      </div>

      <MazeBoard rows={dimensions.rows} columns={dimensions.columns} route={route} />

      <div className="map-legend">
        <span><i className="legend-start" /> Partida</span>
        <span><i className="legend-path" /> Trajeto percorrido</span>
        <span><i className="legend-robot" /> Micromouse</span>
        <span className="distance-readout">
          <Icon name="route" /> {formatNumber(distanceTravelledMeters)} m percorridos
        </span>
      </div>
    </section>
  )
}
