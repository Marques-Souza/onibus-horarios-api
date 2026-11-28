package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.HorariosBuscaResponse;
import com.marques.onibus_horarios_api.application.dto.ProximasSaidasResponse;
import com.marques.onibus_horarios_api.application.usecase.HorarioService;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/horarios")
public class HorariosDestinoController {

    private final HorarioService horarioService;

    public HorariosDestinoController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<HorariosBuscaResponse> listarPorDestino(
            @RequestParam(required = false) String origem,
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) String terminalDestino,
            @RequestParam(required = false) String codigoLinha,
            @RequestParam(required = false) String bairroOrigem,
            @RequestParam(required = false) Sentido sentido,
            @RequestParam(required = false) TipoDia tipoDia
    ) {
        return ResponseEntity.ok(
                horarioService.buscarHorarios(
                        origem,
                        destino == null ? terminalDestino : destino,
                        codigoLinha,
                        bairroOrigem,
                        sentido,
                        tipoDia
                )
        );
    }

    @GetMapping("/proximas-saidas")
    public ResponseEntity<ProximasSaidasResponse> listarProximasSaidas(
            @RequestParam(required = false) String origem,
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) String terminalDestino,
            @RequestParam(required = false) String codigoLinha,
            @RequestParam(required = false) String bairroOrigem,
            @RequestParam(required = false) Sentido sentido,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora
    ) {
        return ResponseEntity.ok(
                horarioService.buscarProximasSaidas(
                        origem,
                        destino == null ? terminalDestino : destino,
                        codigoLinha,
                        bairroOrigem,
                        sentido,
                        dataHora
                )
        );
    }
}
