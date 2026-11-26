package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.LinhaRequest;
import com.marques.onibus_horarios_api.application.dto.LinhaResponse;
import com.marques.onibus_horarios_api.application.usecase.LinhaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
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
    public ResponseEntity<List<LinhaResponse>> listar() {
        return ResponseEntity.ok(linhaService.listarAtivas());
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
