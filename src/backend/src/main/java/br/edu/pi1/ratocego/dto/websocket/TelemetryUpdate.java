package br.edu.pi1.ratocego.dto.websocket;

import br.edu.pi1.ratocego.model.BatteryStatus;
import br.edu.pi1.ratocego.model.Position;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder(toBuilder = true)
public record TelemetryUpdate(
        long runId,
        long sequence,
        Instant timestamp,
        Position position,
        BigDecimal distanceTravelledMeters,
        BigDecimal currentSpeedMetersPerSecond,
        BigDecimal averageSpeedMetersPerSecond,
        BigDecimal batteryVoltageVolts,
        BigDecimal currentMilliAmps,
        BigDecimal currentPowerWatts,
        BatteryStatus batteryStatus,
        BigDecimal chargeConsumedMilliampHours,
        BigDecimal energyConsumedWattHours
) implements WebSocketUpdate {
}
