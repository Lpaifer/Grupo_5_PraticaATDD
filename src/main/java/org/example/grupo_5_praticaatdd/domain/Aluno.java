package org.example.grupo_5_praticaatdd.domain;

public class Aluno {
    private String nome;
    private Plano plano;
    private int cursosAdicionaisLiberados;

    public Aluno(String nome, Plano plano) {
        this(nome, plano, 0);
    }

    public Aluno(String nome, Plano plano, int cursosAdicionaisLiberados) {
        this.nome = nome;
        this.plano = plano;
        this.cursosAdicionaisLiberados = cursosAdicionaisLiberados;
    }

    public void liberarCursos(int quantidade) {
        this.cursosAdicionaisLiberados += quantidade;
    }

    public String getNome() {
        return nome;
    }

    public Plano getPlano() {
        return plano;
    }

    public int getCursosAdicionaisLiberados() {
        return cursosAdicionaisLiberados;
    }
}
