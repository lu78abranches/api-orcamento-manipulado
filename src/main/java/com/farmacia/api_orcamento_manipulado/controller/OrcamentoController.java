package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendingReviewDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoApprovedDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoFinalQuoteDTO;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoAprovadoResponseDTO;
import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import java.math.BigDecimal;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    // ========== STEP 1: Upload Prescription ==========
    /**
     * Upload prescription image and create pending review
     * Returns protocol, status "PENDING_REVIEW", and message
     */
    @PostMapping("/upload")
    public ResponseEntity<OrcamentoPendingReviewDTO> criarOrcamentoPorImagem(
            @RequestParam("imagem") MultipartFile arquivo)
            throws IOException {
        // AI extracts items from prescription image
        // PriceCalculationEngine normalizes prices
        // OrcamentoService creates clean DTO for frontend
        Orcamento orcamento = orcamentoService.processarNovaReceita(arquivo.getBytes());

        // Convert to STEP 1 response DTO
        OrcamentoPendingReviewDTO response = orcamentoService.criarRespostaPendencia(orcamento);

        return ResponseEntity.ok(response);
    }

    // ========== STEP 2: Pharmacist Approves (JWT Required) ==========
    /**
     * Approve prescription by pharmacist (requires JWT authentication)
     * Returns simple status "APPROVED"
     */
    @PutMapping("/{id}/aprovar")
    public ResponseEntity<OrcamentoApprovedDTO> aprovarOrcamento(@PathVariable Long id) {
        // This endpoint requires JWT - secured by SecurityConfig
        Orcamento orcamento = orcamentoService.aprovarOrcamento(id);

        // Convert to STEP 2 response DTO
        OrcamentoApprovedDTO response = orcamentoService.criarRespostaAprovacao(orcamento);

        return ResponseEntity.ok(response);
    }

    // ========== STEP 3: Generate Final Quote ==========
    /**
     * Generate final quote with complete details
     * Returns protocol, status "APPROVED", and total_value
     * Data is formatted as Markdown for frontend display
     */
    @GetMapping("/{id}/final-quote")
    public ResponseEntity<OrcamentoFinalQuoteDTO> obterQuotaFinal(@PathVariable Long id) {
        // This endpoint requires JWT - secured by SecurityConfig
        Orcamento orcamento = orcamentoService.obterOrcamentoPorId(id);

        // Verify that the quote is approved before generating final quote
        if (!orcamento.getStatus().name().equals("APROVADO")) {
            throw new IllegalArgumentException("Orçamento não foi aprovado");
        }

        // Convert to STEP 3 response DTO with Markdown content
        OrcamentoFinalQuoteDTO response = orcamentoService.criarRespostaQuotaFinal(orcamento);

        return ResponseEntity.ok(response);
    }

    // ========== Additional Endpoints ==========

    @PutMapping("/{id}/recusar")
    public ResponseEntity<Orcamento> recusarOrcamento(@PathVariable Long id) {
        Orcamento orcamentoRecusado = orcamentoService.recusarOrcamento(id);
        return ResponseEntity.ok(orcamentoRecusado);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<OrcamentoPendenteDTO>> listarPendentes() {
        List<OrcamentoPendenteDTO> pendentes = orcamentoService.listarPendentes();
        return ResponseEntity.ok(pendentes);
    }

}
