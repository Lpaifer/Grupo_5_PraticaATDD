package org.example.grupo_5_praticaatdd.api.dto;

import java.util.List;

public record AlunoResponse(Long id, String nome, String plano, int cursosAdicionais,
                            List<HistoricoCursoResponse> historico) {
}
