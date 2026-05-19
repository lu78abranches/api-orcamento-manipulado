package com.farmacia.api_orcamento_manipulado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmacia.api_orcamento_manipulado.dto.LoginRequestDTO;
import com.farmacia.api_orcamento_manipulado.model.Usuario;
import com.farmacia.api_orcamento_manipulado.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SuppressWarnings("removal")
    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve autenticar dinamicamente com credenciais validas do banco e retornar o token JWT")
    void deveAutenticarComSucesso() throws Exception {
        String username = "farmaceutico1";
        String senhaPura = "senha123";
        // Criptografa a senha para simular como ela estaria gravada no banco de dados
        String senhaCriptografada = passwordEncoder.encode(senhaPura);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsername(username);
        usuarioMock.setPassword(senhaCriptografada);
        usuarioMock.setRole("FARMACEUTICO");

        // Simula o retorno do banco de dados relacional
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuarioMock));

        LoginRequestDTO loginRequest = new LoginRequestDTO(username, senhaPura);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.token").value(org.hamcrest.Matchers.containsString(".")));
    }
}
