package org.example.grupo_5_praticaatdd.service;

import java.util.List;

import org.example.grupo_5_praticaatdd.api.dto.AlunoResponse;
import org.example.grupo_5_praticaatdd.api.dto.CriarAlunoRequest;
import org.example.grupo_5_praticaatdd.api.dto.HistoricoCursoResponse;
import org.example.grupo_5_praticaatdd.domain.Plano;
import org.example.grupo_5_praticaatdd.entity.AlunoEntity;
import org.example.grupo_5_praticaatdd.entity.ConclusaoEntity;
import org.example.grupo_5_praticaatdd.repository.AlunoRepository;
import org.example.grupo_5_praticaatdd.repository.ConclusaoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ConclusaoRepository conclusaoRepository;

    public AlunoService(AlunoRepository alunoRepository, ConclusaoRepository conclusaoRepository) {
        this.alunoRepository = alunoRepository;
        this.conclusaoRepository = conclusaoRepository;
    }

    @Transactional
    public AlunoResponse criar(CriarAlunoRequest request) {
        var aluno = alunoRepository.save(new AlunoEntity(
                request.nome().trim(), Plano.fromRotulo(request.plano())));
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getPlano().getRotulo(), 0, List.of());
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscar(Long id) {
        var aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado: " + id));
        return toResponse(aluno);
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> listar() {
        return alunoRepository.findAll(Sort.by("id")).stream()
                .map(this::toResponse)
                .toList();
    }

    private AlunoResponse toResponse(AlunoEntity aluno) {
        var historico = conclusaoRepository.findByAluno_IdOrderByIdAsc(aluno.getId()).stream()
                .map(this::toHistoricoResponse)
                .toList();
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getPlano().getRotulo(),
                aluno.getCursosAdicionais(), historico);
    }

    private HistoricoCursoResponse toHistoricoResponse(ConclusaoEntity conclusao) {
        return new HistoricoCursoResponse(conclusao.getCurso().getId(), conclusao.getCurso().getNome(),
                conclusao.getNota(), conclusao.isConcluido(), conclusao.isAprovado());
    }
}
