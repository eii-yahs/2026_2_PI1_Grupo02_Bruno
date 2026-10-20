import { useEffect, useState } from 'react'
import { fetchRun, fetchRuns } from '../api/runs'
import {
  challengeLabel, formatDateTime, formatDuration, formatNumber, mazeLabel, statusLabels, type Run,
} from '../models/run'
import './HistoryPage.css'

type Props = { hash: string }

// #historico → consulta geral; #historico/{id} → detalhe da execução
export function HistoryPage({ hash }: Props) {
  const runId = Number(hash.split('/')[1])
  return <div className="history-page">
    <main className="history-content">
      {Number.isInteger(runId) && runId > 0 ? <RunDetail runId={runId} /> : <RunList />}
    </main>
  </div>
}

function RunList() {
  const [runs, setRuns] = useState<Run[] | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchRuns()
      // O backend devolve da mais antiga para a mais recente; aqui a mais recente fica no topo
      .then((data) => setRuns([...data].reverse()))
      .catch((e: Error) => setError(e.message))
  }, [])

  return <>
    <header className="history-heading">
      <p className="history-eyebrow">HISTÓRICO</p>
      <h1>Consulta geral</h1>
      <p className="history-subheading">Todas as execuções armazenadas, de todos os labirintos.</p>
    </header>
    {error && <p className="history-message history-error">{error}</p>}
    {!error && runs === null && <p className="history-message">Carregando execuções…</p>}
    {runs?.length === 0 && <p className="history-message">Nenhuma execução armazenada ainda.</p>}
    {runs && runs.length > 0 && <div className="history-table-wrap">
      <table className="history-table">
        <thead>
          <tr>
            <th>Execução</th>
            <th>Labirinto</th>
            <th>Status</th>
            <th>Início</th>
            <th>Duração</th>
            <th>Vel. média (m/s)</th>
            <th>Desafio concluído</th>
          </tr>
        </thead>
        <tbody>
          {runs.map((run) => <tr key={run.id}>
            <td><a href={`#historico/${run.id}`}>#{run.id}</a></td>
            <td>{mazeLabel(run)}</td>
            <td><span className={`history-status status-${run.status.toLowerCase()}`}>{statusLabels[run.status]}</span></td>
            <td>{formatDateTime(run.startedAt)}</td>
            <td className="history-mono">{formatDuration(run.duration)}</td>
            <td className="history-mono">{formatNumber(run.averageSpeedMetersPerSecond)}</td>
            <td>{challengeLabel(run)}</td>
          </tr>)}
        </tbody>
      </table>
    </div>}
  </>
}

function RunDetail({ runId }: { runId: number }) {
  const [run, setRun] = useState<Run | null | undefined>(undefined)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchRun(runId).then(setRun).catch((e: Error) => setError(e.message))
  }, [runId])

  return <>
    <a className="history-back" href="#historico">← Voltar para a consulta geral</a>
    <header className="history-heading">
      <p className="history-eyebrow">DETALHE DA EXECUÇÃO</p>
      <h1>Execução #{runId}</h1>
    </header>
    {error && <p className="history-message history-error">{error}</p>}
    {!error && run === undefined && <p className="history-message">Carregando execução…</p>}
    {run === null && <p className="history-message">Execução #{runId} não encontrada.</p>}
    {run && <dl className="history-detail">
      <Field label="Labirinto" value={mazeLabel(run)} />
      <Field label="Status" value={statusLabels[run.status]} />
      <Field label="Desafio concluído" value={challengeLabel(run)} />
      <Field label="Início" value={formatDateTime(run.startedAt)} />
      <Field label="Fim" value={formatDateTime(run.finishedAt)} />
      <Field label="Tempo de execução" value={formatDuration(run.duration)} />
      <Field label="Distância percorrida" value={formatNumber(run.distanceTravelledMeters)} unit="m" />
      <Field label="Velocidade média" value={formatNumber(run.averageSpeedMetersPerSecond)} unit="m/s" />
      <Field label="Carga consumida" value={formatNumber(run.chargeConsumedMilliampHours)} unit="mAh" />
      <Field label="Energia consumida" value={formatNumber(run.energyConsumedWattHours, 3)} unit="Wh" />
    </dl>}
  </>
}

function Field({ label, value, unit }: { label: string; value: string; unit?: string }) {
  return <div className="history-field">
    <dt>{label}</dt>
    <dd>{value}{unit && value !== '—' && <small> {unit}</small>}</dd>
  </div>
}
