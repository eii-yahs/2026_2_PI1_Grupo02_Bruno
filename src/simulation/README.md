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
