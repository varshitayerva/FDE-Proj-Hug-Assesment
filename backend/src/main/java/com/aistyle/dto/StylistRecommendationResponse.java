package com.aistyle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StylistRecommendationResponse {
    private Long recommendationId;
    private Object styleAnalysis;
    private String outfitRecommendations;
    private String summaryReport;
    private Integer numberOfLlmCalls;
    private Boolean isAdvancedRecommendation;
    private String message;
}
