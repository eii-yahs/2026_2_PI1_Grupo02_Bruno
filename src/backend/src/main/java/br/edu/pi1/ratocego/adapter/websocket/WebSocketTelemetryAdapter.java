package br.edu.pi1.ratocego.adapter.websocket;

import br.edu.pi1.ratocego.service.port.TelemetryPublisher;
import br.edu.pi1.ratocego.dto.websocket.RunFinishedUpdate;
import br.edu.pi1.ratocego.dto.websocket.HeartbeatUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunInterruptedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunStartedUpdate;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketTelemetryAdapter implements TelemetryPublisher {

    private static final String RUN_STARTED_DESTINATION = "/topic/run-started";
    private static final String TELEMETRY_DESTINATION = "/topic/telemetry";
    private static final String RUN_FINISHED_DESTINATION = "/topic/run-finished";
    private static final String RUN_INTERRUPTED_DESTINATION = "/topic/run-interrupted";

    private static final String HEARTBEAT_DESTINATION = "/topic/heartbeat";

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketTelemetryAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publishRunStarted(RunStartedUpdate update) {
        messagingTemplate.convertAndSend(RUN_STARTED_DESTINATION, update);
    }

    @Override
    public void publishTelemetry(TelemetryUpdate update) {
        messagingTemplate.convertAndSend(TELEMETRY_DESTINATION, update);
    }

    @Override
    public void publishRunFinished(RunFinishedUpdate update) {
        messagingTemplate.convertAndSend(RUN_FINISHED_DESTINATION, update);
    }

    @Override
    public void publishRunInterrupted(RunInterruptedUpdate update) {
        messagingTemplate.convertAndSend(RUN_INTERRUPTED_DESTINATION, update);
    }

    @Override
    public void publishHeartbeat(HeartbeatUpdate update) {
        messagingTemplate.convertAndSend(HEARTBEAT_DESTINATION, update);
    }
}
