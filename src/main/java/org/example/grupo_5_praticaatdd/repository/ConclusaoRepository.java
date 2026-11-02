package org.example.grupo_5_praticaatdd.repository;

import java.util.List;

import org.example.grupo_5_praticaatdd.entity.ConclusaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConclusaoRepository extends JpaRepository<ConclusaoEntity, Long> {

    @org.springframework.data.jpa.repository.Query("""
            select new org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes(
                c.aluno.id, c.aluno.nome, count(c))
            from ConclusaoEntity c
            where c.aprovado = true
            group by c.aluno.id, c.aluno.nome
            """)
    List<org.example.grupo_5_praticaatdd.ranking.api.ResumoAprovacoes> contarAprovacoesPorAluno();

    List<ConclusaoEntity> findByAluno_IdOrderByIdAsc(Long alunoId);
}
