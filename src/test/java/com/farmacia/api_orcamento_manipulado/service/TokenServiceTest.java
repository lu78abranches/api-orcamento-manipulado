package com.farmacia.api_orcamento_manipulado.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        // Simula a injeção da assinatura secreta do JWT via properties do Spring
        // (@Value)
        ReflectionTestUtils.setField(tokenService, "secret",
                "minha-chave-secreta-super-protegida-e-longa-para-o-jwt-da-farmacia");
    }

    @Test
    @DisplayName("Deve gerar um token JWT criptografado contendo o username do usuario")
    void deveGerarTokenValido() {
        String username = "farmaceutico1";

        String token = tokenService.gerarToken(username);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();

        // Valida que o token gerado pode ser descriptografado e contém o username
        // original
        String usernameExtraido = tokenService.getSubject(token);
        assertThat(usernameExtraido).isEqualTo(username);
    }
}
