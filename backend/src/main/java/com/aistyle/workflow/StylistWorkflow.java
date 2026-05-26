package com.aistyle.workflow;

import com.aistyle.ai.HuggingFaceClient;
import com.aistyle.dto.StyleAnalysisResponse;
import com.aistyle.entity.UserProfile;
import com.aistyle.exception.AIServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StylistWorkflow {

    @Autowired
    private HuggingFaceClient huggingFaceClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StyleAnalysisResponse analyzeStyle(UserProfile profile) {
        String prompt = buildStyleAnalysisPrompt(profile);
        log.info("LLM Call 1: Analyzing user style...");

        String response = huggingFaceClient.generateResponseWithFallback(prompt, 500);

        try {
            String cleanedResponse = extractJsonFromResponse(response);
            return parseStyleAnalysisResponse(cleanedResponse);
        } catch (Exception ex) {
            log.error("Error parsing style analysis response", ex);
            throw new AIServiceException("Failed to parse AI style analysis response");
        }
    }

    public String generateOutfitRecommendations(UserProfile profile, StyleAnalysisResponse analysis) {
        String prompt = buildOutfitPrompt(profile, analysis);
        log.info("LLM Call 2: Generating outfit recommendations...");

        return huggingFaceClient.generateResponseWithFallback(prompt, 1000);
    }

    public String generateSummaryReport(UserProfile profile, StyleAnalysisResponse analysis,
                                       String recommendations) {
        String prompt = buildSummaryPrompt(profile, analysis, recommendations);
        log.info("LLM Call 3: Generating summary report...");

        return huggingFaceClient.generateResponseWithFallback(prompt, 500);
    }

    private String buildStyleAnalysisPrompt(UserProfile profile) {
        return String.format(
            "Analyze the following user's fashion style and provide recommendations. Return ONLY a valid JSON object with NO additional text.\n\n" +
            "User Profile:\n" +
            "- Gender: %s\n" +
            "- Age Group: %s\n" +
            "- Body Type: %s\n" +
            "- Skin Tone: %s\n" +
            "- Budget: %s\n" +
            "- Occasion: %s\n" +
            "- Preferred Colors: %s\n" +
            "- Style Preference: %s\n" +
            "- Weather: %s\n" +
            "- Fashion Confidence: %s\n" +
            "- Favorite Brands: %s\n" +
            "- Fit Preference: %s\n" +
            "- Wardrobe Preferences: %s\n\n" +
            "Return ONLY this JSON structure, no other text:\n" +
            "{\n" +
            "  \"fashion_persona\": \"A brief description of the user's fashion persona\",\n" +
            "  \"body_fit_recommendation\": \"Specific fit recommendations for their body type\",\n" +
            "  \"suitable_colors\": [\"color1\", \"color2\", \"color3\"],\n" +
            "  \"style_match_score\": 75,\n" +
            "  \"confidence_score\": 70,\n" +
            "  \"recommended_categories\": [\"category1\", \"category2\", \"category3\"]\n" +
            "}",
            profile.getGender(),
            profile.getAgeGroup(),
            profile.getBodyType(),
            profile.getSkinTone(),
            profile.getBudgetRange(),
            profile.getOccasionType(),
            profile.getPreferredColors(),
            profile.getStylePreference(),
            profile.getWeather(),
            profile.getConfidenceLevel(),
            profile.getFavoriteBrands(),
            profile.getFitPreference(),
            profile.getWardrobePreferences()
        );
    }

    private String buildOutfitPrompt(UserProfile profile, StyleAnalysisResponse analysis) {
        String basePrompt = String.format(
            "Based on this user's fashion profile and analysis, provide outfit recommendations.\n\n" +
            "User Analysis:\n" +
            "- Fashion Persona: %s\n" +
            "- Style Match Score: %d%%\n" +
            "- Suitable Colors: %s\n" +
            "- Recommended Categories: %s\n\n",
            analysis.getFashionPersona(),
            analysis.getStyleMatchScore(),
            String.join(", ", analysis.getSuitableColors()),
            String.join(", ", analysis.getRecommendedCategories())
        );

        if (analysis.getStyleMatchScore() >= 70) {
            return basePrompt +
                "The user has HIGH confidence in their style. Provide:\n" +
                "1. Premium personalized outfit combinations (3-4 combinations)\n" +
                "2. Matching accessories and styling tips\n" +
                "3. Advanced color pairing techniques\n" +
                "4. Confidence-boosting fashion insights\n" +
                "Include brand suggestions and price guidance for their budget level.";
        } else {
            return basePrompt +
                "The user has LOW to MEDIUM confidence in their style. Provide:\n" +
                "1. Easy, beginner-friendly outfit combinations (3-4 combinations)\n" +
                "2. Safe color matching tips\n" +
                "3. Basic styling rules that are easy to follow\n" +
                "4. Wardrobe improvement suggestions\n" +
                "Keep recommendations simple and confidence-building.";
        }
    }

    private String buildSummaryPrompt(UserProfile profile, StyleAnalysisResponse analysis,
                                     String recommendations) {
        return String.format(
            "Create a brief stylist summary report based on these recommendations.\n\n" +
            "User's Fashion Persona: %s\n" +
            "Style Analysis Score: %d%%\n" +
            "Recommendations Provided:\n%s\n\n" +
            "Write a 2-3 paragraph summary that includes:\n" +
            "1. Why these recommendations were selected\n" +
            "2. Overall style explanation\n" +
            "3. Confidence-building tips for future fashion choices\n" +
            "Keep the tone encouraging and positive.",
            analysis.getFashionPersona(),
            analysis.getStyleMatchScore(),
            recommendations
        );
    }

    private String extractJsonFromResponse(String response) {
        int jsonStart = response.indexOf('{');
        int jsonEnd = response.lastIndexOf('}');

        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            return response.substring(jsonStart, jsonEnd + 1);
        }
        return response;
    }

    private StyleAnalysisResponse parseStyleAnalysisResponse(String jsonResponse) {
        try {
            StyleAnalysisResponse analysis = objectMapper.readValue(jsonResponse, StyleAnalysisResponse.class);

            if (analysis.getStyleMatchScore() == null || analysis.getStyleMatchScore() < 0 || analysis.getStyleMatchScore() > 100) {
                analysis.setStyleMatchScore(75);
            }
            if (analysis.getConfidenceScore() == null || analysis.getConfidenceScore() < 0 || analysis.getConfidenceScore() > 100) {
                analysis.setConfidenceScore(70);
            }
            if (analysis.getSuitableColors() == null || analysis.getSuitableColors().isEmpty()) {
                analysis.setSuitableColors(List.of("Neutral", "Earth tones", "Primary colors"));
            }
            if (analysis.getRecommendedCategories() == null || analysis.getRecommendedCategories().isEmpty()) {
                analysis.setRecommendedCategories(List.of("Casual", "Business Casual", "Evening wear"));
            }

            return analysis;
        } catch (Exception ex) {
            log.error("Error parsing JSON response: " + jsonResponse, ex);
            return getDefaultStyleAnalysis();
        }
    }

    private StyleAnalysisResponse getDefaultStyleAnalysis() {
        return StyleAnalysisResponse.builder()
            .fashionPersona("Contemporary Classic")
            .bodyFitRecommendation("Well-fitted and tailored pieces")
            .suitableColors(List.of("Navy", "Charcoal", "White", "Earth tones"))
            .styleMatchScore(75)
            .confidenceScore(70)
            .recommendedCategories(List.of("Casual", "Business Casual", "Evening wear"))
            .build();
    }
}
