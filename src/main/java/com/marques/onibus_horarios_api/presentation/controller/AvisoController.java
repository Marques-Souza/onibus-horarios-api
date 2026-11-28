package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.AvisoCriadoResponse;
import com.marques.onibus_horarios_api.application.dto.AvisoRequest;
import com.marques.onibus_horarios_api.application.dto.AvisoResponse;
import com.marques.onibus_horarios_api.application.dto.AtualizarStatusAvisoRequest;
import com.marques.onibus_horarios_api.application.usecase.AvisoRateLimiter;
import com.marques.onibus_horarios_api.application.usecase.AvisoService;
import com.marques.onibus_horarios_api.domain.model.Pagina;
import com.marques.onibus_horarios_api.domain.model.StatusAviso;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/avisos")
@Tag(name = "Avisos", description = "Relatos de passageiros sobre problemas")
public class AvisoController {

    private final AvisoService avisoService;
    private final AvisoRateLimiter rateLimiter;
    private final Validator validator;

    public AvisoController(
            AvisoService avisoService,
            AvisoRateLimiter rateLimiter,
            Validator validator
    ) {
        this.avisoService = avisoService;
        this.rateLimiter = rateLimiter;
        this.validator = validator;
    }

    @PostMapping
    @Operation(summary = "Criar aviso", description = "Cria um novo aviso (público, com rate limiting)")
    public ResponseEntity<AvisoCriadoResponse> criar(
            @RequestBody AvisoRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest
    ) {
        AvisoResponse aviso = null;
        if (request.website() == null || request.website().isBlank()) {
            Set<ConstraintViolation<AvisoRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            rateLimiter.validarLimite(httpRequest.getRemoteAddr());
            aviso = avisoService.criar(request);
        }
        URI location = aviso == null
                ? URI.create("/api/v1/avisos")
                : URI.create("/api/v1/avisos/" + aviso.id());
        return ResponseEntity.created(location)
                .body(new AvisoCriadoResponse(aviso == null ? null : aviso.id(), true));
    }

    @GetMapping
    @Operation(summary = "Listar avisos", description = "Lista avisos paginados (requer ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Pagina<AvisoResponse>> listar(
            @RequestParam(required = false) StatusAviso status,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamanho
    ) {
        return ResponseEntity.ok(avisoService.listar(status, pagina, tamanho));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do aviso", description = "Atualiza o status de um aviso (requer ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AvisoResponse> atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarStatusAvisoRequest request
    ) {
        return ResponseEntity.ok(avisoService.atualizarStatus(id, request));
    }
}
