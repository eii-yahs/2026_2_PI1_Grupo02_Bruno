type Props = {
  status: string
  clock: string
}

export function ConnectionStatus({ status, clock }: Props) {
  const statusClass =
    status === 'Ao vivo' ? 'is-live' : status === 'Telemetria desatualizada' ? 'is-stale' : ''

  return (
    <div className={`connection ${statusClass}`}>
      <span className="connection-dot" />
      <span>{status}</span>
      <span className="connection-divider" />
      <span className="mono">{clock}</span>
    </div>
  )
}
