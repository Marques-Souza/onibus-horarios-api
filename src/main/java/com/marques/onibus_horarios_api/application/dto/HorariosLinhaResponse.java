package com.marques.onibus_horarios_api.application.dto;

import java.util.List;

public record HorariosLinhaResponse(
        LinhaResponse linha,
        List<HorarioResponse> horarios
) {
}
