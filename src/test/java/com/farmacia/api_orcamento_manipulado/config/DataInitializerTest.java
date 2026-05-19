package com.farmacia.api_orcamento_manipulado.config;

import com.farmacia.api_orcamento_manipulado.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class DataInitializerTest {

    @SpyBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve verificar se a base de usuarios foi consultada durante a inicializacao do sistema")
    void deveConsultarBancoNaInicializacao() {
        // Verifica se o método de contagem ou busca foi acionado na subida do contexto
        verify(usuarioRepository, atLeastOnce()).count();
    }
}
