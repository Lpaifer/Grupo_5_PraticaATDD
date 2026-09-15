# Grupo 5 - Prática ATDD

## Educação Continuada Gamificada

Projeto desenvolvido para a disciplina de DevOps & QA utilizando os conceitos de Acceptance Test Driven Development (ATDD), combinando BDD e TDD.

O estudo de caso é uma plataforma de educação continuada gamificada. Alunos dos planos Básico ou Premium concluem cursos, recebem três cursos adicionais a cada conclusão aprovada (curso concluído e nota final de pelo menos 7,0) e podem consultar o histórico de tentativas e liberações acumuladas.

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

## Verificação atual

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
