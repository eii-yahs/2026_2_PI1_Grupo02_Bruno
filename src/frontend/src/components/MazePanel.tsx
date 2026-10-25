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
    <section className="min-w-0 rounded-[4px] border border-line bg-surface p-4 pb-3 max-[700px]:px-[11px] max-[700px]:py-[13px]">
      <div className="mb-[14px] flex items-center justify-between gap-4">
        <div>
          <p className="mb-1 font-mono text-caption tracking-[.04em] text-muted">VISUALIZAÇÃO DO PERCURSO</p>
          <h2 className="m-0 font-display text-[23px] leading-[1.05] font-semibold tracking-[.015em] text-text">Mapa do labirinto</h2>
        </div>
        <span className="whitespace-nowrap rounded-[2px] border border-[#41453f] bg-[#242724] px-[7px] py-[5px] font-mono text-caption text-secondary">
          {dimensions.label} <span className="px-[3px] text-amber">·</span> {cellCount} células
        </span>
      </div>

      <MazeBoard rows={dimensions.rows} columns={dimensions.columns} route={route} />

      <div className="flex flex-wrap items-center gap-x-[18px] gap-y-2 px-0.5 pt-3 text-caption text-secondary max-[700px]:gap-x-3.5">
        <span className="inline-flex items-center gap-[7px]"><i className="size-2 rounded-full bg-[#c2beb0]" /> Partida</span>
        <span className="inline-flex items-center gap-[7px]"><i className="h-0.5 w-[15px] bg-cyan" /> Trajeto percorrido</span>
        <span className="inline-flex items-center gap-[7px]"><i className="size-2 rounded-full bg-amber" /> Micromouse</span>
        <span className="ml-auto inline-flex items-center gap-1.5 font-mono tabular-nums text-[#d0cec4] max-[700px]:ml-0 max-[700px]:w-full">
          <Icon name="route" /> {formatNumber(distanceTravelledMeters)} m percorridos
        </span>
      </div>
    </section>
  )
}
