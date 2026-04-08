package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// src/main/java/com/farmacia/api_orcamento_manipulado/service/OpenAIReceitaService.java
@Service
public class OpenAIReceitaService implements IAReceitaService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Override
    public List<ItemOrcamento> extrairItens(byte[] imagem) {
        // Por enquanto, vamos deixar a estrutura pronta.
        // Aqui dentro faremos a chamada HTTP enviando a imagem em Base64.

        System.out.println("Chamando OpenAI com a chave: " + apiKey.substring(0, 5) + "...");

        // Simulação de retorno para não travar seu fluxo antes de você ter a chave
        return List.of(new ItemOrcamento("Item via IA Real", new BigDecimal("50.00")));
    }
}

