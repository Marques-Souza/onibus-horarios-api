package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.HorarioResponse;
import com.marques.onibus_horarios_api.application.dto.HorariosLinhaResponse;
import com.marques.onibus_horarios_api.application.usecase.HorarioService;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/linhas/{linhaId}/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<HorariosLinhaResponse> listar(
            @PathVariable UUID linhaId,
            @RequestParam(required = false) Sentido sentido,
            @RequestParam(required = false) TipoDia tipoDia
    ) {
        return ResponseEntity.ok(horarioService.listarAtivosPorLinha(linhaId, sentido, tipoDia));
    }
}
