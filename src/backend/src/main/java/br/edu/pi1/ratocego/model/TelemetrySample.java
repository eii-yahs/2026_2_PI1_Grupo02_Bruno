package br.edu.pi1.ratocego.model;

import br.edu.pi1.ratocego.dto.mqtt.TelemetrySamplePayload;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "telemetry_samples", uniqueConstraints = {
        @UniqueConstraint(name = "uk_telemetry_run_sequence", columnNames = {"run_id", "sequence_number"}),
        @UniqueConstraint(name = "uk_telemetry_event_id", columnNames = "event_id")
})
@Getter
@Setter
@NoArgsConstructor
public class TelemetrySample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false)
    private Run run;

    @Column(nullable = false)
    private long eventId;

    @Column(nullable = false)
    private long sequenceNumber;

    @Column(nullable = false)
    private Instant recordedAt;

    @Column(nullable = false)
    private int positionRow;

    @Column(nullable = false)
    private int positionColumn;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(nullable = false)
    private Heading heading;

    @Column(nullable = false)
    private BigDecimal distanceTravelledMeters;

    private BigDecimal currentSpeedMetersPerSecond;
    private BigDecimal batteryVoltageVolts;
    private BigDecimal currentMilliAmps;
    private BigDecimal currentPowerWatts;

    public static TelemetrySample createStoredSample(Run run, TelemetrySamplePayload sample, BigDecimal currentPowerWatts) {
        TelemetrySample storedSample = new TelemetrySample();
        storedSample.setRun(run);
        storedSample.setEventId(sample.eventId());
        storedSample.setSequenceNumber(sample.sequence());
        storedSample.setRecordedAt(sample.timestamp());
        storedSample.setPositionRow(sample.position().row());
        storedSample.setPositionColumn(sample.position().column());
        storedSample.setHeading(sample.position().heading());
        storedSample.setDistanceTravelledMeters(sample.distanceTravelledMeters());
        storedSample.setCurrentSpeedMetersPerSecond(sample.currentSpeedMetersPerSecond());
        storedSample.setBatteryVoltageVolts(sample.batteryVoltageVolts());
        storedSample.setCurrentMilliAmps(sample.currentMilliAmps());
        storedSample.setCurrentPowerWatts(currentPowerWatts);
        return storedSample;
    }
}
