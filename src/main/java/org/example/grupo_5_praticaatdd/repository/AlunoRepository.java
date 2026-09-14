package org.example.grupo_5_praticaatdd.repository;

import org.example.grupo_5_praticaatdd.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> {
}
