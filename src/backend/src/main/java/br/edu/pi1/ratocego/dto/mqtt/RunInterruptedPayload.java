package br.edu.pi1.ratocego.dto.mqtt;

import java.time.Instant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RunInterruptedPayload(
        @Positive long eventId,
        @Positive long runId,
        @NotNull Instant timestamp) {
}
