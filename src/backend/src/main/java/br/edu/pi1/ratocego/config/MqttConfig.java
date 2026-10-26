package br.edu.pi1.ratocego.config;

import br.edu.pi1.ratocego.adapter.mqtt.ManagedMqttConnection;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableIntegration
public class MqttConfig {

    @Bean
    MessageChannel mqttTelemetryInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel mqttRunStartedInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel mqttRunFinishedInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel mqttRunInterruptedInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    ManagedMqttConnection managedMqttConnection(
            @Value("${app.mqtt.server-uri}") String brokerUri,
            @Value("${app.mqtt.client-id}") String clientId,
            @Value("${app.mqtt.topic-prefix}") String topicPrefix,
            @Value("${app.mqtt.username}") String username,
            @Value("${app.mqtt.password}") String password,
            @Value("${app.mqtt.connection-timeout-seconds:10}")
            int connectionTimeoutSeconds,
            @Value("${app.mqtt.keep-alive-seconds:10}")
            int keepAliveSeconds,
            @Value("${app.mqtt.silence-timeout-seconds:15}")
            int silenceTimeoutSeconds,
            @Value("${app.mqtt.publish-completion-timeout-ms:5000}")
            long completionTimeoutMs,
            @Qualifier("mqttTelemetryInputChannel")
            MessageChannel telemetryChannel,
            @Qualifier("mqttRunStartedInputChannel")
            MessageChannel startedChannel,
            @Qualifier("mqttRunFinishedInputChannel")
            MessageChannel finishedChannel,
            @Qualifier("mqttRunInterruptedInputChannel")
            MessageChannel interruptedChannel
    ) throws MqttException {

        if (keepAliveSeconds != 10) {
            throw new IllegalArgumentException(
                    "RNF33 exige Keep Alive de 10 segundos"
            );
        }

        if (silenceTimeoutSeconds != 15) {
            throw new IllegalArgumentException(
                    "RNF33 exige timeout de 15 segundos"
            );
        }

        MqttConnectOptions options =
                new MqttConnectOptions();

        options.setMqttVersion(
                MqttConnectOptions.MQTT_VERSION_3_1_1
        );

        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(connectionTimeoutSeconds);
        options.setKeepAliveInterval(keepAliveSeconds);

        // A reconexão pertence ao ManagedMqttConnection.
        options.setAutomaticReconnect(false);

        // As assinaturas serão restauradas a cada conexão.
        options.setCleanSession(true);

        Map<String, MessageChannel> routes = Map.of(
                topicPrefix + "/runs/+/telemetry",
                telemetryChannel,

                topicPrefix + "/runs/+/started",
                startedChannel,

                topicPrefix + "/runs/+/finished",
                finishedChannel,

                topicPrefix + "/runs/+/interrupted",
                interruptedChannel
        );

        return new ManagedMqttConnection(
                brokerUri,
                clientId,
                options,
                Duration.ofSeconds(silenceTimeoutSeconds),
                completionTimeoutMs,
                routes
        );
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    MessageHandler mqttOutbound(
            ManagedMqttConnection connection
    ) {
        return message -> {
            String topic = Objects.requireNonNull(
                    message.getHeaders().get(
                            MqttHeaders.TOPIC,
                            String.class
                    ),
                    "O tópico MQTT deve ser informado"
            );

            try {
                connection.publish(
                        topic,
                        message.getPayload().toString()
                );
            } catch (MqttException exception) {
                throw new IllegalStateException(
                        "Falha ao publicar no tópico " + topic,
                        exception
                );
            }
        };
    }
}