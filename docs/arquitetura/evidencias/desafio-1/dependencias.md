# Desafio 1 — comparação de dependências

Execução em **28/10/2026**, no fuso America/Sao_Paulo.

## Identificação da alteração

- **Antes:** commit `e112d7db12c292030c6980f98814f42de7903c38`, árvore de trabalho limpa no início do estudo.
- **Depois:** alteração local sobre esse commit, apresentada no diff para revisão, ainda sem commit próprio. Nenhuma publicação ou execução Jenkins foi realizada nesta etapa.
- **Base funcional:** `mvnw verify` executado antes da implementação, com 22 testes aprovados e cobertura integral nas classes analisadas. O log foi preservado em `artifacts/desafio-1/baseline-maven.log` antes da limpeza do build.

## Antes e depois

| Aspecto | Antes | Depois |
| --- | --- | --- |
| Consumidor da regra | `ConclusaoService` importava `domain.Aluno`, `Curso`, `ConclusaoCurso` e `PlataformaGamificacao` | Importa apenas `gamificacao.api.AvaliarConclusao` e `DadosConclusao`; o resultado também pertence à API pública |
| Aprovação | `ConclusaoCurso.foiAprovado()` | `gamificacao.internal.dominio.PoliticaConclusao.avaliar()` |
| Recompensa | `PlataformaGamificacao.processarConclusao()` alterava um aluno de domínio | A política calcula um resultado imutável com o saldo acrescido em três quando aprovado |
| Dados da fronteira | Objetos de aluno, curso e conclusão conhecidos pelo serviço | `DadosConclusao(double nota, boolean concluido, int cursosAdicionaisAtuais)` e `ResultadoConclusao(boolean aprovado, int cursosAdicionais)` |
| Persistência | Três repositórios chamados pelo serviço, sob `@Transactional` | Mesmos repositórios, entidades, tabelas e transação; o serviço aplica o resultado e registra a tentativa |
| Verificação das fronteiras | Sem teste arquitetural específico | Três testes ArchUnit executados pelo Surefire na fase `test` |
| Implantação | Um executável e uma imagem do backend | Mesmo executável e configuração de imagem, sem novo serviço |

Os quatro objetos de domínio usados apenas pelo cálculo foram substituídos; `domain.Plano` permanece porque `AlunoService` e `AlunoEntity` continuam utilizando-o. A validação HTTP permanece nos DTOs, e o serviço mantém a rejeição de notas não finitas. O contrato de avaliação recebe valores previamente validados pela aplicação.

Os oito cenários de `PlataformaGamificacaoTest` foram adaptados para o contrato público em `src/test/java/org/example/grupo_5_praticaatdd/gamificacao/api/`. Os nomes dos cenários usados na [rastreabilidade](../../../US-e-rastreabilidade.md) foram preservados. Os testes de API, persistência e demais serviços continuam usando o fluxo real.

## Regras automatizadas

`src/test/java/org/example/grupo_5_praticaatdd/arquitetura/GamificacaoArchitectureTest.java` analisa o bytecode das classes produtivas, excluindo classes de teste:

1. Código fora de `gamificacao` não pode depender de `gamificacao.internal`.
2. O módulo depende somente de `java..` e de `gamificacao..`: são proibidos Spring, JPA, HTTP e pacotes legados, inclusive repositórios.
3. Os records de `gamificacao.api` dependem somente de Java e da própria API pública.

ArchUnit está em escopo `test`, sem dependência adicional no JAR da aplicação. O nome `internal` sozinho não restringe acesso no compilador Java; a proteção é exercida pelo build. O experimento verifica referências de tipos, não apenas texto de imports ou nomes de diretórios.

## Experimento de rejeição controlada

Após a primeira execução aprovada de contrato e arquitetura (11 testes), foram adicionadas duas classes produtivas temporárias:

```java
// service/ViolacaoArquiteturalTemporaria.java
class ViolacaoArquiteturalTemporaria {
    private PoliticaConclusao politica; // import de gamificacao.internal.dominio
}

// gamificacao/internal/DependenciaTecnicaTemporaria.java
class DependenciaTecnicaTemporaria {
    private AlunoRepository alunos;     // import de repository
    private ApplicationContext contexto; // import do Spring
}
```

Comando executado no Windows, com Java 26 em `JAVA_HOME`:

```powershell
.\mvnw.cmd -B -ntp '-Dtest=GamificacaoArchitectureTest' test
```

Resultado esperado e observado:

```text
codigoExternoNaoAcessaImplementacaoInterna: FAILURE
Field <...service.ViolacaoArquiteturalTemporaria.politica>
has type <...gamificacao.internal.dominio.PoliticaConclusao>

moduloDependeSomenteDeJavaEDoProprioModulo: FAILURE
Field <...gamificacao.internal.DependenciaTecnicaTemporaria.alunos>
has type <...repository.AlunoRepository>
Field <...gamificacao.internal.DependenciaTecnicaTemporaria.contexto>
has type <org.springframework.context.ApplicationContext>

Tests run: 3, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

As duas classes temporárias foram removidas em seguida. `clean verify` foi executado para eliminar também seu bytecode e confirmou **25 testes aprovados**, incluindo as três regras arquiteturais, e todos os controles de cobertura. O log da rejeição está em `artifacts/desafio-1/violacao-controlada.log`; o da regressão final está em `artifacts/desafio-1/maven-verify.log`.

## Limites e continuidade

O recorte encapsula avaliação e recompensa. `AlunoService` ainda consulta o histórico pelo repositório legado, e as entidades de conclusão ainda se relacionam com aluno e curso. Cadastro, persistência e orquestração ainda não constituem módulos independentes. O experimento não acrescenta controle de concorrência ao saldo, migrações de banco, Ranking ou escala independente.

Consulte a [validação](validacao.md) para cobertura, execução da aplicação e verificações que dependem de Docker/HOMOL.
