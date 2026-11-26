package com.marques.onibus_horarios_api.repository;

import com.marques.onibus_horarios_api.entity.Linha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LinhaRepository extends JpaRepository<Linha, UUID> {

    boolean existsByCodigo(String codigo);
}
