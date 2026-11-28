package com.marques.onibus_horarios_api.application.dto;

import java.time.Instant;

public record SobreDadosResponse(
        String fonte,
        Instant ultimaImportacao
) {
}
