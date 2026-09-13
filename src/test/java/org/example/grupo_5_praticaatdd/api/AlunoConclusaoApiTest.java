package org.example.grupo_5_praticaatdd.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AlunoConclusaoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void alunoCriadoPodeSerBuscadoEListado() throws Exception {
        long alunoId = criarAluno("Leonardo", "Básico");

        mockMvc.perform(get("/api/alunos/{id}", alunoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alunoId))
                .andExpect(jsonPath("$.nome").value("Leonardo"))
                .andExpect(jsonPath("$.plano").value("Básico"))
                .andExpect(jsonPath("$.cursosAdicionais").value(0))
                .andExpect(jsonPath("$.historico").isEmpty());

        mockMvc.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Leonardo")));
    }

    @Test
    void tentativasFicamNoHistoricoEAprovacoesAcumulamCursos() throws Exception {
        long alunoId = criarAluno("Beatriz", "Premium");
        long cursoId = criarCurso("Geologia");

        processarConclusao(alunoId, cursoId, 8.0, false, false, 0);
        processarConclusao(alunoId, cursoId, 6.5, true, false, 0);
        processarConclusao(alunoId, cursoId, 7.0, true, true, 3);
        processarConclusao(alunoId, cursoId, 8.0, true, true, 6);

        mockMvc.perform(get("/api/alunos/{id}", alunoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursosAdicionais").value(6))
                .andExpect(jsonPath("$.historico.length()").value(4))
                .andExpect(jsonPath("$.historico[0].cursoId").value(cursoId))
                .andExpect(jsonPath("$.historico[0].concluido").value(false))
                .andExpect(jsonPath("$.historico[0].aprovado").value(false))
                .andExpect(jsonPath("$.historico[1].nota").value(6.5))
                .andExpect(jsonPath("$.historico[1].aprovado").value(false))
                .andExpect(jsonPath("$.historico[2].nota").value(7.0))
                .andExpect(jsonPath("$.historico[2].aprovado").value(true))
                .andExpect(jsonPath("$.historico[3].cursoId").value(cursoId))
                .andExpect(jsonPath("$.historico[3].aprovado").value(true));
    }

    @Test
    void entradasInvalidasNaoRegistramConclusao() throws Exception {
        long alunoId = criarAluno("Lucas", "Básico");
        long cursoId = criarCurso("Física");

        mockMvc.perform(post("/api/conclusoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conclusaoJson(alunoId, cursoId, 11.0, true)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/alunos/{id}", alunoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.historico").isEmpty())
                .andExpect(jsonPath("$.cursosAdicionais").value(0));

        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"plano\":\"Inexistente\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void idsInexistentesRetornamNaoEncontrado() throws Exception {
        long cursoId = criarCurso("Química");
        long alunoId = criarAluno("Rafael", "Básico");

        mockMvc.perform(get("/api/alunos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/conclusoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conclusaoJson(Long.MAX_VALUE, cursoId, 7.0, true)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/conclusoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conclusaoJson(alunoId, Long.MAX_VALUE, 7.0, true)))
                .andExpect(status().isNotFound());
    }

    @Test
    void frontendLocalPodeChamarApi() throws Exception {
        mockMvc.perform(options("/api/conclusoes")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    private long criarAluno(String nome, String plano) throws Exception {
        var resultado = mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"%s\",\"plano\":\"%s\"}".formatted(nome, plano)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.plano").value(plano))
                .andExpect(jsonPath("$.cursosAdicionais").value(0))
                .andExpect(jsonPath("$.historico").isEmpty())
                .andReturn();
        return ((Number) JsonPath.read(resultado.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private long criarCurso(String nome) throws Exception {
        var resultado = mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"%s\"}".formatted(nome)))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(resultado.getResponse().getContentAsString(), "$.id")).longValue();
    }

    private void processarConclusao(long alunoId, long cursoId, double nota, boolean concluido,
                                    boolean aprovado, int cursosAdicionais) throws Exception {
        mockMvc.perform(post("/api/conclusoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conclusaoJson(alunoId, cursoId, nota, concluido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunoId").value(alunoId))
                .andExpect(jsonPath("$.aluno").value("Beatriz"))
                .andExpect(jsonPath("$.curso").value("Geologia"))
                .andExpect(jsonPath("$.nota").value(nota))
                .andExpect(jsonPath("$.concluido").value(concluido))
                .andExpect(jsonPath("$.aprovado").value(aprovado))
                .andExpect(jsonPath("$.cursosAdicionais").value(cursosAdicionais));
    }

    private String conclusaoJson(long alunoId, long cursoId, double nota, boolean concluido) {
        return "{\"alunoId\":%d,\"cursoId\":%d,\"nota\":%s,\"concluido\":%s}"
                .formatted(alunoId, cursoId, nota, concluido);
    }
}
