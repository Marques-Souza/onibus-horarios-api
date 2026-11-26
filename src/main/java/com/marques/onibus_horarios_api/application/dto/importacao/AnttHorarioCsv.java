package com.marques.onibus_horarios_api.application.dto.importacao;

public record AnttHorarioCsv(

        String empresa,
        String prefixo,
        String localidadeOrigemDestino,
        String codigoLinha,
        String nomeLinha,
        String sentido,
        String horario,
        String diasUteis,
        String sabado,
        String domingo
) {
}
