package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.service.RunLifecycleService;
import br.edu.pi1.ratocego.service.TelemetryService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MqttListenersTest {

    @Test
    void forwardsRunLifecyclePayloadsToLifecycleService() {
        RunLifecycleService service =
                mock(RunLifecycleService.class);

        MqttRunListener listener = new MqttRunListener(service);

        listener.onRunStarted("started");
        listener.onRunFinished("finished");
        listener.onRunInterrupted("interrupted");

        verify(service).processStarted("started");
        verify(service).processFinished("finished");
        verify(service).processInterrupted("interrupted");
    }

    @Test
    void forwardsTelemetryPayloadToTelemetryService() {
        TelemetryService service =
                mock(TelemetryService.class);

        new MqttTelemetryListener(service)
                .onTelemetrySample("sample");

        verify(service).processSample("sample");
    }
}