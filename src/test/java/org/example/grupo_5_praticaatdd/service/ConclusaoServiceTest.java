package org.example.grupo_5_praticaatdd.service;

import org.example.grupo_5_praticaatdd.api.dto.ProcessarConclusaoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ConclusaoServiceTest {

    @Autowired
    private ConclusaoService conclusaoService;

    @Test
    void notaNaoFinitaNaoPodeSerProcessada() {
        var request = new ProcessarConclusaoRequest(1L, 1L, Double.NaN, true);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> conclusaoService.processar(request));

        assertEquals("A nota deve estar entre 0 e 10.", exception.getMessage());
    }
}
