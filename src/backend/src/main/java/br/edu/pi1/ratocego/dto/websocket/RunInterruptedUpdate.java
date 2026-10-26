package br.edu.pi1.ratocego.dto.websocket;

import br.edu.pi1.ratocego.model.RunStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record RunInterruptedUpdate(
        long runId,
        Instant timestamp,
        RunStatus status
) implements WebSocketUpdate {
}
