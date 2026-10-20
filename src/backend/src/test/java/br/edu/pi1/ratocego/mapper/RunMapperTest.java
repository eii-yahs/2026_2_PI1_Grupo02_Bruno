package br.edu.pi1.ratocego.mapper;

import br.edu.pi1.ratocego.model.Heading;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.model.TelemetrySample;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RunMapperTest {

    private final RunMapper mapper = new RunMapperImpl();

    @Test
    void mapsFinishedRunToResponseWithDerivedDuration() {
        var startedAt = Instant.parse("2026-10-09T14:00:00Z");
        var finishedAt = Instant.parse("2026-10-09T14:00:30Z");
        var run = new Run();
        run.setId(1L);
        run.setStatus(RunStatus.COMPLETED);
        run.setStartedAt(startedAt);
        run.setFinishedAt(finishedAt);
        run.setMazeRows(4);
        run.setMazeColumns(8);
        run.setChallengeCompleted(true);
        run.setDistanceTravelledMeters(new BigDecimal("2.16"));
        run.setAverageSpeedMetersPerSecond(new BigDecimal("0.072"));
        run.setChargeConsumedMilliampHours(new BigDecimal("12.5"));
        run.setEnergyConsumedWattHours(new BigDecimal("0.09"));

        var response = mapper.toRunResponse(run);

        assertEquals(1L, response.id());
        assertEquals(RunStatus.COMPLETED, response.status());
        assertEquals(startedAt, response.startedAt());
        assertEquals(finishedAt, response.finishedAt());
        assertEquals(Duration.ofSeconds(30), response.duration());
        assertEquals(4, response.mazeRows());
        assertEquals(8, response.mazeColumns());
        assertEquals(true, response.challengeCompleted());
        assertEquals(new BigDecimal("2.16"), response.distanceTravelledMeters());
        assertEquals(new BigDecimal("0.072"), response.averageSpeedMetersPerSecond());
        assertEquals(new BigDecimal("12.5"), response.chargeConsumedMilliampHours());
        assertEquals(new BigDecimal("0.09"), response.energyConsumedWattHours());
    }

    @Test
    void leavesDurationEmptyWhileRunHasNotFinished() {
        var run = new Run();
        run.setId(2L);
        run.setStatus(RunStatus.IN_PROGRESS);
        run.setStartedAt(Instant.parse("2026-10-09T14:00:00Z"));

        var response = mapper.toRunResponse(run);

        assertNull(response.finishedAt());
        assertNull(response.duration());
        assertNull(response.challengeCompleted());
    }

    @Test
    void leavesDurationEmptyWhenRunNeverStarted() {
        var run = new Run();
        run.setId(3L);
        run.setStatus(RunStatus.FAILED);
        run.setFinishedAt(Instant.parse("2026-10-09T14:00:05Z"));

        assertNull(mapper.toRunResponse(run).duration());
    }

    @Test
    void mapsStoredTelemetrySampleToResponseWithItsPosition() {
        var recordedAt = Instant.parse("2026-10-09T14:00:01Z");
        var sample = new TelemetrySample();
        sample.setSequenceNumber(7L);
        sample.setRecordedAt(recordedAt);
        sample.setPositionRow(2);
        sample.setPositionColumn(3);
        sample.setHeading(Heading.SOUTH);
        sample.setDistanceTravelledMeters(new BigDecimal("1.75"));
        sample.setCurrentSpeedMetersPerSecond(new BigDecimal("0.4"));
        sample.setBatteryVoltageVolts(new BigDecimal("7.3"));
        sample.setCurrentMilliAmps(new BigDecimal("250"));

        var response = mapper.toTelemetrySampleResponse(sample);

        assertEquals(7L, response.sequence());
        assertEquals(recordedAt, response.recordedAt());
        assertEquals(2, response.row());
        assertEquals(3, response.column());
        assertEquals(Heading.SOUTH, response.heading());
        assertEquals(new BigDecimal("1.75"), response.distanceTravelledMeters());
        assertEquals(new BigDecimal("0.4"), response.currentSpeedMetersPerSecond());
        assertEquals(new BigDecimal("7.3"), response.batteryVoltageVolts());
        assertEquals(new BigDecimal("250"), response.currentMilliAmps());
    }
}
