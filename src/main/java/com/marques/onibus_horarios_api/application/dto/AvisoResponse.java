package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.Aviso;
import com.marques.onibus_horarios_api.domain.model.StatusAviso;
import com.marques.onibus_horarios_api.domain.model.TipoAviso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record AvisoResponse(
        UUID id,
        UUID linhaId,
        TipoAviso tipo,
        String mensagem,
        LocalDateTime horarioReferencia,
        Instant criadoEm,
        StatusAviso status
) {

    public static AvisoResponse from(Aviso aviso) {
        return new AvisoResponse(
                aviso.id(),
                aviso.linhaId(),
                aviso.tipo(),
                aviso.mensagem(),
                aviso.horarioReferencia(),
                aviso.criadoEm(),
                aviso.status()
        );
    }
}
