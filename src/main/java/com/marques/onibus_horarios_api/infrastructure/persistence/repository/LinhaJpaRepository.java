package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinhaJpaRepository extends JpaRepository<LinhaEntity, UUID> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, UUID id);

    Optional<LinhaEntity> findByIdAndActiveTrue(UUID id);

    List<LinhaEntity> findAllByActiveTrue();

    Optional<LinhaEntity> findByCodigo(String codigo);

    @Query("""
        SELECT l FROM LinhaEntity l
        WHERE l.active = true
          AND (:bairroOrigem IS NULL OR LOWER(l.bairroOrigem) LIKE LOWER(CONCAT('%', :bairroOrigem, '%')))
          AND (:terminalDestino IS NULL OR LOWER(l.terminalDestino) LIKE LOWER(CONCAT('%', :terminalDestino, '%')))
        ORDER BY l.codigo
        """)
    Page<LinhaEntity> buscarAtivas(
            @Param("bairroOrigem") String bairroOrigem,
            @Param("terminalDestino") String terminalDestino,
            Pageable pageable
    );
}
