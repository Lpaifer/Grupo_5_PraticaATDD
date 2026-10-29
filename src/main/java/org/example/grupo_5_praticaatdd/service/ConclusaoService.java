package org.example.grupo_5_praticaatdd.service;

import org.example.grupo_5_praticaatdd.api.dto.ConclusaoResponse;
import org.example.grupo_5_praticaatdd.api.dto.ProcessarConclusaoRequest;
import org.example.grupo_5_praticaatdd.gamificacao.api.AvaliarConclusao;
import org.example.grupo_5_praticaatdd.gamificacao.api.DadosConclusao;
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
    private final AvaliarConclusao avaliarConclusao = new AvaliarConclusao();

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

        var resultado = avaliarConclusao.avaliar(new DadosConclusao(
                request.nota(), request.concluido(), aluno.getCursosAdicionais()));

        aluno.atualizarCursosAdicionais(resultado.cursosAdicionais());
        conclusaoRepository.save(new ConclusaoEntity(aluno, curso, request.nota(),
                request.concluido(), resultado.aprovado()));

        return new ConclusaoResponse(aluno.getId(), aluno.getNome(), curso.getNome(), request.nota(),
                request.concluido(), resultado.aprovado(), aluno.getCursosAdicionais());
    }
}
