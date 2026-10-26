package br.edu.pi1.ratocego.dto.websocket;

import br.edu.pi1.ratocego.model.RunStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record RunFinishedUpdate(
        long runId,
        Instant timestamp,
        RunStatus status,
        Boolean challengeCompleted,
        BigDecimal distanceTravelledMeters,
        BigDecimal averageSpeedMetersPerSecond,
        BigDecimal chargeConsumedMilliampHours,
        BigDecimal energyConsumedWattHours
) implements WebSocketUpdate {
}
