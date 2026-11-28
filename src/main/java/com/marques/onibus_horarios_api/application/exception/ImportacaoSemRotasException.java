package com.marques.onibus_horarios_api.application.exception;

public class ImportacaoSemRotasException extends RuntimeException {

    public ImportacaoSemRotasException() {
        super("O CSV não contém registros de rotas com origem em Águas Lindas de Goiás.");
    }
}
