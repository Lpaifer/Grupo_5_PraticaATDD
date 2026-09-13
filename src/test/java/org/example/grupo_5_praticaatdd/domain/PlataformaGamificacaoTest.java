package org.example.grupo_5_praticaatdd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlataformaGamificacaoTest {

@Test
public void naoDeveLiberarCursosEnquantoCursoNaoForConcluido() {
var plataforma = new PlataformaGamificacao();
var aluno = new Aluno("Beatriz Canaveze", Plano.Basico);
var curso = new Curso("Curso em andamento");
var conclusao = new ConclusaoCurso(aluno, curso, 0.0, false);

plataforma.processarConclusao(conclusao);

    assertEquals(3, aluno.getCursoAdicionaisLiberados());
}
@Test
public void naoDeveLiberarCursosQuandoMediaForMenorQue7() {

    var plataforma = new PlataformaGamificacao();

    var aluno = new Aluno("Matheus Marcolino", Plano.Basico);

    var curso = new Curso("Curso 1");

    var conclusao = new ConclusaoCurso(aluno, curso, 6.5, true);

    plataforma.processarConclusao(conclusao);

    assertEquals(0, aluno.getCursoAdicionaisLiberados());
}
@Test
public void naoDeveLiberarCursosQuandoCursoNaoFoiConcluido() {

    var plataforma = new PlataformaGamificacao();

    var aluno = new Aluno("Matheus Marcolino", Plano.Basico);
assertEquals(0, aluno.getCursoAdicionaisLiberados());

    var curso = new Curso("Curso 1");

    var conclusao = new ConclusaoCurso(aluno, curso, 7.0, false);

    plataforma.processarConclusao(conclusao);

    assertEquals(0, aluno.getCursoAdicionaisLiberados());
}
}
    @Test
    public void deveLiberarTresCursosQuandoMediaForExatamente7() {
        var plataforma = new PlataformaGamificacao();
        var aluno = new Aluno("João", Plano.Basico);
        var curso = new Curso("Curso 1");
        var conclusao = new ConclusaoCurso(aluno, curso, 7.0, true);

        plataforma.processarConclusao(conclusao);

        assertEquals(3, aluno.getCursosAdicionaisLiberados());
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
        assertEquals(6, aluno.getCursosAdicionaisLiberados());
    }
}