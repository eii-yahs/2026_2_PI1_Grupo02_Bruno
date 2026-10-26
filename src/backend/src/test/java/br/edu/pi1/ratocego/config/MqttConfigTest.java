package br.edu.pi1.ratocego.config;

import br.edu.pi1.ratocego.adapter.mqtt.ManagedMqttConnection;
import org.junit.jupiter.api.Test;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MqttConfigTest {

    @Test
    void createsDirectChannels() {
        MqttConfig config = new MqttConfig();

        assertInstanceOf(
                DirectChannel.class,
                config.mqttTelemetryInputChannel()
        );

        assertInstanceOf(
                DirectChannel.class,
                config.mqttRunStartedInputChannel()
        );

        assertInstanceOf(
                DirectChannel.class,
                config.mqttRunFinishedInputChannel()
        );

        assertInstanceOf(
                DirectChannel.class,
                config.mqttRunInterruptedInputChannel()
        );

        assertInstanceOf(
                DirectChannel.class,
                config.mqttOutboundChannel()
        );
    }

    @Test
    void forwardsOutboundMessageToConnection() throws Exception {
        MqttConfig config = new MqttConfig();

        ManagedMqttConnection connection =
                mock(ManagedMqttConnection.class);

        MessageHandler handler = config.mqttOutbound(connection);

        handler.handleMessage(
                MessageBuilder.withPayload("{\"command\":\"stop\"}")
                        .setHeader(
                                MqttHeaders.TOPIC,
                                "ratocego/commands"
                        )
                        .build()
        );

        verify(connection).publish(
                "ratocego/commands",
                "{\"command\":\"stop\"}"
        );
    }

    @Test
    void rejectsOutboundMessagesWithoutTopic() {
        MessageHandler handler = new MqttConfig().mqttOutbound(mock(ManagedMqttConnection.class));
        var message = MessageBuilder.withPayload("payload").build();

        assertThrows(NullPointerException.class, () -> handler.handleMessage(message));
    }

    @Test
    void wrapsMqttPublicationFailures() throws Exception {
        ManagedMqttConnection connection = mock(ManagedMqttConnection.class);
        doThrow(new org.eclipse.paho.client.mqttv3.MqttException(1))
                .when(connection).publish(anyString(), anyString());
        MessageHandler handler = new MqttConfig().mqttOutbound(connection);
        var message = MessageBuilder.withPayload("payload")
                .setHeader(MqttHeaders.TOPIC, "ratocego/commands")
                .build();

        var exception = assertThrows(IllegalStateException.class, () -> handler.handleMessage(message));
        assertEquals("Falha ao publicar no tópico ratocego/commands", exception.getMessage());
    }

    @Test
    void enforcesRequiredMqttTimingSettings() {
        MqttConfig config = new MqttConfig();
        DirectChannel channel = new DirectChannel();

        assertThrows(IllegalArgumentException.class, () -> config.managedMqttConnection(
                "tcp://localhost:1883", "test", "ratocego", "user", "password",
                10, 11, 15, 5_000, channel, channel, channel, channel));
        assertThrows(IllegalArgumentException.class, () -> config.managedMqttConnection(
                "tcp://localhost:1883", "test", "ratocego", "user", "password",
                10, 10, 16, 5_000, channel, channel, channel, channel));
    }
}
