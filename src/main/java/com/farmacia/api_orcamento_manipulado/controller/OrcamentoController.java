package com.farmacia.api_orcamento_manipulado.controller;

// src/main/java/com/farmacia/api_orcamento_manipulado/controller/OrcamentoController.java

import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/upload")
    public ResponseEntity<Orcamento> criarOrcamentoPorImagem(@RequestParam("imagem") MultipartFile arquivo) throws IOException {
        // Transformamos o arquivo em bytes para enviar ao serviço
        Orcamento orcamento = orcamentoService.processarNovaReceita(arquivo.getBytes());

        return ResponseEntity.ok(orcamento);
    }
}

