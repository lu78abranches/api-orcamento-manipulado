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
            "melatonina", BigDecimal.valueOf(19.90)
    );

    public BigDecimal calcular(List<ItemOrcamento> itens) {
        if (itens == null || itens.isEmpty()) {
            return TAXA_FIXA;
        }

        BigDecimal somaInsumos = BigDecimal.ZERO;

        for (ItemOrcamento item : itens) {
            BigDecimal preco = item.getPreco();

            // Se o preço for nulo ou zero (caso em que a IA não extraiu valor do papel), definimos um valor fictício realista
            if (preco == null || preco.compareTo(BigDecimal.ZERO) == 0) {
                String nomeLimpo = item.getNome() != null ? item.getNome().toLowerCase().trim() : "";
                
                // Busca se há correspondência na nossa tabela de preços fictícios
                BigDecimal precoFicticio = TABELA_PRECOS.entrySet().stream()
                        .filter(entry -> nomeLimpo.contains(entry.getKey()))
                        .map(java.util.Map.Entry::getValue)
                        .findFirst()
                        .orElseGet(() -> {
                            // Caso não esteja na tabela, gera um preço determinístico baseado no tamanho do nome do insumo
                            int tamanho = item.getNome() != null ? item.getNome().length() : 10;
                            double valor = (tamanho * 1.20) + 5.00;
                            return BigDecimal.valueOf(valor).setScale(2, java.math.RoundingMode.HALF_UP);
                        });

                item.setPreco(precoFicticio);
                preco = precoFicticio;
            }

            somaInsumos = somaInsumos.add(preco);
        }

        // Retorna a soma dos insumos + a taxa fixa regulatória de manipulação
        return somaInsumos.add(TAXA_FIXA);
    }
}
