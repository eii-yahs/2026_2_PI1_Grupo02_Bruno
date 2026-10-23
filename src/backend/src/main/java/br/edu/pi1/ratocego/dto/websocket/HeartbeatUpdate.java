package br.edu.pi1.ratocego.dto.websocket;
 
import lombok.Builder;

import java.time.Instant;

/** Heartbeat de aplicação enviado pelo servidor a cada 2 s (RNF31). */
@Builder
public record HeartbeatUpdate(
        long sequence,
        Instant timestamp
) {
}