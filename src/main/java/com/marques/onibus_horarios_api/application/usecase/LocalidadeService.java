package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.LocaisResponse;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalidadeService {

    private final LinhaRepository linhaRepository;

    public LocalidadeService(LinhaRepository linhaRepository) {
        this.linhaRepository = linhaRepository;
    }

    @Transactional(readOnly = true)
    public LocaisResponse listar(String termo) {
        return new LocaisResponse(
                linhaRepository.buscarBairrosAtivos(termo),
                linhaRepository.buscarTerminaisAtivos(termo)
        );
    }
}
