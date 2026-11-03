package org.example.grupo_5_praticaatdd.ranking.infrastructure;

import java.util.List;
import org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes;
import org.example.grupo_5_praticaatdd.ranking.port.LeituraRankingPort;
import org.example.grupo_5_praticaatdd.repository.ConclusaoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaRankingAdapter implements LeituraRankingPort {
    private final ConclusaoRepository conclusoes;

    public JpaRankingAdapter(ConclusaoRepository conclusoes) {
        this.conclusoes = conclusoes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumoAprovacoes> buscarAprovacoes() {
        return conclusoes.contarAprovacoesPorAluno();
    }
}
