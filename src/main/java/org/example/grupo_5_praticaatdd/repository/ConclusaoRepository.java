package org.example.grupo_5_praticaatdd.repository;

import java.util.List;

import org.example.grupo_5_praticaatdd.entity.ConclusaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConclusaoRepository extends JpaRepository<ConclusaoEntity, Long> {

    List<ConclusaoEntity> findByAluno_IdOrderByIdAsc(Long alunoId);
}
