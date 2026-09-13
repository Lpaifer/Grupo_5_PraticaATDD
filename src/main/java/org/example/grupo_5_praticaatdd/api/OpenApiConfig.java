package org.example.grupo_5_praticaatdd.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI plataformaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Educação Continuada Gamificada")
                .version("1.0")
                .description("API para alunos, cursos, conclusões e histórico de aprendizagem."));
    }
}
