package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.dto.ItemExtraidoDTO;
import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Primary
public class OpenAIReceitaService implements IAReceitaService {

        private static final Logger logger = LoggerFactory.getLogger(OpenAIReceitaService.class);

        @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
        private String apiUrl;

        @Value("${openai.api.key}")
        private String apiKey;

        private final RestTemplate restTemplate;
        private final ObjectProvider<GeminiReceitaService> geminiReceitaServiceProvider;

        public OpenAIReceitaService(RestTemplate restTemplate,
                        ObjectProvider<GeminiReceitaService> geminiReceitaServiceProvider) {
                this.restTemplate = restTemplate;
                this.geminiReceitaServiceProvider = geminiReceitaServiceProvider;
        }

        @Override
        public List<ItemOrcamento> extrairItens(byte[] imagem) {
                String base64Imagem = Base64.getEncoder().encodeToString(imagem);
                String url = apiUrl;

                List<Map<String, Object>> messages = List.of(
                                Map.of("role", "system", "content",
                                                "Você é um assistente que extrai itens e preços de receitas médicas."),
                                Map.of("role", "user", "content",
                                                "Liste os itens e preços desta receita. Retorne APENAS um JSON no formato: {\"itens\": [{\"nome\": \"...\", \"preco\": 0.00}]}\n\nReceita em base64: data:image/jpeg;base64,"
                                                                + base64Imagem));

                Map<String, Object> requestBody = Map.of(
                                "model", "gpt-4o",
                                "messages", messages,
                                "temperature", 0);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);

                // ESTES CABEÇALHOS SÃO CRUCIAIS PARA EVITAR O 403:
                headers.set("User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                headers.set("Accept", "application/json");

                // Opcional: Alguns filtros barram se não houver o Origin ou Referer
                headers.set("Origin", "https://api.openai.com");

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                try {
                        System.out.println("DEBUG: Tentando conectar na URL: " + url);
                        var responseBody = restTemplate.postForObject(url, entity, Map.class);
                        System.out.println("Resposta da OpenAI: " + responseBody);

                        // --- INÍCIO DO PARSE (O que faltava para retornar) ---
                        @SuppressWarnings({ "unchecked", "null" })
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                        @SuppressWarnings("unchecked")
                        String contentJson = (String) ((Map<String, Object>) choices.get(0).get("message"))
                                        .get("content");

                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(contentJson);
                        JsonNode itensNode = rootNode.has("itens") ? rootNode.get("itens") : rootNode;

                        List<ItemExtraidoDTO> dtos = mapper.readValue(
                                        itensNode.toString(),
                                        new TypeReference<List<ItemExtraidoDTO>>() {
                                        });

                        // RETORNO: Transforma DTO em Entidade e entrega a lista
                        return dtos.stream()
                                        .map(dto -> new ItemOrcamento(dto.nome(), dto.preco()))
                                        .collect(Collectors.toList());
                        // --- FIM DO PARSE ---

                } catch (HttpClientErrorException e) {
                        String body = e.getResponseBodyAsString();
                        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS ||
                                        (body != null && body.contains("insufficient_quota"))) {
                                var gemini = geminiReceitaServiceProvider.getIfAvailable();
                                if (gemini != null) {
                                        logger.warn("OpenAI quota exhausted; falling back to Gemini for image extraction.");
                                        return gemini.extrairItens(imagem);
                                }
                                throw new RuntimeException(
                                                "Quota da OpenAI esgotada. Aguarde alguns minutos ou confira seu plano e billing.");
                        }
                        throw new RuntimeException("Erro na OpenAI: " + e.getStatusCode() + ". " + e.getStatusText(),
                                        e);
                } catch (Exception e) {
                        e.printStackTrace(); // Isso vai imprimir o erro completo no seu terminal
                        throw new RuntimeException("Falha na extração: " + e.getMessage(), e);
                }
        }

        @Override
        public List<ItemOrcamento> extrairItensFromText(String texto) {
                String url = apiUrl;

                List<Map<String, Object>> messages = List.of(
                                Map.of("role", "system", "content",
                                                "Você é um assistente que extrai itens e preços de receitas médicas."),
                                Map.of("role", "user", "content",
                                                "Liste os itens e preços desta receita. Retorne APENAS um JSON no formato: {\"itens\": [{\"nome\": \"...\", \"preco\": 0.00}]}\n\nReceita:\n"
                                                                + texto));

                Map<String, Object> requestBody = Map.of(
                                "model", "gpt-4o",
                                "messages", messages,
                                "temperature", 0);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);
                headers.set("User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                headers.set("Accept", "application/json");
                headers.set("Origin", "https://api.openai.com");

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                try {
                        var responseBody = restTemplate.postForObject(url, entity, Map.class);

                        @SuppressWarnings({ "unchecked" })
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                        @SuppressWarnings("unchecked")
                        String contentJson = (String) ((Map<String, Object>) choices.get(0).get("message"))
                                        .get("content");

                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode rootNode = mapper.readTree(contentJson);
                        JsonNode itensNode = rootNode.has("itens") ? rootNode.get("itens") : rootNode;

                        List<ItemExtraidoDTO> dtos = mapper.readValue(
                                        itensNode.toString(),
                                        new TypeReference<List<ItemExtraidoDTO>>() {
                                        });

                        return dtos.stream()
                                        .map(dto -> new ItemOrcamento(dto.nome(), dto.preco()))
                                        .collect(Collectors.toList());

                } catch (HttpClientErrorException e) {
                        String body = e.getResponseBodyAsString();
                        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS ||
                                        (body != null && body.contains("insufficient_quota"))) {
                                var gemini = geminiReceitaServiceProvider.getIfAvailable();
                                if (gemini != null) {
                                        logger.warn("OpenAI quota exhausted; falling back to Gemini for text extraction.");
                                        return gemini.extrairItensFromText(texto);
                                }
                                throw new RuntimeException(
                                                "Quota da OpenAI esgotada. Aguarde alguns minutos ou confira seu plano e billing.");
                        }
                        throw new RuntimeException(
                                        "Erro na OpenAI (texto): " + e.getStatusCode() + ". " + e.getStatusText(), e);
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Falha na extração (texto): " + e.getMessage(), e);
                }
        }

}

/*
 * import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
 * import org.springframework.beans.factory.annotation.Value;
 * import org.springframework.stereotype.Service;
 * 
 * import java.math.BigDecimal;
 * import java.util.List;
 * 
 * // src/main/java/com/farmacia/api_orcamento_manipulado/service/
 * OpenAIReceitaService.java
 * 
 * @Service
 * public class OpenAIReceitaService implements IAReceitaService {
 * 
 * @Value("${openai.api.key}")
 * private String apiKey;
 * 
 * @Override
 * public List<ItemOrcamento> extrairItens(byte[] imagem) {
 * // Por enquanto, vamos deixar a estrutura pronta.
 * // Aqui dentro faremos a chamada HTTP enviando a imagem em Base64.
 * 
 * System.out.println("Chamando OpenAI com a chave: " + apiKey.substring(0, 5) +
 * "...");
 * 
 * // Simulação de retorno para não travar seu fluxo antes de você ter a chave
 * return List.of(new ItemOrcamento("Item via IA Real", new
 * BigDecimal("50.00")));
 * }
 * }
 */
