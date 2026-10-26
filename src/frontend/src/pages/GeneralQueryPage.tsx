import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { ArrowRight } from 'lucide-react'
import { fetchRun, fetchRunTelemetry, fetchRuns } from '../api/runs'
import { AppHeader } from '../components/AppHeader'
import { LineChart, type ChartPoint } from '../components/LineChart'
import { MazeBoard } from '../components/MazeBoard'
import { QueryRefreshButton } from '../components/QueryRefreshButton'
import {
  challengeLabel, durationSeconds, formatDateTime, formatDuration, matchesSearch, mazeLabel, statusLabels, summarizeRuns, withUnit,
  type Run, type RunStatus, type TelemetrySample,
} from '../models/run'
import { mazeOptions } from '../types/maze'
import { formatNumber } from '../utils/format'
import {
  breadcrumbClass, chartsClass, countClass, eyebrowClass, fieldClass, filtersClass, monoClass, pageContentClass,
  pageShellClass, panelClass, tableClass, tableWrapClass, viewLinkClass,
} from './queryClasses'
const mazeLinkClass = 'inline-flex min-h-10 items-center gap-2 rounded-[3px] border border-[#555b50] bg-surface-low px-3 font-mono text-small leading-[normal] whitespace-nowrap text-cyan no-underline hover:border-cyan hover:bg-surface-raised'
const statusClasses: Record<RunStatus, string> = {
  START_REQUESTED: 'bg-surface-raised text-secondary',
  IN_PROGRESS: 'bg-[#302a20] text-amber',
  INTERRUPT_REQUESTED: 'bg-[#302a20] text-amber',
  COMPLETED: 'bg-[#252b22] text-green',
  FAILED: 'bg-[#302321] text-danger',
  INTERRUPTED: 'bg-[#302321] text-danger',
}

type Props = { hash: string }

export function GeneralQueryPage({ hash }: Props) {
  const runId = Number(hash.split('/')[1])
  const isDetail = Number.isInteger(runId) && runId > 0
  return <main className={pageShellClass}>
    <AppHeader active="consulta-geral" />
    <div className={`${pageContentClass} ${isDetail ? 'pt-[26px] pb-5' : ''}`}>
      {isDetail ? <RunDetail runId={runId} /> : <RunList />}
    </div>
  </main>
}

function RunList() {
  const [runs, setRuns] = useState<Run[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [refreshing, setRefreshing] = useState(true)
  const [maze, setMaze] = useState('')
  const [status, setStatus] = useState<RunStatus | ''>('')
  const [search, setSearch] = useState('')

  const refresh = useCallback(async () => {
    setRefreshing(true)
    setError(null)
    try {
      const data = await fetchRuns()
      setRuns([...data].reverse())
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Não foi possível atualizar as execuções.')
    } finally {
      setRefreshing(false)
    }
  }, [])

  useEffect(() => {
    void refresh()
  }, [refresh])

  const filtered = useMemo(() => (runs ?? []).filter((run) =>
    (!maze || mazeLabel(run) === maze) && (!status || run.status === status) && matchesSearch(run, search),
  ), [runs, maze, status, search])
  const summary = summarizeRuns(filtered)
  const attempts = [...filtered].reverse()
  const attemptPoints = (value: (run: Run) => number | null): ChartPoint[] => attempts
    .map((run, index) => ({ run, attempt: index + 1, y: value(run) }))
    .filter((point): point is { run: Run; attempt: number; y: number } => point.y != null)
    .map(({ run, attempt, y }) => ({ x: attempt, y, label: `Tentativa ${attempt} · #${run.id} · ${mazeLabel(run)}` }))

  return <>
    <section className="mb-6 flex items-end justify-between max-[700px]:mb-[18px] max-[700px]:flex-col max-[700px]:items-start max-[700px]:gap-[14px]">
      <div>
        <h1 className="m-0 font-display text-[clamp(36px,4vw,48px)] leading-[.98] font-semibold tracking-[.005em] text-text">Consulta geral</h1>
        <p className="mt-2 mb-0 text-body text-secondary">Histórico completo das execuções armazenadas, de todos os labirintos.</p>
      </div>
      <QueryRefreshButton refreshing={refreshing} onRefresh={() => void refresh()} />
    </section>

    {error && <Message error>{error}</Message>}
    {!error && runs === null && <Message>Carregando execuções…</Message>}
    {runs?.length === 0 && <Message>Nenhuma execução armazenada ainda.</Message>}

    {runs && runs.length > 0 && <>
      <div className="mb-3.5 grid grid-cols-3 gap-3 [@media(max-width:700px)]:grid-cols-1">
        <Stat label="Qtd. testes" value={String(summary.total)} />
        <Stat label="Taxa de sucesso" value={formatNumber(summary.successRatePercent ?? undefined, 0)} unit="%" />
        <Stat label="Consumo médio geral" value={formatNumber(summary.averageChargeMilliampHours ?? undefined)} unit="mAh" />
      </div>

      <div className={filtersClass}>
        <select className={`${fieldClass} cursor-pointer`} aria-label="Filtrar por labirinto" value={maze} onChange={(e) => setMaze(e.target.value)}>
          <option value="">Todos os labirintos</option>
          {Object.values(mazeOptions).map((option) => <option key={option.label} value={option.label}>{option.label}</option>)}
        </select>
        <select className={`${fieldClass} cursor-pointer`} aria-label="Filtrar por status" value={status} onChange={(e) => setStatus(e.target.value as RunStatus | '')}>
          <option value="">Todos os status</option>
          {Object.entries(statusLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select>
        <input
          className={`${fieldClass} min-w-0 flex-[1_1_220px] placeholder:text-muted`}
          type="search"
          aria-label="Buscar por ID, data ou labirinto"
          placeholder="Buscar por ID, data ou labirinto…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <span className={countClass}>{filtered.length} de {runs.length} execuções</span>
        <nav className="flex flex-wrap gap-2" aria-label="Consulta por labirinto">
          {Object.entries(mazeOptions).map(([type, option]) => <a key={type} className={mazeLinkClass} href={`#consulta-por-labirinto/${type}`}>
            Ver {option.label} <ArrowRight className="size-[13px] shrink-0" aria-hidden="true" />
          </a>)}
        </nav>
      </div>

      {filtered.length === 0
        ? <Message>Nenhuma execução corresponde aos filtros.</Message>
        : <div className={tableWrapClass}>
          <table className={tableClass}>
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
                <td className={monoClass}>{formatDuration(run.duration)}</td>
                <td className={monoClass}>{withUnit(run.chargeConsumedMilliampHours, 'mAh')}</td>
                <td className={monoClass}>{withUnit(run.averageSpeedMetersPerSecond, 'm/s')}</td>
                <td className={monoClass}>{withUnit(run.distanceTravelledMeters, 'm')}</td>
                <td>
                  <a className={viewLinkClass} href={`#consulta-geral/${run.id}`}>ver <ArrowRight className="size-[13px] shrink-0" aria-hidden="true" /></a>
                </td>
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

function RunDetail({ runId }: { runId: number }) {
  const [run, setRun] = useState<Run | null | undefined>(undefined)
  const [samples, setSamples] = useState<TelemetrySample[]>([])
  const [error, setError] = useState<string | null>(null)
  const [refreshing, setRefreshing] = useState(true)

  const refresh = useCallback(async () => {
    setRefreshing(true)
    setError(null)
    try {
      const found = await fetchRun(runId)
      setSamples(found ? await fetchRunTelemetry(runId) : [])
      setRun(found)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Não foi possível atualizar a execução.')
    } finally {
      setRefreshing(false)
    }
  }, [runId])

  useEffect(() => {
    void refresh()
  }, [refresh])

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
    <div className="mb-3.5 flex flex-wrap items-center justify-between gap-2">
      <nav className={`${breadcrumbClass} mb-0!`} aria-label="Caminho">
        <a href="#consulta-geral">Consulta geral</a>
        {run && <><span>/</span>{mazeLabel(run)}</>}
        <span>/</span><b>#{runId}</b>
      </nav>
      <QueryRefreshButton refreshing={refreshing} onRefresh={() => void refresh()} />
    </div>

    {error && <Message error>{error}</Message>}
    {!error && run === undefined && <Message>Carregando execução…</Message>}
    {run === null && <Message>Execução #{runId} não encontrada.</Message>}

    {run && <>
      <section className={`mb-3.5 grid grid-cols-[minmax(150px,auto)_repeat(4,minmax(0,1fr))] max-[960px]:grid-cols-2 ${panelClass}`}>
        <RunCell label="Execução">
          <strong className="break-all font-mono text-xl leading-[1.15] font-semibold text-cyan">#{run.id}</strong>
        </RunCell>
        <RunCell label="Labirinto"><Value>{mazeLabel(run)}</Value></RunCell>
        <RunCell label="Data"><Value>{formatDateTime(run.startedAt)}</Value></RunCell>
        <RunCell label="Tempo"><Value className={monoClass}>{formatDuration(run.duration)}</Value></RunCell>
        <RunCell label="Status">
          <StatusTag status={run.status} />
          <small className="text-caption text-secondary">Desafio concluído: {challengeLabel(run)}</small>
        </RunCell>
      </section>

      <div className="grid grid-cols-[auto_minmax(0,1fr)] items-start gap-4 max-[960px]:grid-cols-1">
        <section className="min-w-0" aria-label="Trajeto percorrido">
          <p className={`${eyebrowClass} mb-2`}>TRAJETO PERCORRIDO</p>
          {maze
            ? <div
              className="max-w-full"
              style={{ width: `${maze.columns * 36 + 40}px` }}
            >
              <MazeBoard rows={maze.rows} columns={maze.columns} route={route} compact />
            </div>
            : <Message>Execução sem labirinto associado; não é possível desenhar o trajeto.</Message>}
        </section>

        <div className="grid min-w-0 gap-2.5">
          <div className="grid grid-cols-2 gap-2.5">
            <Stat compact label="Tempo total" value={formatDuration(run.duration)} />
            <Stat compact label="Velocidade média" value={formatNumber(run.averageSpeedMetersPerSecond ?? undefined)} unit="m/s" />
            <Stat compact label="Consumo de bateria" value={formatNumber(run.chargeConsumedMilliampHours ?? undefined)} unit="mAh" />
            <Stat compact label="Distância total" value={formatNumber(run.distanceTravelledMeters ?? undefined)} unit="m" />
          </div>

          <section className={`${panelClass} px-3.5 py-2.5`}>
            <p className={`${eyebrowClass} mb-1`}>HISTÓRICO DO LOG</p>
            {samples.length === 0
              ? <p className="m-0 text-small text-muted">Nenhuma amostra de telemetria armazenada para esta execução.</p>
              : <ol className="m-0 max-h-24 list-none overflow-y-auto p-0 font-mono text-caption leading-[1.6] text-secondary">
                {samples.map((sample) => <li key={sample.sequence}>
                  [{elapsedLabel(sample.recordedAt, firstSampleAt)}] Bateria {formatNumber(sample.batteryVoltageVolts ?? undefined)} V
                  {' '}— Vel. {formatNumber(sample.currentSpeedMetersPerSecond ?? undefined)} m/s
                </li>)}
              </ol>}
          </section>
        </div>
      </div>

      <div className="mt-3 grid grid-cols-2 gap-3.5 [@media(max-width:960px)]:grid-cols-1">
        <LineChart
          title="BATERIA × TEMPO"
          points={samplePoints((sample) => sample.batteryVoltageVolts)}
          color="var(--amber)"
          unit="V"
          height={140}
          compact
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
          compact
          xFromZero
          xTickFormat={(x) => `${x}s`}
        />
      </div>
    </>}
  </>
}

function RunCell({ label, children }: { label: string; children: ReactNode }) {
  return <div className="grid min-w-0 content-center gap-[5px] border-l border-line px-[18px] py-2.5 first:border-l-0 max-[960px]:border-t max-[960px]:border-l-0 max-[960px]:first:col-span-full max-[960px]:first:border-t-0">
    <span className="text-caption text-secondary">{label}</span>
    {children}
  </div>
}

function Value({ className = '', children }: { className?: string; children: ReactNode }) {
  return <strong className={`text-body font-semibold text-text ${className}`}>{children}</strong>
}

export function Message({ error = false, children }: { error?: boolean; children: ReactNode }) {
  const colors = error ? 'border-[#65443d] bg-[#302321] text-danger' : 'border-line bg-surface text-secondary'
  return <p className={`rounded-[4px] border p-4 text-body ${colors}`}>{children}</p>
}

export function Stat({ label, value, unit, compact = false }: { label: string; value: string; unit?: string; compact?: boolean }) {
  const isPlaceholder = value === '—'
  return <article className={`rounded-[4px] border border-line bg-surface ${compact ? 'p-2.5' : 'p-[13px]'}`}>
    <span className="text-caption text-secondary">{label}</span>
    <div className={`mt-2 font-mono text-[25px] leading-[1.1] font-medium tracking-[-.04em] tabular-nums text-text max-[700px]:text-[21px] ${compact ? 'mt-1 text-[21px]' : ''} ${isPlaceholder ? 'text-body font-normal tracking-normal text-muted' : ''}`}>
      {value}{unit && !isPlaceholder && <small className="ml-[7px] font-mono text-caption tracking-normal text-secondary">{unit}</small>}
    </div>
  </article>
}

export function StatusTag({ status }: { status: RunStatus }) {
  return <span className={`inline-block whitespace-nowrap rounded-[3px] px-2 py-1 font-mono text-caption leading-[normal] ${statusClasses[status]}`}>
    {statusLabels[status]}
  </span>
}

function elapsedLabel(recordedAt: string, startedAt: number) {
  const seconds = Math.max(0, (Date.parse(recordedAt) - startedAt) / 1000)
  return Number.isFinite(seconds) ? `${seconds.toFixed(1).padStart(5, '0')}s` : '—'
}
