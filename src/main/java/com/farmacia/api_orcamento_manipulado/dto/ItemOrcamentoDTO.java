package com.farmacia.api_orcamento_manipulado.dto;

import java.math.BigDecimal;

public class ItemOrcamentoDTO {
    private String nome;
    private BigDecimal preco;

    public ItemOrcamentoDTO() {
    }

    public ItemOrcamentoDTO(String nome, BigDecimal preco) {
        this.nome = nome;
        this.preco = preco;
    }

    // getters e setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
}
