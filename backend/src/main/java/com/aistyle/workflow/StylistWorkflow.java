package com.aistyle.workflow;

import com.aistyle.ai.HuggingFaceClient;
import com.aistyle.dto.StyleAnalysisResponse;
import com.aistyle.entity.UserProfile;
import com.aistyle.tracing.TokenCounter;
import com.aistyle.tracing.TraceRecorder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StylistWorkflow {

    @Autowired
    private HuggingFaceClient huggingFaceClient;

    @Autowired
    private TraceRecorder traceRecorder;

    @Autowired
    private TokenCounter tokenCounter;

    public StyleAnalysisResponse analyzeStyle(UserProfile profile) {
        long startTime = System.currentTimeMillis();
        log.info("Starting Style Analysis workflow");

        try {
            String prompt = buildStyleAnalysisPrompt(profile);
            int inputTokens = tokenCounter.countInputTokens(prompt);

            String analysis = huggingFaceClient.callModel(prompt);
            int outputTokens = tokenCounter.countOutputTokens(analysis);

            double styleMatchScore = extractScore(analysis, 50, 95);
            double confidenceScore = extractScore(analysis, 60, 95);

            long endTime = System.currentTimeMillis();
            traceRecorder.recordTrace(
                "Style Analysis",
                startTime,
                endTime,
                inputTokens,
                outputTokens,
                "mistralai/Mistral-7B-Instruct-v0.3"
            );

            log.info("Style Analysis completed - Score: {}", styleMatchScore);

            return StyleAnalysisResponse.builder()
                .styleMatchScore(styleMatchScore)
                .confidenceScore(confidenceScore)
                .analysis(analysis)
                .build();
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            traceRecorder.recordTraceError(
                "Style Analysis",
                startTime,
                endTime,
                e.getMessage(),
                0,
                "mistralai/Mistral-7B-Instruct-v0.3"
            );
            log.error("Style Analysis failed", e);
            throw new RuntimeException("Style analysis failed: " + e.getMessage());
        }
    }

    public String generateOutfitRecommendations(UserProfile profile, StyleAnalysisResponse styleAnalysis) {
        long startTime = System.currentTimeMillis();
        log.info("Starting Outfit Recommendations generation");

        try {
            String prompt = buildOutfitPrompt(profile, styleAnalysis);
            int inputTokens = tokenCounter.countInputTokens(prompt);

            String recommendations = huggingFaceClient.callModel(prompt);
            int outputTokens = tokenCounter.countOutputTokens(recommendations);

            long endTime = System.currentTimeMillis();
            traceRecorder.recordTrace(
                "Outfit Generation",
                startTime,
                endTime,
                inputTokens,
                outputTokens,
                "mistralai/Mistral-7B-Instruct-v0.3"
            );

            log.info("Outfit Recommendations generated successfully");
            return recommendations;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            traceRecorder.recordTraceError(
                "Outfit Generation",
                startTime,
                endTime,
                e.getMessage(),
                0,
                "mistralai/Mistral-7B-Instruct-v0.3"
            );
            log.error("Outfit generation failed", e);
            throw new RuntimeException("Outfit generation failed: " + e.getMessage());
        }
    }

    public String generateSummaryReport(UserProfile profile, StyleAnalysisResponse styleAnalysis,
                                       String outfitRecommendations) {
        long startTime = System.currentTimeMillis();
        log.info("Starting Summary Report generation");

        try {
            String prompt = buildSummaryPrompt(profile, styleAnalysis, outfitRecommendations);
            int inputTokens = tokenCounter.countInputTokens(prompt);

            String summary = huggingFaceClient.callModel(prompt);
            int outputTokens = tokenCounter.countOutputTokens(summary);

            long endTime = System.currentTimeMillis();
            traceRecorder.recordTrace(
                "Summary Generation",
                startTime,
                endTime,
                inputTokens,
                outputTokens,
                "mistralai/Mistral-7B-Instruct-v0.3"
            );

            log.info("Summary Report generated successfully");
            return summary;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            traceRecorder.recordTraceError(
                "Summary Generation",
                startTime,
                endTime,
                e.getMessage(),
                0,
                "mistralai/Mistral-7B-Instruct-v0.3"
            );
            log.error("Summary generation failed", e);
            throw new RuntimeException("Summary generation failed: " + e.getMessage());
        }
    }

    private String buildStyleAnalysisPrompt(UserProfile profile) {
        return String.format(
            "Analyze the fashion style for someone with these characteristics:\n" +
            "- Gender: %s\n" +
            "- Age Group: %s\n" +
            "- Body Type: %s\n" +
            "- Skin Tone: %s\n" +
            "- Preferred Colors: %s\n" +
            "- Style Preference: %s\n" +
            "- Fit Preference: %s\n" +
            "Provide a detailed style analysis with a score from 0-100 on how well their preferences align. " +
            "Start with 'Style Analysis: ' and include the numeric score.",
            profile.getGender(), profile.getAgeGroup(), profile.getBodyType(),
            profile.getSkinTone(), profile.getPreferredColors(),
            profile.getStylePreference(), profile.getFitPreference()
        );
    }

    private String buildOutfitPrompt(UserProfile profile, StyleAnalysisResponse styleAnalysis) {
        return String.format(
            "Based on this style analysis:\n%s\n\n" +
            "And these preferences:\n" +
            "- Budget: %s\n" +
            "- Occasion: %s\n" +
            "- Weather: %s\n" +
            "- Favorite Brands: %s\n\n" +
            "Recommend specific outfit combinations and clothing pieces that would work well.",
            styleAnalysis.getAnalysis(),
            profile.getBudgetRange(), profile.getOccasionType(),
            profile.getWeather(), profile.getFavoriteBrands()
        );
    }

    private String buildSummaryPrompt(UserProfile profile, StyleAnalysisResponse styleAnalysis,
                                     String outfitRecommendations) {
        return String.format(
            "Summarize these fashion recommendations in 2-3 sentences:\n" +
            "Style Analysis: %s\n\n" +
            "Outfit Recommendations:\n%s\n\n" +
            "Create a concise, actionable summary.",
            styleAnalysis.getAnalysis(),
            outfitRecommendations
        );
    }

    private double extractScore(String text, int min, int max) {
        try {
            String[] words = text.split("\\s+");
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].matches(".*\\d.*")) {
                    String word = words[i].replaceAll("[^0-9]", "");
                    if (!word.isEmpty()) {
                        double score = Double.parseDouble(word);
                        if (score >= 0 && score <= 100) {
                            return Math.max(min, Math.min(max, score));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract score from response", e);
        }
        return (min + max) / 2.0;
    }
}
