package br.edu.pi1.ratocego.model;

import java.util.Objects;

public enum RunStatus {
    START_REQUESTED,
    IN_PROGRESS,
    INTERRUPT_REQUESTED,
    COMPLETED,
    FAILED,
    INTERRUPTED;

    public RunStatus transitionTo(RunStatus next) {
        Objects.requireNonNull(next, "next status must not be null");

        boolean allowed = switch (this) {
            case START_REQUESTED -> switch (next) {
                case IN_PROGRESS, FAILED -> true;
                default -> false;
            };
            case IN_PROGRESS -> switch (next) {
                case INTERRUPT_REQUESTED, COMPLETED, FAILED -> true;
                default -> false;
            };
            case INTERRUPT_REQUESTED -> switch (next) {
                case INTERRUPTED, COMPLETED, FAILED -> true;
                default -> false;
            };
            case COMPLETED, FAILED, INTERRUPTED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException("Invalid run status transition: " + this + " -> " + next);
        }
        return next;
    }
}
