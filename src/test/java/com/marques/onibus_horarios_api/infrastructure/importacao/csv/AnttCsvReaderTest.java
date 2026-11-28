package com.marques.onibus_horarios_api.infrastructure.importacao.csv;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnttCsvReaderTest {

    private final AnttCsvReader reader = new AnttCsvReader();

    @Test
    void deveLerCsvEmUtf8() throws Exception {
        var registros = reader.ler(new ByteArrayInputStream(csv("União").getBytes(StandardCharsets.UTF_8)));

        assertEquals("União", registros.getFirst().empresa());
        assertEquals(6, registros.getFirst().numeroLinhaCsv());
    }

    @Test
    void deveUsarWindows1252QuandoUtf8ForInvalido() throws Exception {
        var bytes = csv("União").getBytes(Charset.forName("windows-1252"));

        var registros = reader.ler(new ByteArrayInputStream(bytes));

        assertEquals("União", registros.getFirst().empresa());
        assertEquals(6, registros.getFirst().numeroLinhaCsv());
    }

    private String csv(String empresa) {
        return """
                Informativo 1
                Informativo 2
                Informativo 3
                Informativo 4
                Empresa;Prefixo;Localidade;Codigo;Nome;Sentido;Horario;Dias uteis;Sabado;Domingo;Extra
                %s;123;AGUAS LINDAS DE GOIAS/GO-BRASILIA/DF;2001;Linha teste;SENTIDO GO-DF;7:30;1;0;0;-
                """.formatted(empresa);
    }
}
