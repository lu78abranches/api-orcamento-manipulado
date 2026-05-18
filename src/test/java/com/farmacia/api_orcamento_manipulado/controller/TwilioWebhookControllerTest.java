package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.service.IAReceitaService;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TwilioWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAReceitaService iaReceitaService;

    @MockitoBean
    private OrcamentoService orcamentoService;

    @Test
    @DisplayName("Deve receber payload do Twilio com imagem, chamar extrairItens e retornar 200 OK")
    void deveProcessarWebhookComSucesso() throws Exception {
        String numWhatsAppCliente = "whatsapp:+5511999999999";
        String urlImagemReceita = "https://twilio.com";

        // Configura o mock do Gemini para aceitar qualquer array de bytes e retornar
        // uma lista vazia de itens
        when(iaReceitaService.extrairItens(any(byte[].class))).thenReturn(List.of());

        mockMvc.perform(post("/api/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("From", numWhatsAppCliente)
                .param("MediaUrl0", urlImagemReceita)
                .param("NumMedia", "1"))
                .andExpect(status().isOk());

        // Garante que o controller tentou chamar a extração de itens usando a
        // assinatura correta
        verify(iaReceitaService).extrairItens(any(byte[].class));
    }
}
