# Simulador Micromouse via MQTT

O simulador se conecta ao Mosquitto e escuta `ratocego/commands/run/start` e `ratocego/commands/run/interrupt`. Ao receber um início, confirma as dimensões do labirinto, percorre as células em zigue-zague, envia telemetria a cada 100 ms e publica `run.finished`. Se receber uma interrupção para a execução ativa, publica `run.interrupted`.

Os valores de posição, velocidade, distância, tensão e corrente são gerados pelo simulador para exercitar o fluxo completo do backend; não representam sensores físicos.

## Preparar

Inicie o broker e o backend seguindo [`../backend/README.md`](../backend/README.md). No ambiente local, o simulador lê `MQTT_USERNAME` e `MQTT_PASSWORD` de `../backend/.env` automaticamente. Também é possível fornecer as configurações por variáveis de ambiente ou argumentos.

Instale a dependência e execute:

```bash
cd src/simulation
python -m venv .venv
```

Ative o ambiente virtual antes de instalar/executar, usando o comando do seu shell:

```bash
source .venv/bin/activate
pip install -r requirements.txt
python simulator.py
```

```fish
source .venv/bin/activate.fish
pip install -r requirements.txt
python simulator.py
```

Para iniciar uma demonstração sem a interface, use `--start GRID_8X4`. O simulador publica um comando `run.start` de teste no broker e recebe esse mesmo comando pela inscrição MQTT:

```bash
python simulator.py --start GRID_8X4
```

O comando `--start` existe apenas para demonstração. No fluxo integrado, a aplicação publica `run.start` quando o operador inicia a execução.

## Configuração

```bash
python simulator.py --host localhost --port 1883 --topic-prefix ratocego
```

Variáveis aceitas: `MQTT_HOST`, `MQTT_PORT`, `MQTT_USERNAME`, `MQTT_PASSWORD` e `MQTT_TOPIC_PREFIX`. O intervalo padrão é 100 ms (10 Hz); ele pode ser ajustado com `--sample-interval`. `--speed` controla a velocidade simulada e `--cell-length` a distância entre as células.

O simulador aceita os tipos `GRID_4X4`, `GRID_8X4` e `GRID_12X4`. As dimensões publicadas seguem linhas × colunas: 4×4, 4×8 e 4×12.

## HU24 — Identificação do objetivo (#116)

O TAP (Descrição do Problema) e o projeto conceitual de hardware
(Microcontrolador) definem o objetivo no canto diagonalmente oposto à partida.
O simulador parte de (3, 0), canto inferior esquerdo. Linhas aumentam de cima
para baixo e colunas da esquerda para a direita; índices começam em zero.

| Labirinto | Linhas × colunas | Objetivo |
| --- | --- | --- |
| 4×4 | 4×4 | (0, 3) |
| 8×4 | 4×8 | (0, 7) |
| 12×4 | 4×12 | (0, 11) |

A comparação é por célula. Células físicas medem 18 cm; o padrão existente de
`--cell-length` é 0.18 m. A HU24 não altera argumentos da simulação. O firmware
deverá converter a localização física para células usando essa escala e origem.

### Implementação

- `navigation.py`: `goal_cell`, `is_goal_reached` e `GoalDetector`.
- `simulator.py`: cria um detector novo por tentativa e chama `identify_goal`
  depois de atualizar a posição.
- `tests/test_navigation.py`: regras, entradas inválidas e estado.
- `tests/test_goal_detection.py`: integração, sinalização única e novas tentativas.

`GoalDetector.update(position)` retorna `True` somente na primeira chegada.
`detector.reached` mantém a identificação mesmo após sair do alvo.
Cada tentativa cria um detector novo em `run["goalDetector"]`.
A HU32 (#124) poderá consumir o retorno de `identify_goal` dentro do loop ou
consultar esse estado para realizar o encerramento automático.

Este pacote acrescenta identificação. Mantém o encerramento original no fim
da rota, que ainda não utiliza a identificação da HU24. A integração da chegada
com a parada, timeout e falhas fica para a HU32. Não cria eventos MQTT nem muda
payloads, tópicos, conexão, reconexão ou publicação do resultado final existente.

### Testes automatizados

Com o ambiente Python ativado, em `src/simulation`:

```bash
pip install -r requirements.txt
python -m unittest discover -s tests -p 'test_navigation.py' -v
python -m unittest discover -s tests -p 'test_goal_detection.py' -v
```

São nove testes novos. Usam relógio e publicador em memória, sem broker ou
esperas reais. Para rodar todos os testes existentes na pasta:

```bash
python -m unittest discover -s tests -v
```

### Teste funcional

Com backend e broker ativos:

```bash
python simulator.py | tee hu24-identificacao.log
```

Inicie cada tamanho pela interface, uma tentativa de cada vez. Deve aparecer
uma única identificação por tentativa, por exemplo:

```text
[run 42] objetivo identificado: labirinto=GRID_8X4 posicao=(0, 7) alvo=(0, 7)
```

Verifique tamanho, posição e identificador da tentativa. O log unitário com
publicador simulado não comprova entrega MQTT real. No percurso original,
a identificação pode aparecer antes do encerramento: conectá-la à parada é HU32.

### Robô físico

A main enviada contém apenas README em `src/firmware`. Este pacote identifica
o objetivo no simulador; não implementa sensores, localização ou motores no
ESP32. O firmware deverá integrar a regra à posição real para validar no robô.
