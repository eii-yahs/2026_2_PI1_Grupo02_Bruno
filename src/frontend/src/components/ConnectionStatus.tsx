type Props = {
  status: string
  clock: string
}

export function ConnectionStatus({ status, clock }: Props) {
  const tone = status === 'Ao vivo'
    ? 'text-green'
    : status === 'Telemetria desatualizada' ? 'text-amber' : 'text-secondary'
  const dotTone = status === 'Ao vivo'
    ? 'bg-green'
    : status === 'Telemetria desatualizada' ? 'bg-amber' : 'bg-[#858981]'

  return (
    <div className={`flex items-center gap-[9px] border-l border-line bg-transparent py-2 pl-3 font-mono text-caption ${tone} max-[700px]:self-stretch`}>
      <span className={`size-[7px] shrink-0 rounded-full ${dotTone}`} />
      <span>{status}</span>
      <span className="mx-[3px] h-[13px] w-px bg-line" />
      <span className="font-mono tabular-nums">{clock}</span>
    </div>
  )
}
