package com.aistyle.tracing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenCounter {

    // Approximate token counting based on text length
    // General rule: 1 token ≈ 4 characters for English text
    private static final double CHARS_PER_TOKEN = 4.0;

    public int countInputTokens(String prompt) {
        if (prompt == null || prompt.isEmpty()) {
            return 0;
        }
        // Rough estimation: split by words and count
        // More accurate: average word length is ~5 chars, tokens per word is ~1.3
        int wordCount = prompt.trim().split("\\s+").length;
        int estimatedTokens = (int) Math.ceil(wordCount * 1.3);
        log.debug("Input tokens estimated: {} (word count: {})", estimatedTokens, wordCount);
        return estimatedTokens;
    }

    public int countOutputTokens(String response) {
        if (response == null || response.isEmpty()) {
            return 0;
        }
        int wordCount = response.trim().split("\\s+").length;
        int estimatedTokens = (int) Math.ceil(wordCount * 1.3);
        log.debug("Output tokens estimated: {} (word count: {})", estimatedTokens, wordCount);
        return estimatedTokens;
    }

    public TokenUsage analyzeTokenUsage(String prompt, String response) {
        int inputTokens = countInputTokens(prompt);
        int outputTokens = countOutputTokens(response);
        int totalTokens = inputTokens + outputTokens;

        double estimatedCost = calculateCost(inputTokens, outputTokens);

        return TokenUsage.builder()
            .inputTokens(inputTokens)
            .outputTokens(outputTokens)
            .totalTokens(totalTokens)
            .estimatedCostUsd(estimatedCost)
            .costPerInputK(0.0001)
            .costPerOutputK(0.0003)
            .build();
    }

    public double calculateCost(int inputTokens, int outputTokens) {
        // HuggingFace pricing (approximate)
        // $0.0001 per 1000 input tokens
        // $0.0003 per 1000 output tokens
        double inputCost = (inputTokens / 1000.0) * 0.0001;
        double outputCost = (outputTokens / 1000.0) * 0.0003;
        return inputCost + outputCost;
    }

    public static class TokenUsage {
        private int inputTokens;
        private int outputTokens;
        private int totalTokens;
        private double estimatedCostUsd;
        private double costPerInputK;
        private double costPerOutputK;

        public TokenUsage(int inputTokens, int outputTokens, int totalTokens,
                         double estimatedCostUsd, double costPerInputK, double costPerOutputK) {
            this.inputTokens = inputTokens;
            this.outputTokens = outputTokens;
            this.totalTokens = totalTokens;
            this.estimatedCostUsd = estimatedCostUsd;
            this.costPerInputK = costPerInputK;
            this.costPerOutputK = costPerOutputK;
        }

        public static TokenUsageBuilder builder() {
            return new TokenUsageBuilder();
        }

        public int getInputTokens() { return inputTokens; }
        public int getOutputTokens() { return outputTokens; }
        public int getTotalTokens() { return totalTokens; }
        public double getEstimatedCostUsd() { return estimatedCostUsd; }
        public double getCostPerInputK() { return costPerInputK; }
        public double getCostPerOutputK() { return costPerOutputK; }

        @Override
        public String toString() {
            return String.format(
                "TokenUsage{input=%d, output=%d, total=%d, cost=$%.6f}",
                inputTokens, outputTokens, totalTokens, estimatedCostUsd
            );
        }

        public static class TokenUsageBuilder {
            private int inputTokens;
            private int outputTokens;
            private int totalTokens;
            private double estimatedCostUsd;
            private double costPerInputK = 0.0001;
            private double costPerOutputK = 0.0003;

            public TokenUsageBuilder inputTokens(int inputTokens) {
                this.inputTokens = inputTokens;
                return this;
            }

            public TokenUsageBuilder outputTokens(int outputTokens) {
                this.outputTokens = outputTokens;
                return this;
            }

            public TokenUsageBuilder totalTokens(int totalTokens) {
                this.totalTokens = totalTokens;
                return this;
            }

            public TokenUsageBuilder estimatedCostUsd(double estimatedCostUsd) {
                this.estimatedCostUsd = estimatedCostUsd;
                return this;
            }

            public TokenUsageBuilder costPerInputK(double costPerInputK) {
                this.costPerInputK = costPerInputK;
                return this;
            }

            public TokenUsageBuilder costPerOutputK(double costPerOutputK) {
                this.costPerOutputK = costPerOutputK;
                return this;
            }

            public TokenUsage build() {
                return new TokenUsage(inputTokens, outputTokens, totalTokens,
                    estimatedCostUsd, costPerInputK, costPerOutputK);
            }
        }
    }
}
