# Contrato da API - Frontend Vue

## Base URL

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
  "cursosAdicionais": 0
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

## 4. Buscar aluno

### Endpoint

```text
GET /api/alunos/{id}
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
      "nota": 5.0,
      "concluido": true,
      "aprovado": false
    },
    {
      "cursoId": 10,
      "curso": "Geologia",
      "nota": 8.0,
      "concluido": true,
      "aprovado": true
    }
  ]
}
```

---

## Regras esperadas pelo frontend

- A nota deve estar entre 0 e 10.
- O curso precisa estar concluído para que o aluno possa ser aprovado.
- Nota maior ou igual a 7 deve resultar em aprovação.
- Cada aprovação deve liberar 3 cursos adicionais.
- Novas aprovações devem acumular os cursos adicionais já liberados.
- Uma reprovação não deve aumentar o total de cursos adicionais.
- O histórico deve preservar todas as tentativas do aluno.
- O mesmo curso pode aparecer mais de uma vez no histórico em caso de nova tentativa.

---

## Integração esperada no frontend

O frontend Vue consumirá os endpoints através do arquivo:

```text
frontend/src/services/api.js
```

Funções previstas:

```js
criarAluno()
criarCurso()
processarConclusao()
buscarAluno()
```

Fluxo esperado:

```text
Vue
↓
api.js
↓
API REST Spring Boot
↓
Controller
↓
Service / Regras de negócio
↓
Domínio / Banco de dados
```