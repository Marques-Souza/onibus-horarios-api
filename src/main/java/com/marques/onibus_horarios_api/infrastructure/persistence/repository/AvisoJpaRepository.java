package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.StatusAviso;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.AvisoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AvisoJpaRepository extends JpaRepository<AvisoEntity, UUID> {

    @Query("""
        SELECT a FROM AvisoEntity a
        LEFT JOIN FETCH a.linha
        WHERE a.id = :id
        """)
    Optional<AvisoEntity> buscarPorId(@Param("id") UUID id);

    @Query(
            value = """
                    SELECT a FROM AvisoEntity a
                    LEFT JOIN FETCH a.linha
                    WHERE (:status IS NULL OR a.status = :status)
                    """,
            countQuery = """
                    SELECT COUNT(a) FROM AvisoEntity a
                    WHERE (:status IS NULL OR a.status = :status)
                    """
    )
    Page<AvisoEntity> buscar(@Param("status") StatusAviso status, Pageable pageable);
}
