package org.example.grupo_5_praticaatdd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlataformaGamificacaoTest {

    @Test
    void processamentoPreservaDadosOriginaisDaConclusao() {
        var aluno = new Aluno("Ana", Plano.Basico);
        var curso = new Curso("Fundamentos de Java");
        var conclusao = new ConclusaoCurso(aluno, curso, 7.0, true);

        new PlataformaGamificacao().processarConclusao(conclusao);

        assertEquals("Ana", aluno.getNome());
        assertEquals(Plano.Basico, aluno.getPlano());
        assertSame(curso, conclusao.getCurso());
        assertEquals("Fundamentos de Java", curso.getNome());
        assertEquals(7.0, conclusao.getMedia());
        assertTrue(conclusao.isConcluido());
        assertEquals(3, aluno.getCursosAdicionaisLiberados());
    }

    @Test
    public void deveLiberarTresCursosAoRefazerCursoEObterMedia8() {
        var plataforma = new PlataformaGamificacao();
        var aluno = new Aluno("Matheus Arizono", Plano.Basico);
        var curso = new Curso("Curso 1");

        var primeiraConclusao = new ConclusaoCurso(aluno, curso, 3.0, true);
        plataforma.processarConclusao(primeiraConclusao);
        assertEquals(0, aluno.getCursosAdicionaisLiberados());

        var novaConclusao = new ConclusaoCurso(aluno, curso, 8.0, true);
        plataforma.processarConclusao(novaConclusao);

        assertEquals(3, aluno.getCursosAdicionaisLiberados());
    }
    @Test
    public void naoDeveLiberarCursosQuandoMediaForMenorQue7() {

        var plataforma = new PlataformaGamificacao();

        var aluno = new Aluno("Matheus Marcolino", Plano.Basico);

        var curso = new Curso("Curso 1");

        var conclusao = new ConclusaoCurso(aluno, curso, 6.5, true);

        plataforma.processarConclusao(conclusao);

        assertEquals(0, aluno.getCursosAdicionaisLiberados());
    }

    @Test
    public void naoDeveLiberarCursosQuandoCursoNaoFoiConcluido() {

        var plataforma = new PlataformaGamificacao();

        var aluno = new Aluno("Matheus Marcolino", Plano.Basico);
        assertEquals(0, aluno.getCursosAdicionaisLiberados());

        var curso = new Curso("Curso 1");

        var conclusao = new ConclusaoCurso(aluno, curso, 7.0, false);

        plataforma.processarConclusao(conclusao);

        assertEquals(0, aluno.getCursosAdicionaisLiberados());
    }
  
    @Test
    public void naoDeveLiberarCursosEnquantoCursoNaoForConcluido() {
    var plataforma = new PlataformaGamificacao();
    var aluno = new Aluno("Beatriz Canaveze", Plano.Basico);
    var curso = new Curso("Curso em andamento");
    var conclusao = new ConclusaoCurso(aluno, curso, 0.0, false);

    plataforma.processarConclusao(conclusao);

    assertEquals(0, aluno.getCursosAdicionaisLiberados());


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
