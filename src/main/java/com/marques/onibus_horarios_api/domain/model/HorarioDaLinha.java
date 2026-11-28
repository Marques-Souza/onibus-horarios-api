package com.marques.onibus_horarios_api.domain.model;

public record HorarioDaLinha(
        Horario horario,
        Linha linha
) {
}
