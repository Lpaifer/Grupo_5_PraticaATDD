package org.example.grupo_5_praticaatdd.service;

import java.util.List;

import org.example.grupo_5_praticaatdd.api.dto.CursoResponse;
import org.example.grupo_5_praticaatdd.entity.CursoEntity;
import org.example.grupo_5_praticaatdd.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public CursoResponse criar(String nome) {
        var curso = cursoRepository.save(new CursoEntity(nome.trim()));
        return new CursoResponse(curso.getId(), curso.getNome());
    }

    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {
        return cursoRepository.findAll().stream()
                .map(curso -> new CursoResponse(curso.getId(), curso.getNome()))
                .toList();
    }
}
