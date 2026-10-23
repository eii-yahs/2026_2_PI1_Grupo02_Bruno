package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.dto.rest.TelemetrySampleResponse;
import br.edu.pi1.ratocego.mapper.RunMapper;
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
}
