# Requisitos

> Documento que descreve as funcionalidades, restrições e critérios técnicos que orientam o desenvolvimento e a validação do micromouse Rato Cego.

## Visão Geral dos Requisitos

Este documento apresenta os requisitos funcionais (RF) e não funcionais (RNF) do projeto **Rato Cego**, organizados de acordo com os épicos definidos para o desenvolvimento do Micromouse.

Os requisitos servem como base para o planejamento, desenvolvimento, integração e validação do sistema.

---

# Requisitos Funcionais

## ÉPICO 1 — Estrutura do Micromouse

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rf1"></a> **[RF1](Projeto%20conceitual%20de%20software.md#hu1)** | **Proteção dos componentes internos** — A estrutura deve proteger os componentes eletrônicos, sensores, motores e demais módulos contra choques, colisões e impactos durante a operação. | Must | Heitor963 | [#73](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/73) |
| <a id="rf2"></a> **[RF2](Projeto%20conceitual%20de%20software.md#hu2)** | **Acesso aos componentes internos** — A estrutura deve permitir acesso aos componentes para inspeção, manutenção, substituição e ajustes sem exigir a desmontagem completa do chassi. | Should | Heitor963 | [#72](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/72) |
| <a id="rf3"></a> **[RF3](Projeto%20conceitual%20de%20software.md#hu3)** | **Fixação dos subsistemas** — A estrutura deve possuir pontos de fixação adequados para os subsistemas de hardware, alimentação e controle, evitando deslocamentos durante a operação. | Must | Heitor963 | [#71](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/71) |
| <a id="rf4"></a> **[RF4](Projeto%20conceitual%20de%20software.md#hu4)** | **Modularidade estrutural** — A estrutura deve permitir a substituição ou atualização de módulos sem exigir a reconstrução completa do chassi. | Should | Heitor963 | [#69](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/69) |
| <a id="rf5"></a> **[RF5](Projeto%20conceitual%20de%20software.md#hu5)** | **Fixação e alinhamento do sistema de locomoção** — Os motores, rodas e demais elementos de tração devem ser fixados de forma a manter seu posicionamento e alinhamento durante a operação. | Must | Heitor963 | [#68](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/68) |
| <a id="rf6"></a> **[RF6](Projeto%20conceitual%20de%20software.md#hu6)** | **Acomodação e posicionamento dos sensores** — A estrutura deve possuir espaços e pontos de fixação adequados para os sensores, mantendo sua orientação e evitando obstruções. | Must | Heitor963 | [#67](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/67) |
| <a id="rf7"></a> **[RF7](Projeto%20conceitual%20de%20software.md#hu7)** | **Organização e proteção do cabeamento** — Os cabos devem ser organizados e protegidos de forma a evitar interferência com rodas, motores, sensores e demais componentes móveis. | Must | Heitor963 | [#66](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/66) |
| <a id="rf8"></a> **[RF8](Projeto%20conceitual%20de%20software.md#hu8)** | **Acesso aos elementos de operação** — Os elementos externos de operação, como interruptores, conectores e interfaces, devem permanecer acessíveis durante a utilização do Micromouse. | Should | Heitor963 | [#64](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/64) |

---

## ÉPICO 2 — Hardware e Sensoriamento

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rf9"></a> **[RF9](Projeto%20conceitual%20de%20software.md#hu9)** | **Reconhecimento Espacial e Detecção de Obstáculos** — O Micromouse deve realizar amostragem contínua do ambiente por meio dos sensores de proximidade, identificando paredes de aproximadamente 5 cm de altura e aberturas nas direções frontal e lateral. | Must | Maria-Laura-Regis | [#53](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/53) |
| <a id="rf10"></a> **[RF10](Projeto%20conceitual%20de%20software.md#hu10)** | **Processamento Embarcado Autônomo** — O microcontrolador deve executar localmente a leitura dos sensores, a lógica de controle e o armazenamento das informações necessárias à navegação, sem depender de processamento externo. | Must | Vini-Araujoo | [#54](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/54) |
| <a id="rf11"></a> **[RF11](Projeto%20conceitual%20de%20software.md#hu11)** | **Acionamento e Modulação de Potência Motriz** — O sistema deve converter os comandos lógicos em acionamento elétrico reversível dos motores de tração, permitindo controle contínuo da velocidade por PWM. | Must | Maria-Laura-Regis | [#55](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/55) |
| <a id="rf12"></a> **[RF12](Projeto%20conceitual%20de%20software.md#hu12)** | **Manutenção de Trajetória Centralizada** — O sistema deve ajustar diferencialmente o acionamento dos motores para manter o Micromouse em trajetória reta e centralizada entre as paredes do labirinto. | Must | Vini-Araujoo | [#56](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/56) |
| <a id="rf13"></a> **[RF13](Projeto%20conceitual%20de%20software.md#hu13)** | **Execução de Curvas e Manobras de Rotação** — O sistema deve permitir a execução de curvas de 90° e 180° por meio do acionamento diferencial dos motores, respeitando as dimensões das células de 18 × 18 cm. | Must | Maria-Laura-Regis | [#57](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/57) |
| <a id="rf14"></a> **[RF14](Projeto%20conceitual%20de%20software.md#hu14)** | **Transmissão Sem Fio de Telemetria** — O Micromouse deve possuir um meio de comunicação sem fio capaz de transmitir os dados necessários à telemetria durante a execução. | Must | Vini-Araujoo | [#58](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/58) |
| <a id="rf15"></a> **[RF15](Projeto%20conceitual%20de%20software.md#hu15)** | **Regulação e Distribuição de Energia** — O sistema deve receber energia da fonte recarregável e distribuir tensões reguladas e estáveis aos circuitos de controle, sensores e atuadores. | Must | Maria-Laura-Regis | [#59](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/59) |

---

## ÉPICO 3 — Alimentação e Energia

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rf16"></a> **[RF16](Projeto%20conceitual%20de%20software.md#hu16)** | **Telemetria energética em tempo real** — Durante cada execução, o sistema deve coletar e disponibilizar dados sobre o consumo e a condição da bateria, associando essas informações à execução correspondente. | Must | gabemagioli | [#1](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/1) |
| <a id="rf17"></a> **[RF17](Projeto%20conceitual%20de%20software.md#hu17)** | **Isolamento manual da alimentação** — O circuito de alimentação deve possuir um interruptor físico acessível capaz de desconectar a bateria dos subsistemas durante transporte, montagem ou manutenção. | Must | RafaelWasconcelos | [#2](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/2) |
| <a id="rf18"></a> **[RF18](Projeto%20conceitual%20de%20software.md#hu18)** | **Aviso web de bateria baixa** — O firmware deve monitorar periodicamente a tensão da bateria e, ao atingir o nível crítico definido, enviar um aviso à interface web durante a execução. | Must | pedro271011 | [#3](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/3) |
| <a id="rf19"></a> **[RF19](Projeto%20conceitual%20de%20software.md#hu19)** | **Bateria removível e reinstalável** — A bateria deve poder ser removida e reinstalada sem exigir a desmontagem completa do chassi. | Could | gabemagioli | [#4](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/4) |

---

## ÉPICO 4 — Navegação e Controle

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rf20"></a> **[RF20](Projeto%20conceitual%20de%20software.md#hu20)** | **Identificação de paredes** — O software deve identificar a presença de paredes a partir dos dados obtidos pelos sensores. | Must | OliveiraThiago14 | [#21](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/21) |
| <a id="rf21"></a> **[RF21](Projeto%20conceitual%20de%20software.md#hu21)** | **Localização no labirinto** — O sistema deve acompanhar a localização do Micromouse dentro do labirinto durante a execução. | Must | OliveiraThiago14 | [#22](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/22) |
| <a id="rf22"></a> **[RF22](Projeto%20conceitual%20de%20software.md#hu22)** | **Determinação do percurso** — O sistema deve determinar autonomamente os movimentos necessários com base nas informações obtidas durante a execução. | Must | OliveiraThiago14 | [#23](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/23) |
| <a id="rf23"></a> **[RF23](Projeto%20conceitual%20de%20software.md#hu23)** | **Navegação autônoma** — O Micromouse deve realizar a navegação no labirinto sem intervenção humana durante a execução. | Must | OliveiraThiago14 | [#24](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/24) |
| <a id="rf24"></a> **[RF24](Projeto%20conceitual%20de%20software.md#hu24)** | **Identificação do objetivo** — O sistema deve identificar quando o Micromouse alcançar a região definida como objetivo do labirinto. | Must | felixlaryssa | [#25](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/25) |
| <a id="rf25"></a> **[RF25](Projeto%20conceitual%20de%20software.md#hu25)** | **Registro do trajeto** — O sistema deve registrar o trajeto percorrido pelo Micromouse durante cada execução. | Must | felixlaryssa | [#26](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/26) |
| <a id="rf26"></a> **[RF26](Projeto%20conceitual%20de%20software.md#hu26)** | **Seleção do tipo de labirinto** — O sistema deve permitir ao operador selecionar o tipo de labirinto antes do início da execução. | Must | MariaEduarda-jg | [#28](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/28) |
| <a id="rf27"></a> **[RF27](Projeto%20conceitual%20de%20software.md#hu27)** | **Preparação da execução** — O sistema deve verificar as condições necessárias para iniciar uma execução, incluindo a seleção do labirinto e a disponibilidade dos recursos necessários. | Must | gustavuh7 | [#151](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/151) |
| <a id="rf28"></a> **[RF28](Projeto%20conceitual%20de%20software.md#hu28)** | **Início do percurso** — O sistema deve permitir ao operador iniciar o percurso após a preparação da execução e receber a confirmação `run.started` em até **5 segundos** após o envio do comando. Se não houver confirmação nesse prazo, a tentativa deve ser encerrada como falha de início. | Must | Maria-Laura-Regis | [#152](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/152) |
| <a id="rf29"></a> **[RF29](Projeto%20conceitual%20de%20software.md#hu29)** | **Interrupção do percurso** — O sistema deve permitir ao operador solicitar a interrupção de uma execução em andamento pela interface web e receber a confirmação `run.interrupted` em até **2 segundos** após o envio do comando. Sem confirmação nesse prazo, deve informar que a parada não foi confirmada e manter o estado pendente. | Must | Vini-Araujoo | [#153](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/153) |
| <a id="rf30"></a> **[RF30](Projeto%20conceitual%20de%20software.md#hu30)** | **Reinício de execução** — O sistema deve permitir iniciar uma nova execução após a conclusão ou interrupção de uma execução anterior. | Should | Maria-Laura-Regis | [#154](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/154) |
| <a id="rf31"></a> **[RF31](Projeto%20conceitual%20de%20software.md#hu31)** | **Gerenciamento do estado da execução** — O sistema deve controlar e disponibilizar os estados da execução, incluindo, no mínimo, **aguardando, em execução, concluída, interrompida e encerrada por falha ou timeout**. | Must | Vini-Araujoo | [#155](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/155) |
| <a id="rf32"></a> **[RF32](Projeto%20conceitual%20de%20software.md#hu32)** | **Encerramento automático da execução** — O sistema deve encerrar automaticamente a execução quando o objetivo for alcançado, o tempo máximo for excedido ou ocorrer uma falha que impeça a continuidade segura. | Must | CavalcantiG | [#156](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/156) |
| <a id="rf33"></a> **[RF33](Projeto%20conceitual%20de%20software.md#hu33)** | **Parada segura** — Quando uma execução for interrompida ou ocorrer uma condição de falha que exija parada, o sistema deve interromper ou limitar o acionamento dos motores de forma segura. | Must | gustavuh7 | [#157](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/157) |
| <a id="rf34"></a> **[RF34](Projeto%20conceitual%20de%20software.md#hu34)** | **Medição de deslocamento** — O sistema deve utilizar os encoders dos motores para obter informações de deslocamento e velocidade do Micromouse. | Must | Vini-Araujoo | [#158](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/158) |

---

## ÉPICO 5 — Sistema Web e Telemetria

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rf35"></a> **[RF35](Projeto%20conceitual%20de%20software.md#hu35)** | **Recepção e disponibilização da telemetria** — O sistema web deve receber os dados de telemetria enviados pelo Micromouse e disponibilizá-los para acompanhamento da execução. | Must | MariaEduarda-jg | [#159](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/159) |
| <a id="rf36"></a> **[RF36](Projeto%20conceitual%20de%20software.md#hu36)** | **Visualização do trajeto** — A interface web deve apresentar e atualizar o trajeto percorrido pelo Micromouse durante a execução. | Must | morettipdr | [#29](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/29) |
| <a id="rf37"></a> **[RF37](Projeto%20conceitual%20de%20software.md#hu37)** | **Monitoramento da bateria** — A interface web deve apresentar a condição e o consumo da bateria durante a execução, utilizando os dados fornecidos pela telemetria energética. | Must | felixlaryssa | [#30](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/30) |
| <a id="rf38"></a> **[RF38](Projeto%20conceitual%20de%20software.md#hu38)** | **Monitoramento do tempo** — O sistema deve contabilizar o tempo de execução e apresentá-lo na interface web durante o percurso. | Must | felixlaryssa | [#31](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/31) |
| <a id="rf39"></a> **[RF39](Projeto%20conceitual%20de%20software.md#hu39)** | **Exibição da velocidade média** — A interface web deve calcular e apresentar a velocidade média da execução. | Must | MariaEduarda-jg | [#32](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/32) |
| <a id="rf40"></a> **[RF40](Projeto%20conceitual%20de%20software.md#hu40)** | **Resultado do desafio** — A interface web deve informar se o Micromouse concluiu ou não o desafio ao final da execução. | Must | felixlaryssa | [#33](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/33) |
| <a id="rf41"></a> **[RF41](Projeto%20conceitual%20de%20software.md#hu41)** | **Estabelecimento da conexão WebSocket** — O sistema deve estabelecer uma conexão WebSocket entre a aplicação web e o serviço responsável pela comunicação em tempo real. | Must | MariaEduarda-jg | [#160](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/160) |
| <a id="rf42"></a> **[RF42](Projeto%20conceitual%20de%20software.md#hu42)** | **Detecção de perda de conexão WebSocket** — O sistema deve detectar a perda ou indisponibilidade da conexão WebSocket durante uma execução usando o heartbeat definido no RNF31. A interface deve distinguir conexão perdida de telemetria desatualizada. | Must | morettipdr | [#161](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/161) |
| <a id="rf43"></a> **[RF43](Projeto%20conceitual%20de%20software.md#hu43)** | **Reconexão WebSocket** — Após uma perda de conexão, o sistema deve tentar reconectar automaticamente segundo os intervalos definidos no RNF32 enquanto a página de monitoramento permanecer aberta. | Should | LeticiaMonteiroo | [#162](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/162) |
| <a id="rf44"></a> **[RF44](Projeto%20conceitual%20de%20software.md#hu44)** | **Sinalização de indisponibilidade** — A interface web deve informar ao operador quando uma comunicação necessária para o monitoramento estiver indisponível. | Must | felixlaryssa | [#163](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/163) |
| <a id="rf45"></a> **[RF45](Projeto%20conceitual%20de%20software.md#hu45)** | **Comunicação MQTT** — O sistema deve utilizar MQTT para transmissão e recepção das mensagens definidas pela arquitetura de comunicação do projeto. | Must | OliveiraThiago14 | [#164](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/164) |
| <a id="rf46"></a> **[RF46](Projeto%20conceitual%20de%20software.md#hu46)** | **Reconexão MQTT** — Após uma perda de conexão, o cliente MQTT deve tentar reconectar automaticamente segundo os intervalos definidos no RNF34 até restabelecer a conexão ou ser encerrado. | Must | felixlaryssa | [#165](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/165) |
| <a id="rf47"></a> **[RF47](Projeto%20conceitual%20de%20software.md#hu47)** | **Tratamento de mensagens MQTT** — O sistema deve receber, validar e processar as mensagens MQTT de acordo com o formato definido para a comunicação, descartando ou sinalizando mensagens inválidas. | Must | LeticiaMonteiroo | [#166](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/166) |

---

## ÉPICO 6 — Banco de Dados e Histórico

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rf48"></a> **[RF48](Projeto%20conceitual%20de%20software.md#hu48)** | **Armazenamento da execução** — Após cada execução, o sistema deve armazenar os dados coletados durante o percurso no banco de dados. | Must | morettipdr | [#34](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/34) |
| <a id="rf49"></a> **[RF49](Projeto%20conceitual%20de%20software.md#hu49)** | **Associação ao labirinto** — Os dados armazenados devem ser associados ao tipo de labirinto correspondente à execução. | Must | morettipdr | [#35](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/35) |
| <a id="rf50"></a> **[RF50](Projeto%20conceitual%20de%20software.md#hu50)** | **Consulta por labirinto** — A interface web deve permitir consultar os dados das execuções realizadas em um determinado labirinto. | Must | morettipdr | [#36](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/36) |
| <a id="rf51"></a> **[RF51](Projeto%20conceitual%20de%20software.md#hu51)** | **Consulta geral** — A interface web deve permitir consultar conjuntamente os dados armazenados de diferentes labirintos e execuções. | Must | MariaEduarda-jg | [#37](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/37) |
| <a id="rf52"></a> **[RF52](Projeto%20conceitual%20de%20software.md#hu52)** | **Registro de falhas da execução** — O sistema deve registrar eventos relevantes que impeçam ou interrompam uma execução, incluindo falhas de comunicação, timeout, interrupções e condições de falha detectadas. | Must | morettipdr | [#167](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/167) |

---

## ÉPICO 7 — Integração e Validação

### Requisitos Funcionais

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rf53"></a> **[RF53](Projeto%20conceitual%20de%20software.md#hu53)** | **Correção da movimentação** — O sistema deve utilizar as informações de deslocamento obtidas pelos encoders para auxiliar no controle e posicionamento do Micromouse durante a navegação. | Must | gustavuh7 | [#168](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/168) |
| <a id="rf54"></a> **[RF54](Projeto%20conceitual%20de%20software.md#hu54)** | **Calibração dos sensores** — O sistema deve permitir a calibração dos sensores antes da operação, utilizando procedimentos definidos pelo projeto. | Should | gustavuh7 | [#169](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/169) |
| <a id="rf55"></a> **[RF55](Projeto%20conceitual%20de%20software.md#hu55)** | **Sinalização do ciclo de recarga** — Durante o processo de recarga, o Micromouse poderá indicar visualmente o estado do ciclo de carregamento. | Could | felixlaryssa | [#40](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/40) |

---

# Requisitos Não Funcionais

## ÉPICO 1 — Estrutura do Micromouse

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rnf1"></a> **RNF1** | **Limite dimensional do chassi** — O Micromouse deve possuir dimensões máximas de **16,5 cm × 16,5 cm**, considerando qualquer estado operacional permitido pelo projeto. | Must | Maria-Laura-Regis | [#61](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/61) |
| <a id="rnf2"></a> **RNF2** | **Compatibilidade com as dimensões do labirinto** — A geometria do Micromouse deve permitir movimentação livre nas células de **18 cm**, sem pontos salientes ou interferências que possam causar travamento ou danos. | Must | Heitor963 | [#75](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/75) |
| <a id="rnf3"></a> **RNF3** | **Massa estrutural** — A massa da estrutura deve permanecer dentro do limite definido pelo projeto, sem comprometer aceleração, frenagem, estabilidade ou autonomia. | Must | Heitor963 | [#76](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/76) |
| <a id="rnf4"></a> **RNF4** | **Resistência mecânica** — A estrutura deve suportar movimentação, aceleração, frenagem e impactos previstos durante os testes sem sofrer deformações que comprometam seu funcionamento. | Must | Heitor963 | [#77](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/77) |
| <a id="rnf5"></a> **RNF5** | **Rigidez estrutural** — A estrutura deve manter sua geometria e o posicionamento dos componentes durante a operação, evitando deformações ou folgas que prejudiquem a navegação. | Must | Heitor963 | [#78](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/78) |
| <a id="rnf6"></a> **RNF6** | **Estabilidade estrutural** — O Micromouse deve permanecer estável durante acelerações, frenagens, curvas e mudanças de direção. | Must | Heitor963 | [#79](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/79) |
| <a id="rnf7"></a> **RNF7** | **Distribuição de massa** — A distribuição de massa deve evitar desequilíbrios que prejudiquem a locomoção, estabilidade ou precisão das manobras. | Should | Heitor963 | [#80](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/80) |
| <a id="rnf8"></a> **RNF8** | **Compatibilidade entre subsistemas** — As dimensões, espaços e pontos de fixação da estrutura devem ser compatíveis com os componentes de hardware, alimentação, sensores e demais subsistemas. | Must | Heitor963 | [#81](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/81) |
| <a id="rnf9"></a> **RNF9** | **Precisão dimensional de fabricação** — As dimensões finais da estrutura devem permanecer dentro das tolerâncias de fabricação definidas no projeto. | Must | Heitor963 | [#82](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/82) |
| <a id="rnf10"></a> **RNF10** | **Segurança estrutural** — A estrutura não deve apresentar pontas, arestas ou elementos expostos que possam causar danos aos componentes ou comprometer a operação. | Must | Heitor963 | [#83](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/83) |
| <a id="rnf11"></a> **RNF11** | **Durabilidade da estrutura** — A estrutura deve manter suas características mecânicas e dimensões dentro das tolerâncias estabelecidas após os testes e execuções previstos. | Should | Heitor963 | [#84](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/84) |
| <a id="rnf12"></a> **RNF12** | **Facilidade de montagem e desmontagem** — A estrutura deve permitir montagem, desmontagem e manutenção utilizando as ferramentas disponíveis, sem procedimentos excessivamente complexos. | Should | Heitor963 | [#85](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/85) |
| <a id="rnf13"></a> **RNF13** | **Aproveitamento do espaço interno** — O espaço interno deve ser utilizado de forma eficiente, sem comprometer circulação, manutenção, ventilação ou integração dos subsistemas. | Should | Heitor963 | [#86](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/86) |
| <a id="rnf14"></a> **RNF14** | **Compatibilidade com os materiais disponíveis** — Os materiais utilizados devem ser compatíveis com os processos de fabricação, ferramentas, orçamento e recursos disponíveis no projeto. | Must | Heitor963 | [#87](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/87) |

---

## ÉPICO 2 — Hardware e Sensoriamento

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rnf15"></a> **RNF15** | **Proteção e margem de segurança elétrica** — O sistema deve possuir proteção contra inversão de polaridade, e os condutores devem suportar pelo menos **30% acima da corrente máxima prevista** para o circuito correspondente. | Must | Maria-Laura-Regis | [#63](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/63) |
| <a id="rnf16"></a> **RNF16** | **Estabilidade de tensão lógica** — Os reguladores devem manter as linhas de **3,3 V e 5 V dentro de ±5%** de seus valores nominais durante condições de operação, incluindo a partida dos motores. | Must | Vini-Araujoo | [#65](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/65) |
| <a id="rnf17"></a> **RNF17** | **Latência da malha física de resposta** — O intervalo entre a leitura de um sensor e o acionamento efetivo correspondente dos motores deve ser de, no máximo, **50 ms**. As leituras de navegação e o ciclo de controle devem ocorrer a pelo menos **20 Hz**. | Must | Maria-Laura-Regis | [#70](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/70) |

---

## ÉPICO 3 — Alimentação e Energia

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rnf18"></a> **RNF18** | **Autonomia operacional** — Com sensores, controle, motores e telemetria ativos, o Micromouse deve operar por pelo menos **30 minutos sem recarga ou substituição da bateria**. | Must | RafaelWasconcelos | [#5](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/5) |
| <a id="rnf19"></a> **RNF19** | **Rendimento da conversão de energia** — Os conversores e reguladores devem apresentar eficiência mínima de **85% em carga nominal**. | Should | pedro271011 | [#6](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/6) |
| <a id="rnf20"></a> **RNF20** | **Consumo em inatividade** — Com o sistema energizado, mas sem executar um percurso, o consumo total deve ser de no máximo **50 mA**, considerando a desativação de periféricos desnecessários. | Should | gabemagioli | [#7](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/7) |
| <a id="rnf21"></a> **RNF21** | **Aviso preventivo de descarga** — O sistema deve emitir o aviso de bateria baixa quando qualquer célula atingir **3,3 V**, antes de atingir uma faixa considerada prejudicial à bateria. | Must | RafaelWasconcelos | [#8](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/8) |
| <a id="rnf22"></a> **RNF22** | **Restrição de massa do conjunto energético** — Os componentes destinados à alimentação devem representar no máximo **25% da massa final do Micromouse**. | Must | pedro271011 | [#9](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/9) |
| <a id="rnf23"></a> **RNF23** | **Integridade da alimentação dos sensores** — Durante acelerações e partidas dos motores, a alimentação dos sensores deve permanecer entre **95% e 105% da tensão nominal**. | Must | gabemagioli | [#10](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/10) |
| <a id="rnf24"></a> **RNF24** | **Proteção contra falhas elétricas** — O sistema de alimentação deve interromper ou limitar a corrente em condições de sobrecorrente ou curto-circuito. | Must | RafaelWasconcelos | [#11](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/11) |
| <a id="rnf25"></a> **RNF25** | **Confiabilidade da telemetria energética** — Após a calibração, as leituras de tensão e consumo devem apresentar erro máximo de **5% em relação ao instrumento de referência**. | Should | gabemagioli | [#13](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/13) |
| <a id="rnf26"></a> **RNF26** | **Compatibilidade de tensão dos subsistemas** — A tensão nominal e a faixa de descarga da bateria devem ser compatíveis com todos os subsistemas, e os reguladores devem suportar as tensões e correntes exigidas. | Must | pedro271011 | [#15](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/15) |

---

## ÉPICO 4 — Navegação e Controle

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rnf27"></a> **RNF27** | **Tempo máximo de execução** — Cada tentativa de resolução do labirinto deve ser encerrada em no máximo **10 minutos**. | Must | felixlaryssa | [#41](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/41) |
| <a id="rnf28"></a> **RNF28** | **Compatibilidade com os labirintos** — O software de navegação deve ser compatível com os labirintos de **4 × 4, 8 × 4 e 12 × 4 células**, considerando células de **18 cm**. | Must | OliveiraThiago14 | [#46](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/46) |
| <a id="rnf29"></a> **RNF29** | **Compatibilidade com o hardware** — O software embarcado deve ser compatível com os componentes eletrônicos utilizados para sensoriamento, navegação e comunicação. | Must | OliveiraThiago14 | [#47](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/47) |
| <a id="rnf30"></a> **RNF30** | **Compatibilidade com o microcontrolador** — O software embarcado deve ser executável no **ESP32** definido para o projeto. | Must | felixlaryssa | [#48](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/48) |
| <a id="rnf31"></a> **RNF31** | **Timeout da conexão WebSocket** — Cliente e servidor devem trocar heartbeat de aplicação a cada **2 segundos** e considerar a conexão indisponível após **6 segundos** sem heartbeat válido. O sistema deve sinalizar a indisponibilidade ao operador. | Must | gustavuh7 | [#170](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/170) |
| <a id="rnf32"></a> **RNF32** | **Reconexão WebSocket** — Após a perda de conexão, o frontend deve tentar reconectar após **1, 2, 4, 8 e 16 segundos**; depois, deve repetir a tentativa a cada **30 segundos** enquanto a página de monitoramento permanecer aberta. | Should | CavalcantiG | [#171](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/171) |
| <a id="rnf33"></a> **RNF33** | **Timeout e Keep-Alive MQTT** — O cliente MQTT deve usar Keep Alive de **10 segundos**. A conexão deve ser considerada perdida após **15 segundos** sem pacote de controle válido. | Must | Maria-Laura-Regis | [#172](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/172) |
| <a id="rnf34"></a> **RNF34** | **Reconexão MQTT** — Após a perda de conexão, o cliente MQTT deve tentar reconectar após **1, 2, 4, 8 e 16 segundos**; depois, deve repetir a tentativa a cada **30 segundos** até restabelecer a conexão ou ser encerrado. | Must | Vini-Araujoo | [#173](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/173) |
| <a id="rnf35"></a> **RNF35** | **Integridade das mensagens** — As mensagens trocadas pelo sistema devem seguir o formato definido pela arquitetura de comunicação. Mensagens inválidas, incompletas ou incompatíveis devem ser identificadas e descartadas ou sinalizadas sem provocar comportamento inesperado. | Must | CavalcantiG | [#174](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/174) |
| <a id="rnf36"></a> **RNF36** | **Latência da telemetria** — Na rede local do projeto, pelo menos **95%** das amostras devem ficar visíveis na interface em até **500 ms** após a geração no sistema embarcado, e **99%** em até **1 segundo**. A medição deve usar relógios sincronizados no ensaio. | Must | CavalcantiG | [#175](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/175) |
| <a id="rnf37"></a> **RNF37** | **Precisão de sensoriamento** — Os sensores utilizados para navegação devem apresentar precisão suficiente para identificar paredes, obstáculos e aberturas dentro das tolerâncias estabelecidas nos testes do projeto. | Should | Maria-Laura-Regis | [#176](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/176) |

---

## ÉPICO 5 — Sistema Web e Telemetria

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rnf38"></a> **RNF38** | **Atualização da telemetria** — Durante uma execução, o Micromouse deve publicar uma amostra de telemetria a cada **100 ms (10 Hz)**, e a interface deve aplicar cada amostra válida assim que recebida. Se não receber amostra por **1 segundo**, deve indicar telemetria desatualizada, mesmo que o WebSocket continue conectado. | Must | morettipdr | [#42](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/42) |
| <a id="rnf39"></a> **RNF39** | **Integridade da telemetria** — Os dados apresentados na interface web devem corresponder aos dados transmitidos pelo Micromouse, sem alterações indevidas durante a transmissão ou processamento. | Must | morettipdr | [#43](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/43) |
| <a id="rnf40"></a> **RNF40** | **Adaptação da representação do labirinto** — A representação visual do labirinto deve se adaptar aos três tamanhos previstos, mantendo todas as células e o trajeto visíveis. | Should | MariaEduarda-jg | [#49](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/49) |
| <a id="rnf41"></a> **RNF41** | **Legibilidade da telemetria** — A interface web deve apresentar separadamente, no mínimo, os seis dados obrigatórios: tipo de labirinto, trajeto, consumo da bateria, velocidade média, tempo de execução e resultado do desafio. | Must | morettipdr | [#50](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/50) |
| <a id="rnf42"></a> **RNF42** | **Responsividade da interface** — A interface web deve se adaptar aos tamanhos de tela utilizados pelo projeto sem sobreposição ou ocultação dos dados obrigatórios. | Should | MariaEduarda-jg | [#51](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/51) |
| <a id="rnf43"></a> **RNF43** | **Restrição de recursos** — As tecnologias utilizadas no sistema devem ser compatíveis com os recursos técnicos, materiais e financeiros disponíveis para o projeto. | Must | felixlaryssa | [#52](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/52) |

---

## ÉPICO 6 — Banco de Dados e Histórico

| ID | Requisito | Prioridade | Responsável | Issue do requisito |
| --- | --- | --- | --- | --- |
| <a id="rnf44"></a> **RNF44** | **Persistência dos dados** — Os dados de uma execução concluída devem permanecer armazenados e disponíveis para consultas posteriores. | Must | morettipdr | [#44](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/44) |
| <a id="rnf45"></a> **RNF45** | **Integridade dos dados armazenados** — Os dados armazenados devem preservar corretamente a associação entre execução, labirinto, telemetria e resultado correspondente. | Must | morettipdr | [#45](https://github.com/fcte-pi1/2026_2_PI1_Grupo02_Bruno/issues/45) |

---

## ÉPICO 7 — Integração e Validação

A validação dos requisitos de integração será realizada por meio dos testes definidos nas respectivas Issues do projeto, considerando os requisitos funcionais e não funcionais relacionados aos subsistemas envolvidos.

---

# Histórico de Versões

| Versão | Descrição            | Autor              |    Data    | Revisor                                                 |
| :----: | -------------------- | ------------------ | :--------: | ------------------------------------------------------- |
|   1.0  | Criação do documento | Equipe de Software | 27/09/2026 | [Letícia Monteiro](https://github.com/LeticiaMonteiroo) |
