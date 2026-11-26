package com.marques.onibus_horarios_api.application.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

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
