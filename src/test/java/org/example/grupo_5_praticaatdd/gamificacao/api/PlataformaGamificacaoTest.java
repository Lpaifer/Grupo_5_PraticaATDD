package org.example.grupo_5_praticaatdd.gamificacao.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlataformaGamificacaoTest {

    private final AvaliarConclusao avaliador = new AvaliarConclusao();

    @Test
    void processamentoPreservaDadosOriginaisDaConclusao() {
        var dados = new DadosConclusao(7.0, true, 6);

        var resultado = avaliador.avaliar(dados);

        assertEquals(7.0, dados.nota());
        assertEquals(true, dados.concluido());
        assertEquals(6, dados.cursosAdicionaisAtuais());
        assertEquals(new ResultadoConclusao(true, 9), resultado);
    }

    @Test
    void deveLiberarTresCursosAoRefazerCursoEObterMedia8() {
        var reprovacao = avaliador.avaliar(new DadosConclusao(3.0, true, 0));
        assertEquals(new ResultadoConclusao(false, 0), reprovacao);

        var novaTentativa = avaliador.avaliar(new DadosConclusao(8.0, true, reprovacao.cursosAdicionais()));

        assertEquals(new ResultadoConclusao(true, 3), novaTentativa);
    }

    @Test
    void naoDeveLiberarCursosQuandoMediaForMenorQue7() {
        var resultado = avaliador.avaliar(new DadosConclusao(6.5, true, 6));

        assertEquals(new ResultadoConclusao(false, 6), resultado);
    }

    @Test
    void naoDeveLiberarCursosQuandoCursoNaoFoiConcluido() {
        var resultado = avaliador.avaliar(new DadosConclusao(7.0, false, 3));

        assertEquals(new ResultadoConclusao(false, 3), resultado);
    }

    @Test
    void naoDeveLiberarCursosEnquantoCursoNaoForConcluido() {
        var resultado = avaliador.avaliar(new DadosConclusao(0.0, false, 0));

        assertEquals(new ResultadoConclusao(false, 0), resultado);
    }

    @Test
    void deveLiberarTresCursosQuandoMediaForExatamente7() {
        var resultado = avaliador.avaliar(new DadosConclusao(7.0, true, 0));

        assertEquals(new ResultadoConclusao(true, 3), resultado);
    }

    @Test
    void deveLiberarTresCursosQuandoAlunoConcluirComMedia9() {
        var resultado = avaliador.avaliar(new DadosConclusao(9.0, true, 0));

        assertEquals(new ResultadoConclusao(true, 3), resultado);
    }

    @Test
    void deveAcumularSeisCursosAposSegundaAprovacao() {
        var primeira = avaliador.avaliar(new DadosConclusao(8.0, true, 0));
        var segunda = avaliador.avaliar(new DadosConclusao(7.5, true, primeira.cursosAdicionais()));

        assertEquals(new ResultadoConclusao(true, 6), segunda);
    }
}
