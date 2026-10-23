package br.edu.pi1.ratocego.mapper;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.dto.rest.TelemetrySampleResponse;
import br.edu.pi1.ratocego.model.Run;
import br.edu.pi1.ratocego.model.TelemetrySample;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.Duration;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RunMapper {

    @Mapping(target = "duration", expression = "java(duration(run))")
    RunResponse toRunResponse(Run run);

    @Mapping(target = "sequence", source = "sequenceNumber")
    @Mapping(target = "row", source = "positionRow")
    @Mapping(target = "column", source = "positionColumn")
    TelemetrySampleResponse toTelemetrySampleResponse(TelemetrySample sample);

    default Duration duration(Run run) {
        if (run.getStartedAt() == null || run.getFinishedAt() == null) {
            return null;
        }
        return Duration.between(run.getStartedAt(), run.getFinishedAt());
    }
}
