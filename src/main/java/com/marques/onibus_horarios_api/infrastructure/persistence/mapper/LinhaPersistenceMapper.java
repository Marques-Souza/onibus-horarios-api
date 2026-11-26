package com.marques.onibus_horarios_api.infrastructure.persistence.mapper;

import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LinhaPersistenceMapper {

    LinhaEntity toEntity(Linha linha);

    Linha toDomain(LinhaEntity linhaEntity);
}
