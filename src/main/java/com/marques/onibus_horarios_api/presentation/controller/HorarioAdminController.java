package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.HorarioRequest;
import com.marques.onibus_horarios_api.application.dto.HorarioResponse;
import com.marques.onibus_horarios_api.application.usecase.HorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Horários (Admin)", description = "CRUD de horários (requer ROLE_ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class HorarioAdminController {

    private final HorarioService horarioService;

    public HorarioAdminController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping("/linhas/{linhaId}/horarios")
    @Operation(summary = "Criar horário", description = "Cria um novo horário para uma linha")
    public ResponseEntity<HorarioResponse> criar(
            @PathVariable UUID linhaId,
            @Valid @RequestBody HorarioRequest request
    ) {
        HorarioResponse response = horarioService.criarHorario(linhaId, request);
        return ResponseEntity.created(URI.create("/api/v1/horarios/" + response.id())).body(response);
    }

    @PutMapping("/horarios/{id}")
    @Operation(summary = "Atualizar horário", description = "Atualiza dados de um horário")
    public ResponseEntity<HorarioResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody HorarioRequest request
    ) {
        return ResponseEntity.ok(horarioService.atualizarHorario(id, request));
    }

    @DeleteMapping("/horarios/{id}")
    @Operation(summary = "Desativar horário", description = "Desativa um horário (soft delete)")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        horarioService.desativarHorario(id);
        return ResponseEntity.noContent().build();
    }
}
