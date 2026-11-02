# Desafio 2 — inversão da leitura do Ranking

## Identificação e escopo

Data: 02/11/2026. Branch: `Leonardo-B1-D2`. HEAD base: `317b143a96f1bcf3f1fd74f839f98a991235c711`.
O estado ANTES é o Ranking funcional no diff local sobre essa base, ainda sem commit:
32 testes backend aprovados e 12 cenários Cypress locais com H2. O commit base sozinho
não contém Ranking. Esta etapa preserva esse trabalho e acrescenta a refatoração;
o [registro cumulativo](../../README.md) agora documenta ADR-002 e C4 Component desse recorte.

Referências: `Exercicio_B1_Architecture_Lab_6_Desafios_Reavaliado.docx` e
`Exemplo_Solucao_Desafio_1_Architecture_Lab_B1.docx`. Os materiais complementares
`Ex_A3_EntendendoMocks_EstudoCaso_.docx`, `TESTES com Dublês.pptx` e `mock_spring-1.zip`
não estavam localizados nas buscas anteriores; não foram usados como evidência.

## Hipótese e requisitos

É possível trocar a leitura persistida do Ranking por um mock/fake sem modificar o
caso de uso, a classificação ou o contrato HTTP, preservando o fluxo JPA real?

RF01 Ranking é o recorte; RF02 Conquistas/Gamificação fornece o resultado aprovado
que continua sendo calculado pela regra existente. RNFs analisados: manutenibilidade,
testabilidade, modificabilidade e facilidade de evolução. Três ASRs selecionados no ADR-002: manutenibilidade (limitar acesso à persistência), testabilidade
(executar o caso de uso sem banco) e modificabilidade (substituir a fonte de dados).
Facilidade de evolução permanece analisada, sem virar um quarto ASR.
RPCs: manter Spring Boot, reaproveitar regras existentes e evitar reescrita integral.
A decisão e suas consequências estão registradas no ADR-002 do [registro cumulativo](../../README.md).

## Antes — dependências reais

`ranking.api.ConsultarRanking` importava:

- `java.util.List`;
- `ranking.internal.Classificacao`;
- `repository.ConclusaoRepository`;
- `org.springframework.stereotype.Service`;
- `org.springframework.transaction.annotation.Transactional`.

Tinha `@Service`, campo `ConclusaoRepository conclusoes`, injeção desse repository
no construtor e `@Transactional(readOnly = true)` em `consultar()`. O corpo era:

```java
return classificacao.classificar(conclusoes.contarAprovacoesPorAluno());
```

Fluxo: `RankingController.consultar()` → `ConsultarRanking.consultar()` →
`ConclusaoRepository.contarAprovacoesPorAluno()` → JPA.
A consulta agrupa por `c.aluno.id, c.aluno.nome`, conta `c` e filtra
`c.aprovado = true`, retornando `ResumoAprovacoes`. Nenhuma Entity cruza o contrato.
`Classificacao` já dependia somente de `List`, `Comparator`, `IntStream` e dos records
`ResumoAprovacoes`/`PosicaoRanking`; essa regra não precisava ser reescrita.

## Depois — porta e adapter

```text
RankingController.consultar()
  → ConsultarRanking.consultar()
      → LeituraRankingPort.buscarAprovacoes()
          ← implementada por JpaRankingAdapter
              → ConclusaoRepository.contarAprovacoesPorAluno()
                  → JPA / banco
```

`ranking.port.LeituraRankingPort` pertence ao núcleo e retorna apenas
`List<ResumoAprovacoes>`. `ConsultarRanking` recebe a porta pelo construtor e mantém
`@Service`, mas não importa repository, Entity, JPA ou transações.
`ranking.infrastructure.JpaRankingAdapter` implementa a porta e concentra a leitura
`@Transactional(readOnly = true)`. O repository e sua consulta não mudaram nesta etapa.
O adapter depende do contrato interno; o núcleo não conhece o adapter.

A regra continua: quantidade de conclusões aprovadas decrescente, alunoId crescente
para desempate, sem alunos com zero aprovações; cada aprovação registrada conta.
O endpoint `/api/ranking`, JSON, frontend e regras de gamificação foram preservados.

## Alternativas e custo do recorte

Manter o acesso direto seria mais curto, mas obrigaria o teste do caso de uso a conhecer
a persistência. A porta e o adapter acrescentam dois tipos e uma delegação, em troca
de uma dependência substituível. Não houve reescrita completa em Hexagonal/Clean,
novo serviço, tabela, biblioteca ou alteração no POM. A anotação `@Service` foi mantida
como compromisso explícito com o container Spring existente; não se afirma que todo
o núcleo está livre de Spring.

## Substituição nos testes

`ConsultarRankingTest` usa JUnit Jupiter com `MockitoExtension`, `@Mock LeituraRankingPort`,
`when(porta.buscarAprovacoes()).thenReturn(...)`, `verify(porta).buscarAprovacoes()` e
`verifyNoMoreInteractions(porta)`. Verifica a lista inteira (posição, ID, nome e total),
ordenação, desempate e retorno vazio. Não inicia Spring nem banco.
Não há `verifyNoInteractions` ou `InOrder`: a leitura é necessária e não existe uma
sequência de múltiplas interações relevante para esse contrato.

`FakeLeituraRankingPort`, somente em testes, mantém valores em memória.
`ConsultarRankingFakeTest` instancia o mesmo caso de uso com esse fake e verifica
resultados determinísticos em consultas repetidas.

O adapter real é exercitado pelos cinco testes de `RankingApiTest` com contexto Spring,
MockMvc e H2: controller → caso de uso → bean `JpaRankingAdapter` → repository → banco.
As conclusões são produzidas pelo `ConclusaoService` real. Os testes verificam ausência
de aprovações, reprovação, curso incompleto, repetições, ordenação e desempate.
Mock Objects não validam JPQL, mapeamento ou banco; os testes de integração permanecem
obrigatórios. A validação posterior em PostgreSQL/HOMOL está registrada no
[gate final](validacao.md); os cinco testes desta classe continuam usando H2.

## Proteção arquitetural

`RankingArchitectureTest` analisa somente classes produtivas:

1. Classificação e porta dependem apenas de Java e dos pacotes internos de valores/regra.
2. O caso de uso depende apenas de Java, contratos/regra/porta e `springframework.stereotype`;
   repository, Entity, JPA, transações e HTTP não pertencem à lista permitida.
3. API, regra e porta não dependem de `ranking.infrastructure`.

As três regras de `GamificacaoArchitectureTest` permanecem inalteradas.

## Validação local inicial — 02/11/2026

Esta seção preserva a etapa anterior ao gate externo. O resultado posterior com
PostgreSQL/HOMOL e Jenkins está na [validação final](validacao.md).

Java 26.0.2.1, Maven 3.9.16, cache temporário fora do Git:

```powershell
$env:JAVA_HOME = 'C:\Users\lnrdg\AppData\Local\Programs\Eclipse Adoptium\jdk-26.0.2.101-hotspot'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$MAVEN_TEMP = 'C:\Users\lnrdg\AppData\Local\Temp\grupo5-maven-repository'
.\mvnw.cmd "-Dmaven.repo.local=$MAVEN_TEMP" clean verify
```

Resultado concluído às 15:00:47 (UTC−03:00): **38 testes, 0 failures, 0 errors,
0 skipped; BUILD SUCCESS; All coverage checks have been met.**
Inclui 2 testes Mockito, 1 fake, 3 regras Ranking ArchUnit, 3 regras Gamificação
ArchUnit e os 5 testes integrados de Ranking já existentes.

JaCoCo: **31 classes analisadas, 798/798 instruções, 13/13 branches, 169/169 linhas**.
Nenhum limite foi reduzido e nenhuma nova exclusão foi adicionada. A interface não
adiciona classe com código executável ao contador. Relatórios em `target/surefire-reports/`
e `target/site/jacoco/`; saída Maven em `%TEMP%/d2-maven.log` da sessão de execução.

Frontend inalterado nesta etapa: `npm run lint:check` (Oxlint + ESLint) e
`npm run build` aprovados com Node 24.21.0 e npm 11.19.0.

Cypress 16.1.1 / Electron 146: **12 testes, 12 passing, 0 failing, 0 pending,
0 skipped**, dez de gamificação e dois de Ranking. Execução local com Vue/Vite na
porta 5173 e JAR refatorado na porta 8080, perfil H2 e banco em memória exclusivo
`jdbc:h2:mem:d2_validation`. `CYPRESS_BASE_URL=http://127.0.0.1:5173` e
`npm run test:e2e`; nenhum arquivo de configuração foi alterado. Relatórios e vídeos
ficam em `frontend/cypress/reports/` e `frontend/cypress/videos/` (ignorados pelo Git).
Os processos temporários foram encerrados após a validação.

Na etapa local acima, Docker/HOMOL/PostgreSQL/Cypress completo ainda estavam pendentes;
essa execução com H2 não comprova o ambiente completo. Posteriormente, o gate confirmou
38 testes normais + 1 IT PostgreSQL, cinco smoke checks e Jenkins Build #2 com Cypress
12/12 em HOMOL. A [validação final](validacao.md) distingue as tentativas, os artefatos
e o snapshot temporário do Jenkins. A branch real continua sem commit dessas alterações.
