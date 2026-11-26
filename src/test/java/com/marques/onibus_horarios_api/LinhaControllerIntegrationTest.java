package com.marques.onibus_horarios_api;

import com.marques.onibus_horarios_api.infrastructure.persistence.repository.LinhaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void limparBanco() {
        linhaRepository.deleteAll();
    }

    @Test
    void deveCadastrarLinhaERetornar201() throws Exception {
        String request = """
                {
                  "codigo": "2001",
                  "nome": "Águas Lindas - Brasília",
                  "bairroOrigem": "Jardim da Barragem II",
                  "terminalDestino": "Rodoviária do Plano Piloto",
                  "operadora": "Global",
                  "tarifa": 5.50
                }
                """;

        mockMvc.perform(post("/api/v1/linhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/v1/linhas/")))
                .andExpect(jsonPath("$.codigo").value("2001"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deveRetornar400QuandoRequisicaoForInvalida() throws Exception {
        String request = """
                {
                  "codigo": "",
                  "nome": "",
                  "bairroOrigem": "",
                  "terminalDestino": "",
                  "operadora": "",
                  "tarifa": 0
                }
                """;

        mockMvc.perform(post("/api/v1/linhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoCodigoJaEstiverCadastrado() throws Exception {
        String request = """
                {
                  "codigo": "2001",
                  "nome": "Águas Lindas - Brasília",
                  "bairroOrigem": "Jardim da Barragem II",
                  "terminalDestino": "Rodoviária do Plano Piloto",
                  "operadora": "Global",
                  "tarifa": 5.50
                }
                """;

        mockMvc.perform(post("/api/v1/linhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/linhas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(
                        "Já existe uma linha cadastrada com o código: 2001"
                ));
    }

    @Test
    void deveRetornar404QuandoLinhaNaoExistir() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/linhas/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Linha não encontrada."));
    }
}
