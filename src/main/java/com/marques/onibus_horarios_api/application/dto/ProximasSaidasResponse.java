package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.TipoDia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProximasSaidasResponse(
        String origem,
        String destino,
        String codigoLinha,
        String bairroOrigem,
        LocalDateTime dataHoraConsulta,
        LocalDate dataOperacao,
        TipoDia tipoDia,
        HorarioDestinoResponse proximaSaida,
        List<HorarioDestinoResponse> proximasSaidas
) {
}
