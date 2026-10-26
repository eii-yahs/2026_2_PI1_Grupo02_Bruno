package br.edu.pi1.ratocego.dto.websocket;

import br.edu.pi1.ratocego.model.RunStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record RunStartedUpdate(
        long runId,
        Instant timestamp,
        int mazeRows,
        int mazeColumns,
        RunStatus status
) implements WebSocketUpdate {
}
