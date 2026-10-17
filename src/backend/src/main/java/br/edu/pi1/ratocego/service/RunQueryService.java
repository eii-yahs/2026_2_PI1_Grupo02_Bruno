package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.mapper.RunMapper;
import br.edu.pi1.ratocego.repository.RunRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RunQueryService {

    private static final Sort OLDEST_FIRST = Sort.by(Sort.Direction.ASC, "id");

    private final RunRepository runRepository;
    private final RunMapper runMapper;

    public RunQueryService(RunRepository runRepository, RunMapper runMapper) {
        this.runRepository = runRepository;
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
}
