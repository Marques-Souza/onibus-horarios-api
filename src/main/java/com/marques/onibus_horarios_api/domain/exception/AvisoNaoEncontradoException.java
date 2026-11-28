package com.marques.onibus_horarios_api.domain.exception;

public class AvisoNaoEncontradoException extends RuntimeException {

    public AvisoNaoEncontradoException() {
        super("Aviso não encontrado.");
    }
}
