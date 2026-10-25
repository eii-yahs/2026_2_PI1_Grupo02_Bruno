package br.edu.pi1.ratocego.config;

import org.junit.jupiter.api.Test;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MqttConfigTest {

    @Test
    void createsMqttFactoryChannelsInboundAdaptersAndOutboundHandler() {
        MqttConfig config = new MqttConfig();
        ReflectionTestUtils.setField(config, "clientId", "backend");
        ReflectionTestUtils.setField(config, "topicPrefix", "ratocego");
        ReflectionTestUtils.setField(config, "brokerUri", "tcp://localhost:1883");
        ReflectionTestUtils.setField(config, "qos", 1);
        MqttPahoClientFactory factory = config.mqttClientFactory("tcp://localhost:1883", "user", "secret", 7, 10, true);
        var telemetryChannel = config.mqttTelemetryInputChannel();
        var startedChannel = config.mqttRunStartedInputChannel();
        var finishedChannel = config.mqttRunFinishedInputChannel();
        var interruptedChannel = config.mqttRunInterruptedInputChannel();
        var outboundChannel = config.mqttOutboundChannel();

        assertNotNull(factory.getConnectionOptions());
        assertArrayEquals(new String[]{"tcp://localhost:1883"}, factory.getConnectionOptions().getServerURIs());
        assertEquals(10, factory.getConnectionOptions().getKeepAliveInterval());
        assertTrue(factory.getConnectionOptions().isAutomaticReconnect());
        assertInstanceOf(DirectChannel.class, telemetryChannel);
        assertInstanceOf(DirectChannel.class, startedChannel);
        assertInstanceOf(DirectChannel.class, finishedChannel);
        assertInstanceOf(DirectChannel.class, interruptedChannel);
        assertInstanceOf(DirectChannel.class, outboundChannel);
        var telemetryInbound = config.mqttTelemetryInbound(factory, telemetryChannel);
        var startedInbound = config.mqttRunStartedInbound(factory, startedChannel);
        var finishedInbound = config.mqttRunFinishedInbound(factory, finishedChannel);
        var interruptedInbound = config.mqttRunInterruptedInbound(factory, interruptedChannel);
        var telemetryAdapter = assertInstanceOf(MqttPahoMessageDrivenChannelAdapter.class, telemetryInbound);
        var startedAdapter = assertInstanceOf(MqttPahoMessageDrivenChannelAdapter.class, startedInbound);
        var finishedAdapter = assertInstanceOf(MqttPahoMessageDrivenChannelAdapter.class, finishedInbound);
        var interruptedAdapter = assertInstanceOf(MqttPahoMessageDrivenChannelAdapter.class, interruptedInbound);
        assertArrayEquals(new String[]{"ratocego/runs/+/telemetry"}, telemetryAdapter.getTopic());
        assertArrayEquals(new String[]{"ratocego/runs/+/started"}, startedAdapter.getTopic());
        assertArrayEquals(new String[]{"ratocego/runs/+/finished"}, finishedAdapter.getTopic());
        assertArrayEquals(new String[]{"ratocego/runs/+/interrupted"}, interruptedAdapter.getTopic());
        MessageHandler handler = config.mqttOutbound(factory, "backend-out", 5000, 1);
        assertNotNull(handler);
    }
}
