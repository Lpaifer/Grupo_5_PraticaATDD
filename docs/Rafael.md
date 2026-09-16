# Rafael Amorim - ATDD

## BDD

### Cenário: Liberação de cursos adicionais com média 9,0

**DADO QUE** o aluno concluiu um curso com média final de 9,0  
**QUANDO** o sistema processa a conclusão do curso  
**ENTÃO** o aluno deve ter acesso liberado a mais 3 cursos adicionais.

---

## TDD

### RED

Foi criado o teste:

`deveLiberarTresCursosQuandoAlunoConcluirComMedia9()`

Para registrar a etapa RED, o teste foi executado temporariamente esperando `0` cursos adicionais, enquanto o cenário BDD esperava a liberação de 3 cursos. Como a regra de negócio já liberava os cursos para média 9,0, a asserção falhou:

```text
expected: <0> but was: <3>
Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
BUILD FAILURE
```

Esse RED confirma que o teste detecta quando o resultado esperado não representa o comportamento do cenário.

---

### GREEN

A expectativa foi corrigida para `3`, conforme o BDD. A implementação existente já considera aprovado o curso concluído com média maior ou igual a 7,0, então não foi necessária alteração no código de produção.

Teste final:

```java
@Test
public void deveLiberarTresCursosQuandoAlunoConcluirComMedia9() {
    var plataforma = new PlataformaGamificacao();
    var aluno = new Aluno("Rafael Amorim", Plano.Basico);
    var curso = new Curso("Curso 1");
    var conclusao = new ConclusaoCurso(aluno, curso, 9.0, true);

    plataforma.processarConclusao(conclusao);

    assertEquals(3, aluno.getCursosAdicionaisLiberados());
}
```

Resultado após a correção:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## BLUE - Refatoração

Não foi necessária refatoração para este cenário. O teste usa a mesma estrutura dos demais testes de domínio e reaproveita a regra já centralizada em `ConclusaoCurso.foiAprovado()` e `PlataformaGamificacao.processarConclusao()`.
