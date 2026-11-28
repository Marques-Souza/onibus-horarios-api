package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.AvisoRequest;
import com.marques.onibus_horarios_api.application.dto.AvisoResponse;
import com.marques.onibus_horarios_api.application.dto.AtualizarStatusAvisoRequest;
import com.marques.onibus_horarios_api.domain.exception.AvisoNaoEncontradoException;
import com.marques.onibus_horarios_api.domain.exception.LinhaNaoEncontradaException;
import com.marques.onibus_horarios_api.domain.model.Aviso;
import com.marques.onibus_horarios_api.domain.model.Pagina;
import com.marques.onibus_horarios_api.domain.model.StatusAviso;
import com.marques.onibus_horarios_api.domain.repository.AvisoRepository;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final LinhaRepository linhaRepository;

    public AvisoService(AvisoRepository avisoRepository, LinhaRepository linhaRepository) {
        this.avisoRepository = avisoRepository;
        this.linhaRepository = linhaRepository;
    }

    @Transactional
    public AvisoResponse criar(AvisoRequest request) {
        if (request.linhaId() != null
                && linhaRepository.findByIdAndActiveTrue(request.linhaId()).isEmpty()) {
            throw new LinhaNaoEncontradaException();
        }

        Aviso aviso = new Aviso(
                null,
                request.linhaId(),
                request.tipo(),
                request.mensagem().trim(),
                request.horarioReferencia(),
                Instant.now(),
                StatusAviso.NOVO
        );
        return AvisoResponse.from(avisoRepository.save(aviso));
    }

    @Transactional(readOnly = true)
    public Pagina<AvisoResponse> listar(StatusAviso status, int pagina, int tamanho) {
        Pagina<Aviso> avisos = avisoRepository.buscar(status, pagina, tamanho);
        return new Pagina<>(
                avisos.conteudo().stream().map(AvisoResponse::from).toList(),
                avisos.numero(),
                avisos.tamanho(),
                avisos.totalElementos(),
                avisos.totalPaginas()
        );
    }

    @Transactional
    public AvisoResponse atualizarStatus(UUID id, AtualizarStatusAvisoRequest request) {
        Aviso aviso = avisoRepository.findById(id).orElseThrow(AvisoNaoEncontradoException::new);
        Aviso atualizado = new Aviso(
                aviso.id(),
                aviso.linhaId(),
                aviso.tipo(),
                aviso.mensagem(),
                aviso.horarioReferencia(),
                aviso.criadoEm(),
                request.status()
        );
        return AvisoResponse.from(avisoRepository.save(atualizado));
    }
}
