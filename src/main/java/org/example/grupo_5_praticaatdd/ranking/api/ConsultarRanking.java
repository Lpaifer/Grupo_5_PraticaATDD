package org.example.grupo_5_praticaatdd.ranking.api;

import java.util.List;
import org.example.grupo_5_praticaatdd.ranking.internal.Classificacao;
import org.example.grupo_5_praticaatdd.ranking.port.LeituraRankingPort;
import org.springframework.stereotype.Service;

@Service
public class ConsultarRanking {
    private final LeituraRankingPort leituraRanking;
    private final Classificacao classificacao = new Classificacao();

    public ConsultarRanking(LeituraRankingPort leituraRanking) {
        this.leituraRanking = leituraRanking;
    }

    public List<PosicaoRanking> consultar() {
        return classificacao.classificar(leituraRanking.buscarAprovacoes());
    }
}
