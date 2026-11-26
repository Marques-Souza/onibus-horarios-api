package com.marques.onibus_horarios_api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LinhaRequest(

        @NotBlank @Size(max = 20)
        String codigo,

        @NotBlank @Size(max = 150)
        String nome,

        @NotBlank @Size(max = 100)
        String bairroOrigem,

        @NotBlank @Size(max = 100)
        String terminalDestino,

        @NotBlank @Size(max = 100)
        String operadora,

        @NotNull @Positive @Digits(integer = 6, fraction = 2)
        BigDecimal tarifa
) {
}
