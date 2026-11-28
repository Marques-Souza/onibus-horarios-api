package com.marques.onibus_horarios_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "importacao")
public class ImportacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @Column(name = "importado_em", nullable = false)
    private Instant importadoEm;

    @Column(name = "linhas_criadas", nullable = false)
    private int linhasCriadas;

    @Column(name = "horarios_criados", nullable = false)
    private int horariosCriados;

    @Column(name = "horarios_ignorados", nullable = false)
    private int horariosIgnorados;

    public ImportacaoEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Instant getImportadoEm() {
        return importadoEm;
    }

    public void setImportadoEm(Instant importadoEm) {
        this.importadoEm = importadoEm;
    }

    public int getLinhasCriadas() {
        return linhasCriadas;
    }

    public void setLinhasCriadas(int linhasCriadas) {
        this.linhasCriadas = linhasCriadas;
    }

    public int getHorariosCriados() {
        return horariosCriados;
    }

    public void setHorariosCriados(int horariosCriados) {
        this.horariosCriados = horariosCriados;
    }

    public int getHorariosIgnorados() {
        return horariosIgnorados;
    }

    public void setHorariosIgnorados(int horariosIgnorados) {
        this.horariosIgnorados = horariosIgnorados;
    }
}
