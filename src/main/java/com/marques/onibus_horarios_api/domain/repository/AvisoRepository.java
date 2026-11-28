package com.marques.onibus_horarios_api.domain.repository;

import com.marques.onibus_horarios_api.domain.model.Aviso;
import com.marques.onibus_horarios_api.domain.model.Pagina;
import com.marques.onibus_horarios_api.domain.model.StatusAviso;

import java.util.Optional;
import java.util.UUID;

public interface AvisoRepository {

    Aviso save(Aviso aviso);

    Optional<Aviso> findById(UUID id);

    Pagina<Aviso> buscar(StatusAviso status, int pagina, int tamanho);
}
