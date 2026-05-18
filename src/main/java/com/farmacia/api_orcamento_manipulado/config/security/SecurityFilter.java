package com.farmacia.api_orcamento_manipulado.config.security;

import com.farmacia.api_orcamento_manipulado.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);

        if (token != null) {
            try {
                // Valida o token e extrai o username (subject)
                String subject = tokenService.getSubject(token);

                // Cria o objeto de autenticação do Spring Security (User estático provisório
                // sem Roles)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject,
                        null, Collections.emptyList());

                // Força a autenticação no contexto do Spring para esta requisição
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Se o token for inválido ou expirado, não autentica e deixa o Spring Security
                // bloquear
            }
        }

        // Continua o fluxo normal da requisição na cadeia de filtros
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
