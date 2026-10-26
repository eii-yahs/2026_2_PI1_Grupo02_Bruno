import { lazy, Suspense, useEffect, useState } from 'react'
import App from './App.tsx'

const GeneralQueryPage = lazy(() => import('./pages/GeneralQueryPage.tsx').then(({ GeneralQueryPage }) => ({ default: GeneralQueryPage })))
const MazeQueryPage = lazy(() => import('./pages/MazeQueryPage.tsx').then(({ MazeQueryPage }) => ({ default: MazeQueryPage })))

// Navegação por hash: #consulta-geral abre a consulta geral, #consulta-por-labirinto/GRID_4X4 a consulta
// por labirinto; qualquer outro valor abre o monitoramento.
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
  const showMazeQuery = hash.startsWith('#consulta-por-labirinto')

  return <>
    <div hidden={showGeneralQuery || showMazeQuery}><App /></div>
    <Suspense fallback={<p className="p-4 text-muted" role="status">Carregando consulta…</p>}>
      {showGeneralQuery && <GeneralQueryPage hash={hash} />}
      {showMazeQuery && <MazeQueryPage hash={hash} />}
    </Suspense>
  </>
}
