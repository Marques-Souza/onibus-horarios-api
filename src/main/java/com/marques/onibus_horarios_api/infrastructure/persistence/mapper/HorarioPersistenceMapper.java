package com.marques.onibus_horarios_api.infrastructure.persistence.mapper;

import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.HorarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HorarioPersistenceMapper {

    @Mapping(target = "linha.id", source = "linhaId")
    HorarioEntity toEntity(Horario horario);

    @Mapping(target = "linhaId", source = "linha.id")
    Horario toDomain(HorarioEntity horarioEntity);

}
