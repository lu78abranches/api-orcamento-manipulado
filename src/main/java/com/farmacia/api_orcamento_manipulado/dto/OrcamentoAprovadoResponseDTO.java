package com.farmacia.api_orcamento_manipulado.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrcamentoAprovadoResponseDTO(
    Long id,
    String status,
    String clienteWhatsapp,
    List<ItemExtraidoDTO> itens,
    BigDecimal subtotalItens,
    BigDecimal taxaManipulacao,
    BigDecimal valorTotal,
    String mensagemCliente
) {}
