# Grupo 5 - Prática ATDD

## Educação Continuada Gamificada

Projeto desenvolvido para a disciplina de DevOps & QA utilizando os conceitos de Acceptance Test Driven Development (ATDD), combinando BDD e TDD.

O estudo de caso é uma plataforma de educação continuada gamificada. Alunos dos planos Básico ou Premium concluem cursos, recebem três cursos adicionais a cada conclusão aprovada (curso concluído e nota final de pelo menos 7,0) e podem consultar o histórico de tentativas e liberações acumuladas.

## Evolução arquitetural B1

**Escopo desta entrega: até o Desafio 2 da B1**, com Ranking e Mock Objects/Mockito. Os dois mini-labs estão implementados e validados no escopo descrito abaixo; Desafios 3–6 ficam fora desta entrega. A documentação segue a cadeia **RF → RNF → ASR → RPC → Alternativas/Trade-offs → ADR → C4 → Evidência**.

A [solução do Desafio 1 e o registro arquitetural B1](docs/arquitetura/README.md) descrevem a evolução do Core para monólito modular, seguindo o exemplo da professora e adaptando as fronteiras ao projeto. O primeiro recorte encapsula aprovação e recompensa em `gamificacao`, com contrato Java público, regras internas e testes ArchUnit das dependências. A persistência continua no serviço existente, na mesma transação. A validação em 28/10/2026 aprovou 25 testes da suíte normal, um teste PostgreSQL e dez cenários Cypress, com cobertura integral e smoke checks em HOMOL. Consulte a [comparação de dependências](docs/arquitetura/evidencias/desafio-1/dependencias.md) e a [validação do mini-lab](docs/arquitetura/evidencias/desafio-1/validacao.md) para comandos, resultados e limites da verificação.

O **Desafio 2** usa Ranking como mini-lab: `GET /api/ranking` e painel Vue, com **Ports & Adapters aplicado seletivamente à dependência de leitura do Ranking**. O caso de uso depende de uma porta, implementada pelo adapter JPA e substituída por Mockito/Fake nos testes. Classificação Java pura, ArchUnit, ADR-002 e C4 Component registram e protegem esse recorte; o sistema inteiro não foi convertido para Hexagonal.

No Ranking, **pontuação = quantidade de conclusões aprovadas**. O fluxo existente aprova quando `concluido = true` e `nota >= 7`; o Ranking lê esse resultado persistido. A ordem é decrescente por aprovações, com desempate por `alunoId` crescente. Reprovações e cursos incompletos não pontuam; aprovações repetidas contam, inclusive no mesmo curso; alunos sem aprovação não aparecem. Veja o [contrato da API](docs/API_CONTRACT.md#ranking--get-apiranking).

Mockito fornece o Mock Object controlado pelo teste (`@Mock`, `when`, `verify` e `verifyNoMoreInteractions`); o Fake é uma implementação simplificada em memória; o adapter JPA é a implementação real de infraestrutura. A [evidência de substituição](docs/arquitetura/evidencias/desafio-2/dependencias.md#substituição-nos-testes) demonstra o mesmo caso de uso com essas implementações.

| Cadeia acadêmica | Desafio 1 — arquitetura macro | Desafio 2 — organização interna |
| --- | --- | --- |
| RF/RNF/ASR/RPC | [RFs](docs/arquitetura/README.md#2-rfs-afetados), [RNFs e três ASRs](docs/arquitetura/README.md#3-rnfs-e-seleção-dos-asrs), [RPCs](docs/arquitetura/README.md#4-rpcs-e-consequências) | [RFs](docs/arquitetura/README.md#132-rfs-afetados), [RNFs e três ASRs](docs/arquitetura/README.md#133-rnfs-e-seleção-dos-asrs), [RPCs](docs/arquitetura/README.md#134-rpcs) |
| Alternativas e decisão | [Camadas × modular × serviços seletivos](docs/arquitetura/README.md#5-alternativas-e-trade-offs); monólito modular, uma aplicação/deploy | [Acesso direto × inversão seletiva × reestruturação ampla](docs/arquitetura/README.md#135-alternativas-e-trade-offs); Ports & Adapters seletivo |
| ADR | [ADR-001](docs/arquitetura/README.md#7-adr-001-evolução-do-core-para-monólito-modular) | [ADR-002](docs/arquitetura/README.md#137-adr-002--inverter-a-dependência-de-persistência-do-ranking-com-ports--adapters) |
| C4 | [Container](docs/arquitetura/README.md#8-c4-na-visão-de-container) | [Component do Core](docs/arquitetura/README.md#138-c4-component-do-core--recorte-ranking) |
| Microsolução e Git | [Gamificação, ArchUnit e dependências](docs/arquitetura/evidencias/desafio-1/dependencias.md), [rastreabilidade Git](docs/arquitetura/README.md#10-evidências-para-revisão-no-git) | [Porta, adapter, regra, Mockito/Fake e ArchUnit](docs/arquitetura/evidencias/desafio-2/dependencias.md), [validação e snapshot Git](docs/arquitetura/evidencias/desafio-2/validacao.md) |

A [validação final do Desafio 2](docs/arquitetura/evidencias/desafio-2/validacao.md), em 02/11/2026, confirmou 38 testes normais + 1 IT PostgreSQL, 100% das métricas/classes analisadas pelo JaCoCo atual (31 classes; 798 instruções, 13 branches e 169 linhas), lint/build, cinco smoke checks e Jenkins Build #2 com Cypress 12/12 em HOMOL/PostgreSQL. O Jenkins executou snapshot local temporário, não commit oficial da branch. O relatório distingue tentativas anteriores, repetição no Cypress e limites dos artefatos; os detalhes arquiteturais permanecem no registro cumulativo.

Resultado do pipeline: **Jenkins Build #2 — SUCCESS**, com **39 execuções backend** e Cypress **12 passing, 0 failing** (10 Gamificação + 2 Ranking). A observabilidade confirmou `up{job="grupo5-api"} = 1`, configuração Prometheus e duas regras de alerta válidas, além do Grafana saudável com datasource e dashboard provisionados. O snapshot de laboratório foi `2f1b0cd`; a branch real permaneceu em `317b143` durante o gate, com as alterações ainda sem commit oficial.

## Executar e explorar a API

Com Java 26 configurado em `JAVA_HOME`, execute `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`). A aplicação inicia na porta 8080 com banco H2 em memória.

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Especificação OpenAPI em JSON: <http://localhost:8080/v3/api-docs>

[Capturas da API e dos schemas no Swagger](docs/Swagger.md).

No Swagger, cadastre um curso e um aluno, depois envie uma conclusão com os IDs retornados. A consulta do aluno mostra o total de cursos adicionais e o histórico. Para executar os testes, use `./mvnw verify` (Windows: `.\mvnw.cmd verify`).

Para abrir o console H2 e usar um nome de banco fixo, após o build execute na raiz do projeto:

```powershell
java -jar target/Grupo_5_PraticaATDD-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2 --server.port=8082
```

Acesse <http://localhost:8082/h2-console/>, informe URL JDBC `jdbc:h2:mem:gamificacao`, usuário `sa` e deixe a senha vazia. O valor de `--server.port` deve conter apenas o número `8082`, sem `]` ao final. Os dados desse perfil são mantidos apenas enquanto a aplicação estiver em execução. [Evidência da execução com H2](docs/H2.md).

## Aplicação completa no Docker

Com o Docker Desktop aberto, na raiz do projeto:

```sh
docker compose up --build -d
docker compose ps
```

O frontend fica em <http://localhost:5173/> e encaminha `/api` para o backend pela rede Docker. O Swagger da API fica em <http://localhost:8080/swagger-ui.html>. O PostgreSQL fica disponível em `127.0.0.1:55434`, banco e usuário `gamificacao`, senha de desenvolvimento `gamificacao_dev`. O pgAdmin fica em <http://localhost:5050/> (login de desenvolvimento `admin@grupo5.dev` / `admin_dev`). No pgAdmin, registre um servidor com host `db`, porta `5432`, banco e usuário `gamificacao`, senha `gamificacao_dev`. O Compose ativa o perfil `postgres` na API e guarda os dados do banco e do pgAdmin em volumes Docker. O frontend já é compilado durante o build da imagem, sem precisar executar o Vite no Windows. Para consultar as tabelas diretamente:

```sh
docker compose exec db psql -U gamificacao -d gamificacao
```

Use `docker compose down` para parar os serviços sem apagar os volumes. As portas podem ser alteradas com `FRONTEND_PORT`, `API_PORT`, `POSTGRES_PORT` e `PGADMIN_PORT`; as credenciais iniciais podem ser alteradas com `POSTGRES_PASSWORD`, `PGADMIN_EMAIL` e `PGADMIN_PASSWORD`, por exemplo em um arquivo `.env` local. Defina as senhas antes de criar os volumes pela primeira vez: mudar as variáveis depois não altera as senhas já gravadas. O perfil usa `ddl-auto=update` para desenvolvimento; migrações versionadas ainda são uma evolução futura.

Para desenvolver o frontend fora do Docker, execute `npm ci` e `npm run dev` dentro de `frontend/`. O Vite encaminha as chamadas `/api` para o backend na porta 8080.

[Evidência da execução com PostgreSQL e capturas do pgAdmin](docs/Docker-PostgreSQL.md).

## Pipeline Jenkins, HOMOL e observabilidade

O [Jenkinsfile](Jenkinsfile) versiona o fluxo de checkout, verificação do Docker, testes JUnit com PostgreSQL real, cobertura JaCoCo, lint/build Vue, build das imagens, deploy em homologação, smoke checks e testes Cypress. Falhas interrompem a aprovação do pipeline. Relatórios JUnit, cobertura, vídeos, screenshots e logs são publicados no build.

O ambiente de homologação tem banco e volumes próprios, Prometheus e Grafana com datasource e dashboard provisionados. O ambiente de desenvolvimento continua em `compose.yaml`.

```sh
# Jenkins com Docker interno e plugins necessários
docker compose -f docker-compose.jenkins.yml up --build -d

# Alternativa: executar HOMOL diretamente, sem Jenkins
docker compose -f docker-compose.homol.yml up --build -d --wait db api frontend pgadmin prometheus grafana
docker compose -f docker-compose.homol.yml --profile checks run --build --rm smoke
docker compose -f docker-compose.homol.yml --profile e2e run --build --rm cypress
```

Jenkins: <http://localhost:8080/>. HOMOL: frontend <http://localhost:5174/>, API <http://localhost:8082/>, pgAdmin <http://localhost:5051/>, Prometheus <http://localhost:9091/> e Grafana <http://localhost:3001/>. Se você já usa um Jenkins instalado, configure nele o job conforme a documentação. Use o Jenkins em Docker ou a HOMOL manual por vez, pois compartilham as portas externas de HOMOL.

Consulte [DevOps e pipeline Jenkins](docs/DevOps-Jenkins.md) para desbloquear o Jenkins, criar o job a partir do Git, configurar credenciais e portas, executar CI localmente e coletar os relatórios. A implementação foi inspirada no [projeto Jogo Enigma V5.1](https://github.com/Lpaifer/jogo-enigma-api-v1-A2-portavel-v5.1), adaptada para Spring Boot/Java 26 e Vue deste repositório.

## Evidência histórica de testes

Os resultados históricos da esteira estão em [Validação DevOps — 12/10/2026](docs/DevOps-validacao.md). O resultado atual desta entrega está na [validação final do Desafio 2 — 02/11/2026](docs/arquitetura/evidencias/desafio-2/validacao.md). Os números abaixo preservam a evidência anterior.

Em 14/09/2026, `./mvnw clean verify` passou com **20 testes**. O JaCoCo registrou **100% das instruções (729/729)**, **100% dos branches (11/11)** e **100% das linhas (168/168)** nas 25 classes analisadas. A classe `Grupo5PraticaAtddApplication` é excluída do relatório porque contém apenas o método `main` de inicialização, sem regra de negócio; a inicialização da aplicação é verificada nos testes de contexto e na execução Docker. A fase `verify` falha se surgir qualquer instrução, branch ou linha sem cobertura nas classes analisadas. O relatório detalhado é gerado localmente em `target/site/jacoco/index.html`. As porcentagens nos documentos individuais retratam etapas anteriores do ciclo RED/GREEN/BLUE. [Capturas do JUnit e Maven](docs/Testes.md) e [evidência da cobertura atual](docs/Cobertura-atual.md).

## Integrantes

- Beatriz Canaveze Fontolan Soares - 235099
- Leonardo Godinho da Silva - 236764
- Lucas Paifer - 236576
- Matheus Nicolas Arizono Ywamoto - 235912
- Matheus Marcolino - 161109
- Rafael Amorim - 223380

## Documentação individual

- [Beatriz Canaveze Fontolan Soares](docs/Beatriz.md)
- [Leonardo Godinho da Silva](docs/Leonardo.md)
- [Lucas Paifer](docs/Lucas%20Paifer.md)
- [Matheus Nicolas Arizono Ywamoto](docs/MatheusYwamoto.md)
- [Matheus Marcolino](docs/MatheusMarcolino.md)
- [Rafael Amorim](docs/Rafael.md)

## Cenários BDD identificados

**US escolhida pelo grupo — Aluno Básico:** Como aluno assinante do plano básico, quero ter acesso liberado a mais 3 cursos ao concluir um curso com média igual ou superior a 7,0, para continuar avançando nos estudos e aproveitar melhor minha assinatura.

As [propostas de histórias de usuário e a rastreabilidade US → BDD → teste](docs/US-e-rastreabilidade.md) estão reunidas em uma página própria. As propostas foram derivadas dos BDD já existentes e precisam da validação de autoria pelos integrantes. A autoria da US escolhida ainda precisa ser informada.

| Integrante | Cenário documentado |
| --- | --- |
| Beatriz Canaveze Fontolan Soares | [Não liberar cursos antes da conclusão](docs/Beatriz.md) |
| Leonardo Godinho da Silva | [Acumular cursos após segunda aprovação](docs/Leonardo.md) |
| Lucas Paifer | [Liberar três cursos com média exatamente 7,0](docs/Lucas%20Paifer.md) |
| Matheus Nicolas Arizono Ywamoto | [Liberar cursos após refazer um curso reprovado](docs/MatheusYwamoto.md) |
| Matheus Marcolino | [Não liberar cursos com média abaixo de 7,0](docs/MatheusMarcolino.md) |
| Rafael Amorim | [Liberar três cursos com média 9,0](docs/Rafael.md) |
