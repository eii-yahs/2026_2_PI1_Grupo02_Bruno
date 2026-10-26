package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.mqtt.TelemetrySamplePayload;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import br.edu.pi1.ratocego.mapper.TelemetryMapper;
import br.edu.pi1.ratocego.model.Heading;
import br.edu.pi1.ratocego.model.Position;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.repository.RunRepository;
import br.edu.pi1.ratocego.repository.TelemetrySampleRepository;
import br.edu.pi1.ratocego.util.MqttPayloadParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TelemetryServiceTest {

    private TelemetryMapper mapper;
    private MqttPayloadParser parser;
    private RunRepository runs;
    private TelemetrySampleRepository samples;
    private ApplicationEventPublisher events;
    private TelemetryService service;

    @BeforeEach
    void setUp() {
        mapper = mock(TelemetryMapper.class);
        parser = mock(MqttPayloadParser.class);
        runs = mock(RunRepository.class);
        samples = mock(TelemetrySampleRepository.class);
        events = mock(ApplicationEventPublisher.class);
        service = new TelemetryService(mapper, parser, runs, samples, events, "6.6", "6.8");
    }

    @Test
    void persistsAndPublishesValidSampleWithCalculatedFields() {
        Run run = activeRun();
        var payload = payload(1, 1, 1, "2026-10-09T12:00:01Z", "6.5", "100", "1.0");
        when(parser.parseAndValidate("json", TelemetrySamplePayload.class)).thenReturn(payload);
        when(samples.existsByEventId(1)).thenReturn(false);
        when(samples.existsByRun_IdAndSequenceNumber(1, 1)).thenReturn(false);
        when(runs.findById(1L)).thenReturn(Optional.of(run));
        when(mapper.toTelemetryUpdate(payload)).thenReturn(TelemetryUpdate.builder()
                .runId(1).sequence(1).timestamp(payload.timestamp()).position(payload.position()).build());

        service.processSample("json");

        assertEquals(0, new BigDecimal("0.65").compareTo(run.getLastPowerWatts()));
        assertEquals(0, new BigDecimal("1.0").compareTo(run.getDistanceTravelledMeters()));
        assertEquals(0, BigDecimal.ONE.compareTo(run.getAverageSpeedMetersPerSecond()));
        assertEquals(br.edu.pi1.ratocego.model.BatteryStatus.LOW, run.getBatteryStatus());
        verify(samples).save(any());
        verify(runs).save(run);
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void integratesChargeAndEnergyBetweenSamples() {
        Run run = activeRun();
        run.setLastSequence(1);
        run.setLastSampleAt(Instant.parse("2026-10-09T12:00:01Z"));
        run.setLastCurrentMilliAmps(new BigDecimal("100"));
        run.setLastPowerWatts(new BigDecimal("0.65"));
        run.setBatteryStatus(br.edu.pi1.ratocego.model.BatteryStatus.LOW);
        var payload = payload(2, 1, 2, "2026-10-09T12:00:02Z", "6.7", "200", "2.0");
        stubAcceptedSample(payload, run);

        service.processSample("json");

        assertEquals(0, new BigDecimal("150").multiply(BigDecimal.ONE)
                .divide(new BigDecimal("3600"), java.math.MathContext.DECIMAL64)
                .compareTo(run.getChargeConsumedMilliampHours()));
        assertEquals(br.edu.pi1.ratocego.model.BatteryStatus.LOW, run.getBatteryStatus(),
                "voltage between thresholds keeps the previous LOW state");
    }

    @Test
    void recoversBatteryOnlyAboveRecoveryThreshold() {
        Run run = activeRun();
        run.setBatteryStatus(br.edu.pi1.ratocego.model.BatteryStatus.LOW);
        var atBoundary = payload(2, 1, 1, "2026-10-09T12:00:01Z", "6.8", "100", "1.0");
        stubAcceptedSample(atBoundary, run);
        service.processSample("json");
        assertEquals(br.edu.pi1.ratocego.model.BatteryStatus.LOW, run.getBatteryStatus());

        var aboveBoundary = payload(3, 1, 2, "2026-10-09T12:00:02Z", "6.81", "100", "1.1");
        stubAcceptedSample(aboveBoundary, run);
        service.processSample("json");
        assertEquals(br.edu.pi1.ratocego.model.BatteryStatus.NORMAL, run.getBatteryStatus());
    }

    @Test
    void criticalThresholdEqualityRemainsNormalAndVoltageBelowItBecomesLow() {
        Run run = activeRun();
        var atBoundary = payload(1, 1, 1, "2026-10-09T12:00:01Z", "6.6", "100", "1.0");
        stubAcceptedSample(atBoundary, run);
        service.processSample("json");
        assertEquals(br.edu.pi1.ratocego.model.BatteryStatus.NORMAL, run.getBatteryStatus());

        var belowBoundary = payload(2, 1, 2, "2026-10-09T12:00:02Z", "6.59", "100", "2.0");
        stubAcceptedSample(belowBoundary, run);
        service.processSample("json");
        assertEquals(br.edu.pi1.ratocego.model.BatteryStatus.LOW, run.getBatteryStatus());
    }

    @Test
    void ignoresDuplicateSampleBeforeLoadingRun() {
        var payload = payload(7, 1, 1, "2026-10-09T12:00:01Z", "7.0", "100", "1.0");
        when(parser.parseAndValidate("json", TelemetrySamplePayload.class)).thenReturn(payload);
        when(samples.existsByEventId(7)).thenReturn(true);

        service.processSample("json");

        verify(runs, never()).findById(1L);
        verify(samples, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejectsInvalidPayloadWithoutSideEffectsAndProcessesTheNextValidSample() {
        Run run = activeRun();
        var payload = payload(1, 1, 1, "2026-10-09T12:00:01Z", "7.0", "100", "1.0");
        when(parser.parseAndValidate("invalid", TelemetrySamplePayload.class))
                .thenThrow(new IllegalArgumentException("Invalid MQTT JSON payload"));
        when(parser.parseAndValidate("valid", TelemetrySamplePayload.class)).thenReturn(payload);
        when(samples.existsByEventId(1)).thenReturn(false);
        when(samples.existsByRun_IdAndSequenceNumber(1, 1)).thenReturn(false);
        when(runs.findById(1L)).thenReturn(Optional.of(run));
        when(mapper.toTelemetryUpdate(payload)).thenReturn(TelemetryUpdate.builder()
                .runId(1).sequence(1).timestamp(payload.timestamp()).position(payload.position()).build());

        assertThrows(IllegalArgumentException.class, () -> service.processSample("invalid"));
        verify(samples, never()).save(any());
        verify(events, never()).publishEvent(any());

        service.processSample("valid");

        verify(samples).save(any());
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void ignoresOutOfOrderSampleWithoutPublishingIt() {
        Run run = activeRun();
        run.setLastSequence(2);
        run.setLastSampleAt(Instant.parse("2026-10-09T12:00:02Z"));
        var payload = payload(3, 1, 1, "2026-10-09T12:00:03Z", "7.0", "100", "1.0");
        stubAcceptedSample(payload, run);

        service.processSample("json");

        verify(samples, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejectsTelemetryForNonRunningExecution() {
        Run run = activeRun();
        run.setStatus(RunStatus.INTERRUPT_REQUESTED);
        var payload = payload(1, 1, 1, "2026-10-09T12:00:01Z", "7.0", "100", "1.0");
        stubAcceptedSample(payload, run);

        assertThrows(IllegalStateException.class, () -> service.processSample("json"));
        verify(samples, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejectsPositionOutsideConfirmedMaze() {
        Run run = activeRun();
        var payload = new TelemetrySamplePayload(1, 1, 1, Instant.parse("2026-10-09T12:00:01Z"),
                new Position(4, 0, Heading.NORTH), BigDecimal.ONE, BigDecimal.ONE,
                new BigDecimal("7.0"), new BigDecimal("100"));
        stubAcceptedSample(payload, run);

        assertThrows(IllegalArgumentException.class, () -> service.processSample("json"));
        verify(samples, never()).save(any());
    }

    @Test
    void rejectsNonIncreasingTimestamps() {
        Run run = activeRun();
        run.setLastSampleAt(Instant.parse("2026-10-09T12:00:02Z"));
        run.setLastSequence(1);
        var payload = payload(2, 1, 2, "2026-10-09T12:00:02Z", "7.0", "100", "1.0");
        stubAcceptedSample(payload, run);

        assertThrows(IllegalArgumentException.class, () -> service.processSample("json"));
    }

    private void stubAcceptedSample(TelemetrySamplePayload payload, Run run) {
        when(parser.parseAndValidate("json", TelemetrySamplePayload.class)).thenReturn(payload);
        when(samples.existsByEventId(payload.eventId())).thenReturn(false);
        when(samples.existsByRun_IdAndSequenceNumber(payload.runId(), payload.sequence())).thenReturn(false);
        when(runs.findById(payload.runId())).thenReturn(Optional.of(run));
        when(mapper.toTelemetryUpdate(payload)).thenReturn(TelemetryUpdate.builder()
                .runId(payload.runId()).sequence(payload.sequence()).timestamp(payload.timestamp())
                .position(payload.position()).build());
    }

    private Run activeRun() {
        Run run = new Run();
        run.setId(1L);
        run.setStatus(RunStatus.IN_PROGRESS);
        run.setStartedAt(Instant.parse("2026-10-09T12:00:00Z"));
        run.setMazeRows(4);
        run.setMazeColumns(4);
        run.setDistanceTravelledMeters(BigDecimal.ZERO);
        run.setLastSequence(0);
        return run;
    }

    private TelemetrySamplePayload payload(long eventId, long runId, long sequence, String timestamp,
                                           String voltage, String current, String distance) {
        return new TelemetrySamplePayload(eventId, runId, sequence, Instant.parse(timestamp),
                new Position(0, 0, Heading.EAST), new BigDecimal(distance), BigDecimal.ONE,
                new BigDecimal(voltage), new BigDecimal(current));
    }
}
