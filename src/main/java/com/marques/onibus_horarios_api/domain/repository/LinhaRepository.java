package com.marques.onibus_horarios_api.domain.repository;

import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.model.Pagina;

import java.util.Optional;
import java.util.UUID;

public interface LinhaRepository {

    Linha save(Linha linha);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);

    Optional<Linha> findByIdAndActiveTrue(UUID id);

    Optional<Linha> findByCodigo(String codigo);

    Pagina<Linha> buscarAtivas(
            String bairroOrigem,
            String terminalDestino,
            int pagina,
            int tamanho
    );
}
