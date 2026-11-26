package com.marques.onibus_horarios_api.domain.model;

import java.util.List;

public record Pagina<T>(

        List<T> conteudo,
        int numero,
        int tamanho,
        long totalElementos,
        int totalPaginas
) {

}
