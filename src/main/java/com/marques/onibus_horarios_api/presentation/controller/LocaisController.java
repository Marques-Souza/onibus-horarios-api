package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.LocaisResponse;
import com.marques.onibus_horarios_api.application.usecase.LocalidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/locais")
public class LocaisController {

    private final LocalidadeService localidadeService;

    public LocaisController(LocalidadeService localidadeService) {
        this.localidadeService = localidadeService;
    }

    @GetMapping
    public ResponseEntity<LocaisResponse> listar(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(localidadeService.listar(q));
    }
}
