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


}
