package br.edu.pi1.ratocego.dto.mqtt;

import br.edu.pi1.ratocego.model.Position;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record TelemetrySamplePayload(
        @Positive long eventId,
        @Positive long runId,
        @Positive long sequence,
        @NotNull Instant timestamp,
        @NotNull @Valid Position position,
        @NotNull @DecimalMin("0.0") BigDecimal distanceTravelledMeters,
        @NotNull @DecimalMin("0.0") BigDecimal currentSpeedMetersPerSecond,
        @NotNull @DecimalMin("0.0") BigDecimal batteryVoltageVolts,
        @NotNull @DecimalMin("0.0") BigDecimal currentMilliAmps) {
}
