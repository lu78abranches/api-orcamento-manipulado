package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite que qualquer frontend se conecte à API sem erros de CORS
public class PrescriptionUploadController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPrescription(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, selecione um arquivo de receita válido.");
        }

        try {
            // 1. Transforma o arquivo enviado pelo site em vetor de bytes
            byte[] imagemBytes = file.getBytes();

            // 2. Dispara o fluxo integrado: IA extrai -> Motor calcula -> Salva como
            // PENDENTE no Postgres
            Orcamento novoOrcamento = orcamentoService.processarNovaReceita(imagemBytes);

            // 3. Retorna o orçamento preliminar estruturado para o Dashboard atualizar na
            // tela
            return ResponseEntity.ok(novoOrcamento);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao processar os bytes da imagem: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro no processamento da IA: " + e.getMessage());
        }
    }
}
