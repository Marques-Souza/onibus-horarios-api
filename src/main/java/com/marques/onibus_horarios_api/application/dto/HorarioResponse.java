package com.marques.onibus_horarios_api.application.dto;

import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;

import java.time.LocalTime;
import java.util.UUID;

public record HorarioResponse(
        UUID id,
        Sentido sentido,
        String sentidoDescricao,
        String origem,
        String destino,
        TipoDia tipoDia,
        LocalTime horaPartida,
        String observacao
) {

    public static HorarioResponse from(Horario horario, Linha linha) {
        String origem = horario.sentido() == Sentido.IDA
                ? "Águas Lindas de Goiás"
                : linha.terminalDestino();
        String destino = horario.sentido() == Sentido.IDA
                ? linha.terminalDestino()
                : "Águas Lindas de Goiás";

        return new HorarioResponse(
                horario.id(),
                horario.sentido(),
                origem + " → " + destino,
                origem,
                destino,
                horario.tipoDia(),
                horario.horaPartida(),
                horario.observacao()
        );
    }
}
