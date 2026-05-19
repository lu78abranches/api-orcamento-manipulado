package com.farmacia.api_orcamento_manipulado.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret:minha-chave-secreta-padrao-super-longa-para-o-jwt-da-farmacia-de-manipulacao}")
    private String secret;

    private SecretKey getSigningKey() {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao inicializar o algoritmo SHA-256 para assinatura do JWT", e);
        }
    }

    public String gerarToken(String username) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(key)
                .compact();
    }

    public String getSubject(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
