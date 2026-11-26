package com.marques.onibus_horarios_api.application.mapper.importacao;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class AnttHorarioMapper {

    public List<Horario> toDomain(AnttHorarioCsv registro, UUID linhaId) {
        Sentido sentido = converterSentido(registro.sentido());
        LocalTime horaPartida = LocalTime.parse(registro.horario().trim());
        List<Horario> horarios = new ArrayList<>();

        if (flagAtiva(registro.diasUteis(), "DIAS ÚTEIS")) {
            horarios.add(criarHorario(linhaId, sentido, TipoDia.DIA_UTIL, horaPartida));
        }
        if (flagAtiva(registro.sabado(), "SAB")) {
            horarios.add(criarHorario(linhaId, sentido, TipoDia.SABADO, horaPartida));
        }
        if (flagAtiva(registro.domingo(), "DOM")) {
            horarios.add(criarHorario(linhaId, sentido, TipoDia.DOMINGO_FERIADO, horaPartida));
        }

        return List.copyOf(horarios);
    }

    private Sentido converterSentido(String valor) {
        return switch (valor.trim().toUpperCase(Locale.ROOT)) {
            case "SENTIDO GO-DF" -> Sentido.IDA;
            case "SENTIDO DF-GO" -> Sentido.VOLTA;
            default -> throw new IllegalArgumentException(
                    "Sentido não reconhecido no CSV: " + valor
            );
        };
    }

    private boolean flagAtiva(String valor, String coluna) {
        return switch (valor.trim()) {
            case "1" -> true;
            case "0" -> false;
            default -> throw new IllegalArgumentException(
                    "Flag inválida na coluna " + coluna + ": " + valor
            );
        };
    }

    private Horario criarHorario(
            UUID linhaId,
            Sentido sentido,
            TipoDia tipoDia,
            LocalTime horaPartida
    ) {
        return new Horario(
                null,
                linhaId,
                sentido,
                tipoDia,
                horaPartida,
                null,
                true
        );
    }
}
