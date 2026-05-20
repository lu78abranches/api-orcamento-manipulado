package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.dto.OrcamentoProcessadoDTO;
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
    public ResponseEntity<?> uploadPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nome") String clienteNome) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, selecione um arquivo de receita válido.");
        }

        if (clienteNome == null || clienteNome.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, informe o nome do cliente antes de enviar a receita.");
        }

        try {
            byte[] imagemBytes = file.getBytes();
            OrcamentoProcessadoDTO resposta = orcamentoService.criarRespostaProcessado(
                    orcamentoService.processarNovaReceita(imagemBytes, clienteNome));
            return ResponseEntity.ok(resposta);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao processar os bytes da imagem: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro no processamento da IA: " + e.getMessage());
        }
    }
}
