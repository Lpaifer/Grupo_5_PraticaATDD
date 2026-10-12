# Frontend — Educação Continuada Gamificada

Interface Vue para cadastrar alunos e cursos, processar conclusões e consultar o histórico. A documentação geral e os critérios ATDD ficam no [README principal](../README.md).

## Desenvolvimento local

Com o backend disponível na porta 8080:

```sh
npm ci
npm run dev
```

Abra o endereço informado pelo Vite. As chamadas `/api` são encaminhadas para o backend pelo proxy de desenvolvimento.

## Aplicação completa no Docker

Na raiz do projeto, execute `docker compose up --build -d`. O frontend compilado fica em <http://localhost:5173/>; o Nginx encaminha `/api` para o backend dentro da rede Docker.

Para verificar o build isoladamente, execute `npm run build`.

## Cypress em homologação

Os testes em `cypress/e2e/gamificacao.cy.js` verificam cadastro pela interface, recarregamento dos dados, nota mínima, reprovação, curso incompleto, nova tentativa, acúmulo, nota inválida e falha de rede.

Na raiz do repositório, com HOMOL disponível:

```sh
docker compose -f docker-compose.homol.yml --profile e2e run --build --rm cypress
```

Para rodar no computador, instale as dependências com `npm ci` e execute `npm run test:e2e` (HOMOL na porta 5174) ou `npm run test:e2e:open`. `CYPRESS_BASE_URL` permite usar outro endereço. O Jenkins publica os relatórios JUnit, screenshots e vídeos automaticamente. Veja [o fluxo completo](../docs/DevOps-Jenkins.md).
