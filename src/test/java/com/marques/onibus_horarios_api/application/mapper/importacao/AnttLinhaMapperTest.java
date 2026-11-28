package com.marques.onibus_horarios_api.application.mapper.importacao;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnttLinhaMapperTest {

    private final AnttLinhaMapper mapper = new AnttLinhaMapper();

    @Test
    void naoDeveTratarSiglaDoEstadoComoBairro() {
        AnttHorarioCsv registro = new AnttHorarioCsv(
                "Operadora",
                "2001",
                "AGUAS LINDAS DE GOIAS/GO-BRASILIA/DF",
                "2001",
                "BRASILIA(DF) - AGUAS LINDAS DE GOIAS(GO)",
                "SENTIDO GO-DF",
                "04:15",
                "1",
                "0",
                "0"
        );

        var linha = mapper.toDomain(registro, null);

        assertEquals("Não informado", linha.bairroOrigem());
        assertEquals("BRASILIA", linha.terminalDestino());
    }
}
