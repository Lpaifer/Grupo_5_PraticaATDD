const API_URL = 'http://localhost:8080'

async function apiRequest(endpoint, options = {}) {
  const response = await fetch(`${API_URL}${endpoint}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (!response.ok) {
    throw new Error(`Erro na API: ${response.status}`)
  }

  return response.json()
}

async function criarAluno(aluno) {
  return apiRequest('/api/alunos', {
    method: 'POST',
    body: JSON.stringify(aluno),
  })
}

async function criarCurso(curso) {
  return apiRequest('/api/cursos', {
    method: 'POST',
    body: JSON.stringify(curso),
  })
}

async function processarConclusao(conclusao) {
  return apiRequest('/api/conclusoes', {
    method: 'POST',
    body: JSON.stringify(conclusao),
  })
}

async function buscarAluno(id) {
  return apiRequest(`/api/alunos/${id}`)
}

export {
  API_URL,
  criarAluno,
  criarCurso,
  processarConclusao,
  buscarAluno,
}