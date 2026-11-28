package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.SobreDadosResponse;
import com.marques.onibus_horarios_api.application.usecase.SobreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sobre")
@Tag(name = "Sobre", description = "Informações sobre a API")
public class SobreController {

    private final SobreService sobreService;

    public SobreController(SobreService sobreService) {
        this.sobreService = sobreService;
    }

    @GetMapping("/dados")
    @Operation(summary = "Consultar dados da API", description = "Retorna fonte dos dados e data da última importação")
    public ResponseEntity<SobreDadosResponse> consultarDados() {
        return ResponseEntity.ok(sobreService.consultarDados());
    }
}
