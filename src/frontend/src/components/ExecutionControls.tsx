import { Icon } from './Icon'
import type { MazeSize } from '../types/maze'
import type { RunResult } from '../types/telemetry'
import { mazeOptions } from '../types/maze'
import { getRunStatusAppearance } from '../utils/runStatus'

type Props = {
  maze: MazeSize
  result: RunResult
  onMazeChange: (maze: MazeSize) => void
  onPrepare: () => void
  busy: boolean
  executionLocked: boolean
  canInterrupt: boolean
  onStart: () => void
  onInterrupt: () => void
  notice?: string
}

export function ExecutionControls({ maze, result, onMazeChange, onPrepare, busy, executionLocked, canInterrupt, onStart, onInterrupt, notice }: Props) {
  const appearance = getRunStatusAppearance(result)
  const statusTone = appearance === 'active'
    ? 'border-[#695438] bg-[#302a20] text-amber'
    : appearance === 'success'
      ? 'border-[#4d6040] bg-[#252b22] text-green'
      : appearance === 'failure'
        ? 'border-[#65443d] bg-[#302321] text-danger'
        : 'border-[#424640] bg-[#292c29] text-secondary'
  const markTone = appearance === 'active'
    ? 'bg-amber'
    : appearance === 'success'
      ? 'bg-green'
      : appearance === 'failure' ? 'bg-danger' : 'bg-[#a0a39b]'
  const buttonBase = 'inline-flex min-h-10 cursor-pointer items-center justify-center gap-2 rounded-[3px] border px-3 py-1 text-small leading-[1.1] font-semibold transition-colors disabled:cursor-not-allowed disabled:opacity-[.42] max-[700px]:px-2 max-[700px]:text-caption'

  return (
    <section className="flex min-h-[66px] flex-wrap items-center gap-x-5 gap-y-3 rounded-[4px] border border-line border-l-[3px] border-l-[#7b8d7b] bg-surface p-[10px_14px] max-[960px]:gap-x-4 max-[960px]:gap-y-[11px] max-[700px]:p-[11px]" aria-label="Controles do percurso">
      <div className="flex items-center gap-3">
        <label className="font-mono text-caption text-secondary" htmlFor="maze-select">Labirinto</label>
        <select
          className="min-h-10 min-w-[94px] cursor-pointer rounded-[3px] border border-[#4a4e48] bg-surface-low px-[10px] py-[7px] font-mono text-small text-text"
          id="maze-select"
          value={maze}
          disabled={executionLocked}
          onChange={(event) => onMazeChange(event.target.value as MazeSize)}
        >
          {Object.entries(mazeOptions).map(([value, dimensions]) => (
            <option key={value} value={value}>
              {dimensions.label}
            </option>
          ))}
        </select>
      </div>

      <span className={`inline-flex items-center gap-2 whitespace-nowrap rounded-[2px] border px-[9px] py-1.5 font-mono text-caption ${statusTone}`}>
        <span className={`size-[7px] shrink-0 rounded-full ${markTone}`} />
        {result}
      </span>

      <div className="ml-auto flex items-center gap-2 max-[960px]:ml-0 max-[960px]:w-full max-[960px]:justify-end max-[700px]:grid max-[700px]:grid-cols-2">
        <button className={`${buttonBase} border-[#4a4e48] bg-[#292c29] text-[#d0d0c8] hover:border-[#777b70] hover:bg-[#333733]`} onClick={onPrepare} disabled={executionLocked}>
          <Icon name="layers" /> <span className="button-label">Preparar mapa</span>
        </button>
        <button
          className={`${buttonBase} border-transparent bg-[#a8bd8f] text-[#171b17] hover:bg-[#bacda4]`}
          disabled={busy || executionLocked}
          onClick={onStart}
        >
          <Icon name="play" /> <span className="button-label">Iniciar percurso</span>
        </button>
        <button
          className={`${buttonBase} border-[#67453e] bg-[#302321] text-[#e4a195] hover:bg-[#422c27]`}
          disabled={busy || !canInterrupt}
          onClick={onInterrupt}
        >
          <Icon name="stop" /> <span className="button-label">Interromper</span>
        </button>
      </div>
      {notice && <p className="basis-full -mt-1 mb-0 text-small text-amber" role="status">{notice}</p>}
    </section>
  )
}
