package com.marques.onibus_horarios_api.domain.model;

import java.time.LocalTime;
import java.util.UUID;

public record Horario(

        UUID id,
        UUID linhaId,
        Sentido sentido,
        TipoDia tipoDia,
        LocalTime horaPartida,
        String observacao,
        boolean active
) {
}
