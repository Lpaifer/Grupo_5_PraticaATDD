# Contrato da API - Frontend Vue

## Endereços

O frontend usa caminhos relativos `/api/...`: o Vite os encaminha ao backend no desenvolvimento e o Nginx faz o mesmo no Docker. Para chamar o backend diretamente, use:

```text
http://localhost:8080
```

---

## 1. Criar aluno

### Endpoint

```text
POST /api/alunos
```

### Request

```json
{
  "nome": "Leonardo",
  "plano": "Básico"
}
```

### Response esperada

```json
{
  "id": 1,
  "nome": "Leonardo",
  "plano": "Básico",
  "cursosAdicionais": 0,
  "historico": []
}
```

---

## 2. Criar curso

### Endpoint

```text
POST /api/cursos
```

### Request

```json
{
  "nome": "Geologia"
}
```

### Response esperada

```json
{
  "id": 10,
  "nome": "Geologia"
}
```

---

## 3. Processar conclusão

### Endpoint

```text
POST /api/conclusoes
```

### Request

```json
{
  "alunoId": 1,
  "cursoId": 10,
  "nota": 8.0,
  "concluido": true
}
```

### Response esperada

```json
{
  "alunoId": 1,
  "aluno": "Leonardo",
  "curso": "Geologia",
  "nota": 8.0,
  "concluido": true,
  "aprovado": true,
  "cursosAdicionais": 3
}
```

---

## 4. Buscar aluno por ID

### Endpoint

```text
GET /api/alunos/{id}
```

### Exemplo

```text
GET /api/alunos/1
```

### Response esperada

```json
{
  "id": 1,
  "nome": "Leonardo",
  "plano": "Básico",
  "cursosAdicionais": 6,
  "historico": [
    {
      "cursoId": 10,
      "curso": "Geologia",
      "nota": 8.0,
      "concluido": true,
      "aprovado": true
    },
    {
      "cursoId": 11,
      "curso": "Física",
      "nota": 7.5,
      "concluido": true,
      "aprovado": true
    }
  ]
}
```

---

## 5. Listar alunos

### Endpoint

```text
GET /api/alunos
```

### Response esperada

```json
[
  {
    "id": 1,
    "nome": "Leonardo",
    "plano": "Básico",
    "cursosAdicionais": 6,
    "historico": [
      {
        "cursoId": 10,
        "curso": "Geologia",
        "nota": 8.0,
        "concluido": true,
        "aprovado": true
      },
      {
        "cursoId": 11,
        "curso": "Física",
        "nota": 7.5,
        "concluido": true,
        "aprovado": true
      }
    ]
  }
]
```

Esse endpoint é utilizado pelo frontend ao abrir a página para carregar os alunos já cadastrados no backend.

---

## 6. Listar cursos

### Endpoint

```text
GET /api/cursos
```

### Response esperada

```json
[
  {
    "id": 10,
    "nome": "Geologia"
  },
  {
    "id": 11,
    "nome": "Física"
  }
]
```

Esse endpoint é utilizado pelo frontend ao abrir a página para carregar os cursos já cadastrados no backend.

---

# Regras de negócio esperadas do backend

- A nota deve estar entre 0 e 10.
- O curso precisa estar concluído para que o aluno possa ser aprovado.
- Nota maior ou igual a 7 deve resultar em aprovação.
- Cada aprovação deve liberar 3 cursos adicionais.
- Novas aprovações devem acumular os cursos adicionais já liberados.
- Uma reprovação não deve aumentar o total de cursos adicionais.
- Um curso ainda não concluído não deve liberar cursos adicionais.
- O histórico deve preservar as tentativas do aluno.
- O mesmo curso pode aparecer mais de uma vez no histórico em caso de nova tentativa.

---

# Integração esperada no frontend

O frontend Vue consome os endpoints através do arquivo:

```text
frontend/src/services/api.js
```

## Funções utilizadas

```js
criarAluno()
criarCurso()
processarConclusao()
buscarAluno()
listarAlunos()
listarCursos()
```

---

# Fluxo da aplicação

```text
Frontend Vue
    ↓
api.js
    ↓
API REST Spring Boot
    ↓
Controller
    ↓
Service
    ↓
Regras de negócio
    ↓
Repository / Banco de dados
```

---

# Fluxo de cadastro de aluno

```text
Usuário preenche o formulário
    ↓
Vue valida os campos
    ↓
POST /api/alunos
    ↓
Spring Boot cadastra o aluno
    ↓
Backend retorna JSON
    ↓
Vue adiciona o aluno à lista
```

---

# Fluxo de cadastro de curso

```text
Usuário informa o curso
    ↓
Vue valida o campo
    ↓
POST /api/cursos
    ↓
Spring Boot cadastra o curso
    ↓
Backend retorna JSON
    ↓
Vue adiciona o curso à lista
```

---

# Fluxo de conclusão de curso

```text
Usuário seleciona aluno e curso
    ↓
Informa nota e conclusão
    ↓
Vue faz validações simples
    ↓
POST /api/conclusoes
    ↓
Spring Boot aplica as regras de negócio
    ↓
Backend retorna o resultado
    ↓
Vue apresenta aprovação ou reprovação
    ↓
GET /api/alunos/{id}
    ↓
Vue atualiza cursos adicionais e histórico
```

---

# Carregamento inicial da página

Ao abrir ou recarregar a aplicação, o frontend deverá buscar os dados existentes no backend.

```text
Página Vue é carregada
    ↓
GET /api/alunos
GET /api/cursos
    ↓
Backend retorna os cadastros existentes
    ↓
Vue preenche os seletores de aluno e curso
```

Isso evita que os cadastros desapareçam visualmente após atualizar a página.

---

# Tratamento de erros

O frontend trata falhas nas requisições à API.

Exemplos:

```text
Backend indisponível. Não foi possível cadastrar o aluno.
```

```text
Backend indisponível. Não foi possível cadastrar o curso.
```

```text
Backend indisponível. Não foi possível processar a conclusão.
```

Caso a conclusão seja processada com sucesso, mas a atualização do aluno falhe:

```text
Conclusão processada, mas não foi possível atualizar o histórico do aluno.
```

Esse tratamento é importante para evitar que o usuário processe novamente uma conclusão que já tenha sido registrada no backend.

---

# Responsabilidades

## Frontend Vue

Responsável por:

- interface gráfica;
- formulários;
- validações simples;
- seleção de aluno e curso;
- chamadas HTTP;
- envio dos dados para a API;
- leitura dos JSONs retornados;
- exibição dos resultados;
- exibição do histórico;
- tratamento visual de erros.

## Backend Spring Boot

Responsável por:

- endpoints REST;
- persistência dos dados;
- regras de negócio;
- aprovação ou reprovação;
- liberação de cursos adicionais;
- cálculo do total acumulado;
- histórico de conclusões;
- validações de negócio;
- retorno dos JSONs esperados pelo frontend.
