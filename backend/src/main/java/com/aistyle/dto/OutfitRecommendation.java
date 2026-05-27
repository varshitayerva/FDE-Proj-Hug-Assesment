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
public class OutfitRecommendation {
    private String outfitName;
    private String occasion;
    private List<OutfitPiece> pieces;
    private String totalEstimatedCost;
    private String description;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OutfitPiece {
        private String category;      // e.g., "Top", "Bottoms", "Shoes"
        private String brand;         // e.g., "Zara", "H&M"
        private String description;   // e.g., "Black Satin Camisole"
        private String cost;          // e.g., "$50"
    }
}
