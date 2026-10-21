import { useEffect, useMemo, useState } from 'react'
import { fetchRuns } from '../api/runs'
import { AppHeader } from '../components/AppHeader'
import { LineChart, type ChartPoint } from '../components/LineChart'
import {
  durationSeconds, formatDateTime, formatDuration, matchesSearch, statusLabels, summarizeRuns, withUnit,
  type Run, type RunStatus,
} from '../models/run'
import { mazeOptions, type MazeSize } from '../types/maze'
import { formatElapsedTime, formatNumber } from '../utils/format'
import { Stat, StatusTag } from './GeneralQueryPage'
import '../App.css'
import './GeneralQueryPage.css'
import './MazeQueryPage.css'

type Props = { hash: string }

const mazeSizes = Object.keys(mazeOptions) as MazeSize[]

// #consulta-por-labirinto/GRID_8X4 → execuções só do labirinto 8 × 4
export function MazeQueryPage({ hash }: Props) {
  const requested = hash.split('/')[1] ?? mazeSizes[0]
  const mazeType = mazeSizes.find((size) => size === requested)

  return <main className="app-shell">
    <AppHeader active="consulta-geral" />
    <div className="page-content">
      <nav className="query-breadcrumb" aria-label="Caminho">
        <a href="#consulta-geral">Consulta geral</a>
        <span>/</span><b>Labirinto {mazeType ? mazeOptions[mazeType].label : requested}</b>
      </nav>
      {mazeType
        ? <MazeRuns key={mazeType} mazeType={mazeType} />
        : <>
          <p className="query-message query-error">Labirinto “{requested}” não existe.</p>
          <MazeTabs />
        </>}
    </div>
  </main>
}

function MazeRuns({ mazeType }: { mazeType: MazeSize }) {
  const maze = mazeOptions[mazeType]
  const [runs, setRuns] = useState<Run[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [status, setStatus] = useState<RunStatus | ''>('')
  const [search, setSearch] = useState('')

  useEffect(() => {
    fetchRuns(mazeType)
      // O backend devolve da mais antiga para a mais recente; aqui a mais recente fica no topo
      .then((data) => setRuns([...data].reverse()))
      .catch((e: Error) => setError(e.message))
  }, [mazeType])

  const filtered = useMemo(() => (runs ?? []).filter((run) =>
    (!status || run.status === status) && matchesSearch(run, search),
  ), [runs, status, search])
  const summary = summarizeRuns(filtered)
  // Gráficos por tentativa neste labirinto: da mais antiga para a mais recente
  const attempts = [...filtered].reverse()
  const attemptPoints = (value: (run: Run) => number | null): ChartPoint[] => attempts
    .map((run, index) => ({ run, attempt: index + 1, y: value(run) }))
    .filter((point): point is { run: Run; attempt: number; y: number } => point.y != null)
    .map(({ run, attempt, y }) => ({ x: attempt, y, label: `Tentativa ${attempt} · #${run.id}` }))

  return <>
    <section className="maze-query-top">
      <div className="panel maze-query-layout" aria-label={`Layout do labirinto ${maze.label}`}>
        <p className="eyebrow">LAYOUT — {maze.label}</p>
        <div className="maze-query-mini" style={{ gridTemplateColumns: `repeat(${maze.columns}, 12px)` }}>
          {Array.from({ length: maze.rows * maze.columns }, (_, index) => <span key={index} />)}
        </div>
      </div>
      <div className="maze-query-metrics">
        <Stat label="Execuções" value={runs ? String(summary.total) : '—'} />
        <Stat
          label="Tempo médio"
          value={summary.averageDurationSeconds == null ? '—' : formatElapsedTime(Math.round(summary.averageDurationSeconds), true)}
        />
        <Stat label="Taxa de sucesso" value={formatNumber(summary.successRatePercent ?? undefined, 0)} unit="%" />
        <Stat label="Consumo médio" value={formatNumber(summary.averageChargeMilliampHours ?? undefined)} unit="mAh" />
      </div>
    </section>

    <MazeTabs active={mazeType} />

    {error && <p className="query-message query-error">{error}</p>}
    {!error && runs === null && <p className="query-message">Carregando execuções…</p>}
    {runs?.length === 0 && <p className="query-message">Nenhuma execução armazenada no labirinto {maze.label}.</p>}

    {runs && runs.length > 0 && <>
      <div className="query-filters">
        <select aria-label="Filtrar por status" value={status} onChange={(e) => setStatus(e.target.value as RunStatus | '')}>
          <option value="">Todos os status</option>
          {Object.entries(statusLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select>
        <input
          type="search"
          aria-label="Buscar por ID ou data"
          placeholder="Buscar por ID ou data…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <span className="query-count">{filtered.length} de {runs.length} execuções</span>
      </div>

      {filtered.length === 0
        ? <p className="query-message">Nenhuma execução corresponde aos filtros.</p>
        : <div className="query-table-wrap">
          <table className="query-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Data</th>
                <th>Status</th>
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
                <td><StatusTag status={run.status} /></td>
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

function MazeTabs({ active }: { active?: MazeSize }) {
  return <nav className="maze-query-tabs" aria-label="Escolher labirinto">
    {mazeSizes.map((size) => <a
      key={size}
      href={`#consulta-por-labirinto/${size}`}
      className={size === active ? 'active' : ''}
      aria-current={size === active ? 'page' : undefined}
    >
      {mazeOptions[size].label}
    </a>)}
  </nav>
}
