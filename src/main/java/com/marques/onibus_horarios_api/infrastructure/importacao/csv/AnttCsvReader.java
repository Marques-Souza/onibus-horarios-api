package com.marques.onibus_horarios_api.infrastructure.importacao.csv;


import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.application.port.importacao.LeitorAnttCsv;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnttCsvReader implements LeitorAnttCsv {

    private static final int LINHAS_INFORMATIVAS = 4;
    private static final int QUANTIDADE_COLUNAS = 11;

    @Override
    public List<AnttHorarioCsv> ler(InputStream arquivo) throws IOException {
        byte[] conteudo;
        try (arquivo) {
            conteudo = arquivo.readAllBytes();
        }
        try (var reader = new BufferedReader(new StringReader(decodificar(conteudo)))) {
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
                            registro.get(9),
                            LINHAS_INFORMATIVAS + parser.getCurrentLineNumber()
                    ));
                }

                return List.copyOf(registros);
            }
        }
    }

    private String decodificar(byte[] conteudo) throws IOException {
        try {
            String textoUtf8 = decodificar(conteudo, StandardCharsets.UTF_8);
            if (!textoUtf8.contains("\uFFFD")) {
                return textoUtf8;
            }
        } catch (CharacterCodingException ignored) {
            // Tenta os charsets legados usados em arquivos CSV.
        }

        try {
            return decodificar(conteudo, Charset.forName("windows-1252"));
        } catch (CharacterCodingException ignored) {
            return decodificar(conteudo, StandardCharsets.ISO_8859_1);
        }
    }

    private String decodificar(byte[] conteudo, Charset charset) throws CharacterCodingException {
        return charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(conteudo))
                .toString();
    }

    private void ignorarLinhasInformativas(BufferedReader reader) throws IOException {
        for (int linha = 0; linha < LINHAS_INFORMATIVAS; linha++) {
            if (reader.readLine() == null) {
                throw new IOException("O CSV terminou antes do cabeçalho.");
            }
        }
    }
}
