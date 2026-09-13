package org.example.grupo_5_praticaatdd.service;

import org.example.grupo_5_praticaatdd.api.dto.ConclusaoResponse;
import org.example.grupo_5_praticaatdd.api.dto.ProcessarConclusaoRequest;
import org.example.grupo_5_praticaatdd.domain.Aluno;
import org.example.grupo_5_praticaatdd.domain.ConclusaoCurso;
import org.example.grupo_5_praticaatdd.domain.Curso;
import org.example.grupo_5_praticaatdd.domain.PlataformaGamificacao;
import org.example.grupo_5_praticaatdd.entity.ConclusaoEntity;
import org.example.grupo_5_praticaatdd.repository.AlunoRepository;
import org.example.grupo_5_praticaatdd.repository.ConclusaoRepository;
import org.example.grupo_5_praticaatdd.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConclusaoService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final ConclusaoRepository conclusaoRepository;
    private final PlataformaGamificacao plataforma = new PlataformaGamificacao();

    public ConclusaoService(AlunoRepository alunoRepository, CursoRepository cursoRepository,
                            ConclusaoRepository conclusaoRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.conclusaoRepository = conclusaoRepository;
    }

    @Transactional
    public ConclusaoResponse processar(ProcessarConclusaoRequest request) {
        if (!Double.isFinite(request.nota())) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10.");
        }

        var aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado: " + request.alunoId()));
        var curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado: " + request.cursoId()));

        var alunoDominio = new Aluno(aluno.getNome(), aluno.getPlano(), aluno.getCursosAdicionais());
        var conclusaoDominio = new ConclusaoCurso(alunoDominio, new Curso(curso.getNome()),
                request.nota(), request.concluido());
        plataforma.processarConclusao(conclusaoDominio);

        aluno.atualizarCursosAdicionais(alunoDominio.getCursosAdicionaisLiberados());
        conclusaoRepository.save(new ConclusaoEntity(aluno, curso, request.nota(),
                request.concluido(), conclusaoDominio.foiAprovado()));

        return new ConclusaoResponse(aluno.getId(), aluno.getNome(), curso.getNome(), request.nota(),
                request.concluido(), conclusaoDominio.foiAprovado(), aluno.getCursosAdicionais());
    }
}
