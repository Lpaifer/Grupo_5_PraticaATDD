package org.example.grupo_5_praticaatdd.ranking;

import java.util.List;
import org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes;
import org.example.grupo_5_praticaatdd.ranking.port.LeituraRankingPort;

final class FakeLeituraRankingPort implements LeituraRankingPort {
    private final List<ResumoAprovacoes> dados;

    FakeLeituraRankingPort(List<ResumoAprovacoes> dados) {
        this.dados = List.copyOf(dados);
    }

    @Override
    public List<ResumoAprovacoes> buscarAprovacoes() {
        return dados;
    }
}
