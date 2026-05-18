package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.LoginRequestDTO;
import com.farmacia.api_orcamento_manipulado.dto.LoginResponseDTO;
import com.farmacia.api_orcamento_manipulado.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;

    // Injeção via construtor padronizada no projeto
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        // Validação estática temporária mantida até a criação da entidade Usuario
        if ("farmaceutico1".equals(loginRequest.username()) && "senha123".equals(loginRequest.password())) {

            // Geração do token criptográfico real baseado no username do solicitante
            String jwtReal = tokenService.gerarToken(loginRequest.username());

            LoginResponseDTO response = new LoginResponseDTO(jwtReal, "Bearer");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
