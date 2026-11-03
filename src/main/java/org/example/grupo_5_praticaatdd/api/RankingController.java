package org.example.grupo_5_praticaatdd.api;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.grupo_5_praticaatdd.ranking.api.ConsultarRanking;
import org.example.grupo_5_praticaatdd.ranking.api.PosicaoRanking;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ranking")
@Tag(name = "Ranking", description = "Classificação por quantidade de conclusões aprovadas")
public class RankingController {
    private final ConsultarRanking ranking;

    public RankingController(ConsultarRanking ranking) {
        this.ranking = ranking;
    }

    @GetMapping
    @Operation(summary = "Consultar ranking; desempate por alunoId crescente")
    public List<PosicaoRanking> consultar() {
        return ranking.consultar();
    }
}
