package com.aistyle.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@Component
public class HuggingFaceClient {

    @Value("${huggingface.api-token:}")
    private String apiToken;

    @Value("${huggingface.primary-model:mistralai/Mistral-7B-Instruct-v0.3}")
    private String primaryModel;

    @Value("${huggingface.fallback-model:google/gemma-2-2b-it}")
    private String fallbackModel;

    @Value("${huggingface.api-url:https://api-inference.huggingface.co/models/}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String callModel(String prompt) {
        if (apiToken == null || apiToken.isEmpty()) {
            log.error("HF_API_TOKEN not configured! Set HF_API_TOKEN environment variable");
            throw new RuntimeException("Hugging Face API token not configured");
        }

        log.info("Calling HuggingFace model with prompt length: {}", prompt.length());

        try {
            return callModelEndpoint(primaryModel, prompt);
        } catch (Exception e) {
            log.warn("Primary model ({}) failed: {}. Trying fallback...", primaryModel, e.getMessage());
            try {
                return callModelEndpoint(fallbackModel, prompt);
            } catch (Exception fallbackError) {
                log.error("Both models failed. Primary: {}, Fallback: {}", e.getMessage(), fallbackError.getMessage());
                throw new RuntimeException("AI service unavailable: " + fallbackError.getMessage());
            }
        }
    }

    private String callModelEndpoint(String model, String prompt) {
        String url = apiUrl + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiToken);

        // OpenAI-compatible format for Hugging Face Router
        String payload = buildOpenAIChatPayload(prompt, model);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        log.info("Calling HF Router /chat/completions endpoint with model: {}", model);

        try {
            var response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("API call successful, parsing chat response");
                return extractChatCompletionText(response.getBody());
            }

            throw new RuntimeException("API returned status: " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("HTTP Error - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("API Error: " + e.getMessage());
        }
    }

    private String buildOpenAIChatPayload(String prompt, String model) {
        try {
            var messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", prompt);

            var messagesArray = objectMapper.createArrayNode();
            messagesArray.add(messageNode);

            var payload = objectMapper.createObjectNode();
            payload.put("model", model);
            payload.set("messages", messagesArray);
            payload.put("max_tokens", 500);
            payload.put("temperature", 0.7);

            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Failed to build chat payload", e);
            throw new RuntimeException("Payload creation failed: " + e.getMessage());
        }
    }

    private String extractChatCompletionText(String responseBody) {
        try {
            log.debug("Raw response: {}", responseBody);
            JsonNode node = objectMapper.readTree(responseBody);

            // Handle OpenAI chat completion response format
            if (node.has("choices") && node.get("choices").isArray()) {
                JsonNode choices = node.get("choices");
                if (choices.size() > 0) {
                    JsonNode firstChoice = choices.get(0);
                    if (firstChoice.has("message")) {
                        JsonNode message = firstChoice.get("message");
                        if (message.has("content")) {
                            String content = message.get("content").asText();
                            log.info("Successfully extracted content from chat completion");
                            return content;
                        }
                    }
                }
            }

            // Fallback: return as string
            log.warn("Unexpected response format, returning raw body");
            return responseBody;
        } catch (Exception e) {
            log.warn("Failed to parse chat completion response: {}", e.getMessage());
            return responseBody;
        }
    }
}
