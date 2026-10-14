import { useEffect, useMemo, useState } from 'react'
import { useTelemetryRealtime } from './realtime/useTelemetryRealtime'
import { MazeBoard } from './components/MazeBoard'
import { MetricCard } from './components/MetricCard'
import { Icon } from './components/Icon'
import './App.css'

type MazeSize = 'GRID_4X4' | 'GRID_8X4' | 'GRID_12X4'
type Position = { row: number; column: number; heading?: string }
type Telemetry = {
  runId?: number; sequence?: number; position?: Position; distanceTravelledMeters?: number
  averageSpeedMetersPerSecond?: number; batteryVoltageVolts?: number; batteryStatus?: string
  chargeConsumedMilliampHours?: number; energyConsumedWattHours?: number; timestamp?: string
}
const mazeOptions: Record<MazeSize, { label: string; rows: number; columns: number }> = {
  GRID_4X4: { label: '4 × 4', rows: 4, columns: 4 },
  GRID_8X4: { label: '8 × 4', rows: 4, columns: 8 },
  GRID_12X4: { label: '12 × 4', rows: 4, columns: 12 },
}
const fmt = (n: number | undefined, digits = 2) => n == null || !Number.isFinite(n) ? '—' : n.toFixed(digits)

function App() {
  const { connected, lastMessageAt, message } = useTelemetryRealtime()
  const [maze, setMaze] = useState<MazeSize>('GRID_8X4')
  const [dimensions, setDimensions] = useState(mazeOptions.GRID_8X4)
  const [route, setRoute] = useState<Position[]>([])
  const [telemetry, setTelemetry] = useState<Telemetry>({})
  const [startedAt, setStartedAt] = useState<number | null>(null)
  const [elapsed, setElapsed] = useState(0)
  const [result, setResult] = useState<'Em andamento' | 'Sucesso' | 'Interrompido' | 'Aguardando' | 'Não concluído' | 'Resultado indisponível'>('Aguardando')
  const stale = result === 'Em andamento' && Date.now() - (lastMessageAt ?? startedAt ?? Date.now()) > 1_000

  useEffect(() => {
    const timer = window.setInterval(() => {
      if (startedAt && result === 'Em andamento') setElapsed(Math.floor((Date.now() - startedAt) / 1000))
    }, 250)
    return () => window.clearInterval(timer)
  }, [startedAt, result])

  useEffect(() => {
    if (!message) return
    if (message.kind === 'started') {
      const rows = Number(message.payload.mazeRows)
      const columns = Number(message.payload.mazeColumns)
      if (rows > 0 && columns > 0) setDimensions({ rows, columns, label: `${columns} × ${rows}` })
      setRoute([]); setTelemetry({}); setResult('Em andamento'); setStartedAt(Date.now()); setElapsed(0)
    }
    if (message.kind === 'telemetry') {
      const sample = message.payload as Telemetry
      const p = sample.position
      setTelemetry(sample)
      if (p && Number.isInteger(p.row) && Number.isInteger(p.column) && p.row >= 0 && p.column >= 0 && p.row < dimensions.rows && p.column < dimensions.columns) {
        setRoute((current) => current.length && current[current.length - 1].row === p.row && current[current.length - 1].column === p.column ? current : [...current, p])
      }
    }
    if (message.kind === 'finished') setResult(message.payload.challengeCompleted === true ? 'Sucesso' : message.payload.challengeCompleted === false ? 'Não concluído' : 'Resultado indisponível')
    if (message.kind === 'interrupted') setResult('Interrompido')
  }, [message, dimensions.columns, dimensions.rows])

  const status = !connected ? 'Desconectado' : stale ? 'Telemetria desatualizada' : connected && lastMessageAt ? 'Ao vivo' : 'Conectado'
  const battery = telemetry.batteryVoltageVolts
  const clock = useMemo(() => startedAt ? `${String(Math.floor(elapsed / 60)).padStart(2, '0')}:${String(elapsed % 60).padStart(2, '0')}` : '--:--', [elapsed, startedAt])

  function prepare() {
    setDimensions(mazeOptions[maze]); setRoute([])
  }

  return <main className="app-shell">
    <header className="topbar">
      <a className="brand" href="#monitor" aria-label="Rato Cego, monitor">RATO<span>CEGO</span></a>
      <nav aria-label="Navegação principal"><a className="nav-link active" href="#monitor">Monitoramento</a><a className="nav-link" href="#historico" onClick={(e) => e.preventDefault()}>Histórico</a></nav>
      <span className="topbar-note">CENTRAL DE OPERAÇÃO <span>•</span> MICROMOUSE</span>
    </header>

    <div className="page-content" id="monitor">
      <section className="page-heading">
        <div><p className="eyebrow">PAINEL DE CONTROLE <span>／</span> SESSÃO AO VIVO</p><h1>Monitoramento</h1><p className="subheading">Acompanhe o percurso e os dados da execução em tempo real.</p></div>
        <div className={`connection ${status === 'Ao vivo' ? 'is-live' : status === 'Telemetria desatualizada' ? 'is-stale' : ''}`}><span className="connection-dot"/><span>{status}</span><span className="connection-divider"/><span className="mono">{clock}</span></div>
      </section>

      <section className="control-panel" aria-label="Controles do percurso">
        <div className="maze-select-wrap"><label htmlFor="maze-select">Labirinto</label><select id="maze-select" value={maze} onChange={(e) => { const next = e.target.value as MazeSize; setMaze(next); setDimensions(mazeOptions[next]); setRoute([]) }}><option value="GRID_4X4">4 × 4</option><option value="GRID_8X4">8 × 4</option><option value="GRID_12X4">12 × 4</option></select></div>
        <span className={`run-state ${result === 'Sucesso' ? 'success' : result === 'Interrompido' ? 'halted' : ''}`}><span className="state-mark"/>{result}</span>
        <div className="control-actions">
          <button className="button button-quiet" onClick={prepare}><Icon name="layers"/> Preparar mapa</button>
          <button className="button button-primary" disabled title="Aguardando integração do controle de execução com o backend"><Icon name="play"/> Iniciar percurso</button>
          <button className="button button-danger" disabled title="Aguardando integração do controle de execução com o backend"><Icon name="stop"/> Interromper</button>
          <button className="button button-quiet" disabled title="Aguardando integração do controle de execução com o backend"><Icon name="refresh"/> Reiniciar</button>
        </div>
      </section>
      {!connected && <div className="connection-notice"><span className="notice-dot"/> Aguardando conexão com o WebSocket. Os indicadores serão preenchidos quando a telemetria chegar.</div>}

      <div className="monitor-grid">
        <section className="panel maze-panel">
          <div className="panel-heading"><div><p className="eyebrow">VISUALIZAÇÃO DO PERCURSO</p><h2>Mapa do labirinto</h2></div><span className="dimension-tag">{dimensions.label} <span>·</span> {dimensions.rows * dimensions.columns} células</span></div>
          <MazeBoard rows={dimensions.rows} columns={dimensions.columns} route={route}/>
          <div className="map-legend"><span><i className="legend-start"/> Partida</span><span><i className="legend-path"/> Trajeto percorrido</span><span><i className="legend-robot"/> Micromouse</span><span className="distance-readout"><Icon name="route"/> {fmt(telemetry.distanceTravelledMeters)} m percorridos</span></div>
        </section>

        <aside className="metrics-column" aria-label="Dados da execução">
          <div className={`battery-card ${telemetry.batteryStatus === 'LOW' ? 'battery-low' : ''}`}>
            <div className="metric-label-row"><span>Consumo da bateria</span><span className="battery-value">{fmt(telemetry.chargeConsumedMilliampHours, 1)} <small>mAh</small></span></div>
            <div className="battery-foot battery-voltage"><span>Tensão da bateria</span><span>{battery == null ? '— V' : `${fmt(battery, 1)} V`}</span></div>
            {telemetry.batteryStatus === 'LOW' && <p className="battery-warning"><Icon name="alert"/> Bateria em nível crítico</p>}
          </div>
          <MetricCard label="Velocidade média" value={fmt(telemetry.averageSpeedMetersPerSecond)} unit="m/s" icon="speed" />
          <MetricCard label="Tempo de execução" value={clock} icon="time" mono />
          <div className={`result-card ${result === 'Sucesso' ? 'result-success' : ''}`}><span className="metric-label">Resultado do desafio</span><div className="result-value"><span className="result-icon">{result === 'Sucesso' ? '✓' : result === 'Em andamento' ? '↗' : '—'}</span>{result === 'Sucesso' ? 'Concluído' : result}</div></div>
        </aside>
      </div>
      <footer className="footer-note"><span>RATO CEGO <b>／</b> MONITOR DE EXECUÇÃO</span><span>Atualização automática <b>·</b> 10 Hz</span></footer>
    </div>
  </main>
}

export default App
