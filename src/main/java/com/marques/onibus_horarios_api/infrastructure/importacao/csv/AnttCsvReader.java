package com.marques.onibus_horarios_api.infrastructure.importacao.csv;


import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.application.port.importacao.LeitorAnttCsv;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnttCsvReader implements LeitorAnttCsv {

    private static final int LINHAS_INFORMATIVAS = 4;
    private static final int QUANTIDADE_COLUNAS = 11;

    @Override
    public List<AnttHorarioCsv> ler(InputStream arquivo) throws IOException {
        try (var reader = new BufferedReader(
                new InputStreamReader(arquivo, StandardCharsets.UTF_8)
        )) {
            ignorarLinhasInformativas(reader);

            var formato = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .get();

            try (var parser = formato.parse(reader)) {
                var registros = new ArrayList<AnttHorarioCsv>();

                for (var registro : parser) {
                    if (registro.size() != QUANTIDADE_COLUNAS) {
                        throw new IOException(
                                "Registro CSV " + registro.getRecordNumber()
                                        + " possui " + registro.size()
                                        + " colunas; eram esperadas "
                                        + QUANTIDADE_COLUNAS + "."
                        );
                    }

                    registros.add(new AnttHorarioCsv(
                            registro.get(0),
                            registro.get(1),
                            registro.get(2),
                            registro.get(3),
                            registro.get(4),
                            registro.get(5),
                            registro.get(6),
                            registro.get(7),
                            registro.get(8),
                            registro.get(9)
                    ));
                }

                return List.copyOf(registros);
            }
        }
    }

    private void ignorarLinhasInformativas(BufferedReader reader) throws IOException {
        for (int linha = 0; linha < LINHAS_INFORMATIVAS; linha++) {
            if (reader.readLine() == null) {
                throw new IOException("O CSV terminou antes do cabeçalho.");
            }
        }
    }
}
