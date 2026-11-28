package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.importacao.ResultadoImportacaoAntt;
import com.marques.onibus_horarios_api.application.exception.ErroDeImportacaoException;
import com.marques.onibus_horarios_api.application.usecase.ImportarDadosAnttService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/importacoes")
@Tag(name = "Importação (Admin)", description = "Importação de dados da ANTT (requer ROLE_ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class ImportacaoAnttController {

    private final ImportarDadosAnttService importarDadosAnttService;

    public ImportacaoAnttController(ImportarDadosAnttService importarDadosAnttService) {
        this.importarDadosAnttService = importarDadosAnttService;
    }

    @PostMapping("/antt")
    @Operation(summary = "Importar CSV da ANTT", description = "Processa arquivo CSV com dados de linhas e horários da ANTT")
    public ResponseEntity<ResultadoImportacaoAntt> importar(@RequestParam("arquivo") MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new ErroDeImportacaoException("O arquivo CSV não pode estar vazio.");
        }
        String nomeArquivo = arquivo.getOriginalFilename();
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new ErroDeImportacaoException("O arquivo enviado precisa ter um nome.");
        }

        try (var inputStream = arquivo.getInputStream()) {
            return ResponseEntity.ok(importarDadosAnttService.importar(nomeArquivo, inputStream));
        } catch (IOException exception) {
            throw new ErroDeImportacaoException(
                    "Não foi possível ler o arquivo CSV enviado.",
                    exception
            );
        }
    }
}
