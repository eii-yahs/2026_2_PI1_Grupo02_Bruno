package br.edu.pi1.ratocego.controller;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.service.RunQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/runs")
public class RunController {

    private final RunQueryService runQueryService;

    public RunController(RunQueryService runQueryService) {
        this.runQueryService = runQueryService;
    }

    @GetMapping
    public List<RunResponse> getAllRuns() {
        return runQueryService.findAllRuns();
    }

    @GetMapping("/{runId}")
    public ResponseEntity<RunResponse> getRunById(@PathVariable long runId) {
        return ResponseEntity.of(runQueryService.findRunById(runId));
    }
}
