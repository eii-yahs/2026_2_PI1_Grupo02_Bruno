package br.edu.pi1.ratocego.service.port;

import br.edu.pi1.ratocego.dto.websocket.RunFinishedUpdate;
import br.edu.pi1.ratocego.dto.websocket.HeartbeatUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunInterruptedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunStartedUpdate;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunCommandStatusUpdate;

/** Port for publishing telemetry updates to real-time consumers. */
public interface TelemetryPublisher {

    void publishRunStarted(RunStartedUpdate update);

    void publishTelemetry(TelemetryUpdate update);

    void publishRunFinished(RunFinishedUpdate update);

    void publishRunInterrupted(RunInterruptedUpdate update);

    void publishHeartbeat(HeartbeatUpdate update);

    void publishRunCommandStatus(RunCommandStatusUpdate update);
}
