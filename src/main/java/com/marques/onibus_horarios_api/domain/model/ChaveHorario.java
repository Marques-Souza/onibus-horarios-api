package com.marques.onibus_horarios_api.domain.model;

import java.time.LocalTime;
import java.util.UUID;

public record ChaveHorario(
        UUID linhaId,
        Sentido sentido,
        TipoDia tipoDia,
        LocalTime horaPartida
) {
}
