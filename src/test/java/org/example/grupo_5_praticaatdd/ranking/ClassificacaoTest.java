package org.example.grupo_5_praticaatdd.ranking;

import java.util.List;
import org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes;
import org.example.grupo_5_praticaatdd.ranking.internal.Classificacao;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClassificacaoTest {
    private final Classificacao regra = new Classificacao();

    @Test
    void rankingVazioESemAprovacoes() {
        assertTrue(regra.classificar(List.of()).isEmpty());
        assertTrue(regra.classificar(List.of(new ResumoAprovacoes(1L, "Sem aprovação", 0))).isEmpty());
    }

    @Test
    void ordenaPorAprovacoesEDesempataPorIdSemAlterarEntrada() {
        var entrada = List.of(new ResumoAprovacoes(7L, "B", 1),
                new ResumoAprovacoes(3L, "A", 3), new ResumoAprovacoes(2L, "C", 3));
        var resultado = regra.classificar(entrada);
        assertEquals(List.of(2L, 3L, 7L), resultado.stream().map(p -> p.alunoId()).toList());
        assertEquals(List.of(1, 2, 3), resultado.stream().map(p -> p.posicao()).toList());
        assertEquals("C", resultado.getFirst().nome());
        assertEquals(3, resultado.getFirst().aprovacoes());
        assertEquals(7L, entrada.getFirst().alunoId());
        assertThrows(UnsupportedOperationException.class, resultado::clear);
    }
}
