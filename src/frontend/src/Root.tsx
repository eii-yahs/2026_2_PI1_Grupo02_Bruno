import { useEffect, useState } from 'react'
import App from './App.tsx'
import { GeneralQueryPage } from './pages/GeneralQueryPage.tsx'

// Navegação por hash: #consulta-geral abre a consulta geral; qualquer outro valor abre o monitoramento.
// O monitoramento continua montado (só fica escondido) para não perder a conexão e o trajeto
// de uma execução em andamento enquanto o operador consulta o histórico.
export function Root() {
  const [hash, setHash] = useState(window.location.hash)

  useEffect(() => {
    const onHashChange = () => setHash(window.location.hash)
    window.addEventListener('hashchange', onHashChange)
    return () => window.removeEventListener('hashchange', onHashChange)
  }, [])

  const showGeneralQuery = hash.startsWith('#consulta-geral')

  return <>
    <div hidden={showGeneralQuery}><App /></div>
    {showGeneralQuery && <GeneralQueryPage hash={hash} />}
  </>
}
