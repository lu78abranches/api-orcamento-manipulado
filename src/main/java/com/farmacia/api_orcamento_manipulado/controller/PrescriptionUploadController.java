package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.OrcamentoProcessadoDTO;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import com.farmacia.api_orcamento_manipulado.service.IAReceitaService;
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
    private final IAReceitaService iaReceitaService;
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

        String contentType = file.getContentType();

        try {
            if (contentType != null && contentType.startsWith("image/")) {
                byte[] imagemBytes = file.getBytes();
                OrcamentoProcessadoDTO resposta = orcamentoService.criarRespostaProcessado(
                        orcamentoService.processarNovaReceita(imagemBytes, clienteNome));
                return ResponseEntity.ok(resposta);
            }

            if (contentType != null && contentType.equals("application/pdf")) {
                // Extract text from PDF
                try (var pdfStream = file.getInputStream()) {
                    org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.pdmodel.PDDocument.load(pdfStream);
                    org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                    String texto = stripper.getText(doc);
                    doc.close();

                    var itens = orcamentoService.iaReceitaService.extrairItensFromText(texto);
                    var orcamento = orcamentoService.criarOrcamentoPreliminar(clienteNome, null, itens);
                    OrcamentoProcessadoDTO resposta = orcamentoService.criarRespostaProcessado(orcamento);
                    return ResponseEntity.ok(resposta);
                }
            }

            if (contentType != null && contentType.startsWith("text/")) {
                String texto = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
                var itens = orcamentoService.iaReceitaService.extrairItensFromText(texto);
                var orcamento = orcamentoService.criarOrcamentoPreliminar(clienteNome, null, itens);
                OrcamentoProcessadoDTO resposta = orcamentoService.criarRespostaProcessado(orcamento);
                return ResponseEntity.ok(resposta);
            }

            logger.warn("Upload rejeitado por tipo de conteúdo não suportado: {}", contentType);
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Formato não suportado. Envie imagens (png, jpg, jpeg), PDFs ou arquivos de texto.");

        } catch (IOException e) {
            logger.error("Erro ao ler bytes do arquivo enviado", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro no processamento da IA ou criação do orçamento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro no processamento da IA: " + e.getMessage());
        }
    }
}
