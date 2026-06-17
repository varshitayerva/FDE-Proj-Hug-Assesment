package com.aistyle.ai;

import com.aistyle.tracing.TokenCounter;
import com.aistyle.tracing.TraceRecorder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TraceRecorder traceRecorder;

    @Autowired
    private TokenCounter tokenCounter;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String callModel(String prompt) {
        long startTime = System.currentTimeMillis();
        log.info("Calling HuggingFace model with prompt: {}", prompt.substring(0, Math.min(100, prompt.length())));

        int inputTokens = tokenCounter.countInputTokens(prompt);

        try {
            String response = callModelEndpoint(primaryModel, prompt);
            long endTime = System.currentTimeMillis();

            int outputTokens = tokenCounter.countOutputTokens(response);
            traceRecorder.recordTrace(
                "HuggingFace API Call",
                startTime,
                endTime,
                inputTokens,
                outputTokens,
                primaryModel
            );

            log.info("Model call successful. Input tokens: {}, Output tokens: {}", inputTokens, outputTokens);
            return response;
        } catch (Exception e) {
            log.warn("Primary model failed, trying fallback. Error: {}", e.getMessage());
            try {
                long fallbackStartTime = System.currentTimeMillis();
                String response = callModelEndpoint(fallbackModel, prompt);
                long endTime = System.currentTimeMillis();

                int outputTokens = tokenCounter.countOutputTokens(response);
                traceRecorder.recordTrace(
                    "HuggingFace API Call (Fallback)",
                    fallbackStartTime,
                    endTime,
                    inputTokens,
                    outputTokens,
                    fallbackModel
                );

                log.info("Fallback model call successful. Input tokens: {}, Output tokens: {}", inputTokens, outputTokens);
                return response;
            } catch (Exception fallbackError) {
                long endTime = System.currentTimeMillis();
                traceRecorder.recordTraceError(
                    "HuggingFace API Call",
                    startTime,
                    endTime,
                    fallbackError.getMessage(),
                    inputTokens,
                    primaryModel
                );

                log.error("Both primary and fallback models failed", fallbackError);
                throw new RuntimeException("AI service unavailable. Both primary and fallback models failed.");
            }
        }
    }

    private String callModelEndpoint(String model, String prompt) {
        String url = apiUrl.replaceAll("/$", "") + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiToken);

        String payload = String.format(
            "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 500}",
            model,
            escapeJson(prompt)
        );

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
            if (node.has("choices") && node.get("choices").isArray() && node.get("choices").size() > 0) {
                var firstChoice = node.get("choices").get(0);
                if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                    return firstChoice.get("message").get("content").asText();
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
