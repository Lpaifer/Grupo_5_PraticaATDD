# Evidência — cobertura JaCoCo atual

## Mini-lab B1 — 28/10/2026

Após o primeiro recorte modular, `mvnw clean verify` passou com **25 testes**, sem falhas, erros ou testes ignorados. Nas 25 classes analisadas, a cobertura foi de **682/682 instruções, 11/11 branches e 144/144 linhas (100%)**. A exclusão da classe de inicialização e os controles de cobertura integral foram mantidos. Consulte a [validação do Desafio 1](arquitetura/evidencias/desafio-1/validacao.md) para ambiente, comandos, escopo e limitações.

## Evidência histórica — 14/09/2026

Verificação anterior ao mini-lab:

```sh
./mvnw clean verify
```

```text
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
BUILD SUCCESS
```

O relatório `target/site/jacoco/index.html` e o CSV `target/site/jacoco/jacoco.csv` apresentaram, nas **25 classes analisadas**:

![Relatório JaCoCo atual com cobertura de 100% nas classes analisadas](evidencias/Cobertura_JaCoCo_Atual.png)

| Métrica | Coberto | Perdido | Cobertura |
| --- | ---: | ---: | ---: |
| Instruções | 729 | 0 | 100% |
| Branches | 11 | 0 | 100% |
| Linhas | 168 | 0 | 100% |

A classe `Grupo5PraticaAtddApplication` foi excluída do relatório por conter somente o método `main` que inicializa o Spring Boot, sem regra de negócio. As demais classes permanecem incluídas. O plugin JaCoCo executa `check` na fase `verify` e falha o build se alguma instrução, branch ou linha dessas classes ficar sem cobertura.

A imagem acima foi capturada do relatório gerado pelo Maven em 13/09/2026. Após o cenário do Rafael, a execução textual de 14/09/2026 passou com 20 testes e manteve as mesmas métricas de cobertura, pois nenhuma classe de produção foi alterada. Para reproduzir o relatório atualizado, execute o comando acima e abra `target/site/jacoco/index.html` no navegador. O diretório `target/` é ignorado pelo Git; esta página preserva o resultado e o comando de reprodução.
