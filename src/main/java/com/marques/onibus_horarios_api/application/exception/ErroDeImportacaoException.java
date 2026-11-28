package com.marques.onibus_horarios_api.application.exception;

public class ErroDeImportacaoException extends RuntimeException {

    public ErroDeImportacaoException(String message) {
        super(message);
    }

    public ErroDeImportacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
