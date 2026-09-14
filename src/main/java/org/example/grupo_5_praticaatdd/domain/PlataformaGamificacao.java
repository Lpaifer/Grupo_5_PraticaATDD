package org.example.grupo_5_praticaatdd.domain;

public class PlataformaGamificacao {
    
    private static final int QUANTIDADE_CURSOS_LIBERADOS = 3;

    public void processarConclusao(ConclusaoCurso conclusao) {
        
        if (conclusao.foiAprovado()) {

            conclusao
                .getAluno()
                .liberarCursos(QUANTIDADE_CURSOS_LIBERADOS);
            
        }
    }
}
