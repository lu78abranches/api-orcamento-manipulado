package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Primary
public class GeminiReceitaService implements IAReceitaService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiReceitaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ItemOrcamento> extrairItens(byte[] imagem) {
        String base64Imagem = Base64.getEncoder().encodeToString(imagem);

        // CORREÇÃO AQUI: Usamos a apiUrl do properties e concatenamos a chave corretamente
        String urlFinal = apiUrl + "?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", "Liste os itens e preços desta receita. Retorne APENAS um JSON no formato: {\"itens\": [{\"nome\": \"...\", \"preco\": 0.00}]}"),
                                Map.of("inline_data", Map.of(
                                        "mime_type", "image/jpeg",
                                        "data", base64Imagem
                                ))
                        ))
                )
        );

        try {
            // Usamos a urlFinal corrigida
            var response = restTemplate.postForObject(urlFinal, requestBody, Map.class);

            if (response == null || !response.containsKey("candidates")) {
                throw new RuntimeException("Resposta inválida do Gemini");
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");

            // Limpeza de Markdown
            String jsonLimpo = text.replace("```json", "").replace("```", "").trim();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonLimpo);
            JsonNode itensNode = rootNode.has("itens") ? rootNode.get("itens") : rootNode;

            List<ItemExtraidoDTO> dtos = mapper.readValue(
                    itensNode.toString(),
                    new TypeReference<List<ItemExtraidoDTO>>() {}
            );

            return dtos.stream()
                    .map(dto -> new ItemOrcamento(dto.nome(), dto.preco()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Imprime o erro no console para facilitar o seu debug
            e.printStackTrace();
            throw new RuntimeException("Erro no Gemini: " + e.getMessage(), e);
        }
    }
}

