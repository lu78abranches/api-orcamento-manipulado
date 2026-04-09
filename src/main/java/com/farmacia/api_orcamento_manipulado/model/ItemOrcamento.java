package com.farmacia.api_orcamento_manipulado.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class ItemOrcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private BigDecimal preco;



    // Construtor conveniente para o teste
    public ItemOrcamento(String nome, BigDecimal preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public ItemOrcamento() {} // Necessário para o JPA
}

