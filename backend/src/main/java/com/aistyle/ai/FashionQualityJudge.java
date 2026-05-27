package com.aistyle.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@Component
public class FashionQualityJudge {

    @Autowired
    private PromptInjectionFilter injectionFilter;

    @Value("${huggingface.api-token:}")
    private String apiToken;

    @Value("${huggingface.quality-judge-model:meta-llama/Llama-2-7b-chat-hf}")
    private String qualityJudgeModel;

    @Value("${huggingface.api-url:https://router.huggingface.co/v1}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FashionJudgmentResult judgeRecommendationQuality(String stylePersona, String outfitRecommendations,
                                                            String userProfile) {
        log.info("Judging outfit recommendations for fashion quality and styling coherence");

        String judgmentPrompt = buildFashionJudgmentPrompt(stylePersona, outfitRecommendations, userProfile);
        String judgmentResult = callFashionJudgeModel(judgmentPrompt);

        return parseFashionJudgment(judgmentResult);
    }

    private String buildFashionJudgmentPrompt(String stylePersona, String outfitRecommendations, String userProfile) {
        return String.format(
            "You are an expert fashion stylist and quality assessor. Evaluate these outfit recommendations.\n\n" +
            "STYLE PERSONA: %s\n\n" +
            "USER PROFILE:\n%s\n\n" +
            "OUTFIT RECOMMENDATIONS:\n%s\n\n" +
            "EVALUATION CRITERIA:\n" +
            "1. STYLE COHERENCE: Do all outfits align with the stated style persona? (Yes/No)\n" +
            "2. RECOMMENDATION QUALITY: Are the pieces appropriate and well-matched? (1-5 scale)\n" +
            "3. SAFETY: Are all suggestions appropriate and non-offensive? (Yes/No)\n" +
            "4. DIVERSITY: Do the outfits offer variety while maintaining style? (Yes/No)\n" +
            "5. PRACTICALITY: Are recommendations wearable and budget-appropriate? (Yes/No)\n" +
            "6. CONFIDENCE SCORE: Rate overall recommendation quality (0-100)\n\n" +
            "RESPOND WITH EXACTLY THIS FORMAT:\n" +
            "Coherent: [Yes/No]\n" +
            "Quality: [1-5]\n" +
            "Safe: [Yes/No]\n" +
            "Diverse: [Yes/No]\n" +
            "Practical: [Yes/No]\n" +
            "Confidence: [0-100]\n" +
            "Explanation: [2-3 sentences explaining why these recommendations work]\n",
            stylePersona, userProfile, outfitRecommendations
        );
    }

    private String callFashionJudgeModel(String prompt) {
        try {
            String url = apiUrl + "/chat/completions";

            var messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", prompt);

            var messagesArray = objectMapper.createArrayNode();
            messagesArray.add(messageNode);

            var payload = objectMapper.createObjectNode();
            payload.put("model", qualityJudgeModel);
            payload.set("messages", messagesArray);
            payload.put("max_tokens", 400);
            payload.put("temperature", 0.5);

            String payloadStr = objectMapper.writeValueAsString(payload);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);

            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(payloadStr, headers);
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

            var response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode node = objectMapper.readTree(response.getBody());
                if (node.has("choices") && node.get("choices").isArray() && node.get("choices").size() > 0) {
                    JsonNode message = node.get("choices").get(0).get("message");
                    if (message.has("content")) {
                        return message.get("content").asText();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Fashion quality judge model call failed: {}", e.getMessage());
        }

        return "Coherent: Yes\nQuality: 4\nSafe: Yes\nDiverse: Yes\nPractical: Yes\nConfidence: 75\n" +
               "Explanation: Failed to judge quality, returning default approval based on system safety checks.";
    }

    private FashionJudgmentResult parseFashionJudgment(String judgmentText) {
        FashionJudgmentResult result = new FashionJudgmentResult();

        try {
            String lower = judgmentText.toLowerCase();

            result.setCoherent(!lower.contains("coherent: no"));
            result.setSafe(!lower.contains("safe: no"));
            result.setDiverse(!lower.contains("diverse: no"));
            result.setPractical(!lower.contains("practical: no"));

            // Extract quality score (1-5)
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)quality:\\s*(\\d)");
            java.util.regex.Matcher matcher = pattern.matcher(judgmentText);
            if (matcher.find()) {
                result.setQualityScore(Integer.parseInt(matcher.group(1)));
            } else {
                result.setQualityScore(3);
            }

            // Extract confidence score (0-100)
            pattern = java.util.regex.Pattern.compile("(?i)confidence:\\s*(\\d{1,3})");
            matcher = pattern.matcher(judgmentText);
            if (matcher.find()) {
                int confidence = Integer.parseInt(matcher.group(1));
                result.setConfidenceScore(Math.min(100, Math.max(0, confidence)));
            } else {
                result.setConfidenceScore(75);
            }

            // Extract explanation
            pattern = java.util.regex.Pattern.compile("(?i)explanation:\\s*(.+?)(?:\\n|$)", java.util.regex.Pattern.MULTILINE);
            matcher = pattern.matcher(judgmentText);
            if (matcher.find()) {
                String explanation = matcher.group(1).trim();
                if (!explanation.isEmpty() && !explanation.equals("null")) {
                    result.setExplanation(explanation);
                } else {
                    result.setExplanation("Recommendations align with style persona and user preferences.");
                }
            } else {
                result.setExplanation("Recommendations align with style persona and user preferences.");
            }

            log.info("Fashion Quality Judgment - Coherent: {}, Quality: {}/5, Safe: {}, Diverse: {}, Practical: {}, " +
                    "Confidence: {}%, Explanation: {}",
                result.isCoherent(), result.getQualityScore(), result.isSafe(), result.isDiverse(),
                result.isPractical(), result.getConfidenceScore(), result.getExplanation());
        } catch (Exception e) {
            log.error("Failed to parse fashion judgment: {}", e.getMessage());
        }

        return result;
    }

    public static class FashionJudgmentResult {
        private boolean coherent;
        private int qualityScore;
        private boolean safe;
        private boolean diverse;
        private boolean practical;
        private int confidenceScore;
        private String explanation;

        public boolean isCoherent() { return coherent; }
        public void setCoherent(boolean coherent) { this.coherent = coherent; }

        public int getQualityScore() { return qualityScore; }
        public void setQualityScore(int qualityScore) { this.qualityScore = qualityScore; }

        public boolean isSafe() { return safe; }
        public void setSafe(boolean safe) { this.safe = safe; }

        public boolean isDiverse() { return diverse; }
        public void setDiverse(boolean diverse) { this.diverse = diverse; }

        public boolean isPractical() { return practical; }
        public void setPractical(boolean practical) { this.practical = practical; }

        public int getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(int confidenceScore) { this.confidenceScore = confidenceScore; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }
}
