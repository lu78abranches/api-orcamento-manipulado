package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendingReviewDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoApprovedDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoFinalQuoteDTO;
import com.farmacia.api_orcamento_manipulado.dto.OrcamentoProcessadoDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        normalizarItens(itens);

        BigDecimal total = priceCalculationEngine.calcular(itens);

        Orcamento orcamento = new Orcamento();
        orcamento.setItens(itens);
        orcamento.setValorTotal(total);
        orcamento.setStatus(OrcamentoStatus.PENDENTE_REVISAO);

        return orcamento;
    }

    public Orcamento processarNovaReceita(byte[] imagem, String clienteNome) {
        List<ItemOrcamento> itens = iaReceitaService.extrairItens(imagem);
        return criarOrcamentoPreliminar(clienteNome, null, itens);
    }

    public Orcamento processarNovaReceita(byte[] imagem) {
        return processarNovaReceita(imagem, null);
    }

    public Orcamento criarOrcamentoPreliminar(String clienteNome, String clienteWhatsapp, List<ItemOrcamento> itens) {
        if (itens == null) {
            itens = List.of();
        }

        normalizarItens(itens);

        Orcamento orcamento = new Orcamento();
        orcamento.setClienteNome(clienteNome);
        orcamento.setClienteWhatsapp(clienteWhatsapp);
        orcamento.setItens(itens);
        orcamento.setValorTotal(priceCalculationEngine.calcular(itens));
        orcamento.setStatus(OrcamentoStatus.PENDENTE_REVISAO);

        return repository.save(orcamento);
    }

    public Orcamento criarOrcamentoPreliminar(String clienteWhatsapp, List<ItemOrcamento> itens) {
        return criarOrcamentoPreliminar(null, clienteWhatsapp, itens);
    }

    private void normalizarItens(List<ItemOrcamento> itens) {
        for (ItemOrcamento item : itens) {
            if (item.getPreco() == null || item.getPreco().compareTo(BigDecimal.ZERO) == 0) {
                item.setPreco(priceCalculationEngine.normalizarPreco(item.getNome()));
            }
        }
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

    public OrcamentoProcessadoDTO criarRespostaProcessado(Orcamento orcamento) {
        String cliente = orcamento.getClienteNome() != null && !orcamento.getClienteNome().isBlank()
                ? orcamento.getClienteNome()
                : "Cliente não informado";

        String status = switch (orcamento.getStatus()) {
            case PENDENTE_REVISAO -> "Em análise farmacêutica";
            case APROVADO -> "Aprovado";
            case RECUSADO -> "Recusado";
            default -> orcamento.getStatus().name();
        };

        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        BigDecimal totalValue = orcamento.getValorTotal();
        if (totalValue == null) {
            totalValue = priceCalculationEngine.calcular(orcamento.getItens());
        }

        String markdownContent = construirMarkdownProcessado(orcamento, cliente, status, data, totalValue);

        return new OrcamentoProcessadoDTO(
                orcamento.getId(),
                status,
                cliente,
                data,
                markdownContent);
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
        String cliente = orcamento.getClienteNome() != null && !orcamento.getClienteNome().isBlank()
                ? orcamento.getClienteNome()
                : "Cliente não informado";

        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String status = "Aprovado";

        StringBuilder md = new StringBuilder();

        md.append("# 💊 Orçamento de Manipulação\n\n");
        md.append("## ✅ Orçamento aprovado com sucesso\n\n");
        md.append(String.format("**Protocolo:** #%d  \n", orcamento.getId()));
        md.append(String.format("**Status:** %s  \n", status));
        md.append(String.format("**Cliente:** %s  \n", cliente));
        md.append(String.format("**Data:** %s\n\n", data));
        md.append("---\n\n");
        md.append("## 🧾 Itens identificados na receita\n\n");
        md.append("| Medicamento | Valor |\n");
        md.append("|---|---:|\n");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemOrcamento item : orcamento.getItens()) {
            BigDecimal preco = item.getPreco() != null ? item.getPreco() : BigDecimal.ZERO;
            md.append(String.format("| %s | R$ %.2f |\n", item.getNome(), preco));
            subtotal = subtotal.add(preco);
        }

        BigDecimal taxaManipulacao = BigDecimal.valueOf(10.00);

        md.append("\n---\n\n");
        md.append("## 💰 Resumo financeiro\n\n");
        md.append("| Descrição | Valor |\n");
        md.append("|---|---:|\n");
        md.append(String.format("| Subtotal dos insumos | R$ %.2f |\n", subtotal));
        md.append(String.format("| Taxa de manipulação | R$ %.2f |\n", taxaManipulacao));
        md.append(String.format("| **Total estimado** | **R$ %.2f** |\n\n", totalValue));
        md.append("---\n\n");
        md.append("## 📌 Observações\n\n");
        md.append("- Este orçamento foi gerado automaticamente por IA.\n");
        md.append("- Os valores podem sofrer ajustes após validação farmacêutica.\n");
        md.append("- Você receberá a confirmação final via WhatsApp.\n\n");
        md.append("---\n\n");
        md.append("## 🚀 Próximos passos\n\n");
        md.append("✅ Receita recebida  \n");
        md.append("⏳ Revisão farmacêutica em andamento  \n");
        md.append("📲 Aprovação e envio do pagamento via WhatsApp\n\n");
        md.append("---\n\n");
        md.append("### 🏥 Farmácia Magistral AI\n");
        md.append("Sistema inteligente de pré-orçamento farmacêutico\n");

        return md.toString();
    }

    private String construirMarkdownProcessado(Orcamento orcamento, String cliente, String status, String data,
            BigDecimal totalValue) {
        StringBuilder md = new StringBuilder();

        md.append("# 💊 Orçamento de Manipulação\n\n");
        md.append("## ✅ Receita processada com sucesso\n\n");
        md.append(String.format("**Protocolo:** #%d  \n", orcamento.getId()));
        md.append(String.format("**Status:** %s  \n", status));
        md.append(String.format("**Cliente:** %s  \n", cliente));
        md.append(String.format("**Data:** %s\n\n", data));
        md.append("---\n\n");
        md.append("## 🧾 Itens identificados na receita\n\n");
        md.append("| Medicamento | Valor |\n");
        md.append("|---|---:|\n");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemOrcamento item : orcamento.getItens()) {
            BigDecimal preco = item.getPreco() != null ? item.getPreco() : BigDecimal.ZERO;
            md.append(String.format("| %s | R$ %.2f |\n", item.getNome(), preco));
            subtotal = subtotal.add(preco);
        }

        BigDecimal taxaManipulacao = BigDecimal.valueOf(10.00);

        md.append("\n---\n\n");
        md.append("## 💰 Resumo financeiro\n\n");
        md.append("| Descrição | Valor |\n");
        md.append("|---|---:|\n");
        md.append(String.format("| Subtotal dos insumos | R$ %.2f |\n", subtotal));
        md.append(String.format("| Taxa de manipulação | R$ %.2f |\n", taxaManipulacao));
        md.append(String.format("| **Total estimado** | **R$ %.2f** |\n\n", totalValue));
        md.append("---\n\n");
        md.append("## 📌 Observações\n\n");
        md.append("- Este orçamento foi gerado automaticamente por IA.\n");
        md.append("- Os valores podem sofrer ajustes após validação farmacêutica.\n");
        md.append("- Você receberá a confirmação final via WhatsApp.\n\n");
        md.append("---\n\n");
        md.append("## 🚀 Próximos passos\n\n");
        md.append("✅ Receita recebida  \n");
        md.append("⏳ Revisão farmacêutica em andamento  \n");
        md.append("📲 Aprovação e envio do pagamento via WhatsApp\n\n");
        md.append("---\n\n");
        md.append("### 🏥 Farmácia Magistral AI\n");
        md.append("Sistema inteligente de pré-orçamento farmacêutico\n");

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
