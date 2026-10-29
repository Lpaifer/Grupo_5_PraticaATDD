package org.example.grupo_5_praticaatdd.gamificacao.api;

import org.example.grupo_5_praticaatdd.gamificacao.internal.dominio.PoliticaConclusao;

/** Entrada pública do módulo; não realiza consultas nem altera dados persistidos. */
public final class AvaliarConclusao {

    private final PoliticaConclusao politica = new PoliticaConclusao();

    public ResultadoConclusao avaliar(DadosConclusao dados) {
        return politica.avaliar(dados);
    }
}
