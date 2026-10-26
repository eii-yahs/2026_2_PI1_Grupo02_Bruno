package br.edu.pi1.ratocego.adapter.websocket;

import br.edu.pi1.ratocego.dto.websocket.RunFinishedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunInterruptedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunStartedUpdate;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.service.WebSocketUpdateEvent;
import br.edu.pi1.ratocego.service.port.TelemetryPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebSocketUpdateEventListenerTest {

    private TelemetryPublisher publisher;
    private WebSocketUpdateEventListener listener;

    @BeforeEach
    void setUp() {
        publisher = mock(TelemetryPublisher.class);
        listener = new WebSocketUpdateEventListener(publisher);
    }

    @Test
    void dispatchesEachSupportedUpdateTypeToItsPortMethod() {
        var started = RunStartedUpdate.builder().runId(1).timestamp(java.time.Instant.now())
                .mazeRows(4).mazeColumns(4).status(RunStatus.IN_PROGRESS).build();
        var telemetry = TelemetryUpdate.builder().runId(1).sequence(1).build();
        var finished = RunFinishedUpdate.builder().runId(1).status(RunStatus.COMPLETED).build();
        var interrupted = RunInterruptedUpdate.builder().runId(1).status(RunStatus.INTERRUPTED).build();

        listener.onUpdate(new WebSocketUpdateEvent(started));
        listener.onUpdate(new WebSocketUpdateEvent(telemetry));
        listener.onUpdate(new WebSocketUpdateEvent(finished));
        listener.onUpdate(new WebSocketUpdateEvent(interrupted));

        verify(publisher).publishRunStarted(started);
        verify(publisher).publishTelemetry(telemetry);
        verify(publisher).publishRunFinished(finished);
        verify(publisher).publishRunInterrupted(interrupted);
    }

}
