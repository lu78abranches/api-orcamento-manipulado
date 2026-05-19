package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.model.Orcamento;
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

import static org.mockito.Mockito.when;
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

                // Configura o comportamento esperado do mock do Service
                when(orcamentoService.aprovarOrcamento(orcamentoId)).thenReturn(orcamentoAprovado);

                // Executa a chamada PUT simulando o clique de aprovação do painel do
                // farmacêutico
                mockMvc.perform(put("/api/orcamentos/" + orcamentoId + "/aprovar"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(orcamentoId))
                                .andExpect(jsonPath("$.clienteWhatsapp").value("whatsapp:+5511999999999"));
        }

        @Test
        @WithMockUser(username = "farmaceutico1", roles = { "FARMACEUTICO" })
        @DisplayName("Deve recusar o orcamento com sucesso e retornar o status 200 OK")
        void deveRecusarOrcamentoComSucesso() throws Exception {
                Long orcamentoId = 1L;

                Orcamento orcamentoRecusado = new Orcamento();
                orcamentoRecusado.setId(orcamentoId);
                orcamentoRecusado.setClienteWhatsapp("whatsapp:+5511999999999");
                orcamentoRecusado.setStatus("RECUSADO"); // Define o status simulado de retorno

                // Configura o comportamento esperado do mock do Service
                when(orcamentoService.recusarOrcamento(orcamentoId)).thenReturn(orcamentoRecusado);

                // Executa a chamada PUT simulando o clique de recusa do painel
                mockMvc.perform(put("/api/orcamentos/" + orcamentoId + "/recusar"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(orcamentoId))
                                .andExpect(jsonPath("$.status").value("RECUSADO"))
                                .andExpect(jsonPath("$.clienteWhatsapp").value("whatsapp:+5511999999999"));
        }

}
