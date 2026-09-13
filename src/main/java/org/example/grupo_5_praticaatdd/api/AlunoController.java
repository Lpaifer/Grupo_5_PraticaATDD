package org.example.grupo_5_praticaatdd.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.grupo_5_praticaatdd.api.dto.AlunoResponse;
import org.example.grupo_5_praticaatdd.api.dto.CriarAlunoRequest;
import org.example.grupo_5_praticaatdd.service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Cadastro e consulta de alunos e do histórico de cursos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar aluno", description = "Cria um aluno em um plano")
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody CriarAlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.criar(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar aluno", description = "Retorna o aluno, os cursos liberados e seu histórico")
    public AlunoResponse buscar(@PathVariable Long id) {
        return alunoService.buscar(id);
    }

    @GetMapping
    @Operation(summary = "Listar alunos")
    public List<AlunoResponse> listar() {
        return alunoService.listar();
    }
}
