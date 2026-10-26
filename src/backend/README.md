# Backend

API do Rato Cego, inicializada pelo [Spring Initializr](https://start.spring.io/) com Java 21, Maven e Spring Boot 4.1.1.

## Requisitos

- JDK 21 ou superior
- Docker Compose (para iniciar PostgreSQL e Mosquitto em containers)
- Maven Wrapper incluído (`./mvnw`)

## Executar

```bash
./mvnw spring-boot:run
```

Por padrão, a aplicação usa o banco `ratocego`, usuário e senha `ratocego`. Configure `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` para usar outros valores. A configuração MQTT fica nas variáveis `MQTT_SERVER_URI`, `MQTT_CLIENT_ID` e `MQTT_TOPIC_PREFIX`.

O backend integra PostgreSQL por Spring Data JPA, recebe eventos MQTT de ciclo de execução e telemetria, valida os payloads, persiste execuções e amostras, calcula consumo e publica atualizações STOMP/WebSocket. O estado da conexão MQTT também é exposto pelo Actuator. O projeto inclui Spring Boot 4.1.1, Spring Integration MQTT com Eclipse Paho, validação, WebSocket, JPA, Actuator, driver PostgreSQL e DevTools (opcional para desenvolvimento).

## Rodar PostgreSQL e Mosquitto no Docker

Na pasta `src/backend`, configure as credenciais do broker no arquivo `.env`:

```dotenv
MQTT_USERNAME=ratocego
MQTT_PASSWORD=troque-por-uma-senha
```

Inicie apenas os serviços de infraestrutura. Assim, o Spring pode continuar rodando localmente com hot reload:

```bash
docker compose up -d database mosquitto
```

O PostgreSQL ficará disponível em `localhost:5432` (banco/usuário/senha padrão `ratocego`) e o broker em `localhost:1883`. Para usar outros dados no banco, defina `DATABASE_NAME`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` e, se necessário, `POSTGRES_PORT` no `.env`. Para acompanhar as mensagens, abra outro terminal em `src/backend` e assine os tópicos do projeto:

```bash
docker compose exec -T mosquitto sh -c 'mosquitto_sub -h localhost -u "$MQTT_USERNAME" -P "$MQTT_PASSWORD" -t "ratocego/#" -v'
```

Em outro terminal, publique os payloads de exemplo. O script usa `mosquitto_pub` dentro do container e as credenciais do `.env`:

```bash
./scripts/publish-mqtt-example.sh started
./scripts/publish-mqtt-example.sh telemetry
./scripts/publish-mqtt-example.sh finished
```

Também há o exemplo `interrupted`; escolha `finished` ou `interrupted` para demonstrar o resultado da execução. Os JSONs estão em `examples/mqtt` e podem ser editados antes da publicação. Eles usam `runId: 1`, que também aparece no tópico. Se mudar esse valor, atualize o tópico correspondente em `scripts/publish-mqtt-example.sh`.

Os campos `runId` e `eventId` dos exemplos são números inteiros, sem aspas.

O backend cria/atualiza as tabelas JPA localmente (`DDL_AUTO=update`) e grava `startedAt`, `finishedAt`, dimensões do labirinto, amostras e totais calculados. A duração é derivada de `finishedAt - startedAt`. Para a bateria LiPo 2S de 7,4 V nominal, os limites padrão seguem o requisito de 3,3 V por célula: **6,6 V** para entrar em estado `LOW` e **6,8 V** para retornar a `NORMAL`, com histerese. Os valores podem ser sobrescritos por `BATTERY_CRITICAL_VOLTAGE_VOLTS` e `BATTERY_RECOVERY_VOLTAGE_VOLTS`; se ambas estiverem vazias, `batteryStatus` será `null`.

O backend recebe `run.started`, `run.finished` e `run.interrupted` em listeners MQTT de ciclo de vida, e `telemetry.sample` em um listener dedicado. As amostras são associadas ao `runId`, descartadas quando duplicadas ou fora de sequência, persistidas com restrições únicas de `eventId` e `(runId, sequence)`, e usadas para atualizar distância, velocidade média, corrente acumulada, potência, energia e estado da bateria. As atualizações são publicadas em `/topic/run-started`, `/topic/telemetry`, `/topic/run-finished` e `/topic/run-interrupted` pela conexão STOMP em `/ws`.

As classes `RunLifecycleService` e `TelemetryService` concentram o processamento. `RunRepository` e `TelemetrySampleRepository` são repositórios Spring Data JPA; `TelemetryPublisher` é a porta de publicação WebSocket e `MessagePublisher` a porta de publicação MQTT. O modelo contém `Run` e `TelemetrySample`; as dimensões do labirinto ficam em `Run`, sem entidade de catálogo `Maze`.

Para parar somente o broker:

```bash
docker compose stop mosquitto
```

O backend pode ser iniciado separadamente pela IDE ou com `./mvnw spring-boot:run`. Para iniciar toda a stack (backend, banco e broker) em Docker, use `docker compose up --build`.

Os pacotes seguem a arquitetura em `config`, `controller`, `dto` (`mqtt` e `websocket`), `mapper`, `model`, `repository`, `service` (`port`), `util` e `adapter` (`mqtt`, `persistence` e `websocket`).
