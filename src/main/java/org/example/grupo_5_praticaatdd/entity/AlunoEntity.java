package org.example.grupo_5_praticaatdd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.example.grupo_5_praticaatdd.domain.Plano;

@Entity
@Table(name = "alunos")
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Plano plano;

    @Column(nullable = false)
    private int cursosAdicionais;

    protected AlunoEntity() {
    }

    public AlunoEntity(String nome, Plano plano) {
        this.nome = nome;
        this.plano = plano;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Plano getPlano() {
        return plano;
    }

    public int getCursosAdicionais() {
        return cursosAdicionais;
    }

    public void atualizarCursosAdicionais(int cursosAdicionais) {
        this.cursosAdicionais = cursosAdicionais;
    }
}
