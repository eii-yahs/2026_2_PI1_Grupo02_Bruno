package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.mqtt.RunFinishedPayload;
import br.edu.pi1.ratocego.dto.mqtt.RunInterruptedPayload;
import br.edu.pi1.ratocego.dto.mqtt.RunStartedPayload;
import br.edu.pi1.ratocego.dto.mqtt.StartRunCommand;
import br.edu.pi1.ratocego.dto.mqtt.InterruptRunCommand;
import br.edu.pi1.ratocego.dto.websocket.RunCommandStatusUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunFinishedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunInterruptedUpdate;
import br.edu.pi1.ratocego.dto.websocket.RunStartedUpdate;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.repository.RunRepository;
import br.edu.pi1.ratocego.util.MqttPayloadParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.scheduling.annotation.Scheduled;
import tools.jackson.databind.ObjectMapper;
import br.edu.pi1.ratocego.model.MazeType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class RunLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(RunLifecycleService.class);
    private static final AtomicLong COMMAND_EVENT_IDS = new AtomicLong(System.currentTimeMillis() * 1_000L);
    private final MqttPayloadParser payloadParser;
    private final RunRepository runRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final String topicPrefix;

    public RunLifecycleService(MqttPayloadParser payloadParser, RunRepository runRepository,
                               ApplicationEventPublisher eventPublisher,
                               ObjectMapper objectMapper, @Value("${app.mqtt.topic-prefix:ratocego/}") String topicPrefix) {
        this.payloadParser = payloadParser;
        this.runRepository = runRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.topicPrefix = topicPrefix.endsWith("/") ? topicPrefix : topicPrefix + "/";
    }

    @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public Run start(MazeType mazeType) {
        if (!runRepository.findByStatusIn(List.of(RunStatus.START_REQUESTED, RunStatus.IN_PROGRESS,
                RunStatus.INTERRUPT_REQUESTED)).isEmpty()) throw new IllegalStateException("A run is already active");
        Instant now = Instant.now();
        Run run = new Run(); run.setStatus(RunStatus.START_REQUESTED);
        run.setMazeRows(4);
        run.setMazeColumns(columnsFor(mazeType));
        run.setStartRequestedAt(now); runRepository.save(run);
        long runId = run.getId();
        publishAfterCommit(topicPrefix + "commands/run/start", new StartRunCommand(nextEventId(), runId, mazeType, now));
        notifyStatus(run, "Início solicitado. Aguardando confirmação do robô.");
        return run;
    }

    @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRED)
    public Run interrupt(long runId) {
        Run run = findRun(runId);
        if (run.getStatus() == RunStatus.INTERRUPT_REQUESTED) {
            Instant retryAt = Instant.now();
            run.setInterruptRequestedAt(retryAt); run.setInterruptTimeoutWarned(false); runRepository.save(run);
            publishAfterCommit(topicPrefix + "commands/run/interrupt", new InterruptRunCommand(nextEventId(), runId, retryAt));
            return run;
        }
        if (run.getStatus() != RunStatus.IN_PROGRESS) throw new IllegalStateException("Run is not interruptible");
        Instant now = Instant.now(); run.setStatus(RunStatus.INTERRUPT_REQUESTED); run.setInterruptRequestedAt(now);
        run.setInterruptTimeoutWarned(false); runRepository.save(run);
        publishAfterCommit(topicPrefix + "commands/run/interrupt", new InterruptRunCommand(nextEventId(), runId, now));
        notifyStatus(run, "Interrupção solicitada. Aguardando confirmação do robô.");
        return run;
    }

    @Scheduled(fixedDelay = 250)
    @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRED)
    public void processCommandTimeouts() {
        Instant now = Instant.now();
        for (Run run : runRepository.findByStatusIn(List.of(RunStatus.START_REQUESTED, RunStatus.INTERRUPT_REQUESTED))) {
            if (run.getStatus() == RunStatus.START_REQUESTED && run.getStartRequestedAt() != null
                    && !run.getStartRequestedAt().plusSeconds(5).isAfter(now)) {
                run.setStatus(RunStatus.FAILED); run.setFinishedAt(now); runRepository.save(run);
                notifyStatus(run, "O robô não confirmou o início dentro de 5 segundos.");
            } else if (run.getStatus() == RunStatus.INTERRUPT_REQUESTED && !run.isInterruptTimeoutWarned()
                    && run.getInterruptRequestedAt() != null && !run.getInterruptRequestedAt().plusSeconds(2).isAfter(now)) {
                run.setInterruptTimeoutWarned(true); runRepository.save(run);
                notifyStatus(run, "A interrupção não foi confirmada após 2 segundos. Você pode tentar novamente.", true);
            }
        }
    }

    private void notifyStatus(Run run, String message) {
        notifyStatus(run, message, false);
    }

    private void notifyStatus(Run run, String message, boolean retryAllowed) {
        eventPublisher.publishEvent(new WebSocketUpdateEvent(new RunCommandStatusUpdate(
                run.getId(), run.getStatus(), Instant.now(), message, retryAllowed)));
    }

    private static int columnsFor(MazeType mazeType) {
        return switch (mazeType) {
            case GRID_4X4 -> 4;
            case GRID_8X4 -> 8;
            case GRID_12X4 -> 12;
        };
    }

    private long nextEventId() { return COMMAND_EVENT_IDS.incrementAndGet(); }

    private void publishAfterCommit(String topic, Object command) {
        try {
            eventPublisher.publishEvent(new MqttCommandPublicationRequested(
                    topic,
                    objectMapper.writeValueAsString(command)));
        } catch (Exception e) {
            log.error("Could not serialize run command for MQTT topic {}", topic, e);
            throw new RunCommandPublicationException("Could not publish run command", e);
        }
    }

    @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRED)
    public void processStarted(String payload) {
        RunStartedPayload event = payloadParser.parseAndValidate(payload, RunStartedPayload.class);

        Run run = runRepository.findById(event.runId()).orElse(null);
        if (run == null || run.getStatus() != RunStatus.START_REQUESTED) {
            log.warn("Ignoring unsolicited or late run.started for runId={}", event.runId());
            return;
        }
        if (run.getStartRequestedAt() != null && Instant.now().isAfter(run.getStartRequestedAt().plusSeconds(5))) {
            run.setStatus(RunStatus.FAILED);
            run.setFinishedAt(Instant.now());
            runRepository.save(run);
            notifyStatus(run, "O início não foi confirmado dentro de 5 segundos.");
            return;
        }
        if (!Integer.valueOf(event.mazeRows()).equals(run.getMazeRows())
                || !Integer.valueOf(event.mazeColumns()).equals(run.getMazeColumns())) {
            run.setStatus(RunStatus.FAILED);
            run.setFinishedAt(Instant.now());
            runRepository.save(run);
            notifyStatus(run, "O robô confirmou dimensões de labirinto diferentes das solicitadas.");
            return;
        }
        if (run.getStatus() == RunStatus.IN_PROGRESS) {
            if (event.timestamp().equals(run.getStartedAt())) {
                log.warn("Ignoring duplicate run.started for runId={}", event.runId());
                return;
            }
            throw new IllegalStateException("Run is already in progress for runId=" + event.runId());
        }
        run.setStatus(run.getStatus().transitionTo(RunStatus.IN_PROGRESS));
        initializeRun(run, event);
        runRepository.save(run);
        eventPublisher.publishEvent(new WebSocketUpdateEvent(RunStartedUpdate.builder()
                .runId(event.runId())
                .timestamp(event.timestamp())
                .mazeRows(event.mazeRows())
                .mazeColumns(event.mazeColumns())
                .status(RunStatus.IN_PROGRESS)
                .build()));
    }

    @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRED)
    public void processFinished(String payload) {
        RunFinishedPayload event = payloadParser.parseAndValidate(payload, RunFinishedPayload.class);
        if (event.status() != RunStatus.COMPLETED && event.status() != RunStatus.FAILED) {
            throw new IllegalArgumentException("run.finished must include a terminal status");
        }

        Run run = findRun(event.runId());
        validateFinishTimestamp(run, event.timestamp(), "run.finished");
        run.setStatus(run.getStatus().transitionTo(event.status()));
        run.setFinishedAt(event.timestamp());
        run.setChallengeCompleted(event.challengeCompleted());
        runRepository.save(run);
        log.info("Run {} finished with status {}", event.runId(), event.status());

        eventPublisher.publishEvent(new WebSocketUpdateEvent(RunFinishedUpdate.builder()
                .runId(event.runId())
                .timestamp(event.timestamp())
                .status(event.status())
                .challengeCompleted(event.challengeCompleted())
                .distanceTravelledMeters(run.getDistanceTravelledMeters())
                .averageSpeedMetersPerSecond(run.getAverageSpeedMetersPerSecond())
                .chargeConsumedMilliampHours(run.getChargeConsumedMilliampHours())
                .energyConsumedWattHours(run.getEnergyConsumedWattHours())
                .build()));
    }

    @Transactional(transactionManager = "transactionManager", propagation = Propagation.REQUIRED)
    public void processInterrupted(String payload) {
        RunInterruptedPayload event = payloadParser.parseAndValidate(payload, RunInterruptedPayload.class);

        Run run = findRun(event.runId());
        validateFinishTimestamp(run, event.timestamp(), "run.interrupted");
        run.setStatus(run.getStatus().transitionTo(RunStatus.INTERRUPTED));
        run.setFinishedAt(event.timestamp());
        run.setChallengeCompleted(false);
        runRepository.save(run);

        eventPublisher.publishEvent(new WebSocketUpdateEvent(RunInterruptedUpdate.builder()
                .runId(event.runId())
                .timestamp(event.timestamp())
                .status(RunStatus.INTERRUPTED)
                .build()));
    }

    private void initializeRun(Run run, RunStartedPayload event) {
        run.setStartedAt(event.timestamp());
        run.setFinishedAt(null);
        run.setMazeRows(event.mazeRows());
        run.setMazeColumns(event.mazeColumns());
        run.setChallengeCompleted(null);
        run.setDistanceTravelledMeters(BigDecimal.ZERO);
        run.setAverageSpeedMetersPerSecond(null);
        run.setChargeConsumedMilliampHours(null);
        run.setEnergyConsumedWattHours(null);
        run.setBatteryStatus(null);
        run.setLastSequence(0);
        run.setLastSampleAt(null);
        run.setLastCurrentMilliAmps(null);
        run.setLastPowerWatts(null);
    }

    private void validateFinishTimestamp(Run run, Instant timestamp, String eventName) {
        if (run.getStartedAt() == null || timestamp.isBefore(run.getStartedAt())
                || (run.getLastSampleAt() != null && timestamp.isBefore(run.getLastSampleAt()))) {
            throw new IllegalArgumentException(eventName + " timestamp precedes run start or latest telemetry");
        }
    }

    private Run findRun(long runId) {
        return runRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("No run found for runId=" + runId));
    }

}
