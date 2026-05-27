package com.aistyle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StyleAnalysisResponse {
    private Double styleMatchScore;
    private Double confidenceScore;
    private String analysis;
    private String fashionPersona;
    private String bodyFitRecommendation;
    private List<String> suitableColors;
    private List<String> recommendedCategories;
}
