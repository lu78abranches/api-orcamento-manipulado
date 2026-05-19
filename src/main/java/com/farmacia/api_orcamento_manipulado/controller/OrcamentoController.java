package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
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
import java.util.List; // <--- Make sure this is imported!

@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/upload")
    public ResponseEntity<Orcamento> criarOrcamentoPorImagem(@RequestParam("imagem") MultipartFile arquivo)
            throws IOException {
        // Transformamos o arquivo em bytes para enviar ao serviço
        Orcamento orcamento = orcamentoService.processarNovaReceita(arquivo.getBytes());

        return ResponseEntity.ok(orcamento);
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<OrcamentoAprovadoResponseDTO> aprovarOrcamento(@PathVariable Long id) {
        Orcamento orcamento = orcamentoService.aprovarOrcamento(id);

        // Calcula o subtotal dos itens
        BigDecimal subtotal = orcamento.getItens().stream()
                .map(item -> item.getPreco() != null ? item.getPreco() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxaManipulacao = BigDecimal.valueOf(10.00);
        BigDecimal total = orcamento.getValorTotal();

        // Converte os itens para DTO
        List<ItemExtraidoDTO> itensDTO = orcamento.getItens().stream()
                .map(item -> new ItemExtraidoDTO(item.getNome(), item.getPreco() != null ? item.getPreco() : BigDecimal.ZERO))
                .toList();

        // Constrói a mensagem amigável e legível para o cliente
        StringBuilder sb = new StringBuilder();
        sb.append("Olá! Seu orçamento foi revisado e aprovado com sucesso por nosso farmacêutico. 💊\n\n");
        sb.append("Detalhamento dos valores:\n");
        sb.append("----------------------------------------\n");
        for (ItemExtraidoDTO item : itensDTO) {
            sb.append(String.format("- %s: R$ %,.2f\n", item.nome(), item.preco()));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal dos Insumos: R$ %,.2f\n", subtotal));
        sb.append(String.format("Taxa de Manipulação: R$ %,.2f\n", taxaManipulacao));
        sb.append("----------------------------------------\n");
        sb.append(String.format("VALOR TOTAL: R$ %,.2f\n\n", total));
        sb.append("Seu pedido já pode ser enviado para a manipulação! Entre em contato para confirmar a entrega. 🚀");

        OrcamentoAprovadoResponseDTO response = new OrcamentoAprovadoResponseDTO(
                orcamento.getId(),
                orcamento.getStatus(),
                orcamento.getClienteWhatsapp(),
                itensDTO,
                subtotal,
                taxaManipulacao,
                total,
                sb.toString()
        );

        return ResponseEntity.ok(response);
    }

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
