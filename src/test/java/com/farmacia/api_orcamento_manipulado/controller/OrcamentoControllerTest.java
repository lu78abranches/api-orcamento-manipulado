package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoApprovedDTO;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.model.OrcamentoStatus;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.farmacia.api_orcamento_manipulado.service.TokenService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

// src/test/java/com/farmacia/api_orcamento_manipulado/controller/OrcamentoControllerTest.java
@WebMvcTest(OrcamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
                "DB_USERNAME=teste",
                "DB_PASSWORD=teste",
                "OPENAI_API_KEY=teste"
})
@ImportAutoConfiguration(exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class
})
public class OrcamentoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private OrcamentoService orcamentoService;

        @MockitoBean
        private TokenService tokenService;

        @Test
        void deveAceitarUploadDeImagem() throws Exception {
                MockMultipartFile arquivo = new MockMultipartFile(
                                "imagem", "receita.jpg", "image/jpeg", "conteudo".getBytes());

                mockMvc.perform(multipart("/api/orcamentos/upload").file(arquivo))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "farmaceutico1", roles = { "FARMACEUTICO" })
        @DisplayName("Deve aprovar o orcamento com sucesso e retornar o status 200 OK")
        void deveAprovarOrcamentoComSucesso() throws Exception {
                Long orcamentoId = 1L;

                // Monta o orçamento simulado de retorno
                Orcamento orcamentoAprovado = new Orcamento();
                orcamentoAprovado.setId(orcamentoId);
                orcamentoAprovado.setClienteWhatsapp("whatsapp:+5511999999999");
                orcamentoAprovado.setStatus(OrcamentoStatus.APROVADO);

                // Configura o comportamento esperado do mock do Service
                when(orcamentoService.aprovarOrcamento(orcamentoId)).thenReturn(orcamentoAprovado);

                // Mock the DTO conversion method
                OrcamentoApprovedDTO dtoResponse = new OrcamentoApprovedDTO("APROVADO");
                when(orcamentoService.criarRespostaAprovacao(orcamentoAprovado)).thenReturn(dtoResponse);

                // Executa a chamada PUT simulando o clique de aprovação do painel do
                // farmacêutico
                // STEP 2: Pharmacist approval (JWT required) returns only status
                mockMvc.perform(put("/api/orcamentos/" + orcamentoId + "/aprovar"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("APROVADO"));
        }

        @Test
        @WithMockUser(username = "farmaceutico1", roles = { "FARMACEUTICO" })
        @DisplayName("Deve recusar o orcamento com sucesso e retornar o status 200 OK")
        void deveRecusarOrcamentoComSucesso() throws Exception {
                Long orcamentoId = 1L;

                Orcamento orcamentoRecusado = new Orcamento();
                orcamentoRecusado.setId(orcamentoId);
                orcamentoRecusado.setClienteWhatsapp("whatsapp:+5511999999999");
                orcamentoRecusado.setStatus(OrcamentoStatus.RECUSADO);

                // Configura o comportamento esperado do mock do Service
                when(orcamentoService.recusarOrcamento(orcamentoId)).thenReturn(orcamentoRecusado);

                // Executa a chamada PUT simulando o clique de recusa do painel
                mockMvc.perform(put("/api/orcamentos/" + orcamentoId + "/recusar"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(orcamentoId))
                                .andExpect(jsonPath("$.status").value("RECUSADO"))
                                .andExpect(jsonPath("$.clienteWhatsapp").value("whatsapp:+5511999999999"));
        }

        @Test
        @WithMockUser(username = "farmaceutico1") // Simula o usuário autenticado via JWT
        @DisplayName("GET /api/orcamentos/pendentes deve retornar 200 OK e a lista de pendentes")
        void deveRetornarListaDePendentesComSucesso() throws Exception {
                ItemExtraidoDTO item = new ItemExtraidoDTO("Teste", BigDecimal.TEN);
                OrcamentoPendenteDTO dto = new OrcamentoPendenteDTO(1L, "123", "PENDENTE", BigDecimal.TEN,
                                List.of(item));

                when(orcamentoService.listarPendentes()).thenReturn(List.of(dto));

                mockMvc.perform(get("/api/orcamentos/pendentes")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].clienteWhatsapp").value("123"))
                                .andExpect(jsonPath("$[0].status").value("PENDENTE"));
        }

}
