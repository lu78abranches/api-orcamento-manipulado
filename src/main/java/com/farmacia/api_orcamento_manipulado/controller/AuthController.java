package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.LoginRequestDTO;
import com.farmacia.api_orcamento_manipulado.dto.LoginResponseDTO;
import com.farmacia.api_orcamento_manipulado.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    // Injeção de dependências robusta via construtor
    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // 1. Cria o token de autenticação não verificado com os dados vindos do DTO
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.username(), loginRequest.password());

            // 2. O manager chama o UserDetailsService, descriptografa com BCrypt e valida
            // as credenciais
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. Se passou pela validação, gera o token JWT criptografado baseado no
            // Principal autenticado
            String jwt = tokenService.gerarToken(authentication.getName());

            return ResponseEntity.ok(new LoginResponseDTO(jwt, "Bearer"));

        } catch (Exception e) {
            // Em caso de credenciais inválidas (BadCredentialsException), retorna 401
            // Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
