package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.infrastructure.persistence.entity.ImportacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImportacaoJpaRepository extends JpaRepository<ImportacaoEntity, UUID> {

    Optional<ImportacaoEntity> findFirstByOrderByImportadoEmDesc();
}
