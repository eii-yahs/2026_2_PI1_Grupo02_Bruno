package br.edu.pi1.ratocego.dto.rest;

import br.edu.pi1.ratocego.model.Heading;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record TelemetrySampleResponse(
        long sequence,
        Instant recordedAt,
        int row,
        int column,
        Heading heading,
        BigDecimal distanceTravelledMeters,
        BigDecimal currentSpeedMetersPerSecond,
        BigDecimal batteryVoltageVolts,
        BigDecimal currentMilliAmps
) {
}
