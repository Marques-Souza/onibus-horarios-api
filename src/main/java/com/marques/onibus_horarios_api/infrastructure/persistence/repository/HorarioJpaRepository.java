package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.infrastructure.persistence.entity.HorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface HorarioJpaRepository extends JpaRepository<HorarioEntity, UUID>,
        JpaSpecificationExecutor<HorarioEntity> {

    boolean existsByLinha_IdAndSentidoAndTipoDiaAndHoraPartida(
            UUID linhaId,
            com.marques.onibus_horarios_api.domain.model.Sentido sentido,
            com.marques.onibus_horarios_api.domain.model.TipoDia tipoDia,
            LocalTime horaPartida
    );

    List<HorarioEntity> findAllByLinha_IdAndActiveTrueOrderByHoraPartidaAsc(UUID linhaId);

    List<HorarioEntity> findAllByLinha_IdAndSentidoAndActiveTrueOrderByHoraPartidaAsc(
            UUID linhaId,
            com.marques.onibus_horarios_api.domain.model.Sentido sentido
    );

    List<HorarioEntity> findAllByLinha_IdAndTipoDiaAndActiveTrueOrderByHoraPartidaAsc(
            UUID linhaId,
            com.marques.onibus_horarios_api.domain.model.TipoDia tipoDia
    );

    List<HorarioEntity> findAllByLinha_IdAndSentidoAndTipoDiaAndActiveTrueOrderByHoraPartidaAsc(
            UUID linhaId,
            com.marques.onibus_horarios_api.domain.model.Sentido sentido,
            com.marques.onibus_horarios_api.domain.model.TipoDia tipoDia
    );
}
