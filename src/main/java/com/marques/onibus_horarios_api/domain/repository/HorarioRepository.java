package com.marques.onibus_horarios_api.domain.repository;

import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.HorarioDaLinha;
import com.marques.onibus_horarios_api.domain.model.ChaveHorario;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface HorarioRepository {

    Horario save(Horario horario);

    Optional<Horario> findAtivoById(UUID id);

    boolean existsDuplicate(
            UUID linhaId,
            Sentido sentido,
            TipoDia tipoDia,
            LocalTime horaPartida,
            UUID ignoredId
    );

    Set<ChaveHorario> buscarChavesExistentesPorLinhaIds(Collection<UUID> linhaIds);

    List<Horario> buscarAtivosPorLinha(UUID linhaId, Sentido sentido, TipoDia tipoDia);

    List<HorarioDaLinha> buscarAtivosPorTrajeto(
            String origem,
            String destino,
            String codigoLinha,
            String bairroOrigem,
            Sentido sentido,
            TipoDia tipoDia
    );
}
