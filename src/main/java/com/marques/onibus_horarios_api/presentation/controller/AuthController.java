package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.auth.LoginRequest;
import com.marques.onibus_horarios_api.application.dto.auth.TokenResponse;
import com.marques.onibus_horarios_api.application.usecase.AdminAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Autenticação de administradores")
public class AuthController {

    private final AdminAuthenticationService adminAuthenticationService;

    public AuthController(AdminAuthenticationService adminAuthenticationService) {
        this.adminAuthenticationService = adminAuthenticationService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login admin", description = "Autentica um administrador e retorna token JWT")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(adminAuthenticationService.autenticar(request));
    }
}
