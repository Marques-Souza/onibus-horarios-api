package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.mapper.LinhaPersistenceMapper;
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
    public List<Linha> findAllByActiveTrue() {
        return linhaJpaRepository.findAllByActiveTrue()
                .stream()
                .map(linhaPersistenceMapper::toDomain)
                .toList();
    }
}
