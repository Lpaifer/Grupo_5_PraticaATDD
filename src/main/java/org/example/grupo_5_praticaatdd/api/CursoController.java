package org.example.grupo_5_praticaatdd.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.grupo_5_praticaatdd.api.dto.CriarCursoRequest;
import org.example.grupo_5_praticaatdd.api.dto.CursoResponse;
import org.example.grupo_5_praticaatdd.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Cadastro e listagem de cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar curso")
    public ResponseEntity<CursoResponse> criar(@Valid @RequestBody CriarCursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.criar(request.nome()));
    }

    @GetMapping
    @Operation(summary = "Listar cursos")
    public List<CursoResponse> listar() {
        return cursoService.listar();
    }
}
