package org.example.grupo_5_praticaatdd.api;

import org.example.grupo_5_praticaatdd.domain.Plano;
import org.example.grupo_5_praticaatdd.entity.AlunoEntity;
import org.example.grupo_5_praticaatdd.entity.CursoEntity;
import org.example.grupo_5_praticaatdd.repository.*;
import org.example.grupo_5_praticaatdd.service.ConclusaoService;
import org.example.grupo_5_praticaatdd.api.dto.ProcessarConclusaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RankingApiTest {
    @Autowired MockMvc mvc;
    @Autowired AlunoRepository alunos;
    @Autowired CursoRepository cursos;
    @Autowired ConclusaoRepository conclusoes;
    @Autowired ConclusaoService servico;
    private AlunoEntity a;
    private AlunoEntity b;
    private CursoEntity curso;

    @BeforeEach
    void preparar() {
        conclusoes.deleteAllInBatch();
        alunos.deleteAllInBatch();
        cursos.deleteAllInBatch();
        a = alunos.save(new AlunoEntity("A", Plano.Basico));
        b = alunos.save(new AlunoEntity("B", Plano.Basico));
        curso = cursos.save(new CursoEntity("Curso"));
    }

    private void concluir(AlunoEntity aluno, double nota, boolean concluido) {
        servico.processar(new ProcessarConclusaoRequest(aluno.getId(), curso.getId(), nota, concluido));
    }

    @Test
    void semAprovacoesRetorna200EListaVazia() throws Exception {
        mvc.perform(get("/api/ranking")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void reprovacaoNaoConta() throws Exception {
        concluir(a, 6.9, true);
        mvc.perform(get("/api/ranking")).andExpect(content().json("[]"));
    }

    @Test
    void cursoIncompletoNaoConta() throws Exception {
        concluir(a, 10, false);
        mvc.perform(get("/api/ranking")).andExpect(content().json("[]"));
    }

    @Test
    void contaCadaAprovacaoInclusiveRepetidaEOrdena() throws Exception {
        concluir(a, 7, true);
        concluir(b, 8, true);
        concluir(b, 9, true);
        concluir(b, 10, true);
        concluir(b, 6.9, true);
        concluir(b, 10, false);
        mvc.perform(get("/api/ranking")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].posicao").value(1))
                .andExpect(jsonPath("$[0].alunoId").value(b.getId()))
                .andExpect(jsonPath("$[0].nome").value("B"))
                .andExpect(jsonPath("$[0].aprovacoes").value(3))
                .andExpect(jsonPath("$[1].aprovacoes").value(1));
    }

    @Test
    void empateUsaMenorAlunoId() throws Exception {
        concluir(b, 7, true);
        concluir(a, 7, true);
        mvc.perform(get("/api/ranking"))
                .andExpect(jsonPath("$[0].alunoId").value(a.getId()))
                .andExpect(jsonPath("$[1].alunoId").value(b.getId()));
    }
}
