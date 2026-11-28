package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.ChaveHorario;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.HorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HorarioJpaRepository extends JpaRepository<HorarioEntity, UUID>,
        JpaSpecificationExecutor<HorarioEntity> {

    Optional<HorarioEntity> findByIdAndActiveTrue(UUID id);

    @Query("""
        SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
        FROM HorarioEntity h
        WHERE h.linha.id = :linhaId
          AND h.sentido = :sentido
          AND h.tipoDia = :tipoDia
          AND h.horaPartida = :horaPartida
          AND (:ignoredId IS NULL OR h.id <> :ignoredId)
        """)
    boolean existsDuplicate(
            @Param("linhaId") UUID linhaId,
            @Param("sentido") Sentido sentido,
            @Param("tipoDia") TipoDia tipoDia,
            @Param("horaPartida") LocalTime horaPartida,
            @Param("ignoredId") UUID ignoredId
    );

    @Query("""
        SELECT new com.marques.onibus_horarios_api.domain.model.ChaveHorario(
            h.linha.id, h.sentido, h.tipoDia, h.horaPartida
        )
        FROM HorarioEntity h
        WHERE h.linha.id IN :linhaIds
        """)
    List<ChaveHorario> buscarChavesExistentesPorLinhaIds(@Param("linhaIds") Collection<UUID> linhaIds);

    List<HorarioEntity> findAllByLinha_IdAndActiveTrueOrderByHoraPartidaAsc(UUID linhaId);

    List<HorarioEntity> findAllByLinha_IdAndSentidoAndActiveTrueOrderByHoraPartidaAsc(
            UUID linhaId,
            Sentido sentido
    );

    List<HorarioEntity> findAllByLinha_IdAndTipoDiaAndActiveTrueOrderByHoraPartidaAsc(
            UUID linhaId,
            TipoDia tipoDia
    );

    List<HorarioEntity> findAllByLinha_IdAndSentidoAndTipoDiaAndActiveTrueOrderByHoraPartidaAsc(
            UUID linhaId,
            Sentido sentido,
            TipoDia tipoDia
    );
}
