import { useEffect, useState } from 'react'
import App from './App.tsx'
import { HistoryPage } from './pages/HistoryPage.tsx'

// Navegação por hash: #historico abre a consulta geral; qualquer outro valor abre o monitoramento
export function Root() {
  const [hash, setHash] = useState(window.location.hash)

  useEffect(() => {
    const onHashChange = () => setHash(window.location.hash)
    window.addEventListener('hashchange', onHashChange)
    return () => window.removeEventListener('hashchange', onHashChange)
  }, [])

  return hash.startsWith('#historico') ? <HistoryPage hash={hash} /> : <App />
}
