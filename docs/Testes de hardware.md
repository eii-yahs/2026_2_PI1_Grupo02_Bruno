# Testes de Hardware

## Testes de _hardware_

> Apresentar com detalhes os testes feitos para conferir se os objetivos do *hardware* foram atendidos, incluindo:
>
> 1. Processamento (Arduino, ESP32 etc):
>    - Definir interface de desenvolvimento;
>    - Rodar exemplo mínimo.














## 2. Sistema sensorial (posição, odometria e telemetria)

Este tópico documenta a arquitetura de percepção do **Micromouse**, detalhando a aquisição, o tratamento e a transmissão de dados dos sensores infravermelhos (detecção de paredes), encoders magnéticos (velocidade e distância) e do módulo INA219 (tensão e corrente).

### 2.1. Realização da Leitura Básica de Dados

A aquisição de dados foi dividida de acordo com a natureza de cada sensor, visando otimizar o uso do processador da ESP32.

- **Sensores Infravermelhos (IR):** A leitura é analógica, realizada de forma sequencial por meio da função `analogRead()`, utilizando os pinos ADC exclusivos de entrada da ESP32 (GPIOs 34, 35, 36 e 39).

  ```cpp
  rawE = analogRead(IR_ESQ);
  rawF = analogRead(IR_FRENTE);
  rawD = analogRead(IR_DIR);
  rawT = analogRead(IR_TRAS);
  ```

- **Encoders (odometria):** A leitura de posição é realizada de forma assíncrona por meio de interrupções de hardware, configuradas com `attachInterrupt(..., RISING)`. A cada borda de subida detectada no canal A, as funções `isrE()` e `isrD()` incrementam ou decrementam os contadores de *ticks*, conforme o estado do canal B.

  Essa abordagem reduz a possibilidade de perda de pulsos durante a execução de outras tarefas, embora não garanta a captura de todos os pulsos em qualquer condição de operação.

  ```cpp
  attachInterrupt(ENC_E_A, isrE, RISING);
  attachInterrupt(ENC_D_A, isrD, RISING);
  ```

- **Monitoramento de energia:** O módulo INA219 fornece os dados de tensão e corrente por meio do barramento I²C, utilizando as funções da biblioteca `Adafruit_INA219`.

  ```cpp
  ina219.getBusVoltage_V();
  ina219.getCurrent_mA();
  ```

### 2.2. Validação e calibração das leituras

A calibração do sistema é definida por constantes no código, cujos valores devem ser ajustados por meio de testes experimentais para relacionar as leituras dos sensores às grandezas físicas.

#### Calibração de distância

Os encoders permitem estimar deslocamentos a partir da quantidade de pulsos registrados.

As constantes utilizadas inicialmente são:

```cpp
const long TICKS_90 = 330;
const long TICKS_ALINHAMENTO = 125;
const long TICKS_AVANCO = 250;
```

- **`TICKS_90`:** Quantidade estimada de ticks necessária para realizar uma rotação de 90°.
- **`TICKS_ALINHAMENTO`:** Quantidade de ticks utilizada para alinhar o centro do robô com uma abertura lateral antes de realizar uma curva.
- **`TICKS_AVANCO`:** Quantidade de ticks utilizada para avançar após uma curva e entrar no novo corredor.

Considerando um labirinto que possui células de **18 × 18 cm**, é necessário calibrar essas constantes conforme o diâmetro das rodas, a resolução dos encoders e as dimensões físicas do robô.

#### Calibração de paredes

A detecção de obstáculos utiliza um limiar aplicado às leituras analógicas dos sensores infravermelhos.

```cpp
const int LIMITE_PAREDE = 1500;
```

Considerando a resolução padrão de 12 bits do ADC da ESP32, as leituras variam nominalmente entre **0 e 4095**.

Quando a leitura ultrapassa o limiar definido, o sistema considera que existe uma parede próxima ao sensor.

O valor de `1500` é um parâmetro inicial, que deve ser validado experimentalmente conforme a distância de detecção desejada, as características dos sensores e as condições de iluminação.

### 2.3. Definição das taxas de amostragem e volume de dados

O sistema utiliza duas frequências de operação distintas: uma destinada à leitura e ao controle local e outra à atualização da interface de telemetria.

#### Amostragem de controle (alta frequência)

O ciclo principal do programa utiliza:

```cpp
delay(10);
```

Esse intervalo corresponde a uma frequência nominal máxima de aproximadamente **100 Hz**, desconsiderando o tempo de execução das demais funções.

Entretanto, a frequência efetiva pode ser inferior devido ao processamento das leituras, à comunicação HTTP e às rotinas de movimentação.

O controle de alinhamento das rodas utiliza uma estratégia **proporcional (P)**, baseada na diferença entre os deslocamentos registrados pelos encoders.

```cpp
const float KP = 2.0;
```

#### Transmissão de telemetria (baixa frequência)

A atualização da interface web é realizada por requisições HTTP assíncronas (*polling*).

O navegador solicita os dados ao servidor da ESP32 a cada **200 milissegundos**, correspondendo a uma frequência nominal de **5 Hz**.

```javascript
setInterval(async () => {
  const j = await (await fetch('/dados')).json();
}, 200);
```

Essa estratégia permite acompanhar o estado dos sensores e do robô sem exigir uma transmissão contínua de dados.

#### Quantidade de dados

As informações são organizadas em uma estrutura JSON compacta, construída pela função `enviarDados()`.

Os dados transmitidos incluem:

| Informação | Variável JSON | Tipo |
|---|---|---|
| Sensor frontal | `f` | Inteiro |
| Sensor traseiro | `t` | Inteiro |
| Sensor esquerdo | `e` | Inteiro |
| Sensor direito | `d` | Inteiro |
| Parede frontal | `pf` | Booleano |
| Parede traseira | `pt` | Booleano |
| Parede esquerda | `pe` | Booleano |
| Parede direita | `pd` | Booleano |
| Frente invertida | `inv` | Booleano |
| Ação atual | `acao` | String |
| Tensão | `v` | Numérico |
| Corrente | `i` | Numérico |

Dessa forma, são transmitidos **11 campos numéricos ou booleanos e uma string de estado**, totalizando 12 campos.

### 2.4. Validação das taxas definidas

A utilização de interrupções para aquisição dos pulsos dos encoders permite que a contagem ocorra independentemente das leituras periódicas realizadas no programa principal.

Paralelamente, a comunicação com a interface web utiliza requisições HTTP espaçadas, reduzindo a frequência de processamento das solicitações.

O servidor é atendido pela função:

```cpp
server.handleClient();
```

Essa função é executada no `loop()` principal e durante determinadas rotinas de movimentação, permitindo que a ESP32 processe comandos enviados pela interface.

A taxa nominal de **5 Hz** para a telemetria foi escolhida como compromisso inicial entre atualização visual e carga de processamento.

Entretanto, sua adequação deve ser confirmada experimentalmente, medindo a latência das requisições e o impacto sobre o tempo de execução do controle.

É importante destacar que as rotinas de movimentação utilizam estruturas bloqueantes (`while`), de modo que a frequência de leitura dos sensores e a resposta do sistema podem variar durante as manobras.

### 2.5. Processamento básico de dados (tratamento de sinais)

O código aplica operações de processamento digital aos sinais adquiridos, transformando as leituras em informações utilizadas pela lógica de navegação.

#### Limiarização (*thresholding*)

A limiarização é o principal método de processamento aplicado aos sensores infravermelhos.

Os valores analógicos são comparados com um limite previamente definido, produzindo estados booleanos que indicam a presença ou ausência de paredes.

```cpp
paredeE = rawE > LIMITE_PAREDE;
paredeF = rawF > LIMITE_PAREDE;
paredeD = rawD > LIMITE_PAREDE;
paredeT = rawT > LIMITE_PAREDE;
```

Assim, a lógica de navegação recebe informações simplificadas:

- `true`: parede detectada.
- `false`: parede não detectada.

Esse processamento facilita a implementação do algoritmo de seguimento de paredes pela direita (*Right-Hand Rule*) e da estratégia de inversão da frente em becos sem saída.

Entretanto, a limiarização simples não elimina ruídos ou oscilações próximas ao valor de referência.

#### Tratamento dos encoders com `labs()`

A função `labs()` calcula o valor absoluto de uma diferença entre contagens dos encoders.

```cpp
long deltaE = labs(e - referenciaE);
long deltaD = labs(d - referenciaD);
```

Essa operação permite comparar os módulos dos deslocamentos das rodas, independentemente do sinal das contagens.

A diferença entre os deslocamentos é utilizada para calcular o erro de alinhamento:

```cpp
int erro = constrain(
  (int)(deltaE - deltaD),
  -50,
  50
);
```

Em seguida, o controlador proporcional ajusta a velocidade de cada motor:

```cpp
int velE = constrain(
  (int)(VEL - KP * erro),
  0,
  255
);

int velD = constrain(
  (int)(VEL + KP * erro),
  0,
  255
);
```

Dessa maneira, busca-se reduzir a diferença de deslocamento entre as rodas durante o movimento retilíneo.

É importante observar que `labs()` não constitui um filtro digital e que o erro calculado pode assumir valores positivos ou negativos. A operação apenas elimina o sinal individual dos deslocamentos.

#### Média móvel (implementação futura)

Caso sejam observadas oscilações significativas nas leituras dos sensores infravermelhos, poderá ser implementado um filtro de média móvel.

A proposta consiste em armazenar as últimas cinco leituras de cada sensor em um *buffer* circular e calcular sua média antes da limiarização.

A média móvel de cinco amostras pode ser representada por:

```math
y[n] = \frac{1}{5}\sum_{k=0}^{4}x[n-k]
```

Onde:

- \(x[n]\): leitura atual do sensor.
- \(x[n-k]\): leituras anteriores.
- \(y[n]\): valor filtrado utilizado na detecção de paredes.

A implementação desse filtro poderá reduzir a influência de variações rápidas e ruídos nas leituras analógicas.

Como contrapartida, introduzirá um pequeno atraso na identificação de mudanças, que deverá ser considerado na calibração das curvas e na detecção das aberturas do labirinto.

## 🎥 Demonstração do protótipo

O vídeo a seguir apresenta o estágio atual de desenvolvimento do **Micromouse**, demonstrando o funcionamento geral do protótipo e a integração dos sistemas implementados até o momento.

A demonstração tem como objetivo registrar o progresso do projeto e apresentar os resultados preliminares dos testes realizados.

**Vídeo demonstrativo:**

<

https://github.com/user-attachments/assets/1e8d90ab-a7ef-4162-961d-9c9f254efaf5

>

## 3. Armazenamento de Dados

O sistema de armazenamento do **Micromouse** utiliza exclusivamente a memória interna da ESP32, dispensando o uso de dispositivos externos, como cartões SD ou pendrives.

Como o robô realiza a navegação de maneira autônoma e processa as informações dos sensores em tempo de execução, não há necessidade de manter um histórico permanente das leituras.

### 3.1. Armazenamento de Dados Básicos

Os dados utilizados pelo sistema são armazenados temporariamente na memória RAM da ESP32, por meio de variáveis globais definidas no código.

Entre as principais informações armazenadas estão:

- **Leituras dos sensores infravermelhos:** valores analógicos dos sensores frontal, traseiro, esquerdo e direito, armazenados nas variáveis `rawF`, `rawT`, `rawE` e `rawD`.
- **Estado de detecção de paredes:** variáveis booleanas `paredeF`, `paredeT`, `paredeE` e `paredeD`, que indicam a presença ou ausência de obstáculos.
- **Dados dos encoders:** contadores `ticksE` e `ticksD`, responsáveis por registrar os pulsos dos motores e permitir a estimativa dos deslocamentos.
- **Estado de navegação:** variáveis como `rodando`, `frenteInvertida` e `ultimaAcao`, utilizadas para controlar o comportamento do robô.

Essas informações são atualizadas durante a execução do programa e permanecem disponíveis enquanto a ESP32 estiver em funcionamento.

Como os dados são mantidos em memória volátil, eles são perdidos quando o dispositivo é desligado ou reiniciado.

### 3.2. Armazenamento e Validação dos Dados dos Sensores

As leituras dos sensores são armazenadas temporariamente em variáveis e processadas antes de serem utilizadas pelo algoritmo de navegação.

No caso dos sensores infravermelhos, os valores analógicos são obtidos por meio da função `analogRead()`:

```cpp
rawE = analogRead(IR_ESQ);
rawF = analogRead(IR_FRENTE);
rawD = analogRead(IR_DIR);
rawT = analogRead(IR_TRAS);
```

Em seguida, os valores são comparados com um limiar previamente definido:

```cpp
paredeE = rawE > LIMITE_PAREDE;
paredeF = rawF > LIMITE_PAREDE;
paredeD = rawD > LIMITE_PAREDE;
paredeT = rawT > LIMITE_PAREDE;
```

Esse processamento transforma os valores analógicos em informações booleanas, permitindo que o sistema identifique a presença ou ausência de paredes.

Para os encoders, os pulsos registrados são armazenados em contadores do tipo `volatile long`, atualizados por interrupções de hardware.

```cpp
volatile long ticksE = 0;
volatile long ticksD = 0;
```

Esses valores são utilizados para estimar os deslocamentos das rodas e realizar a correção proporcional de velocidade dos motores.

A validação das leituras é realizada por meio da comparação com os parâmetros de referência definidos no código. Entretanto, o sistema ainda não implementa mecanismos mais avançados de validação, como filtros digitais, detecção de leituras inconsistentes ou armazenamento de históricos para análise posterior.

### 3.3. Disponibilização dos Dados para Telemetria

Embora os dados não sejam armazenados permanentemente, o sistema permite sua visualização por meio de uma interface web hospedada na própria ESP32.

A função `enviarDados()` organiza as informações em formato JSON e as disponibiliza por meio do servidor HTTP.

O navegador solicita periodicamente os dados, permitindo acompanhar:

- Leituras dos quatro sensores infravermelhos;
- Estado de detecção das paredes;
- Orientação atual do robô;
- Ação executada pelo algoritmo de navegação;
- Tensão e corrente medidas pelo INA219.

A interface funciona como uma ferramenta de monitoramento em tempo de execução, sem realizar o registro permanente das informações.

### 3.4. Considerações sobre o Armazenamento

A utilização exclusiva da memória RAM simplifica a arquitetura do sistema, reduzindo a quantidade de componentes necessários e evitando operações de escrita em dispositivos externos.

Essa abordagem é adequada à estratégia de navegação adotada, baseada no seguimento de paredes, pois o algoritmo utiliza principalmente as informações atuais dos sensores para determinar os movimentos.

Como possibilidade de desenvolvimento futuro, poderá ser implementado o armazenamento de históricos de leituras e eventos de navegação na memória não volátil da ESP32, permitindo análises posteriores do comportamento do robô.


# **4. Atuadores**

Os atuadores são dois motores DC N20 com caixa de redução e *encoder* de quadratura, acionados pela ponte H dupla L298N. O ESP32 define o sentido de giro pelas entradas IN1 a IN4 e a velocidade pelo PWM nos pinos de habilitação ENA e ENB.

| Motor | Sentido | Velocidade (PWM) | *Encoder* (A / B) |
| :---- | :---- | :---- | :---- |
| Esquerdo | IN1 (GPIO16) / IN2 (GPIO17) | ENA (GPIO4) | GPIO32 / GPIO33 |
| Direito | IN3 (GPIO18) / IN4 (GPIO19) | ENB (GPIO2) | GPIO25 / GPIO26 |

*Tabela 11 – Pinos de acionamento dos motores.*

Fonte: Elaborado pelos autores (2026).

## **4.1. Acionamentos básicos (ID 3.12)**

**Requisitos associados:** [RF11](http://requisitos.md#rf11) e [RF33](http://requisitos.md#rf33).

Cada motor foi acionado nos três estados da L298N:

| IN1 / IN2 (IN3 / IN4) | ENA (ENB) | Resultado |
| :---: | :---: | :---- |
| Alto / baixo | PWM | Motor gira para frente |
| Baixo / alto | PWM | Motor gira para trás |
| Qualquer | 0 | Motor desenergizado (parada) |

*Tabela 12 – Estados de acionamento dos motores.*

Fonte: Elaborado pelos autores (2026).

**Resultado.** Os dois motores giraram nos dois sentidos e pararam com o PWM em zero, confirmando o acionamento reversível ([RF11](http://requisitos.md#rf11)). A parada também foi verificada pelo comando "Parar" (/p) da página de supervisão ([RF33](http://requisitos.md#rf33)).

## **4.2. Processamento básico: PWM (ID 3.13)**

**Requisito associado:** [RF11](http://requisitos.md#rf11).

O PWM é gerado pelo periférico LEDC do ESP32 (ledcAttach e ledcWrite) nos pinos ENA (GPIO4) e ENB (GPIO2), a **20 kHz** e com **8 bits** de resolução (0 a 255). A frequência de 20 kHz fica no limite da audição humana, o que evita o chiado dos motores em frequências mais baixas.

| *Duty cycle* | Valor em ledcWrite |
| :---: | :---: |
| 25% | 64 |
| 50% | 128 |
| 75% | 191 |
| 100% | 255 |

*Tabela 13 – Relação entre duty cycle e valor do PWM.*

Fonte: Elaborado pelos autores (2026).

**Resultado.** O PWM nos pinos ENA e ENB controlou a velocidade dos dois motores, nos dois sentidos de giro.

## **4.3. Acionamento com base nos dados dos sensores (ID 3.14)**

**Requisitos associados:** [RF12](http://requisitos.md#rf12), [RF13](http://requisitos.md#rf13), [RF34](http://requisitos.md#rf34) e [RF53](http://requisitos.md#rf53).

Os motores são acionados a partir de dois sensores:

* ***Encoders*****:** os pulsos de cada roda (ticksE e ticksD) são usados para corrigir desvios em linha reta e para encerrar cada movimento ao atingir a meta de pulsos: TICKS\_AVANCO \= 250 no avanço e TICKS\_90 \= 330 no giro de 90°.

* **Sensores IR:** as leituras das GPIOs 36, 39 e 34 são comparadas com o limiar LIMITE\_PAREDE \= 1500 para indicar parede à esquerda, à frente e à direita. A partir dessa informação, o robô decide se avança, gira 90° para a direita ou para a esquerda.



| **ID** | **Descrição** | **Data de Início** | **Data de Conclusão** | **Responsável** | **Resultados Esperados** | **Resultados Obtidos** | **Aprovação Interna (S/N)** | **Aprovação do Orientador (S/N)** | **Observações** |
|:------:|---------------|--------------------|-----------------------|-----------------|--------------------------|------------------------|:---------------------------:|:--------------------------------:|-----------------|
| 3.1 | | | | | | | | | |
| 3.2 | | | | | | | | | |
| 3.3 | | | | | | | | | |
| 3.4 | | | | | | | | | |
| 3.5 | | | | | | | | | |
| 3.6 | | | | | | | | | |
| 3.7 | | | | | | | | | |
| 3.8 | | | | | | | | | |
| 3.9 | | | | | | | | | |
| 3.10 | | | | | | | | | |
| 3.11 | | | | | | | | | |
| 3.12 | | | | | | | | | |
| 3.13 | | | | | | | | | |
| 3.14 | | | | | | | | | |
| 3.15 | | | | | | | | | |
| 3.16 | | | | | | | | | |
