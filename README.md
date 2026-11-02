# Grupo 5 - Prática ATDD

## Educação Continuada Gamificada

Projeto desenvolvido para a disciplina de DevOps & QA utilizando os conceitos de Acceptance Test Driven Development (ATDD), combinando BDD e TDD.

O estudo de caso é uma plataforma de educação continuada gamificada. Alunos dos planos Básico ou Premium concluem cursos, recebem três cursos adicionais a cada conclusão aprovada (curso concluído e nota final de pelo menos 7,0) e podem consultar o histórico de tentativas e liberações acumuladas.

## B1 — Architecture Lab

A B1 dá continuidade ao MVP de Educação Continuada Gamificada da AC1 e reúne seis desafios arquiteturais. O trabalho atual corresponde ao **Desafio 1 — Drivers e Evolução do Core**. O objetivo é justificar a evolução com requisitos e evidências, e não escolher uma tecnologia apenas por ser moderna.

```text
AC1 → B1
   ↓
RF / RNF
   ↓
ASR + RPC
   ↓
Alternativas e Trade-offs
   ↓
ADR
   ↓
C4
   ↓
Implementação / Evidência
```

Este README apresenta o resumo executivo. O raciocínio completo, o ADR e os modelos estão na [documentação arquitetural B1](docs/arquitetura/README.md).

### Evolução a partir da AC1

A AC1 já possuía Java 26, Spring Boot, Vue.js, API REST, PostgreSQL, H2, JUnit, JaCoCo, Docker e Swagger/OpenAPI. A B1 reaproveita esse produto funcional e evolui sua arquitetura de forma incremental.

### Desafio 1 — Drivers e Evolução do Core

O cenário considera crescimento de aproximadamente 1.000 para até 100.000 usuários e evolução para capacidades como Ranking, conquistas, recompensas, IA, notificações, eventos, IoT e observabilidade. Esses números são uma premissa do exercício, não uma capacidade comprovada por teste de carga. As capacidades futuras não estão todas implementadas; a infraestrutura de observabilidade já integra HOMOL.

O desafio é decidir a arquitetura macro antes de decompor o sistema, comparando Monólito em Camadas, Monólito Modular e decomposição seletiva em serviços.

### RFs analisados

| ID | Requisito |
| --- | --- |
| RF01 | Ranking |
| RF02 | Conquistas e gamificação |
| AC1-01 | Cadastro e consulta de alunos |
| AC1-02 | Cadastro e listagem de cursos |
| AC1-03 | Registro de conclusões e aprovação |
| AC1-04 | Acúmulo de recompensas e histórico |

### RNFs analisados e ASRs selecionados

Os requisitos não funcionais (RNFs) analisados foram **Manutenibilidade, Escalabilidade, Desempenho, Testabilidade e Complexidade operacional**. Três foram priorizados como requisitos arquiteturalmente significativos (ASRs) para esta decisão:

| ASR | Por que orienta a arquitetura |
| --- | --- |
| Manutenibilidade | Capacidades evoluem em ritmos diferentes; fronteiras explícitas diminuem acoplamento e impacto de mudanças. |
| Escalabilidade | Principalmente Ranking pode apresentar carga diferente e futuramente exigir estratégia própria de escala. |
| Complexidade operacional | A equipe de seis integrantes precisa evitar o custo prematuro de deploys distribuídos, rede, observabilidade e tratamento de falhas. |

Nem todo RNF analisado se tornou automaticamente um ASR. Desempenho e testabilidade continuam relevantes à validação, mas não foram selecionados entre os três direcionadores desta decisão macro.

### RPCs

As restrições e premissas de contexto são: Core atual em Java/Spring Boot, equipe de seis desenvolvedores, banco relacional e legado funcional da AC1. A evolução deve ser incremental, evitando uma reescrita completa apenas para aplicar um padrão arquitetural.

### Alternativas e Trade-offs

| Alternativa | Benefícios | Custos / riscos |
| --- | --- | --- |
| Monólito em Camadas | Simplicidade e continuidade | Fronteiras de negócio pouco explícitas |
| Monólito Modular | Fronteiras claras, um deploy e evolução incremental | Processo e banco ainda compartilhados |
| Decomposição seletiva em serviços | Escala e deploy independentes | Rede, observabilidade e maior complexidade operacional |

### Decisão arquitetural

**Decisão: evoluir o Core para Monólito Modular.**

O Core continua sendo uma única aplicação Spring Boot, com um único deploy. As capacidades passam a ter contratos internos mais claros e chamadas locais, sem introduzir comunicação distribuída. Isso prepara uma futura extração somente se métricas reais a justificarem.

### ADR-001 — Evolução do Core

**Contexto:** o MVP funcional da AC1 precisa evoluir para novas capacidades e para o crescimento previsto no case, respeitando a equipe de seis pessoas e a preservação do legado.

**Decisão:** Monólito em Camadas → Monólito Modular, orientada pelos ASRs de Manutenibilidade, Escalabilidade e Complexidade operacional.

**Consequências positivas:** fronteiras mais explícitas, menor acoplamento, evolução incremental e possibilidade de futura extração. **Trade-offs:** deploy conjunto, processo e banco compartilhados; os módulos ainda não têm escala independente.

**Gatilho de revisão:** avaliar a extração de uma capacidade quando métricas de carga, latência, throughput, necessidade de escala ou frequência de deploy justificarem. Consulte o [registro arquitetural detalhado](docs/arquitetura/README.md) para o ADR completo.

### C4

| Nível | Visão da solução |
| --- | --- |
| Context | O usuário utiliza a plataforma de Educação Continuada Gamificada. |
| Container | Frontend Vue, Core Java/Spring Boot, PostgreSQL e infraestrutura de observabilidade. O Core permanece um único container de aplicação. |
| Component | O Core começa a organizar capacidades por módulos; o primeiro recorte é `gamificacao`. |
| Code | Separação entre o contrato público `gamificacao.api` e os detalhes de implementação em `gamificacao.internal`. |

A [documentação arquitetural](docs/arquitetura/README.md) apresenta a visão de Container e o detalhamento do primeiro recorte. A visão de Component será aprofundada no Desafio 2; a tabela acima resume os níveis sem representar modelos completos de todos eles.

### Implementação do primeiro recorte modular

O primeiro recorte implementado é **Gamificação / aprovação e recompensa**:

```text
gamificacao
├── api
│   ├── AvaliarConclusao
│   ├── DadosConclusao
│   └── ResultadoConclusao
└── internal
    └── dominio
        └── PoliticaConclusao
```

`gamificacao.api` é o contrato público; `gamificacao.internal` contém os detalhes que o código externo não deve acessar diretamente. A fronteira é verificada por testes ArchUnit; o nome do pacote não impede, por si só, acessos pelo compilador. `ConclusaoService` consome o contrato público, enquanto a persistência continua no serviço existente e a transação permanece local. Esse primeiro recorte não significa que todo o Core já foi modularizado.

### Evidência do Desafio 1

Hipótese do mini-lab:

> É possível criar uma fronteira modular para a regra de gamificação sem alterar o comportamento funcional da AC1 e sem introduzir comunicação distribuída?

Os testes ArchUnit verificam que código externo não acessa `gamificacao.internal`, que o módulo não depende de Spring, HTTP ou JPA e que os objetos de dados do contrato público não dependem da implementação interna.

Consulte a [comparação de dependências](docs/arquitetura/evidencias/desafio-1/dependencias.md) e a [validação do mini-lab](docs/arquitetura/evidencias/desafio-1/validacao.md) para comandos, resultados e limites da verificação.

### Validação

A validação documentada em **28/10/2026** registra **25 testes da suíte principal**, incluindo ArchUnit, **um teste de integração PostgreSQL** e **10 cenários Cypress**. Também registra cobertura JaCoCo integral das classes analisadas, lint e build do Vue e smoke tests em HOMOL.

Esses são resultados históricos documentados, não uma nova execução feita nesta atualização do README. A validação utilizou HOMOL diretamente via Compose; não comprova uma execução do job Jenkins nem capacidade para 100.000 usuários.

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

```text
Checkout
   ↓
Preflight
   ↓
JUnit + PostgreSQL + JaCoCo
   ↓
Lint + Build Vue
   ↓
Build Docker
   ↓
Deploy HOMOL
   ↓
Smoke Tests
   ↓
Cypress E2E
```

As etapas de homologação dependem da opção `DEPLOY_HOMOL` do pipeline.

O ambiente de homologação tem banco e volumes próprios, Prometheus e Grafana com datasource e dashboard provisionados. O ambiente de desenvolvimento continua em `compose.yaml`.

O Prometheus coleta as métricas configuradas e o Grafana as apresenta com datasource e dashboard provisionados. As configurações ficam em [prometheus/](prometheus/) e [grafana/](grafana/). Essas evidências de operação apoiarão as decisões arquiteturais dos próximos desafios; a presença das ferramentas, por si só, não demonstra capacidade de escala.

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

Os resultados do fluxo novo estão em [Validação DevOps — 12/10/2026](docs/DevOps-validacao.md), incluindo JUnit, PostgreSQL real, Cypress e observabilidade. Os números abaixo preservam a evidência anterior.

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

Cada integrante definiu uma User Story no Product Backlog. Para a prática ATDD, o grupo selecionou uma das seis histórias: a User Story **Aluno Básico**, de **Rafael Amorim**, como base comum. A partir dela, cada integrante elaborou um cenário BDD individual para explorar diferentes condições da mesma regra de negócio e executou seu ciclo ATDD correspondente. Consulte a [documentação de User Stories e rastreabilidade US → BDD → teste](docs/US-e-rastreabilidade.md).

O documento vinculado preserva a rastreabilidade dos cenários, mas sua descrição de origem e autoria das User Stories ainda precisa ser alinhada à informação corrigida acima.

| Integrante | Cenário documentado |
| --- | --- |
| Beatriz Canaveze Fontolan Soares | [Não liberar cursos antes da conclusão](docs/Beatriz.md) |
| Leonardo Godinho da Silva | [Acumular cursos após segunda aprovação](docs/Leonardo.md) |
| Lucas Paifer | [Liberar três cursos com média exatamente 7,0](docs/Lucas%20Paifer.md) |
| Matheus Nicolas Arizono Ywamoto | [Liberar cursos após refazer um curso reprovado](docs/MatheusYwamoto.md) |
| Matheus Marcolino | [Não liberar cursos com média abaixo de 7,0](docs/MatheusMarcolino.md) |
| Rafael Amorim | [Liberar três cursos com média 9,0](docs/Rafael.md) |
