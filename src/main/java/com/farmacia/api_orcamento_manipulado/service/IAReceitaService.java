package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;

import java.util.List;

// src/main/java/com/farmacia/api_orcamento_manipulado/service/IAReceitaService.java
public interface IAReceitaService {
    List<ItemOrcamento> extrairItens(byte[] imagem);
}
