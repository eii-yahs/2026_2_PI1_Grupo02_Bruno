package br.edu.pi1.ratocego.dto.rest;

import br.edu.pi1.ratocego.model.RunStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Builder
public record RunResponse(
        long id,
        RunStatus status,
        Instant startedAt,
        Instant finishedAt,
        Duration duration,
        Integer mazeRows,
        Integer mazeColumns,
        Boolean challengeCompleted,
        BigDecimal distanceTravelledMeters,
        BigDecimal averageSpeedMetersPerSecond,
        BigDecimal chargeConsumedMilliampHours,
        BigDecimal energyConsumedWattHours,
        Instant interruptRequestedAt
) {
}
