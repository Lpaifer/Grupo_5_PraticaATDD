# Desafio 2 — validação final da organização interna do Core

> **Registro histórico de 02/11/2026.** A implementação foi posteriormente publicada em `688823f` e integrada à `master` em `c269fa8`. O fechamento atual, com nova execução Docker em 03/11 e verificação explícita do Ranking no IT PostgreSQL, está no [README principal](../../../../README.md#evolução-arquitetural-b1) e no [resumo de resultados](fechamento-2026-11-03.json). O Jenkins abaixo refere-se ao snapshot temporário histórico; não houve nova execução do job em 03/11. As pendências de commit descritas neste relato retratam a data original.

## 1. Escopo e identificação

Evidências de 02/11/2026, conferidas no código, relatórios XML e logs locais. Este documento registra resultados já executados; sua redação não representa uma nova execução do pipeline.

- Branch real: `Leonardo-B1-D2`.
- HEAD real durante o gate e a revisão: `317b143a96f1bcf3f1fd74f839f98a991235c711`.
- Na data desse gate, Ranking e Desafio 2 eram alterações locais, ainda sem commit oficial; a publicação posterior está identificada na nota acima.
- Mini-lab: **Ports & Adapters aplicado seletivamente à dependência de leitura do Ranking**.
- Escopo: RF01 Ranking e preservação de RF02 Conquistas/Gamificação. Não inclui os Desafios 3–6 nem conversão integral do sistema para Hexagonal.

O [registro cumulativo](../../README.md#13-desafio-2--organização-interna-do-core) contém decisão, ADR-002 e C4 Component. A [comparação de dependências](dependencias.md) preserva o estado anterior e a primeira validação local.

## 2. Aderência ao enunciado

Conferência contra `Exercicio_B1_Architecture_Lab_6_Desafios_Reavaliado.docx`, seção “Desafio 2 — Organização interna do Core”. O enunciado pede uma regra isolada e substituição de pelo menos um adapter, mantendo os testes do domínio; não exige reescrever todo o produto.

| Requisito | Evidência no registro cumulativo/código | Resultado |
| --- | --- | --- |
| RF/RNF | Seções 13.2–13.3: Ranking; Gamificação preservada; manutenibilidade, testabilidade, modificabilidade e facilidade de evolução | Atendido |
| Até três ASRs | ASR-04 manutenibilidade, ASR-05 testabilidade e ASR-06 modificabilidade; três específicos deste desafio | Atendido |
| RPCs | Seção 13.4: manter Spring Boot, reaproveitar regras e evitar reescrita integral; continuidade JPA identificada como restrição do projeto | Atendido |
| Alternativas e trade-offs | Seção 13.5: acesso direto, Ports & Adapters seletivo e reestruturação ampla; custos e limites explícitos | Atendido |
| ADR-002 | Seção 13.7: contexto, direcionadores, alternativas, decisão e consequências | Atendido |
| C4 Component | Seção 13.8: fronteira do Core, controller, caso de uso, regra, porta, adapter, repository e banco | Atendido |
| Microsolução | Classificação pura; mesma consulta executada com adapter JPA, mock e fake | Atendido |
| Evidência | Testes, cobertura, integração PostgreSQL, HOMOL e Jenkins discriminados abaixo | Atendido |
| Git | No gate histórico, registro e evidências estavam no workspace; publicação posterior em `688823f`, merge em `c269fa8` | Implementação publicada após o gate |

A matriz cumulativa contém os três ASRs do Desafio 1 e os três do Desafio 2; não são seis ASRs selecionados para uma única decisão.

## 3. Arquitetura validada

```text
RankingController → ConsultarRanking → Classificacao
                              ↓
                       LeituraRankingPort
                              ↑ implementa
                       JpaRankingAdapter → ConclusaoRepository → JPA/PostgreSQL
```

Em `src/main/java/org/example/grupo_5_praticaatdd/`:

- `api/RankingController.java` expõe `GET /api/ranking` e delega ao caso de uso.
- `ranking/api/ConsultarRanking.java` recebe `LeituraRankingPort` no construtor; não importa repository, Entity, JPA ou transação.
- `ranking/port/LeituraRankingPort.java` retorna `List<ResumoAprovacoes>`.
- `ranking/infrastructure/JpaRankingAdapter.java` implementa a porta, acessa o repository e concentra `@Transactional(readOnly = true)`.
- `ranking/internal/Classificacao.java` mantém classificação Java pura: aprovações decrescentes, alunoId crescente no empate, exclusão de zero e posições sequenciais.
- `ResumoAprovacoes` e `PosicaoRanking` são records; não expõem entidades JPA.

`ConsultarRanking` mantém `@Service`: o isolamento comprovado é da persistência, não de toda anotação Spring. Gamificação e as camadas legadas continuam no mesmo Core. A leitura utiliza `aprovado` persistido pelo fluxo existente, sem duplicar a decisão de aprovação ou recalcular recompensas.

## 4. Mockito, Fake, adapter real e ArchUnit

Os arquivos citados estão em `src/test/java/org/example/grupo_5_praticaatdd/`.

| Teste | Execuções aprovadas | O que demonstra |
| --- | ---: | --- |
| `ranking/ConsultarRankingTest.java` | 2 | `MockitoExtension`, `@Mock LeituraRankingPort`, `when(...).thenReturn(...)`, `verify(...)` e `verifyNoMoreInteractions(...)`; lista ordenada completa e caso vazio, sem Spring/banco |
| `ranking/ConsultarRankingFakeTest.java` | 1 | Mesmo caso de uso com `FakeLeituraRankingPort`, dados em memória e consultas determinísticas |
| `ranking/ClassificacaoTest.java` | 2 | Regra pura, vazio/zero, ordenação, desempate e imutabilidade |
| `api/RankingApiTest.java` | 5 | Controller → caso de uso → adapter JPA real → repository → H2; conclusões geradas pelo serviço real |
| `arquitetura/RankingArchitectureTest.java` | 3 | Dependências da regra/porta; caso de uso sem persistência/HTTP; núcleo sem dependência do adapter |
| `arquitetura/GamificacaoArchitectureTest.java` | 3 | Proteções arquiteturais existentes preservadas |

**Mockito/Mock Objects** substitui a porta e verifica interações. **MockMvc** exercita a camada HTTP nos testes integrados; não substitui a evidência Mockito. Mock/fake não comprovam JPQL ou persistência; adapter real e testes com banco complementam essa verificação.

## 5. Backend e PostgreSQL

O fluxo versionado de CI executa `./mvnw -B -ntp -Ppostgres-it clean verify` com Java 26 e PostgreSQL descartável, separado do banco HOMOL.

| Suíte | Tests run | Failures | Errors | Skipped |
| --- | ---: | ---: | ---: | ---: |
| Surefire — normal | 38 | 0 | 0 | 0 |
| Failsafe — `PostgresPersistenceIT` | 1 | 0 | 0 | 0 |
| Total backend por execução do fluxo | **39** | **0** | **0** | **0** |

Confirmado nos XML/TXT de `artifacts/gate-final/backend/surefire-reports/` e `failsafe-reports/`, em `ci-backend.log` e novamente no log Jenkins Build #2. Não se somam execuções repetidas do CI manual e do Jenkins como testes distintos. O CI manual terminou em `BUILD SUCCESS` às 18:55:57 UTC de 02/11/2026.

`PostgresPersistenceIT` verifica por metadados JDBC que o produto é `PostgreSQL`, processa reprovação e aprovação e confere histórico, saldo e linhas persistidas. Os cinco testes específicos de Ranking da suíte normal usam H2; a prova específica de Ranking com PostgreSQL vem da validação funcional em HOMOL e do Cypress integrado.

## 6. Ranking em HOMOL e persistência real

`api.log` confirma `jdbc:postgresql://db:5432/gamificacao_homol`, PostgreSQL JDBC Driver, PostgreSQLDialect e versão 17.11. Dados de teste foram criados pelos endpoints normais, sem inserção SQL para substituir o fluxo funcional.

| Verificação | Resultado observado |
| --- | --- |
| Aprovação conta | Aluno A: notas 7 e 8 concluídas produzem 2 aprovações |
| Reprovação não conta | Nota 6,9 concluída não aumenta o Ranking |
| Curso incompleto não conta | Nota 10 com `concluido=false` não aumenta o Ranking |
| Múltiplas aprovações | Cada tentativa aprovada conta, inclusive no mesmo curso |
| Desempate | A e B com 2 aprovações: ID 1 antes de ID 2 |
| Ordenação | Nova aprovação de B: ID 2 com 3 aprovações antes de ID 1 com 2 |
| Sem aprovação | Aluno C, com reprovação e tentativa incompleta, não aparece |
| JSON | `posicao`, `alunoId`, `nome`, `aprovacoes`, coerentes com o contrato |
| PostgreSQL | Consulta SQL de conferência: A = 4 tentativas/2 aprovações; B = 3/3; C = 2/0 |

Evidências: `ranking.json`, `ranking-ordenacao.json`, `ranking-validacao.json`, `ranking-postgres.txt` e `ranking-postgres-final.txt` em `artifacts/gate-final/`. A primeira conferência PowerShell tratou o array JSON incorretamente; a leitura do script foi corrigida e a verificação repetida com sucesso, sem alteração da aplicação. A consulta SQL foi somente de conferência.

## 7. JaCoCo

Contadores da raiz de `artifacts/gate-final/backend/site/jacoco/jacoco.xml`:

| Métrica | Coberto | Perdido | Cobertura |
| --- | ---: | ---: | ---: |
| Classes analisadas | 31 | 0 | 100% |
| Instructions | 798 | 0 | 100% |
| Branches | 13 | 0 | 100% |
| Lines | 169 | 0 | 100% |

**100% das métricas/classes analisadas pelo JaCoCo atual**, dentro do escopo configurado. Não significa “100% do código” do projeto, do frontend ou de todos os comportamentos possíveis. A exclusão preexistente de `Grupo5PraticaAtddApplication.class` permanece no POM. Nenhum limite foi reduzido e nenhuma exclusão adicional foi criada. CI manual e Jenkins registram `All coverage checks have been met.`; os contadores exatos acima foram extraídos do XML preservado do CI manual.

## 8. Frontend e Cypress

O painel Vue consulta a API, mostra carregamento/vazio/erro, permite atualização e recarrega após conclusão. O frontend não recalcula a classificação. O Build #2 registra lint Oxlint/ESLint e build Vite aprovados.

**Cypress em HOMOL/PostgreSQL aprovado no Jenkins Build #2:**

| Spec | Tests | Passing | Failing | Pending | Skipped |
| --- | ---: | ---: | ---: | ---: | ---: |
| `gamificacao.cy.js` | 10 | 10 | 0 | 0 | 0 |
| `ranking.cy.js` | 2 | 2 | 0 | 0 | 0 |
| Total | **12** | **12** | **0** | **0** | **0** |

O console contém os relatórios JUnit e `All specs passed!`. O fluxo integrado atravessa Vue/Nginx → API → regras/adapter → PostgreSQL. Há interceptações deliberadas para falhas de rede e estados de UI: o primeiro cenário de Ranking simula vazio/erro/recuperação; o segundo consulta a API real e atualiza após conclusão pela tela. Não se afirma que todas as requisições dos 12 cenários atingem o banco.

**Distinção entre tentativas:** `artifacts/gate-final/cypress.log` pertence à tentativa direta anterior, que falhou na verificação inicial do executável Cypress por timeout de 30 segundos, antes de executar os cenários. Não é a fonte do 12/12. A fonte do resultado aprovado é `jenkins-build-2.log`. Nesse build há uma screenshot marcada `failed` para o cenário de acúmulo em dois cursos, embora o resultado final seja 10/10 nessa spec. A configuração existente permite uma repetição (`retries.runMode = 1`); a evidência indica recuperação de tentativa e não comprova aprovação de todos os cenários na primeira tentativa. A causa dessa tentativa isolada não está detalhada no log preservado.

## 9. HOMOL, Prometheus e Grafana

Os dois arquivos Compose foram validados antes da subida. `homol-status-final.txt` registra PostgreSQL, API, frontend, Prometheus e Grafana saudáveis; pgAdmin ativo (sem health check próprio no Compose) e sua página de login respondeu HTTP 200 na conferência manual.

Os cinco checks versionados de `scripts/smoke-homol.mjs` passaram tanto na execução direta (`smoke.log`) quanto no Jenkins:

```text
OK: API e banco saudáveis
OK: Frontend e proxy Nginx
OK: Métricas JVM e HTTP disponíveis
OK: Prometheus coletando a API
OK: Grafana com datasource e dashboard provisionados
```

`prometheus-up.json` registra `up{job="grupo5-api"} = 1`, instância `api:8080`, ambiente `homol`. `promtool.txt` confirma configuração válida e duas regras de alerta. O smoke consulta métricas JVM/HTTP, health do Grafana, datasource UID `prometheus` apontando para `http://prometheus:9090` e dashboard UID `grupo5-homol`. É evidência de execução/provisionamento por HTTP, não apenas de existência de arquivos nem de revisão visual de todos os painéis.

Na preparação direta houve falha transitória de resolução Maven, recuperada pelas tentativas previstas no Dockerfile, e erro 500 do Docker ao consultar a inicialização do banco. A nova subida, preservando volumes, concluiu e permitiu as verificações acima; nenhum teste ou regra funcional foi desativado para obter aprovação.

## 10. Jenkins — execução atual e snapshot de laboratório

Fonte: `artifacts/gate-final/jenkins-build-2.log`. Job `Grupo5-B1-Validacao`, **Build #2**, em Linux/Docker-in-Docker. O console comprova checkout, preflight, JUnit/PostgreSQL/JaCoCo, lint/build Vue, build de imagens, deploy HOMOL, smoke/observabilidade, Cypress e publicação de relatórios/artefatos.

Checkout registrado:

```text
file:///var/jenkins_home/grupo5-snapshot
2f1b0cddaeee37e82ccb2015e4157b6e640e6206
test: snapshot local para validacao Jenkins
```

Esse é o **snapshot local temporário informado para validar o estado corrente**, não um commit oficial da branch de trabalho. O HEAD real conferido permaneceu `317b143a96f1bcf3f1fd74f839f98a991235c711`, com alterações não commitadas. O console confirma a revisão usada; não substitui uma comparação integral de conteúdo entre snapshot e workspace, que não está preservada nos artefatos consultados.

Trecho final confirmado:

```text
HOMOL aprovada: aplicação, PostgreSQL, Cypress, Prometheus e Grafana verificados.
Pipeline concluído. Relatórios de testes e cobertura disponíveis neste build.
Finished: SUCCESS
```

Esta evidência é do gate do Desafio 2 em 02/11/2026; é distinta da execução histórica de 32 testes descrita em `docs/DevOps-validacao.md`. O ramo Windows (`bat`) do Jenkinsfile não foi validado pelo build Linux.

**Limite documental:** o responsável informou que o Build #1 foi bloqueado pelo checkout `file://` e que o laboratório habilitou `-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true` para o Build #2. Não foram encontrados o log do Build #1 nem um export da configuração JVM entre os artefatos atuais; portanto, essa causa e a configuração são registradas como relato do responsável, não como fatos independentemente confirmados. O log #2 comprova checkout local bem-sucedido. Não há alteração do Jenkinsfile no diff da branch.

## 11. Rastreabilidade e limites dos artefatos

Todos os caminhos de artefatos desta seção são relativos à raiz do repositório e estão sob `artifacts/gate-final/`, diretório local ignorado pelo Git. Este documento preserva a síntese acadêmica; os logs/XML completos devem ser conservados ou anexados à entrega pelo responsável, pois não acompanham automaticamente um clone.

| Arquivo/pasta | Evidência |
| --- | --- |
| `backend/surefire-reports/`, `backend/failsafe-reports/` | 38 + 1 execuções e resultados por classe |
| `ci-backend.log` | Fluxo manual com PostgreSQL, BUILD SUCCESS e checks JaCoCo |
| `backend/site/jacoco/jacoco.xml` | Contadores exatos de cobertura |
| `api.log`, `ranking*.json`, `ranking-postgres*.txt` | API PostgreSQL e Ranking funcional |
| `smoke.log`, `promtool.txt`, `prometheus-up.json` | Cinco checks e observabilidade |
| `homol-status-final.txt` | Estado dos serviços na coleta |
| `cypress.log` | Tentativa direta interrompida antes dos cenários |
| `jenkins-build-2.log` | Pipeline atual aprovado e Cypress 12/12 |

SHA-256 dos arquivos conferidos:

```text
jenkins-build-2.log
75BD1580883A22B0273A01A8C4CC44968B0C6CE3423B72B418D2C50F0B2F0FC3
backend/site/jacoco/jacoco.xml
F59D492FCB1DCE4C266300D12199EE0E42CDECD724570E6A91D4F296AC9BFFEF
```

## 12. Resultado final

**DESAFIO 2 VALIDADO — PRONTO PARA COMMIT APÓS REVISÃO DO DIFF.** A hipótese arquitetural foi demonstrada: substituir a leitura por mock/fake preserva caso de uso, regra e contrato; o adapter real permanece operacional com JPA e o fluxo completo passou em PostgreSQL/HOMOL.

Não resta bloqueio técnico do gate solicitado nos resultados finais disponíveis. Permanecem registrados: tentativa Cypress recuperada, ausência dos artefatos específicos do Build #1/configuração local e necessidade de conservar os logs ignorados pelo Git. A instalação npm local anterior reportou 14 vulnerabilidades (9 moderadas, 4 altas e 1 crítica); não houve nova auditoria de dependências nesta revisão nem `npm audit fix`. A execução Docker usa `--no-audit`, portanto seu sucesso não comprova ausência de vulnerabilidades.

Não se comprovaram carga para 100.000 usuários, migração integral do Core, deploy produtivo ou execução Jenkins após um futuro commit oficial. Commit, push, merge e rebase não fazem parte desta revisão documental.
