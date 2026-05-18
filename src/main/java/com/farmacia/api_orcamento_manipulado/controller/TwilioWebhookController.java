package com.farmacia.api_orcamento_manipulado.controller;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.farmacia.api_orcamento_manipulado.service.IAReceitaService;
import com.farmacia.api_orcamento_manipulado.service.OrcamentoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class TwilioWebhookController {

    private final IAReceitaService iaReceitaService;
    private final OrcamentoService orcamentoService;
    private final RestTemplate restTemplate;

    public TwilioWebhookController(IAReceitaService iaReceitaService,
            OrcamentoService orcamentoService,
            RestTemplate restTemplate) {
        this.iaReceitaService = iaReceitaService;
        this.orcamentoService = orcamentoService;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/whatsapp", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> receberMensagemWhatsapp(@RequestParam Map<String, String> payload) {
        String numMedia = payload.get("NumMedia");

        if (numMedia != null && Integer.parseInt(numMedia) > 0) {
            String urlImagem = payload.get("MediaUrl0");
            String clienteWhatsapp = payload.get("From");

            try {
                // Realiza o download da imagem a partir da URL fornecida pela Twilio/Meta
                byte[] imagemBytes = restTemplate.getForObject(urlImagem, byte[].class);

                if (imagemBytes != null && imagemBytes.length > 0) {
                    // Chama a assinatura correta do método existente na camada de serviço
                    List<ItemOrcamento> itens = iaReceitaService.extrairItens(imagemBytes);

                    // O OrcamentoService orquestrará a gravação e aplicação da regra de R$ 10,00
                    orcamentoService.criarOrcamentoPreliminar(clienteWhatsapp, itens);
                }
            } catch (Exception e) {
                // Logar a exceção em produção. Mantemos o retorno 200 para a Twilio não
                // retentar requisições com erro interno.
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.ok().build();
    }
}
