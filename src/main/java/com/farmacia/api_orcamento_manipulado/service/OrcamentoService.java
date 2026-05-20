package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendingReviewDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoApprovedDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoFinalQuoteDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.model.OrcamentoStatus;
import com.farmacia.api_orcamento_manipulado.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final IAReceitaService iaReceitaService;
    private final PriceCalculationEngine priceCalculationEngine;

    public OrcamentoService(OrcamentoRepository repository,
            IAReceitaService iaReceitaService,
            PriceCalculationEngine priceCalculationEngine) {
        this.repository = repository;
        this.iaReceitaService = iaReceitaService;
        this.priceCalculationEngine = priceCalculationEngine;
    }

    public Orcamento processarOrcamento(List<ItemOrcamento> itens) {

        BigDecimal total = priceCalculationEngine.calcular(itens);

        Orcamento orcamento = new Orcamento();
        orcamento.setItens(itens);
        orcamento.setValorTotal(total);
        orcamento.setStatus(OrcamentoStatus.PENDENTE_REVISAO);

        return orcamento;
    }

    public Orcamento processarNovaReceita(byte[] imagem) {
        List<ItemOrcamento> itens = iaReceitaService.extrairItens(imagem);
        return criarOrcamentoPreliminar(null, itens);
    }

    public Orcamento criarOrcamentoPreliminar(String clienteWhatsapp, List<ItemOrcamento> itens) {
        if (itens == null) {
            itens = List.of();
        }

        Orcamento orcamento = new Orcamento();
        orcamento.setClienteWhatsapp(clienteWhatsapp);
        orcamento.setItens(itens);
        orcamento.setValorTotal(priceCalculationEngine.calcular(itens));
        orcamento.setStatus(OrcamentoStatus.PENDENTE_REVISAO);

        return repository.save(orcamento);
    }

    public Orcamento aprovarOrcamento(Long id) {
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado: " + id));

        orcamento.setStatus(OrcamentoStatus.APROVADO);
        if (orcamento.getValorTotal() == null) {
            orcamento.setValorTotal(priceCalculationEngine.calcular(orcamento.getItens()));
        }

        return repository.save(orcamento);
    }

    public Orcamento recusarOrcamento(Long id) {
        Orcamento orcamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado: " + id));

        orcamento.setStatus(OrcamentoStatus.RECUSADO);
        return repository.save(orcamento);
    }

    public List<OrcamentoPendenteDTO> listarPendentes() {
        return repository.findByStatus(OrcamentoStatus.PENDENTE_REVISAO).stream()
                .map(orcamento -> {
                    String status = orcamento.getStatus() == OrcamentoStatus.PENDENTE_REVISAO
                            ? "PENDENTE"
                            : orcamento.getStatus().name();

                    return new OrcamentoPendenteDTO(
                            orcamento.getId(),
                            orcamento.getClienteWhatsapp(),
                            status,
                            orcamento.getValorTotal(),
                            orcamento.getItens().stream()
                                    .map(item -> new ItemExtraidoDTO(item.getNome(), item.getPreco()))
                                    .collect(Collectors.toList()));
                })
                .collect(Collectors.toList());
    }

    // ========== STEP 1: Create Pending Review Response ==========
    /**
     * Creates STEP 1 response: Upload response with protocol, status, and message
     * Pipeline: AI extracts items → PriceCalculationEngine normalizes prices →
     * OrcamentoService → clean DTO
     */
    public OrcamentoPendingReviewDTO criarRespostaPendencia(Orcamento orcamento) {
        return new OrcamentoPendingReviewDTO(
                orcamento.getId(),
                OrcamentoStatus.PENDENTE_REVISAO.name(),
                "Prescrição enviada para análise farmacêutica");
    }

    // ========== STEP 2: Create Approval Response ==========
    /**
     * Creates STEP 2 response: Pharmacist approval (requires JWT)
     */
    public OrcamentoApprovedDTO criarRespostaAprovacao(Orcamento orcamento) {
        return new OrcamentoApprovedDTO(
                OrcamentoStatus.APROVADO.name());
    }

    // ========== STEP 3: Create Final Quote Response ==========
    /**
     * Creates STEP 3 response: Final quote with protocol, status, and total value
     * Data is formatted as Markdown for frontend display
     */
    public OrcamentoFinalQuoteDTO criarRespostaQuotaFinal(Orcamento orcamento) {
        // Ensure total value is calculated
        BigDecimal totalValue = orcamento.getValorTotal();
        if (totalValue == null) {
            totalValue = priceCalculationEngine.calcular(orcamento.getItens());
        }

        // Build Markdown content for frontend display
        String markdownContent = construirMarkdownQuota(orcamento, totalValue);

        return new OrcamentoFinalQuoteDTO(
                orcamento.getId(),
                OrcamentoStatus.APROVADO.name(),
                totalValue,
                markdownContent);
    }

    /**
     * Constructs Markdown formatted quote for clean frontend display
     */
    private String construirMarkdownQuota(Orcamento orcamento, BigDecimal totalValue) {
        StringBuilder md = new StringBuilder();

        md.append("# 💊 Orçamento Aprovado\n\n");
        md.append(String.format("**Protocolo:** %d\n\n", orcamento.getId()));
        md.append(String.format("**Status:** %s\n\n", OrcamentoStatus.APROVADO.name()));

        md.append("## Itens Solicitados\n\n");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemOrcamento item : orcamento.getItens()) {
            BigDecimal preco = item.getPreco() != null ? item.getPreco() : BigDecimal.ZERO;
            md.append(String.format("- **%s:** R$ %.2f\n", item.getNome(), preco));
            subtotal = subtotal.add(preco);
        }

        BigDecimal taxaManipulacao = BigDecimal.valueOf(10.00);

        md.append("\n## Resumo de Valores\n\n");
        md.append(String.format("| Descrição | Valor |\n"));
        md.append(String.format("|-----------|-------|\n"));
        md.append(String.format("| Subtotal dos Insumos | R$ %.2f |\n", subtotal));
        md.append(String.format("| Taxa de Manipulação | R$ %.2f |\n", taxaManipulacao));
        md.append(String.format("| **VALOR TOTAL** | **R$ %.2f** |\n\n", totalValue));

        md.append("✅ Seu orçamento foi aprovado por nosso farmacêutico!\n");
        md.append("🚀 Seu pedido já pode ser enviado para manipulação.\n");
        md.append("📞 Entre em contato para confirmar a entrega.\n");

        return md.toString();
    }

    /**
     * Retrieves an orcamento by ID
     */
    public Orcamento obterOrcamentoPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado: " + id));
    }
}
