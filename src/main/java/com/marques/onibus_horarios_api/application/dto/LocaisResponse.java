package com.marques.onibus_horarios_api.application.dto;

import java.util.List;

public record LocaisResponse(
        List<String> bairrosOrigem,
        List<String> terminaisDestino
) {
}
