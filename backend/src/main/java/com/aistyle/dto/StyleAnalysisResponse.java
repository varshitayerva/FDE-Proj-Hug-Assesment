package com.aistyle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StyleAnalysisResponse {
    private Double styleMatchScore;
    private Double confidenceScore;
    private String analysis;
}
