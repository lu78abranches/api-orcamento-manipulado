package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(prefix = "gemini.api", name = "enabled", havingValue = "true", matchIfMissing = false)
public class GeminiReceitaService implements IAReceitaService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiReceitaService.class);

    @Value("${gemini.api.url:}")
    private String apiUrl;

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiReceitaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ItemOrcamento> extrairItens(byte[] imagem) {
        String base64Imagem = Base64.getEncoder().encodeToString(imagem);
        String urlFinal = apiUrl + "?key=" + apiKey;

        Map<String, Object> textPart = Map.of(
                "type", "text",
                "text",
                "Liste os itens e preços desta receita. Retorne APENAS um JSON no formato: {\"itens\": [{\"nome\": \"...\", \"preco\": 0.00}]}");

        Map<String, Object> imagePart = Map.of(
                "type", "image",
                "image_bytes", base64Imagem,
                "mime_type", "image/jpeg");

        Map<String, Object> requestBody = Map.of(
                "instances", List.of(
                        Map.of("content", List.of(textPart, imagePart))));

        try {
            var entity = createGeminiEntity(requestBody);
            var response = restTemplate.postForObject(urlFinal, entity, Map.class);
            String text = getTextFromGeminiResponse(response);

            String jsonLimpo = text.replace("```json", "").replace("```", "").trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonLimpo);
            JsonNode itensNode = rootNode.has("itens") ? rootNode.get("itens") : rootNode;

            List<ItemExtraidoDTO> dtos = mapper.readValue(
                    itensNode.toString(),
                    new TypeReference<List<ItemExtraidoDTO>>() {
                    });

            return dtos.stream()
                    .map(dto -> new ItemOrcamento(dto.nome(), dto.preco()))
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                throw new RuntimeException(
                        "Quota do Gemini esgotada. Aguarde alguns minutos ou use outra conta/API key.");
            }
            throw new RuntimeException("Erro no Gemini: " + e.getStatusCode() + ". " + e.getStatusText(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro no Gemini: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ItemOrcamento> extrairItensFromText(String texto) {
        String urlFinal = apiUrl + "?key=" + apiKey;

        Map<String, Object> promptPart = Map.of(
                "type", "text",
                "text",
                "Liste os itens e preços desta receita. Retorne APENAS um JSON no formato: {\"itens\": [{\"nome\": \"...\", \"preco\": 0.00}]}");
        Map<String, Object> userTextPart = Map.of(
                "type", "text",
                "text", texto);
        Map<String, Object> requestBody = Map.of(
                "instances", List.of(
                        Map.of("content", List.of(promptPart, userTextPart))));

        try {
            var entity = createGeminiEntity(requestBody);
            var response = restTemplate.postForObject(urlFinal, entity, Map.class);
            String text = getTextFromGeminiResponse(response);

            String jsonLimpo = text.replace("```json", "").replace("```", "").trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonLimpo);
            JsonNode itensNode = rootNode.has("itens") ? rootNode.get("itens") : rootNode;

            List<ItemExtraidoDTO> dtos = mapper.readValue(
                    itensNode.toString(),
                    new TypeReference<List<ItemExtraidoDTO>>() {
                    });

            return dtos.stream()
                    .map(dto -> new ItemOrcamento(dto.nome(), dto.preco()))
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                throw new RuntimeException(
                        "Quota do Gemini esgotada. Aguarde alguns minutos ou use outra conta/API key.");
            }
            throw new RuntimeException("Erro no Gemini (texto): " + e.getStatusCode() + ". " + e.getStatusText(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro no Gemini (texto): " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String getTextFromGeminiResponse(Map<String, Object> response) {
        if (response == null) {
            throw new RuntimeException("Resposta inválida do Gemini");
        }

        if (response.containsKey("predictions")) {
            List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.get("predictions");
            if (predictions.isEmpty()) {
                throw new RuntimeException("Resposta inválida do Gemini");
            }
            Map<String, Object> prediction = predictions.get(0);
            List<Map<String, Object>> content = (List<Map<String, Object>>) prediction.get("content");
            if (content == null || content.isEmpty()) {
                throw new RuntimeException("Resposta inválida do Gemini");
            }
            return (String) content.get(0).get("text");
        }

        if (response.containsKey("candidates")) {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates.isEmpty()) {
                throw new RuntimeException("Resposta inválida do Gemini");
            }
            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            if (content == null || !content.containsKey("parts")) {
                throw new RuntimeException("Resposta inválida do Gemini");
            }
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("Resposta inválida do Gemini");
            }
            return (String) parts.get(0).get("text");
        }

        throw new RuntimeException("Resposta inválida do Gemini");
    }

    private org.springframework.http.HttpEntity<Map<String, Object>> createGeminiEntity(
            Map<String, Object> requestBody) {
        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        return new org.springframework.http.HttpEntity<>(requestBody, headers);
    }
}
