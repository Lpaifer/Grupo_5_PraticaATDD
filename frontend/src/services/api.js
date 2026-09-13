const API_URL = 'http://localhost:8080'

async function apiRequest(endpoint, options = {}) {
  let response

  try {
    response = await fetch(`${API_URL}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    })
    } catch (error) {
    throw new Error('Backend indisponível', { cause: error })
    }

  if (!response.ok) {
    throw new Error(`Erro na API: ${response.status}`)
  }

  try {
    return await response.json()
    } catch (error) {
    throw new Error('Resposta inválida do backend', { cause: error })
    }
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

async function listarAlunos() {
  return apiRequest('/api/alunos')
}

async function listarCursos() {
  return apiRequest('/api/cursos')
}

export {
  API_URL,
  criarAluno,
  criarCurso,
  processarConclusao,
  buscarAluno,
  listarAlunos,
  listarCursos,
}