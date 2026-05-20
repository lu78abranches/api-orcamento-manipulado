package com.farmacia.api_orcamento_manipulado.dto;

import java.math.BigDecimal;

/**
 * STEP 3 - Final Quote Response DTO
 * Response with complete quote details including total value
 * Formatted as Markdown for frontend display
 */
public record OrcamentoFinalQuoteDTO(
        Long protocol,
        String status,
        BigDecimal totalValue,
        String markdownContent) {
}
