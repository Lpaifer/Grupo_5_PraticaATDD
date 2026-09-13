package org.example.grupo_5_praticaatdd.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarCursoRequest(@NotBlank String nome) {
}
