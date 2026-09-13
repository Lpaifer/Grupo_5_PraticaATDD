package org.example.grupo_5_praticaatdd.api.dto;

public record HistoricoCursoResponse(Long cursoId, String curso, double nota,
                                    boolean concluido, boolean aprovado) {
}
