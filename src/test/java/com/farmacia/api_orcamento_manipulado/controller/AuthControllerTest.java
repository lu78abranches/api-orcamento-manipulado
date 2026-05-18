package com.farmacia.api_orcamento_manipulado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmacia.api_orcamento_manipulado.dto.LoginRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar o token JWT criptografado real")
    void deveAutenticarComSucesso() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("farmaceutico1", "senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                // Altera a asserção: em vez do texto fixo, valida se a String retornada
                // possui o formato criptográfico de 3 partes de um JWT real (contém pontos)
                .andExpect(jsonPath("$.token").value(org.hamcrest.Matchers.containsString(".")));
    }

}
