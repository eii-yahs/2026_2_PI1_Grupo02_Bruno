package br.edu.pi1.ratocego.dto.mqtt;

import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RunEventPayloadValidationTest {

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
    void rejectsIncompleteOrNonPositiveRunStartedPayload() {
        var violations = validator.validate(new RunStartedPayload(0, 0, null, 0, 0));

        assertTrue(hasViolation(violations, "eventId"));
        assertTrue(hasViolation(violations, "runId"));
        assertTrue(hasViolation(violations, "timestamp"));
        assertTrue(hasViolation(violations, "mazeRows"));
        assertTrue(hasViolation(violations, "mazeColumns"));
    }

    @Test
    void rejectsIncompleteRunFinishedPayload() {
        var violations = validator.validate(new RunFinishedPayload(0, 0, null, null, null));

        assertTrue(hasViolation(violations, "eventId"));
        assertTrue(hasViolation(violations, "runId"));
        assertTrue(hasViolation(violations, "timestamp"));
        assertTrue(hasViolation(violations, "status"));
    }

    @Test
    void rejectsIncompleteRunInterruptedPayload() {
        var violations = validator.validate(new RunInterruptedPayload(0, 0, null));

        assertTrue(hasViolation(violations, "eventId"));
        assertTrue(hasViolation(violations, "runId"));
        assertTrue(hasViolation(violations, "timestamp"));
    }

    private boolean hasViolation(Set<? extends ConstraintViolation<?>> violations, String propertyName) {
        return violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(propertyName));
    }
}
