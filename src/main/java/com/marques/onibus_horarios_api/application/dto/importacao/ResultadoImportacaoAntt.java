package com.marques.onibus_horarios_api.application.dto.importacao;

public record ResultadoImportacaoAntt(


        int registrosLidos,
        int registrosIgnorados,
        int linhasCriadas,
        int linhasAtualizadas,
        int horariosCriados,
        int horariosJaExistentes

) {
}
