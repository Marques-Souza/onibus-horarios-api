package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.model.Pagina;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import com.marques.onibus_horarios_api.infrastructure.persistence.mapper.LinhaPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LinhaRepositoryAdapter implements LinhaRepository {

    private final LinhaJpaRepository linhaJpaRepository;
    private final LinhaPersistenceMapper linhaPersistenceMapper;

    public LinhaRepositoryAdapter(
            LinhaJpaRepository linhaJpaRepository,
            LinhaPersistenceMapper linhaPersistenceMapper
    ) {
        this.linhaJpaRepository = linhaJpaRepository;
        this.linhaPersistenceMapper = linhaPersistenceMapper;
    }

    @Override
    public Linha save(Linha linha) {
        var entity = linhaPersistenceMapper.toEntity(linha);
        return linhaPersistenceMapper.toDomain(linhaJpaRepository.save(entity));
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return linhaJpaRepository.existsByCodigo(codigo);
    }

    @Override
    public boolean existsByCodigoAndIdNot(String codigo, UUID id) {
        return linhaJpaRepository.existsByCodigoAndIdNot(codigo, id);
    }

    @Override
    public Optional<Linha> findByIdAndActiveTrue(UUID id) {
        return linhaJpaRepository.findByIdAndActiveTrue(id)
                .map(linhaPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Linha> findByCodigo(String codigo) {
        return linhaJpaRepository.findByCodigo(codigo)
                .map(linhaPersistenceMapper::toDomain);
    }

    @Override
    public Pagina<Linha> buscarAtivas(
            String bairroOrigem,
            String terminalDestino,
            int pagina,
            int tamanho
    ){
        Page<LinhaEntity> resultado = linhaJpaRepository.buscarAtivas(
                    normalizarFiltro(bairroOrigem),
                    normalizarFiltro(terminalDestino),
                PageRequest.of(pagina, tamanho)
        );

        List<Linha> linhas = resultado.getContent()
                .stream()
                .map(linhaPersistenceMapper::toDomain)
                .toList();

        return new Pagina<>(
                linhas,
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    @Override
    public List<String> buscarBairrosAtivos(String termo) {
        return linhaJpaRepository.buscarBairrosAtivos(normalizarFiltro(termo));
    }

    @Override
    public List<String> buscarTerminaisAtivos(String termo) {
        return linhaJpaRepository.buscarTerminaisAtivos(normalizarFiltro(termo));
    }

    private String normalizarFiltro(String filtro) {
        return filtro == null ? "" : filtro.trim();
    }
}
