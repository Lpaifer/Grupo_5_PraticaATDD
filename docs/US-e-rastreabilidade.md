# Histórias de usuário e rastreabilidade ATDD

O estudo de caso é **Educação Continuada Gamificada**. Os cinco textos abaixo são **propostas editoriais derivadas dos cenários BDD já documentados**. Eles não foram apresentados como a redação original dos integrantes. Cada responsável deve confirmar ou substituir a sua US e identificar quem a redigiu antes da entrega.

| Responsável pela validação | Proposta de US | Cenário BDD e evidências | Teste de domínio |
| --- | --- | --- | --- |
| Beatriz Canaveze Fontolan Soares | Como aluna que ainda está cursando um curso, quero que novos cursos permaneçam bloqueados até sua conclusão, para que a liberação reflita apenas cursos concluídos e aprovados. | [Não liberar enquanto o curso não for concluído](Beatriz.md) | `naoDeveLiberarCursosEnquantoCursoNaoForConcluido()` |
| Leonardo Godinho da Silva | Como aluno que conclui vários cursos com aprovação, quero acumular três cursos adicionais por aprovação, para que minhas conquistas anteriores sejam preservadas. | [Acumular após a segunda aprovação](Leonardo.md) | `deveAcumularSeisCursosAposSegundaAprovacao()` |
| Lucas Paifer | Como aluno que concluiu um curso com média exatamente 7,0, quero receber três cursos adicionais, para que a nota mínima de aprovação também garanta o benefício. | [Liberar com média 7,0](Lucas%20Paifer.md) | `deveLiberarTresCursosQuandoMediaForExatamente7()` |
| Matheus Nicolas Arizono Ywamoto | Como aluno que refez um curso após reprovação, quero receber três cursos adicionais quando a nova tentativa for aprovada, para que minha evolução seja considerada. | [Liberar após refazer o curso](MatheusYwamoto.md) | `deveLiberarTresCursosAoRefazerCursoEObterMedia8()` |
| Matheus Marcolino | Como aluno que concluiu um curso com média inferior a 7,0, quero que nenhum curso adicional seja liberado, para que o benefício seja concedido somente após aprovação. | [Não liberar com média inferior a 7,0](MatheusMarcolino.md) | `naoDeveLiberarCursosQuandoMediaForMenorQue7()` |
| Rafael Amorim | **Aguardando texto do integrante.** | **Aguardando cenário e evidências do integrante.** | **Aguardando teste do integrante.** |

## US escolhida pelo grupo: Aluno Básico

> **COMO** aluno assinante do plano básico  
> **QUERO** ter acesso liberado a mais 3 cursos ao concluir um curso com média igual ou superior a 7,0  
> **PARA** continuar avançando nos estudos e aproveitar melhor minha assinatura.

Essa é a US escolhida, conforme informado pelo grupo. A autoria da redação original ainda não foi informada. O critério de média exatamente 7,0 está verificado em `deveLiberarTresCursosQuandoMediaForExatamente7()`; o processamento de aprovações com notas superiores a 7,0 também aparece em `deveAcumularSeisCursosAposSegundaAprovacao()` e `deveLiberarTresCursosAoRefazerCursoEObterMedia8()`. Os cenários de curso não concluído e média inferior a 7,0 verificam que a liberação não ocorre fora das condições da US. A página [Lucas Paifer](Lucas%20Paifer.md) documenta especificamente o limite de 7,0 com RED/GREEN/BLUE.

Os testes relacionados estão em [`PlataformaGamificacaoTest.java`](../src/test/java/org/example/grupo_5_praticaatdd/domain/PlataformaGamificacaoTest.java). As páginas individuais preservam as evidências históricas de RED, GREEN e BLUE. A [cobertura atual](Cobertura-atual.md) refere-se ao projeto completo, com a exclusão declarada da classe de inicialização.

Antes de publicar, cada integrante deve conferir se sua proposta de US representa a história que de fato redigiu, registrar o texto final com autoria e confirmar a ligação com seu BDD. O grupo precisa informar quem redigiu a US escolhida.

## Verificação ponta a ponta em HOMOL

Os cenários existentes também são exercitados em [`gamificacao.cy.js`](../frontend/cypress/e2e/gamificacao.cy.js), usando Vue, Nginx, API e PostgreSQL reais. O [pipeline Jenkins](DevOps-Jenkins.md) publica os resultados JUnit e os vídeos do Cypress.

| Critério existente | Cenário Cypress |
|---|---|
| Não liberar antes da conclusão | `não libera cursos antes da conclusão` |
| Acumular após a segunda aprovação | `acumula seis cursos após aprovações em dois cursos diferentes` |
| Aprovar com nota exatamente 7,0 | `libera três cursos com nota exatamente 7` |
| Aprovar após refazer curso reprovado | `aprova uma nova tentativa e preserva a reprovação no histórico` |
| Não liberar com nota inferior a 7,0 | `não libera cursos com nota abaixo de 7` |

Os testes adicionais verificam cadastro/recarregamento, rejeição de nota inválida, erro de formulário, falha de rede e o comportamento atual de recompensar nova aprovação no mesmo curso. Eles ampliam a verificação técnica; a autoria das US continua sendo a registrada pelos integrantes.
