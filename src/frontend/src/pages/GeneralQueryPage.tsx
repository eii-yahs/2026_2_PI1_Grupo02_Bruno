import { useEffect, useMemo, useState } from 'react'
import { fetchRun, fetchRunTelemetry, fetchRuns } from '../api/runs'
import { AppHeader } from '../components/AppHeader'
import { LineChart, type ChartPoint } from '../components/LineChart'
import { MazeBoard } from '../components/MazeBoard'
import {
  challengeLabel, durationSeconds, formatDateTime, formatDuration, matchesSearch, mazeLabel, statusLabels, summarizeRuns, withUnit,
  type Run, type RunStatus, type TelemetrySample,
} from '../models/run'
import { mazeOptions } from '../types/maze'
import { formatNumber } from '../utils/format'
import '../App.css'
import './GeneralQueryPage.css'

type Props = { hash: string }

// #consulta-geral → lista de execuções; #consulta-geral/{id} → detalhe da execução
export function GeneralQueryPage({ hash }: Props) {
  const runId = Number(hash.split('/')[1])
  return <main className="app-shell">
    <AppHeader active="consulta-geral" />
    <div className={`page-content ${Number.isInteger(runId) && runId > 0 ? 'query-detail-page' : ''}`}>
      {Number.isInteger(runId) && runId > 0 ? <RunDetail runId={runId} /> : <RunList />}
    </div>
  </main>
}

function RunList() {
  const [runs, setRuns] = useState<Run[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [maze, setMaze] = useState('')
  const [status, setStatus] = useState<RunStatus | ''>('')
  const [search, setSearch] = useState('')

  useEffect(() => {
    fetchRuns()
      // O backend devolve da mais antiga para a mais recente; aqui a mais recente fica no topo
      .then((data) => setRuns([...data].reverse()))
      .catch((e: Error) => setError(e.message))
  }, [])

  const filtered = useMemo(() => (runs ?? []).filter((run) =>
    (!maze || mazeLabel(run) === maze) && (!status || run.status === status) && matchesSearch(run, search),
  ), [runs, maze, status, search])
  const summary = summarizeRuns(filtered)
  // Gráficos por tentativa: as execuções filtradas da mais antiga para a mais recente
  const attempts = [...filtered].reverse()
  const attemptPoints = (value: (run: Run) => number | null): ChartPoint[] => attempts
    .map((run, index) => ({ run, attempt: index + 1, y: value(run) }))
    .filter((point): point is { run: Run; attempt: number; y: number } => point.y != null)
    .map(({ run, attempt, y }) => ({ x: attempt, y, label: `Tentativa ${attempt} · #${run.id} · ${mazeLabel(run)}` }))

  return <>
    <section className="page-heading">
      <div>
        <p className="eyebrow">HISTÓRICO <span>／</span> TODAS AS EXECUÇÕES</p>
        <h1>Consulta geral</h1>
        <p className="subheading">Histórico completo das execuções armazenadas, de todos os labirintos.</p>
      </div>
    </section>

    {error && <p className="query-message query-error">{error}</p>}
    {!error && runs === null && <p className="query-message">Carregando execuções…</p>}
    {runs?.length === 0 && <p className="query-message">Nenhuma execução armazenada ainda.</p>}

    {runs && runs.length > 0 && <>
      <div className="query-summary">
        <Stat label="Qtd. testes" value={String(summary.total)} />
        <Stat label="Taxa de sucesso" value={formatNumber(summary.successRatePercent ?? undefined, 0)} unit="%" />
        <Stat label="Consumo médio geral" value={formatNumber(summary.averageChargeMilliampHours ?? undefined)} unit="mAh" />
      </div>

      <div className="query-filters">
        <select aria-label="Filtrar por labirinto" value={maze} onChange={(e) => setMaze(e.target.value)}>
          <option value="">Todos os labirintos</option>
          {Object.values(mazeOptions).map((option) => <option key={option.label} value={option.label}>{option.label}</option>)}
        </select>
        <select aria-label="Filtrar por status" value={status} onChange={(e) => setStatus(e.target.value as RunStatus | '')}>
          <option value="">Todos os status</option>
          {Object.entries(statusLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select>
        <input
          type="search"
          aria-label="Buscar por ID, data ou labirinto"
          placeholder="Buscar por ID, data ou labirinto…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <span className="query-count">{filtered.length} de {runs.length} execuções</span>
        <nav className="query-maze-links" aria-label="Consulta por labirinto">
          {Object.entries(mazeOptions).map(([type, option]) => <a key={type} href={`#consulta-por-labirinto/${type}`}>
            Ver {option.label} →
          </a>)}
        </nav>
      </div>

      {filtered.length === 0
        ? <p className="query-message">Nenhuma execução corresponde aos filtros.</p>
        : <div className="query-table-wrap">
          <table className="query-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Data</th>
                <th>Labirinto</th>
                <th>Status</th>
                <th>Desafio concluído</th>
                <th>Tempo</th>
                <th>Consumo</th>
                <th>Vel. média</th>
                <th>Distância</th>
                <th><span className="sr-only">Detalhe</span></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((run) => <tr key={run.id}>
                <td><a href={`#consulta-geral/${run.id}`}>#{run.id}</a></td>
                <td>{formatDateTime(run.startedAt)}</td>
                <td>{mazeLabel(run)}</td>
                <td><StatusTag status={run.status} /></td>
                <td>{challengeLabel(run)}</td>
                <td className="mono">{formatDuration(run.duration)}</td>
                <td className="mono">{withUnit(run.chargeConsumedMilliampHours, 'mAh')}</td>
                <td className="mono">{withUnit(run.averageSpeedMetersPerSecond, 'm/s')}</td>
                <td className="mono">{withUnit(run.distanceTravelledMeters, 'm')}</td>
                <td><a className="query-view" href={`#consulta-geral/${run.id}`}>ver →</a></td>
              </tr>)}
            </tbody>
          </table>
        </div>}

      {filtered.length > 0 && <div className="query-charts">
        <LineChart
          title="CONSUMO DE BATERIA × TENTATIVA (mAh)"
          points={attemptPoints((run) => run.chargeConsumedMilliampHours)}
          color="var(--amber)"
          unit="mAh"
          integerX
          xTickFormat={(x) => String(x)}
        />
        <LineChart
          title="TEMPO × TENTATIVA (s)"
          points={attemptPoints((run) => durationSeconds(run.duration))}
          color="var(--cyan)"
          unit="s"
          yDigits={1}
          integerX
          xTickFormat={(x) => String(x)}
        />
      </div>}
    </>}
  </>
}

function RunDetail({ runId }: { runId: number }) {
  const [run, setRun] = useState<Run | null | undefined>(undefined)
  const [samples, setSamples] = useState<TelemetrySample[]>([])
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchRun(runId)
      .then(async (found) => {
        setSamples(found ? await fetchRunTelemetry(runId) : [])
        setRun(found)
      })
      .catch((e: Error) => setError(e.message))
  }, [runId])

  const maze = run && run.mazeRows != null && run.mazeColumns != null
    ? { label: mazeLabel(run), rows: run.mazeRows, columns: run.mazeColumns }
    : null
  const route = samples.map(({ row, column, heading }) => ({ row, column, heading }))
  const firstSampleAt = Date.parse(run?.startedAt ?? samples[0]?.recordedAt ?? '')
  const samplePoints = (value: (sample: TelemetrySample) => number | null): ChartPoint[] => samples
    .map((sample) => ({ sample, y: value(sample) }))
    .filter((point): point is { sample: TelemetrySample; y: number } => point.y != null)
    .map(({ sample, y }) => {
      const x = (Date.parse(sample.recordedAt) - firstSampleAt) / 1000
      return { x, y, label: `${x.toFixed(1)} s · amostra #${sample.sequence}` }
    })

  return <>
    <nav className="query-breadcrumb" aria-label="Caminho">
      <a href="#consulta-geral">Consulta geral</a>
      {run && <><span>/</span>{mazeLabel(run)}</>}
      <span>/</span><b>#{runId}</b>
    </nav>

    {error && <p className="query-message query-error">{error}</p>}
    {!error && run === undefined && <p className="query-message">Carregando execução…</p>}
    {run === null && <p className="query-message">Execução #{runId} não encontrada.</p>}

    {run && <>
      {/* Faixa de resumo em uma linha, separada por divisórias, como no protótipo */}
      <section className="query-run-strip">
        <div className="query-run-cell">
          <span className="metric-label">Execução</span>
          <strong className="query-run-id">#{run.id}</strong>
        </div>
        <div className="query-run-cell"><span className="metric-label">Labirinto</span><strong>{mazeLabel(run)}</strong></div>
        <div className="query-run-cell"><span className="metric-label">Data</span><strong>{formatDateTime(run.startedAt)}</strong></div>
        <div className="query-run-cell"><span className="metric-label">Tempo</span><strong className="mono">{formatDuration(run.duration)}</strong></div>
        <div className="query-run-cell">
          <span className="metric-label">Status</span>
          <StatusTag status={run.status} />
          <small className="query-run-challenge">Desafio concluído: {challengeLabel(run)}</small>
        </div>
      </section>

      <div className="query-detail-grid">
        <section className="query-trajectory" aria-label="Trajeto percorrido">
          <p className="eyebrow">TRAJETO PERCORRIDO</p>
          {maze
            ? <div className="query-trajectory-board" style={{ width: `${maze.columns * 36 + 40}px` }}>
              <MazeBoard rows={maze.rows} columns={maze.columns} route={route} />
            </div>
            : <p className="query-message">Execução sem labirinto associado; não é possível desenhar o trajeto.</p>}
        </section>

        <div className="query-detail-side">
          <div className="query-detail-metrics">
            <Stat label="Tempo total" value={formatDuration(run.duration)} />
            <Stat label="Velocidade média" value={formatNumber(run.averageSpeedMetersPerSecond ?? undefined)} unit="m/s" />
            <Stat label="Consumo de bateria" value={formatNumber(run.chargeConsumedMilliampHours ?? undefined)} unit="mAh" />
            <Stat label="Distância total" value={formatNumber(run.distanceTravelledMeters ?? undefined)} unit="m" />
          </div>

          <section className="panel query-log">
            <p className="eyebrow">HISTÓRICO DO LOG</p>
            {samples.length === 0
              ? <p className="query-log-empty">Nenhuma amostra de telemetria armazenada para esta execução.</p>
              : <ol>
                {samples.map((sample) => <li key={sample.sequence}>
                  [{elapsedLabel(sample.recordedAt, firstSampleAt)}] Bateria {formatNumber(sample.batteryVoltageVolts ?? undefined)} V
                  {' '}— Vel. {formatNumber(sample.currentSpeedMetersPerSecond ?? undefined)} m/s
                </li>)}
              </ol>}
          </section>
        </div>
      </div>

      <div className="query-charts">
        <LineChart
          title="BATERIA × TEMPO"
          points={samplePoints((sample) => sample.batteryVoltageVolts)}
          color="var(--amber)"
          unit="V"
          height={140}
          xFromZero
          zeroBaseline={false}
          xTickFormat={(x) => `${x}s`}
        />
        <LineChart
          title="VELOCIDADE × TEMPO"
          points={samplePoints((sample) => sample.currentSpeedMetersPerSecond)}
          color="var(--cyan)"
          unit="m/s"
          height={140}
          xFromZero
          xTickFormat={(x) => `${x}s`}
        />
      </div>
    </>}
  </>
}

export function Stat({ label, value, unit }: { label: string; value: string; unit?: string }) {
  const isPlaceholder = value === '—'
  return <article className="metric-card">
    <span className="metric-label">{label}</span>
    <div className={`metric-number ${isPlaceholder ? 'is-placeholder' : ''}`}>
      {value}{unit && !isPlaceholder && <small>{unit}</small>}
    </div>
  </article>
}

export function StatusTag({ status }: { status: RunStatus }) {
  return <span className={`query-status status-${status.toLowerCase()}`}>{statusLabels[status]}</span>
}

// Tempo desde o início da execução, no formato do log do protótipo (ex.: 012.3s)
function elapsedLabel(recordedAt: string, startedAt: number) {
  const seconds = Math.max(0, (Date.parse(recordedAt) - startedAt) / 1000)
  return Number.isFinite(seconds) ? `${seconds.toFixed(1).padStart(5, '0')}s` : '—'
}
