package br.edu.pi1.ratocego.controller;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.service.RunQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RunController.class)
class RunControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RunQueryService runQueryService;

    @Test
    void getAllRunsReturnsStoredRunsFromDifferentMazesAsJson() throws Exception {
        when(runQueryService.findAllRuns()).thenReturn(List.of(
                RunResponse.builder()
                        .id(1)
                        .status(RunStatus.COMPLETED)
                        .startedAt(Instant.parse("2026-10-09T14:00:00Z"))
                        .finishedAt(Instant.parse("2026-10-09T14:02:01Z"))
                        .duration(Duration.ofSeconds(121))
                        .mazeRows(4)
                        .mazeColumns(4)
                        .challengeCompleted(true)
                        .distanceTravelledMeters(new BigDecimal("3.8"))
                        .averageSpeedMetersPerSecond(new BigDecimal("0.2"))
                        .chargeConsumedMilliampHours(new BigDecimal("260"))
                        .energyConsumedWattHours(new BigDecimal("1.9"))
                        .build(),
                RunResponse.builder()
                        .id(2)
                        .status(RunStatus.INTERRUPTED)
                        .startedAt(Instant.parse("2026-10-09T15:00:00Z"))
                        .finishedAt(Instant.parse("2026-10-09T15:00:15Z"))
                        .duration(Duration.ofSeconds(15))
                        .mazeRows(4)
                        .mazeColumns(8)
                        .build()));

        mockMvc.perform(get("/api/runs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$[0].startedAt").value("2026-10-09T14:00:00Z"))
                .andExpect(jsonPath("$[0].duration").value("PT2M1S"))
                .andExpect(jsonPath("$[0].mazeRows").value(4))
                .andExpect(jsonPath("$[0].mazeColumns").value(4))
                .andExpect(jsonPath("$[0].challengeCompleted").value(true))
                .andExpect(jsonPath("$[0].chargeConsumedMilliampHours").value(260))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("INTERRUPTED"))
                .andExpect(jsonPath("$[1].mazeColumns").value(8))
                .andExpect(jsonPath("$[1].challengeCompleted").value(nullValue()));
    }

    @Test
    void getAllRunsReturnsEmptyArrayWhenNothingWasStored() throws Exception {
        when(runQueryService.findAllRuns()).thenReturn(List.of());

        mockMvc.perform(get("/api/runs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRunByIdReturnsTheRequestedRun() throws Exception {
        when(runQueryService.findRunById(1L)).thenReturn(Optional.of(RunResponse.builder()
                .id(1)
                .status(RunStatus.COMPLETED)
                .startedAt(Instant.parse("2026-10-09T14:00:00Z"))
                .finishedAt(Instant.parse("2026-10-09T14:02:01Z"))
                .duration(Duration.ofSeconds(121))
                .mazeRows(4)
                .mazeColumns(4)
                .challengeCompleted(true)
                .build()));

        mockMvc.perform(get("/api/runs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.duration").value("PT2M1S"))
                .andExpect(jsonPath("$.mazeRows").value(4))
                .andExpect(jsonPath("$.mazeColumns").value(4))
                .andExpect(jsonPath("$.challengeCompleted").value(true));
    }

    @Test
    void getRunByIdReturnsNotFoundWhenRunDoesNotExist() throws Exception {
        when(runQueryService.findRunById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/runs/99"))
                .andExpect(status().isNotFound());
    }
}
