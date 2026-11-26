package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.Linha;

import java.math.BigDecimal;
import java.util.UUID;

public record LinhaResponse(
        UUID id,
        String codigo,
        String nome,
        String bairroOrigem,
        String terminalDestino,
        String operadora,
        BigDecimal tarifa,
        boolean active
) {

    public static LinhaResponse from(Linha linha) {
        return new LinhaResponse(
                linha.id(),
                linha.codigo(),
                linha.nome(),
                linha.bairroOrigem(),
                linha.terminalDestino(),
                linha.operadora(),
                linha.tarifa(),
                linha.active()
        );
    }
}
