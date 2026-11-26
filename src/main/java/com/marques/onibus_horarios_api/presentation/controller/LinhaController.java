package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.LinhaRequest;
import com.marques.onibus_horarios_api.application.dto.LinhaResponse;
import com.marques.onibus_horarios_api.application.usecase.LinhaService;
import com.marques.onibus_horarios_api.domain.model.Pagina;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/linhas")
public class LinhaController {

    private final LinhaService linhaService;

    public LinhaController(LinhaService linhaService) {
        this.linhaService = linhaService;
    }

    @PostMapping
    public ResponseEntity<LinhaResponse> criar(@Valid @RequestBody LinhaRequest request) {
        LinhaResponse response = linhaService.criar(request);
        URI location = URI.create("/api/v1/linhas/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Pagina<LinhaResponse>> listar(
            @RequestParam(required = false) String bairroOrigem,
            @RequestParam(required = false) String terminalDestino,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanho
    ) {
        return ResponseEntity.ok(
                linhaService.listarAtivas(bairroOrigem, terminalDestino, pagina, tamanho)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LinhaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(linhaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LinhaResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody LinhaRequest request
    ) {
        return ResponseEntity.ok(linhaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        linhaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
