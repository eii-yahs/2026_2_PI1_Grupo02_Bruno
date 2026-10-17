package br.edu.pi1.ratocego.mapper;

import br.edu.pi1.ratocego.dto.rest.RunResponse;
import br.edu.pi1.ratocego.model.Run;
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

    default Duration duration(Run run) {
        if (run.getStartedAt() == null || run.getFinishedAt() == null) {
            return null;
        }
        return Duration.between(run.getStartedAt(), run.getFinishedAt());
    }
}
