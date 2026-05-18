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
    @DisplayName("Deve autenticar com sucesso e retornar o token JWT")
    void deveAutenticarComSucesso() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("farmaceutico1", "senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                // Garante que a estrutura básica do DTO de resposta está correta
                .andExpect(jsonPath("$.token").value("mocked-jwt-token-string-para-testes"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

}
