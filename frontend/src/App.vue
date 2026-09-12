<script setup>
  import { ref, computed } from 'vue'

  import {
    criarAluno,
    criarCurso,
    processarConclusao as processarConclusaoApi,
    buscarAluno,
  } from './services/api'

  const nomeAluno = ref('')
  const planoAluno = ref('Básico')
  const alunos = ref([])
  const nomeCurso = ref('')
  const cursos = ref([])

  const alunoSelecionado = ref('')
  const cursoSelecionado = ref('')
  const nota = ref('')
  const concluido = ref(false)

  const erro = ref('')
  const resultado = ref(null)

  const historicoPorCurso = computed(() => {
    const aluno = alunos.value.find(
      aluno => aluno.id === alunoSelecionado.value
    )

    if (!aluno || !aluno.historico) {
      return {}
    }

    return aluno.historico.reduce((grupos, item) => {
      if (!grupos[item.cursoId]) {
        grupos[item.cursoId] = {
          curso: item.curso,
          tentativas: [],
        }
      }

      grupos[item.cursoId].tentativas.push(item)

      return grupos
    }, {})
  })

  async function cadastrarAluno() {
    erro.value = ''

    if (!nomeAluno.value.trim()) {
      erro.value = 'Informe o nome do aluno.'
      return
    }

    try {
      const alunoCriado = await criarAluno({
        nome: nomeAluno.value.trim(),
        plano: planoAluno.value,
      })

      alunos.value.push({
        ...alunoCriado,
        historico: alunoCriado.historico ?? [],
      })

      nomeAluno.value = ''
      planoAluno.value = 'Básico'
    } catch (e) {
      erro.value = 'Backend indisponível. Não foi possível cadastrar o aluno.'
    }
  }

  async function cadastrarCurso() {
    erro.value = ''

    if (!nomeCurso.value.trim()) {
      erro.value = 'Informe o nome do curso.'
      return
    }

    try {
      const cursoCriado = await criarCurso({
        nome: nomeCurso.value.trim(),
      })

      cursos.value.push(cursoCriado)

      nomeCurso.value = ''
    } catch (e) {
      erro.value = 'Backend indisponível. Não foi possível cadastrar o curso.'
    }
  }
    
  async function processarConclusao() {
    erro.value = ''

    const aluno = alunos.value.find(
      aluno => aluno.id === alunoSelecionado.value
    )

    const curso = cursos.value.find(
      curso => curso.id === cursoSelecionado.value
    )

    if (!aluno || !curso || nota.value === '') {
      erro.value = 'Preencha aluno, curso e nota antes de processar.'
      return
    }

    const notaNumerica = Number(nota.value)

    if (
      !Number.isFinite(notaNumerica) ||
      notaNumerica < 0 ||
      notaNumerica > 10
    ) {
      erro.value = 'A nota deve estar entre 0 e 10.'
      return
    }

    try {
      const resposta = await processarConclusaoApi({
        alunoId: aluno.id,
        cursoId: curso.id,
        nota: notaNumerica,
        concluido: concluido.value,
      })

      resultado.value = resposta

      const alunoAtualizado = await buscarAluno(aluno.id)

      const indiceAluno = alunos.value.findIndex(
        item => item.id === aluno.id
      )

      if (indiceAluno !== -1) {
        alunos.value[indiceAluno] = alunoAtualizado
      }

      nota.value = ''
      concluido.value = false
    } catch (e) {
      erro.value =
        'Backend indisponível. Não foi possível processar a conclusão.'
    }
  }

</script>


<template>
  <header class="topbar">
    <div class="topbar-content">
      <div>
        <h1>Educação Continuada Gamificada</h1>
        <p>Projeto ATDD - Grupo 5</p>
      </div>

      <span>DevOps & QA</span>
    </div>
  </header>
  <main>

    <section>
      <h2>Cadastrar Aluno</h2>

      <div class="form-grid">
        <div>
          <label>Nome</label>
          <input
            v-model="nomeAluno"
            type="text"
            placeholder="Digite o nome do aluno"
          />
        </div>

        <div>
          <label>Plano</label>
          <select v-model="planoAluno">
            <option>Básico</option>
            <option>Premium</option>
          </select>
        </div>
      </div>

      <button @click="cadastrarAluno">
        Cadastrar Aluno
      </button>

      <div v-if="alunos.length > 0">
        <h3>Alunos cadastrados</h3>

        <ul>
          <li v-for="aluno in alunos" :key="aluno.id">
            {{ aluno.nome }} - {{ aluno.plano }}
          </li>
        </ul>
      </div>
    </section>

    <section>
      <h2>Cadastrar Curso</h2>

      <label>Nome do Curso</label>
      <input
        v-model="nomeCurso"
        type="text"
        placeholder="Digite o nome do curso"
      />

      <button @click="cadastrarCurso">
        Cadastrar Curso
      </button>
      <div v-if="cursos.length > 0">
        <h3>Cursos cadastrados</h3>

        <ul>
          <li v-for="curso in cursos" :key="curso.id">
            {{ curso.nome }}
          </li>
        </ul>
      </div>
    </section>

    <section>
      <h2>Registrar Conclusão</h2>

      <div class="form-grid">
        <div>
          <label>Aluno</label>
          <select v-model="alunoSelecionado">
            <option disabled value="">Selecione um aluno</option>

            <option
              v-for="aluno in alunos"
              :key="aluno.id"
              :value="aluno.id"
            >
              {{ aluno.nome }}
            </option>
          </select>
        </div>

        <div>
          <label>Curso</label>
          <select v-model="cursoSelecionado">
            <option disabled value="">Selecione um curso</option>

            <option
              v-for="curso in cursos"
              :key="curso.id"
              :value="curso.id"
            >
              {{ curso.nome }}
            </option>
          </select>
        </div>

        <div class="campo-largo">
          <label>Nota</label>
          <input
            v-model="nota"
            type="number"
            min="0"
            max="10"
            step="0.1"
          />
        </div>
      </div>

      <label>
        <input
          v-model="concluido"
          type="checkbox"
        />
        Curso concluído
      </label>
      
      <p v-if="erro" class="mensagem-erro">
        {{ erro }}
      </p>

      <button @click="processarConclusao">
        Processar Conclusão
      </button>
    </section>

    <section>
      <h2>Resultado</h2>

      <div v-if="resultado" class="resultado-card">
        <div class="resultado-grid">
          <p><strong>Aluno:</strong> {{ resultado.aluno }}</p>
          <p><strong>Curso:</strong> {{ resultado.curso }}</p>
          <p><strong>Nota:</strong> {{ resultado.nota }}</p>
          <p>
            <strong>Status:</strong>
            <span
              :class="resultado.aprovado ? 'status-aprovado' : 'status-reprovado'"
            >
              {{ resultado.aprovado ? 'Aprovado' : 'Não aprovado' }}
            </span>
          </p>
          <p class="resultado-total">
            <strong>Total de cursos adicionais liberados:</strong>
            <span class="total-cursos">{{ resultado.cursosAdicionais }}</span>
          </p>
        </div>

        <h3>Histórico do aluno</h3>

        <div class="historico-cursos">
          <div
            v-for="grupo in historicoPorCurso"
            :key="grupo.curso"
            class="curso-historico"
          >
            <h4>{{ grupo.curso }}</h4>

            <div
              v-for="item in grupo.tentativas"
              :key="`${item.cursoId}-${item.nota}-${item.concluido}`"
              class="historico-item"
            >
              <span>
                <strong>Nota:</strong>
                {{ item.nota }}
              </span>

              <span
                :class="item.aprovado ? 'status-aprovado' : 'status-reprovado'"
              >
                {{ item.aprovado ? 'Aprovado' : 'Não aprovado' }}
              </span>

              <span>
                {{ item.aprovado ? '+3 cursos' : '+0 cursos' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <p v-else>
        Nenhum resultado processado ainda.
      </p>
    </section>
  </main>
  <footer class="footer">
    <div class="footer-content">
      <p>Grupo 5 - DevOps & QA</p>

      <div class="footer-members">
        <span>Beatriz Canaveze Fontolan Soares - 235099</span>
        <span>Leonardo Godinho da Silva - 236764</span>
        <span>Lucas Paifer - 236576</span>
        <span>Matheus Nicolas Arizono Ywamoto - 235912</span>
        <span>Matheus Marcolino - 161109</span>
        <span>Rafael Amorim - 223380</span>
      </div>
    </div>
  </footer>
</template>

<style scoped>
:global(body) {
  margin: 0;
  background: #f7f9fb;
  color: #2f3a45;
  font-family: Arial, Helvetica, sans-serif;
}

/* HEADER */

.topbar {
  width: 100%;
  background: #003852;
  color: #ffffff;
  padding: 22px 0;
  box-shadow: 0 2px 8px rgba(0, 56, 82, 0.18);
}

.topbar-content {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
  box-sizing: border-box;

  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.topbar h1 {
  margin: 0;
  color: #ffffff;
  font-size: 28px;
}

.topbar p {
  margin: 6px 0 0;
  color: #d8e8ef;
}

.topbar span {
  font-weight: 600;
  color: #ffffff;
}

/* CONTEÚDO PRINCIPAL */

main {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 40px 24px 60px;
  box-sizing: border-box;
}

h2 {
  margin-top: 0;
  color: #003852;
  font-size: 22px;
}

h3 {
  margin-top: 24px;
  color: #2f3a45;
}

section {
  margin-top: 24px;
  padding: 24px;
  background: #ffffff;
  border: 1px solid #d9e1e8;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 56, 82, 0.08);
}

/* FORMULÁRIOS */

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  width: 100%;
}

.form-grid input,
.form-grid select {
  max-width: 100%;
}

label {
  display: block;
  margin-top: 16px;
  margin-bottom: 6px;
  font-weight: 600;
  color: #2f3a45;
}

input,
select {
  width: 100%;
  max-width: none;
  box-sizing: border-box;
  padding: 10px 12px;

  border: 1px solid #cbd5e1;
  border-radius: 4px;

  font-size: 14px;
  background: #ffffff;
  color: #2f3a45;
}

input:focus,
select:focus {
  outline: none;
  border-color: #009fe3;
  box-shadow: 0 0 0 2px rgba(0, 159, 227, 0.12);
}

input[type='checkbox'] {
  width: auto;
  margin-right: 8px;
  accent-color: #009fe3;
}

button {
  margin-top: 18px;
  padding: 10px 18px;

  border: none;
  border-radius: 4px;

  background: #003852;
  color: #ffffff;

  font-size: 14px;
  font-weight: 600;

  cursor: pointer;
  transition: background 0.2s ease;
}

button:hover {
  background: #00597d;
}

.campo-largo {
  grid-column: 1 / -1;
}

.mensagem-erro {
  margin-top: 12px;
  color: #b42318;
  font-weight: 600;
}

/* RESULTADO */

.resultado-card {
  margin-top: 12px;
}

.resultado-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 24px;
}

.resultado-grid p {
  margin: 6px 0;
}

.resultado-total {
  grid-column: 1 / -1;
}

.total-cursos {
  margin-left: 6px;
  font-weight: 700;
  color: #003852;
}

.status-aprovado {
  color: #16803c;
  font-weight: 700;
}

.status-reprovado {
  color: #b42318;
  font-weight: 700;
}

/* HISTÓRICO */

.historico-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1.5fr 1fr;
  gap: 16px;

  align-items: center;

  padding: 10px 12px;

  background: #f7f9fb;
  border: 1px solid #d9e1e8;
  border-radius: 6px;
}

.historico-cursos {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.curso-historico {
  padding: 14px;
  background: #f7f9fb;
  border: 1px solid #d9e1e8;
  border-radius: 8px;
}

.curso-historico h4 {
  margin: 0 0 10px;
  color: #003852;
  font-size: 16px;
}

.curso-historico .historico-item {
  background: #ffffff;
  margin-top: 8px;
}

/* TEXTOS */

ul {
  padding-left: 22px;
}

li {
  margin-bottom: 6px;
}

strong {
  color: #003852;
}

/* FOOTER */

.footer {
  width: 100%;
  margin-top: 40px;

  background: #003852;
  color: #ffffff;

  padding: 24px 0;
}

.footer-content {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
  box-sizing: border-box;
}

.footer-content p {
  margin: 0 0 12px;
  font-weight: 700;
}

.footer-members {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 24px;

  font-size: 14px;
  color: #d8e8ef;
}

/* RESPONSIVO */

@media (max-width: 600px) {
  .topbar-content {
    padding: 0 16px;
    align-items: flex-start;
    flex-direction: column;
  }

  .topbar h1 {
    font-size: 24px;
  }

  main {
    padding: 24px 16px;
  }

  section {
    padding: 18px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .resultado-grid {
    grid-template-columns: 1fr;
  }

  .historico-item {
    grid-template-columns: 1fr;
    gap: 4px;
  }

  .footer-members {
    grid-template-columns: 1fr;
  }

  .footer-content {
    padding: 0 16px;
  }

  input,
  select {
    max-width: 100%;
  }
}
</style>