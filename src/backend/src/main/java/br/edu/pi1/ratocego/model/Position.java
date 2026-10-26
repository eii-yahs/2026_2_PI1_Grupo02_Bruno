package br.edu.pi1.ratocego.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record Position(
        @PositiveOrZero int row,
        @PositiveOrZero int column,
        @NotNull Heading heading
) {
}
