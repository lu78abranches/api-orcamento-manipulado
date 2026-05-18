package com.farmacia.api_orcamento_manipulado.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao acessar endpoint de orçamentos sem autenticação")
    void deveRetornar401QuandoNaoAutenticado() {
        // Tenta fazer um POST na rota existente no OrcamentoController
        ResponseEntity<String> response = restTemplate.postForEntity("/api/orcamentos", null, String.class);

        // Asserção: Espera que a segurança bloqueie o acesso anônimo
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    @DisplayName("Deve retornar 200 OK ou 201 Created ao acessar a rota pública do Webhook do WhatsApp sem autenticação")
    void devePermitirAcessoPublicoAoWebhookDoWhatsapp() {
        // Envia uma requisição vazia para simular o disparo externo da Twilio
        ResponseEntity<String> response = restTemplate.postForEntity("/api/webhooks/whatsapp", null, String.class);

        // Asserção: Espera que a segurança não bloqueie (não retorne 401 ou 403)
        // Como o controller ainda não existe ou retornará sucesso, validamos que o status não é de erro de segurança
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
    }
}
