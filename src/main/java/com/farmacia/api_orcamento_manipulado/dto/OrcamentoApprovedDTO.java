package com.farmacia.api_orcamento_manipulado.dto;

/**
 * STEP 2 - Pharmacist Approval Response DTO
 * Response after pharmacist approves the prescription (JWT required)
 */
public record OrcamentoApprovedDTO(
        String status) {
}
