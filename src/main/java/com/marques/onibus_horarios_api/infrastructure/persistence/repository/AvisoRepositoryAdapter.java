package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.Aviso;
import com.marques.onibus_horarios_api.domain.model.Pagina;
import com.marques.onibus_horarios_api.domain.model.StatusAviso;
import com.marques.onibus_horarios_api.domain.repository.AvisoRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.AvisoEntity;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AvisoRepositoryAdapter implements AvisoRepository {

    private final AvisoJpaRepository avisoJpaRepository;
    private final LinhaJpaRepository linhaJpaRepository;

    public AvisoRepositoryAdapter(
            AvisoJpaRepository avisoJpaRepository,
            LinhaJpaRepository linhaJpaRepository
    ) {
        this.avisoJpaRepository = avisoJpaRepository;
        this.linhaJpaRepository = linhaJpaRepository;
    }

    @Override
    public Aviso save(Aviso aviso) {
        AvisoEntity entity = aviso.id() == null
                ? new AvisoEntity()
                : avisoJpaRepository.findById(aviso.id()).orElseGet(AvisoEntity::new);
        entity.setId(aviso.id());
        entity.setLinha(aviso.linhaId() == null
                ? null
                : linhaJpaRepository.getReferenceById(aviso.linhaId()));
        entity.setTipo(aviso.tipo());
        entity.setMensagem(aviso.mensagem());
        entity.setHorarioReferencia(aviso.horarioReferencia());
        entity.setCriadoEm(aviso.criadoEm());
        entity.setStatus(aviso.status());
        return toDomain(avisoJpaRepository.save(entity));
    }

    @Override
    public Optional<Aviso> findById(UUID id) {
        return avisoJpaRepository.buscarPorId(id).map(this::toDomain);
    }

    @Override
    public Pagina<Aviso> buscar(StatusAviso status, int pagina, int tamanho) {
        var resultado = avisoJpaRepository.buscar(
                status,
                PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "criadoEm"))
        );
        return new Pagina<>(
                resultado.getContent().stream().map(this::toDomain).toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private Aviso toDomain(AvisoEntity entity) {
        return new Aviso(
                entity.getId(),
                entity.getLinha() == null ? null : entity.getLinha().getId(),
                entity.getTipo(),
                entity.getMensagem(),
                entity.getHorarioReferencia(),
                entity.getCriadoEm(),
                entity.getStatus()
        );
    }
}
