package com.marques.onibus_horarios_api.application.mapper.importacao;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.application.exception.ErroDeImportacaoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                "0",
                6
        );

        var linha = mapper.toDomain(registro, null);

        assertEquals("Não informado", linha.bairroOrigem());
        assertEquals("BRASILIA", linha.terminalDestino());
    }

    @Test
    void deveInformarLinhaCsvQuandoLocalidadeForInvalida() {
        AnttHorarioCsv registro = new AnttHorarioCsv(
                "Operadora",
                "2001",
                "LOCALIDADE INVALIDA",
                "2001",
                "Linha teste",
                "SENTIDO GO-DF",
                "04:15",
                "1",
                "0",
                "0",
                12
        );

        var exception = assertThrows(
                ErroDeImportacaoException.class,
                () -> mapper.toDomain(registro, null)
        );

        assertEquals(
                "Localidade de origem e destino inválida na linha 12 do CSV: LOCALIDADE INVALIDA",
                exception.getMessage()
        );
    }
}
