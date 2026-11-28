package com.marques.onibus_horarios_api.domain.exception;

public class CodigoDeLinhaDuplicadoException extends RuntimeException {

    public CodigoDeLinhaDuplicadoException(String codigo) {
        super("Já existe uma linha cadastrada com o código: " + codigo);
    }
}
