# Cronograma

## Subequipes

A divisão de responsabilidades segue as subequipes do TAP. Quando uma HU envolve mais de uma frente, a subequipe indicada lidera a entrega e a outra aparece como apoio.

| Subequipe | Integrantes | HUs sob responsabilidade | Qtde. |
|---|---|---|:---:|
| **Estruturas** | Heitor Santos Nobre, Henrique Brandão dos Santos, Pedro Augusto Ribeiro, Yasmim de Souza Santos, Pedro de Oliveira Calcado | HU01–HU08 | 8 |
| **Energia** | Gabriel Andrade Magioli, Rafael Silva Wasconcelos, Pedro Gustavo Nunes Silva | HU15–HU19, HU55 | 6 |
| **Hardware** | Gabriel Cavalcanti Monteiro, Maria Laura, Vinícius Araújo Oliveira, Gustavo Rodrigues de Noronha | HU09–HU14, HU27–HU30, HU32–HU34, HU53, HU54 | 15 |
| **Software** | Laryssa Felix Ribeiro Lopes, Leticia da Silva Monteiro, Maria Eduarda de Jezus Guimaraes, Pedro Augusto Moretti Moreira, Thiago Alencar | HU20–HU26, HU31, HU35–HU52 | 26 |

- **Hardware** cuida do firmware de baixo nível: sensores, motores, encoders, controle e o lado embarcado dos comandos de execução.
- **Software** cuida da lógica de navegação, da comunicação (MQTT e WebSocket), do backend, do frontend e do banco de dados.
- A carga de Software é maior. Para equilibrar, Hardware apoia a navegação (HU21–HU23) no Sprint 3.

## Marcos

| Marco | Data | Critério |
|---|---|---|
| M0: Planejamento | qua 30/09 (AP6) | Cronograma, orçamento e roadmap aprovados; HUs atribuídas |
| M1: Bancada | qua 07/10 (AP8) | Sensores, motores e energia validados em bancada; chassi v1 montado; software de ponta a ponta com simulador |
| M2: Movimento e comando | qua 14/10 (AP9) | Robô anda centralizado e faz curvas; a web inicia e interrompe a execução e recebe telemetria real |
| M3: Congelamento | qua 21/10 (AP11) | Todas as HUs Must concluídas; daqui em diante, só correções e testes |
| M4: Entrega | seg 26/10 (AP12) | Testes de estrutura, energia, hardware e software apresentados |

**Sprints:**

| Sprint | Período |
|---|---|
| S1 | 30/09–06/10 |
| S2 | 07/10–13/10 |
| S3 | 14/10–20/10 |
| S4 | 21/10–25/10 |

O feriado de 12/10 não conta como dia de trabalho.

**Legenda de status:**

| Status | Significado |
|---|---|
| Concluído | Tarefa entregue |
| Em andamento | Tarefa iniciada e ainda não entregue |
| Não iniciado | Tarefa ainda não começou |

---

## 1. Gestão e especificação (equipe completa)

| **ID** | **Fase** | **Entrega** | **Tarefa** | **Data de Início** | **Data de Fim** | **Responsável** | **Predecessor** | **% de Execução** | **Status** | **_Milestone_** |
|:------:|:------:|:--------:|------------|-----------------|-----------------|--------------------|-----------------------|:-----------------:|-------------|---------------|
| 1 | Iniciação | Planejamento | Gestão e especificação do projeto | 02/09/2026 | 02/10/2026 | Equipe completa | — | 90% | Em andamento | — |
| 1.1 | Iniciação | TAP | Termo de Abertura do Projeto | 02/09/2026 | 02/09/2026 | Equipe completa | — | 100% | Concluído | AP2 |
| 1.2 | Especificação | Requisitos | Levantamento de RFs e RNFs e criação das issues | 03/09/2026 | 14/09/2026 | Equipe completa | 1.1 | 100% | Concluído | AP3 |
| 1.3 | Especificação | EAP | Estrutura Analítica do Produto | 15/09/2026 | 16/09/2026 | Equipe completa | 1.2 | 100% | Concluído | AP4 |
| 1.4 | Especificação | Projeto conceitual | Projetos conceituais de estruturas, energia, hardware e software | 17/09/2026 | 28/09/2026 | Equipe completa | 1.3 | 100% | Concluído | AP5 |
| 1.5 | Especificação | Backlog | Histórias de usuário, épicos e critérios de aceitação (issues #88–#150) | 17/09/2026 | 28/09/2026 | Equipe completa | 1.2 | 100% | Concluído | AP5 |
| 1.6 | Planejamento | Cronograma e orçamento | Roadmap das HUs, cronograma e orçamento | 29/09/2026 | 30/09/2026 | Equipe completa | 1.4, 1.5 | 100% | Em andamento | M0 |
| 1.7 | Planejamento | GitHub Projects | Atribuir responsáveis e sprint às 55 HUs; criar milestones S1–S4 | 30/09/2026 | 02/10/2026 | Líderes das subequipes | 1.6 | 100% | Não iniciado | M0 |
| 1.8 | Planejamento | Compras | Compra dos componentes do orçamento e do filamento | 30/09/2026 | 02/10/2026 | Líderes das subequipes | 1.6 | 90% | Não iniciado | M0 |
| 1.9 | Planejamento | Decisões técnicas | Definir telemetria energética em tempo real e necessidade de regulador externo | 30/09/2026 | 02/10/2026 | Energia + Hardware | 1.4 | 0% | Não iniciado | M0 |

## 2. Subequipe de Estruturas

| **ID** | **Fase** | **Entrega** | **Tarefa** | **Data de Início** | **Data de Fim** | **Responsável** | **Predecessor** | **% de Execução** | **Status** | **_Milestone_** |
|:------:|:------:|:--------:|------------|-----------------|-----------------|--------------------|-----------------------|:-----------------:|-------------|---------------|
| 2 | Execução | Estrutura do Micromouse | Épico 1: chassi, fixações e labirinto de teste | 30/09/2026 | 25/10/2026 | Subequipe de Estruturas | 1.4 | 0% | Não iniciado | M4 |
| 2.1 | S1 | Chassi v1 | Ajustes finais do CAD do chassi e envio para impressão | 30/09/2026 | 02/10/2026 | Heitor Santos Nobre | 1.4 | 0% | Não iniciado | — |
| 2.2 | S1 | Chassi v1 | Impressão 3D do chassi v1 em PLA | 01/10/2026 | 04/10/2026 | Henrique Brandão dos Santos | 2.1, 1.8 | 0% | Não iniciado | — |
| 2.3 | S1 | HU03 | Fixação dos subsistemas (placa, ponte H, INA219, bateria) | 03/10/2026 | 06/10/2026 | Heitor Santos Nobre | 2.2 | 0% | Não iniciado | M1 |
| 2.4 | S1 | HU05 | Fixação e alinhamento de motores, rodas e roda boba | 03/10/2026 | 06/10/2026 | Henrique Brandão dos Santos | 2.2, 1.8 | 0% | Não iniciado | M1 |
| 2.5 | S1 | HU06 | Suporte e orientação dos 3 sensores IR | 03/10/2026 | 06/10/2026 | Pedro Augusto Ribeiro | 2.2 | 0% | Não iniciado | M1 |
| 2.6 | S1–S2 | Labirinto 4×4 | Fabricação do labirinto modular de teste (base e paredes) | 30/09/2026 | 09/10/2026 | Pedro de Oliveira Calcado | 1.4, 1.8 | 0% | Não iniciado | M2 |
| 2.7 | S2 | HU01 | Chassi v2: proteção dos componentes, limite de 16,5 × 16,5 cm, sem arestas expostas | 07/10/2026 | 13/10/2026 | Heitor Santos Nobre | 2.3, 2.4, 2.5 | 0% | Não iniciado | M2 |
| 2.8 | S2 | HU07 | Organização e proteção do cabeamento (apoio: Energia) | 07/10/2026 | 13/10/2026 | Yasmim de Souza Santos | 2.3, 4.1 | 0% | Não iniciado | M2 |
| 2.9 | S3 | HU02 | Acesso aos componentes internos (tampa ou abertura de manutenção) | 14/10/2026 | 20/10/2026 | Yasmim de Souza Santos | 2.7 | 0% | Não iniciado | M3 |
| 2.10 | S3 | HU04 | Modularidade estrutural | 14/10/2026 | 20/10/2026 | Pedro de Oliveira Calcado | 2.7 | 0% | Não iniciado | M3 |
| 2.11 | S3 | HU08 | Acesso externo à chave, USB e botões | 14/10/2026 | 20/10/2026 | Pedro Augusto Ribeiro | 2.7, 3.3 | 0% | Não iniciado | M3 |
| 2.12 | S4 | Testes de estrutura | Ensaios de RNF01–RNF14 (dimensões, massa, rigidez, estabilidade, impacto) | 21/10/2026 | 24/10/2026 | Henrique Brandão dos Santos + Heitor Santos Nobre | 2.7–2.11 | 0% | Não iniciado | M4 |
| 2.13 | S4 | Documentação | Preencher [Testes de estrutura](Testes%20de%20estrutura.md) com evidências | 22/10/2026 | 25/10/2026 | Pedro de Oliveira Calcado + Yasmim de Souza Santos | 2.12 | 0% | Não iniciado | M4 |

## 3. Subequipe de Energia

| **ID** | **Fase** | **Entrega** | **Tarefa** | **Data de Início** | **Data de Fim** | **Responsável** | **Predecessor** | **% de Execução** | **Status** | **_Milestone_** |
|:------:|:------:|:--------:|------------|-----------------|-----------------|--------------------|-----------------------|:-----------------:|-------------|---------------|
| 3 | Execução | Alimentação e energia | Épico 3 + HU15: regulação, isolamento, telemetria e avisos | 30/09/2026 | 25/10/2026 | Subequipe de Energia | 1.4 | 0% | Não iniciado | M4 |
| 3.1 | S1 | Decisões técnicas | Especificar o envio em tempo real das leituras do INA219 e o limiar crítico por célula | 30/09/2026 | 02/10/2026 | Gabriel Andrade Magioli | 1.9 | 0% | Não iniciado | — |
| 3.2 | S1 | HU15 | Regulação e distribuição: medir 5 V e 3,3 V sob carga, com motores e Wi-Fi ativos (apoio: Hardware) | 30/09/2026 | 06/10/2026 | Pedro Gustavo Nunes Silva | 1.8, 1.9 | 0% | Não iniciado | M1 |
| 3.3 | S1 | HU17 | Chave geral de isolamento instalada e testada | 30/09/2026 | 06/10/2026 | Rafael Silva Wasconcelos | 1.8 | 0% | Não iniciado | M1 |
| 3.4 | S2 | HU16 | Telemetria energética em tempo real (INA219 via I²C, integrada à telemetria) | 07/10/2026 | 13/10/2026 | Gabriel Andrade Magioli | 3.1, 3.2, 4.10 | 0% | Não iniciado | M2 |
| 3.5 | S3 | HU18 | Aviso web de bateria baixa: limiar no firmware e alerta na interface (apoio: Software) | 14/10/2026 | 20/10/2026 | Pedro Gustavo Nunes Silva | 3.4, 5.10 | 0% | Não iniciado | M3 |
| 3.6 | S3 | Proteção da bateria | Corte dos motores ao atingir 640 mAh consumidos (integra com HU33) | 14/10/2026 | 20/10/2026 | Rafael Silva Wasconcelos | 3.4, 4.9 | 0% | Não iniciado | M3 |
| 3.7 | S4 | HU19 (Could) | Bateria removível sem desmontar o chassi (apoio: Estruturas). Só se todas as Must estiverem prontas | 21/10/2026 | 23/10/2026 | Gabriel Andrade Magioli | 2.7, M3 | 0% | Não iniciado | M4 |
| 3.8 | S4 | HU55 (Could) | Sinalização visual do ciclo de recarga. Só se todas as Must estiverem prontas | 21/10/2026 | 23/10/2026 | Rafael Silva Wasconcelos | 3.3, M3 | 0% | Não iniciado | M4 |
| 3.9 | S4 | Testes de energia | Ensaios de RNF18–RNF26 (autonomia, consumo em repouso, rendimento, aviso de descarga) | 21/10/2026 | 24/10/2026 | Subequipe de Energia | 3.4–3.6 | 0% | Não iniciado | M4 |
| 3.10 | S4 | Documentação | Preencher [Testes de energia](Testes%20de%20energia.md) com medições | 22/10/2026 | 25/10/2026 | Pedro Gustavo Nunes Silva | 3.9 | 0% | Não iniciado | M4 |

## 4. Subequipe de Hardware

| **ID** | **Fase** | **Entrega** | **Tarefa** | **Data de Início** | **Data de Fim** | **Responsável** | **Predecessor** | **% de Execução** | **Status** | **_Milestone_** |
|:------:|:------:|:--------:|------------|-----------------|-----------------|--------------------|-----------------------|:-----------------:|-------------|---------------|
| 4 | Execução | Hardware e firmware de controle | Épico 2 + controle de movimento e comandos embarcados | 30/09/2026 | 25/10/2026 | Subequipe de Hardware | 1.4 | 0% | Não iniciado | M4 |
| 4.1 | S1 | Placa eletrônica | Montagem e solda da placa perfurada (ESP32, L298N, INA219, capacitores) | 30/09/2026 | 04/10/2026 | Gabriel Cavalcanti Monteiro | 1.8 | 0% | Não iniciado | — |
| 4.2 | S1 | HU10 | Firmware base no ESP32 (PlatformIO): laço de controle e módulos | 30/09/2026 | 06/10/2026 | Vinícius Araújo Oliveira | 1.4 | 0% | Não iniciado | M1 |
| 4.3 | S1 | HU09 | Leitura dos 3 sensores IR e curva distância × leitura | 30/09/2026 | 06/10/2026 | Maria Laura | 4.1 | 0% | Não iniciado | M1 |
| 4.4 | S1 | HU11 | Acionamento reversível dos motores com PWM pelo L298N | 30/09/2026 | 06/10/2026 | Gustavo Rodrigues de Noronha | 4.1 | 0% | Não iniciado | M1 |
| 4.5 | S2 | HU34 | Leitura dos encoders por interrupção: distância e velocidade de cada roda | 07/10/2026 | 13/10/2026 | Vinícius Araújo Oliveira | 4.2, 4.4, 2.4 | 0% | Não iniciado | M2 |
| 4.6 | S2 | HU12 | Controle PID para trajetória reta e centralizada | 07/10/2026 | 13/10/2026 | Gabriel Cavalcanti Monteiro | 4.3, 4.5 | 0% | Não iniciado | M2 |
| 4.7 | S2 | HU13 | Curvas de 90° e 180° dentro da célula de 18 cm | 07/10/2026 | 13/10/2026 | Maria Laura | 4.5 | 0% | Não iniciado | M2 |
| 4.8 | S2 | HU54 | Rotina de calibração dos sensores | 07/10/2026 | 13/10/2026 | Gustavo Rodrigues de Noronha | 4.3, 2.5 | 0% | Não iniciado | M2 |
| 4.9 | S2 | HU33 | Parada segura: corte de PWM em falha, interrupção ou perda de comunicação | 07/10/2026 | 13/10/2026 | Gustavo Rodrigues de Noronha | 4.4 | 0% | Não iniciado | M2 |
| 4.10 | S2 | HU14 | Wi-Fi e publicação MQTT da telemetria a 10 Hz pelo ESP32 | 07/10/2026 | 09/10/2026 | Vinícius Araújo Oliveira | 4.2, 5.4 | 0% | Não iniciado | M2 |
| 4.11 | S2 | HU27 | Verificação das pré-condições no robô antes de liberar o início | 07/10/2026 | 13/10/2026 | Gustavo Rodrigues de Noronha | 4.10, 5.6 | 0% | Não iniciado | M2 |
| 4.12 | S2 | HU28 | Início do percurso: tratar o comando e confirmar `run.started` em até 5 s (apoio: Software na interface) | 07/10/2026 | 13/10/2026 | Maria Laura | 4.10, 5.6 | 0% | Não iniciado | M2 |
| 4.13 | S2 | HU29 | Interrupção: tratar o comando e confirmar `run.interrupted` em até 2 s (apoio: Software na interface) | 07/10/2026 | 13/10/2026 | Gabriel Cavalcanti Monteiro | 4.9, 4.10, 5.6 | 0% | Não iniciado | M2 |
| 4.14 | S3 | HU53 | Correção da movimentação com odometria (avanço de célula e ângulo) | 14/10/2026 | 20/10/2026 | Gustavo Rodrigues de Noronha | 4.5, 4.6, 4.7 | 0% | Não iniciado | M3 |
| 4.15 | S3 | HU32 | Encerramento automático por objetivo, timeout de 10 min ou falha | 14/10/2026 | 20/10/2026 | Gabriel Cavalcanti Monteiro | 4.12, 5.20 | 0% | Não iniciado | M3 |
| 4.16 | S3 | HU30 (Should) | Nova execução sem reiniciar o sistema | 14/10/2026 | 20/10/2026 | Maria Laura | 4.12, 4.13, 4.15 | 0% | Não iniciado | M3 |
| 4.17 | S3 | Apoio à navegação | Integração da navegação com o controle de movimento (HU21–HU23) | 14/10/2026 | 20/10/2026 | Vinícius Araújo Oliveira | 4.14, 5.17 | 0% | Não iniciado | M3 |
| 4.18 | S4 | Testes de hardware | Ensaios de RNF15–RNF17 e RNF37 (proteção elétrica, tensão lógica, latência da malha, precisão dos sensores) | 21/10/2026 | 24/10/2026 | Subequipe de Hardware | 4.14–4.17 | 0% | Não iniciado | M4 |
| 4.19 | S4 | Documentação | Preencher [Testes de hardware](Testes%20de%20hardware.md) com evidências | 22/10/2026 | 25/10/2026 | Maria Laura + Gabriel Cavalcanti Monteiro | 4.18 | 0% | Não iniciado | M4 |

## 5. Subequipe de Software

| **ID** | **Fase** | **Entrega** | **Tarefa** | **Data de Início** | **Data de Fim** | **Responsável** | **Predecessor** | **% de Execução** | **Status** | **_Milestone_** |
|:------:|:------:|:--------:|------------|-----------------|-----------------|--------------------|-----------------------|:-----------------:|-------------|---------------|
| 5 | Execução | Sistema web, navegação e dados | Épicos 4 (navegação), 5 e 6 | 30/09/2026 | 25/10/2026 | Subequipe de Software | 1.4 | 0% | Não iniciado | M4 |
| 5.1 | S1 | Infraestrutura | `docker-compose` (Mosquitto, PostgreSQL, backend, frontend) e CI com testes e cobertura | 30/09/2026 | 04/10/2026 | Laryssa Felix Ribeiro Lopes | 1.4 | 0% | Não iniciado | — |
| 5.2 | S1 | Banco de dados | Esquema e migrações a partir do DER | 30/09/2026 | 04/10/2026 | Pedro Augusto Moretti Moreira | 5.1 | 0% | Não iniciado | — |
| 5.3 | S1 | Simulador | Simulador do robô publicando os tópicos e *payloads* do contrato MQTT | 30/09/2026 | 06/10/2026 | Thiago Alencar | 5.4 | 0% | Não iniciado | M1 |
| 5.4 | S1 | HU45 | Comunicação MQTT entre backend e broker conforme os contratos | 30/09/2026 | 04/10/2026 | Thiago Alencar | 5.1 | 0% | Não iniciado | M1 |
| 5.5 | S1 | HU47 | Validação de esquema das mensagens MQTT, com descarte das inválidas | 01/10/2026 | 06/10/2026 | Leticia da Silva Monteiro | 5.4 | 0% | Não iniciado | M1 |
| 5.6 | S1 | HU31 | Máquina de estados da execução no backend (apoio: Hardware) | 01/10/2026 | 06/10/2026 | Leticia da Silva Monteiro | 5.4 | 0% | Não iniciado | M1 |
| 5.7 | S1 | HU41 | Conexão WebSocket entre frontend React e backend, com *heartbeat* | 30/09/2026 | 06/10/2026 | Maria Eduarda de Jezus Guimaraes | 5.1 | 0% | Não iniciado | M1 |
| 5.8 | S1 | HU26 | Seleção do tipo de labirinto (4×4, 8×4, 12×4) | 01/10/2026 | 06/10/2026 | Maria Eduarda de Jezus Guimaraes | 5.2 | 0% | Não iniciado | M1 |
| 5.9 | S2 | HU20 | Classificação de paredes a partir dos sensores | 07/10/2026 | 13/10/2026 | Thiago Alencar | 4.3, 4.8 | 0% | Não iniciado | M2 |
| 5.10 | S2 | HU35 | Recepção da telemetria (MQTT → backend → WebSocket → tela), com latência medida | 07/10/2026 | 13/10/2026 | Maria Eduarda de Jezus Guimaraes | 5.4, 5.7 | 0% | Não iniciado | M2 |
| 5.11 | S2 | HU42 | Detecção de perda do WebSocket (timeout de 6 s) | 07/10/2026 | 13/10/2026 | Pedro Augusto Moretti Moreira | 5.7 | 0% | Não iniciado | M2 |
| 5.12 | S2 | HU44 | Sinalização de indisponibilidade na interface | 07/10/2026 | 13/10/2026 | Laryssa Felix Ribeiro Lopes | 5.11 | 0% | Não iniciado | M2 |
| 5.13 | S2 | HU46 | Reconexão MQTT automática (backend e ESP32) | 07/10/2026 | 13/10/2026 | Laryssa Felix Ribeiro Lopes | 5.4, 4.10 | 0% | Não iniciado | M2 |
| 5.14 | S2 | Interface de comando | Telas de preparação, início e interrupção (parte web de HU27–HU29) | 07/10/2026 | 13/10/2026 | Leticia da Silva Monteiro | 5.6, 5.8 | 0% | Não iniciado | M2 |
| 5.15 | S2 | HU48 | Armazenamento da execução e das amostras ao final | 07/10/2026 | 13/10/2026 | Pedro Augusto Moretti Moreira | 5.2, 5.6 | 0% | Não iniciado | M2 |
| 5.16 | S2 | HU49 | Associação da execução ao tipo de labirinto | 07/10/2026 | 13/10/2026 | Pedro Augusto Moretti Moreira | 5.8, 5.15 | 0% | Não iniciado | M2 |
| 5.17 | S3 | HU21 | Localização no labirinto: posição (x, y) e orientação | 14/10/2026 | 17/10/2026 | Thiago Alencar | 5.9, 4.5 | 0% | Não iniciado | M3 |
| 5.18 | S3 | HU22 | Determinação do percurso (algoritmo de decisão) | 14/10/2026 | 17/10/2026 | Thiago Alencar | 5.9 | 0% | Não iniciado | M3 |
| 5.19 | S3 | HU23 | Navegação autônoma: primeira volta completa no 4×4 até 19/10 (apoio: Hardware) | 16/10/2026 | 20/10/2026 | Thiago Alencar | 5.17, 5.18, 4.14, 2.6 | 0% | Não iniciado | M3 |
| 5.20 | S3 | HU24 | Identificação da célula objetivo em cada tipo de labirinto | 14/10/2026 | 20/10/2026 | Laryssa Felix Ribeiro Lopes | 5.17 | 0% | Não iniciado | M3 |
| 5.21 | S3 | HU25 | Registro do trajeto enviado na telemetria | 14/10/2026 | 20/10/2026 | Leticia da Silva Monteiro | 5.17, 5.10 | 0% | Não iniciado | M3 |
| 5.22 | S3 | HU36 | Visualização do trajeto ao vivo, com grade adaptada ao labirinto | 14/10/2026 | 20/10/2026 | Pedro Augusto Moretti Moreira | 5.10, 5.21 | 0% | Não iniciado | M3 |
| 5.23 | S3 | HU37 | Painel de bateria (tensão, corrente, consumo) | 14/10/2026 | 20/10/2026 | Leticia da Silva Monteiro | 5.10, 3.4 | 0% | Não iniciado | M3 |
| 5.24 | S3 | HU38 | Cronômetro da execução | 14/10/2026 | 20/10/2026 | Laryssa Felix Ribeiro Lopes | 5.10, 4.12 | 0% | Não iniciado | M3 |
| 5.25 | S3 | HU39 | Velocidade média | 14/10/2026 | 20/10/2026 | Maria Eduarda de Jezus Guimaraes | 5.10, 4.5 | 0% | Não iniciado | M3 |
| 5.26 | S3 | HU40 | Tela de resultado do desafio | 14/10/2026 | 20/10/2026 | Laryssa Felix Ribeiro Lopes | 5.6, 4.15 | 0% | Não iniciado | M3 |
| 5.27 | S3 | HU43 (Should) | Reconexão WebSocket (1, 2, 4, 8 e 16 s, depois a cada 30 s) | 14/10/2026 | 20/10/2026 | Leticia da Silva Monteiro | 5.11 | 0% | Não iniciado | M3 |
| 5.28 | S3 | HU50 | Consulta por labirinto | 14/10/2026 | 20/10/2026 | Pedro Augusto Moretti Moreira | 5.16 | 0% | Não iniciado | M3 |
| 5.29 | S3 | HU51 | Consulta geral e detalhe da execução | 14/10/2026 | 20/10/2026 | Maria Eduarda de Jezus Guimaraes | 5.15 | 0% | Não iniciado | M3 |
| 5.30 | S3 | HU52 | Registro e exibição de falhas da execução | 14/10/2026 | 20/10/2026 | Pedro Augusto Moretti Moreira | 5.15, 5.6 | 0% | Não iniciado | M3 |
| 5.31 | S1–S4 | Testes automatizados | Testes unitários e de integração com cobertura ≥ 80% | 30/09/2026 | 24/10/2026 | Subequipe de Software | 5.1 | 0% | Não iniciado | M4 |
| 5.32 | S2–S4 | Testes E2E | Suíte E2E (Playwright ou Cypress) usando o simulador | 07/10/2026 | 24/10/2026 | Maria Eduarda de Jezus Guimaraes + Laryssa Felix Ribeiro Lopes | 5.3, 5.10 | 0% | Não iniciado | M4 |
| 5.33 | S4 | Testes funcionais | Execução dos casos CT-01 a CT-16 | 21/10/2026 | 24/10/2026 | Subequipe de Software | M3 | 0% | Não iniciado | M4 |
| 5.34 | S4 | Documentação | Preencher [Testes de software](Testes%20de%20software.md) com relatórios de cobertura, E2E e CTs | 22/10/2026 | 25/10/2026 | Leticia da Silva Monteiro + Thiago Alencar | 5.31–5.33 | 0% | Não iniciado | M4 |

## 6. Integração e validação (equipe completa)

| **ID** | **Fase** | **Entrega** | **Tarefa** | **Data de Início** | **Data de Fim** | **Responsável** | **Predecessor** | **% de Execução** | **Status** | **_Milestone_** |
|:------:|:------:|:--------:|------------|-----------------|-----------------|--------------------|-----------------------|:-----------------:|-------------|---------------|
| 6 | Validação | Produto integrado | Integração dos subsistemas e ensaios finais | 08/10/2026 | 26/10/2026 | Equipe completa | 2, 3, 4, 5 | 0% | Não iniciado | M4 |
| 6.1 | S2 | Integração robô–web | ESP32 publicando no broker real e web exibindo telemetria real | 08/10/2026 | 09/10/2026 | Vinícius Araújo Oliveira + Thiago Alencar | 4.10, 5.10 | 0% | Não iniciado | M2 |
| 6.2 | S2 | Integração mecânica | Placa, bateria, sensores e cabeamento montados no chassi v2 | 10/10/2026 | 13/10/2026 | Estruturas + Hardware + Energia | 2.7, 2.8, 4.1, 3.3 | 0% | Não iniciado | M2 |
| 6.3 | S3 | Primeira volta autônoma | Volta completa no labirinto 4×4 sem intervenção | 19/10/2026 | 19/10/2026 | Equipe completa | 5.19, 6.2 | 0% | Não iniciado | AP10 |
| 6.4 | S3 | Congelamento | Todas as HUs Must concluídas; a partir daqui, só correções | 21/10/2026 | 21/10/2026 | Equipe completa | 2–5 (Must) | 0% | Não iniciado | M3 |
| 6.5 | S4 | Ensaios em labirinto | Ensaios nos labirintos 4×4, 8×4 e 12×4, com vídeos e tempos (RNF27 e RNF28) | 21/10/2026 | 24/10/2026 | Equipe completa | 6.4 | 0% | Não iniciado | M4 |
| 6.6 | S4 | RNFs de telemetria | Medir latência (RNF36), taxa de 10 Hz (RNF38), *heartbeat* e reconexão (RNF31–RNF34) | 21/10/2026 | 24/10/2026 | Pedro Augusto Moretti Moreira + Vinícius Araújo Oliveira | 6.1, 6.4 | 0% | Não iniciado | M4 |
| 6.7 | S4 | Ensaio geral | Ensaio da apresentação da AP12 | 25/10/2026 | 25/10/2026 | Equipe completa | 2.13, 3.10, 4.19, 5.34 | 0% | Não iniciado | M4 |
| 6.8 | Entrega | AP12 | Apresentação dos testes de estrutura, energia, hardware e software | 26/10/2026 | 26/10/2026 | Equipe completa | 6.7 | 0% | Não iniciado | M4 |

## 7. Marcos seguintes da disciplina

Estas entregas vêm depois de 26/10 e dependem do produto concluído. Ficam aqui apenas como referência.

| **ID** | **Fase** | **Entrega** | **Tarefa** | **Data de Início** | **Data de Fim** | **Responsável** | **Predecessor** | **% de Execução** | **Status** | **_Milestone_** |
|:------:|:------:|:--------:|------------|-----------------|-----------------|--------------------|-----------------------|:-----------------:|-------------|---------------|
| 7.1 | Encerramento | Testes de integração | Documentar [Testes de integração](Testes%20de%20integração.md) | 27/10/2026 | 23/11/2026 | Equipe completa | 6.8 | 0% | Não iniciado | AP18 |
| 7.2 | Encerramento | Apresentação do produto | Apresentação final do Micromouse | 24/11/2026 | 02/12/2026 | Equipe completa | 7.1 | 0% | Não iniciado | APT |
| 7.3 | Encerramento | Relatório final | Avaliação de desempenho e relatório de encerramento | 24/11/2026 | 04/12/2026 | Equipe completa | 7.1 | 0% | Não iniciado | AP20 |

---

## Carga por integrante

| Subequipe | Integrante | S1 | S2 | S3 | S4 |
|---|---|---|---|---|---|
| Estruturas | Heitor Santos Nobre | CAD e HU03 | HU01 | Apoio a HU02, HU04 e HU08 | Testes de estrutura |
| Estruturas | Henrique Brandão dos Santos | Impressão 3D e HU05 | Apoio a HU01 | Ajustes do chassi | Testes de estrutura |
| Estruturas | Pedro Augusto Ribeiro | HU06 | Apoio a HU07 | HU08 | Testes de estrutura |
| Estruturas | Yasmim de Souza Santos | Apoio a HU03 | HU07 | HU02 | Documentação |
| Estruturas | Pedro de Oliveira Calcado | Labirinto 4×4 | Labirinto 4×4 | HU04 | Documentação |
| Energia | Gabriel Andrade Magioli | Decisão sobre telemetria | HU16 | Apoio a HU18 | HU19, testes de energia |
| Energia | Rafael Silva Wasconcelos | HU17 | Apoio a HU07 | Corte por consumo | HU55, testes de energia |
| Energia | Pedro Gustavo Nunes Silva | HU15 | Apoio a HU16 | HU18 | Testes de energia, documentação |
| Hardware | Gabriel Cavalcanti Monteiro | Placa eletrônica | HU12, HU29 | HU32 | Testes de hardware, documentação |
| Hardware | Maria Laura | HU09 | HU13, HU28 | HU30 | Testes de hardware, documentação |
| Hardware | Vinícius Araújo Oliveira | HU10 | HU34, HU14 | Apoio à navegação | RNFs de telemetria |
| Hardware | Gustavo Rodrigues de Noronha | HU11 | HU54, HU33, HU27 | HU53 | Testes de hardware |
| Software | Laryssa Felix Ribeiro Lopes | Docker e CI | HU44, HU46 | HU24, HU38, HU40 | E2E |
| Software | Leticia da Silva Monteiro | HU47, HU31 | Interface de comando | HU25, HU37, HU43 | Cobertura, documentação |
| Software | Maria Eduarda de Jezus Guimaraes | HU41, HU26 | HU35 | HU39, HU51 | E2E |
| Software | Pedro Augusto Moretti Moreira | Esquema do banco | HU42, HU48, HU49 | HU36, HU50, HU52 | RNFs de telemetria |
| Software | Thiago Alencar | HU45, simulador | HU20 | HU21, HU22, HU23 | CTs, documentação |