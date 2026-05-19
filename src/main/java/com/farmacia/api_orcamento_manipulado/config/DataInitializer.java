package com.farmacia.api_orcamento_manipulado.config;

import com.farmacia.api_orcamento_manipulado.model.Usuario;
import com.farmacia.api_orcamento_manipulado.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Se não houver nenhum usuário cadastrado (comum no primeiro deploy no Render)
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("farmaceutico1");

            // Grava a senha "senha123" criptografada com hash seguro BCrypt
            admin.setPassword(passwordEncoder.encode("senha123"));
            admin.setRole("FARMACEUTICO");

            usuarioRepository.save(admin);
            System.out.println(">>> SEED: Usuário padrão 'farmaceutico1' criado com sucesso para testes de produção!");
        }
    }
}
