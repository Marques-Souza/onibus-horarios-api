package com.marques.onibus_horarios_api.domain.exception;

public class HorarioNaoEncontradoException extends RuntimeException {

    public HorarioNaoEncontradoException() {
        super("Horário não encontrado.");
    }
}
