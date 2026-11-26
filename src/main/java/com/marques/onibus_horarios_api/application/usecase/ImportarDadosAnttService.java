package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;
import com.marques.onibus_horarios_api.application.dto.importacao.ResultadoImportacaoAntt;
import com.marques.onibus_horarios_api.application.mapper.importacao.AnttHorarioMapper;
import com.marques.onibus_horarios_api.application.mapper.importacao.AnttLinhaMapper;
import com.marques.onibus_horarios_api.application.port.importacao.LeitorAnttCsv;
import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.repository.HorarioRepository;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;

@Service
public class ImportarDadosAnttService {

    private static final String PREFIXO_LOCALIDADE_ALVO = "AGUAS LINDAS DE GOIAS/GO-";

    private final LeitorAnttCsv leitorAnttCsv;
    private final LinhaRepository linhaRepository;
    private final HorarioRepository horarioRepository;
    private final AnttLinhaMapper linhaMapper;
    private final AnttHorarioMapper horarioMapper;

    public ImportarDadosAnttService(
            LeitorAnttCsv leitorAnttCsv,
            LinhaRepository linhaRepository,
            HorarioRepository horarioRepository,
            AnttLinhaMapper linhaMapper,
            AnttHorarioMapper horarioMapper
    ) {
        this.leitorAnttCsv = leitorAnttCsv;
        this.linhaRepository = linhaRepository;
        this.horarioRepository = horarioRepository;
        this.linhaMapper = linhaMapper;
        this.horarioMapper = horarioMapper;
    }

    @Transactional(rollbackFor = IOException.class)
    public ResultadoImportacaoAntt importar(InputStream arquivo) throws IOException {
        var registros = leitorAnttCsv.ler(arquivo);
        var linhasPorCodigo = new HashMap<String, Linha>();

        int registrosIgnorados = 0;
        int linhasCriadas = 0;
        int linhasAtualizadas = 0;
        int horariosCriados = 0;
        int horariosJaExistentes = 0;
        int registrosAlvo = 0;

        for (AnttHorarioCsv registro : registros) {
            if (!ehLocalidadeAlvo(registro.localidadeOrigemDestino())) {
                registrosIgnorados++;
                continue;
            }

            registrosAlvo++;
            String codigo = registro.codigoLinha().trim();
            if (codigo.isBlank()) {
                throw new IllegalArgumentException("O CSV contém código de linha vazio.");
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

            var horarios = horarioMapper.toDomain(registro, linha.id());
            for (var horario : horarios) {
                boolean existe = horarioRepository.existsByLinhaIdAndSentidoAndTipoDiaAndHoraPartida(
                        horario.linhaId(),
                        horario.sentido(),
                        horario.tipoDia(),
                        horario.horaPartida()
                );

                if (existe) {
                    horariosJaExistentes++;
                } else {
                    horarioRepository.save(horario);
                    horariosCriados++;
                }
            }
        }

        if (registrosAlvo == 0) {
            throw new IllegalArgumentException(
                    "O CSV não contém registros de rotas com origem em Águas Lindas de Goiás."
            );
        }

        return new ResultadoImportacaoAntt(
                registros.size(),
                registrosIgnorados,
                linhasCriadas,
                linhasAtualizadas,
                horariosCriados,
                horariosJaExistentes
        );
    }

    private boolean ehLocalidadeAlvo(String localidade) {
        String normalizada = Normalizer.normalize(localidade, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);

        return normalizada.startsWith(PREFIXO_LOCALIDADE_ALVO);
    }
}