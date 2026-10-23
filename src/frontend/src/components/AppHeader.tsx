type Props = { active?: 'monitor' | 'consulta-geral' }

export function AppHeader({ active = 'monitor' }: Props) {
  return (
    <header className="topbar">
      <a className="brand" href="#monitor" aria-label="Rato Cego, monitor">
        RATO<span>CEGO</span>
      </a>

      <nav aria-label="Navegação principal">
        <a className={`nav-link ${active === 'monitor' ? 'active' : ''}`} href="#monitor">
          Monitoramento
        </a>
        <a className={`nav-link ${active === 'consulta-geral' ? 'active' : ''}`} href="#consulta-geral">
          Consulta geral
        </a>
      </nav>

      <span className="topbar-note">
        CENTRAL DE OPERAÇÃO <span>•</span> MICROMOUSE
      </span>
    </header>
  )
}
