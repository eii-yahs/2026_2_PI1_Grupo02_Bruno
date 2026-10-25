package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.mqtt.RunFinishedPayload;
import br.edu.pi1.ratocego.dto.mqtt.RunInterruptedPayload;
import br.edu.pi1.ratocego.dto.mqtt.RunStartedPayload;
import br.edu.pi1.ratocego.dto.websocket.RunCommandStatusUpdate;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.repository.RunRepository;
import br.edu.pi1.ratocego.util.MqttPayloadParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import tools.jackson.databind.ObjectMapper;
import br.edu.pi1.ratocego.model.MazeType;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RunLifecycleServiceTest {

    private MqttPayloadParser parser;
    private RunRepository runs;
    private ApplicationEventPublisher events;
    private ObjectMapper objectMapper;
    private RunLifecycleService service;

    @BeforeEach
    void setUp() {
        parser = mock(MqttPayloadParser.class);
        runs = mock(RunRepository.class);
        events = mock(ApplicationEventPublisher.class);
        objectMapper = mock(ObjectMapper.class);
        service = new RunLifecycleService(parser, runs, events, objectMapper, "ratocego/");
    }

    @Test
    void startPersistsRequestPublishesCommandAndAnnouncesStatus() throws Exception {
        when(runs.findByStatusIn(any())).thenReturn(java.util.List.of());
        when(runs.save(any())).thenAnswer(invocation -> {
            Run saved = invocation.getArgument(0);
            saved.setId(17L);
            return saved;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("command-json");
        Run run = service.start(MazeType.GRID_4X4);
        assertEquals(RunStatus.START_REQUESTED, run.getStatus());
        verify(runs).save(org.mockito.ArgumentMatchers.argThat(saved -> saved.getStartRequestedAt() != null
                && saved.getStatus() == RunStatus.START_REQUESTED));
        var command = org.mockito.ArgumentCaptor.forClass(MqttCommandPublicationRequested.class);
        verify(events).publishEvent(command.capture());
        assertEquals("ratocego/commands/run/start", command.getValue().topic());
        assertEquals("command-json", command.getValue().payload());
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void startTimeoutMarksRequestFailed() {
        Run run = activeRun(Instant.now().minusSeconds(6), RunStatus.START_REQUESTED);
        run.setStartRequestedAt(Instant.now().minusSeconds(6));
        when(runs.findByStatusIn(any())).thenReturn(java.util.List.of(run));
        service.processCommandTimeouts();
        assertEquals(RunStatus.FAILED, run.getStatus());
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void interruptPublishesCommandAndMovesRunToPending() throws Exception {
        Run run = activeRun(Instant.now().minusSeconds(1), RunStatus.IN_PROGRESS);
        when(runs.findById(42L)).thenReturn(Optional.of(run));
        when(objectMapper.writeValueAsString(any())).thenReturn("interrupt-json");

        Run updated = service.interrupt(42L);

        assertEquals(RunStatus.INTERRUPT_REQUESTED, updated.getStatus());
        assertTrue(updated.getInterruptRequestedAt() != null);
        var command = org.mockito.ArgumentCaptor.forClass(MqttCommandPublicationRequested.class);
        verify(events).publishEvent(command.capture());
        assertEquals("ratocego/commands/run/interrupt", command.getValue().topic());
        assertEquals("interrupt-json", command.getValue().payload());
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void interruptTimeoutKeepsRunPendingAndAllowsRetry() {
        Run run = activeRun(Instant.now().minusSeconds(5), RunStatus.INTERRUPT_REQUESTED);
        run.setInterruptRequestedAt(Instant.now().minusSeconds(3));
        when(runs.findByStatusIn(any())).thenReturn(java.util.List.of(run));

        service.processCommandTimeouts();

        assertEquals(RunStatus.INTERRUPT_REQUESTED, run.getStatus());
        assertTrue(run.isInterruptTimeoutWarned());
        var event = org.mockito.ArgumentCaptor.forClass(WebSocketUpdateEvent.class);
        verify(events).publishEvent(event.capture());
        assertTrue(((RunCommandStatusUpdate) event.getValue().update()).retryAllowed());
    }

    @Test
    void lateStartConfirmationMarksRunFailed() {
        Instant requestedAt = Instant.now().minusSeconds(6);
        Run run = activeRun(null, RunStatus.START_REQUESTED);
        run.setStartRequestedAt(requestedAt);
        run.setMazeRows(4);
        run.setMazeColumns(4);
        when(parser.parseAndValidate("json", RunStartedPayload.class))
                .thenReturn(new RunStartedPayload(1, 42, Instant.now(), 4, 4));
        when(runs.findById(42L)).thenReturn(Optional.of(run));

        service.processStarted("json");

        assertEquals(RunStatus.FAILED, run.getStatus());
        verify(runs).save(run);
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void unsolicitedStartedEventIsIgnored() {
        Instant startedAt = Instant.parse("2026-10-09T12:00:00Z");
        when(parser.parseAndValidate("json", RunStartedPayload.class))
                .thenReturn(new RunStartedPayload(1, 42, startedAt, 4, 4));
        when(runs.findById(42L)).thenReturn(Optional.empty());

        service.processStarted("json");

        verify(runs, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void duplicateStartedEventDoesNotPublishAnotherUpdate() {
        Instant startedAt = Instant.parse("2026-10-09T12:00:00Z");
        Run run = activeRun(startedAt, RunStatus.IN_PROGRESS);
        when(parser.parseAndValidate("json", RunStartedPayload.class))
                .thenReturn(new RunStartedPayload(1, 42, startedAt, 4, 4));
        when(runs.findById(42L)).thenReturn(Optional.of(run));

        service.processStarted("json");

        verify(runs, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void finishedEventTransitionsRunAndPublishesAfterPersistenceRequest() {
        Instant startedAt = Instant.parse("2026-10-09T12:00:00Z");
        Instant finishedAt = startedAt.plusSeconds(5);
        Run run = activeRun(startedAt, RunStatus.INTERRUPT_REQUESTED);
        when(parser.parseAndValidate("json", RunFinishedPayload.class))
                .thenReturn(new RunFinishedPayload(2, 42, finishedAt, RunStatus.COMPLETED, true));
        when(runs.findById(42L)).thenReturn(Optional.of(run));

        service.processFinished("json");

        assertEquals(RunStatus.COMPLETED, run.getStatus());
        assertEquals(finishedAt, run.getFinishedAt());
        verify(runs).save(run);
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void interruptedEventRequiresAnOutstandingInterruptionRequest() {
        Instant startedAt = Instant.parse("2026-10-09T12:00:00Z");
        Run run = activeRun(startedAt, RunStatus.IN_PROGRESS);
        when(parser.parseAndValidate("json", RunInterruptedPayload.class))
                .thenReturn(new RunInterruptedPayload(2, 42, startedAt.plusSeconds(1)));
        when(runs.findById(42L)).thenReturn(Optional.of(run));

        assertThrows(IllegalStateException.class, () -> service.processInterrupted("json"));

        assertEquals(RunStatus.IN_PROGRESS, run.getStatus());
        verify(runs, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void interruptedEventCompletesRequestedTransition() {
        Instant startedAt = Instant.parse("2026-10-09T12:00:00Z");
        Instant interruptedAt = startedAt.plusSeconds(2);
        Run run = activeRun(startedAt, RunStatus.INTERRUPT_REQUESTED);
        when(parser.parseAndValidate("json", RunInterruptedPayload.class))
                .thenReturn(new RunInterruptedPayload(2, 42, interruptedAt));
        when(runs.findById(42L)).thenReturn(Optional.of(run));

        service.processInterrupted("json");

        assertEquals(RunStatus.INTERRUPTED, run.getStatus());
        assertEquals(interruptedAt, run.getFinishedAt());
        verify(runs).save(run);
        verify(events).publishEvent(any(WebSocketUpdateEvent.class));
    }

    @Test
    void rejectsFinishTimestampBeforeLatestSample() {
        Instant startedAt = Instant.parse("2026-10-09T12:00:00Z");
        Run run = activeRun(startedAt, RunStatus.IN_PROGRESS);
        run.setLastSampleAt(startedAt.plusSeconds(4));
        when(parser.parseAndValidate("json", RunFinishedPayload.class))
                .thenReturn(new RunFinishedPayload(2, 42, startedAt.plusSeconds(3), RunStatus.COMPLETED, true));
        when(runs.findById(42L)).thenReturn(Optional.of(run));

        assertThrows(IllegalArgumentException.class, () -> service.processFinished("json"));
        verify(runs, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejectsRunFinishedWithInterruptedStatus() {
        when(parser.parseAndValidate("json", RunFinishedPayload.class))
                .thenReturn(new RunFinishedPayload(2, 42, Instant.now(), RunStatus.INTERRUPTED, null));

        assertThrows(IllegalArgumentException.class, () -> service.processFinished("json"));
        verify(runs, never()).findById(42L);
    }

    @Test
    void terminalRunCannotBeChangedByAnotherFinishedEvent() {
        Instant startedAt = Instant.parse("2026-10-09T12:00:00Z");
        Run run = activeRun(startedAt, RunStatus.COMPLETED);
        when(parser.parseAndValidate("json", RunFinishedPayload.class))
                .thenReturn(new RunFinishedPayload(2, 42, startedAt.plusSeconds(5), RunStatus.COMPLETED, true));
        when(runs.findById(42L)).thenReturn(Optional.of(run));

        assertThrows(IllegalStateException.class, () -> service.processFinished("json"));
        assertEquals(RunStatus.COMPLETED, run.getStatus());
        verify(runs, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    private Run activeRun(Instant startedAt, RunStatus status) {
        Run run = new Run();
        run.setId(42L);
        run.setStatus(status);
        run.setStartedAt(startedAt);
        return run;
    }
}
