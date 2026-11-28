package com.marques.onibus_horarios_api.application.mapper.importacao;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.application.exception.ErroDeImportacaoException;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnttHorarioMapperTest {

    private final AnttHorarioMapper mapper = new AnttHorarioMapper();

    @Test
    void deveAceitarHoraComUmOuDoisDigitos() {
        var horarioComUmDigito = mapper.toDomain(registro("7:30", 6), UUID.randomUUID());
        var horarioComDoisDigitos = mapper.toDomain(registro("07:30", 7), UUID.randomUUID());

        assertEquals(LocalTime.of(7, 30), horarioComUmDigito.getFirst().horaPartida());
        assertEquals(LocalTime.of(7, 30), horarioComDoisDigitos.getFirst().horaPartida());
        assertEquals(TipoDia.DIA_UTIL, horarioComUmDigito.getFirst().tipoDia());
    }

    @Test
    void deveInformarNumeroDaLinhaQuandoHorarioForInvalido() {
        var exception = assertThrows(
                ErroDeImportacaoException.class,
                () -> mapper.toDomain(registro("25:70", 18), UUID.randomUUID())
        );

        assertEquals(
                "Horário inválido na linha 18 do CSV: '25:70'. Use o formato H:mm ou HH:mm.",
                exception.getMessage()
        );
    }

    private AnttHorarioCsv registro(String horario, long numeroLinha) {
        return new AnttHorarioCsv(
                "Operadora",
                "2001",
                "AGUAS LINDAS DE GOIAS/GO-BRASILIA/DF",
                "2001",
                "Linha teste",
                "SENTIDO GO-DF",
                horario,
                "1",
                "0",
                "0",
                numeroLinha
        );
    }
}
