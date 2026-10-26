package br.edu.pi1.ratocego.adapter.mqtt;

import org.junit.jupiter.api.Test;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MqttMessagePublisherAdapterTest {

    @Test
    void publishesPayloadWithTopicAndQosHeaders() {
        MessageChannel channel = mock(MessageChannel.class);
        when(channel.send(org.mockito.ArgumentMatchers.<Message<?>>any())).thenReturn(true);
        var adapter = new MqttMessagePublisherAdapter(channel);

        adapter.publish("ratocego/commands/run/start", "{\"runId\":1}");

        verify(channel).send(argThat(message -> message.getPayload().equals("{\"runId\":1}")
                && message.getHeaders().get(MqttHeaders.TOPIC).equals("ratocego/commands/run/start")
                && message.getHeaders().get(MqttHeaders.QOS).equals(1)));
    }

    @Test
    void reportsWhenOutboundChannelRejectsTheMessage() {
        MessageChannel channel = mock(MessageChannel.class);
        when(channel.send(org.mockito.ArgumentMatchers.<Message<?>>any())).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> new MqttMessagePublisherAdapter(channel).publish("topic", "payload"));
    }
}
