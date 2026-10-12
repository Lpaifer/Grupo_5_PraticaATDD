# Validação do fluxo DevOps

Execução local em **12/10/2026**, com Docker Desktop/Engine 27.1.1. Os testes utilizaram contêineres Linux, Java 26, PostgreSQL 17 e Cypress 16.1.1.

## Backend e PostgreSQL real

```sh
docker compose -p grupo5-validacao-ci -f docker-compose.ci.yml build backend frontend
docker compose -p grupo5-validacao-ci -f docker-compose.ci.yml run --name grupo5-validacao-backend backend
```

Resultado de `./mvnw -B -ntp -Ppostgres-it clean verify`:

```text
Surefire: Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
Failsafe: Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
BUILD SUCCESS
```

`PostgresPersistenceIT` confirmou o produto JDBC `PostgreSQL`, gravou uma reprovação seguida de aprovação pela API e consultou diretamente o saldo e as tentativas nas tabelas. O banco de CI é separado de HOMOL e usa armazenamento descartável.

JaCoCo, nas 25 classes analisadas, com a exclusão já existente da classe de inicialização:

| Métrica | Coberto | Perdido |
|---|---:|---:|
| Instruções | 729 | 0 |
| Branches | 11 | 0 |
| Linhas | 168 | 0 |

## Frontend

O contêiner de CI executou `npm run lint:check` e `npm run build`: Oxlint e ESLint sem erros e build Vite concluído. O lint de CI não altera os arquivos.

## Homologação e observabilidade

Os seis serviços de HOMOL iniciaram pelo Compose; os serviços que possuem health check ficaram saudáveis. O smoke check retornou:

```text
OK: API e banco saudáveis
OK: Frontend e proxy Nginx
OK: Métricas JVM e HTTP disponíveis
OK: Prometheus coletando a API
OK: Grafana com datasource e dashboard provisionados
```

O `promtool check config /etc/prometheus/prometheus.yml` validou o arquivo de coleta e as duas regras de alerta. O smoke check também consultou `up{job="grupo5-api"}` e verificou o valor `1`, além de consultar pelo HTTP a existência do datasource e do dashboard no Grafana.

## Cypress

```sh
docker compose -f docker-compose.homol.yml --profile e2e run --no-deps --name grupo5-validacao-cypress cypress
```

Resultado:

```text
Tests: 10
Passing: 10
Failing: 0
Pending: 0
Skipped: 0
All specs passed!
```

O Cypress gerou relatório JUnit e vídeo do fluxo completo. Os cenários utilizam a interface compilada em Nginx, API e PostgreSQL, com simulação de erro de rede apenas no cenário dedicado.

## Jenkins

O `Jenkinsfile` foi submetido ao endpoint `/pipeline-model-converter/validate` de uma instância temporária Jenkins com os plugins deste repositório:

```text
Jenkinsfile successfully validated.
```

O Compose de Jenkins também foi iniciado em um projeto de validação com portas alternativas; a comunicação TLS entre Jenkins e Docker-in-Docker foi confirmada com `docker version` no controlador.

Além da validação de sintaxe, o Jenkins executou o pipeline completo a partir de um snapshot Git local dos arquivos, sem publicar mudanças no GitHub. O build 2 do job temporário terminou com **`SUCCESS`**, incluindo checkout, preflight, CI, publicação do JaCoCo/JUnit, build das imagens, deploy, smoke checks, Cypress e arquivamento das evidências. O relatório agregado registrou **32 testes aprovados**, sem falhas ou testes ignorados. A execução utilizou o ramo Unix do Jenkinsfile no controlador Linux em Docker; o ramo `bat` para um agente Jenkins Windows não foi executado nesta verificação.

O Jenkins temporário usou portas alternativas e foi removido após a coleta dos resultados. A instalação definitiva deve seguir o [passo a passo do job SCM](DevOps-Jenkins.md).

## Artefatos locais

Os relatórios foram copiados para `artifacts/backend/`, `artifacts/cypress/` e `artifacts/smoke-homol.log`. Essa pasta é ignorada pelo Git por conter arquivos gerados, incluindo JAR, relatório HTML e vídeo. No job Jenkins, as mesmas evidências são publicadas como resultados/artefatos do build. Para reproduzir a execução, consulte [DevOps e Jenkins](DevOps-Jenkins.md).

Os metadados do build e seu console completo ficam em `artifacts/jenkins-build.json` e `artifacts/jenkins-console.log`.
