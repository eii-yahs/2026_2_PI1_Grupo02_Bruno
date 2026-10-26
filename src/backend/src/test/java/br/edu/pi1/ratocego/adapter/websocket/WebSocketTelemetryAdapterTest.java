package br.edu.pi1.ratocego.adapter.websocket;

import br.edu.pi1.ratocego.dto.websocket.RunFinishedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunInterruptedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunStartedUpdate;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebSocketTelemetryAdapterTest {

    @Test
    void sendsEachUpdateToItsStompDestinationWithoutOpeningAConnection() {
        SimpMessagingTemplate messaging = mock(SimpMessagingTemplate.class);
        WebSocketTelemetryAdapter adapter = new WebSocketTelemetryAdapter(messaging);
        var started = RunStartedUpdate.builder().runId(1).timestamp(Instant.now()).mazeRows(4).mazeColumns(4).build();
        var telemetry = TelemetryUpdate.builder().runId(1).sequence(1).build();
        var finished = RunFinishedUpdate.builder().runId(1).build();
        var interrupted = RunInterruptedUpdate.builder().runId(1).build();

        adapter.publishRunStarted(started);
        adapter.publishTelemetry(telemetry);
        adapter.publishRunFinished(finished);
        adapter.publishRunInterrupted(interrupted);

        verify(messaging).convertAndSend("/topic/run-started", started);
        verify(messaging).convertAndSend("/topic/telemetry", telemetry);
        verify(messaging).convertAndSend("/topic/run-finished", finished);
        verify(messaging).convertAndSend("/topic/run-interrupted", interrupted);
    }
}
