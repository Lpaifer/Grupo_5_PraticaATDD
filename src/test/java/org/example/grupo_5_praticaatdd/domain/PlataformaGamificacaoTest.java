package org.example.grupo_5_praticaatdd.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlataformaGamificacaoTest {

@Test 
void DeveLiberarTresCursosQuandoAlunoConcluirComMediaNove() {
    var plataforma = new PlataformaGamificacao();

    var aluno = new Aluno("Lucas Paifer", Plano.Basico);
    var curso1 = new Curso("Java");

    aluno.concluirCurso(curso1; 9.0);

    plataforma.processarConclusao(aluno, curso1);

    assertEquals(3, aluno.getCursoAdicionaisLiberados());
}

}
