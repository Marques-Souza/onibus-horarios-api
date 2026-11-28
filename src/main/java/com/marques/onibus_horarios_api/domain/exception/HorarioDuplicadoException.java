package com.marques.onibus_horarios_api.domain.exception;

public class HorarioDuplicadoException extends RuntimeException {

    public HorarioDuplicadoException() {
        super("Já existe um horário com a mesma linha, sentido, tipo de dia e hora de partida.");
    }
}
