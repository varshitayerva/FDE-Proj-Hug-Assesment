package com.aistyle.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class HuggingFaceClient {

    @Value("${huggingface.api-token:hf_test_token_placeholder}")
    private String apiToken;

    @Value("${huggingface.primary-model:mistralai/Mistral-7B-Instruct-v0.3}")
    private String primaryModel;

    @Value("${huggingface.fallback-model:google/gemma-2-2b-it}")
    private String fallbackModel;

    @Value("${huggingface.api-url:http://localhost:3001/models/}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String callModel(String prompt) {
        log.info("Calling HuggingFace model with prompt: {}", prompt.substring(0, Math.min(100, prompt.length())));

        try {
            return callModelEndpoint(primaryModel, prompt);
        } catch (Exception e) {
            log.warn("Primary model failed, trying fallback. Error: {}", e.getMessage());
            try {
                return callModelEndpoint(fallbackModel, prompt);
            } catch (Exception fallbackError) {
                log.error("Both primary and fallback models failed", fallbackError);
                throw new RuntimeException("AI service unavailable. Both primary and fallback models failed.");
            }
        }
    }

    private String callModelEndpoint(String model, String prompt) {
        String url = apiUrl + model;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiToken);

        String payload = String.format("{\"inputs\": \"%s\", \"parameters\": {\"max_length\": 500}}",
            escapeJson(prompt));

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        log.info("Calling endpoint: {}", url);
        var response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return extractResponseText(response.getBody());
        }

        throw new RuntimeException("API returned status: " + response.getStatusCode());
    }

    private String extractResponseText(String responseBody) {
        try {
            var node = objectMapper.readTree(responseBody);
            if (node.isArray() && node.size() > 0) {
                var firstItem = node.get(0);
                if (firstItem.has("generated_text")) {
                    return firstItem.get("generated_text").asText();
                }
            }
            return responseBody;
        } catch (Exception e) {
            log.warn("Failed to parse response: {}", e.getMessage());
            return responseBody;
        }
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
