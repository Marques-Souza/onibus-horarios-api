package com.marques.onibus_horarios_api.dto;

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
}
