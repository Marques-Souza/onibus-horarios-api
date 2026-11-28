package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.application.dto.importacao.ResultadoImportacaoAntt;
import com.marques.onibus_horarios_api.application.exception.ErroDeImportacaoException;
import com.marques.onibus_horarios_api.application.exception.ImportacaoSemRotasException;
import com.marques.onibus_horarios_api.application.mapper.importacao.AnttHorarioMapper;
import com.marques.onibus_horarios_api.application.mapper.importacao.AnttLinhaMapper;
import com.marques.onibus_horarios_api.application.port.importacao.LeitorAnttCsv;
import com.marques.onibus_horarios_api.domain.model.ChaveHorario;
import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.Importacao;
import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.repository.HorarioRepository;
import com.marques.onibus_horarios_api.domain.repository.ImportacaoRepository;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImportarDadosAnttService {

    private static final String PREFIXO_LOCALIDADE_ALVO = "AGUAS LINDAS DE GOIAS/GO-";

    private final LeitorAnttCsv leitorAnttCsv;
    private final LinhaRepository linhaRepository;
    private final HorarioRepository horarioRepository;
    private final ImportacaoRepository importacaoRepository;
    private final AnttLinhaMapper linhaMapper;
    private final AnttHorarioMapper horarioMapper;

    public ImportarDadosAnttService(
            LeitorAnttCsv leitorAnttCsv,
            LinhaRepository linhaRepository,
            HorarioRepository horarioRepository,
            ImportacaoRepository importacaoRepository,
            AnttLinhaMapper linhaMapper,
            AnttHorarioMapper horarioMapper
    ) {
        this.leitorAnttCsv = leitorAnttCsv;
        this.linhaRepository = linhaRepository;
        this.horarioRepository = horarioRepository;
        this.importacaoRepository = importacaoRepository;
        this.linhaMapper = linhaMapper;
        this.horarioMapper = horarioMapper;
    }

    @Transactional(rollbackFor = IOException.class)
    public ResultadoImportacaoAntt importar(String nomeArquivo, InputStream arquivo) throws IOException {
        var registros = leitorAnttCsv.ler(arquivo);
        var linhasPorCodigo = new HashMap<String, Linha>();
        var horariosImportados = new ArrayList<Horario>();

        int registrosIgnorados = 0;
        int linhasCriadas = 0;
        int linhasAtualizadas = 0;
        int registrosAlvo = 0;

        for (AnttHorarioCsv registro : registros) {
            if (!ehLocalidadeAlvo(registro.localidadeOrigemDestino())) {
                registrosIgnorados++;
                continue;
            }

            registrosAlvo++;
            String codigo = registro.codigoLinha().trim();
            if (codigo.isBlank()) {
                throw new ErroDeImportacaoException(
                        "Código de linha vazio na linha " + registro.numeroLinhaCsv() + " do CSV."
                );
            }

            Linha linha = linhasPorCodigo.get(codigo);
            if (linha == null) {
                var existente = linhaRepository.findByCodigo(codigo).orElse(null);
                linha = linhaMapper.toDomain(registro, existente);
                linha = linhaRepository.save(linha);
                linhasPorCodigo.put(codigo, linha);

                if (existente == null) {
                    linhasCriadas++;
                } else {
                    linhasAtualizadas++;
                }
            }

            horariosImportados.addAll(horarioMapper.toDomain(registro, linha.id()));
        }

        if (registrosAlvo == 0) {
            throw new ImportacaoSemRotasException();
        }

        Set<UUID> linhaIds = linhasPorCodigo.values().stream()
                .map(Linha::id)
                .collect(Collectors.toSet());
        Set<ChaveHorario> chavesExistentes = horarioRepository.buscarChavesExistentesPorLinhaIds(linhaIds);

        int horariosCriados = 0;
        int horariosJaExistentes = 0;
        for (Horario horario : horariosImportados) {
            ChaveHorario chave = new ChaveHorario(
                    horario.linhaId(),
                    horario.sentido(),
                    horario.tipoDia(),
                    horario.horaPartida()
            );
            if (!chavesExistentes.add(chave)) {
                horariosJaExistentes++;
                continue;
            }
            horarioRepository.save(horario);
            horariosCriados++;
        }

        var resultado = new ResultadoImportacaoAntt(
                registros.size(),
                registrosIgnorados,
                linhasCriadas,
                linhasAtualizadas,
                horariosCriados,
                horariosJaExistentes
        );
        importacaoRepository.save(new Importacao(
                null,
                nomeArquivo,
                Instant.now(),
                linhasCriadas,
                horariosCriados,
                horariosJaExistentes
        ));
        return resultado;
    }

    private boolean ehLocalidadeAlvo(String localidade) {
        String normalizada = Normalizer.normalize(localidade, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);

        return normalizada.startsWith(PREFIXO_LOCALIDADE_ALVO);
    }
}