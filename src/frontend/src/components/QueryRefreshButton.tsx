import { RefreshCw } from 'lucide-react'

type Props = {
  refreshing: boolean
  onRefresh: () => void
}

export function QueryRefreshButton({ refreshing, onRefresh }: Props) {
  return (
    <button
      type="button"
      className="inline-flex min-h-10 shrink-0 cursor-pointer items-center justify-center gap-2 rounded-[3px] border border-[#4a4e48] bg-[#292c29] px-3 text-small font-semibold text-[#d0d0c8] transition-colors hover:border-[#777b70] hover:bg-[#333733] disabled:cursor-not-allowed disabled:opacity-[.42]"
      onClick={onRefresh}
      disabled={refreshing}
      aria-label="Atualizar consulta"
    >
      <RefreshCw className={`size-[15px] ${refreshing ? 'animate-spin' : ''}`} aria-hidden="true" />
      <span>{refreshing ? 'Atualizando…' : 'Atualizar'}</span>
    </button>
  )
}
