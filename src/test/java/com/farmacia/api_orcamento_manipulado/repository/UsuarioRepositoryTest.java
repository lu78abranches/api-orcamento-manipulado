package com.farmacia.api_orcamento_manipulado.repository;

import com.farmacia.api_orcamento_manipulado.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    @DisplayName("Deve encontrar um usuario cadastrado atraves do username")
    void deveEncontrarUsuarioPorUsername() {
        Usuario usuario = new Usuario();
        usuario.setUsername("farmaceutico1");
        usuario.setPassword("senhaCriptografada123");
        usuario.setRole("FARMACEUTICO");

        repository.save(usuario);

        Optional<Usuario> resultado = repository.findByUsername("farmaceutico1");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsername()).isEqualTo("farmaceutico1");
    }
}
