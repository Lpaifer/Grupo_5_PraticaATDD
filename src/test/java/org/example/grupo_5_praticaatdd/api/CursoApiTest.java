package org.example.grupo_5_praticaatdd.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CursoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cursoCriadoPodeSerConsultadoPelaApi() throws Exception {
        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Geologia\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Geologia"));

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Geologia")));
    }

    @Test
    void nomeEmBrancoNaoCadastraCurso() throws Exception {
        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\" \"}"))
                .andExpect(status().isBadRequest());
    }
}
