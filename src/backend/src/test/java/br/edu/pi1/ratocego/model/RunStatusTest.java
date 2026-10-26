package br.edu.pi1.ratocego.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RunStatusTest {

    @Test
    void allowsOnlyConfiguredTransitions() {
        assertEquals(RunStatus.IN_PROGRESS, RunStatus.START_REQUESTED.transitionTo(RunStatus.IN_PROGRESS));
        assertEquals(RunStatus.FAILED, RunStatus.START_REQUESTED.transitionTo(RunStatus.FAILED));
        assertEquals(RunStatus.INTERRUPT_REQUESTED, RunStatus.IN_PROGRESS.transitionTo(RunStatus.INTERRUPT_REQUESTED));
        assertEquals(RunStatus.COMPLETED, RunStatus.IN_PROGRESS.transitionTo(RunStatus.COMPLETED));
        assertEquals(RunStatus.FAILED, RunStatus.IN_PROGRESS.transitionTo(RunStatus.FAILED));
        assertEquals(RunStatus.INTERRUPTED, RunStatus.INTERRUPT_REQUESTED.transitionTo(RunStatus.INTERRUPTED));
        assertEquals(RunStatus.COMPLETED, RunStatus.INTERRUPT_REQUESTED.transitionTo(RunStatus.COMPLETED));
        assertEquals(RunStatus.FAILED, RunStatus.INTERRUPT_REQUESTED.transitionTo(RunStatus.FAILED));
    }

    @Test
    void rejectsEveryUnconfiguredTransition() {
        for (RunStatus from : RunStatus.values()) {
            for (RunStatus to : RunStatus.values()) {
                boolean allowed = switch (from) {
                    case START_REQUESTED -> to == RunStatus.IN_PROGRESS || to == RunStatus.FAILED;
                    case IN_PROGRESS -> to == RunStatus.INTERRUPT_REQUESTED
                            || to == RunStatus.COMPLETED || to == RunStatus.FAILED;
                    case INTERRUPT_REQUESTED -> to == RunStatus.INTERRUPTED
                            || to == RunStatus.COMPLETED || to == RunStatus.FAILED;
                    case COMPLETED, FAILED, INTERRUPTED -> false;
                };
                if (!allowed) {
                    assertThrows(IllegalStateException.class, () -> from.transitionTo(to), from + " -> " + to);
                }
            }
        }
    }

    @Test
    void rejectsNullTarget() {
        assertThrows(NullPointerException.class, () -> RunStatus.IN_PROGRESS.transitionTo(null));
    }
}
