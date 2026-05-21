package com.farmacia.api_orcamento_manipulado.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * STEP 1 - Processed prescription response with structured fields and Markdown report
 */
public record OrcamentoProcessadoDTO(
        Long protocol,
        String status,
        String cliente,
        String data,
        BigDecimal budget,
        List<String> medications,
        String markdownContent) {
}
