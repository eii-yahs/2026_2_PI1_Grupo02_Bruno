package br.edu.pi1.ratocego.adapter.websocket;
 
import br.edu.pi1.ratocego.dto.websocket.ClientHeartbeat;
import br.edu.pi1.ratocego.dto.websocket.HeartbeatUpdate;
import br.edu.pi1.ratocego.service.port.TelemetryPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebSocketHeartbeatTest {

    @Test
    void publishesIncreasingHeartbeatSequence() {
        TelemetryPublisher publisher = mock(TelemetryPublisher.class);
        var heartbeatPublisher = new WebSocketHeartbeatPublisher(publisher);

        heartbeatPublisher.publishHeartbeat();
        heartbeatPublisher.publishHeartbeat();

        var captor = ArgumentCaptor.forClass(HeartbeatUpdate.class);
        verify(publisher, times(2)).publishHeartbeat(captor.capture());
        assertEquals(1, captor.getAllValues().get(0).sequence());
        assertEquals(2, captor.getAllValues().get(1).sequence());
        assertNotNull(captor.getAllValues().get(0).timestamp());
    }

    @Test
    void adapterSendsHeartbeatToItsStompDestination() {
        SimpMessagingTemplate messaging = mock(SimpMessagingTemplate.class);
        var update = HeartbeatUpdate.builder().sequence(1).timestamp(Instant.now()).build();

        new WebSocketTelemetryAdapter(messaging).publishHeartbeat(update);

        verify(messaging).convertAndSend("/topic/heartbeat", update);
    }

    @Test
    void recordsOnlyValidClientHeartbeats() {
        var listener = new WebSocketHeartbeatListener();

        listener.onClientHeartbeat(new ClientHeartbeat(null));
        assertNull(listener.lastReceivedAt());

        listener.onClientHeartbeat(new ClientHeartbeat(Instant.now()));
        assertNotNull(listener.lastReceivedAt());
    }
}