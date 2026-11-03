<script setup>
import { ref, watch } from 'vue'
import { listarRanking } from '../services/api'

const props = defineProps({ atualizacao: { type: Number, default: 0 } })
const ranking = ref([])
const erro = ref('')
const carregando = ref(false)
let consulta = 0

async function carregar() {
  const atual = ++consulta
  carregando.value = true
  erro.value = ''
  try {
    const resposta = await listarRanking()
    if (atual === consulta) ranking.value = resposta
  } catch (error) {
    if (atual === consulta) {
      ranking.value = []
      erro.value = `Não foi possível carregar o ranking. ${error.message}`
    }
  } finally {
    if (atual === consulta) carregando.value = false
  }
}

watch(() => props.atualizacao, carregar, { immediate: true })
</script>

<template>
  <section aria-labelledby="titulo-ranking" data-cy="ranking">
    <h2 id="titulo-ranking">Ranking</h2>
    <p>Conclusões aprovadas, com desempate pelo identificador do aluno.</p>
    <button type="button" :disabled="carregando" @click="carregar">Atualizar ranking</button>
    <p v-if="carregando" role="status">Carregando ranking...</p>
    <p v-else-if="erro" role="alert">{{ erro }}</p>
    <p v-else-if="ranking.length === 0">Nenhuma conclusão aprovada ainda.</p>
    <table v-else>
      <thead><tr><th scope="col">Posição</th><th scope="col">Aluno</th><th scope="col">Aprovações</th></tr></thead>
      <tbody>
        <tr v-for="aluno in ranking" :key="aluno.alunoId">
          <td>{{ aluno.posicao }}</td><td>{{ aluno.nome }}</td><td>{{ aluno.aprovacoes }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
