package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.StatusAviso;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusAvisoRequest(@NotNull StatusAviso status) {
}
