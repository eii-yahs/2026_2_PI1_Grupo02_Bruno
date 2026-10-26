package br.edu.pi1.ratocego.adapter.websocket;
 
import br.edu.pi1.ratocego.dto.websocket.HeartbeatUpdate;
import br.edu.pi1.ratocego.service.port.TelemetryPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Envia o heartbeat de aplicação a cada 2 s (RNF31). O frontend considera a conexão
 * indisponível após 6 s sem receber um heartbeat válido.
 */
@Component
public class WebSocketHeartbeatPublisher {

    private final TelemetryPublisher telemetryPublisher;
    private final AtomicLong sequence = new AtomicLong();

    public WebSocketHeartbeatPublisher(TelemetryPublisher telemetryPublisher) {
        this.telemetryPublisher = telemetryPublisher;
    }

    @Scheduled(fixedRateString = "${app.websocket.heartbeat-interval-ms:2000}")
    public void publishHeartbeat() {
        telemetryPublisher.publishHeartbeat(HeartbeatUpdate.builder()
                .sequence(sequence.incrementAndGet())
                .timestamp(Instant.now())
                .build());
    }
}