package org.example.grupo_5_praticaatdd.ranking.port;

import java.util.List;
import org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes;

public interface LeituraRankingPort {
    List<ResumoAprovacoes> buscarAprovacoes();
}
