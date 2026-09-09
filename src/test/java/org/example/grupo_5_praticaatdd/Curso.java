package org.example.grupo_5_praticaatdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CursoTest {

    @Test
    public void deveCriarCursoComNomeECargaHoraria() {

        // ARRANGE
        String nomeEsperado = "Engenharia da Computação";
        int cargaHorariaEsperada = 3600;

        Curso curso = new Curso(nomeEsperado, cargaHorariaEsperada);

        // ACTION
        String nomeObtido = curso.getNome();
        int cargaHorariaObtida = curso.getCargaHoraria();

        // ASSERT
        assertEquals(nomeEsperado, nomeObtido);
        assertEquals(cargaHorariaEsperada, cargaHorariaObtida);

    }
}