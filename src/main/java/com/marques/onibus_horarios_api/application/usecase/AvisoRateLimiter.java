package com.marques.onibus_horarios_api.application.usecase;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AvisoRateLimiter {

    private static final int LIMITE = 5;
    private static final Duration JANELA = Duration.ofMinutes(10);

    private final ConcurrentMap<String, Tentativas> tentativasPorIp = new ConcurrentHashMap<>();
    private final AtomicLong requisicoes = new AtomicLong();

    public void validarLimite(String ip) {
        Instant agora = Instant.now();
        Instant inicioJanela = agora.minus(JANELA);
        AtomicBoolean limiteAtingido = new AtomicBoolean();
        tentativasPorIp.compute(ip, (chave, existentes) -> {
            List<Instant> recentes = existentes == null
                    ? new ArrayList<>()
                    : existentes.instantes().stream()
                            .filter(instante -> instante.isAfter(inicioJanela))
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            if (recentes.size() >= LIMITE) {
                limiteAtingido.set(true);
                return new Tentativas(List.copyOf(recentes));
            }
            recentes.add(agora);
            return new Tentativas(List.copyOf(recentes));
        });

        if (requisicoes.incrementAndGet() % 256 == 0) {
            limparTentativasExpiradas(inicioJanela);
        }
        if (limiteAtingido.get()) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Limite de avisos atingido. Tente novamente mais tarde."
            );
        }
    }

    private void limparTentativasExpiradas(Instant inicioJanela) {
        tentativasPorIp.forEach((ip, tentativas) -> {
            if (!tentativas.instantes().isEmpty()
                    && !tentativas.instantes().getLast().isAfter(inicioJanela)) {
                tentativasPorIp.remove(ip, tentativas);
            }
        });
    }

    private record Tentativas(List<Instant> instantes) {
    }
}
