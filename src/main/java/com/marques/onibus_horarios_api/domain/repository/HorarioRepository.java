package com.marques.onibus_horarios_api.domain.repository;

import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;

import java.time.LocalTime;
import java.util.UUID;

public interface HorarioRepository {

    Horario save(Horario horario);

    boolean existsByLinhaIdAndSentidoAndTipoDiaAndHoraPartida(
            UUID linhaId,
            Sentido sentido,
            TipoDia tipoDia,
            LocalTime horaPartida
    );
}
