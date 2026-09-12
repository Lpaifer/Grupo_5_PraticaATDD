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

        assertEquals(0, aluno.getCursoAdicionaisLiberados());

    }

    // Cenário (Matheus Arizono):
    // Dado que o aluno reprovou um curso anteriormente com média abaixo de 7,0
    // E refaz o mesmo curso obtendo média final de 8,0
    // Quando o sistema processa a nova conclusão do curso
    // Então o aluno deve ter acesso liberado a mais 3 cursos
    @Test
    public void deveLiberarTresCursosAoRefazerCursoEObterMedia8() {
        var plataforma = new PlataformaGamificacao();
        var aluno = new Aluno("Matheus Arizono", Plano.Basico);
        var curso = new Curso("Curso 1");

        // primeira tentativa: reprovado (média < 7,0) -> nada é liberado
        var primeiraConclusao = new ConclusaoCurso(aluno, curso, 3.0, true);
        plataforma.processarConclusao(primeiraConclusao);
        assertEquals(0, aluno.getCursoAdicionaisLiberados());

        // aluno refaz o mesmo curso e obtém média 8,0
        var novaConclusao = new ConclusaoCurso(aluno, curso, 8.0, true);
        plataforma.processarConclusao(novaConclusao);

        assertEquals(3, aluno.getCursoAdicionaisLiberados());
    }

}
