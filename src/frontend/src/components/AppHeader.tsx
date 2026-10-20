export function AppHeader() {
  return (
    <header className="topbar">
      <a className="brand" href="#monitor" aria-label="Rato Cego, monitor">
        RATO<span>CEGO</span>
      </a>

      <nav aria-label="Navegação principal">
        <a className="nav-link active" href="#monitor">
          Monitoramento
        </a>
        <a className="nav-link" href="#historico" onClick={(event) => event.preventDefault()}>
          Histórico
        </a>
      </nav>

      <span className="topbar-note">
        CENTRAL DE OPERAÇÃO <span>•</span> MICROMOUSE
      </span>
    </header>
  )
}
