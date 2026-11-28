package com.marques.onibus_horarios_api.application.mapper.importacao;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.application.exception.ErroDeImportacaoException;
import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class AnttHorarioMapper {

    private static final DateTimeFormatter FORMATO_HORA = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);

    public List<Horario> toDomain(AnttHorarioCsv registro, UUID linhaId) {
        Sentido sentido = converterSentido(registro);
        LocalTime horaPartida = converterHorario(registro);
        List<Horario> horarios = new ArrayList<>();

        if (flagAtiva(registro, registro.diasUteis(), "DIAS ÚTEIS")) {
            horarios.add(criarHorario(linhaId, sentido, TipoDia.DIA_UTIL, horaPartida));
        }
        if (flagAtiva(registro, registro.sabado(), "SAB")) {
            horarios.add(criarHorario(linhaId, sentido, TipoDia.SABADO, horaPartida));
        }
        if (flagAtiva(registro, registro.domingo(), "DOM")) {
            horarios.add(criarHorario(linhaId, sentido, TipoDia.DOMINGO_FERIADO, horaPartida));
        }

        return List.copyOf(horarios);
    }

    private LocalTime converterHorario(AnttHorarioCsv registro) {
        try {
            return LocalTime.parse(registro.horario().trim(), FORMATO_HORA);
        } catch (DateTimeParseException exception) {
            throw new ErroDeImportacaoException(
                    "Horário inválido na linha " + registro.numeroLinhaCsv()
                            + " do CSV: '" + registro.horario()
                            + "'. Use o formato H:mm ou HH:mm.",
                    exception
            );
        }
    }

    private Sentido converterSentido(AnttHorarioCsv registro) {
        String valor = registro.sentido();
        return switch (valor.trim().toUpperCase(Locale.ROOT)) {
            case "SENTIDO GO-DF" -> Sentido.IDA;
            case "SENTIDO DF-GO" -> Sentido.VOLTA;
            default -> throw new ErroDeImportacaoException(
                    "Sentido inválido na linha " + registro.numeroLinhaCsv()
                            + " do CSV: " + valor
            );
        };
    }

    private boolean flagAtiva(AnttHorarioCsv registro, String valor, String coluna) {
        return switch (valor.trim()) {
            case "1" -> true;
            case "0" -> false;
            default -> throw new ErroDeImportacaoException(
                    "Flag inválida na coluna " + coluna + " na linha "
                            + registro.numeroLinhaCsv() + " do CSV: " + valor
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
