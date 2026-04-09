package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OpenAIReceitaService implements IAReceitaService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public OpenAIReceitaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ItemOrcamento> extrairItens(byte[] imagem) {
        String base64Imagem = Base64.getEncoder().encodeToString(imagem);
        String url = "https://openai.com";

        // Montamos o corpo da requisição (simplificado para o exemplo)
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "user", "content", List.of(
                                Map.of("type", "text", "text", "Liste os itens e preços desta receita de manipulação. Retorne APENAS um JSON no formato: [{\"nome\": \"...\", \"preco\": 0.00}]"),
                                Map.of("type", "image_url", "image_url", Map.of("url", "data:image/jpeg;base64," + base64Imagem))
                        ))
                ),
                "response_format", Map.of("type", "json_object")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Chamada real
        var response = restTemplate.postForObject(url, entity, Map.class);

        // Aqui você faria o parse do JSON da resposta para List<ItemOrcamento>
        // Por enquanto, vamos retornar uma lista vazia para você testar a compilação
        //return new ArrayList<>();
        // ... dentro do método extrairItens, após o restTemplate.postForObject

        try {
            // 1. Pegamos o conteúdo da resposta da OpenAI
            Map<String, Object> responseBody = restTemplate.postForObject(url, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            String contentJson = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            // 2. Usamos o ObjectMapper para transformar o JSON em uma lista de DTOs
            ObjectMapper mapper = new ObjectMapper();
            // Configuração para ignorar campos que a IA possa inventar e que não temos no DTO
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // O conteúdo vem como um objeto JSON. Vamos extrair a lista de itens.
            // Dica: Se o prompt pediu um objeto com uma lista, o parse deve refletir isso.
            JsonNode rootNode = mapper.readTree(contentJson);
            JsonNode itensNode = rootNode.has("itens") ? rootNode.get("itens") : rootNode;

            List<ItemExtraidoDTO> dtos = mapper.readValue(
                    itensNode.toString(),
                    new TypeReference<List<ItemExtraidoDTO>>() {}
            );

            // 3. Convertemos DTOs para sua Entidade ItemOrcamento
            return dtos.stream()
                    .map(dto -> new ItemOrcamento(dto.nome(), dto.preco()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta da IA: " + e.getMessage());
        }

    }
}


/*import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// src/main/java/com/farmacia/api_orcamento_manipulado/service/OpenAIReceitaService.java
@Service
public class OpenAIReceitaService implements IAReceitaService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Override
    public List<ItemOrcamento> extrairItens(byte[] imagem) {
        // Por enquanto, vamos deixar a estrutura pronta.
        // Aqui dentro faremos a chamada HTTP enviando a imagem em Base64.

        System.out.println("Chamando OpenAI com a chave: " + apiKey.substring(0, 5) + "...");

        // Simulação de retorno para não travar seu fluxo antes de você ter a chave
        return List.of(new ItemOrcamento("Item via IA Real", new BigDecimal("50.00")));
    }
}*/

