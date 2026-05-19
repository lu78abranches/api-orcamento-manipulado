package com.farmacia.api_orcamento_manipulado.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
// Força o Hibernate a criar a tabela com nome explícito em minúsculo e aspas
// para o PostgreSQL
@Table(name = "\"tb_usuarios\"")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "\"username\"")
    private String username;

    @Column(nullable = false, name = "\"password\"")
    private String password;

    @Column(nullable = false, name = "\"role_name\"") // Modificado de role para role_name para evitar conflitos no
                                                      // PostgreSQL
    private String role;
}
