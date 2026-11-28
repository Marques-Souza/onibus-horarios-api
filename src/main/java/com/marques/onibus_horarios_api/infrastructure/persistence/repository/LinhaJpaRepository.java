package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

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

    Optional<LinhaEntity> findByCodigo(String codigo);

    @Query("""
        SELECT l FROM LinhaEntity l
        WHERE l.active = true
          AND (:bairroOrigem = '' OR CAST(FUNCTION('unaccent', LOWER(l.bairroOrigem)) AS string)
              LIKE CAST(FUNCTION('unaccent', LOWER(CONCAT('%', :bairroOrigem, '%'))) AS string))
          AND (:terminalDestino = '' OR CAST(FUNCTION('unaccent', LOWER(l.terminalDestino)) AS string)
              LIKE CAST(FUNCTION('unaccent', LOWER(CONCAT('%', :terminalDestino, '%'))) AS string))
        ORDER BY l.codigo
        """)
    Page<LinhaEntity> buscarAtivas(
            @Param("bairroOrigem") String bairroOrigem,
            @Param("terminalDestino") String terminalDestino,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT l.bairroOrigem FROM LinhaEntity l
        WHERE l.active = true
          AND (:termo = '' OR CAST(FUNCTION('unaccent', LOWER(l.bairroOrigem)) AS string)
              LIKE CAST(FUNCTION('unaccent', LOWER(CONCAT('%', :termo, '%'))) AS string))
        ORDER BY l.bairroOrigem
        """)
    List<String> buscarBairrosAtivos(@Param("termo") String termo);

    @Query("""
        SELECT DISTINCT l.terminalDestino FROM LinhaEntity l
        WHERE l.active = true
          AND (:termo = '' OR CAST(FUNCTION('unaccent', LOWER(l.terminalDestino)) AS string)
              LIKE CAST(FUNCTION('unaccent', LOWER(CONCAT('%', :termo, '%'))) AS string))
        ORDER BY l.terminalDestino
        """)
    List<String> buscarTerminaisAtivos(@Param("termo") String termo);
}
