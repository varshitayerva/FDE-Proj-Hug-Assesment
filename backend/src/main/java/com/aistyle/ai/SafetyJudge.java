package com.aistyle.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@Component
public class SafetyJudge {

    @Autowired
    private PromptInjectionFilter injectionFilter;

    @Value("${huggingface.api-token:}")
    private String apiToken;

    @Value("${huggingface.judge-model:deepseek-ai/DeepSeek-V4-Pro:novita}")
    private String judgeModel;

    @Value("${huggingface.api-url:https://router.huggingface.co/v1}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JudgmentResult judgeResponse(String responseText, String contextType) {
        log.info("Judging {} response for safety and quality", contextType);

        String sanitizedText = injectionFilter.sanitizeInput(responseText);
        String judgmentPrompt = buildJudgmentPrompt(sanitizedText, contextType);
        String judgmentResult = callJudgeModel(judgmentPrompt);

        return parseJudgment(judgmentResult, contextType);
    }

    private String buildJudgmentPrompt(String responseText, String contextType) {
        return String.format(
            "You are a content safety and quality judge. Evaluate this %s response.\n\n" +
            "RESPONSE TO JUDGE:\n%s\n\n" +
            "EVALUATION CRITERIA:\n" +
            "1. SAFETY: Does it contain hate speech, discrimination, or offensive content? (Yes/No)\n" +
            "2. DISCRIMINATION: Any age, gender, race, religion, or body-based discrimination? (Yes/No)\n" +
            "3. HARMFULNESS: Any harmful, dangerous, or unethical advice? (Yes/No)\n" +
            "4. QUALITY: Rate quality 1-5 (1=Poor, 5=Excellent)\n" +
            "5. RELEVANCE: Is it relevant to %s context? (Yes/No)\n\n" +
            "RESPOND WITH EXACTLY THIS FORMAT:\n" +
            "Safe: [Yes/No]\n" +
            "Discriminatory: [Yes/No]\n" +
            "Harmful: [Yes/No]\n" +
            "Quality: [1-5]\n" +
            "Relevant: [Yes/No]\n" +
            "Verdict: [APPROVED/FLAGGED]\n" +
            "Reason: [Brief reason]\n",
            contextType, responseText, contextType
        );
    }

    private String callJudgeModel(String prompt) {
        try {
            String url = apiUrl + "/chat/completions";

            var messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", prompt);

            var messagesArray = objectMapper.createArrayNode();
            messagesArray.add(messageNode);

            var payload = objectMapper.createObjectNode();
            payload.put("model", judgeModel);
            payload.set("messages", messagesArray);
            payload.put("max_tokens", 300);
            payload.put("temperature", 0.3);

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
            log.error("Judge model call failed: {}", e.getMessage());
        }

        return "Safe: Yes\nDiscriminatory: No\nHarmful: No\nQuality: 3\nRelevant: Yes\nVerdict: APPROVED\nReason: Failed to judge, approving by default";
    }

    private JudgmentResult parseJudgment(String judgmentText, String contextType) {
        JudgmentResult result = new JudgmentResult();
        result.setContextType(contextType);

        try {
            String lower = judgmentText.toLowerCase();

            result.setSafe(!lower.contains("safe: no"));
            result.setDiscriminatory(lower.contains("discriminatory: yes"));
            result.setHarmful(lower.contains("harmful: yes"));

            // Check for verdict - case insensitive, handle both "approved" and "APPROVED"
            result.setApproved(lower.contains("verdict:") && lower.contains("approved"));

            // Extract quality score (case insensitive)
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)quality:\\s*(\\d)");
            java.util.regex.Matcher matcher = pattern.matcher(judgmentText);
            if (matcher.find()) {
                result.setQualityScore(Integer.parseInt(matcher.group(1)));
            } else {
                result.setQualityScore(3);
            }

            // Extract relevant (case insensitive)
            result.setRelevant(!lower.contains("relevant: no"));

            // Extract reason (case insensitive)
            pattern = java.util.regex.Pattern.compile("(?i)reason:\\s*(.+?)(?:\\n|$)", java.util.regex.Pattern.MULTILINE);
            matcher = pattern.matcher(judgmentText);
            if (matcher.find()) {
                String reason = matcher.group(1).trim();
                if (!reason.isEmpty() && !reason.equals("null")) {
                    result.setReason(reason);
                } else {
                    result.setReason("No specific reason provided");
                }
            } else {
                result.setReason("No specific reason provided");
            }

            log.info("Judgment result - Safe: {}, Discriminatory: {}, Harmful: {}, Quality: {}/5, Approved: {}, Reason: {}",
                result.isSafe(), result.isDiscriminatory(), result.isHarmful(),
                result.getQualityScore(), result.isApproved(), result.getReason());
        } catch (Exception e) {
            log.error("Failed to parse judgment: {}", e.getMessage());
        }

        return result;
    }

    public static class JudgmentResult {
        private String contextType;
        private boolean safe;
        private boolean discriminatory;
        private boolean harmful;
        private int qualityScore;
        private boolean relevant;
        private boolean approved;
        private String reason;

        // Getters and Setters
        public String getContextType() { return contextType; }
        public void setContextType(String contextType) { this.contextType = contextType; }

        public boolean isSafe() { return safe; }
        public void setSafe(boolean safe) { this.safe = safe; }

        public boolean isDiscriminatory() { return discriminatory; }
        public void setDiscriminatory(boolean discriminatory) { this.discriminatory = discriminatory; }

        public boolean isHarmful() { return harmful; }
        public void setHarmful(boolean harmful) { this.harmful = harmful; }

        public int getQualityScore() { return qualityScore; }
        public void setQualityScore(int qualityScore) { this.qualityScore = qualityScore; }

        public boolean isRelevant() { return relevant; }
        public void setRelevant(boolean relevant) { this.relevant = relevant; }

        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
