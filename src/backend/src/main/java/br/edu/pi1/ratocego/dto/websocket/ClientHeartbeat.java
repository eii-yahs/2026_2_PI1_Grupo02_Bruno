package br.edu.pi1.ratocego.dto.websocket;
 
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/** Heartbeat de aplicação enviado pelo frontend ao destino STOMP /app/heartbeat (RNF31). */
public record ClientHeartbeat(@NotNull Instant timestamp) {
}