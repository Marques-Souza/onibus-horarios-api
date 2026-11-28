package com.marques.onibus_horarios_api.presentation.controller;

import com.marques.onibus_horarios_api.application.dto.importacao.ResultadoImportacaoAntt;
import com.marques.onibus_horarios_api.application.exception.ErroDeImportacaoException;
import com.marques.onibus_horarios_api.application.usecase.ImportarDadosAnttService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/importacoes")
public class ImportacaoAnttController {

    private final ImportarDadosAnttService importarDadosAnttService;

    public ImportacaoAnttController(ImportarDadosAnttService importarDadosAnttService) {
        this.importarDadosAnttService = importarDadosAnttService;
    }

    @PostMapping("/antt")
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
