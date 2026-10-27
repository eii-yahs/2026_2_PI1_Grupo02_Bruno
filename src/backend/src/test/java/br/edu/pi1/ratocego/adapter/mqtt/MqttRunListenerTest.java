package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.service.RunLifecycleService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MqttRunListenerTest {

    @Test
    void forwardsRunLifecycleEvents() {
        RunLifecycleService service = mock(RunLifecycleService.class);
        MqttRunListener listener = new MqttRunListener(service);

        listener.onRunStarted("started");
        listener.onRunFinished("finished");
        listener.onRunInterrupted("interrupted");

        verify(service).processStarted("started");
        verify(service).processFinished("finished");
        verify(service).processInterrupted("interrupted");
    }

    @Test
    void rethrowsFailureProcessingFinishedEvent() {
        RunLifecycleService service = mock(RunLifecycleService.class);
        RuntimeException cause = new IllegalStateException("invalid event");
        doThrow(cause).when(service).processFinished("finished");
        MqttRunListener listener = new MqttRunListener(service);

        assertSame(cause, assertThrows(RuntimeException.class, () -> listener.onRunFinished("finished")));
    }
}
