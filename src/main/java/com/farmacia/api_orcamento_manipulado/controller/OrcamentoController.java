package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.OrcamentoPendenteDTO;
import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public ResponseEntity<Orcamento> aprovarOrcamento(@PathVariable Long id) {
        Orcamento orcamentoAprovado = orcamentoService.aprovarOrcamento(id);
        return ResponseEntity.ok(orcamentoAprovado);
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
