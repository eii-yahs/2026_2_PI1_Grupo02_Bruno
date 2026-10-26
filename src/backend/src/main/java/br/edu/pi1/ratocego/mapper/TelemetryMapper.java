package br.edu.pi1.ratocego.mapper;

import br.edu.pi1.ratocego.dto.mqtt.TelemetrySamplePayload;
import br.edu.pi1.ratocego.dto.websocket.TelemetryUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TelemetryMapper {

    TelemetryUpdate toTelemetryUpdate(TelemetrySamplePayload payload);
}
