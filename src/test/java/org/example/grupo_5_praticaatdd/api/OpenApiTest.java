package org.example.grupo_5_praticaatdd.api;

import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void documentaFluxosDaApi() throws Exception {
        String documento = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Map<String, Object>> caminhos = JsonPath.read(documento, "$.paths");
        assertTrue(caminhos.get("/api/alunos").containsKey("post"));
        assertTrue(caminhos.get("/api/alunos").containsKey("get"));
        assertTrue(caminhos.get("/api/alunos/{id}").containsKey("get"));
        assertTrue(caminhos.get("/api/cursos").containsKey("post"));
        assertTrue(caminhos.get("/api/cursos").containsKey("get"));
        assertTrue(caminhos.get("/api/conclusoes").containsKey("post"));
    }

    @Test
    void swaggerUiEstaDisponivel() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
