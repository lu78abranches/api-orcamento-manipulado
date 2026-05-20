package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PriceCalculationEngine {

    // Constante da Taxa Fixa de Manipulação exigida pela regra de negócio
    private static final BigDecimal TAXA_FIXA = BigDecimal.valueOf(10.00);

    // Tabela de preços fictícios para simulação de insumos comuns
    private static final java.util.Map<String, BigDecimal> TABELA_PRECOS = java.util.Map.of(
            "colecalciferol", BigDecimal.valueOf(14.50),
            "mecobalamina", BigDecimal.valueOf(18.20),
            "metilfolato", BigDecimal.valueOf(22.00),
            "paracetamol", BigDecimal.valueOf(8.50),
            "amoxicilina", BigDecimal.valueOf(25.00),
            "dipirona", BigDecimal.valueOf(6.00),
            "vitamina c", BigDecimal.valueOf(12.00),
            "zinco", BigDecimal.valueOf(9.50),
            "magnesio", BigDecimal.valueOf(15.00),
            "melatonina", BigDecimal.valueOf(19.90));

    public BigDecimal calcular(List<ItemOrcamento> itens) {

        BigDecimal somaInsumos = BigDecimal.ZERO;

        for (ItemOrcamento item : itens) {
            BigDecimal preco = item.getPreco();

            if (preco == null || preco.compareTo(BigDecimal.ZERO) == 0) {
                preco = normalizarPreco(item.getNome());
                item.setPreco(preco);
            }

            somaInsumos = somaInsumos.add(preco);
        }

        return somaInsumos.add(TAXA_FIXA);
    }

    public BigDecimal normalizarPreco(String nome) {
        String nomeLimpo = nome != null
                ? nome.toLowerCase().trim()
                : "";

        return TABELA_PRECOS.entrySet().stream()
                .filter(entry -> !nomeLimpo.isBlank() && nomeLimpo.contains(entry.getKey()))
                .map(java.util.Map.Entry::getValue)
                .findFirst()
                .orElseGet(() -> {
                    int tamanho = nomeLimpo.length();
                    double valor = (tamanho * 1.20) + 5.00;
                    return BigDecimal.valueOf(valor)
                            .setScale(2, java.math.RoundingMode.HALF_UP);
                });
    }
}
