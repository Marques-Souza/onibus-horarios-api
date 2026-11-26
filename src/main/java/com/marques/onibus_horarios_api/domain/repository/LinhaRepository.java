package com.marques.onibus_horarios_api.domain.repository;

import com.marques.onibus_horarios_api.domain.model.Linha;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinhaRepository {

    Linha save(Linha linha);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);

    Optional<Linha> findByIdAndActiveTrue(UUID id);

    List<Linha> findAllByActiveTrue();
}
