import { useEffect, useState } from 'react'
import { useTelemetryRealtime } from './realtime/useTelemetryRealtime'
import { AppHeader } from './components/AppHeader'
import { ConnectionStatus } from './components/ConnectionStatus'
import { ExecutionControls } from './components/ExecutionControls'
import { ExecutionMetrics } from './components/ExecutionMetrics'
import { MazePanel } from './components/MazePanel'
import { mazeOptions } from './types/maze'
import type { MazeDimensions, MazeSize, Position } from './types/maze'
import type { RunResult, Telemetry } from './types/telemetry'
import { formatElapsedTime } from './utils/format'
import './App.css'

function App() {
  const { connected, lastMessageAt, message } = useTelemetryRealtime()
  const [maze, setMaze] = useState<MazeSize>('GRID_8X4')
  const [dimensions, setDimensions] = useState<MazeDimensions>(mazeOptions.GRID_8X4)
  const [route, setRoute] = useState<Position[]>([])
  const [telemetry, setTelemetry] = useState<Telemetry>({})
  const [startedAt, setStartedAt] = useState<number | null>(null)
  const [elapsed, setElapsed] = useState(0)
  const [result, setResult] = useState<RunResult>('Aguardando')

  const stale =
    result === 'Em andamento' && Date.now() - (lastMessageAt ?? startedAt ?? Date.now()) > 1_000
  const status = !connected
    ? 'Desconectado'
    : stale
      ? 'Telemetria desatualizada'
      : lastMessageAt
        ? 'Ao vivo'
        : 'Conectado'
  const clock = formatElapsedTime(elapsed, startedAt !== null)

  useEffect(() => {
    const timer = window.setInterval(() => {
      if (startedAt !== null && result === 'Em andamento') {
        setElapsed(Math.floor((Date.now() - startedAt) / 1_000))
      }
    }, 250)

    return () => window.clearInterval(timer)
  }, [startedAt, result])

  useEffect(() => {
    if (!message) return

    if (message.kind === 'started') {
      const rows = Number(message.payload.mazeRows)
      const columns = Number(message.payload.mazeColumns)

      if (rows > 0 && columns > 0) {
        setDimensions({ rows, columns, label: `${columns} × ${rows}` })
      }

      setRoute([])
      setTelemetry({})
      setResult('Em andamento')
      setStartedAt(Date.now())
      setElapsed(0)
      return
    }

    if (message.kind === 'telemetry') {
      const sample = message.payload as Telemetry
      const position = sample.position

      setTelemetry(sample)

      if (isPositionInsideMaze(position, dimensions)) {
        setRoute((currentRoute) => {
          const previousPosition = currentRoute[currentRoute.length - 1]
          const positionUnchanged =
            previousPosition?.row === position.row && previousPosition?.column === position.column

          return positionUnchanged ? currentRoute : [...currentRoute, position]
        })
      }

      return
    }

    if (message.kind === 'finished') {
      setResult(
        message.payload.challengeCompleted === true
          ? 'Sucesso'
          : message.payload.challengeCompleted === false
            ? 'Não concluído'
            : 'Resultado indisponível',
      )
      return
    }

    if (message.kind === 'interrupted') {
      setResult('Interrompido')
    }
  }, [message, dimensions.columns, dimensions.rows])

  function handleMazeChange(nextMaze: MazeSize) {
    setMaze(nextMaze)
    setDimensions(mazeOptions[nextMaze])
    setRoute([])
  }

  function prepareMap() {
    setDimensions(mazeOptions[maze])
    setRoute([])
  }

  return (
    <main className="app-shell">
      <AppHeader />

      <div className="page-content" id="monitor">
        <section className="page-heading">
          <div>
            <p className="eyebrow">
              PAINEL DE CONTROLE <span>／</span> SESSÃO AO VIVO
            </p>
            <h1>Monitoramento</h1>
            <p className="subheading">
              Acompanhe o percurso e os dados da execução em tempo real.
            </p>
          </div>
          <ConnectionStatus status={status} clock={clock} />
        </section>

        <ExecutionControls
          maze={maze}
          result={result}
          onMazeChange={handleMazeChange}
          onPrepare={prepareMap}
        />

        {!connected && (
          <div className="connection-notice">
            <span className="notice-dot" />
            Aguardando conexão com o WebSocket. Os indicadores serão preenchidos quando a
            telemetria chegar.
          </div>
        )}

        <div className="monitor-grid">
          <MazePanel
            dimensions={dimensions}
            route={route}
            distanceTravelledMeters={telemetry.distanceTravelledMeters}
          />
          <ExecutionMetrics telemetry={telemetry} clock={clock} result={result} />
        </div>

        <footer className="footer-note">
          <span>
            RATO CEGO <b>／</b> MONITOR DE EXECUÇÃO
          </span>
          <span>
            Atualização automática <b>·</b> 10 Hz
          </span>
        </footer>
      </div>
    </main>
  )
}

function isPositionInsideMaze(
  position: Position | undefined,
  dimensions: MazeDimensions,
): position is Position {
  return (
    position !== undefined &&
    Number.isInteger(position.row) &&
    Number.isInteger(position.column) &&
    position.row >= 0 &&
    position.column >= 0 &&
    position.row < dimensions.rows &&
    position.column < dimensions.columns
  )
}

export default App
