package br.edu.pi1.ratocego.adapter.websocket;

import br.edu.pi1.ratocego.dto.websocket.RunFinishedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunInterruptedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunStartedUpdate;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunCommandStatusUpdate;
import br.edu.pi1.ratocego.service.WebSocketUpdateEvent;
import br.edu.pi1.ratocego.service.port.TelemetryPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WebSocketUpdateEventListener {

    private final TelemetryPublisher telemetryPublisher;

    public WebSocketUpdateEventListener(TelemetryPublisher telemetryPublisher) {
        this.telemetryPublisher = telemetryPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdate(WebSocketUpdateEvent event) {
        switch (event.update()) {
            case RunStartedUpdate started -> telemetryPublisher.publishRunStarted(started);
            case TelemetryUpdate telemetry -> telemetryPublisher.publishTelemetry(telemetry);
            case RunFinishedUpdate finished -> telemetryPublisher.publishRunFinished(finished);
            case RunInterruptedUpdate interrupted -> telemetryPublisher.publishRunInterrupted(interrupted);
            case RunCommandStatusUpdate status -> telemetryPublisher.publishRunCommandStatus(status);
        }
    }
}
