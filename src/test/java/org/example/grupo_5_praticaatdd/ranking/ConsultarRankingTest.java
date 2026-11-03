package org.example.grupo_5_praticaatdd.ranking;

import java.util.List;
import org.example.grupo_5_praticaatdd.ranking.api.ConsultarRanking;
import org.example.grupo_5_praticaatdd.ranking.api.PosicaoRanking;
import org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes;
import org.example.grupo_5_praticaatdd.ranking.port.LeituraRankingPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarRankingTest {
    @Mock
    private LeituraRankingPort porta;

    @Test
    void classificaDadosDaPortaSemJpa() {
        when(porta.buscarAprovacoes()).thenReturn(List.of(
                new ResumoAprovacoes(9L, "B", 1),
                new ResumoAprovacoes(7L, "C", 3),
                new ResumoAprovacoes(2L, "A", 3)));
        var resultado = new ConsultarRanking(porta).consultar();
        assertEquals(List.of(new PosicaoRanking(1, 2L, "A", 3),
                new PosicaoRanking(2, 7L, "C", 3),
                new PosicaoRanking(3, 9L, "B", 1)), resultado);
        verify(porta).buscarAprovacoes();
        verifyNoMoreInteractions(porta);
    }

    @Test
    void portaVaziaProduzRankingVazio() {
        when(porta.buscarAprovacoes()).thenReturn(List.of());
        assertEquals(List.of(), new ConsultarRanking(porta).consultar());
        verify(porta).buscarAprovacoes();
        verifyNoMoreInteractions(porta);
    }
}
