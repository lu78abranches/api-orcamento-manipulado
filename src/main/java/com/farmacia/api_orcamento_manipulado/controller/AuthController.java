package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.LoginRequestDTO;
import com.farmacia.api_orcamento_manipulado.dto.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        // Validação estática temporária para simular o fluxo de sucesso
        if ("farmaceutico1".equals(loginRequest.username()) && "senha123".equals(loginRequest.password())) {
            // Mock de um token JWT fictício estruturado
            LoginResponseDTO response = new LoginResponseDTO("mocked-jwt-token-string-para-testes", "Bearer");
            return ResponseEntity.ok(response);
        }

        // Retorna 401 caso as credenciais simuladas falhem
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
