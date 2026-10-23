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
import { Message, Stat, StatusTag } from './GeneralQueryPage'
import {
  breadcrumbClass, chartsClass, countClass, fieldClass, filtersClass, tableClass, tableWrapClass, viewLinkClass,
} from './queryClasses'
import '../App.css'

type Props = { hash: string }

const mazeSizes = Object.keys(mazeOptions) as MazeSize[]
const tabClass = 'inline-flex min-h-9 items-center rounded-(--radius-control) border px-3 font-(family-name:--mono) text-(length:--text-small) leading-[normal] no-underline'

export function MazeQueryPage({ hash }: Props) {
  const requested = hash.split('/')[1] ?? mazeSizes[0]
  const mazeType = mazeSizes.find((size) => size === requested)

  return <main className="app-shell">
    <AppHeader active="consulta-geral" />
    <div className="page-content">
      <nav className={breadcrumbClass} aria-label="Caminho">
        <a href="#consulta-geral">Consulta geral</a>
        <span>/</span><b>Labirinto {mazeType ? mazeOptions[mazeType].label : requested}</b>
      </nav>
      {mazeType
        ? <MazeRuns key={mazeType} mazeType={mazeType} />
        : <>
          <Message error>Labirinto “{requested}” não existe.</Message>
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
      .then((data) => setRuns([...data].reverse()))
      .catch((e: Error) => setError(e.message))
  }, [mazeType])

  const filtered = useMemo(() => (runs ?? []).filter((run) =>
    (!status || run.status === status) && matchesSearch(run, search),
  ), [runs, status, search])
  const summary = summarizeRuns(filtered)
  const attempts = [...filtered].reverse()
  const attemptPoints = (value: (run: Run) => number | null): ChartPoint[] => attempts
    .map((run, index) => ({ run, attempt: index + 1, y: value(run) }))
    .filter((point): point is { run: Run; attempt: number; y: number } => point.y != null)
    .map(({ run, attempt, y }) => ({ x: attempt, y, label: `Tentativa ${attempt} · #${run.id}` }))

  return <>
    <section className="mb-3.5 grid grid-cols-[auto_minmax(0,1fr)] items-start gap-3 [@media(max-width:700px)]:grid-cols-1">
      <div className="panel p-3.5" aria-label={`Layout do labirinto ${maze.label}`}>
        <p className="eyebrow mb-3!">LAYOUT — {maze.label}</p>
        <div className="grid justify-center gap-[3px]" style={{ gridTemplateColumns: `repeat(${maze.columns}, 12px)` }}>
          {Array.from({ length: maze.rows * maze.columns }, (_, index) => <span
            key={index}
            className="h-3 w-3 rounded-[2px] border border-[#32485d] bg-[#172434]"
          />)}
        </div>
      </div>
      <div className="grid grid-cols-3 gap-3 [@media(max-width:700px)]:grid-cols-2">
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

    {error && <Message error>{error}</Message>}
    {!error && runs === null && <Message>Carregando execuções…</Message>}
    {runs?.length === 0 && <Message>Nenhuma execução armazenada no labirinto {maze.label}.</Message>}

    {runs && runs.length > 0 && <>
      <div className={filtersClass}>
        <select className={`${fieldClass} cursor-pointer`} aria-label="Filtrar por status" value={status} onChange={(e) => setStatus(e.target.value as RunStatus | '')}>
          <option value="">Todos os status</option>
          {Object.entries(statusLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select>
        <input
          className={`${fieldClass} min-w-0 flex-[1_1_220px] placeholder:text-(--text-muted)`}
          type="search"
          aria-label="Buscar por ID ou data"
          placeholder="Buscar por ID ou data…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <span className={countClass}>{filtered.length} de {runs.length} execuções</span>
      </div>

      {filtered.length === 0
        ? <Message>Nenhuma execução corresponde aos filtros.</Message>
        : <div className={tableWrapClass}>
          <table className={tableClass}>
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
                <td><a className={viewLinkClass} href={`#consulta-geral/${run.id}`}>ver →</a></td>
              </tr>)}
            </tbody>
          </table>
        </div>}

      {filtered.length > 0 && <div className={chartsClass}>
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
  return <nav className="mb-3.5 flex flex-wrap gap-2" aria-label="Escolher labirinto">
    {mazeSizes.map((size) => <a
      key={size}
      href={`#consulta-por-labirinto/${size}`}
      className={`${tabClass} ${size === active
        ? 'border-(--cyan) bg-[#0f2430] text-(--cyan)'
        : 'border-[#30445a] bg-[#0c1520] text-(--text-secondary) hover:border-[#48627c] hover:text-(--text)'}`}
      aria-current={size === active ? 'page' : undefined}
    >
      {mazeOptions[size].label}
    </a>)}
  </nav>
}
