package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;

import java.util.List;

// src/main/java/com/farmacia/api_orcamento_manipulado/service/IAReceitaService.java
//Strategy Pattern: define uma "estratégia" para extrair dados da receita. O OrcamentoService não sabe como a IA funciona,
// ele apenas sabe que ela entrega uma lista de itens. Isso permite trocar o "algoritmo"
// (OpenAI, Google Vision, AWS Textract) facilmente.
public interface IAReceitaService {
    List<ItemOrcamento> extrairItens(byte[] imagem);
}
