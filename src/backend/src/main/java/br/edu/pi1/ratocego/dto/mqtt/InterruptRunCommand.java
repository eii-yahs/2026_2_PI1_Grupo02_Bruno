package br.edu.pi1.ratocego.dto.mqtt;

import java.time.Instant;

public record InterruptRunCommand(
        long eventId,
        long runId,
        Instant timestamp) {
}
