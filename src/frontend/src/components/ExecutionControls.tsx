import { Icon } from './Icon'
import type { MazeSize } from '../types/maze'
import { mazeOptions } from '../types/maze'

type Props = {
  maze: MazeSize
  result: string
  onMazeChange: (maze: MazeSize) => void
  onPrepare: () => void
}

export function ExecutionControls({ maze, result, onMazeChange, onPrepare }: Props) {
  const stateClass =
    result === 'Sucesso' ? 'success' : result === 'Interrompido' ? 'halted' : ''

  return (
    <section className="control-panel" aria-label="Controles do percurso">
      <div className="maze-select-wrap">
        <label htmlFor="maze-select">Labirinto</label>
        <select
          id="maze-select"
          value={maze}
          onChange={(event) => onMazeChange(event.target.value as MazeSize)}
        >
          {Object.entries(mazeOptions).map(([value, dimensions]) => (
            <option key={value} value={value}>
              {dimensions.label}
            </option>
          ))}
        </select>
      </div>

      <span className={`run-state ${stateClass}`}>
        <span className="state-mark" />
        {result}
      </span>

      <div className="control-actions">
        <button className="button button-quiet" onClick={onPrepare}>
          <Icon name="layers" /> Preparar mapa
        </button>
        <button
          className="button button-primary"
          disabled
          title="Aguardando integração do controle de execução com o backend"
        >
          <Icon name="play" /> Iniciar percurso
        </button>
        <button
          className="button button-danger"
          disabled
          title="Aguardando integração do controle de execução com o backend"
        >
          <Icon name="stop" /> Interromper
        </button>
        <button
          className="button button-quiet"
          disabled
          title="Aguardando integração do controle de execução com o backend"
        >
          <Icon name="refresh" /> Reiniciar
        </button>
      </div>
    </section>
  )
}
