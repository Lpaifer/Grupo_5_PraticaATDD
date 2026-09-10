package org.example.grupo_5_praticaatdd.domain;

public class ConclusaoCurso {
    private Aluno aluno;
    private Curso curso;
    private double media;
    private boolean concluido;

    public ConclusaoCurso(
        Aluno aluno, 
        Curso curso, 
        double media,
        boolean concluido
    )   {
        this.aluno = aluno;
        this.curso = curso;
        this.media = media;
        this.concluido = concluido;
    }

    public boolean foiAprovado() {
        return concluido && this.media >= 7.0;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    public double getMedia() {
        return media;
    }

    public boolean isConcluido() {
        return concluido;
    }
}
