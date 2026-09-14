package org.example.grupo_5_praticaatdd.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.grupo_5_praticaatdd.api.dto.ConclusaoResponse;
import org.example.grupo_5_praticaatdd.api.dto.ProcessarConclusaoRequest;
import org.example.grupo_5_praticaatdd.service.ConclusaoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conclusoes")
@Tag(name = "Conclusões", description = "Processamento de conclusões e liberação de cursos")
public class ConclusaoController {

    private final ConclusaoService conclusaoService;

    public ConclusaoController(ConclusaoService conclusaoService) {
        this.conclusaoService = conclusaoService;
    }

    @PostMapping
    @Operation(summary = "Processar conclusão", description = "Registra a nota, atualiza o histórico e calcula os cursos adicionais")
    public ConclusaoResponse processar(@Valid @RequestBody ProcessarConclusaoRequest request) {
        return conclusaoService.processar(request);
    }
}
