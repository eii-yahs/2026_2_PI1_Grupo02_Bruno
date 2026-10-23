package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.mapper.RunMapperImpl;
import br.edu.pi1.ratocego.model.Heading;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.model.TelemetrySample;
import br.edu.pi1.ratocego.repository.RunRepository;
import br.edu.pi1.ratocego.repository.TelemetrySampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RunQueryServiceTest {

    private static final Sort OLDEST_FIRST = Sort.by(Sort.Direction.ASC, "id");

    private RunRepository runs;
    private TelemetrySampleRepository samples;
    private RunQueryService service;

    @BeforeEach
    void setUp() {
        runs = mock(RunRepository.class);
        samples = mock(TelemetrySampleRepository.class);
        service = new RunQueryService(runs, samples, new RunMapperImpl());
    }

    @Test
    void findAllRunsReturnsRunsFromEveryMazeInAttemptOrder() {
        when(runs.findAll(OLDEST_FIRST)).thenReturn(List.of(
                run(1L, RunStatus.FAILED, 4, 4),
                run(2L, RunStatus.COMPLETED, 4, 8),
                run(3L, RunStatus.INTERRUPTED, 4, 12)));

        var responses = service.findAllRuns();

        verify(runs).findAll(OLDEST_FIRST);
        assertEquals(List.of(1L, 2L, 3L), responses.stream().map(response -> response.id()).toList());
        assertEquals(List.of(4, 8, 12), responses.stream().map(response -> response.mazeColumns()).toList());
        assertEquals(RunStatus.INTERRUPTED, responses.getLast().status());
    }

    @Test
    void findAllRunsReturnsEmptyListWhenNothingWasStored() {
        when(runs.findAll(OLDEST_FIRST)).thenReturn(List.of());

        assertTrue(service.findAllRuns().isEmpty());
    }

    @Test
    void findRunByIdReturnsTheRequestedRunWithItsMaze() {
        when(runs.findById(2L)).thenReturn(Optional.of(run(2L, RunStatus.COMPLETED, 4, 8)));

        var response = service.findRunById(2L).orElseThrow();

        assertEquals(2L, response.id());
        assertEquals(RunStatus.COMPLETED, response.status());
        assertEquals(4, response.mazeRows());
        assertEquals(8, response.mazeColumns());
    }

    @Test
    void findRunByIdReturnsEmptyWhenRunDoesNotExist() {
        when(runs.findById(99L)).thenReturn(Optional.empty());

        assertFalse(service.findRunById(99L).isPresent());
    }

    @Test
    void findRunTelemetryReturnsSamplesOfTheRunInSequenceOrder() {
        var run = run(2L, RunStatus.COMPLETED, 4, 8);
        when(runs.existsById(2L)).thenReturn(true);
        when(samples.findByRun_IdOrderBySequenceNumberAsc(2L)).thenReturn(List.of(
                sample(run, 1L, 0, 0),
                sample(run, 2L, 0, 1),
                sample(run, 3L, 1, 1)));

        var telemetry = service.findRunTelemetry(2L).orElseThrow();

        assertEquals(List.of(1L, 2L, 3L), telemetry.stream().map(sample -> sample.sequence()).toList());
        assertEquals(List.of(0, 0, 1), telemetry.stream().map(sample -> sample.row()).toList());
        assertEquals(List.of(0, 1, 1), telemetry.stream().map(sample -> sample.column()).toList());
    }

    @Test
    void findRunTelemetryReturnsEmptyListWhenRunHasNoSamples() {
        when(runs.existsById(3L)).thenReturn(true);
        when(samples.findByRun_IdOrderBySequenceNumberAsc(3L)).thenReturn(List.of());

        assertTrue(service.findRunTelemetry(3L).orElseThrow().isEmpty());
    }

    @Test
    void findRunTelemetryReturnsEmptyWhenRunDoesNotExist() {
        when(runs.existsById(99L)).thenReturn(false);

        assertFalse(service.findRunTelemetry(99L).isPresent());
        verify(samples, never()).findByRun_IdOrderBySequenceNumberAsc(99L);
    }

    private static TelemetrySample sample(Run run, long sequence, int row, int column) {
        var sample = new TelemetrySample();
        sample.setRun(run);
        sample.setEventId(100 + sequence);
        sample.setSequenceNumber(sequence);
        sample.setRecordedAt(Instant.parse("2026-10-09T14:00:00Z").plusSeconds(sequence));
        sample.setPositionRow(row);
        sample.setPositionColumn(column);
        sample.setHeading(Heading.EAST);
        sample.setDistanceTravelledMeters(BigDecimal.valueOf(sequence));
        return sample;
    }

    private static Run run(long id, RunStatus status, int rows, int columns) {
        var run = new Run();
        run.setId(id);
        run.setStatus(status);
        run.setMazeRows(rows);
        run.setMazeColumns(columns);
        return run;
    }
}
