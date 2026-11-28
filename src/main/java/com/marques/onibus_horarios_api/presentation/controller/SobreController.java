package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.SobreDadosResponse;
import com.marques.onibus_horarios_api.application.usecase.SobreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sobre")
public class SobreController {

    private final SobreService sobreService;

    public SobreController(SobreService sobreService) {
        this.sobreService = sobreService;
    }

    @GetMapping("/dados")
    public ResponseEntity<SobreDadosResponse> consultarDados() {
        return ResponseEntity.ok(sobreService.consultarDados());
    }
}
