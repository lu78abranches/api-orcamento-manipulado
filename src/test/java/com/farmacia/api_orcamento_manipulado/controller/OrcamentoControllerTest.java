package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// src/test/java/com/farmacia/api_orcamento_manipulado/controller/OrcamentoControllerTest.java
@WebMvcTest(OrcamentoController.class)
public class OrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrcamentoService orcamentoService;

    @Test
    void deveAceitarUploadDeImagem() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "imagem", "receita.jpg", "image/jpeg", "conteudo".getBytes());

        mockMvc.perform(multipart("/api/orcamentos/upload").file(arquivo))
                .andExpect(status().isOk());
    }
}

