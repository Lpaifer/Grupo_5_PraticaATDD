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
