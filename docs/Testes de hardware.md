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

- **Encoders (Odometria):** A leitura de posição é realizada de forma assíncrona por meio de interrupções de hardware, configuradas com `attachInterrupt(..., RISING)`. A cada borda de subida detectada no canal A, as funções `isrE()` e `isrD()` incrementam ou decrementam os contadores de *ticks*, conforme o estado do canal B.

  Essa abordagem reduz a possibilidade de perda de pulsos durante a execução de outras tarefas, embora não garanta a captura de todos os pulsos em qualquer condição de operação.

  ```cpp
  attachInterrupt(ENC_E_A, isrE, RISING);
  attachInterrupt(ENC_D_A, isrD, RISING);
  ```

- **Monitoramento de Energia:** O módulo INA219 fornece os dados de tensão e corrente por meio do barramento I²C, utilizando as funções da biblioteca `Adafruit_INA219`.

  ```cpp
  ina219.getBusVoltage_V();
  ina219.getCurrent_mA();
  ```

### 2.2. Validação e Calibração das Leituras

A calibração do sistema é definida por constantes no código, cujos valores devem ser ajustados por meio de testes experimentais para relacionar as leituras dos sensores às grandezas físicas.

#### Calibração de Distância

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

O labirinto possui células de **18 × 18 cm**, sendo necessário calibrar essas constantes conforme o diâmetro das rodas, a resolução dos encoders e as dimensões físicas do robô.

#### Calibração de Paredes

A detecção de obstáculos utiliza um limiar aplicado às leituras analógicas dos sensores infravermelhos.

```cpp
const int LIMITE_PAREDE = 1500;
```

Considerando a resolução padrão de 12 bits do ADC da ESP32, as leituras variam nominalmente entre **0 e 4095**.

Quando a leitura ultrapassa o limiar definido, o sistema considera que existe uma parede próxima ao sensor.

O valor de `1500` é um parâmetro inicial, que deve ser validado experimentalmente conforme a distância de detecção desejada, as características dos sensores e as condições de iluminação.

### 2.3. Definição das Taxas de Amostragem e Volume de Dados

O sistema utiliza duas frequências de operação distintas: uma destinada à leitura e ao controle local e outra à atualização da interface de telemetria.

#### Amostragem de Controle (Alta Frequência)

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

#### Transmissão de Telemetria (Baixa Frequência)

A atualização da interface web é realizada por requisições HTTP assíncronas (*polling*).

O navegador solicita os dados ao servidor da ESP32 a cada **200 milissegundos**, correspondendo a uma frequência nominal de **5 Hz**.

```javascript
setInterval(async () => {
  const j = await (await fetch('/dados')).json();
}, 200);
```

Essa estratégia permite acompanhar o estado dos sensores e do robô sem exigir uma transmissão contínua de dados.

#### Quantidade de Dados

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

### 2.4. Validação das Taxas Definidas

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

### 2.5. Processamento Básico de Dados (Tratamento de Sinais)

O código aplica operações de processamento digital aos sinais adquiridos, transformando as leituras em informações utilizadas pela lógica de navegação.

#### Limiarização (*Thresholding*)

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

#### Tratamento dos Encoders com `labs()`

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

#### Média Móvel (Implementação Futura)

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

## 🎥 Demonstração do Protótipo

O vídeo a seguir apresenta o estágio atual de desenvolvimento do **Micromouse**, demonstrando o funcionamento geral do protótipo e a integração dos sistemas implementados até o momento.

A demonstração tem como objetivo registrar o progresso do projeto e apresentar os resultados preliminares dos testes realizados.

**Vídeo demonstrativo:**

<

https://github.com/user-attachments/assets/1e8d90ab-a7ef-4162-961d-9c9f254efaf5

>





> 3. Armazenamento (memória interna, cartão SD, pendrive etc.):
>    - Armazenar dados básicos;
>    - Armazenar e validar dados de sensores.
> 4. Atuadores (Motores DC, relés, LEDs etc.):
>    - Realizar acionamentos básicos;
>    - Realizar acionamentos com base em dados de sensores;
>    - Realizar processamento básico de dados (PWM etc.).
> 5. Comunicações (UART, I2C, SPI, USB, WiFi, Bluetooth etc.):
>    - Transmissão básica de dados;
>    - Transmissão de dados de sensores dentro da taxa de amostragem.

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
