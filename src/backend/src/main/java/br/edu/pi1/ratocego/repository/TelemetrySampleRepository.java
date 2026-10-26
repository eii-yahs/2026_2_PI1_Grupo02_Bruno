package br.edu.pi1.ratocego.repository;

import br.edu.pi1.ratocego.model.TelemetrySample;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelemetrySampleRepository extends JpaRepository<TelemetrySample, Long> {

    boolean existsByEventId(long eventId);

    boolean existsByRun_IdAndSequenceNumber(long runId, long sequenceNumber);

    List<TelemetrySample> findByRun_IdOrderBySequenceNumberAsc(long runId);
}
