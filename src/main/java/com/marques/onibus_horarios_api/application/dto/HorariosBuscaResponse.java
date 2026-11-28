package com.marques.onibus_horarios_api.application.dto;

import java.util.List;

public record HorariosBuscaResponse(
        String origem,
        String destino,
        String codigoLinha,
        String bairroOrigem,
        List<HorarioDestinoResponse> horarios
) {
}
