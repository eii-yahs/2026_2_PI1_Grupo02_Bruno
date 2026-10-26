package br.edu.pi1.ratocego.adapter.websocket;
 
import br.edu.pi1.ratocego.dto.websocket.ClientHeartbeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/** Recebe o heartbeat enviado pelo frontend em /app/heartbeat (RNF31). */
@Controller
public class WebSocketHeartbeatListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHeartbeatListener.class);

    private final AtomicReference<Instant> lastReceivedAt = new AtomicReference<>();

    @MessageMapping("/heartbeat")
    public void onClientHeartbeat(@Payload ClientHeartbeat heartbeat) {
        if (heartbeat == null || heartbeat.timestamp() == null) {
            log.warn("Ignoring invalid WebSocket heartbeat");
            return;
        }
        lastReceivedAt.set(Instant.now());
    }

    /** Momento do último heartbeat válido recebido de um cliente, ou {@code null} se nenhum chegou. */
    public Instant lastReceivedAt() {
        return lastReceivedAt.get();
    }
}