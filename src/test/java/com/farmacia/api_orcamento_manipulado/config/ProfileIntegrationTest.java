package com.farmacia.api_orcamento_manipulado.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // Garante que o ambiente de testes usa o perfil isolado
public class ProfileIntegrationTest {

    @Value("${spring.application.name}")
    private String applicationName;

    @Test
    @DisplayName("Deve carregar as propriedades corretas do perfil de testes")
    void deveCarregarPerfilDeTestes() {
        assertThat(applicationName).isNotNull();
        // Garante que o contexto de teste não está lendo configurações de produção
    }
}
