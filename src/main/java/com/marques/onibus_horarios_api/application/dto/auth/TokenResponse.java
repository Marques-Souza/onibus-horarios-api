package com.marques.onibus_horarios_api.application.dto.auth;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
