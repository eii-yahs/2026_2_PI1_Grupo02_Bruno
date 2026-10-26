package br.edu.pi1.ratocego.dto.mqtt;

import br.edu.pi1.ratocego.model.RunStatus;

import java.time.Instant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RunFinishedPayload(
        @Positive long eventId,
        @Positive long runId,
        @NotNull Instant timestamp,
        @NotNull RunStatus status,
        Boolean challengeCompleted) {
}
