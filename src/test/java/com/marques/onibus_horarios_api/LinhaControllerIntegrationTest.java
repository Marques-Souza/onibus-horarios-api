package com.marques.onibus_horarios_api;

import com.marques.onibus_horarios_api.infrastructure.persistence.repository.LinhaJpaRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.repository.HorarioJpaRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.repository.ImportacaoJpaRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.HorarioEntity;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class LinhaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinhaJpaRepository linhaRepository;

    @Autowired
    private HorarioJpaRepository horarioRepository;

    @Autowired
    private ImportacaoJpaRepository importacaoRepository;

    @BeforeEach
    void limparBanco() {
        importacaoRepository.deleteAll();
        horarioRepository.deleteAll();
        linhaRepository.deleteAll();
    }

    @Test
    void deveCadastrarLinhaERetornar201() throws Exception {
        mockMvc.perform(post("/api/v1/linhas")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoLinha("2001", "Águas Lindas - Brasília")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/v1/linhas/")))
                .andExpect(jsonPath("$.codigo").value("2001"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deveRetornar400QuandoRequisicaoForInvalida() throws Exception {
        mockMvc.perform(post("/api/v1/linhas")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "",
                                  "nome": "",
                                  "bairroOrigem": "",
                                  "terminalDestino": "",
                                  "operadora": "",
                                  "tarifa": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("A requisição contém campos inválidos."))
                .andExpect(jsonPath("$.errors.codigo").exists())
                .andExpect(jsonPath("$.errors.nome").exists())
                .andExpect(jsonPath("$.errors.bairroOrigem").exists())
                .andExpect(jsonPath("$.errors.terminalDestino").exists())
                .andExpect(jsonPath("$.errors.operadora").exists())
                .andExpect(jsonPath("$.errors.tarifa").exists());
    }

    @Test
    void deveRetornar409QuandoCodigoJaEstiverCadastrado() throws Exception {
        String request = requisicaoLinha("2001", "Águas Lindas - Brasília");

        mockMvc.perform(post("/api/v1/linhas")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/linhas")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(
                        "Já existe uma linha cadastrada com o código: 2001"
                ));
    }

    @Test
    void deveRetornar401AoCadastrarLinhaSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/v1/linhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoLinha("2001", "Linha protegida")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Autenticação necessária."));
    }

    @Test
    void deveRetornar403AoCadastrarLinhaSemPerfilAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/linhas")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoLinha("2001", "Linha protegida")))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").value("Acesso negado."));
    }

    @Test
    void deveAtualizarLinhaComPerfilAdmin() throws Exception {
        LinhaEntity linha = criarLinha("2001", "BRASILIA", true);

        mockMvc.perform(put("/api/v1/linhas/{id}", linha.getId())
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requisicaoLinha("2001", "Linha atualizada")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Linha atualizada"));
    }

    @Test
    void deveDesativarLinhaSemExcluirFisicamente() throws Exception {
        LinhaEntity linha = criarLinha("2001", "BRASILIA", true);

        mockMvc.perform(delete("/api/v1/linhas/{id}", linha.getId())
                        .with(adminJwt()))
                .andExpect(status().isNoContent());

        org.assertj.core.api.Assertions.assertThat(linhaRepository.findById(linha.getId()))
                .isPresent()
                .get()
                .extracting(LinhaEntity::isActive)
                .isEqualTo(false);
    }

    @Test
    void deveAutenticarAdminComCredenciaisValidas() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test-admin","password":"test-admin-password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void deveRetornar401ComCredenciaisInvalidas() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test-admin","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Credenciais inválidas."));
    }

    @Test
    void deveRetornar422QuandoCsvValidoNaoContiverRotasDaCidadeAtendida() throws Exception {
        String csv = """
                Informativo 1
                Informativo 2
                Informativo 3
                Informativo 4
                Empresa;Prefixo;Localidade;Codigo;Nome;Sentido;Horario;Dias uteis;Sabado;Domingo;Extra
                Operadora;123;GOIANIA/GO-BRASILIA/DF;2001;Linha teste;SENTIDO GO-DF;7:30;1;0;0;-
                """;
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "linhas.csv",
                "text/csv",
                csv.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/admin/importacoes/antt")
                        .file(arquivo)
                        .with(adminJwt()))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.status").value(422))
        .andExpect(jsonPath("$.detail").value(
                "O CSV não contém registros de rotas com origem em Águas Lindas de Goiás."
        ));
    }

    @Test
    void deveRetornar400ComNumeroDaLinhaQuandoHorarioCsvForInvalido() throws Exception {
        String csv = """
        Informativo 1
        Informativo 2
        Informativo 3
        Informativo 4
        Empresa;Prefixo;Localidade;Codigo;Nome;Sentido;Horario;Dias uteis;Sabado;Domingo;Extra
        Operadora;123;AGUAS LINDAS DE GOIAS/GO-BRASILIA/DF;2001;Linha teste;SENTIDO GO-DF;25:70;1;0;0;-
        """;
        MockMultipartFile arquivo = new MockMultipartFile(
        "arquivo",
        "linhas-invalidas.csv",
        "text/csv",
        csv.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/admin/importacoes/antt")
                .file(arquivo)
                .with(adminJwt()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail")
                .value("Horário inválido na linha 6 do CSV: '25:70'. Use o formato H:mm ou HH:mm."));
    }

    @Test
    void deveRetornar404QuandoLinhaNaoExistir() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/linhas/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Linha não encontrada."));
    }

    @Test
    void deveRetornar400ComProblemDetailParaJsonMalformado() throws Exception {
        mockMvc.perform(post("/api/v1/linhas")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("O corpo da requisição é inválido."));
    }

    @Test
    void deveRetornar400ComProblemDetailParaFiltroEnumeradoInvalido() throws Exception {
        mockMvc.perform(get("/api/v1/horarios/proximas-saidas")
                        .queryParam("destino", "BRASILIA")
                        .queryParam("sentido", "DIRETO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Parâmetro inválido: sentido."));
    }

    @Test
    void deveExigirSentidoParaConsultarProximasSaidas() throws Exception {
        mockMvc.perform(get("/api/v1/horarios/proximas-saidas")
                        .queryParam("destino", "BRASILIA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Parâmetro obrigatório ausente: sentido."));
    }

    @Test
    void deveUsarDestinoComoUnicoParametroDeDestinoNaBuscaDeHorarios() throws Exception {
        mockMvc.perform(get("/api/v1/horarios")
                        .queryParam("terminalDestino", "BRASILIA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveListarLinhasSemFiltrosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/linhas"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/linhas")
                        .queryParam("terminalDestino", "BRASILIA"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarLocaisDistintosDasLinhasAtivasEFiltrarSemAcentos() throws Exception {
        LinhaEntity primeira = criarLinha("LOCAL-1", "Brasília Centro", true);
        primeira.setBairroOrigem("Ceilândia Norte");
        linhaRepository.save(primeira);

        LinhaEntity repetida = criarLinha("LOCAL-2", "Brasília Centro", true);
        repetida.setBairroOrigem("Ceilândia Norte");
        linhaRepository.save(repetida);

        LinhaEntity inativa = criarLinha("LOCAL-3", "Brasília Sul", false);
        inativa.setBairroOrigem("Ceilândia Sul");
        linhaRepository.save(inativa);

        mockMvc.perform(get("/api/v1/locais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bairrosOrigem.length()").value(1))
                .andExpect(jsonPath("$.bairrosOrigem[0]").value("Ceilândia Norte"))
                .andExpect(jsonPath("$.terminaisDestino.length()").value(1))
                .andExpect(jsonPath("$.terminaisDestino[0]").value("Brasília Centro"));

        mockMvc.perform(get("/api/v1/locais").queryParam("q", "brasilia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bairrosOrigem.length()").value(0))
                .andExpect(jsonPath("$.terminaisDestino.length()").value(1))
                .andExpect(jsonPath("$.terminaisDestino[0]").value("Brasília Centro"));

        mockMvc.perform(get("/api/v1/locais").queryParam("q", "ceilandia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bairrosOrigem.length()").value(1))
                .andExpect(jsonPath("$.bairrosOrigem[0]").value("Ceilândia Norte"))
                .andExpect(jsonPath("$.terminaisDestino.length()").value(0));
    }

    @Test
    void deveRetornarFonteEDataNulaQuandoAindaNaoHouveImportacao() throws Exception {
        mockMvc.perform(get("/api/v1/sobre/dados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fonte").value("ANTT"))
                .andExpect(jsonPath("$.ultimaImportacao").isEmpty());
    }

    @Test
    void deveRegistrarImportacaoEManterIdempotenciaDosHorarios() throws Exception {
        String csv = """
                Informativo 1
                Informativo 2
                Informativo 3
                Informativo 4
                Empresa;Prefixo;Localidade;Codigo;Nome;Sentido;Horario;Dias uteis;Sabado;Domingo;Extra
                Operadora;123;AGUAS LINDAS DE GOIAS/GO-BRASILIA/DF;IMPORT-1;AGUAS LINDAS DE GOIAS - BRASILIA;SENTIDO GO-DF;7:30;1;1;1;-
                Operadora;123;AGUAS LINDAS DE GOIAS/GO-BRASILIA/DF;IMPORT-1;AGUAS LINDAS DE GOIAS - BRASILIA;SENTIDO GO-DF;7:30;1;1;1;-
                """;

        mockMvc.perform(multipart("/api/v1/admin/importacoes/antt")
                        .file(arquivoCsv("rotas-primeiro.csv", csv))
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.linhasCriadas").value(1))
                .andExpect(jsonPath("$.horariosCriados").value(3))
                .andExpect(jsonPath("$.horariosJaExistentes").value(3));

        mockMvc.perform(multipart("/api/v1/admin/importacoes/antt")
                        .file(arquivoCsv("rotas-segundo.csv", csv))
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.linhasCriadas").value(0))
                .andExpect(jsonPath("$.horariosCriados").value(0))
                .andExpect(jsonPath("$.horariosJaExistentes").value(6));

        var ultimaImportacao = importacaoRepository.findFirstByOrderByImportadoEmDesc().orElseThrow();
        org.assertj.core.api.Assertions.assertThat(ultimaImportacao.getNomeArquivo())
                .isEqualTo("rotas-segundo.csv");
        org.assertj.core.api.Assertions.assertThat(ultimaImportacao.getLinhasCriadas()).isZero();
        org.assertj.core.api.Assertions.assertThat(ultimaImportacao.getHorariosCriados()).isZero();
        org.assertj.core.api.Assertions.assertThat(ultimaImportacao.getHorariosIgnorados()).isEqualTo(6);

        mockMvc.perform(get("/api/v1/sobre/dados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fonte").value("ANTT"))
                .andExpect(jsonPath("$.ultimaImportacao").isNotEmpty());
    }

    @Test
    void naoDeveRegistrarImportacaoSemRotasDaCidadeAtendida() throws Exception {
        String csv = """
                Informativo 1
                Informativo 2
                Informativo 3
                Informativo 4
                Empresa;Prefixo;Localidade;Codigo;Nome;Sentido;Horario;Dias uteis;Sabado;Domingo;Extra
                Operadora;123;GOIANIA/GO-BRASILIA/DF;2001;Linha teste;SENTIDO GO-DF;7:30;1;0;0;-
                """;

        mockMvc.perform(multipart("/api/v1/admin/importacoes/antt")
                        .file(arquivoCsv("sem-rotas.csv", csv))
                        .with(adminJwt()))
                .andExpect(status().isUnprocessableEntity());

        org.assertj.core.api.Assertions.assertThat(importacaoRepository.count()).isZero();
    }

    @Test
    void deveFiltrarLinhasEAplicarPaginacao() throws Exception {
        criarLinha("2501", "TAGUATINGA", true);
        criarLinha("2502", "TAGUATINGA", true);
        criarLinha("2503", "CEILANDIA", true);

        mockMvc.perform(get("/api/v1/linhas")
                        .queryParam("bairroOrigem", "Centro")
                        .queryParam("terminalDestino", "TAGUATINGA")
                        .queryParam("pagina", "0")
                        .queryParam("tamanho", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.numero").value(0))
                .andExpect(jsonPath("$.tamanho").value(1))
                .andExpect(jsonPath("$.totalElementos").value(2))
                .andExpect(jsonPath("$.totalPaginas").value(2));
    }

    @Test
    void deveBuscarLinhasEHorariosSemDiferenciarAcentos() throws Exception {
        LinhaEntity linha = criarLinha("ACENTO-TESTE", "Brasília", true);
        linha.setBairroOrigem("Ceilândia Norte");
        linha.setNome("Águas Lindas de Goiás - Ceilândia");
        linha = linhaRepository.save(linha);
        criarHorario(linha, "IDA", "DIA_UTIL", "07:30", true);

        mockMvc.perform(get("/api/v1/linhas")
                        .queryParam("bairroOrigem", "ceilandia")
                        .queryParam("terminalDestino", "brasilia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(1))
                .andExpect(jsonPath("$.conteudo[0].codigo").value("ACENTO-TESTE"));

        mockMvc.perform(get("/api/v1/horarios")
                        .queryParam("origem", "aguas lindas")
                        .queryParam("destino", "ceilandia")
                        .queryParam("bairroOrigem", "ceilandia")
                        .queryParam("sentido", "IDA")
                        .queryParam("tipoDia", "DIA_UTIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horarios.length()").value(1))
                .andExpect(jsonPath("$.horarios[0].codigoLinha").value("ACENTO-TESTE"));
    }

    @Test
    void deveListarHorariosAtivosOrdenadosEFiltrarPorSentidoETipoDia() throws Exception {
        LinhaEntity linha = criarLinhaParaConsultaDeHorarios();
        criarHorario(linha, "IDA", "DIA_UTIL", "07:30", true);
        criarHorario(linha, "VOLTA", "DIA_UTIL", "08:00", true);
        criarHorario(linha, "IDA", "DIA_UTIL", "09:00", false);

        mockMvc.perform(get("/api/v1/linhas/{linhaId}/horarios", linha.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.linha.codigo").value("HORARIO-TESTE"))
                .andExpect(jsonPath("$.linha.terminalDestino").value("Brasília"))
                .andExpect(jsonPath("$.horarios.length()").value(2))
                .andExpect(jsonPath("$.horarios[0].horaPartida").value("07:30:00"))
                .andExpect(jsonPath("$.horarios[0].sentidoDescricao")
                        .value("Águas Lindas de Goiás → Brasília"))
                .andExpect(jsonPath("$.horarios[0].origem").value("Águas Lindas de Goiás"))
                .andExpect(jsonPath("$.horarios[0].destino").value("Brasília"))
                .andExpect(jsonPath("$.horarios[1].origem").value("Brasília"))
                .andExpect(jsonPath("$.horarios[1].destino").value("Águas Lindas de Goiás"))
                .andExpect(jsonPath("$.horarios[1].horaPartida").value("08:00:00"));

        mockMvc.perform(get("/api/v1/linhas/{linhaId}/horarios", linha.getId())
                        .queryParam("sentido", "IDA")
                        .queryParam("tipoDia", "DIA_UTIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horarios.length()").value(1))
                .andExpect(jsonPath("$.horarios[0].sentido").value("IDA"));
    }

    @Test
    void deveRetornar404AoConsultarHorariosDeLinhaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/linhas/{linhaId}/horarios", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Linha não encontrada."));
    }

    @Test
    void deveListarHorariosDeTodasAsLinhasDoDestino() throws Exception {
        LinhaEntity linha2501 = criarLinha("2501", "TAGUATINGA", true);
        LinhaEntity linha2502 = criarLinha("2502", "TAGUATINGA", true);
        LinhaEntity linhaBrasilia = criarLinha("2001", "BRASILIA", true);
        LinhaEntity linhaInativa = criarLinha("2503", "TAGUATINGA", false);

        criarHorario(linha2501, "IDA", "DIA_UTIL", "06:05", true);
        criarHorario(linha2502, "IDA", "DIA_UTIL", "06:30", true);
        criarHorario(linhaBrasilia, "IDA", "DIA_UTIL", "05:00", true);
        criarHorario(linhaInativa, "IDA", "DIA_UTIL", "06:45", true);

        mockMvc.perform(get("/api/v1/horarios")
                        .queryParam("destino", "taguatinga")
                        .queryParam("origem", "Águas Lindas de Goiás")
                        .queryParam("sentido", "IDA")
                        .queryParam("tipoDia", "DIA_UTIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destino").value("taguatinga"))
                .andExpect(jsonPath("$.horarios.length()").value(2))
                .andExpect(jsonPath("$.horarios[0].codigoLinha").value("2501"))
                .andExpect(jsonPath("$.horarios[0].bairroOrigem").value("Centro"))
                .andExpect(jsonPath("$.horarios[0].terminalDestino").value("TAGUATINGA"))
                .andExpect(jsonPath("$.horarios[0].horaPartida").value("06:05:00"))
                .andExpect(jsonPath("$.horarios[1].codigoLinha").value("2502"))
                .andExpect(jsonPath("$.horarios[1].horaPartida").value("06:30:00"));

        mockMvc.perform(get("/api/v1/horarios")
                        .queryParam("codigoLinha", "2502")
                        .queryParam("bairroOrigem", "Centro")
                        .queryParam("tipoDia", "DIA_UTIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horarios.length()").value(1))
                .andExpect(jsonPath("$.horarios[0].codigoLinha").value("2502"));
    }

    @Test
    void deveBuscarTrajetoPorOrigemEDestinoNaDescricaoDaLinha() throws Exception {
        LinhaEntity linhaW3 = criarLinha("2010", "BRASILIA", true);
        linhaW3.setNome("AGUAS LINDAS DE GOIAS - W3 SUL - BRASILIA");
        linhaW3 = linhaRepository.save(linhaW3);
        criarHorario(linhaW3, "IDA", "DIA_UTIL", "07:00", true);
        criarHorario(linhaW3, "VOLTA", "DIA_UTIL", "17:00", true);

        mockMvc.perform(get("/api/v1/horarios")
                        .queryParam("origem", "Águas Lindas de Goiás")
                        .queryParam("destino", "W3")
                        .queryParam("sentido", "IDA")
                        .queryParam("tipoDia", "DIA_UTIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horarios.length()").value(1))
                .andExpect(jsonPath("$.horarios[0].codigoLinha").value("2010"))
                .andExpect(jsonPath("$.horarios[0].bairroOrigem").value("Centro"))
                .andExpect(jsonPath("$.horarios[0].horaPartida").value("07:00:00"));
    }

    @Test
    void deveRetornarProximasSaidasDoDiaAtual() throws Exception {
        LinhaEntity linha = criarLinha("PROXIMAS-TESTE", "BRASILIA", true);
        criarHorario(linha, "IDA", "DIA_UTIL", "07:30", true);
        criarHorario(linha, "IDA", "DIA_UTIL", "08:00", true);
        criarHorario(linha, "IDA", "SABADO", "09:00", true);

        mockMvc.perform(get("/api/v1/horarios/proximas-saidas")
                        .queryParam("destino", "BRASILIA")
                        .queryParam("sentido", "IDA")
                        .queryParam("dataHora", "2026-11-26T07:45:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataHoraConsulta").value("2026-11-26T07:45:00"))
                .andExpect(jsonPath("$.dataOperacao").value("2026-11-26"))
                .andExpect(jsonPath("$.tipoDia").value("DIA_UTIL"))
                .andExpect(jsonPath("$.minutosParaSaida").value(15))
                .andExpect(jsonPath("$.proximaSaida.horaPartida").value("08:00:00"))
                .andExpect(jsonPath("$.proximasSaidas.length()").value(1));
    }

    @Test
    void deveBuscarProximaSaidaNoProximoDiaComHorario() throws Exception {
        LinhaEntity linha = criarLinha("SABADO-TESTE", "BRASILIA", true);
        criarHorario(linha, "IDA", "SABADO", "08:00", true);
        criarHorario(linha, "IDA", "SABADO", "09:00", true);

        mockMvc.perform(get("/api/v1/horarios/proximas-saidas")
                        .queryParam("destino", "BRASILIA")
                        .queryParam("sentido", "IDA")
                        .queryParam("dataHora", "2026-11-26T20:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataOperacao").value("2026-11-28"))
                .andExpect(jsonPath("$.tipoDia").value("SABADO"))
                .andExpect(jsonPath("$.minutosParaSaida").isEmpty())
                .andExpect(jsonPath("$.proximaSaida.horaPartida").value("08:00:00"))
                .andExpect(jsonPath("$.proximasSaidas.length()").value(2));
    }

    @Test
    void deveEncontrarSaidaNoProximoCicloSemanal() throws Exception {
        LinhaEntity linha = criarLinha("DOMINGO-TESTE", "BRASILIA", true);
        criarHorario(linha, "IDA", "DOMINGO_FERIADO", "08:00", true);

        mockMvc.perform(get("/api/v1/horarios/proximas-saidas")
                        .queryParam("destino", "BRASILIA")
                        .queryParam("sentido", "IDA")
                        .queryParam("dataHora", "2026-11-29T23:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataOperacao").value("2026-12-06"))
                .andExpect(jsonPath("$.tipoDia").value("DOMINGO_FERIADO"))
                .andExpect(jsonPath("$.proximaSaida.horaPartida").value("08:00:00"));
    }

    private LinhaEntity criarLinhaParaConsultaDeHorarios() {
        return criarLinha("HORARIO-TESTE", "Brasília", true);
    }

    private String requisicaoLinha(String codigo, String nome) {
        return """
                {
                  "codigo": "%s",
                  "nome": "%s",
                  "bairroOrigem": "Jardim da Barragem II",
                  "terminalDestino": "Rodoviária do Plano Piloto",
                  "operadora": "Global",
                  "tarifa": 5.50
                }
                """.formatted(codigo, nome);
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor adminJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private MockMultipartFile arquivoCsv(String nome, String conteudo) {
        return new MockMultipartFile(
                "arquivo",
                nome,
                "text/csv",
                conteudo.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    private LinhaEntity criarLinha(String codigo, String terminalDestino, boolean active) {
        LinhaEntity linha = new LinhaEntity();
        linha.setCodigo(codigo);
        linha.setNome("Linha de teste");
        linha.setBairroOrigem("Centro");
        linha.setTerminalDestino(terminalDestino);
        linha.setOperadora("Operadora de teste");
        linha.setActive(active);
        return linhaRepository.save(linha);
    }

    private void criarHorario(LinhaEntity linha, String sentido, String tipoDia, String horaPartida, boolean active) {
        HorarioEntity horario = new HorarioEntity();
        horario.setLinha(linha);
        horario.setSentido(Sentido.valueOf(sentido));
        horario.setTipoDia(TipoDia.valueOf(tipoDia));
        horario.setHoraPartida(LocalTime.parse(horaPartida));
        horario.setActive(active);
        horarioRepository.save(horario);
    }
}
