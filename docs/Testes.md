# Evidências de execução dos testes

## Mini-lab B1 — 28/10/2026

`mvnw clean verify` passou com **25 testes**, sem falhas, erros ou testes ignorados: oito cenários adaptados para o contrato público de gamificação, três novos testes de arquitetura e 14 testes existentes de API, persistência, observabilidade, contexto e serviço. O complemento em Docker aprovou também **um teste de integração PostgreSQL e dez cenários Cypress**, totalizando **36 testes** no CI/E2E, além de lint/build frontend, cobertura integral, deploy, smoke e configuração do Prometheus. A [validação do Desafio 1](arquitetura/evidencias/desafio-1/validacao.md) registra os comandos e resultados; o job Jenkins não foi disparado. As capturas abaixo preservam as execuções históricas anteriores à refatoração.

## JUnit — testes de domínio

A captura da execução de `PlataformaGamificacaoTest` pelo Test Runner for Java mostra **7 testes executados, sem falhas, erros ou testes ignorados**. Após o cenário do Rafael, a execução atual desse arquivo pelo Maven passou a ter **8 testes**, incluindo a média final de 9,0. Entre os cenários estão a média exatamente 7,0, a média 9,0, a reprovação por nota inferior a 7,0, o curso não concluído, a nova tentativa aprovada e o acúmulo após uma segunda aprovação.

![Sete testes de domínio aprovados no JUnit](evidencias/Test_JUnit.png)

## Maven — suíte completa

A captura do Maven mostra **19 testes executados, sem falhas, erros ou testes ignorados**, com `BUILD SUCCESS`. Após o cenário do Rafael, a execução atual de `./mvnw clean verify` passou a ter **20 testes**, sem falhas, erros ou testes ignorados. Esse total inclui os testes de domínio, API, serviço, persistência H2 e OpenAPI.

![Dezenove testes aprovados no Maven](evidencias/Test_Maven.png)

Para repetir a execução completa, rode `./mvnw clean verify` (Windows: `.\mvnw.cmd clean verify`) com a aplicação Java encerrada, pois o Windows bloqueia a limpeza do JAR enquanto ele está em uso. A [cobertura JaCoCo atual](Cobertura-atual.md) é documentada separadamente; o resultado `BUILD SUCCESS` da captura do Maven, isoladamente, não mostra os percentuais de cobertura.
