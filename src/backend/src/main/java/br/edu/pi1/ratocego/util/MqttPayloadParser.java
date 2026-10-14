package br.edu.pi1.ratocego.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;

@Slf4j
@Component
public class MqttPayloadParser {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public MqttPayloadParser(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public <T> T parseAndValidate(String payload, Class<T> payloadType) {
        T parsed;
        try {
            parsed = objectMapper.readValue(payload, payloadType);
        } catch (Exception exception) {
            int payloadLength = payload == null ? 0 : payload.length();
            log.warn("Rejected MQTT payload: type={}, length={}, reason=invalid_json, cause={}",
                    payloadType.getSimpleName(), payloadLength, exception.getClass().getSimpleName());
            throw new IllegalArgumentException(
                    "Invalid MQTT JSON payload for " + payloadType.getSimpleName(), exception);
        }

        var violations = validator.validate(parsed);
        if (!violations.isEmpty()) {
            var violationSummary = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .sorted()
                    .toList();
            log.warn("Rejected MQTT payload: type={}, reason=constraint_violation, violations={}",
                    payloadType.getSimpleName(), violationSummary);
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        return parsed;
    }
}
