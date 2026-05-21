package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.OrcamentoProcessadoDTO;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionUploadController.class);

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nome") String clienteNome) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, selecione um arquivo de receita válido.");
        }

        if (clienteNome == null || clienteNome.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, informe o nome do cliente antes de enviar a receita.");
        }

        String contentType = detectContentType(file);

        if (contentType != null && contentType.startsWith("image/")) {
            byte[] imagemBytes = file.getBytes();
            OrcamentoProcessadoDTO resposta = orcamentoService.criarRespostaProcessado(
                    orcamentoService.processarNovaReceita(imagemBytes, clienteNome));
            return ResponseEntity.ok(resposta);
        }

        if (contentType != null && contentType.equals("application/pdf")) {
            try (var pdfStream = file.getInputStream();
                    var doc = org.apache.pdfbox.pdmodel.PDDocument.load(pdfStream)) {
                org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                String texto = stripper.getText(doc);
                if (texto == null || texto.isBlank()) {
                    throw new IllegalArgumentException(
                            "Não foi possível extrair texto do PDF. Verifique o arquivo e tente novamente.");
                }

                var itens = orcamentoService.extrairItensFromText(texto);
                var orcamento = orcamentoService.criarOrcamentoPreliminar(clienteNome, null, itens);
                OrcamentoProcessadoDTO resposta = orcamentoService.criarRespostaProcessado(orcamento);
                return ResponseEntity.ok(resposta);
            }
        }

        if (contentType != null && contentType.startsWith("text/")) {
            String texto = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
            if (texto.isBlank()) {
                throw new IllegalArgumentException(
                        "O arquivo de texto está vazio. Envie um arquivo contendo a receita.");
            }
            var itens = orcamentoService.extrairItensFromText(texto);
            var orcamento = orcamentoService.criarOrcamentoPreliminar(clienteNome, null, itens);
            OrcamentoProcessadoDTO resposta = orcamentoService.criarRespostaProcessado(orcamento);
            return ResponseEntity.ok(resposta);
        }

        logger.warn("Upload rejeitado por tipo de conteúdo não suportado: {}", contentType);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body("Formato não suportado. Envie imagens (png, jpg, jpeg), PDFs ou arquivos de texto.");
    }

    private String detectContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            return contentType;
        }

        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lower.endsWith(".txt")) {
            return "text/plain";
        }
        if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/" + lower.substring(lower.lastIndexOf('.') + 1);
        }

        return contentType;
    }
}
