package br.edu.pi1.ratocego.util;

import br.edu.pi1.ratocego.dto.mqtt.TelemetrySamplePayload;
import br.edu.pi1.ratocego.model.Heading;
import br.edu.pi1.ratocego.model.Position;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MqttPayloadParserTest {

    @Test
    void parsesAndReturnsValidatedPayload() {
        ObjectMapper mapper = mock(ObjectMapper.class);
        Validator validator = mock(Validator.class);
        TelemetrySamplePayload payload = new TelemetrySamplePayload(1, 2, 3, Instant.now(),
                new Position(0, 0, Heading.NORTH), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ONE, BigDecimal.ZERO);
        when(mapper.readValue("{}", TelemetrySamplePayload.class)).thenReturn(payload);
        when(validator.validate(payload)).thenReturn(Set.of());

        TelemetrySamplePayload result = new MqttPayloadParser(mapper, validator)
                .parseAndValidate("{}", TelemetrySamplePayload.class);

        assertEquals(payload, result);
    }

    @Test
    void wrapsMalformedJsonAsInvalidPayload() {
        ObjectMapper mapper = mock(ObjectMapper.class);
        Validator validator = mock(Validator.class);
        when(mapper.readValue("broken", TelemetrySamplePayload.class))
                .thenThrow(new IllegalArgumentException("invalid json"));

        assertThrows(IllegalArgumentException.class,
                () -> new MqttPayloadParser(mapper, validator).parseAndValidate("broken", TelemetrySamplePayload.class));
    }

    @Test
    void rejectsConstraintViolationsAndCanParseTheNextValidPayload() {
        ObjectMapper mapper = mock(ObjectMapper.class);
        TelemetrySamplePayload invalid = new TelemetrySamplePayload(0, 0, 0, null,
                new Position(-1, 0, null), new BigDecimal("-1"), BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);
        TelemetrySamplePayload valid = new TelemetrySamplePayload(1, 2, 3, Instant.now(),
                new Position(0, 0, Heading.NORTH), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ONE, BigDecimal.ZERO);
        when(mapper.readValue("invalid", TelemetrySamplePayload.class)).thenReturn(invalid);
        when(mapper.readValue("valid", TelemetrySamplePayload.class)).thenReturn(valid);

        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var parser = new MqttPayloadParser(mapper, validatorFactory.getValidator());

            ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                    () -> parser.parseAndValidate("invalid", TelemetrySamplePayload.class));

            assertTrue(exception.getConstraintViolations().stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().equals("eventId")));
            assertEquals(valid, parser.parseAndValidate("valid", TelemetrySamplePayload.class));
        }
    }

    @Test
    void rejectsIncompleteAndIncompatibleJsonUsingTheRealMapperAndValidator() {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var parser = new MqttPayloadParser(new JsonMapper(), validatorFactory.getValidator());

            ConstraintViolationException incomplete = assertThrows(ConstraintViolationException.class,
                    () -> parser.parseAndValidate("{\"eventId\":1,\"runId\":2,\"sequence\":3}",
                            TelemetrySamplePayload.class));
            assertTrue(incomplete.getConstraintViolations().stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().equals("timestamp")));

            IllegalArgumentException incompatible = assertThrows(IllegalArgumentException.class,
                    () -> parser.parseAndValidate("{\"eventId\":\"not-a-number\"}",
                            TelemetrySamplePayload.class));
            assertTrue(incompatible.getMessage().contains("TelemetrySamplePayload"));
        }
    }
}
