# Análise de Consumo Energético do Produto

## Subsistemas elétricos

O micromouse utiliza dois motores DC N20 com encoders para locomoção e odometria. Uma ponte H L298N, controlada pelo ESP32, aciona os motores. Três sensores infravermelhos, orientados para a frente e para as laterais, fornecem as informações usadas na navegação pelas paredes. O INA219 mede a tensão da linha principal e a corrente fornecida pela bateria.

O ESP32 concentra a leitura dos sensores e encoders, o controle dos motores e o monitoramento elétrico. As características dos circuitos integrados constam nas folhas de dados dos fabricantes [1–3]. Para o dimensionamento energético, considera-se uma operação de 30 minutos por lançamento, com todos os componentes ativos durante esse período.

## Consumo energético

A energia consumida por cada grupo de componentes foi calculada a partir da tensão de operação, da corrente média por unidade e do tempo de uso:

$$
E_i = n_i V_i I_i t_i
$$

Nessa expressão, $n_i$ é a quantidade de componentes do grupo. Os dados adotados e os resultados estão reunidos na tabela a seguir.

**Tabela 1 — Consumo energético estimado para 1800 s de operação.**

| Componente | Quantidade | Tensão (V) | Corrente por unidade (mA) | Tempo (s) | Energia (J) | Energia (Wh) |
|:--|--:|--:|--:|--:|--:|--:|
| Motores DC N20 | 2 | 6,0 | 300 | 1800 | 6480,00 | 1,80000 |
| Ponte H L298N | 1 | 5,0 | 40 | 1800 | 360,00 | 0,10000 |
| Sensores IR | 3 | 3,3 | 20 | 1800 | 356,40 | 0,09900 |
| ESP32 | 1 | 3,3 | 100 | 1800 | 594,00 | 0,16500 |
| INA219 | 1 | 3,3 | 1 | 1800 | 5,94 | 0,00165 |
| **Total** | | | | | **7796,34** | **2,16565** |

O consumo estimado por lançamento é de **7796,34 J**, equivalentes a **2,16565 Wh**. Os motores respondem pela maior parte dessa demanda. Com uma margem de dimensionamento de 20%, a energia requerida passa a:

$$
E_{\mathrm{dim}}
= 1{,}20 E_{\mathrm{total}}
= 2{,}599\ \mathrm{Wh}
$$

## Fonte de alimentação

A fonte selecionada é uma bateria LiPo 2S de 800 mAh. Com tensão nominal de 7,4 V, ela armazena 5,92 Wh. O projeto limita a utilização a 80% da capacidade, preservando uma parcela da carga para evitar descarga excessiva da bateria [4]. Nessas condições, a energia considerada disponível é:

$$
E_{\mathrm{disp}}
= 0{,}80(5{,}92)
= 4{,}736\ \mathrm{Wh}
$$

Esse valor supera os **2,599 Wh** estabelecidos para o lançamento.

## Planejamento do circuito de alimentação

A bateria alimenta o barramento principal através do INA219 e da chave geral. Instalado em série com o positivo da bateria, o INA219 mede a corrente fornecida ao conjunto. A ponte H recebe energia desse barramento e aciona os dois motores conforme os sinais enviados pelo ESP32. O regulador de 5 V presente no módulo L298N alimenta o pino VIN da placa ESP32; a linha de 3,3 V da placa alimenta os sensores IR, o INA219 e os encoders.

A distribuição separa os caminhos de corrente dos motores das linhas de alimentação e sinal dos sensores, mantendo o terra comum necessário às medições e ao controle. Capacitores nos barramentos de alimentação e próximos aos circuitos integrados reduzem as variações de tensão e a propagação do ruído produzido durante a comutação dos motores. A chave geral permite desligar o robô sem remover a bateria.

## Monitoramento via software e coleta de dados

Durante o percurso, o ESP32 consulta o INA219 pelo barramento I²C e associa cada leitura de tensão e corrente ao tempo decorrido desde a partida. A carga retirada da bateria é calculada pela integração discreta da corrente:

$$
Q_{\mathrm{cons}} = \sum_{k=1}^{N} I_k\Delta t_k
$$

Com $I_k$ em ampères e $\Delta t_k$ em horas, $Q_{\mathrm{cons}}$ é obtido em ampère-hora. Para a bateria de 800 mAh, o limite de utilização definido para o projeto corresponde a **640 mAh consumidos**. Ao atingir esse valor, o programa interrompe o acionamento dos motores.

Os registros de tempo, tensão e corrente são gravados em blocos na memória interna do ESP32 e exportados após o percurso. A série medida permite verificar o tempo efetivo de operação, identificar a queda de tensão durante a movimentação e comparar a energia consumida com a estimativa da Tabela 1.

## Referências

1. STMicroelectronics. *L298: Dual Full-Bridge Driver*. [Folha de dados](https://www.st.com/resource/en/datasheet/l298.pdf).
2. Espressif Systems. *ESP32 Series Datasheet*. [Folha de dados](https://documentation.espressif.com/esp32_datasheet_en.pdf).
3. Texas Instruments. *INA219: Bidirectional Current/Power Monitor*. [Folha de dados](https://www.ti.com/lit/ds/symlink/ina219.pdf).
4. Gens Ace & Tattu. *LiPo Battery: Safety Instructions and Warnings*. [Manual](https://genstattu.com/content/instock/Tattu-Soft-UAV-pack-Manual.pdf).
