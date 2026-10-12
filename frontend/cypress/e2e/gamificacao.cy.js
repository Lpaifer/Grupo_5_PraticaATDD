const campo = nome => cy.get(`[data-cy="${nome}"]`)

function cadastrarDados(plano = 'Básico') {
  const identificador = `${Date.now()}-${Cypress._.random(100000)}`
  cy.request('POST', '/api/alunos', { nome: `Aluno E2E ${identificador}`, plano })
    .its('body').as('aluno')
  cy.request('POST', '/api/cursos', { nome: `Curso E2E ${identificador}` })
    .its('body').as('curso')
  cy.intercept('GET', '/api/alunos').as('listarAlunos')
  cy.intercept('GET', '/api/cursos').as('listarCursos')
  cy.visit('/')
  cy.wait(['@listarAlunos', '@listarCursos'])
  cy.get('@aluno').then(aluno => campo('selecionar-aluno').select(String(aluno.id)))
  cy.get('@curso').then(curso => campo('selecionar-curso').select(String(curso.id)))
  cy.intercept('POST', '/api/conclusoes').as('conclusao')
  cy.intercept('GET', '/api/alunos/*').as('historico')
}

function concluir(nota, concluido, total) {
  campo('nota').clear().type(String(nota))
  campo('concluido')[concluido ? 'check' : 'uncheck']()
  campo('processar-conclusao').click()
  cy.wait('@conclusao').its('response.statusCode').should('eq', 200)
  cy.wait('@historico')
  campo('total-cursos').should('have.text', String(total))
}

describe('Educação Continuada Gamificada — homologação', () => {
  it('cadastra aluno e curso pela interface e mantém os cadastros após recarregar', () => {
    const id = `${Date.now()}-${Cypress._.random(100000)}`
    const aluno = `Cadastro E2E ${id}`
    const curso = `Curso cadastro E2E ${id}`
    cy.visit('/')
    cy.contains('h1', 'Educação Continuada Gamificada').should('be.visible')
    cy.intercept('POST', '/api/alunos').as('criarAluno')
    campo('nome-aluno').type(aluno)
    campo('plano-aluno').select('Premium')
    campo('cadastrar-aluno').click()
    cy.wait('@criarAluno').its('response.statusCode').should('eq', 201)
    cy.intercept('POST', '/api/cursos').as('criarCurso')
    campo('nome-curso').type(curso)
    campo('cadastrar-curso').click()
    cy.wait('@criarCurso').its('response.statusCode').should('eq', 201)
    cy.reload()
    campo('selecionar-aluno').find('option').should('contain.text', aluno)
    campo('selecionar-curso').find('option').should('contain.text', curso)
  })

  for (const cenario of [
    { titulo: 'libera três cursos com nota exatamente 7', nota: 7, concluido: true, total: 3 },
    { titulo: 'não libera cursos com nota abaixo de 7', nota: 6.9, concluido: true, total: 0 },
    { titulo: 'não libera cursos antes da conclusão', nota: 8, concluido: false, total: 0 },
  ]) {
    it(cenario.titulo, () => {
      cadastrarDados()
      concluir(cenario.nota, cenario.concluido, cenario.total)
      campo('historico').find('.historico-item').should('have.length', 1)
      campo('resultado').find(
        cenario.total ? '.status-aprovado' : '.status-reprovado',
      ).should('contain.text', cenario.total ? 'Aprovado' : 'Não aprovado')
      cy.get('@aluno').then(aluno => {
        cy.request(`/api/alunos/${aluno.id}`).its('body.cursosAdicionais').should('eq', cenario.total)
      })
    })
  }

  it('aprova uma nova tentativa e preserva a reprovação no histórico', () => {
    cadastrarDados()
    concluir(6, true, 0)
    concluir(8, true, 3)
    campo('historico').find('.historico-item').should('have.length', 2)
    campo('historico').find('.status-reprovado').should('have.length', 1)
    campo('historico').find('.status-aprovado').should('have.length', 1)
  })

  it('acumula seis cursos após aprovações em dois cursos diferentes', () => {
    cadastrarDados('Premium')
    concluir(8, true, 3)
    cy.request('POST', '/api/cursos', { nome: `Segundo curso E2E ${Date.now()}` })
      .its('body').then(curso => {
        cy.reload()
        cy.wait(['@listarAlunos', '@listarCursos'])
        cy.get('@aluno').then(aluno => campo('selecionar-aluno').select(String(aluno.id)))
        campo('selecionar-curso').select(String(curso.id))
      })
    concluir(7.5, true, 6)
    campo('historico').find('.curso-historico').should('have.length', 2)
  })

  it('preserva a regra atual de conceder recompensa por nova aprovação no mesmo curso', () => {
    cadastrarDados()
    concluir(7, true, 3)
    concluir(9, true, 6)
    campo('historico').find('.historico-item').should('have.length', 2)
  })

  it('rejeita nota inválida na API sem alterar o histórico ou o saldo', () => {
    cadastrarDados()
    cy.get('@aluno').then(aluno => {
      cy.get('@curso').then(curso => {
        cy.request({
          method: 'POST', url: '/api/conclusoes', failOnStatusCode: false,
          body: { alunoId: aluno.id, cursoId: curso.id, nota: 11, concluido: true },
        }).its('status').should('eq', 400)
      })
      cy.request(`/api/alunos/${aluno.id}`).its('body').should(body => {
        expect(body.cursosAdicionais).to.eq(0)
        expect(body.historico).to.have.length(0)
      })
    })
  })

  it('informa o erro ao tentar cadastrar aluno sem nome', () => {
    cy.visit('/')
    campo('cadastrar-aluno').click()
    campo('erro-aluno').should('contain.text', 'Informe o nome do aluno.')
  })

  it('apresenta falha de rede sem exibir uma aprovação', () => {
    cadastrarDados()
    cy.intercept('POST', '/api/conclusoes', { forceNetworkError: true })
    campo('nota').type('8')
    campo('concluido').check()
    campo('processar-conclusao').click()
    campo('erro-conclusao').should('contain.text', 'Backend indisponível')
    campo('resultado').should('not.exist')
  })
})
