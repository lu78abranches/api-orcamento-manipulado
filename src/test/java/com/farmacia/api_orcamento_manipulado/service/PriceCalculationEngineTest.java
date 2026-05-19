package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceCalculationEngineTest {

    private PriceCalculationEngine calculationEngine;

    @BeforeEach
    void setUp() {
        calculationEngine = new PriceCalculationEngine();
    }

    @Test
    @DisplayName("Deve calcular o valor total somando os itens e aplicando a taxa fixa de 10 reais")
    void deveCalcularValorTotalComTaxaFixa() {
        // Arranjo (Given)
        ItemOrcamento item1 = new ItemOrcamento();
        item1.setNome("Princípio Ativo A");
        item1.setPreco(BigDecimal.valueOf(25.50));

        ItemOrcamento item2 = new ItemOrcamento();
        item2.setNome("Princípio Ativo B");
        item2.setPreco(BigDecimal.valueOf(14.32));

        List<ItemOrcamento> itens = List.of(item1, item2);

        // Ação (When)
        // Cálculo esperado: 25.50 + 14.32 + 10.00 = 49.82
        BigDecimal valorTotal = calculationEngine.calcular(itens);

        // Asserção (Then)
        assertThat(valorTotal).isEqualByComparingTo(BigDecimal.valueOf(49.82));
    }

    @Test
    @DisplayName("Deve retornar apenas a taxa fixa de 10 reais se a lista de itens estiver vazia")
    void deveRetornarTaxaFixaSeListaVazia() {
        BigDecimal valorTotal = calculationEngine.calcular(List.of());
        assertThat(valorTotal).isEqualByComparingTo(BigDecimal.valueOf(10.00));
    }
}
