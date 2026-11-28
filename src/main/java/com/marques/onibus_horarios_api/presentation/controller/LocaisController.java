package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.LocaisResponse;
import com.marques.onibus_horarios_api.application.usecase.LocalidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/locais")
@Tag(name = "Locais", description = "Listagem de bairros e terminais para autocompletar")
public class LocaisController {

    private final LocalidadeService localidadeService;

    public LocaisController(LocalidadeService localidadeService) {
        this.localidadeService = localidadeService;
    }

    @GetMapping
    @Operation(summary = "Listar locais", description = "Retorna bairros de origem e terminais de destino distintos")
    public ResponseEntity<LocaisResponse> listar(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(localidadeService.listar(q));
    }
}
