package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.auth.LoginRequest;
import com.marques.onibus_horarios_api.application.dto.auth.TokenResponse;
import com.marques.onibus_horarios_api.application.exception.CredenciaisInvalidasException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AdminAuthenticationService {

    private static final long TOKEN_LIFETIME_SECONDS = 3600;

    private final String adminUsername;
    private final String encodedAdminPassword;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AdminAuthenticationService(
            @Value("${app.security.admin.username}") String adminUsername,
            @Value("${app.security.admin.password}") String adminPassword,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    ) {
        this.adminUsername = adminUsername;
        this.passwordEncoder = passwordEncoder;
        this.encodedAdminPassword = passwordEncoder.encode(adminPassword);
        this.jwtEncoder = jwtEncoder;
    }

    public TokenResponse autenticar(LoginRequest request) {
        if (!adminUsername.equals(request.username())
                || !passwordMatches(request.password())) {
            throw new CredenciaisInvalidasException();
        }

        Instant agora = Instant.now();
        Instant expiracao = agora.plus(TOKEN_LIFETIME_SECONDS, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("onibus-horarios-api")
                .subject(adminUsername)
                .issuedAt(agora)
                .expiresAt(expiracao)
                .claim("roles", List.of("ADMIN"))
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
        )).getTokenValue();

        return new TokenResponse(token, "Bearer", TOKEN_LIFETIME_SECONDS);
    }

    private boolean passwordMatches(String password) {
        return password != null && passwordEncoder.matches(password, encodedAdminPassword);
    }
}
