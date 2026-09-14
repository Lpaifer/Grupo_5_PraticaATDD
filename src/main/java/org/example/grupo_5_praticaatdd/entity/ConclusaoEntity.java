package org.example.grupo_5_praticaatdd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "conclusoes")
public class ConclusaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private AlunoEntity aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private CursoEntity curso;

    @Column(nullable = false)
    private double nota;

    @Column(nullable = false)
    private boolean concluido;

    @Column(nullable = false)
    private boolean aprovado;

    protected ConclusaoEntity() {
    }

    public ConclusaoEntity(AlunoEntity aluno, CursoEntity curso, double nota,
                           boolean concluido, boolean aprovado) {
        this.aluno = aluno;
        this.curso = curso;
        this.nota = nota;
        this.concluido = concluido;
        this.aprovado = aprovado;
    }

    public Long getId() {
        return id;
    }

    public AlunoEntity getAluno() {
        return aluno;
    }

    public CursoEntity getCurso() {
        return curso;
    }

    public double getNota() {
        return nota;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public boolean isAprovado() {
        return aprovado;
    }
}
