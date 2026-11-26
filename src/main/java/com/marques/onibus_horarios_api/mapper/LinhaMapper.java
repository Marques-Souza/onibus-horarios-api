package com.marques.onibus_horarios_api.mapper;


import com.marques.onibus_horarios_api.dto.LinhaRequest;
import com.marques.onibus_horarios_api.dto.LinhaResponse;
import com.marques.onibus_horarios_api.entity.Linha;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LinhaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Linha toEntity(LinhaRequest dto);

    LinhaResponse toResponse(Linha linha);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(LinhaRequest dto, @MappingTarget Linha linha);
}
