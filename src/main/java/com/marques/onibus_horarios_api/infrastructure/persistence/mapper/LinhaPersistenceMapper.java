package com.marques.onibus_horarios_api.infrastructure.persistence.mapper;

import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LinhaPersistenceMapper {

    LinhaEntity toEntity(Linha linha);

    @Mapping(target = "atualizarCom", ignore = true)
    Linha toDomain(LinhaEntity linhaEntity);
}
