package org.example.grupo_5_praticaatdd.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.example.grupo_5_praticaatdd.repository.ConclusaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class H2PersistenceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConclusaoRepository conclusaoRepository;

    @Test
    void conclusaoProcessadaFicaRegistradaNasTabelasH2() throws Exception {
        String banco = jdbcTemplate.execute((ConnectionCallback<String>) connection ->
                connection.getMetaData().getDatabaseProductName());
        assertEquals("H2", banco);

        long cursoId = criar("/api/cursos", "{\"nome\":\"Curso H2\"}");
        long alunoId = criar("/api/alunos", "{\"nome\":\"Aluno H2\",\"plano\":\"Basico\"}");

        mockMvc.perform(post("/api/conclusoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alunoId\":%d,\"cursoId\":%d,\"nota\":7.0,\"concluido\":true}"
                                .formatted(alunoId, cursoId)))
                .andExpect(status().isOk());

        Integer cursosAdicionais = jdbcTemplate.queryForObject(
                "SELECT cursos_adicionais FROM alunos WHERE id = ?", Integer.class, alunoId);
        Boolean aprovado = jdbcTemplate.queryForObject(
                "SELECT aprovado FROM conclusoes WHERE aluno_id = ? AND curso_id = ?",
                Boolean.class, alunoId, cursoId);

        assertEquals(3, cursosAdicionais);
        assertEquals(true, aprovado);

        var registro = conclusaoRepository.findByAluno_IdOrderByIdAsc(alunoId).getFirst();
        assertNotNull(registro.getId());
        assertEquals(alunoId, registro.getAluno().getId());
    }

    private long criar(String caminho, String corpo) throws Exception {
        String resposta = mockMvc.perform(post(caminho)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(resposta, "$.id")).longValue();
    }
}
