package org.example.grupo_5_praticaatdd.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProcessarConclusaoRequest(
        @NotNull @Positive Long alunoId,
        @NotNull @Positive Long cursoId,
        @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double nota,
        @NotNull Boolean concluido) {
}
