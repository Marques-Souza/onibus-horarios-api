package com.marques.onibus_horarios_api.application.mapper.importacao;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.domain.model.Linha;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AnttLinhaMapper {


    private static final String NAO_INFORMADO = "Não informado";

    private static final Pattern BAIRROS_EXPLICITOS = Pattern.compile(
            "(?iu)(?:ÁGUAS LINDAS DE GOIÁS|AGUAS LINDAS DE GOIAS)\\s*\\(([^)]+)\\)"
    );

    public Linha toDomain(AnttHorarioCsv registro, Linha existente) {
        return new Linha(
                existente == null ? null : existente.id(),
                registro.codigoLinha().trim(),
                registro.nomeLinha().trim(),
                extrairBairro(registro.nomeLinha()),
                extrairTerminal(registro.localidadeOrigemDestino()),
                registro.empresa().trim(),
                existente == null ? null : existente.tarifa(),
                existente == null || existente.active()
        );
    }

    private String extrairBairro(String nomeLinha) {
        Matcher matcher = BAIRROS_EXPLICITOS.matcher(nomeLinha);
        if (matcher.find()) {
            String bairro = matcher.group(1).trim();
            if (!ehSiglaEstado(bairro)) {
                return bairro;
            }
        }

        String nomeNormalizado = removerAcentos(nomeLinha).toUpperCase(Locale.ROOT);
        if (nomeNormalizado.startsWith("BRASILIA(DF)")
                || nomeNormalizado.startsWith("TAGUATINGA(DF)")) {
            return NAO_INFORMADO;
        }

        int separador = primeiroSeparadorRota(nomeLinha);
        if (separador > 0) {
            return nomeLinha.substring(0, separador).trim();
        }

        return NAO_INFORMADO;
    }

    private boolean ehSiglaEstado(String valor) {
        return valor.equalsIgnoreCase("GO") || valor.equalsIgnoreCase("DF");
    }

    private String extrairTerminal(String localidade) {
        String[] trechos = localidade.split("-", 2);
        if (trechos.length < 2) {
            throw new IllegalArgumentException(
                    "Localidade de origem e destino inválida: " + localidade
            );
        }

        String destino = trechos[1].split("/", 2)[0].trim();
        if (destino.isBlank()) {
            throw new IllegalArgumentException(
                    "Destino não informado na localidade: " + localidade
            );
        }

        return destino;
    }

    private int primeiroSeparadorRota(String nomeLinha) {
        int barra = nomeLinha.indexOf('/');
        int hifen = nomeLinha.indexOf(" - ");

        if (barra < 0) {
            return hifen;
        }
        if (hifen < 0) {
            return barra;
        }
        return Math.min(barra, hifen);
    }

    private String removerAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }


}
