package org.example.grupo_5_praticaatdd.api.dto;

public record ConclusaoResponse(Long alunoId, String aluno, String curso, double nota,
                                boolean concluido, boolean aprovado, int cursosAdicionais) {
}
