package org.example.grupo_5_praticaatdd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlataformaGamificacaoTest {

    @Test
    public void deveLiberarTresCursosQuandoMediaForExatamente7() {
        var plataforma = new PlataformaGamificacao();
        var aluno = new Aluno("João", Plano.Basico);
        var curso = new Curso("Curso 1");
        var conclusao = new ConclusaoCurso(aluno, curso, 7.0, true);

        plataforma.processarConclusao(conclusao);

        assertEquals(3, aluno.getCursoAdicionaisLiberados());
    }

    @Test
    void deveAcumularSeisCursosAposSegundaAprovacao() {

        // ARRANGE
        var plataforma = new PlataformaGamificacao();
        var aluno = new Aluno("Leonardo", Plano.Basico);

        var primeiroCurso = new Curso("Curso 1");
        var primeiraConclusao =
                new ConclusaoCurso(aluno, primeiroCurso, 8.0, true);

        plataforma.processarConclusao(primeiraConclusao);

        var segundoCurso = new Curso("Curso 2");
        var segundaConclusao =
                new ConclusaoCurso(aluno, segundoCurso, 7.5, true);

        // ACT
        plataforma.processarConclusao(segundaConclusao);

        // ASSERT
        assertEquals(6, aluno.getCursoAdicionaisLiberados());
    }
}