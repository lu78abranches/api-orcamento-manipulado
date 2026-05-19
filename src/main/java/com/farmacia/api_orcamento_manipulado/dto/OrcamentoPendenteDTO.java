package com.farmacia.api_orcamento_manipulado.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrcamentoPendenteDTO(
        Long id,
        String clienteWhatsapp,
        String status,
        BigDecimal valorTotalEstimado,
        List<ItemExtraidoDTO> itens) {
}
