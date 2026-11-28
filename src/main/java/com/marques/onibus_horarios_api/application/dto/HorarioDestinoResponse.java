package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.HorarioDaLinha;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;

import java.time.LocalTime;
import java.util.UUID;

public record HorarioDestinoResponse(
        UUID linhaId,
        String codigoLinha,
        String nomeLinha,
        String operadora,
        String bairroOrigem,
        Sentido sentido,
        String origem,
        String terminalDestino,
        TipoDia tipoDia,
        LocalTime horaPartida,
        String observacao
) {

    public static HorarioDestinoResponse from(HorarioDaLinha horarioDaLinha) {
        var horario = horarioDaLinha.horario();
        var linha = horarioDaLinha.linha();
        String origem = horario.sentido() == Sentido.IDA
                ? "Águas Lindas de Goiás"
                : linha.terminalDestino();
        String destino = horario.sentido() == Sentido.IDA
                ? linha.terminalDestino()
                : "Águas Lindas de Goiás";

        return new HorarioDestinoResponse(
                linha.id(),
                linha.codigo(),
                linha.nome(),
                linha.operadora(),
                linha.bairroOrigem(),
                horario.sentido(),
                origem,
                destino,
                horario.tipoDia(),
                horario.horaPartida(),
                horario.observacao()
        );
    }
}
