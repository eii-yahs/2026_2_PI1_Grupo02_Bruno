package br.edu.pi1.ratocego.dto.mqtt;

import br.edu.pi1.ratocego.model.MazeType;

import java.time.Instant;

public record StartRunCommand(
        long eventId,
        long runId,
        MazeType mazeType,
        Instant timestamp) {
}
