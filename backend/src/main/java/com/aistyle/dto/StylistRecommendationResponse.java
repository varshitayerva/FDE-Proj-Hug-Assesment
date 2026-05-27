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
public class StylistRecommendationResponse {
    private Long recommendationId;
    private StyleAnalysisResponse styleAnalysis;
    private List<OutfitRecommendation> outfitRecommendations;
    private String summaryReport;
    private Integer numberOfLlmCalls;
    private Boolean isAdvancedRecommendation;
    private String message;
}
