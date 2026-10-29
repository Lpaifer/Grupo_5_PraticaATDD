package org.example.grupo_5_praticaatdd.gamificacao.internal.dominio;

import org.example.grupo_5_praticaatdd.gamificacao.api.DadosConclusao;
import org.example.grupo_5_praticaatdd.gamificacao.api.ResultadoConclusao;

/** Implementação interna: consumidores externos devem usar gamificacao.api. */
public final class PoliticaConclusao {

    private static final double MEDIA_MINIMA_APROVACAO = 7.0;
    private static final int QUANTIDADE_CURSOS_LIBERADOS = 3;

    public ResultadoConclusao avaliar(DadosConclusao dados) {
        boolean aprovado = dados.concluido() && dados.nota() >= MEDIA_MINIMA_APROVACAO;
        int saldo = dados.cursosAdicionaisAtuais();
        if (aprovado) {
            saldo += QUANTIDADE_CURSOS_LIBERADOS;
        }
        return new ResultadoConclusao(aprovado, saldo);
    }
}
