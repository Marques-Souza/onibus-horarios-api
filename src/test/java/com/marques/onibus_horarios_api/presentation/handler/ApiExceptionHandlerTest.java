package com.marques.onibus_horarios_api.presentation.handler;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void deveOcultarDetalhesInternosNoErro500() {
        var problem = handler.handleUnexpected(new IllegalStateException("database password leaked"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problem.getStatus());
        assertEquals("Ocorreu um erro interno. Tente novamente.", problem.getDetail());
        assertFalse(problem.getDetail().contains("database password"));
    }

    @Test
    void deveTransformarViolacaoDeIntegridadeEm409SemDetalhesDoBanco() {
        var problem = handler.handleConflict(
                new DataIntegrityViolationException("duplicate key secret constraint details")
        );

        assertEquals(HttpStatus.CONFLICT.value(), problem.getStatus());
        assertEquals("O registro conflita com dados já existentes.", problem.getDetail());
        assertFalse(problem.getDetail().contains("secret"));
    }
}
