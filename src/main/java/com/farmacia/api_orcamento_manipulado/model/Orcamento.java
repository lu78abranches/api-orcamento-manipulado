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
    private String status = "PENDENTE_REVISAO";

    // O método getStatus() manual foi removido! O @Getter faz o trabalho.



    /*Abaixo, codigo criado antes de criar Entidade JPA e usar o Lombok

     Criamos o método, mas retornamos null ou vazio para o teste falhar no valor
    public String getStatus() {
        return null;
    }
    // 1. Definimos o valor que o teste espera
    private String status = "PENDENTE_REVISAO";

    // 2. Retornamos esse valor no método
    public String getStatus() {
        return this.status;
    }*/

    /* Adicione os campos e métodos na classe Orcamento
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private static final BigDecimal TAXA_MANIPULACAO = new BigDecimal("10.00");

    public void adicionarItem(String nome, BigDecimal preco) {
        // Lógica simples para fazer o teste passar
        if (this.valorTotal.equals(BigDecimal.ZERO)) {
            this.valorTotal = this.valorTotal.add(TAXA_MANIPULACAO);
        }
        this.valorTotal = this.valorTotal.add(preco);
    }

    public BigDecimal getValorTotal() {
        return this.valorTotal;
    }*/

    @OneToMany(cascade = CascadeType.ALL)
    private List<ItemOrcamento> itens = new ArrayList<>();

    public void adicionarItem(ItemOrcamento item) {
        this.itens.add(item);
    }

    public BigDecimal getValorTotal() {
        BigDecimal somaItens = itens.stream()
                .map(ItemOrcamento::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return somaItens.add(new BigDecimal("10.00")); // Taxa fixa
    }


}
