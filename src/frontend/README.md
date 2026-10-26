# Frontend

Interface do Rato Cego, inicializada pelo [Vite](https://vite.dev/) com React, TypeScript, `@vitejs/plugin-react` e Tailwind CSS 4 pelo plugin oficial `@tailwindcss/vite`.

## Requisitos

- Node.js 22.12 ou superior (compatível com Vite 8)
- npm

## Executar

```bash
npm install
npm run dev
```

O servidor de desenvolvimento encaminha `/api` para `http://localhost:8080` e WebSocket em `/ws` para o backend local. A URL e o caminho finais deverão acompanhar os contratos da API/WebSocket quando forem implementados.

Os diretórios `api`, `components`, `models` e `realtime` seguem a organização apresentada no diagrama de pacotes; os módulos serão preenchidos conforme as funcionalidades forem implementadas.
