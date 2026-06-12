package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import com.farmacia.api_orcamento_manipulado.service.ReceitaValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrescriptionUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PrescriptionUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrcamentoService orcamentoService;

    @MockBean
    private ReceitaValidationService receitaValidationService;

    @Test
    @DisplayName("Deve rejeitar upload não prescricional com mensagem amigável em português")
    void deveRejeitarUploadNaoPrescricional() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "file",
                "documento.jpg",
                "image/jpeg",
                "conteudo generico nao prescricao".getBytes());

        when(orcamentoService.extrairItensDaImagem(any())).thenReturn(List.of());
        when(receitaValidationService.isPrescricaoValida(anyList(), eq(null))).thenReturn(false);

        mockMvc.perform(multipart("/api/prescriptions/upload")
                .file(arquivo)
                .param("nome", "João Silva")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Por favor, envie apenas receita médica. O aplicativo aceita somente receitas válidas."));
    }
}
