# Testes de Software

## Testes de _software_

Esta seção descreve a estratégia de verificação do software do Rato Cego, formada pelo firmware de navegação, pela interface web e pelo backend. A estrutura segue a seção de testes de software do relatório de referência. As histórias de usuário (HUs), requisitos e casos de teste funcionais são rastreados ao [Projeto conceitual de software](Projeto%20conceitual%20de%20software.md).

O repositório contém testes automatizados do backend. Na inspeção dos arquivos versionados, não foram encontrados testes unitários automatizados do frontend nem uma suíte E2E configurada. Também não há relatório de execução ou de cobertura versionado; por isso, este documento não atribui resultados de aprovação ou percentuais de cobertura que ainda não foram registrados.

### 1. Testes do algoritmo de navegação

O algoritmo de navegação embarcada deve ser validado isoladamente antes de sua integração com sensores, motores e comunicação. Os cenários definidos para a validação funcional incluem identificar paredes, atualizar a posição e o trajeto e alcançar o objetivo sem intervenção, conforme CT-01, CT-02 e CT-03 do roteiro funcional.

No estado atual do repositório, `src/firmware` contém apenas instruções de organização, sem código-fonte do firmware ou arquivos de teste do algoritmo. Assim, não há execução ou evidência versionada desses cenários nesta entrega. Quando o algoritmo estiver disponível, os testes devem cobrir ao menos os labirintos 4×4, 8×4 e 12×4 previstos no roteiro, incluindo caminhos sem saída, paredes em cada direção e chegada ao objetivo.

### 2. Histórias de usuário e casos de teste funcionais

O [protótipo funcional](Projeto%20conceitual%20de%20software.md#10-protótipo-funcional) documenta quatro páginas: Monitoramento em Tempo Real, Consulta Geral, Consulta por Labirinto e Detalhe da Execução. A tabela relaciona os casos CT-01 a CT-16 do roteiro funcional às HUs. Os casos são especificados no projeto conceitual; a existência de um caso documentado não significa que ele já tenha sido executado ou automatizado.

| Caso | Funcionalidade verificada | Histórias de usuário relacionadas | Protótipo ou subsistema | Situação da automação neste repositório |
|---|---|---|---|---|
| CT-01 | Identificação de paredes | HU20 | Firmware / navegação | Roteiro documentado; firmware e teste automatizado não localizados. |
| CT-02 | Localização, deslocamento e registro do trajeto | HU21, HU25, HU34, HU53 | Monitoramento em Tempo Real e Detalhe da Execução; firmware | Roteiro documentado; sem suíte E2E localizada. |
| CT-03 | Navegação autônoma e identificação do objetivo | HU22, HU23, HU24 | Firmware / navegação | Roteiro documentado; firmware e teste automatizado não localizados. |
| CT-04 | Seleção do labirinto e início da execução | HU26, HU27, HU28, HU30 | Monitoramento em Tempo Real | Roteiro documentado; sem suíte E2E localizada. |
| CT-05 | Interrupção e parada segura | HU29, HU33 | Monitoramento em Tempo Real; firmware | Roteiro documentado; sem suíte E2E localizada. |
| CT-06 | Estados e encerramento da execução | HU31, HU32 | Monitoramento em Tempo Real | Roteiro documentado; sem suíte E2E localizada. |
| CT-07 | Recepção e integridade da telemetria | HU35, HU45, HU47 | Monitoramento em Tempo Real; backend | Há testes automatizados de backend relacionados a payloads, MQTT e fluxo de telemetria; E2E não localizado. |
| CT-08 | Exibição dos dados da execução e alerta de bateria | HU16, HU18, HU36–HU40 | Monitoramento em Tempo Real | Roteiro documentado; sem suíte E2E localizada. |
| CT-09 | Conexão, perda e reconexão WebSocket | HU41–HU44 | Monitoramento em Tempo Real; backend | Há testes automatizados do backend para configuração, heartbeat e publicação WebSocket; E2E não localizado. |
| CT-10 | Perda e reconexão MQTT | HU46 | Backend / comunicação MQTT | Há testes automatizados de backend para configuração, saúde e adaptador MQTT; sem cenário E2E localizado. |
| CT-11 | Responsividade e adaptação da interface | RNF40, RNF42 | Todas as páginas | Roteiro documentado; sem teste visual ou E2E localizado. |
| CT-12 | Armazenamento e associação da execução ao labirinto | HU48, HU49 | Consulta Geral e Detalhe da Execução; backend | Há testes automatizados de serviços de ciclo e consulta; E2E não localizado. |
| CT-13 | Consulta do histórico por labirinto e geral | HU50, HU51 | Consulta por Labirinto e Consulta Geral | Há testes automatizados do serviço de consulta; E2E não localizado. |
| CT-14 | Registro de falhas e interrupções | HU52 | Consulta Geral; backend | Há testes automatizados do ciclo de execução; E2E não localizado. |
| CT-15 | Calibração dos sensores | HU54 | Firmware / sensores | Roteiro documentado; firmware e teste automatizado não localizados. |
| CT-16 | Sinalização do ciclo de recarga | HU55 | Firmware / hardware | Roteiro documentado; firmware e teste automatizado não localizados. |

### 3. Testes unitários do frontend

Na inspeção de `src/frontend`, não foram encontrados arquivos de teste (`*.test.*` ou `*.spec.*`), configuração de Vitest/Jest nem dependência de ferramenta de testes no `package.json`. O frontend é uma aplicação React com TypeScript; suas páginas e componentes estão em `src/frontend/src/pages` e `src/frontend/src/components`.

Ainda precisam ser implementados testes para componentes e utilitários, cobrindo renderização, propriedades, interações, formatação de dados, estados de execução e atualização da interface. Também é necessário configurar a coleta de cobertura e registrar o relatório resultante. Até essa configuração e execução, não há percentual de cobertura do frontend a apresentar.

### 4. Testes unitários e de integração do backend

O backend Java utiliza JUnit 5 e Mockito, com suporte de testes do Spring Boot. Existem 21 classes de teste em `src/backend/src/test/java/br/edu/pi1/ratocego`, abrangendo serviços, controladores, modelos, validadores, mapeadores e adaptadores.

Os testes de serviço verificam o ciclo de vida das execuções, validação e processamento de telemetria e consultas ao histórico. Há também testes para validação dos payloads MQTT, configuração e publicação MQTT, recebimento de eventos, configuração e publicação WebSocket, heartbeat e transação das atualizações. A classe `MqttTelemetryWebSocketFlowTest` cobre o fluxo de telemetria entre as camadas de MQTT e WebSocket. Essa suíte testa componentes do backend e suas integrações simuladas; não substitui os testes de sistema com a interface, o broker e o robô conectados.

O Maven está configurado com JaCoCo para gerar relatório durante a fase `verify` e verificar cobertura mínima de 80% das linhas do conjunto do backend. A classe `RunStatus` também possui metas próprias de 100% para linhas e desvios condicionais. Esses valores são critérios configurados, não resultados medidos: um relatório de cobertura executado ainda precisa ser anexado ou referenciado nesta documentação.

Para gerar a evidência localmente, execute na pasta `src/backend`:

```bash
./mvnw verify
```

O relatório HTML de cobertura é gerado em `src/backend/target/site/jacoco/index.html`; os resultados individuais do Surefire ficam em `src/backend/target/surefire-reports`. Os arquivos de saída não estão versionados no repositório no momento.

### 5. Testes de sistema E2E

O roteiro funcional do projeto define os casos CT-01 a CT-16 e prevê a validação da interface junto aos serviços. Porém, não foram encontrados no frontend arquivos de Cypress ou Playwright, configuração de execução E2E, nem dependências correspondentes. Portanto, não há relatório de execução E2E ou casos automatizados a declarar nesta versão.

Quando a suíte for criada, os casos prioritários para o fluxo web são CT-04 a CT-14: iniciar e interromper uma execução, receber telemetria, exibir métricas, lidar com conexões, persistir e consultar o histórico. CT-01 a CT-03, CT-15 e CT-16 dependem também do firmware e do hardware. A automação deve registrar a execução aprovada e reprovada, e relacionar cada cenário ao caso funcional correspondente.

### 6. Testes de integração com os componentes externos

Os testes automatizados versionados exercitam integrações internas do backend por meio de dependências simuladas e testes de componentes Spring. Eles cobrem, entre outros fluxos, a recepção de eventos MQTT, o processamento pelo serviço, a publicação de atualizações WebSocket, os endpoints e as regras de persistência/consulta.

Não foi localizada evidência versionada de uma execução integrada com broker Mosquitto, banco PostgreSQL, interface web e firmware físico em funcionamento simultâneo. Essa validação de ponta a ponta permanece associada aos casos CT-04 a CT-14 e deve registrar as versões/configurações usadas, os resultados observados e as evidências visuais ou logs. Os testes de hardware e do firmware devem ser documentados também nas seções próprias de [testes de hardware](Testes%20de%20hardware.md) e do projeto conceitual.
