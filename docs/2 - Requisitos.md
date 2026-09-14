# Requisitos

> Documento que descreve as funcionalidades, restrições e critérios técnicos que orientam o desenvolvimento e a validação do micromouse Rato Cego.

## Visão Geral dos Requisitos

Este documento consolida os requisitos do projeto Rato Cego, um robô móvel autônomo capaz de mapear e solucionar labirintos. Os requisitos orientam as decisões das equipes de Hardware, Estruturas, Energia e Software, assegurando que os subsistemas sejam desenvolvidos de forma integrada e atendam aos objetivos definidos no Termo de Abertura do Projeto.

Cada requisito possui um identificador único, uma descrição objetiva, uma prioridade e os campos destinados à definição dos responsáveis e ao acompanhamento no GitHub Projects. A numeração é contínua dentro de cada tipo de requisito, independentemente da área responsável.

## Classificação e Priorização

Os **Requisitos Funcionais (RF)** definem os comportamentos e serviços que o micromouse e seus sistemas associados devem fornecer, como navegação, monitoramento e registro de dados.

Os **Requisitos Não Funcionais (RNF)** estabelecem critérios mensuráveis de qualidade, desempenho, segurança e restrições físicas que a solução deve cumprir.

A prioridade de cada item é definida pela classificação MoSCoW:

- **Must:** requisito indispensável para a entrega e validação do projeto.
- **Should:** requisito importante, a ser implementado quando houver viabilidade técnica e de prazo.
- **Could:** requisito desejável, implementado caso não comprometa os itens de maior prioridade.

## Requisitos Funcionais (RF)

**Energia**

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
| RF1 | Consulta local do estado energético | O Rato Cego deve informar localmente a faixa de carga da bateria por meio de LEDs de estado ou display OLED, permitindo ao operador verificar a condição energética antes da corrida sem acessar a plataforma web. | Could | - | - |
| RF2 | Telemetria energética por execução | Ao término de cada percurso, o sistema deve associar o consumo de energia à execução realizada e disponibilizar esse registro para consulta no sistema web, juntamente com os demais dados de desempenho. | Must | - | - |
| RF3 | Isolamento manual da alimentação | O circuito de alimentação deve incluir uma chave física de fácil acesso que desconecte a bateria dos subsistemas do robô, permitindo desligamento seguro durante transporte, montagem e manutenção. | Must | - | - |
| RF4 | Estratégia de conservação por bateria baixa | O firmware deve acompanhar periodicamente a tensão da bateria e, ao identificar nível crítico, reduzir cargas não essenciais e limitar a operação para preservar energia e evitar desligamento abrupto durante a execução. | Must | - | - |
| RF5 | Sinalização do ciclo de recarga | Durante a recarga, o micromouse deve indicar visualmente o estado do ciclo, distinguindo carregamento em andamento, carga concluída e condição de falha. | Could | - | - |
| RF6 | Retenção de dados energéticos | Os dados de tensão e consumo coletados durante as corridas devem ser mantidos em memória não volátil até o envio à plataforma web ou a exclusão deliberada pelo operador. | Should | - | - |

**Hardware**

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |

**Estruturas**

| **ID** | **Nome do Requisito**                         | **Descrição**                                                                                                                                                                                                                                        | **Prioridade** | **Responsáveis** | **Link Github Projects** |
| :----: | --------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------: | ---------------- | ------------------------ |
|   RF7  | Deslocamento livre no labirinto               | A estrutura do Rato Cego deve permitir que o micromouse percorra os corredores do labirinto sem interferências com as paredes ou demais elementos do percurso, mantendo espaço adequado para a movimentação dos componentes de locomoção e sensores. |      Must      | -                | -                        |
|   RF8  | Proteção dos componentes internos             | A estrutura deve proteger os componentes eletrônicos, sensores, motores e demais módulos internos contra choques mecânicos e possíveis danos decorrentes da movimentação ou de eventuais colisões durante a execução dos percursos.                  |      Must      | -                | -                        |
|   RF9  | Acesso aos componentes internos               | A estrutura deve permitir o acesso aos componentes internos para inspeção, manutenção, substituição e realização de ajustes, sem exigir a desmontagem completa do chassi.                                                                            |     Should     | -                | -                        |
|  RF10  | Fixação dos subsistemas                       | A estrutura deve possuir pontos de fixação adequados para acomodar e manter posicionados os componentes dos subsistemas de Hardware, Energia e Software embarcado, evitando deslocamentos durante a movimentação do micromouse.                      |      Must      | -                | -                        |
|  RF11  | Modularidade estrutural                       | A estrutura deve permitir a substituição ou atualização de módulos e componentes de forma independente, possibilitando alterações no protótipo sem a necessidade de reconstrução completa do chassi.                                                 |     Should     | -                | -                        |
|  RF12  | Fixação e alinhamento do sistema de locomoção | A estrutura deve possuir pontos de fixação para motores, rodas e demais elementos do sistema de locomoção, mantendo seu posicionamento e alinhamento durante a execução dos percursos.                                                               |      Must      | -                | -                        |
|  RF13  | Acomodação e posicionamento dos sensores      | A estrutura deve possuir espaços e pontos de fixação adequados para a instalação dos sensores nas posições definidas pelo projeto, permitindo que permaneçam orientados e desobstruídos durante o percurso.                                          |      Must      | -                | -                        |
|  RF14  | Organização e proteção do cabeamento          | A estrutura deve disponibilizar meios para organizar e proteger os cabos e conexões internas, evitando que interfiram na movimentação das rodas, motores, sensores ou demais componentes durante a operação.                                         |      Must      | -                | -                        |
|  RF15  | Acesso aos elementos de operação              | A estrutura deve permitir o acesso externo aos elementos necessários para operação, manutenção e preparação do micromouse, como chave de alimentação, conectores e demais interfaces definidas pelos subsistemas.                                    |     Should     | -                | -                        |


**Software**

| ID       | Nome do Requisito                   | Descrição                                                                                                                                                                                      | Prioridade  | Responsáveis | Link para projetos do Github |
| -------- | ----------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------- | ------------ | ---------------------------- |
| **RF01** | Identificação de paredes            | O software do **Rato Cego** deve identificar a presença de paredes no percurso a partir dos dados fornecidos pelos sensores.                                                                   | Deve    | -            | -                            |
| **RF02** | Mapeamento do labirinto             | O software do **Rato Cego** deve construir e atualizar progressivamente o mapeamento do labirinto conforme o robô realiza o percurso.                                                          | Deve    | -            | -                            |
| **RF03** | Localização no labirinto            | O software do **Rato Cego** deve monitorar a localização do robô dentro do labirinto durante o percurso.                                                                                       | Deve    | -            | -                            |
| **RF04** | Determinação do percurso            | O software do **Rato Cego** deve determinar autonomamente os movimentos necessários para avançar pelo labirinto com base nas informações obtidas durante o percurso.                           | Deve    | -            | -                            |
| **RF05** | Navegação autônoma                  | O **Rato Cego** deve navegar pelo labirinto sem intervenção humana durante a execução do percurso.                                                                                             | Deve    | -            | -                            |
| **RF06** | Identificação do objetivo           | O software do **Rato Cego** deve identificar quando o robô alcançar a área de objetivo do labirinto.                                                                                           | Deve    | -            | -                            |
| **RF07** | Registro do trajeto                 | O sistema deve registrar o trajeto percorrido pelo **Rato Cego** durante cada execução.                                                                                                        | Deve    | -            | -                            |
| **RF08** | Transmissão de telemetria           | O **Rato Cego** deve transmitir ao sistema web os dados de desempenho necessários para o acompanhamento da execução.                                                                           | Deve    | -            | -                            |
| **RF09** | Identificação do tipo de labirinto  | O sistema web deve identificar e exibir o tipo de labirinto correspondente à execução atual.                                                                                                   | Deve    | -            | -                            |
| **RF10** | Visualização do trajeto             | Durante o percurso, o sistema web deve exibir e atualizar em tempo real o trajeto percorrido pelo **Rato Cego**.                                                                               | Deve    | -            | -                            |
| **RF11** | Monitoramento da bateria            | Durante o percurso, o sistema web deve exibir e atualizar em tempo real o consumo de bateria do **Rato Cego**.                                                                                 | Deve    | -            | -                            |
| **RF12** | Monitoramento do tempo              | O sistema deve contabilizar o tempo da execução e apresentá-lo no sistema web durante o percurso.                                                                                              | Deve    | -            | -                            |
| **RF13** | Exibição da velocidade média        | O sistema web deve calcular e apresentar a velocidade média do **Rato Cego** referente à execução.                                                                                             | Deve    | -            | -                            |
| **RF14** | Resultado do desafio                | O sistema web deve informar se o **Rato Cego** cumpriu ou não o desafio correspondente à execução.                                                                                             | Deve    | -            | -                            |
| **RF15** | Armazenamento da execução           | Após a execução, o sistema deve armazenar no banco de dados as informações coletadas durante o percurso.                                                                                       | Deve    | -            | -                            |
| **RF16** | Associação ao labirinto             | O sistema deve associar os dados armazenados de cada execução ao respectivo labirinto.                                                                                                         | Deve    | -            | -                            |
| **RF17** | Consulta por labirinto              | O sistema web deve permitir consultar os dados das execuções referentes a um labirinto específico.                                                                                             | Deve    | -            | -                            |
| **RF18** | Consulta geral                      | O sistema web deve permitir consultar conjuntamente os dados armazenados das execuções realizadas nos diferentes labirintos.                                                                   | Deve    | -            | -                            |
| **RF19** | Consulta local do estado energético | O **Rato Cego** poderia informar localmente a condição da bateria por meio de um indicador visual, permitindo sua verificação sem acesso ao sistema web.                                       | Poderia | -            | -                            |
| **RF20** | Conservação em bateria baixa        | O firmware do **Rato Cego** deveria monitorar a condição da bateria e, ao identificar nível crítico, adotar medidas para preservar energia e evitar desligamentos abruptos durante a execução. | Deveria | -            | -                            |
| **RF21** | Sinalização do ciclo de recarga     | Durante a recarga, o **Rato Cego** poderia indicar visualmente o estado do ciclo de carregamento.                                                                                              | Poderia | -            | -                            |


## Requisitos Não-Funcionais (RNF)

**Energia**

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
| RNF1 | Autonomia operacional | Com sensores, controle, motores e telemetria ativos no perfil normal de corrida, o Rato Cego deve funcionar continuamente por pelo menos 20 minutos sem recarga ou troca de bateria. | Must | - | - |
| RNF2 | Capacidade energética para exploração | A capacidade útil da bateria deve ser suficiente para que o robô conclua o mapeamento de um labirinto de 16 × 16 células em uma única carga. | Must | - | - |
| RNF3 | Rendimento da conversão de energia | Os conversores e reguladores de tensão devem apresentar eficiência igual ou superior a 85% na faixa nominal de carga dos subsistemas que alimentam. | Should | - | - |
| RNF4 | Consumo em inatividade | Quando o robô estiver ligado e sem executar uma corrida, a corrente total do sistema deve permanecer em até 50 mA, desativando periféricos que não sejam necessários. | Should | - | - |
| RNF5 | Aviso preventivo de descarga | O sistema deve reconhecer a condição de baixa carga e gerar alerta quando qualquer célula atingir 3,2 V, antes de alcançar uma faixa que possa comprometer sua vida útil. | Must | - | - |
| RNF6 | Restrição de massa do conjunto energético | Bateria, circuito de recarga, reguladores, proteções e cabeamento de alimentação não podem, em conjunto, ultrapassar 25% da massa final do micromouse. | Must | - | - |
| RNF7 | Acesso externo para recarga | A recarga da bateria deve ocorrer por conector acessível na estrutura externa do robô, sem remoção de peças do chassi ou desconexão de componentes internos. | Must | - | - |
| RNF8 | Integridade da alimentação dos sensores | Sob aceleração e partida dos motores, a tensão fornecida aos sensores deve se manter entre 95% e 105% de seu valor nominal. | Must | - | - |
| RNF9 | Proteção contra falhas elétricas | O sistema de alimentação deve interromper ou limitar a corrente em situações de sobrecorrente ou curto-circuito, protegendo a bateria e os circuitos eletrônicos. | Must | - | - |
| RNF10 | Confiabilidade da telemetria energética | Após a calibração, as leituras de tensão e consumo registradas pelo robô devem apresentar erro máximo de 5% em relação ao instrumento de referência. | Should | - | - |

**Hardware**

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |

**Estruturas**

| **ID** | **Nome do Requisito**                         | **Descrição**                                                                                                                                                                                                                              | **Prioridade** | **Responsáveis** | **Link Github Projects** |
| :----: | --------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | :------------: | ---------------- | ------------------------ |
|  RNF11 | Limite dimensional do chassi                  | O conjunto estrutural do Rato Cego deve respeitar o limite máximo de **16,5 cm de comprimento e 16,5 cm de largura**, considerando o robô em sua configuração de operação e todos os componentes que façam parte de sua estrutura externa. |      Must      | -                | -                        |
|  RNF12 | Compatibilidade com as dimensões do labirinto | A estrutura deve ser compatível com os corredores formados por células de **18 cm de lado**, permitindo a circulação do micromouse sem que suas dimensões impeçam a passagem pelo percurso.                                                |      Must      | -                | -                        |
|  RNF13 | Massa estrutural                              | A massa do chassi e dos elementos estruturais deve ser mantida dentro de um limite que não comprometa a aceleração, frenagem, estabilidade e autonomia do micromouse.                                                                      |      Must      | -                | -                        |
|  RNF14 | Resistência mecânica                          | A estrutura deve suportar os esforços mecânicos decorrentes da movimentação, aceleração, frenagem e eventuais impactos durante os testes, sem apresentar deformações que comprometam o funcionamento do robô.                              |      Must      | -                | -                        |
|  RNF15 | Rigidez estrutural                            | O chassi deve apresentar rigidez suficiente para manter sua geometria e os componentes fixados em suas posições durante a execução dos percursos, evitando deformações ou folgas que prejudiquem a navegação.                              |      Must      | -                | -                        |
|  RNF16 | Estabilidade estrutural                       | A estrutura deve proporcionar estabilidade ao micromouse durante acelerações, frenagens, curvas e mudanças de direção, evitando inclinações ou deslocamentos que comprometam sua movimentação.                                             |      Must      | -                | -                        |
|  RNF17 | Distribuição de massa                         | A disposição dos componentes na estrutura deve proporcionar uma distribuição de massa adequada, evitando desequilíbrios que possam prejudicar a estabilidade e o desempenho do micromouse durante o percurso.                              |     Should     | -                | -                        |
|  RNF18 | Compatibilidade entre subsistemas             | A estrutura deve possuir dimensões, espaços internos e pontos de fixação compatíveis com os componentes definidos pelas equipes de Hardware, Energia e Software, permitindo a integração dos subsistemas sem interferências físicas.       |      Must      | -                | -                        |
|  RNF19 | Não interferência com o labirinto             | A estrutura não deve possuir dimensões, partes ou elementos que provoquem travamento, arraste ou deslocamento das paredes do labirinto durante a execução dos percursos.                                                                   |      Must      | -                | -                        |
|  RNF20 | Precisão dimensional de fabricação            | As dimensões finais da estrutura devem permanecer dentro das tolerâncias definidas no projeto, garantindo que variações decorrentes do processo de fabricação não façam o micromouse ultrapassar os limites dimensionais estabelecidos.    |      Must      | -                | -                        |
|  RNF21 | Segurança estrutural                          | A estrutura não deve apresentar pontas, arestas ou elementos expostos que possam causar danos aos componentes, ao labirinto ou comprometer a operação do micromouse.                                                                       |      Must      | -                | -                        |
|  RNF22 | Durabilidade da estrutura                     | A estrutura deve manter suas características mecânicas e dimensões durante os testes e execuções previstas no projeto, sem apresentar desgaste ou deformação que comprometa o funcionamento do micromouse.                                 |     Should     | -                | -                        |
|  RNF23 | Facilidade de montagem e desmontagem          | A estrutura deve ser projetada de forma que sua montagem, desmontagem e manutenção possam ser realizadas utilizando os recursos e ferramentas disponíveis para a equipe, sem procedimentos excessivamente complexos.                       |     Should     | -                | -                        |
|  RNF24 | Aproveitamento do espaço interno              | A estrutura deve utilizar de forma eficiente o espaço disponível no chassi, permitindo a acomodação dos componentes necessários sem comprometer a circulação, manutenção, ventilação ou funcionamento dos demais subsistemas.              |     Should     | -                | -                        |
|  RNF25 | Compatibilidade com os materiais disponíveis  | Os materiais utilizados na fabricação da estrutura devem ser compatíveis com os processos de fabricação, ferramentas, orçamento e recursos disponíveis para o desenvolvimento do projeto.                                                  |      Must      | -                | -                        |


**Software**

| ID        | Nome do Requisito                       | Descrição                                                                                                                                                                                                                     | Prioridade | Responsáveis | Link para projetos do Github |
| --------- | --------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------- | ------------ | ---------------------------- |
| **RNF01** | Tempo máximo de execução                | O sistema do **Rato Cego** deve permitir que cada tentativa de resolução do labirinto seja realizada dentro do limite máximo de **10 minutos** estabelecido para o desafio.                                                   | Deve       | -            | -                            |
| **RNF02** | Atualização da telemetria               | O sistema web deve receber, processar e atualizar em tempo real os dados de telemetria transmitidos pelo **Rato Cego** durante o percurso.                                                                                    | Deve       | -            | -                            |
| **RNF03** | Integridade da telemetria               | O sistema deve garantir que os dados de telemetria recebidos e apresentados correspondam aos dados transmitidos pelo **Rato Cego**, sem alteração indevida das informações.                                                   | Deve       | -            | -                            |
| **RNF04** | Persistência dos dados                  | Os dados referentes às execuções concluídas devem permanecer armazenados no banco de dados e disponíveis para consultas posteriores.                                                                                          | Deve       | -            | -                            |
| **RNF05** | Integridade dos dados armazenados       | O sistema deve preservar a associação entre os dados de cada execução e o respectivo labirinto, evitando que informações de execuções distintas sejam incorretamente relacionadas.                                            | Deve       | -            | -                            |
| **RNF06** | Compatibilidade com os labirintos       | O software de navegação do **Rato Cego** deve ser compatível com os três labirintos definidos para o projeto: **4×4, 8×4 e 12×4 células**, sendo cada célula de **18 cm de lado**.                                            | Deve       | -            | -                            |
| **RNF07** | Compatibilidade com o hardware          | O software embarcado deve ser compatível com os componentes eletrônicos utilizados no **Rato Cego** e necessários às funções de sensoriamento, navegação e comunicação.                                                       | Deve       | -            | -                            |
| **RNF08** | Compatibilidade com o microcontrolador  | O software embarcado do **Rato Cego** deve ser executável no microcontrolador **ESP32** utilizado no protótipo.                                                                                                               | Deve       | -            | -                            |
| **RNF09** | Adaptação da representação do labirinto | A interface web deveria adaptar a representação visual do mapeamento às dimensões dos três tipos de labirinto utilizados pelo **Rato Cego**, mantendo todas as células e o trajeto visíveis.                                  | Deveria    | -            | -                            |
| **RNF10** | Legibilidade da telemetria              | A interface web deve apresentar separadamente os seis dados obrigatórios de telemetria: tipo do labirinto, trajeto percorrido, consumo de bateria, velocidade média, tempo de conclusão e situação de cumprimento do desafio. | Deve       | -            | -                            |
| **RNF11** | Responsividade da interface             | A interface web deveria adaptar sua disposição a diferentes dimensões de tela sem provocar sobreposição ou corte das informações obrigatórias de telemetria.                                                                  | Deveria    | -            | -                            |
| **RNF12** | Restrição de recursos                   | A solução de software deve utilizar tecnologias compatíveis com os recursos técnicos, materiais e financeiros disponíveis para o desenvolvimento do projeto.                                                                  | Deve       | -            | -                            |

