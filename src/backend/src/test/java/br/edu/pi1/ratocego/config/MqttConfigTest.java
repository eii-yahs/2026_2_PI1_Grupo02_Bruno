package br.edu.pi1.ratocego.config;

import br.edu.pi1.ratocego.adapter.mqtt.ManagedMqttConnection;
import org.junit.jupiter.api.Test;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
}