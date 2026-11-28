package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.TipoAviso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvisoRequest(
        UUID linhaId,
        @NotNull TipoAviso tipo,
        @NotBlank @Size(min = 10, max = 500) String mensagem,
        LocalDateTime horarioReferencia,
        @Size(max = 200) String website
) {
}
