package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.service.MqttCommandPublicationRequested;
import br.edu.pi1.ratocego.service.RunCommandPublicationException;
import br.edu.pi1.ratocego.service.port.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MqttCommandPublicationListener {

    private static final Logger log = LoggerFactory.getLogger(MqttCommandPublicationListener.class);

    private final MessagePublisher messagePublisher;

    public MqttCommandPublicationListener(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(MqttCommandPublicationRequested event) {
        try {
            messagePublisher.publish(event.topic(), event.payload());
        } catch (Exception exception) {
            log.error("Could not publish run command to MQTT topic {}", event.topic(), exception);
            throw new RunCommandPublicationException("Could not publish run command", exception);
        }
    }
}
