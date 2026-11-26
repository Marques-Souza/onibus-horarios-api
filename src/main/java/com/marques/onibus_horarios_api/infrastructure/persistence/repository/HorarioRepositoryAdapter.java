package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import com.marques.onibus_horarios_api.domain.repository.HorarioRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.mapper.HorarioPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.UUID;

@Repository
public class HorarioRepositoryAdapter implements HorarioRepository {


    private final HorarioJpaRepository horarioJpaRepository;
    private final HorarioPersistenceMapper horarioPersistenceMapper;

    public HorarioRepositoryAdapter(
            HorarioJpaRepository horarioJpaRepository,
            HorarioPersistenceMapper horarioPersistenceMapper
    ) {
        this.horarioJpaRepository = horarioJpaRepository;
        this.horarioPersistenceMapper = horarioPersistenceMapper;
    }

    @Override
    public Horario save(Horario horario) {
        var entity = horarioPersistenceMapper.toEntity(horario);
        var salvo = horarioJpaRepository.save(entity);
        return horarioPersistenceMapper.toDomain(salvo);
    }

    @Override
    public boolean existsByLinhaIdAndSentidoAndTipoDiaAndHoraPartida(
            UUID linhaId,
            Sentido sentido,
            TipoDia tipoDia,
            LocalTime horaPartida
    ) {
        return horarioJpaRepository.existsByLinha_IdAndSentidoAndTipoDiaAndHoraPartida(
                linhaId,
                sentido,
                tipoDia,
                horaPartida
        );
    }
}
