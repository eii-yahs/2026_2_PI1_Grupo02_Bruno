package br.edu.pi1.ratocego.dto.mqtt;

import br.edu.pi1.ratocego.model.Heading;
import br.edu.pi1.ratocego.model.Position;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelemetrySamplePayloadValidationTest {

    private static jakarta.validation.ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void acceptsValidSample() {
        var sample = new TelemetrySamplePayload(1, 1, 1, Instant.now(), new Position(0, 0, Heading.NORTH),
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("7.4"), new BigDecimal("50"));
        assertTrue(validator.validate(sample).isEmpty());
    }

    @Test
    void rejectsInvalidIdentifiersMeasurementsAndPosition() {
        var sample = new TelemetrySamplePayload(0, 0, 0, null, new Position(-1, 0, null),
                new BigDecimal("-1"), new BigDecimal("-1"), new BigDecimal("-1"), BigDecimal.ZERO);
        assertFalse(validator.validate(sample).isEmpty());
    }
}
