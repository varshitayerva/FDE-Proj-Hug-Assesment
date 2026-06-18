package com.aistyle.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Random;

@Slf4j
@Component
public class MockHuggingFaceClient {

    private static final Random random = new Random();

    public String generateMockStyleAnalysis(String prompt) {
        log.info("Generating mock style analysis response");
        return """
            The style profile analysis reveals:\n
            - Style Match Score: 85/100\n
            - Primary Style: Minimalist with Modern Influences\n
            - Color Palette Recommendation: Neutral base with jewel tone accents\n
            - Recommended Fabrics: Cotton blends, linen, structured knits\n
            - Best Silhouettes: Fitted, clean lines, A-line cuts\n
            - Confidence Level: High confidence in personalized recommendations\n
            - Personal Style Strengths: Ability to carry monochromatic looks, good proportions\n
            - Development Areas: Experimentation with pattern mixing\n
            - Overall Assessment: Well-defined aesthetic with room for sophisticated experimentation""";
    }

    public String generateMockOutfitRecommendations(String prompt) {
        log.info("Generating mock outfit recommendations");
        return """
            ## OUTFIT RECOMMENDATIONS\n
            \n
            ### Outfit 1: Professional Elegance\n
            - Top: Crisp white button-down shirt\n
            - Bottom: Navy tailored trousers\n
            - Shoes: Black leather loafers\n
            - Accessories: Minimalist gold watch\n
            - Estimated Cost: $250\n
            - Occasion: Office, formal meetings\n
            \n
            ### Outfit 2: Casual Sophistication\n
            - Top: Soft gray knit sweater\n
            - Bottom: Black ankle pants\n
            - Shoes: White minimalist sneakers\n
            - Accessories: Neutral canvas tote\n
            - Estimated Cost: $180\n
            - Occasion: Weekend brunch, casual outings\n
            \n
            ### Outfit 3: Smart Casual\n
            - Top: Cream linen shirt\n
            - Bottom: Beige chinos\n
            - Shoes: Brown leather sandals\n
            - Accessories: Woven belt\n
            - Estimated Cost: $200\n
            - Occasion: Summer events, relaxed gatherings\n
            \n
            ### Outfit 4: Evening Ready\n
            - Top: Black fitted blouse\n
            - Bottom: Charcoal slim-fit skirt\n
            - Shoes: Heeled ankle boots\n
            - Accessories: Simple drop earrings\n
            - Estimated Cost: $320\n
            - Occasion: Dinner dates, evening events\n
            \n
            ### Outfit 5: Weekend Comfort\n
            - Top: Oversized striped tee\n
            - Bottom: Light blue denim\n
            - Shoes: White canvas slip-ons\n
            - Accessories: Denim jacket\n
            - Estimated Cost: $150\n
            - Occasion: Shopping, leisure activities""";
    }

    public String generateMockSummaryReport(String prompt) {
        log.info("Generating mock summary report");
        return """
            FASHION STYLIST SUMMARY REPORT\n
            ===============================\n
            \n
            WARDROBE ASSESSMENT:\n
            Your personal style demonstrates a strong preference for clean lines and neutral tones, which creates a cohesive and versatile wardrobe foundation. This minimalist approach allows for easy mix-and-match combinations and timeless appeal.\n
            \n
            KEY INSIGHTS:\n
            1. Your style philosophy favors quality over quantity\n
            2. You have excellent color coordination instincts\n
            3. Fit and proportion are your strongest styling assets\n
            4. You would benefit from introducing strategic pattern mixing\n
            \n
            RECOMMENDATIONS:\n
            - Invest in quality basics (neutral tees, well-fitting jeans, blazers)\n
            - Experiment with texture variation (linen, corduroy, wool blends)\n
            - Add one statement piece per season\n
            - Consider a capsule wardrobe approach for efficiency\n
            \n
            BUDGET ALLOCATION:\n
            - 60% Essential basics and workwear\n
            - 25% Versatile accessories and layering pieces\n
            - 15% Statement items and seasonal updates\n
            \n
            NEXT STEPS:\n
            1. Review your current wardrobe against recommendations\n
            2. Identify gaps in your collection\n
            3. Shop intentionally with the provided outfit combinations\n
            4. Revisit recommendations seasonally\n
            \n
            This analysis is tailored to your preferences and lifestyle needs.""";
    }
}
