package com.aistyle.workflow;

import com.aistyle.ai.HuggingFaceClient;
import com.aistyle.ai.SafetyJudge;
import com.aistyle.ai.FashionQualityJudge;
import com.aistyle.ai.PromptInjectionFilter;
import com.aistyle.dto.StyleAnalysisResponse;
import com.aistyle.dto.OutfitRecommendation;
import com.aistyle.entity.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class StylistWorkflow {

    @Autowired
    private HuggingFaceClient huggingFaceClient;

    @Autowired
    private SafetyJudge safetyJudge;

    @Autowired
    private FashionQualityJudge fashionQualityJudge;

    @Autowired
    private PromptInjectionFilter injectionFilter;

    public StyleAnalysisResponse analyzeStyle(UserProfile profile) {
        validateUserProfile(profile);
        String prompt = buildStyleAnalysisPrompt(profile);
        String analysis = huggingFaceClient.callModel(prompt);

        // Judge the response for safety and quality
        log.info("========== JUDGING STYLE ANALYSIS WITH DEEPSEEK-V4-PRO ==========");
        SafetyJudge.JudgmentResult judgment = safetyJudge.judgeResponse(analysis, "Style Analysis");
        log.info("Style Judge Verdict: {} | Safe: {} | Discriminatory: {} | Harmful: {} | Quality: {}/5 | Reason: {}",
            judgment.isApproved() ? "APPROVED" : "FLAGGED",
            judgment.isSafe(),
            judgment.isDiscriminatory(),
            judgment.isHarmful(),
            judgment.getQualityScore(),
            judgment.getReason()
        );
        log.info("=================================================================");

        if (!judgment.isSafe() || judgment.isDiscriminatory() || judgment.isHarmful()) {
            log.warn("Style analysis flagged by safety judge - Reason: {}", judgment.getReason());
            throw new RuntimeException("Response failed safety check: " + judgment.getReason());
        }

        if (judgment.getQualityScore() < 2) {
            log.warn("Style analysis quality too low (score: {}), retrying...", judgment.getQualityScore());
            analysis = huggingFaceClient.callModel(prompt);
            judgment = safetyJudge.judgeResponse(analysis, "Style Analysis");
        }

        double styleMatchScore = extractScore(analysis, 60, 95);
        double confidenceScore = extractScore(analysis, 65, 95);

        StyleAnalysisResponse response = StyleAnalysisResponse.builder()
            .styleMatchScore(styleMatchScore)
            .confidenceScore(confidenceScore)
            .analysis(analysis)
            .fashionPersona(extractFashionPersona(analysis))
            .bodyFitRecommendation(extractBodyFitRecommendation(analysis))
            .suitableColors(extractColors(profile.getPreferredColors()))
            .recommendedCategories(extractCategories(analysis))
            .build();

        return response;
    }

    public String generateOutfitRecommendations(UserProfile profile, StyleAnalysisResponse styleAnalysis) {
        String prompt = buildOutfitPrompt(profile, styleAnalysis);
        String recommendations = huggingFaceClient.callModel(prompt);

        // Judge the outfit recommendations for safety
        log.info("========== JUDGING OUTFIT RECOMMENDATIONS WITH DEEPSEEK-V4-PRO (SAFETY JUDGE) ==========");
        SafetyJudge.JudgmentResult judgment = safetyJudge.judgeResponse(recommendations, "Outfit Recommendations");
        log.info("Safety Judge Verdict: {} | Safe: {} | Discriminatory: {} | Harmful: {} | Quality: {}/5 | Reason: {}",
            judgment.isApproved() ? "APPROVED" : "FLAGGED",
            judgment.isSafe(),
            judgment.isDiscriminatory(),
            judgment.isHarmful(),
            judgment.getQualityScore(),
            judgment.getReason()
        );
        log.info("====================================================================================");

        if (!judgment.isSafe() || judgment.isDiscriminatory() || judgment.isHarmful()) {
            log.warn("Outfit recommendations flagged by safety judge - Reason: {}", judgment.getReason());
            throw new RuntimeException("Outfit recommendations failed safety check: " + judgment.getReason());
        }

        if (judgment.getQualityScore() < 2) {
            log.warn("Outfit quality too low (score: {}), retrying...", judgment.getQualityScore());
            recommendations = huggingFaceClient.callModel(prompt);
        }

        // Judge the outfit recommendations for fashion quality with second model
        log.info("========== JUDGING OUTFIT RECOMMENDATIONS WITH LLAMA-2 (FASHION QUALITY JUDGE) ==========");
        String userProfileSummary = String.format(
            "Gender: %s, Age: %s, Body Type: %s, Style: %s, Budget: %s, Occasion: %s",
            profile.getGender(), profile.getAgeGroup(), profile.getBodyType(),
            profile.getStylePreference(), profile.getBudgetRange(), profile.getOccasionType()
        );
        FashionQualityJudge.FashionJudgmentResult fashionJudgment = fashionQualityJudge.judgeRecommendationQuality(
            styleAnalysis.getFashionPersona(),
            recommendations,
            userProfileSummary
        );
        log.info("Fashion Quality Judge Verdict - Coherent: {}, Quality: {}/5, Safe: {}, Diverse: {}, Practical: {}, " +
                "Confidence: {}%, Explanation: {}",
            fashionJudgment.isCoherent() ? "YES" : "NO",
            fashionJudgment.getQualityScore(),
            fashionJudgment.isSafe() ? "YES" : "NO",
            fashionJudgment.isDiverse() ? "YES" : "NO",
            fashionJudgment.isPractical() ? "YES" : "NO",
            fashionJudgment.getConfidenceScore(),
            fashionJudgment.getExplanation()
        );
        log.info("======================================================================================");

        return recommendations;
    }

    public List<OutfitRecommendation> parseOutfitRecommendations(String outfitText) {
        List<OutfitRecommendation> outfits = new java.util.ArrayList<>();

        String[] outfitSections = outfitText.split("(?i)outfit\\s+\\d+:");

        for (int i = 1; i < outfitSections.length && i <= 4; i++) {
            String section = outfitSections[i].trim();
            if (section.isEmpty() || section.length() < 20) continue;

            OutfitRecommendation outfit = parseStructuredOutfitSection(section, i);
            if (outfit != null) {
                outfits.add(outfit);
            }
        }

        if (outfits.isEmpty()) {
            log.warn("Failed to parse outfits, using defaults");
            return createDefaultOutfits();
        }
        return outfits;
    }

    private OutfitRecommendation parseStructuredOutfitSection(String section, int index) {
        List<OutfitRecommendation.OutfitPiece> pieces = new java.util.ArrayList<>();
        String outfitName = "";
        String totalCost = "$0";
        String description = "";

        String[] lines = section.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.matches("(?i).*total.*cost.*")) {
                totalCost = extractCost(line);
            } else if (line.matches("(?i)why:.*") || line.matches("(?i)occasion:.*")) {
                description = line.replaceAll("(?i)(why:|occasion:)", "").trim();
                if (description.length() > 150) {
                    description = description.substring(0, 150);
                }
            } else if (line.contains("-") && (line.contains("$") || line.toLowerCase().contains("brand"))) {
                OutfitRecommendation.OutfitPiece piece = parseStructuredPiece(line);
                if (piece != null) {
                    pieces.add(piece);
                }
            }
        }

        if (outfitName.isEmpty()) {
            outfitName = "Outfit " + index;
        }

        if (pieces.isEmpty()) {
            pieces = createDefaultPieces();
        }

        return OutfitRecommendation.builder()
            .outfitName(outfitName)
            .occasion("Casual")
            .pieces(pieces)
            .totalEstimatedCost(totalCost)
            .description(description.isEmpty() ? "A carefully curated outfit suggestion" : description)
            .build();
    }

    private OutfitRecommendation.OutfitPiece parseStructuredPiece(String line) {
        try {
            // Format: "Category: Brand Name - Product Name ($XX)" or "Category: Brand Name – Product Name ($XX)"
            String[] colonParts = line.split(":", 2);
            if (colonParts.length < 2) return null;

            String category = colonParts[0].trim();
            String rest = colonParts[1].trim();

            // Extract brand and description before cost - handle both "-" and "–"
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "([A-Za-z&]+)\\s*[-–]\\s*(.+?)\\s*\\((\\$[\\d,]+)\\)",
                java.util.regex.Pattern.MULTILINE | java.util.regex.Pattern.DOTALL
            );
            java.util.regex.Matcher matcher = pattern.matcher(rest);

            if (matcher.find()) {
                String brand = matcher.group(1).trim();
                String fullDescription = matcher.group(2).trim();
                String cost = matcher.group(3).trim();

                // Clean up the description
                fullDescription = fullDescription.replaceAll("\\*\\*", "").trim();

                return OutfitRecommendation.OutfitPiece.builder()
                    .category(extractCategoryFromName(category))
                    .brand(brand)
                    .description(fullDescription)
                    .cost(cost)
                    .build();
            }
        } catch (Exception e) {
            log.warn("Failed to parse piece: {} - {}", line, e.getMessage());
        }
        return null;
    }

    private String extractCategoryFromName(String category) {
        String lower = category.toLowerCase().trim();
        if (lower.contains("top")) return "Top";
        if (lower.contains("bottom")) return "Bottoms";
        if (lower.contains("shoe")) return "Shoes";
        if (lower.contains("accessory") || lower.contains("accessori")) return "Accessories";
        return category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase();
    }



    private String extractCost(String text) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return "$" + matcher.group(1);
        }
        return "$0";
    }

    private List<OutfitRecommendation.OutfitPiece> createDefaultPieces() {
        return Arrays.asList(
            OutfitRecommendation.OutfitPiece.builder()
                .category("Top").brand("Zara").description("Satin Camisole").cost("$50").build(),
            OutfitRecommendation.OutfitPiece.builder()
                .category("Bottom").brand("H&M").description("High-Waisted Trousers").cost("$40").build(),
            OutfitRecommendation.OutfitPiece.builder()
                .category("Shoes").brand("Premium").description("Black Pointed-Toe Mules").cost("$150").build()
        );
    }

    private List<OutfitRecommendation> createDefaultOutfits() {
        return Arrays.asList(
            OutfitRecommendation.builder()
                .outfitName("Classic Evening")
                .occasion("Evening")
                .pieces(createDefaultPieces())
                .totalEstimatedCost("$240")
                .description("A timeless evening look combining affordable basics with investment pieces")
                .build()
        );
    }

    public String generateSummaryReport(UserProfile profile, StyleAnalysisResponse styleAnalysis,
                                       String outfitRecommendations) {
        String prompt = buildSummaryPrompt(profile, styleAnalysis, outfitRecommendations);
        String summary = huggingFaceClient.callModel(prompt);

        // Judge the summary report
        log.info("========== JUDGING SUMMARY REPORT WITH DEEPSEEK-V4-PRO ==========");
        SafetyJudge.JudgmentResult judgment = safetyJudge.judgeResponse(summary, "Summary Report");
        log.info("Summary Judge Verdict: {} | Safe: {} | Discriminatory: {} | Harmful: {} | Quality: {}/5 | Reason: {}",
            judgment.isApproved() ? "APPROVED" : "FLAGGED",
            judgment.isSafe(),
            judgment.isDiscriminatory(),
            judgment.isHarmful(),
            judgment.getQualityScore(),
            judgment.getReason()
        );
        log.info("==================================================================");

        if (!judgment.isSafe() || judgment.isDiscriminatory() || judgment.isHarmful()) {
            log.warn("Summary report flagged by safety judge - Reason: {}", judgment.getReason());
            throw new RuntimeException("Summary report failed safety check: " + judgment.getReason());
        }

        if (judgment.getQualityScore() < 2) {
            log.warn("Summary quality too low (score: {}), retrying...", judgment.getQualityScore());
            summary = huggingFaceClient.callModel(prompt);
        }

        return parseSummaryReport(summary);
    }

    private String parseSummaryReport(String rawSummary) {
        if (rawSummary == null || rawSummary.isEmpty()) {
            return "";
        }

        String parsed = rawSummary
            .replaceAll("\\*\\*", "")
            .replaceAll("\\*", "")
            .replaceAll("#{1,6}\\s*", "")
            .trim();

        return parsed;
    }

    private String buildStyleAnalysisPrompt(UserProfile profile) {
        return String.format(
            "You are a professional fashion stylist. Analyze this style profile:\n\n" +
            "PROFILE:\n" +
            "- Gender: %s, Age Group: %s\n" +
            "- Body Type: %s, Skin Tone: %s\n" +
            "- Colors: %s\n" +
            "- Style: %s, Fit: %s\n" +
            "- Occasion: %s, Weather: %s\n" +
            "- Confidence: %s\n\n" +
            "RESPOND WITH EXACTLY THIS STRUCTURE:\n\n" +
            "Fashion Persona: [One clear style name, e.g., 'Minimalist Chic', 'Urban Professional']\n\n" +
            "Body Fit: [One paragraph on what fits their body type best, 2-3 sentences]\n\n" +
            "Style Match Score: [Number 0-100]\n\n" +
            "Confidence Score: [Number 0-100]\n\n" +
            "Each section must start with its label followed by a colon.",
            profile.getGender(), profile.getAgeGroup(), profile.getBodyType(),
            profile.getSkinTone(), profile.getPreferredColors(),
            profile.getStylePreference(), profile.getFitPreference(),
            profile.getOccasionType(), profile.getWeather(),
            profile.getConfidenceLevel()
        );
    }

    private String buildOutfitPrompt(UserProfile profile, StyleAnalysisResponse styleAnalysis) {
        return String.format(
            "You are a professional fashion stylist. Create 3 specific outfit combinations.\n\n" +
            "Use EXACTLY this format (no variations):\n\n" +
            "OUTFIT 1: [Outfit Name]\n" +
            "Top: [Brand Name] - [Specific Product Name] ($XX)\n" +
            "Bottoms: [Brand Name] - [Specific Product Name] ($XX)\n" +
            "Shoes: [Brand Name] - [Specific Product Name] ($XX)\n" +
            "Accessories: [Brand Name] - [Specific Product Name] ($XX)\n" +
            "Total Cost: $XXX\n\n" +
            "OUTFIT 2: [Outfit Name]\n" +
            "[Same format]\n\n" +
            "OUTFIT 3: [Outfit Name]\n" +
            "[Same format]\n\n" +
            "EXAMPLES OF GOOD PRODUCT NAMES:\n" +
            "- Black Satin Camisole\n" +
            "- High-Waisted Wide-Leg Trousers\n" +
            "- Pointed-Toe Mules\n" +
            "- Gold Chain Shoulder Bag\n\n" +
            "PROFILE:\n" +
            "- Body Type: %s\n" +
            "- Style: %s\n" +
            "- Colors: %s\n" +
            "- Budget: %s\n" +
            "- Occasion: %s\n" +
            "- Brands: %s\n\n" +
            "Product names must be specific and descriptive. Include color and style details.",
            profile.getBodyType(), profile.getStylePreference(),
            profile.getPreferredColors(), profile.getBudgetRange(),
            profile.getOccasionType(), profile.getFavoriteBrands()
        );
    }

    private String buildSummaryPrompt(UserProfile profile, StyleAnalysisResponse styleAnalysis,
                                     String outfitRecommendations) {
        return String.format(
            "Create a concise, encouraging styling summary (2-3 paragraphs) for this person:\n\n" +
            "STYLE PERSONA: %s\n" +
            "CONFIDENCE LEVEL: %.0f%%\n\n" +
            "OUTFIT RECOMMENDATIONS:\n%s\n\n" +
            "Write a summary that:\n" +
            "1. Confirms their style identity\n" +
            "2. Explains why the recommendations work\n" +
            "3. Provides confidence-boosting styling tips\n" +
            "4. Encourages them to experiment\n\n" +
            "Be warm, encouraging, and professional.",
            styleAnalysis.getFashionPersona(),
            styleAnalysis.getConfidenceScore(),
            outfitRecommendations
        );
    }

    private String extractFashionPersona(String text) {
        try {
            // Look for pattern like 'The "Nocturnal Minimalist"' or Fashion Persona: "..."
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(?i)(?:fashion persona|the)\\s*[\"\\*]([^\"\\*\\n]+)[\"\\*]",
                java.util.regex.Pattern.MULTILINE
            );
            java.util.regex.Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String persona = matcher.group(1).trim();
                // Remove markdown characters and extra text
                persona = persona.replaceAll("\\*\\*", "").replaceAll("###\\s*\\d+\\.?\\s*", "").trim();
                if (persona.length() > 3 && persona.length() < 100) {
                    return persona;
                }
            }

            // Alternative pattern: just the name after persona
            pattern = java.util.regex.Pattern.compile(
                "(?i)fashion persona:?\\s*([A-Za-z\\s\"]+?)(?:\\n|$|This)",
                java.util.regex.Pattern.MULTILINE
            );
            matcher = pattern.matcher(text);
            if (matcher.find()) {
                String persona = matcher.group(1).trim().replaceAll("[\"\\*]", "");
                if (persona.length() > 3 && persona.length() < 100) {
                    return persona;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract persona", e);
        }
        return "Contemporary Stylist's Choice";
    }

    private String extractBodyFitRecommendation(String text) {
        try {
            // Look for "Body Fit:" followed by text until next section or 2 sentences
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(?i)body fit:?\\s*([^\\n]+(?:\\.[^\\n]*)?)",
                java.util.regex.Pattern.MULTILINE
            );
            java.util.regex.Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String fit = matcher.group(1).trim();
                fit = fit.replaceAll("\\*\\*", "").replaceAll("###.*", "").trim();
                if (fit.length() > 20 && fit.length() < 250) {
                    // Keep only first 1-2 sentences
                    String[] sentences = fit.split("\\.");
                    if (sentences.length > 0) {
                        String result = sentences[0].trim();
                        if (sentences.length > 1 && result.length() < 100) {
                            result = result + ". " + sentences[1].trim();
                        }
                        return result + (result.endsWith(".") ? "" : ".");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract body fit recommendation", e);
        }
        return "Choose fitted styles that complement your unique body shape.";
    }

    private List<String> extractColors(String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) {
            return Arrays.asList("Black", "White", "Navy", "Gray");
        }
        String[] colors = colorString.split(",");
        return Arrays.asList(Arrays.stream(colors)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toArray(String[]::new));
    }

    private List<String> extractCategories(String text) {
        String[] categories = {"dresses", "tops", "pants", "jackets", "shoes", "accessories",
            "blazers", "sweaters", "skirts", "shirts", "outerwear", "layering"};
        List<String> found = Arrays.asList();
        for (String category : categories) {
            if (text.toLowerCase().contains(category)) {
                found = Arrays.asList(found.toArray(new String[0]));
                if (!found.contains(category)) {
                    found = Arrays.asList(Arrays.stream(found.toArray(new String[0]))
                        .filter(c -> !c.equals(category))
                        .toArray(String[]::new));
                }
            }
        }
        return found.isEmpty() ? Arrays.asList("Casual Wear", "Professional", "Accessories") : found;
    }

    private double extractScore(String text, int min, int max) {
        try {
            String lowerText = text.toLowerCase();
            String[] patterns = {"score", "rate", "%", "out of"};

            for (String pattern : patterns) {
                int index = lowerText.indexOf(pattern);
                if (index != -1) {
                    String segment = text.substring(index, Math.min(text.length(), index + 50));
                    String[] words = segment.split("\\s+");
                    for (String word : words) {
                        try {
                            String digits = word.replaceAll("[^0-9]", "");
                            if (!digits.isEmpty()) {
                                double score = Double.parseDouble(digits);
                                if (score >= 0 && score <= 100) {
                                    return Math.max(min, Math.min(max, score));
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Continue
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract score from response", e);
        }
        return (min + max) / 2.0;
    }

    private void validateUserProfile(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("User profile cannot be null");
        }

        try {
            if (profile.getPreferredColors() != null) {
                injectionFilter.validateAndSanitize(profile.getPreferredColors());
            }
            if (profile.getFavoriteBrands() != null) {
                injectionFilter.validateAndSanitize(profile.getFavoriteBrands());
            }
            if (profile.getWardrobePreferences() != null) {
                injectionFilter.validateAndSanitize(profile.getWardrobePreferences());
            }
        } catch (SecurityException e) {
            log.error("Security validation failed: {}", e.getMessage());
            throw new IllegalArgumentException("Input contains suspicious patterns and cannot be processed", e);
        }
    }
}
