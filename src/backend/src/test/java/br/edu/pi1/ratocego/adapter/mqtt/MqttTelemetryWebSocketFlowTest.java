package br.edu.pi1.ratocego.adapter.mqtt;

import br.edu.pi1.ratocego.adapter.websocket.WebSocketTelemetryAdapter;
import br.edu.pi1.ratocego.adapter.websocket.WebSocketUpdateEventListener;
import br.edu.pi1.ratocego.dto.mqtt.TelemetrySamplePayload;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import br.edu.pi1.ratocego.mapper.TelemetryMapper;
import br.edu.pi1.ratocego.model.Heading;
import br.edu.pi1.ratocego.model.Position;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.repository.RunRepository;
import br.edu.pi1.ratocego.repository.TelemetrySampleRepository;
import br.edu.pi1.ratocego.service.TelemetryService;
import br.edu.pi1.ratocego.service.WebSocketUpdateEvent;
import br.edu.pi1.ratocego.service.port.TelemetryPublisher;
import br.edu.pi1.ratocego.util.MqttPayloadParser;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Exercises the backend MQTT-to-WebSocket path without a live broker or database. */
class MqttTelemetryWebSocketFlowTest {

    @Test
    void forwardsAcceptedMqttSampleAsWebSocketTelemetryWithItsValues() {
        var mapper = mock(TelemetryMapper.class);
        var parser = mock(MqttPayloadParser.class);
        var runs = mock(RunRepository.class);
        var samples = mock(TelemetrySampleRepository.class);
        var eventPublisher = mock(ApplicationEventPublisher.class);
        var messaging = mock(SimpMessagingTemplate.class);
        TelemetryPublisher websocketPublisher = new WebSocketTelemetryAdapter(messaging);
        var eventListener = new WebSocketUpdateEventListener(websocketPublisher);

        Instant generatedAt = Instant.parse("2026-10-09T12:00:01Z");
        var sample = new TelemetrySamplePayload(11, 7, 1, generatedAt,
                new Position(1, 2, Heading.EAST), new BigDecimal("1.25"),
                new BigDecimal("0.5"), new BigDecimal("7.4"), new BigDecimal("100"));
        var baseUpdate = TelemetryUpdate.builder()
                .runId(sample.runId())
                .sequence(sample.sequence())
                .timestamp(sample.timestamp())
                .position(sample.position())
                .distanceTravelledMeters(sample.distanceTravelledMeters())
                .currentSpeedMetersPerSecond(sample.currentSpeedMetersPerSecond())
                .batteryVoltageVolts(sample.batteryVoltageVolts())
                .currentMilliAmps(sample.currentMilliAmps())
                .build();
        var run = new Run();
        run.setId(sample.runId());
        run.setStatus(RunStatus.IN_PROGRESS);
        run.setStartedAt(generatedAt.minusSeconds(1));
        run.setMazeRows(4);
        run.setMazeColumns(4);
        run.setDistanceTravelledMeters(BigDecimal.ZERO);
        run.setLastSequence(0);
        when(parser.parseAndValidate("mqtt-json", TelemetrySamplePayload.class)).thenReturn(sample);
        when(samples.existsByEventId(sample.eventId())).thenReturn(false);
        when(samples.existsByRun_IdAndSequenceNumber(sample.runId(), sample.sequence())).thenReturn(false);
        when(runs.findById(sample.runId())).thenReturn(Optional.of(run));
        when(mapper.toTelemetryUpdate(sample)).thenReturn(baseUpdate);

        var telemetryService = new TelemetryService(mapper, parser, runs, samples, eventPublisher, "6.6", "6.8");
        var mqttListener = new MqttTelemetryListener(telemetryService);

        mqttListener.onTelemetrySample("mqtt-json");

        var event = org.mockito.ArgumentCaptor.forClass(WebSocketUpdateEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        eventListener.onUpdate(event.getValue());

        verify(messaging).convertAndSend(org.mockito.ArgumentMatchers.eq("/topic/telemetry"),
                org.mockito.ArgumentMatchers.<Object>argThat(message -> message instanceof TelemetryUpdate update
                        && update.runId() == sample.runId()
                        && update.sequence() == sample.sequence()
                        && update.timestamp().equals(generatedAt)
                        && update.position().equals(sample.position())
                        && update.distanceTravelledMeters().compareTo(sample.distanceTravelledMeters()) == 0
                        && update.currentSpeedMetersPerSecond().compareTo(sample.currentSpeedMetersPerSecond()) == 0
                        && update.batteryVoltageVolts().compareTo(sample.batteryVoltageVolts()) == 0
                        && update.currentMilliAmps().compareTo(sample.currentMilliAmps()) == 0
                        && update.averageSpeedMetersPerSecond().compareTo(new BigDecimal("1.25")) == 0
                        && update.currentPowerWatts().compareTo(new BigDecimal("0.74")) == 0));
        verify(samples).save(any());
        verify(runs).save(run);
    }
}
