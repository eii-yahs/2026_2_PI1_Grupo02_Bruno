package br.edu.pi1.ratocego.mapper;

import br.edu.pi1.ratocego.dto.mqtt.TelemetrySamplePayload;
import br.edu.pi1.ratocego.model.Heading;
import br.edu.pi1.ratocego.model.Position;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TelemetryMapperTest {

    @Test
    void mapsSamplePayloadToWebSocketUpdate() {
        var timestamp = Instant.parse("2026-10-09T12:00:00Z");
        var position = new Position(2, 3, Heading.WEST);
        var sample = new TelemetrySamplePayload(7, 5, 4, timestamp, position,
                new BigDecimal("2.5"), new BigDecimal("0.4"), new BigDecimal("7.2"), new BigDecimal("120"));

        var update = new TelemetryMapperImpl().toTelemetryUpdate(sample);

        assertEquals(5, update.runId());
        assertEquals(4, update.sequence());
        assertEquals(timestamp, update.timestamp());
        assertEquals(position, update.position());
        assertEquals(new BigDecimal("2.5"), update.distanceTravelledMeters());
        assertEquals(new BigDecimal("0.4"), update.currentSpeedMetersPerSecond());
        assertEquals(new BigDecimal("7.2"), update.batteryVoltageVolts());
        assertEquals(new BigDecimal("120"), update.currentMilliAmps());
    }
}
