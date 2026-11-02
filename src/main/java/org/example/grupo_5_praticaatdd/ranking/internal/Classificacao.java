package org.example.grupo_5_praticaatdd.ranking.internal;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.example.grupo_5_praticaatdd.ranking.api.PosicaoRanking;
import org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes;

public final class Classificacao {
    public List<PosicaoRanking> classificar(List<ResumoAprovacoes> alunos) {
        var ordenados = alunos.stream()
                .filter(aluno -> aluno.aprovacoes() > 0)
                .sorted(Comparator.comparingLong(ResumoAprovacoes::aprovacoes).reversed()
                        .thenComparing(ResumoAprovacoes::alunoId))
                .toList();
        return IntStream.range(0, ordenados.size()).mapToObj(indice -> {
            var aluno = ordenados.get(indice);
            return new PosicaoRanking(indice + 1, aluno.alunoId(), aluno.nome(), aluno.aprovacoes());
        }).toList();
    }
}
