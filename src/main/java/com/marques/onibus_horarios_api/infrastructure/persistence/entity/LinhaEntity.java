package com.marques.onibus_horarios_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "linha")
@Getter
@Setter
@NoArgsConstructor
public class LinhaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "bairro_origem", nullable = false, length = 100)
    private String bairroOrigem;

    @Column(name = "terminal_destino", nullable = false, length = 100)
    private String terminalDestino;

    @Column(nullable = false, length = 100)
    private String operadora;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal tarifa;

    @Column(nullable = false)
    private boolean active = true;
}
