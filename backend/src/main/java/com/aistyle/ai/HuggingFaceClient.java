package com.aistyle.ai;

import com.aistyle.exception.AIServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HuggingFaceClient {

    @Value("${huggingface.api-token}")
    private String apiToken;

    @Value("${huggingface.primary-model}")
    private String primaryModel;

    @Value("${huggingface.fallback-model}")
    private String fallbackModel;

    @Value("${huggingface.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(String prompt, String model, int maxTokens) {
        try {
            String modelUrl = apiUrl + model;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);

            Map<String, Object> payload = new HashMap<>();
            payload.put("inputs", prompt);
            payload.put("parameters", Map.of(
                "max_length", maxTokens,
                "temperature", 0.7,
                "top_p", 0.95
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                modelUrl,
                request,
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractTextFromResponse(response.getBody());
            } else {
                throw new AIServiceException("Failed to get response from Hugging Face API");
            }
        } catch (Exception ex) {
            log.error("Error calling Hugging Face API with model: " + model, ex);
            throw new AIServiceException("AI service error: " + ex.getMessage());
        }
    }

    public String generateResponseWithFallback(String prompt, int maxTokens) {
        try {
            log.info("Attempting primary model: " + primaryModel);
            return generateResponse(prompt, primaryModel, maxTokens);
        } catch (Exception ex) {
            log.warn("Primary model failed, attempting fallback: " + fallbackModel, ex);
            try {
                return generateResponse(prompt, fallbackModel, maxTokens);
            } catch (Exception fallbackEx) {
                log.error("Both models failed", fallbackEx);
                throw new AIServiceException("AI service unavailable. Both primary and fallback models failed.");
            }
        }
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            if (root.isArray() && root.size() > 0) {
                JsonNode firstElement = root.get(0);
                if (firstElement.has("generated_text")) {
                    return firstElement.get("generated_text").asText();
                }
            } else if (root.has("generated_text")) {
                return root.get("generated_text").asText();
            }
            return response;
        } catch (Exception ex) {
            log.error("Error extracting text from response", ex);
            return response;
        }
    }
}
