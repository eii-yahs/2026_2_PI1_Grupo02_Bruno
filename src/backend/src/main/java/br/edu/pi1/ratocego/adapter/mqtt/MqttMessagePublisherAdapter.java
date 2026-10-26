package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.service.port.MessagePublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MqttMessagePublisherAdapter implements MessagePublisher {

    private final MessageChannel mqttOutboundChannel;

    public MqttMessagePublisherAdapter(@Qualifier("mqttOutboundChannel") MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    @Override
    public void publish(String topic, String payload) {
        var message = MessageBuilder.withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();

        if (!mqttOutboundChannel.send(message)) {
            throw new IllegalStateException("Could not publish MQTT message to topic " + topic);
        }
    }
}
