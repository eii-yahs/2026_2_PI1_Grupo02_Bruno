package br.edu.pi1.ratocego.adapter.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ManagedMqttConnection
        implements SmartLifecycle, DisposableBean {

    @FunctionalInterface
    private interface MqttClientFactory {
        MqttAsyncClient create(MqttActivity activity) throws MqttException;
    }

    private static final Logger log =
            LoggerFactory.getLogger(ManagedMqttConnection.class);

    private final MqttAsyncClient client;
    private final MqttConnectOptions options;
    private final MqttReconnectPolicy reconnectPolicy;
    private final MqttActivity activity;
    private final Duration silenceTimeout;
    private final Map<String, MessageChannel> routes;
    private final long completionTimeoutMs;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(task -> {
                Thread thread = new Thread(
                        task,
                        "mqtt-connection-supervisor"
                );
                thread.setDaemon(true);
                return thread;
            });

    private volatile boolean running;
    private volatile boolean subscribed;

    private boolean connecting;
    private int attemptIndex;

    private ScheduledFuture<?> reconnectTask;
    private ScheduledFuture<?> watchdogTask;

    public ManagedMqttConnection(
            String brokerUri,
            String clientId,
            MqttConnectOptions options,
            Duration silenceTimeout,
            long completionTimeoutMs,
            Map<String, MessageChannel> routes
    ) throws MqttException {

        this(activity -> new MqttAsyncClient(
                        brokerUri,
                        clientId,
                        new MemoryPersistence(),
                        new TrackingMqttPingSender(activity)),
                options,
                silenceTimeout,
                completionTimeoutMs,
                routes);
    }

    ManagedMqttConnection(
            MqttAsyncClient client,
            MqttConnectOptions options,
            Duration silenceTimeout,
            long completionTimeoutMs,
            Map<String, MessageChannel> routes
    ) throws MqttException {
        this(activity -> client, options, silenceTimeout, completionTimeoutMs, routes);
    }

    private ManagedMqttConnection(
            MqttClientFactory clientFactory,
            MqttConnectOptions options,
            Duration silenceTimeout,
            long completionTimeoutMs,
            Map<String, MessageChannel> routes
    ) throws MqttException {

        this.options = options;
        this.silenceTimeout = silenceTimeout;
        this.completionTimeoutMs = completionTimeoutMs;
        this.routes = Map.copyOf(routes);

        this.reconnectPolicy = new MqttReconnectPolicy();
        this.activity = new MqttActivity();

        this.client = clientFactory.create(activity);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                log.warn(
                        "MQTT conexão perdida: clientId={}, motivo={}",
                        client.getClientId(),
                        cause == null
                                ? "não informado"
                                : cause.getMessage()
                );

                connectionFailed();
            }

            @Override
            public void messageArrived(
                    String topic,
                    MqttMessage message
            ) {
                activity.receivedControlPacket();

                dispatchMessage(topic, message);
            }

            @Override
            public void deliveryComplete(
                    IMqttDeliveryToken token
            ) {
                // As publicações desta implementação usam QoS 1.
                // A conclusão corresponde à confirmação PUBACK.
                activity.receivedControlPacket();
            }
        });
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }

        running = true;
        subscribed = false;
        attemptIndex = 0;

        watchdogTask = scheduler.scheduleAtFixedRate(
                this::checkSilence,
                100,
                100,
                TimeUnit.MILLISECONDS
        );

        // A conexão inicial é imediata.
        scheduleConnection(0);
    }

    private synchronized void scheduleConnection(
            long delaySeconds
    ) {
        if (!running || connecting) {
            return;
        }

        if (reconnectTask != null
                && !reconnectTask.isDone()) {
            return;
        }

        log.info(
                "MQTT tentativa agendada: clientId={}, espera={}s",
                client.getClientId(),
                delaySeconds
        );

        reconnectTask = scheduler.schedule(
                this::connect,
                delaySeconds,
                TimeUnit.SECONDS
        );
    }

    private void connect() {
        synchronized (this) {
            reconnectTask = null;

            if (!running || connecting || client.isConnected()) {
                return;
            }

            connecting = true;
        }

        log.info(
                "MQTT iniciando tentativa: clientId={}",
                client.getClientId()
        );

        try {
            client.connect(
                    options,
                    null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken token) {
                            connectionEstablished();
                        }

                        @Override
                        public void onFailure(
                                IMqttToken token,
                                Throwable exception
                        ) {
                            log.warn(
                                    "MQTT tentativa falhou: {}",
                                    exception.getMessage()
                            );

                            connectionFailed();
                        }
                    }
            );
        } catch (MqttException exception) {
            log.warn(
                    "MQTT erro ao iniciar conexão",
                    exception
            );

            connectionFailed();
        }
    }

    private void connectionEstablished() {
        synchronized (this) {
            connecting = false;

            if (!running) {
                return;
            }

            attemptIndex = 0;
            activity.receivedControlPacket();
        }

        log.info(
                "MQTT conectado: clientId={}",
                client.getClientId()
        );

        subscribe();
    }

    private void subscribe() {
        String[] topics = routes.keySet().toArray(String[]::new);
        int[] qos = new int[topics.length];

        java.util.Arrays.fill(qos, 1);

        try {
            client.subscribe(
                    topics,
                    qos,
                    null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken token) {
                            activity.receivedControlPacket();

                            subscribed =
                                    running && client.isConnected();

                            log.info(
                                    "MQTT assinaturas restauradas: {}",
                                    (Object) topics
                            );
                        }

                        @Override
                        public void onFailure(
                                IMqttToken token,
                                Throwable exception
                        ) {
                            log.warn(
                                    "MQTT falha nas assinaturas",
                                    exception
                            );

                            disconnectAndRetry();
                        }
                    }
            );
        } catch (MqttException exception) {
            log.warn(
                    "MQTT erro ao assinar tópicos",
                    exception
            );

            disconnectAndRetry();
        }
    }

    private synchronized void connectionFailed() {
        connecting = false;
        subscribed = false;

        if (!running) {
            return;
        }

        if (reconnectTask != null
                && !reconnectTask.isDone()) {
            return;
        }

        long delaySeconds =
                reconnectPolicy.delaySeconds(attemptIndex);

        if (attemptIndex < 5) {
            attemptIndex++;
        }

        scheduleConnection(delaySeconds);
    }

    private void checkSilence() {
        try {
            if (!running || !client.isConnected()) {
                return;
            }

            if (activity.isExpired(silenceTimeout)) {
                log.warn(
                        "MQTT timeout: clientId={}, limite={}s",
                        client.getClientId(),
                        silenceTimeout.toSeconds()
                );

                disconnectAndRetry();
            }
        } catch (RuntimeException exception) {
            // Impede que uma exceção encerre permanentemente
            // a execução periódica do watchdog.
            log.error(
                    "MQTT falha na verificação de atividade",
                    exception
            );
        }
    }

    private void disconnectAndRetry() {
        subscribed = false;

        forceDisconnect();
        connectionFailed();
    }

    private void forceDisconnect() {
        try {
            client.disconnectForcibly(
                    0,
                    0,
                    false
            );
        } catch (MqttException exception) {
            log.debug(
                    "MQTT conexão já encerrada ou indisponível",
                    exception
            );
        }
    }

    private void dispatchMessage(
            String topic,
            MqttMessage mqttMessage
    ) {
        routes.forEach((filter, channel) -> {
            if (!MqttTopic.isMatched(filter, topic)) {
                return;
            }

            String payload = new String(
                    mqttMessage.getPayload(),
                    StandardCharsets.UTF_8
            );

            boolean sent = channel.send(
                    MessageBuilder.withPayload(payload)
                            .setHeader(
                                    MqttHeaders.RECEIVED_TOPIC,
                                    topic
                            )
                            .setHeader(
                                    MqttHeaders.RECEIVED_QOS,
                                    mqttMessage.getQos()
                            )
                            .build()
            );

            if (!sent) {
                throw new IllegalStateException(
                        "Não foi possível encaminhar MQTT: " + topic
                );
            }
        });
    }

    public void publish(
            String topic,
            String payload
    ) throws MqttException {
        if (!isConnected()) {
            throw new IllegalStateException(
                    "MQTT indisponível para publicação"
            );
        }

        MqttMessage message = new MqttMessage(
                payload.getBytes(StandardCharsets.UTF_8)
        );

        message.setQos(1);

        client.publish(topic, message)
                .waitForCompletion(completionTimeoutMs);
    }

    public boolean isConnected() {
        return running
                && subscribed
                && client.isConnected();
    }

    @Override
    public void stop() {
        synchronized (this) {
            running = false;
            subscribed = false;
            connecting = false;

            if (reconnectTask != null) {
                reconnectTask.cancel(false);
                reconnectTask = null;
            }

            if (watchdogTask != null) {
                watchdogTask.cancel(false);
                watchdogTask = null;
            }
        }

        forceDisconnect();

        log.info("MQTT gerenciamento encerrado");
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        stop();
        scheduler.shutdownNow();
        client.close(true);
    }
}
