package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.LinhaRequest;
import com.marques.onibus_horarios_api.application.dto.LinhaResponse;
import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class LinhaService {

    private final LinhaRepository linhaRepository;

    public LinhaService(LinhaRepository linhaRepository) {
        this.linhaRepository = linhaRepository;
    }

    public LinhaResponse criar(LinhaRequest request) {
        validarCodigoDisponivel(request.codigo());

        Linha linha = new Linha(
                null,
                request.codigo(),
                request.nome(),
                request.bairroOrigem(),
                request.terminalDestino(),
                request.operadora(),
                request.tarifa(),
                true
        );

        return LinhaResponse.from(linhaRepository.save(linha));
    }

    @Transactional(readOnly = true)
    public LinhaResponse buscarPorId(UUID id) {
        return LinhaResponse.from(buscarLinhaAtiva(id));
    }

    @Transactional(readOnly = true)
    public List<LinhaResponse> listarAtivas() {
        return linhaRepository.findAllByActiveTrue()
                .stream()
                .map(LinhaResponse::from)
                .toList();
    }

    public LinhaResponse atualizar(UUID id, LinhaRequest request) {
        Linha linhaAtual = buscarLinhaAtiva(id);
        validarCodigoDisponivelParaAtualizacao(request.codigo(), id);

        Linha dadosAtualizados = new Linha(
                id,
                request.codigo(),
                request.nome(),
                request.bairroOrigem(),
                request.terminalDestino(),
                request.operadora(),
                request.tarifa(),
                true
        );

        return LinhaResponse.from(linhaRepository.save(linhaAtual.atualizarCom(dadosAtualizados)));
    }

    public void desativar(UUID id) {
        Linha linha = buscarLinhaAtiva(id);
        linhaRepository.save(linha.desativar());
    }

    private Linha buscarLinhaAtiva(UUID id) {
        return linhaRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NoSuchElementException("Linha não encontrada."));
    }

    private void validarCodigoDisponivel(String codigo) {
        if (linhaRepository.existsByCodigo(codigo)) {
            throw new IllegalStateException("Já existe uma linha cadastrada com o código: " + codigo);
        }
    }

    private void validarCodigoDisponivelParaAtualizacao(String codigo, UUID id) {
        if (linhaRepository.existsByCodigoAndIdNot(codigo, id)) {
            throw new IllegalStateException("Já existe uma linha cadastrada com o código: " + codigo);
        }
    }
}
