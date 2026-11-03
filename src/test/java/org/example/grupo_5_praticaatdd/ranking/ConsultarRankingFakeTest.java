package org.example.grupo_5_praticaatdd.ranking;

import java.util.List;
import org.example.grupo_5_praticaatdd.ranking.api.ConsultarRanking;
import org.example.grupo_5_praticaatdd.ranking.api.PosicaoRanking;
import org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsultarRankingFakeTest {
    @Test
    void substituiPersistenciaPorDadosEmMemoria() {
        var fake = new FakeLeituraRankingPort(List.of(
                new ResumoAprovacoes(4L, "B", 1), new ResumoAprovacoes(2L, "A", 2)));
        var consulta = new ConsultarRanking(fake);
        var esperado = List.of(new PosicaoRanking(1, 2L, "A", 2), new PosicaoRanking(2, 4L, "B", 1));
        assertEquals(esperado, consulta.consultar());
        assertEquals(esperado, consulta.consultar());
    }
}
