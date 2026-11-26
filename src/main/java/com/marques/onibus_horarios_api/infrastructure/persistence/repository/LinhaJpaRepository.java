package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinhaJpaRepository extends JpaRepository<LinhaEntity, UUID> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);

    Optional<LinhaEntity> findByIdAndActiveTrue(UUID id);

    List<LinhaEntity> findAllByActiveTrue();
}
