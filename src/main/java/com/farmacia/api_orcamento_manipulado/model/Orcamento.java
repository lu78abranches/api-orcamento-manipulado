package com.farmacia.api_orcamento_manipulado.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity // Diz ao JPA que esta classe é uma tabela no banco
@Getter // O Lombok cria todos os métodos get (inclusive o getStatus())
@Setter // O Lombok cria todos os métodos set

public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Definimos o valor padrão diretamente no atributo
    @Enumerated(EnumType.STRING)
    private OrcamentoStatus status = OrcamentoStatus.PENDENTE_REVISAO;

    private String clienteWhatsapp;

    private BigDecimal valorTotal;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ItemOrcamento> itens = new ArrayList<>();

    public void adicionarItem(ItemOrcamento item) {
        this.itens.add(item);
    }

    public BigDecimal getValorTotal() {
        if (this.valorTotal != null) {
            return this.valorTotal;
        }
        BigDecimal somaItens = itens.stream()
                .map(ItemOrcamento::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return somaItens.add(new BigDecimal("10.00")); // Taxa fixa
    }

    public void setStatus(OrcamentoStatus status) {
        this.status = status;
    }
}
