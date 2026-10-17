package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.mapper.RunMapperImpl;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.repository.RunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RunQueryServiceTest {

    private static final Sort OLDEST_FIRST = Sort.by(Sort.Direction.ASC, "id");

    private RunRepository runs;
    private RunQueryService service;

    @BeforeEach
    void setUp() {
        runs = mock(RunRepository.class);
        service = new RunQueryService(runs, new RunMapperImpl());
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

    private static Run run(long id, RunStatus status, int rows, int columns) {
        var run = new Run();
        run.setId(id);
        run.setStatus(status);
        run.setMazeRows(rows);
        run.setMazeColumns(columns);
        return run;
    }
}
