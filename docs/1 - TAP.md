# Termo de Abertura do Projeto

> Termo de abertura do projeto / Project Charter. Um documento publicado pelo iniciador ou patrocinador do projeto que autoriza formalmente a existência de um projeto e fornece ao gerente do projeto a autoridade para aplicar os recursos organizacionais nas atividades do projeto.

## Visão Geral do Projeto

### Dados do Projeto
- **Nome do Projeto:** Rato Cego
- **Data de Início:** 2 de setembro de 2026
- **Data de Término:** 4 de dezembro de 2026
- **Patrocinador:** Universidade de Brasília

## Objetivos

O projeto tem como finalidade o desenvolvimento, a integração e a validação de um robô móvel autônomo (Micromouse) capaz de mapear e solucionar labirintos desconhecidos de forma autônoma, unindo soluções das engenharias de Software, Eletrônica, Energia, Automotiva e Aeroespacial. Para orientar o desenvolvimento e garantir o rigor das entregas técnicas, o objetivo foi estruturado segundo a metodologia SMART:

### Specific (Específico)

O projeto entregará um protótipo físico funcional (chassi, circuito eletrônico e gerenciamento de energia) e um ecossistema de software (algoritmo de navegação, interface web e banco de dados). A solução deve mapear autonomamente as paredes do circuito por meio de sensores, localizar a célula final sem intervenção humana e transmitir em tempo real os dados de desempenho para a plataforma web, armazenando o histórico para consultas pós-corrida.

### Measurable (Mensurável)

O sucesso do projeto será avaliado por meio de parâmetros numéricos e indicadores objetivos de desempenho técnico, como:

1. **Dimensões Físicas do Robô:** O protótipo deve respeitar estritamente o limite físico de 16,5 cm de largura e comprimento máximos (sem restrição para altura).
2. **Navegação e Resolução de Desafios:** Resolver autonomamente três labirintos de tamanhos progressivos compostos por células de 18 cm de lado:
   - Primeiro: 4x4 células (72 x 72 cm²)
   - Segundo: 8x4 células (144 x 72 cm²)
   - Terceiro: 12x4 células (216 x 72 cm²)
3. **Tempo Limite de Execução:** Completar cada tentativa dentro do tempo limite de 10 minutos estipulado para a resolução de cada desafio prático.
4. **Transmissão de Telemetria:** Apresentar graficamente em tempo real no sistema web 6 dados obrigatórios:
   - Tipo do labirinto
   - Trajeto percorrido
   - Consumo de bateria
   - Velocidade média
   - Tempo de conclusão
   - Confirmação de desafio cumprido (S/N)
5. **Persistência de Dados:** Salvar as informações pós-corrida em um banco de dados que possibilite realizar consultas específicas filtradas por um labirinto individualizado ou gerais para exibir todos os dados de forma conjunta.
6. **Desempenho de Resolução (Métricas de Sucesso):** Obter êxito na resolução do percurso avaliado pelo número de tentativas por labirinto (com notas decrescentes de 10, 7,5 e 5 para 1, 2 ou 3 tentativas, respectivamente) e pela integridade de transmissão de dados do software (fator multiplicador de nota de 1,0 para dados exibidos com sucesso e de 0,75 para falha na exibição).

### Agreed (Acordado)

O objetivo deve ser acordado entre os integrantes do grupo e os professores responsáveis pelo projeto, garantindo que os requisitos, decisões e entregas estejam alinhados às expectativas estabelecidas para o desafio. As soluções desenvolvidas deverão ser discutidas e submetidas à avaliação dos professores ao longo do projeto, permitindo validar se o desenvolvimento está de acordo com as especificações definidas.

### Realistic (Realista)

O desenvolvimento deve ser viável considerando as restrições de orçamento, materiais disponíveis, conhecimentos técnicos da equipe e prazo estabelecido para o projeto. A solução será construída utilizando componentes e tecnologias compatíveis com os recursos disponíveis, priorizando a integração entre hardware e software e o cumprimento dos requisitos essenciais de navegação, telemetria e armazenamento de dados.

### Time Bound (Limitado no Tempo)

O desenvolvimento, a integração e a validação do protótipo deverão ser concluídos até 4 de dezembro de 2026, conforme o período definido para o projeto. Durante as 12 semanas de execução, deverão ser realizadas as etapas de planejamento, desenvolvimento, integração, testes e validação da solução, de modo que o protótipo esteja funcional e apto à execução dos três desafios dentro do prazo estabelecido.

## Público-Alvo

O público-alvo deste projeto abrange tanto atores diretos envolvidos na execução e avaliação do sistema quanto a comunidade externa beneficiada pelas soluções desenvolvidas para o desafio da disciplina.

- **Público direto:** professores e avaliadores da disciplina de Projeto Integrador de Engenharia 1, além dos próprios membros da equipe.
- **Público indireto:** comunidade acadêmica, estudantes e organizações interessados em tópicos relacionados a robótica.

## Descrição do Problema

O projeto surge da necessidade de desenvolver um sistema capaz de solucionar, de forma autônoma, labirintos. O principal desafio está em fazer com que o micromouse seja capaz de monitorar sua própria localização, identificar as paredes presentes no percurso, construir o mapeamento do labirinto e reconhecer quando alcançar a área de objetivo, sem intervenção humana durante a execução.

Além da capacidade de navegação autônoma, o sistema precisa operar dentro de restrições físicas e de funcionamento específicas, como o limite de 16,5 cm de comprimento e largura, a impossibilidade de utilizar mecanismos como voo, salto, escalada ou propulsão por combustão ou foguete, e a necessidade de evitar danos ao labirinto. Essas condições tornam necessário o desenvolvimento integrado de uma solução de estruturas, energia, hardware e software, em vez da utilização de uma solução pronta disponível no mercado.

O desafio é ampliado pelo fato de o micromouse precisar resolver três labirintos de diferentes dimensões (4×4, 8×4 e 12×4 células), iniciando em um beco sem saída e tendo como objetivo uma área localizada no canto diametralmente oposto. Dessa forma, a solução deve ser capaz de lidar com diferentes configurações de percurso, mantendo sua autonomia e capacidade de navegação.

Outro aspecto do problema é a necessidade de acompanhar o desempenho do micromouse durante e após a execução. O sistema deve apresentar, em tempo real, informações como o tipo de labirinto, o trajeto percorrido, o consumo de bateria, a velocidade média, o tempo de conclusão e o cumprimento ou não do desafio. Após a execução, esses dados devem ser armazenados em um banco de dados, permitindo consultas específicas de cada labirinto ou de todos eles. A oportunidade está em integrar conhecimentos das diferentes engenharias para construir e validar uma solução própria, capaz de atender a todos esses requisitos em um único sistema.

## Indicadores
 
1. **Nº de equipes que cursam Projeto Integrador de Engenharia por semestre na FCTE/UnB Gama:** Por volta de 10 equipes totais, contando com todas as turmas e duas equipes por turma, uma vez que tem uma turma para cada Engenharia ofertada na Faculdade de Ciências e Tecnologias em Engenahrias.
2. **Nº de equipes escolares participantes na fase regional da Olimpíada Brasileira de Robótica (OBR) no Distrito Federal:** mais de 100 equipes de escolas públicas e privadas do DF, reunindo cerca de 500 estudantes (edição 2026), público potencial para uma versão didática do produto.
   *Fonte: Correio Braziliense, "Sesc Taguatinga Norte sedia etapa da Olimpíada Brasileira de Robótica" (set. 2026) — https://www.correiobraziliense.com.br/cidades-df/2026/09/7493970-sesc-taguatinga-norte-sedia-etapa-da-olimpiada-brasileira-de-robotica.html*
3. **Nº de participantes da Olimpíada Brasileira de Robótica (OBR) em nível nacional:** mais de 220 mil participantes na edição de 2025, uma das maiores olimpíadas científicas do país, realizada anualmente desde 2006 pelo IFRN em parceria com a RoboCup Brasil.
   *Fonte: OBR – Olimpíada Brasileira de Robótica, site oficial — https://obr.robocup.org.br/sobre/*
4. **Nº de equipes de competição de robótica registradas na UnB:** ao menos 4 equipes ativas (UnBall, UnBeatables, DROID e EDRA), que representaram a universidade na Competição Brasileira de Robótica (CBR) 2023, compartilhando laboratórios, fornecedores e conhecimento técnico com o projeto.
   *Fonte: UnB Notícias, "UnB leva pódio na Competição Brasileira de Robótica 2023" — https://noticias.unb.br/ensino/6927-unb-leva-podio-na-competicao-brasileira-de-robotica-2023*
5. **Nº de competições de categoria micromouse realizadas anualmente:** estima-se mais de 100 competições de micromouse por ano, muitas delas patrocinadas por universidades e pela IEEE (dado de abrangência mundial, não exclusivo do Brasil).
   *Fonte: Christiansen, D. (2014), citado em Embarcados, "Micromouse: Um robô solucionador de labirinto" — https://embarcados.com.br/micromouse/*
   
## Membros do Grupo

### Equipe de Software

| Nome | Matrícula | Curso | Email |
|---|---|---|---|
| Laryssa Felix Ribeiro Lopes | 231026840 | Engenharia de Software | 231026840@aluno.unb.br |
| Leticia da Silva Monteiro | 231026859 | Engenharia de Software | emaildeestudos90@gmail.com |
| Maria Eduarda de Jezus Guimaraes | 242015924 | Engenharia de Software | mariaeduardajg2401@gmail.com |
| Pedro Augusto Moretti Moreira | 241011546 | Engenharia de Software | morettipdr@gmail.com |
| Thiago Alencar | 241011635 | Engenharia de Software | thiagooliveira1403@gmail.com |

### Equipe de Hardware

| Nome | Matrícula | Curso | Email |
|---|---|---|---|
| Gabriel Cavalcanti Monteiro | 241011920 | Engenharia Eletrônica | jackfrostyy061@gmail.com |
| Maria Laura | 232005361 | Engenharia de Software | marialauraregi@gmail.com |
| Vinícius Araújo Oliveira | 241025425 | Engenharia de Software | viniciustrabalhosunb@gmail.com |
| Gustavo Rodrigues de Noronha | 241011930 | Engenharia de Software | gustavuh0012@gmail.com |

### Equipe de Energia

| Nome | Matrícula | Curso | Email |
|---|---|---|---|
| Gabriel Andrade Magioli | 232013980 | Engenharia de Software | 232013980@aluno.unb.br |
| Rafael Silva Wasconcelos | 232030364 | Engenharia de Software | 232030364@aluno.unb.br |
| Pedro Gustavo Nunes Silva | 241011573 | Engenharia Aeroespacial | pedrogustavo.5@outlook.com |

### Equipe de Estruturas

| Nome | Matrícula | Curso | Email |
|---|---|---|---|
| Heitor Santos Nobre | 241025461 | Engenharia de Software | heitor.santosnobre@gmail.com |
| Henrique Brandao dos Santos | 241011152 | Engenharia Aeroespacial | riqb.santos@gmail.com |
| Pedro Augusto Ribeiro | 241040323 | Engenharia de Software | pedrufb@gmail.com |
| Yasmim de Souza Santos | 241040860 | Engenharia de Software | yasmimdesouzasantos200612@gmail.com |
| Pedro de Oliveira Calcado | 241011564 | Engenharia Aeroespacial | pedrool3unb@gmail.com |

**Orientador:** Bruno Luiz Pereira

## Orçamento Estimado (R$)

O orçamento prévio para o desenvolvimento foi estabelecido com base em materiais e peças necessários para construção do projeto. Levando em consideração as quantidades e valores, o valor total estimado é de **R$ 428,00**, e considerando que a equipe é composta por 17 membros, chega-se a um custo aproximado de **R$ 25,18 por membro** da equipe.

**PROJETO INTEGRADOR 1 | CONTROLE DE COMPRAS | Grupo 2**

| | |
|---|---|
| **Investimento Total** | R$ 428,00 |
| Itens Cadastrados | 10 |
| Quantidade Total de Itens (Und) | 11 |
| Valor Para Cada Membro | R$ 25,18 |

| Nome do Item | Quantidade do Item | Valor Unitário (R$) | Total (R$) |
|---|---|---|---|
| MOTOR | 2 | R$ 30,00 | R$ 60,00 |
| PLACA PERFURADA | 1 | R$ 20,00 | R$ 20,00 |
| 1 PAR RODAS | 1 | R$ 20,00 | R$ 20,00 |
| 4 SENSORES INFRAVERMELHO | 1 | R$ 16,00 | R$ 16,00 |
| RODA BOBA | 1 | R$ 9,00 | R$ 9,00 |
| L298N | 1 | R$ 20,00 | R$ 20,00 |
| ESP32 | 1 | R$ 40,00 | R$ 40,00 |
| 40x JUMPERS MACHO-FÊMEA | 1 | R$ 13,00 | R$ 13,00 |
| BATERIA 2S 7,4V | 1 | R$ 140,00 | R$ 140,00 |
| FILAMENTO (PLA/PETG) | 1 | R$ 90,00 | R$ 90,00 |

## Duração Estimada (Horas)

- **Carga horária semanal por integrante:** aproximadamente 5 horas semanais, compreendendo horários de aula e horários extracurriculares.
- **Duração do projeto:** 12 semanas.
- **Esforço individual:** Considerando 5 horas semanais por 12 semanas, o esforço é de 60 horas por integrante.
- **Esforço por equipe:** Considerando as equipes de 4 integrantes, o esforço será de 240 horas, e para a equipe de 5 integrantes, o esforço será de 300 horas.
- **Esforço total:** O projeto em sua totalidade, com todos os integrantes, será realizado em um total de 1.020 horas.