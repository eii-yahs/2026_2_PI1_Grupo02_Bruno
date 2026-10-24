# Projeto conceitual de _hardware_

<figure>

| Diagrama de blocos | Esquemático |
|:------------------:|:-----------:|
| <img src="Images/BLOCK_DIAGRAM.png" alt="Diagrama de blocos do micromouse" width="450"> | <img src="Images/MICRO_MOUSE_SCHEMATIC_2.png" alt="Esquemático do micromouse" width="450"> |

<figcaption>

**Figura 1.** Diagrama de blocos e esquemático elétrico do sistema de controle e navegação do micromouse.

</figcaption>

</figure>

## Descrição dos subsistemas e justificativas de projeto

### 1. Microcontrolador
- **Componentes:** Módulo ESP_WROOM_32 (ESP32).
- **Conexões principais:** Recebe alimentação de 5V proveniente do regulador LM7805 e distribui sinais lógicos através dos seus pinos GPIO e barramento I2C.
- **Justificativa:** O ESP32 foi selecionado pelo seu elevado poder de processamento. Por ser dual core, é possível que um dos cores dela seja posto a trabalhar somente aos dados de leitura e comunicação com os módulos (INA219, ponte h, encoders e sensores), enquanto o outro core dela atua com a telemetria via protocolo de comunicação MQTT, onde serão exibidos dados como o estado da bateria, labirinto concluído e informações adicionais relevantes do robô. Também possui vasta disponibilidade de pinos e suporte a interrupções de *hardware*. Como o objetivo do robô é chegar à célula diagonalmente oposta guiando-se pelas paredes (sem a necessidade de mapear o centro do labirinto com algoritmos complexos como o *flood fill*), a capacidade de processamento do ESP32 é totalmente direcionada para garantir um algoritmo de *wall-following* reativo e de alta velocidade. O processamento rápido, aliado às interrupções, garante leituras precisas dos *encoders* e dos sensores IR em tempo real para manter o alinhamento constante.

### 2. Regulador de tensão (alimentação lógica)
- **Componentes:** Regulador de tensão linear LM7805.
- **Conexões principais:** A entrada (VI) recebe os 8.4V da bateria LiPo 2S (após passar pela chave liga/desliga), o GND é comum ao circuito, e a saída (VO) fornece 5V estabilizados para o pino VIN do ESP32.
- **Justificativa:** Diferente do esquemático anterior, a nova arquitetura com a "Mini Ponte H" não dispõe de um regulador interno. Portanto, o LM7805 tornou-se uma adição obrigatória para atuar como conversor *step-down*, reduzindo a tensão bruta da bateria (8.4V) para os 5V exigidos pelo ESP32 de forma segura, garantindo que o "cérebro" do robô não seja danificado por sobretensão.

### 3. Driver de motor e locomoção
- **Componentes:** Módulo mini ponte H (MX1508) e dois motores DC com *encoders* integrados.
- **Conexões principais:** A Mini ponte H é alimentada diretamente pelos 8.4V da bateria. O ESP32 envia sinais de controle para os pinos de entrada (IN1 a IN4), e o *driver* entrega a potência correspondente para as saídas dos motores (OUT1 a OUT4). 
- **Justificativa:** A substituição do volumoso L298N tradicional pela mini ponte H foi uma decisão feita devido a testes de bancada onde constatou-se que em  que nos picos de correntes exigidos pelos motores, a tensão regulada dos 5v do l298n se tornava instável, o que reiniciava a esp32. Tal problema acontecia pois o máximo de corrente que a L298N consegue fornecer é de até 500mA nos seus +5V. Além disso, uma ponte-h mini foi adotada pelo facto otimização de espaço e peso. Em competições de micromouse, reduzir a massa e o tamanho da placa aumenta consideravelmente a agilidade e a relação peso/potência do robô. Embora tenha exigido a adição do regulador LM7805 externo, a Mini Ponte H é muito mais eficiente e perfeitamente dimensionada para a corrente exigida pelos micro motores DC do projeto.

### 4. Sensores IR (sistema de percepção)
- **Componentes:** Quatro módulos de sensores infravermelhos FC-51.
- **Conexões principais:** Alimentados com 3.3V gerados pelo próprio ESP32, possuem suas saídas de sinal conectadas diretamente aos pinos de leitura do microcontrolador (GPIOs 34, 35, 36 e 39).
- **Justificativa:** O sistema foi atualizado de três para quatro sensores. Esse arranjo expande o campo de percepção do micromouse, permitindo configurações mais robustas (como o uso de um sensor frontal para identificação da parede da frente, um sensor traseiro para a inversão da "frente" do robô para que não seja necessário o robô girar em torno do próprio eixo z no caso de uma célula sem saída, além dos sensores laterais para identificação se há parede lateral ou não para saber quando deve virar). Isso aumenta significativamente a confiabilidade do algoritmo de *wall-following* em altas velocidades e a precisão nas tomadas de decisão nos cruzamentos do labirinto.

### 5. Sensor de corrente e tensão (gerenciamento de energia)
- **Componentes:** Módulo INA219.
- **Conexões principais:** Comunica-se com o ESP32 exclusivamente via protocolo I2C. Conectado aos terminais da bateria (linha de 8.4V) para leitura principal.
- **Justificativa:** Implementar o INA219 permite ao robô monitorar ativamente o seu próprio consumo elétrico e a tensão da bateria LiPo 2S. Isso é crucial para evitar descargas profundas (que danificam irreversivelmente baterias de lítio) e para garantir que o desempenho dos motores — que cai conforme a tensão da bateria diminui — possa ser dinamicamente compensado via *software* através do controle PID.

### 6. Motores DC com *encoders* (atuação e odometria)
- **Componentes:** Motores DC (N20) com caixa de redução e *encoders* de quadratura integrados (canais A e B).
- **Conexões principais:** Os terminais de potência dos motores são conectados diretamente às saídas da Mini Ponte H. Os pinos de sinal dos *encoders* são conectados aos pinos GPIO do ESP32 configurados com interrupção, recebendo alimentação lógica adequada.
- **Justificativa:** A utilização de motores com *encoders* integrados é vital para o controle de malha fechada. Operar em malha aberta resultaria em trajetórias tortas devido a diferenças de atrito no chão, desgastes na caixa de redução e flutuações na tensão da bateria. Os *encoders* fornecem o *feedback* exato de deslocamento e velocidade de cada roda (odometria), alimentando o algoritmo PID. Além de garantir retas perfeitas e giros exatos de 90° ou 180°, essa odometria permite contar exatamente quantas células do labirinto foram atravessadas, sendo o mecanismo principal para o robô identificar a célula de parada (a quina diagonalmente oposta).

### 7. Chave liga/desliga e capacitores de filtro (estabilidade e segurança)
- **Componentes:** Chave interruptora mecânica (*switch*) e capacitores para o regulador (0.47µF e 100nF).
- **Conexões principais:** A chave (*switch*) está conectada logo após o terminal VIN- do INA219, servindo de ponte para o restante do circuito e para a medição no INA219. Os capacitores estão alocados nas entradas e saídas do LM7805 e próximos ao ESP32.
- **Justificativa:** A chave mecânica permite ligar e desligar o robô rapidamente de forma segura, prevenindo desgastes dos conectores da bateria e garantindo que a partida do robô no labirinto seja controlada. Os capacitores são críticos para a estabilidade do regulador LM7805 e para a filtragem geral (*bypass*). Como os motores DC geram ruído elétrico severo e demandam picos de corrente ao arrancar ou inverter o sentido, os capacitores absorvem essas oscilações e filtram as altas frequências. Sem eles, o ruído da ponte H invadiria a alimentação lógica, causando travamentos e reinicializações indesejadas no ESP32.

## Mapeamento de Pinos (Pinout) do Microcontrolador

A tabela a seguir descreve a alocação dos pinos do módulo ESP32 (ESP-WROOM-32) utilizados no projeto, detalhando a sua respectiva função e o subsistema ao qual estão conectados, conforme definido no esquemático elétrico do robô.

| Pino na Placa (Serigrafia) | GPIO Interno | Subsistema | Função / Descrição |
| :--- | :--- | :--- | :--- |
| **VIN** | - | Alimentação | Entrada de alimentação lógica de 5V (proveniente do regulador LM7805). |
| **GND** | - | Alimentação | Referência de terra (GND) comum do circuito. |
| **3V3** | - | Alimentação | Saída regulada de 3.3V, utilizada para alimentar os quatro módulos de sensores IR. |
| **D21** | GPIO21 | Gerenciamento de Energia | Linha SDA (dados) do barramento I2C para comunicação com o sensor INA219. |
| **D22** | GPIO22 | Gerenciamento de Energia | Linha SCL (clock) do barramento I2C para comunicação com o sensor INA219. |
| **D35** | GPIO35 | Sistema de Percepção | Leitura do sinal do sensor infravermelho IR1. |
| **VP** | GPIO36 | Sistema de Percepção | Leitura do sinal do sensor infravermelho IR2. |
| **VN** | GPIO39 | Sistema de Percepção | Leitura do sinal do sensor infravermelho IR3. |
| **D34** | GPIO34 | Sistema de Percepção | Leitura do sinal do sensor infravermelho IR4. |
| **D32** | GPIO32 | Odometria (Motor A) | Leitura do canal C1 do encoder de quadratura do motor U3 (interrupção de hardware). |
| **D33** | GPIO33 | Odometria (Motor A) | Leitura do canal C2 do encoder de quadratura do motor U3 (interrupção de hardware). |
| **D25** | GPIO25 | Odometria (Motor B) | Leitura do canal C1 do encoder de quadratura do motor U6 (interrupção de hardware). |
| **D26** | GPIO26 | Odometria (Motor B) | Leitura do canal C2 do encoder de quadratura do motor U6 (interrupção de hardware). |
| **RX2** | GPIO16 | Atuação (Ponte H) | Sinal de controle (IN1) para o motor A na Mini Ponte H. |
| **TX2** | GPIO17 | Atuação (Ponte H) | Sinal de controle (IN2) para o motor A na Mini Ponte H. |
| **D18** | GPIO18 | Atuação (Ponte H) | Sinal de controle (IN3) para o motor B na Mini Ponte H. |
| **D19** | GPIO19 | Atuação (Ponte H) | Sinal de controle (IN4) para o motor B na Mini Ponte H. |
