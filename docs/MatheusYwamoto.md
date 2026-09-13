# Matheus Arizono - ATDD

## BDD

### Cenário: Liberação de cursos adicionais ao refazer um curso reprovado

**DADO QUE** o aluno do plano Básico reprovou um curso anteriormente com média abaixo de 7,0  
**E** refez o mesmo curso obtendo média final de 8,0  
**QUANDO** o sistema processar a nova conclusão do curso  
**ENTÃO** o aluno deve ter acesso liberado a mais 3 cursos adicionais.

---

## TDD

### RED

Foi criado o teste:

`deveLiberarTresCursosAoRefazerCursoEObterMedia8()`

O teste registra uma primeira conclusão reprovada (média 3,0), confirma que nenhum curso foi liberado e, em seguida, registra a nova conclusão do mesmo curso com média 8,0, esperando 3 cursos adicionais.

No primeiro momento, o fluxo do teste ainda não processava a nova conclusão do curso refeito: a segunda chamada de `processarConclusao` recebia a conclusão reprovada, e nenhum curso era liberado. O teste esperava 3 cursos adicionais, porém o sistema retornava 0.

Resultado obtido:

```text
org.opentest4j.AssertionFailedError: expected: <3> but was: <0>
Tests run: 2, Failures: 1, Errors: 0
BUILD FAILURE
```

### Evidência RED

![RED - Matheus Arizono](evidencias/MatheusArizono_RED.png)

---

### GREEN

O processamento passou a receber a nova conclusão do curso refeito (`novaConclusao`, média 8,0). Como a regra de aprovação (`media >= 7.0`) já estava implementada em `ConclusaoCurso.foiAprovado()`, a nova conclusão aprovada libera os 3 cursos adicionais ao aluno.

Teste final:

```java
@Test
public void deveLiberarTresCursosAoRefazerCursoEObterMedia8() {
    var plataforma = new PlataformaGamificacao();
    var aluno = new Aluno("Matheus Arizono", Plano.Basico);
    var curso = new Curso("Curso 1");

    // primeira tentativa: reprovado (média < 7,0) -> nada é liberado
    var primeiraConclusao = new ConclusaoCurso(aluno, curso, 3.0, true);
    plataforma.processarConclusao(primeiraConclusao);
    assertEquals(0, aluno.getCursoAdicionaisLiberados());

    // aluno refaz o mesmo curso e obtém média 8,0
    var novaConclusao = new ConclusaoCurso(aluno, curso, 8.0, true);
    plataforma.processarConclusao(novaConclusao);

    assertEquals(3, aluno.getCursoAdicionaisLiberados());
}
```

Após a correção, os testes passaram com sucesso:

```text
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Evidência GREEN

![GREEN - Matheus Arizono](evidencias/MatheusArizono_GREEN.png)

![Terminal - Matheus Arizono](evidencias/MatheusArizono_TERMINAL.png)

---

## Cobertura de Testes - JaCoCo

Após a etapa GREEN, foi executado o JaCoCo (versão 0.8.15) para análise da cobertura dos testes, com o comando:

```bash
./mvnw org.jacoco:jacoco-maven-plugin:0.8.15:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.15:report
```

Resultados obtidos:

| Pacote | Instruções | Branches |
|---|---|---|
| `org.example.grupo_5_praticaatdd.domain` | **82%** | **100%** (0 de 6 perdidos) |
| `org.example.grupo_5_praticaatdd` | 37% | n/a |
| **Total** | **79%** (23 de 110 instruções perdidas) | **100%** |

Todos os 6 branches do pacote `domain` foram cobertos, incluindo os dois lados da regra de aprovação (`concluido && media >= 7.0`) exercitados pelo cenário de reprovação seguida de aprovação.

As instruções não cobertas no pacote `domain` correspondem a getters ainda não utilizados pelos testes (`getNome`, `getPlano`, `getCurso`, `getMedia`, `isConcluido`). O pacote raiz contém apenas a classe de inicialização do Spring Boot (`Grupo5PraticaAtddApplication`), cujo método `main` não é executado pelos testes.

### Evidência JaCoCo

![JaCoCo - Matheus Arizono](evidencias/MatheusArizono_JACOCO.png)

---

## BLUE - Refatoração

Na etapa BLUE foi realizada refatoração sem alterar a regra de negócio da aplicação.

### Documentação do cenário no teste

O cenário BDD foi registrado em comentários diretamente acima do teste, no formato Dado / E / Quando / Então, mantendo a rastreabilidade entre o critério de aceitação e o código:

```java
// Cenário (Matheus Arizono):
// Dado que o aluno reprovou um curso anteriormente com média abaixo de 7,0
// E refaz o mesmo curso obtendo média final de 8,0
// Quando o sistema processa a nova conclusão do curso
// Então o aluno deve ter acesso liberado a mais 3 cursos
```

### Nomenclatura das variáveis

As conclusões passaram a ter nomes que descrevem o momento do cenário — `primeiraConclusao` (tentativa reprovada) e `novaConclusao` (curso refeito) — e a asserção intermediária (`assertEquals(0, ...)`) foi adicionada para tornar explícito o passo "Dado que o aluno reprovou", evitando um falso positivo caso a regra de aprovação liberasse cursos indevidamente.

Após as refatorações, todos os testes continuaram passando:

```text
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Commits

- `<hash>` - RED - criar teste de liberacao de cursos ao refazer curso reprovado
- `<hash>` - GREEN - processar nova conclusao do curso refeito
- `<hash>` - JACOCO - adicionar plugin de cobertura ao pom.xml
