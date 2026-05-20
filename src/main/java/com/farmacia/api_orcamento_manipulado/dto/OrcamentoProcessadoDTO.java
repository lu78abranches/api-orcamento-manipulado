package com.farmacia.api_orcamento_manipulado.dto;

/**
 * STEP 1 - Processed prescription response with Markdown content
 */
public record OrcamentoProcessadoDTO(
        Long protocol,
        String status,
        String cliente,
        String data,
        String markdownContent) {
}
