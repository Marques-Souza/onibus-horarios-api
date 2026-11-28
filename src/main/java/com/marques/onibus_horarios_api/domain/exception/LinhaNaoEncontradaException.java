package com.marques.onibus_horarios_api.domain.exception;

public class LinhaNaoEncontradaException extends RuntimeException {

    public LinhaNaoEncontradaException() {
        super("Linha não encontrada.");
    }
}
