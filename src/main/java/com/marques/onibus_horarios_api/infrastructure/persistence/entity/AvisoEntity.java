package com.marques.onibus_horarios_api.infrastructure.persistence.entity;

import com.marques.onibus_horarios_api.domain.model.StatusAviso;
import com.marques.onibus_horarios_api.domain.model.TipoAviso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "aviso")
public class AvisoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linha_id")
    private LinhaEntity linha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoAviso tipo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(name = "horario_referencia")
    private LocalDateTime horarioReferencia;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAviso status;

    public AvisoEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LinhaEntity getLinha() {
        return linha;
    }

    public void setLinha(LinhaEntity linha) {
        this.linha = linha;
    }

    public TipoAviso getTipo() {
        return tipo;
    }

    public void setTipo(TipoAviso tipo) {
        this.tipo = tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getHorarioReferencia() {
        return horarioReferencia;
    }

    public void setHorarioReferencia(LocalDateTime horarioReferencia) {
        this.horarioReferencia = horarioReferencia;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public StatusAviso getStatus() {
        return status;
    }

    public void setStatus(StatusAviso status) {
        this.status = status;
    }
}
