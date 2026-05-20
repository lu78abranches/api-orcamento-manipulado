package com.farmacia.api_orcamento_manipulado.dto;

/**
 * STEP 1 - Upload Response DTO
 * Response after prescription upload and initial analysis
 */
public record OrcamentoPendingReviewDTO(
        Long protocol,
        String status,
        String message) {
}
