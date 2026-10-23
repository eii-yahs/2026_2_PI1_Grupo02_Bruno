package br.edu.pi1.ratocego.controller;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.dto.rest.TelemetrySampleResponse;
import br.edu.pi1.ratocego.model.MazeType;
import br.edu.pi1.ratocego.service.RunQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/runs")
public class RunController {

    private final RunQueryService runQueryService;

    public RunController(RunQueryService runQueryService) {
        this.runQueryService = runQueryService;
    }

    // Sem mazeType: consulta geral (HU51). Com mazeType: consulta por labirinto (HU50)
    @GetMapping
    public List<RunResponse> getRuns(@RequestParam(required = false) MazeType mazeType) {
        return mazeType == null ? runQueryService.findAllRuns() : runQueryService.findRunsByMaze(mazeType);
    }

    @GetMapping("/{runId}")
    public ResponseEntity<RunResponse> getRunById(@PathVariable long runId) {
        return ResponseEntity.of(runQueryService.findRunById(runId));
    }

    @GetMapping("/{runId}/telemetry")
    public ResponseEntity<List<TelemetrySampleResponse>> getRunTelemetry(@PathVariable long runId) {
        return ResponseEntity.of(runQueryService.findRunTelemetry(runId));
    }
}
