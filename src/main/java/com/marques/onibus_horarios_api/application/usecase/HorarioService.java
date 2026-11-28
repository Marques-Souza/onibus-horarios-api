package com.marques.onibus_horarios_api.application.usecase;

import com.marques.onibus_horarios_api.application.dto.HorarioResponse;
import com.marques.onibus_horarios_api.application.dto.HorarioDestinoResponse;
import com.marques.onibus_horarios_api.application.dto.HorariosBuscaResponse;
import com.marques.onibus_horarios_api.application.dto.HorariosLinhaResponse;
import com.marques.onibus_horarios_api.application.dto.ProximasSaidasResponse;
import com.marques.onibus_horarios_api.application.dto.LinhaResponse;
import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.model.HorarioDaLinha;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import com.marques.onibus_horarios_api.domain.repository.HorarioRepository;
import com.marques.onibus_horarios_api.domain.repository.LinhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class HorarioService {

    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final int DIAS_PARA_BUSCAR = 8;

    private final HorarioRepository horarioRepository;
    private final LinhaRepository linhaRepository;

    public HorarioService(HorarioRepository horarioRepository, LinhaRepository linhaRepository) {
        this.horarioRepository = horarioRepository;
        this.linhaRepository = linhaRepository;
    }

    @Transactional(readOnly = true)
    public HorariosLinhaResponse listarAtivosPorLinha(
            UUID linhaId,
            Sentido sentido,
            TipoDia tipoDia
    ) {
        Linha linha = linhaRepository.findByIdAndActiveTrue(linhaId)
                .orElseThrow(() -> new NoSuchElementException("Linha não encontrada."));

        List<HorarioResponse> horarios = horarioRepository.buscarAtivosPorLinha(linhaId, sentido, tipoDia)
                .stream()
                .map(horario -> HorarioResponse.from(horario, linha))
                .toList();

        return new HorariosLinhaResponse(LinhaResponse.from(linha), horarios);
    }

    @Transactional(readOnly = true)
    public HorariosBuscaResponse buscarHorarios(
            String origem,
            String destino,
            String codigoLinha,
            String bairroOrigem,
            Sentido sentido,
            TipoDia tipoDia
    ) {
        validarFiltrosDaBusca(origem, destino, codigoLinha, bairroOrigem);

        String origemNormalizada = normalizar(origem);
        String destinoNormalizado = normalizar(destino);
        String codigoNormalizado = normalizar(codigoLinha);
        String bairroNormalizado = normalizar(bairroOrigem);
        List<HorarioDestinoResponse> horarios = horarioRepository
                .buscarAtivosPorTrajeto(
                        origemNormalizada,
                        destinoNormalizado,
                        codigoNormalizado,
                        bairroNormalizado,
                        sentido,
                        tipoDia
                )
                .stream()
                .map(HorarioDestinoResponse::from)
                .toList();

        return new HorariosBuscaResponse(
                origemNormalizada,
                destinoNormalizado,
                codigoNormalizado,
                bairroNormalizado,
                horarios
        );
    }

    @Transactional(readOnly = true)
    public ProximasSaidasResponse buscarProximasSaidas(
            String origem,
            String destino,
            String codigoLinha,
            String bairroOrigem,
            Sentido sentido,
            LocalDateTime dataHora
    ) {
        validarFiltrosDaBusca(origem, destino, codigoLinha, bairroOrigem);

        LocalDateTime dataHoraConsulta = dataHora == null
                ? LocalDateTime.now(FUSO_BRASILIA)
                : dataHora;
        List<HorarioDaLinha> horarios = horarioRepository.buscarAtivosPorTrajeto(
                normalizar(origem),
                normalizar(destino),
                normalizar(codigoLinha),
                normalizar(bairroOrigem),
                sentido,
                null
        );

        for (int dia = 0; dia < DIAS_PARA_BUSCAR; dia++) {
            LocalDate dataOperacao = dataHoraConsulta.toLocalDate().plusDays(dia);
            TipoDia tipoDia = tipoDiaPara(dataOperacao);
            LocalTime limite = dia == 0 ? dataHoraConsulta.toLocalTime() : LocalTime.MIN;

            List<HorarioDestinoResponse> saidasDoDia = horarios.stream()
                    .filter(item -> item.horario().tipoDia() == tipoDia)
                    .filter(item -> !item.horario().horaPartida().isBefore(limite))
                    .map(HorarioDestinoResponse::from)
                    .toList();

            if (!saidasDoDia.isEmpty()) {
                return new ProximasSaidasResponse(
                        normalizar(origem),
                        normalizar(destino),
                        normalizar(codigoLinha),
                        normalizar(bairroOrigem),
                        dataHoraConsulta,
                        dataOperacao,
                        tipoDia,
                        saidasDoDia.getFirst(),
                        saidasDoDia
                );
            }
        }

        return new ProximasSaidasResponse(
                normalizar(origem),
                normalizar(destino),
                normalizar(codigoLinha),
                normalizar(bairroOrigem),
                dataHoraConsulta,
                null,
                null,
                null,
                List.of()
        );
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private void validarFiltrosDaBusca(
            String origem,
            String destino,
            String codigoLinha,
            String bairroOrigem
    ) {
        if (estaVazio(origem) && estaVazio(destino)
                && estaVazio(codigoLinha) && estaVazio(bairroOrigem)) {
            throw new IllegalArgumentException(
                    "Informe origem, destino, código da linha ou bairro de origem para pesquisar."
            );
        }
    }

    private TipoDia tipoDiaPara(LocalDate data) {
        DayOfWeek diaDaSemana = data.getDayOfWeek();
        if (diaDaSemana == DayOfWeek.SATURDAY) {
            return TipoDia.SABADO;
        }
        if (diaDaSemana == DayOfWeek.SUNDAY) {
            return TipoDia.DOMINGO_FERIADO;
        }
        return TipoDia.DIA_UTIL;
    }

    private String normalizar(String valor) {
        return estaVazio(valor) ? null : valor.trim();
    }
}
