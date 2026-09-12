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

    var curso = new Curso("Curso 1");

    var conclusao = new ConclusaoCurso(aluno, curso, 7.0, false);

    plataforma.processarConclusao(conclusao);

    assertEquals(0, aluno.getCursoAdicionaisLiberados());
}
}
