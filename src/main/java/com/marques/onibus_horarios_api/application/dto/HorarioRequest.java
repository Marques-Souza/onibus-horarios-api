package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record HorarioRequest(
        @NotNull Sentido sentido,
        @NotNull TipoDia tipoDia,
        @NotNull LocalTime horaPartida,
        @Size(max = 255) String observacao
) {
}
