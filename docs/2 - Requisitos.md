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

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |

**Software**

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |

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

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |

**Software**

| **ID** | **Nome do Requisito** | **Descrição** | **Prioridade** | **Responsáveis** | **Link Github Projects** |
|:------:|------------------------|---------------|:--------------:|------------------|--------------------------|
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
|  |  |  |  | - | - |
