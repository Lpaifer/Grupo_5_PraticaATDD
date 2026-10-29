# Desafio 1 — validação do primeiro recorte modular

Data: **28/10/2026**, fuso America/Sao_Paulo. Base Git: `e112d7db12c292030c6980f98814f42de7903c38`. Os resultados posteriores correspondem ao diff local desta implementação, ainda sem commit próprio. A árvore estava limpa antes do trabalho.

## Ambiente e base anterior

Windows, PowerShell, JDK Oracle **26.0.1**, Maven Wrapper do repositório, Spring Boot **4.1.1**, JaCoCo **0.8.15** e ArchUnit **1.4.2** em escopo de teste. O `JAVA_HOME` da máquina apontava para Java 21; o ajuste para Java 26 foi feito somente no processo dos comandos, sem alterar a configuração global.

Antes da refatoração, a execução `mvnw verify` realizada no estudo deste mesmo commit terminou com:

```text
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
BUILD SUCCESS
```

Essa base apresentava 729 instruções, 11 branches e 168 linhas cobertas nas 25 classes analisadas. O resultado anterior não é apresentado como execução da alteração.

## Contrato, fronteiras e regressão final

Primeiro foram executados os oito cenários do contrato e as três regras arquiteturais:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-26.0.1'
.\mvnw.cmd -B -ntp '-Dtest=PlataformaGamificacaoTest,GamificacaoArchitectureTest' clean test
```

Resultado: **11 testes aprovados**, sem falhas, erros ou testes ignorados. Em seguida, o [experimento de violação](dependencias.md) produziu as duas falhas arquiteturais esperadas. Depois da remoção das duas classes temporárias, foi executado:

```powershell
.\mvnw.cmd -B -ntp clean verify
```

Resultado final, concluído às **19:47:19**:

```text
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
Analyzed bundle 'Grupo_5_PraticaATDD' with 25 classes
All coverage checks have been met.
BUILD SUCCESS
```

| Suíte | Testes aprovados |
| --- | ---: |
| `AlunoConclusaoApiTest` | 5 |
| `CursoApiTest` | 2 |
| `H2PersistenceTest` | 1 |
| `ObservabilidadeApiTest` | 2 |
| `OpenApiTest` | 2 |
| `GamificacaoArchitectureTest` | 3 |
| `PlataformaGamificacaoTest` | 8 |
| `Grupo5PraticaAtddApplicationTests` | 1 |
| `ConclusaoServiceTest` | 1 |
| **Total** | **25** |

Os testes de API e persistência H2 mantidos sem alteração verificam o serviço integrado ao módulo, gravação de saldo e tentativa, reprovação seguida de aprovação, nova recompensa no mesmo curso, ordenação do histórico, HTTP 400/404, CORS e documentação OpenAPI. Os testes do contrato também verificam que reprovação e curso incompleto preservam saldo já acumulado.

## Cobertura depois da refatoração

Valores extraídos dos contadores globais de `target/site/jacoco/jacoco.xml` após `clean verify`:

| Métrica | Coberto | Perdido | Cobertura |
| --- | ---: | ---: | ---: |
| Instruções | 682 | 0 | 100% |
| Branches | 11 | 0 | 100% |
| Linhas | 144 | 0 | 100% |
| Classes analisadas | 25 | 0 | 100% |

O total de instruções e linhas mudou porque os objetos intermediários do domínio foram substituídos por records e pela política interna. A exclusão continua restrita a `Grupo5PraticaAtddApplication`; os limites de cobertura integral no POM não foram reduzidos. `target/` foi limpo após o experimento de violação, evitando resultados de classes removidas ou cobertura acumulada da base anterior.

## Execução do JAR e contratos HTTP

O JAR produzido pela regressão foi iniciado com H2 isolado e porta livre escolhida pelo servidor:

```powershell
java -jar target/Grupo_5_PraticaATDD-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2 --server.port=0 --spring.datasource.url=jdbc:h2:mem:mini_lab_d1
```

A porta efetiva aparece no log `Tomcat started on port ...`; nesta execução foi **49406**. A verificação HTTP utilizou essa porta para criar aluno e curso e registrar, no mesmo curso, as tentativas abaixo para cada plano (`Basico` e `Premium`):

| Nota | Concluído | Aprovado | Saldo retornado |
| ---: | --- | --- | ---: |
| 8,0 | Não | Não | 0 |
| 6,5 | Sim | Não | 0 |
| 7,0 | Sim | Sim | 3 |
| 9,0 | Sim | Sim | 6 |

A execução HTTP terminou aprovada às **19:49:17**. Para cada plano, os cadastros retornaram HTTP 201 e as quatro tentativas HTTP 200; a consulta do aluno confirmou saldo 6 e quatro registros na ordem enviada. Uma quinta tentativa com nota 11 retornou HTTP 400 sem alterar o saldo ou o histórico. As listagens de alunos/cursos responderam, o health check retornou `UP` e o OpenAPI manteve a operação de conclusões. O processo iniciado para esse experimento foi encerrado ao final.

```text
OK: JAR executado com H2, ambos os planos, oito tentativas, saldo 6 e quatro registros por aluno; nota inválida HTTP 400, consultas e OpenAPI preservados.
```

As respostas estão em `artifacts/desafio-1/execucao-http.json` e os logs em `artifacts/desafio-1/smoke-http.log` e `aplicacao.log`. O teste `H2PersistenceTest`, já aprovado na regressão, confirma também os valores gravados nas tabelas por JDBC.

## Complemento da validação em Docker — 28/10/2026

Na etapa local anterior, o Docker Engine estava indisponível (`dockerDesktopLinuxEngine` não encontrado). Nesta continuação, o Docker Desktop foi iniciado e o Engine **28.0.4**, com Compose **v2.34.0-desktop.1**, respondeu normalmente. Os contêineres Linux executam em `x86_64`, com quatro CPUs e aproximadamente 8 GB disponíveis ao Docker.

### CI com PostgreSQL real e frontend

O CI usa o projeto descartável `grupo5-d1-ci-20261028`, com banco em `tmpfs`, executando os Dockerfiles versionados:

```powershell
docker compose -p grupo5-d1-ci-20261028 -f docker-compose.ci.yml build backend
docker compose -p grupo5-d1-ci-20261028 -f docker-compose.ci.yml build frontend
docker compose -p grupo5-d1-ci-20261028 -f docker-compose.ci.yml run --name grupo5-d1-ci-20261028-backend backend
docker compose -p grupo5-d1-ci-20261028 -f docker-compose.ci.yml run --name grupo5-d1-ci-20261028-frontend --no-deps frontend
```

O backend executou `./mvnw -B -ntp -Ppostgres-it clean verify` com Java Temurin **26.0.2.1** e PostgreSQL **17.11**. Resultado aprovado às **21:03:05**, horário de São Paulo (o log do contêiner registra `2026-10-29T00:03:05Z`):

```text
Surefire: Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
Failsafe: Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
BUILD SUCCESS
```

`PostgresPersistenceIT` confirmou o produto JDBC PostgreSQL, registrou reprovação seguida de aprovação e consultou diretamente o saldo e as tentativas nas tabelas. O JaCoCo do contêiner confirmou novamente **682/682 instruções, 11/11 branches e 144/144 linhas**, nas 25 classes analisadas.

O contêiner de frontend, com Node **22.23.4**, executou `npm run lint:check` e `npm run build`, com **zero erros e zero avisos no Oxlint**, ESLint aprovado e build Vite concluído. Os relatórios completos de backend foram copiados antes da limpeza dos contêineres para `artifacts/desafio-1/docker/backend/`.

### Downloads interrompidos e ajuste nos builds

As duas primeiras tentativas pararam antes dos testes: uma em `npm ci`, por `ECONNRESET`, e outra em `dependency:go-offline`, por download incompleto de `hibernate-core:7.4.5.Final`. Os logs dessas tentativas foram preservados. Os Dockerfiles de backend, frontend e Cypress receberam até três tentativas dos comandos de preparação/empacotamento, com intervalo de três segundos e falha final caso nenhuma tentativa tenha sucesso. O cache da tentativa anterior é reaproveitado dentro da mesma etapa de build.

As versões, os lockfiles, as validações TLS e os comandos que executam testes foram mantidos. Esse ajuste operacional é uma alteração desta validação; o primeiro recorte de gamificação não exigiu alterações de tabelas ou contratos HTTP.

### HOMOL e navegador

HOMOL usa o projeto isolado `grupo5-d1-homol-20261028`, volumes próprios e as imagens locais `grupo5-d1-api:20261028-e112d7d-local` e `grupo5-d1-frontend:20261028-e112d7d-local`. As portas alternativas escolhidas para a execução são:

| Serviço | Porta no host |
| --- | ---: |
| API | 18082 |
| Frontend | 15174 |
| pgAdmin | 15051 |
| Prometheus | 19091 |
| Grafana | 13001 |

O deploy dos seis serviços foi aprovado com `up -d --no-build --wait --wait-timeout 180 db api frontend pgadmin prometheus grafana`. API, banco, frontend, Prometheus e Grafana ficaram saudáveis nos health checks; pgAdmin ficou em execução. A consulta SQL anterior ao E2E confirmou **zero alunos, zero cursos e zero conclusões** no banco isolado.

Os cinco smoke checks terminaram aprovados às **21:22:47**:

```text
OK: API e banco saudáveis
OK: Frontend e proxy Nginx
OK: Métricas JVM e HTTP disponíveis
OK: Prometheus coletando a API
OK: Grafana com datasource e dashboard provisionados
```

O smoke verificou `up{job="grupo5-api"}=1` no Prometheus e consultou o datasource e o dashboard provisionados pela API do Grafana. `promtool check config /etc/prometheus/prometheus.yml` também aprovou a configuração e as **duas regras de alerta**.

O Cypress **16.1.1**, com Node **24.21.0** e navegador **Electron 146 headless**, concluiu a execução às **21:24:19**:

```text
Tests: 10
Passing: 10
Failing: 0
Pending: 0
Skipped: 0
All specs passed!
```

Os cenários utilizaram Vue compilado, Nginx, API com o módulo de gamificação e PostgreSQL reais. A simulação de falha de rede ficou restrita ao cenário dedicado já existente. Foram verificados cadastro e recarregamento, limite de aprovação, curso incompleto, reprovação e nova tentativa, acúmulo em cursos diferentes, nova aprovação no mesmo curso, nota inválida e mensagens de erro.

Após os testes, a consulta SQL confirmou **9 alunos, 10 cursos e 9 conclusões** no banco de HOMOL, comparados ao estado inicial vazio. O relatório JUnit e o vídeo foram copiados para `artifacts/desafio-1/docker/cypress/` antes da remoção dos contêineres.

Com as variáveis da tabela de portas acima (`HOMOL_API_PORT`, `HOMOL_FRONTEND_PORT`, `HOMOL_PGADMIN_PORT`, `HOMOL_PROMETHEUS_PORT` e `HOMOL_GRAFANA_PORT`) e os nomes/tag de imagem informados, os comandos de HOMOL foram:

```powershell
$env:API_IMAGE = 'grupo5-d1-api'
$env:FRONTEND_IMAGE = 'grupo5-d1-frontend'
$env:IMAGE_TAG = '20261028-e112d7d-local'
docker compose -p grupo5-d1-homol-20261028 -f docker-compose.homol.yml --profile checks --profile e2e build api frontend smoke cypress
docker compose -p grupo5-d1-homol-20261028 -f docker-compose.homol.yml up -d --no-build --wait --wait-timeout 180 db api frontend pgadmin prometheus grafana
docker compose -p grupo5-d1-homol-20261028 -f docker-compose.homol.yml --profile checks run --name grupo5-d1-homol-20261028-smoke --no-deps smoke
docker compose -p grupo5-d1-homol-20261028 -f docker-compose.homol.yml exec -T prometheus promtool check config /etc/prometheus/prometheus.yml
docker compose -p grupo5-d1-homol-20261028 -f docker-compose.homol.yml --profile e2e run --name grupo5-d1-homol-20261028-cypress --no-deps cypress
```

### Encerramento da validação

| Verificação | Resultado |
| --- | --- |
| Suíte normal | 25 testes aprovados |
| Persistência PostgreSQL real | 1 teste aprovado |
| Cobertura | 100% de instruções, branches e linhas nas classes analisadas |
| Qualidade e build frontend | Aprovados |
| Deploy de seis serviços de HOMOL | Aprovado |
| Smoke checks | 5 aprovados |
| Configuração Prometheus | Aprovada, incluindo 2 regras de alerta |
| Cypress | 10 cenários aprovados |
| **Total de testes automatizados JUnit/Failsafe/Cypress** | **36 aprovados, sem falhas ou testes ignorados** |

A comparação de fontes usa `artifacts/desafio-1/docker/fontes-sha256.csv`; `etapas.json` registra comandos, horários e códigos de saída. As imagens usadas são locais, identificadas pelo sufixo `local`; a tag não representa um commit da alteração nem uma publicação em registry.

Depois da coleta de relatórios e logs, os projetos exclusivos `grupo5-d1-ci-20261028` e `grupo5-d1-homol-20261028` foram encerrados com remoção de seus contêineres, redes e volumes temporários. Os ambientes e dados de outros projetos não foram utilizados. As portas de HOMOL desta validação não permanecem servindo a aplicação após a limpeza.

## Limitações da verificação

Esta continuação executa os comandos Compose do [fluxo de CI/HOMOL existente](../../../DevOps-Jenkins.md) diretamente. O pipeline Jenkins não foi disparado. As evidências de outubro anteriores à refatoração permanecem históricas em [DevOps-validacao.md](../../../DevOps-validacao.md). Nenhum ensaio de carga foi realizado e não há evidência de capacidade para 100.000 usuários.

## Artefatos

Os relatos desta pasta são destinados ao Git; os arquivos completos abaixo são gerados e permanecem ignorados:

- `artifacts/desafio-1/baseline-maven.log` e `baseline-jacoco.xml`: execução anterior.
- `artifacts/desafio-1/contrato-fronteiras.log`: primeira execução do contrato e arquitetura.
- `artifacts/desafio-1/violacao-controlada.log`: rejeição das referências proibidas.
- `artifacts/desafio-1/maven-verify.log`, `jacoco.xml` e `surefire-reports/`: regressão limpa final.
- `artifacts/desafio-1/aplicacao.log`, `aplicacao-stderr.log`, `smoke-http.log` e `execucao-http.json`: execução real da aplicação e requisições HTTP.
- `artifacts/desafio-1/docker/etapas.json`, `fontes-sha256.csv` e `ambiente.txt`: identificação da execução em Docker.
- `artifacts/desafio-1/docker/resumo-testes.json` e `limpeza-confirmada.json`: contagem extraída dos relatórios XML e confirmação de remoção dos recursos temporários.
- `artifacts/desafio-1/docker/backend/`: JAR, relatórios Surefire/Failsafe e cobertura do CI PostgreSQL.
- `artifacts/desafio-1/docker/cypress/reports/` e `cypress/videos/`: relatório JUnit e vídeo dos dez cenários E2E.
- `artifacts/desafio-1/docker/smoke-homol.log`, `prometheus-config.log`, `homol-status.log`, `postgres-antes-e2e.log` e `postgres-registros.log`: integração, observabilidade e persistência.
- `artifacts/desafio-1/docker/tentativa-1-build-ci.log` e `tentativa-2-build-ci-backend.log`: falhas de download anteriores ao ajuste operacional.
- `artifacts/desafio-1/docker/limpeza-ci.log` e `limpeza-homol.log`: encerramento dos projetos temporários.
- `target/site/jacoco/index.html`: relatório navegável da cobertura final.

Os testes arquiteturais passam a rodar também no Jenkins existente por fazerem parte da fase Maven `test`. Isso não representa uma execução do pipeline nesta revisão.
