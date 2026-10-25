package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.dto.rest.TelemetrySampleResponse;
import br.edu.pi1.ratocego.mapper.RunMapper;
import br.edu.pi1.ratocego.model.MazeType;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.repository.RunRepository;
import br.edu.pi1.ratocego.repository.TelemetrySampleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RunQueryService {

    private static final Sort OLDEST_FIRST = Sort.by(Sort.Direction.ASC, "id");
    // Os três tipos de labirinto (4×4, 8×4, 12×4) têm 4 linhas; muda só o número de colunas
    private static final int MAZE_ROWS = 4;

    private final RunRepository runRepository;
    private final TelemetrySampleRepository telemetrySampleRepository;
    private final RunMapper runMapper;

    public RunQueryService(RunRepository runRepository, TelemetrySampleRepository telemetrySampleRepository,
                           RunMapper runMapper) {
        this.runRepository = runRepository;
        this.telemetrySampleRepository = telemetrySampleRepository;
        this.runMapper = runMapper;
    }

    @Transactional(readOnly = true)
    public List<RunResponse> findAllRuns() {
        return runRepository.findAll(OLDEST_FIRST).stream()
                .map(runMapper::toRunResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<RunResponse> findActiveRun() {
        return runRepository.findFirstByStatusInOrderByIdDesc(
                        List.of(RunStatus.START_REQUESTED, RunStatus.IN_PROGRESS, RunStatus.INTERRUPT_REQUESTED))
                .map(runMapper::toRunResponse);
    }

    // Execuções de um tipo de labirinto, identificado pelas dimensões gravadas na execução
    @Transactional(readOnly = true)
    public List<RunResponse> findRunsByMaze(MazeType mazeType) {
        return runRepository.findByMazeRowsAndMazeColumns(MAZE_ROWS, mazeColumns(mazeType), OLDEST_FIRST).stream()
                .map(runMapper::toRunResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<RunResponse> findRunById(long runId) {
        return runRepository.findById(runId).map(runMapper::toRunResponse);
    }

    // Amostras na ordem em que foram enviadas (sequence); o trajeto é a sequência das posições.
    // Vazio quando a execução não existe, para diferenciar de uma execução sem amostras.
    @Transactional(readOnly = true)
    public Optional<List<TelemetrySampleResponse>> findRunTelemetry(long runId) {
        if (!runRepository.existsById(runId)) {
            return Optional.empty();
        }
        return Optional.of(telemetrySampleRepository.findByRun_IdOrderBySequenceNumberAsc(runId).stream()
                .map(runMapper::toTelemetrySampleResponse)
                .toList());
    }

    // Mesmo padrão do simulador e do frontend: GRID_8X4 = 4 linhas e 8 colunas
    private static int mazeColumns(MazeType mazeType) {
        return switch (mazeType) {
            case GRID_4X4 -> 4;
            case GRID_8X4 -> 8;
            case GRID_12X4 -> 12;
        };
    }
}
