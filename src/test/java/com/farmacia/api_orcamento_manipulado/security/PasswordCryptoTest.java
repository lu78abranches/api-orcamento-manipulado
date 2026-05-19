package com.farmacia.api_orcamento_manipulado.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PasswordCryptoTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Deve criptografar a senha com BCrypt e validar que o hash gerado é diferente da senha pura")
    void deveCriptografarComBCrypt() {
        String senhaPura = "senhaFarmacia123";

        // Criptografa
        String senhaCriptografada = passwordEncoder.encode(senhaPura);

        // Asserções
        assertThat(senhaCriptografada).isNotBlank();
        assertThat(senhaCriptografada).isNotEqualTo(senhaPura);

        // Valida que o encoder consegue verificar a senha original contra o Hash gerado
        assertThat(passwordEncoder.matches(senhaPura, senhaCriptografada)).isTrue();
    }
}
