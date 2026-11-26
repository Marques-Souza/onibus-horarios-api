package com.marques.onibus_horarios_api.infrastructure.persistence.entity;


import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "horario")
public class HorarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "linha_id", nullable = false)
    private LinhaEntity linha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sentido sentido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dia", nullable = false, length = 20)
    private TipoDia tipoDia;

    @Column(name = "hora_partida", nullable = false)
    private LocalTime horaPartida;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false)
    private boolean active = true;

    public HorarioEntity(){
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

    public Sentido getSentido() {
        return sentido;
    }

    public void setSentido(Sentido sentido) {
        this.sentido = sentido;
    }

    public TipoDia getTipoDia() {
        return tipoDia;
    }

    public void setTipoDia(TipoDia tipoDia) {
        this.tipoDia = tipoDia;
    }

    public LocalTime getHoraPartida() {
        return horaPartida;
    }

    public void setHoraPartida(LocalTime horaPartida) {
        this.horaPartida = horaPartida;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
