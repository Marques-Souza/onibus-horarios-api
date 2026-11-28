package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.SobreDadosResponse;
import com.marques.onibus_horarios_api.domain.repository.ImportacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SobreService {

    private final ImportacaoRepository importacaoRepository;

    public SobreService(ImportacaoRepository importacaoRepository) {
        this.importacaoRepository = importacaoRepository;
    }

    @Transactional(readOnly = true)
    public SobreDadosResponse consultarDados() {
        return new SobreDadosResponse(
                "ANTT",
                importacaoRepository.buscarUltima()
                        .map(importacao -> importacao.importadoEm())
                        .orElse(null)
        );
    }
}
