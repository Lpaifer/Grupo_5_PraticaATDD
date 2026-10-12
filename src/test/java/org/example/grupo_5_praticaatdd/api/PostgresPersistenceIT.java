package org.example.grupo_5_praticaatdd.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("postgres-test")
class PostgresPersistenceIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void tentativasERecompensasSaoPersistidasNoPostgresReal() throws Exception {
        String banco = jdbcTemplate.execute((ConnectionCallback<String>) connection ->
                connection.getMetaData().getDatabaseProductName());
        assertEquals("PostgreSQL", banco);

        long alunoId = criar("/api/alunos", "{\"nome\":\"Aluno CI\",\"plano\":\"Básico\"}");
        long cursoId = criar("/api/cursos", "{\"nome\":\"Curso CI\"}");
        for (double nota : new double[]{6.5, 7.0}) {
            mockMvc.perform(post("/api/conclusoes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"alunoId\":%d,\"cursoId\":%d,\"nota\":%s,\"concluido\":true}"
                                    .formatted(alunoId, cursoId, nota)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/alunos/{id}", alunoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursosAdicionais").value(3))
                .andExpect(jsonPath("$.historico.length()").value(2))
                .andExpect(jsonPath("$.historico[0].aprovado").value(false))
                .andExpect(jsonPath("$.historico[1].aprovado").value(true));

        assertEquals(3, jdbcTemplate.queryForObject(
                "SELECT cursos_adicionais FROM alunos WHERE id = ?", Integer.class, alunoId));
        assertEquals(2, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM conclusoes WHERE aluno_id = ?", Integer.class, alunoId));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM conclusoes WHERE aluno_id = ? AND aprovado = true",
                Integer.class, alunoId));
    }

    private long criar(String caminho, String corpo) throws Exception {
        String resposta = mockMvc.perform(post(caminho)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(resposta, "$.id")).longValue();
    }
}
