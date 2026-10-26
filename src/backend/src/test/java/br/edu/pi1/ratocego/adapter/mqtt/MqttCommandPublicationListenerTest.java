package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.service.MqttCommandPublicationRequested;
import br.edu.pi1.ratocego.service.RunCommandPublicationException;
import br.edu.pi1.ratocego.service.port.MessagePublisher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MqttCommandPublicationListenerTest {

    @Test
    void publishesRequestedCommand() throws Exception {
        MessagePublisher publisher = mock(MessagePublisher.class);
        MqttCommandPublicationListener listener = new MqttCommandPublicationListener(publisher);
        MqttCommandPublicationRequested event = new MqttCommandPublicationRequested("runs/command", "start");

        listener.publish(event);

        verify(publisher).publish("runs/command", "start");
    }

    @Test
    void wrapsPublicationFailures() throws Exception {
        MessagePublisher publisher = mock(MessagePublisher.class);
        IllegalStateException cause = new IllegalStateException("broker unavailable");
        doThrow(cause).when(publisher).publish("runs/command", "start");
        MqttCommandPublicationListener listener = new MqttCommandPublicationListener(publisher);

        RunCommandPublicationException exception = assertThrows(RunCommandPublicationException.class,
                () -> listener.publish(new MqttCommandPublicationRequested("runs/command", "start")));

        assertEquals("Could not publish run command", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
