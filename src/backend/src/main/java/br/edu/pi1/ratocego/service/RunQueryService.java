package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.mapper.RunMapper;
import br.edu.pi1.ratocego.repository.RunRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RunQueryService {

    private static final Sort MOST_RECENT_FIRST = Sort.by(Sort.Direction.DESC, "id");

    private final RunRepository runRepository;
    private final RunMapper runMapper;

    public RunQueryService(RunRepository runRepository, RunMapper runMapper) {
        this.runRepository = runRepository;
        this.runMapper = runMapper;
    }

    @Transactional(readOnly = true)
    public List<RunResponse> findAllRuns() {
        return runRepository.findAll(MOST_RECENT_FIRST).stream()
                .map(runMapper::toRunResponse)
                .toList();
    }
}
