package br.edu.pi1.ratocego.dto.websocket;

import br.edu.pi1.ratocego.model.RunStatus;
import java.time.Instant;

/** Command lifecycle notice. Fields: runId, status, timestamp, message, retryAllowed. */
public record RunCommandStatusUpdate(long runId, RunStatus status, Instant timestamp, String message,
                                     boolean retryAllowed)
        implements WebSocketUpdate { }
