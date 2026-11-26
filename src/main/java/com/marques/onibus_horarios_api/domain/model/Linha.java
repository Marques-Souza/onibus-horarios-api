package com.marques.onibus_horarios_api.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Linha(
        UUID id,
        String codigo,
        String nome,
        String bairroOrigem,
        String terminalDestino,
        String operadora,
        BigDecimal tarifa,
        boolean active
) {

    public Linha atualizarCom(Linha dadosAtualizados) {
        return new Linha(
                id,
                dadosAtualizados.codigo(),
                dadosAtualizados.nome(),
                dadosAtualizados.bairroOrigem(),
                dadosAtualizados.terminalDestino(),
                dadosAtualizados.operadora(),
                dadosAtualizados.tarifa(),
                active
        );
    }

    public Linha desativar() {
        return new Linha(id, codigo, nome, bairroOrigem, terminalDestino, operadora, tarifa, false);
    }
}
