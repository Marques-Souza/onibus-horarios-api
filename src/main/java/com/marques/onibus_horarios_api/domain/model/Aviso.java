package com.marques.onibus_horarios_api.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record Aviso(
        UUID id,
        UUID linhaId,
        TipoAviso tipo,
        String mensagem,
        LocalDateTime horarioReferencia,
        Instant criadoEm,
        StatusAviso status
) {
}
