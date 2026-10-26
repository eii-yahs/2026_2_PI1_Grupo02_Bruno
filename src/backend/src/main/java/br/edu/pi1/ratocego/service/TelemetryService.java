package br.edu.pi1.ratocego.service;

import br.edu.pi1.ratocego.dto.mqtt.TelemetrySamplePayload;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import br.edu.pi1.ratocego.mapper.TelemetryMapper;
import br.edu.pi1.ratocego.model.BatteryStatus;
import br.edu.pi1.ratocego.model.Position;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.RunStatus;
import br.edu.pi1.ratocego.model.TelemetrySample;
import br.edu.pi1.ratocego.repository.RunRepository;
import br.edu.pi1.ratocego.repository.TelemetrySampleRepository;
import br.edu.pi1.ratocego.util.MqttPayloadParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;

@Service
public class TelemetryService {

    private static final Logger log = LoggerFactory.getLogger(TelemetryService.class);
    private static final MathContext CALCULATION_CONTEXT = MathContext.DECIMAL64;
    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1_000);
    private static final BigDecimal SECONDS_PER_HOUR = BigDecimal.valueOf(3_600);
    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    private final TelemetryMapper telemetryMapper;
    private final MqttPayloadParser mqttPayloadParser;
    private final RunRepository runRepository;
    private final TelemetrySampleRepository sampleRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final String criticalVoltageSetting;
    private final String recoveryVoltageSetting;

    public TelemetryService(TelemetryMapper telemetryMapper, MqttPayloadParser mqttPayloadParser,
                            RunRepository runRepository, TelemetrySampleRepository sampleRepository,
                            ApplicationEventPublisher eventPublisher,
                            @Value("${app.battery.critical-voltage-volts:6.6}") String criticalVoltageSetting,
                            @Value("${app.battery.recovery-voltage-volts:6.8}") String recoveryVoltageSetting) {
        this.telemetryMapper = telemetryMapper;
        this.mqttPayloadParser = mqttPayloadParser;
        this.runRepository = runRepository;
        this.sampleRepository = sampleRepository;
        this.eventPublisher = eventPublisher;
        this.criticalVoltageSetting = criticalVoltageSetting;
        this.recoveryVoltageSetting = recoveryVoltageSetting;
    }

    @Transactional
    public void processSample(String payload) {
        TelemetrySamplePayload sample = mqttPayloadParser.parseAndValidate(payload, TelemetrySamplePayload.class);
        if (isDuplicate(sample)) return;

        Run run = findRun(sample.runId());
        if (!prepareSampleForProcessing(run, sample)) return;
        processAcceptedSample(run, sample);
    }

    private void processAcceptedSample(Run run, TelemetrySamplePayload sample) {
        BigDecimal powerWatts = calculatePower(sample);
        accumulateConsumption(run, sample, powerWatts);
        BigDecimal averageSpeed = calculateAverageSpeed(run, sample);
        BatteryStatus batteryStatus = classifyBattery(run.getBatteryStatus(), sample.batteryVoltageVolts());

        sampleRepository.save(TelemetrySample.createStoredSample(run, sample, powerWatts));
        updateRunFromSample(run, sample, powerWatts, averageSpeed, batteryStatus);
        publishTelemetry(sample, run, powerWatts, averageSpeed, batteryStatus);
    }

    private boolean isDuplicate(TelemetrySamplePayload sample) {
        boolean duplicate = sampleRepository.existsByEventId(sample.eventId())
                || sampleRepository.existsByRun_IdAndSequenceNumber(sample.runId(), sample.sequence());
        if (duplicate) {
            log.warn("Ignoring duplicate telemetry eventId={} runId={} sequence={}",
                    sample.eventId(), sample.runId(), sample.sequence());
        }
        return duplicate;
    }

    private boolean prepareSampleForProcessing(Run run, TelemetrySamplePayload sample) {
        if (run.getStatus() != RunStatus.IN_PROGRESS) {
            throw new IllegalStateException("Telemetry is only accepted for an IN_PROGRESS runId=" + sample.runId());
        }
        Position position = sample.position();
        if ((run.getMazeRows() != null && position.row() >= run.getMazeRows())
                || (run.getMazeColumns() != null && position.column() >= run.getMazeColumns())) {
            throw new IllegalArgumentException("Telemetry position is outside the confirmed maze dimensions");
        }
        if (sample.timestamp().isBefore(run.getStartedAt())) {
            throw new IllegalArgumentException("Telemetry timestamp is before run start for runId=" + sample.runId());
        }
        if (sample.sequence() <= run.getLastSequence()) {
            log.warn("Ignoring out-of-order telemetry runId={} sequence={} lastSequence={}",
                    sample.runId(), sample.sequence(), run.getLastSequence());
            return false;
        }
        if (sample.sequence() > run.getLastSequence() + 1) {
            log.warn("Telemetry sequence gap for runId={}: expected {}, received {}",
                    run.getId(), run.getLastSequence() + 1, sample.sequence());
        }
        if (run.getLastSampleAt() != null && !sample.timestamp().isAfter(run.getLastSampleAt())) {
            throw new IllegalArgumentException("Telemetry timestamps must increase for runId=" + sample.runId());
        }
        return true;
    }

    private BigDecimal calculatePower(TelemetrySamplePayload sample) {
        return sample.batteryVoltageVolts().multiply(sample.currentMilliAmps())
                .divide(THOUSAND, CALCULATION_CONTEXT);
    }

    private void accumulateConsumption(Run run, TelemetrySamplePayload sample, BigDecimal powerWatts) {
        if (run.getLastSampleAt() == null) return;

        BigDecimal seconds = secondsBetween(run.getLastSampleAt(), sample.timestamp());
        BigDecimal averageCurrent = run.getLastCurrentMilliAmps().add(sample.currentMilliAmps())
                .divide(TWO, CALCULATION_CONTEXT);
        BigDecimal averagePower = run.getLastPowerWatts().add(powerWatts).divide(TWO, CALCULATION_CONTEXT);
        run.setChargeConsumedMilliampHours(addOrZero(run.getChargeConsumedMilliampHours(),
                averageCurrent.multiply(seconds).divide(SECONDS_PER_HOUR, CALCULATION_CONTEXT)));
        run.setEnergyConsumedWattHours(addOrZero(run.getEnergyConsumedWattHours(),
                averagePower.multiply(seconds).divide(SECONDS_PER_HOUR, CALCULATION_CONTEXT)));
    }

    private BigDecimal calculateAverageSpeed(Run run, TelemetrySamplePayload sample) {
        BigDecimal elapsed = secondsBetween(run.getStartedAt(), sample.timestamp());
        return elapsed.signum() <= 0 ? null : sample.distanceTravelledMeters().divide(elapsed, CALCULATION_CONTEXT);
    }

    private void updateRunFromSample(Run run, TelemetrySamplePayload sample, BigDecimal powerWatts,
                                     BigDecimal averageSpeed, BatteryStatus batteryStatus) {
        run.setLastSequence(sample.sequence());
        run.setLastSampleAt(sample.timestamp());
        run.setLastCurrentMilliAmps(sample.currentMilliAmps());
        run.setLastPowerWatts(powerWatts);
        run.setDistanceTravelledMeters(sample.distanceTravelledMeters());
        run.setAverageSpeedMetersPerSecond(averageSpeed);
        run.setBatteryStatus(batteryStatus);
        runRepository.save(run);
    }

    private void publishTelemetry(TelemetrySamplePayload sample, Run run, BigDecimal powerWatts,
                                  BigDecimal averageSpeed, BatteryStatus batteryStatus) {
        eventPublisher.publishEvent(new WebSocketUpdateEvent(telemetryMapper.toTelemetryUpdate(sample).toBuilder()
                .averageSpeedMetersPerSecond(averageSpeed)
                .currentPowerWatts(powerWatts)
                .batteryStatus(batteryStatus)
                .chargeConsumedMilliampHours(run.getChargeConsumedMilliampHours())
                .energyConsumedWattHours(run.getEnergyConsumedWattHours())
                .build()));
    }

    private Run findRun(long runId) {
        return runRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("No run found for runId=" + runId));
    }

    private BatteryStatus classifyBattery(BatteryStatus previousStatus, BigDecimal voltage) {
        if (criticalVoltageSetting.isBlank() && recoveryVoltageSetting.isBlank()) {
            return null;
        }
        if (criticalVoltageSetting.isBlank() || recoveryVoltageSetting.isBlank()) {
            throw new IllegalStateException("Configure both battery voltage thresholds before processing telemetry");
        }

        BigDecimal criticalVoltage = new BigDecimal(criticalVoltageSetting);
        BigDecimal recoveryVoltage = new BigDecimal(recoveryVoltageSetting);
        if (recoveryVoltage.compareTo(criticalVoltage) <= 0) {
            throw new IllegalStateException("Battery recovery voltage must be greater than critical voltage");
        }

        BatteryStatus currentStatus = previousStatus == null ? BatteryStatus.NORMAL : previousStatus;
        if (currentStatus == BatteryStatus.NORMAL && voltage.compareTo(criticalVoltage) < 0) {
            return BatteryStatus.LOW;
        }
        if (currentStatus == BatteryStatus.LOW && voltage.compareTo(recoveryVoltage) > 0) {
            return BatteryStatus.NORMAL;
        }
        return currentStatus;
    }

    private BigDecimal secondsBetween(Instant start, Instant end) {
        Duration duration = Duration.between(start, end);
        return BigDecimal.valueOf(duration.getSeconds())
                .add(BigDecimal.valueOf(duration.getNano(), 9));
    }

    private BigDecimal addOrZero(BigDecimal currentTotal, BigDecimal amount) {
        return (currentTotal == null ? BigDecimal.ZERO : currentTotal).add(amount);
    }
}
