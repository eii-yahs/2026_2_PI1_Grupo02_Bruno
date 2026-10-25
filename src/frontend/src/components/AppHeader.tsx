type Props = { active?: 'monitor' | 'consulta-geral' }

export function AppHeader({ active = 'monitor' }: Props) {
  return (
    <header className="flex h-16 items-center gap-12 border-b border-line bg-[#1d201e] px-[max(5vw,calc((100vw-1440px)/2))] max-[700px]:h-[58px] max-[700px]:gap-6 max-[700px]:px-5">
      <a className="font-mono text-sm font-semibold tracking-[.08em] text-text no-underline" href="#monitor" aria-label="Rato Cego, monitor">
        RATO<span className="text-amber">CEGO</span>
      </a>

      <nav className="flex h-full items-stretch gap-[25px] max-[700px]:gap-[17px]" aria-label="Navegação principal">
        <a className={`relative grid items-center text-body no-underline transition-colors hover:text-text max-[700px]:text-caption ${active === 'monitor' ? 'text-text after:absolute after:inset-x-0 after:-bottom-px after:h-0.5 after:bg-amber' : 'text-muted'}`} href="#monitor">
          Monitoramento
        </a>
        <a className={`relative grid items-center text-body no-underline transition-colors hover:text-text max-[700px]:text-caption ${active === 'consulta-geral' ? 'text-text after:absolute after:inset-x-0 after:-bottom-px after:h-0.5 after:bg-amber' : 'text-muted'}`} href="#consulta-geral">
          Consulta geral
        </a>
      </nav>

    </header>
  )
}
