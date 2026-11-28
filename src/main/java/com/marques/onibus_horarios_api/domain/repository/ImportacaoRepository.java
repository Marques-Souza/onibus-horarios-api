package com.marques.onibus_horarios_api.domain.repository;

import com.marques.onibus_horarios_api.domain.model.Importacao;

import java.util.Optional;

public interface ImportacaoRepository {

    Importacao save(Importacao importacao);

    Optional<Importacao> buscarUltima();
}
