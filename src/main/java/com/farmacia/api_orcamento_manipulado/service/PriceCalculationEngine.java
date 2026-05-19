package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PriceCalculationEngine {

    // Constante da Taxa Fixa de Manipulação exigida pela regra de negócio
    private static final BigDecimal TAXA_FIXA = BigDecimal.valueOf(10.00);

    public BigDecimal calcular(List<ItemOrcamento> itens) {
        if (itens == null || itens.isEmpty()) {
            return TAXA_FIXA;
        }

        // Soma o preço de todos os insumos usando reduce
        BigDecimal somaInsumos = itens.stream()
                .map(ItemOrcamento::getPreco)
                .filter(preco -> preco != null) // Evita NullPointerException se algum item vier sem preço
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Retorna a soma dos insumos + a taxa fixa regulatória de manipulação
        return somaInsumos.add(TAXA_FIXA);
    }
}
