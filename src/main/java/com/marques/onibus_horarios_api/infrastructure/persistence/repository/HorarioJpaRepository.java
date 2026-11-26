package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.infrastructure.persistence.entity.HorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.UUID;

public interface HorarioJpaRepository extends JpaRepository<HorarioEntity, UUID> {

    boolean existsByLinha_IdAndSentidoAndTipoDiaAndHoraPartida(
            UUID linhaId,
            com.marques.onibus_horarios_api.domain.model.Sentido sentido,
            com.marques.onibus_horarios_api.domain.model.TipoDia tipoDia,
            LocalTime horaPartida
    );
}
