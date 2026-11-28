package com.marques.onibus_horarios_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Importacao(
        UUID id,
        String nomeArquivo,
        Instant importadoEm,
        int linhasCriadas,
        int horariosCriados,
        int horariosIgnorados
) {
}
