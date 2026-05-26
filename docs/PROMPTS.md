# AI-Stylist Prompt Engineering Documentation

## Overview

This document contains all prompts used in the AI-Stylist application for Hugging Face Inference API calls. Each prompt is carefully engineered for accuracy, consistency, and safety.

---

## Prompt 1: Style Analysis

### Purpose
Analyze user's fashion profile and generate quantified style assessment with JSON response.

### Model Used
- **Primary:** mistralai/Mistral-7B-Instruct-v0.3
- **Fallback:** google/gemma-2-2b-it

### Parameters
- **Max Tokens:** 500
- **Temperature:** 0.7
- **Top P:** 0.95

### Prompt Template

```
Analyze the following user's fashion style and provide recommendations. 
Return ONLY a valid JSON object with NO additional text.

User Profile:
- Gender: {gender}
- Age Group: {ageGroup}
- Body Type: {bodyType}
- Skin Tone: {skinTone}
- Budget: {budgetRange}
- Occasion: {occasionType}
- Preferred Colors: {preferredColors}
- Style Preference: {stylePreference}
- Weather: {weather}
- Fashion Confidence: {confidenceLevel}
- Favorite Brands: {favoriteBrands}
- Fit Preference: {fitPreference}
- Wardrobe Preferences: {wardrobePreferences}

Return ONLY this JSON structure, no other text:
{
  "fashion_persona": "A brief description of the user's fashion persona",
  "body_fit_recommendation": "Specific fit recommendations for their body type",
  "suitable_colors": ["color1", "color2", "color3"],
  "style_match_score": 75,
  "confidence_score": 70,
  "recommended_categories": ["category1", "category2", "category3"]
}
```

### Java Implementation

```java
private String buildStyleAnalysisPrompt(UserProfile profile) {
    return String.format(
        "Analyze the following user's fashion style and provide recommendations. " +
        "Return ONLY a valid JSON object with NO additional text.\n\n" +
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
```

### Example Input

```json
{
  "gender": "FEMALE",
  "ageGroup": "TWENTIES",
  "bodyType": "HOURGLASS",
  "skinTone": "MEDIUM",
  "budgetRange": "MODERATE",
  "occasionType": "CASUAL",
  "preferredColors": "Blue, White, Earth Tones",
  "stylePreference": "CLASSIC",
  "weather": "WARM",
  "confidenceLevel": "MEDIUM",
  "favoriteBrands": "Zara, H&M",
  "fitPreference": "REGULAR",
  "wardrobePreferences": "Comfortable and stylish"
}
```

### Example Output

```json
{
  "fashion_persona": "Contemporary Elegant with Classic Undertones",
  "body_fit_recommendation": "Well-fitted, tailored pieces that accentuate curves",
  "suitable_colors": ["Navy Blue", "White", "Cream", "Rust Orange"],
  "style_match_score": 82,
  "confidence_score": 78,
  "recommended_categories": ["Casual", "Business Casual", "Evening Wear"]
}
```

### Key Design Decisions

1. **JSON-only Output:** Forces structured response, easier to parse
2. **Explicit Score Range:** 0-100 makes validation easy
3. **Multiple Fields:** Captures different aspects of style
4. **Body Type Specific:** Ensures fit recommendations are personalized

---

## Prompt 2A: Advanced Outfit Recommendations (IF: scoreMatchScore >= 70)

### Purpose
Generate expert-level, sophisticated outfit combinations for confident users.

### Model Used
- **Primary:** mistralai/Mistral-7B-Instruct-v0.3
- **Fallback:** google/gemma-2-2b-it

### Parameters
- **Max Tokens:** 1000
- **Temperature:** 0.7
- **Top P:** 0.95

### Prompt Template

```
Based on this user's fashion profile and analysis, provide outfit recommendations 
for someone with HIGH fashion confidence.

User Analysis:
- Fashion Persona: {fashionPersona}
- Style Match Score: {styleMatchScore}%
- Suitable Colors: {suitableColors}
- Recommended Categories: {recommendedCategories}

Provide:
1. PREMIUM PERSONALIZED OUTFIT COMBINATIONS (3-4 combinations)
   - Specific clothing items
   - Layering suggestions
   - Seasonal appropriateness
   
2. MATCHING ACCESSORIES & STYLING TIPS
   - Jewelry recommendations
   - Footwear suggestions
   - Bag/accessory pairings
   
3. ADVANCED COLOR PAIRING TECHNIQUES
   - Color combination theory
   - Complementary palettes
   - Trend-conscious color choices
   
4. CONFIDENCE STYLING TIPS
   - How to wear combinations with confidence
   - Mix and match possibilities
   - Investment pieces vs. seasonal trends

Include brand suggestions and price guidance for their budget level and style preferences.
```

### Java Implementation

```java
private String buildOutfitPrompt(UserProfile profile, StyleAnalysisResponse analysis) {
    String basePrompt = String.format(
        "Based on this user's fashion profile and analysis, provide outfit recommendations " +
        "for someone with HIGH fashion confidence.\n\n" +
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
            "Provide:\n" +
            "1. PREMIUM PERSONALIZED OUTFIT COMBINATIONS (3-4 combinations)\n" +
            "   - Specific clothing items\n" +
            "   - Layering suggestions\n" +
            "   - Seasonal appropriateness\n" +
            "2. MATCHING ACCESSORIES & STYLING TIPS\n" +
            "   - Jewelry recommendations\n" +
            "   - Footwear suggestions\n" +
            "   - Bag/accessory pairings\n" +
            "3. ADVANCED COLOR PAIRING TECHNIQUES\n" +
            "   - Color combination theory\n" +
            "   - Complementary palettes\n" +
            "   - Trend-conscious color choices\n" +
            "4. CONFIDENCE STYLING TIPS\n" +
            "   - How to wear combinations with confidence\n" +
            "   - Mix and match possibilities\n" +
            "   - Investment pieces vs. seasonal trends\n\n" +
            "Include brand suggestions and price guidance for their budget level.";
    } else {
        // Beginner branch (see Prompt 2B)
    }
}
```

### Example Input

```
User Analysis:
- Fashion Persona: Contemporary Elegant with Classic Undertones
- Style Match Score: 82%
- Suitable Colors: Navy Blue, White, Cream, Rust Orange
- Recommended Categories: Casual, Business Casual, Evening Wear
```

### Example Output

```
OUTFIT 1: Weekend Brunch (Casual)
- Navy blazer with structured silhouette (Mango)
- White fitted t-shirt (Uniqlo)
- Tailored cream trousers (Banana Republic)
- Burgundy loafers (Cole Haan)
- Gold minimalist jewelry set
- Structured leather crossbody bag (Coach)

OUTFIT 2: Business Casual
[...]

ADVANCED COLOR THEORY:
- Navy + Cream: A timeless, sophisticated combination that works in any season
- Adding Rust: Creates depth and visual interest without overwhelming the palette
- White as the base: Draws attention upward, creating a balanced silhouette

STYLING TIPS:
- These combinations are versatile enough for mixing and matching
- Invest in the blazer and trousers as anchors
- Seasonal pieces: Rotate sweaters and accessories
```

---

## Prompt 2B: Beginner Outfit Recommendations (ELSE: scoreMatchScore < 70)

### Purpose
Generate simplified, confidence-building outfit suggestions for users developing their style.

### Model Used
- **Primary:** mistralai/Mistral-7B-Instruct-v0.3
- **Fallback:** google/gemma-2-2b-it

### Parameters
- **Max Tokens:** 1000
- **Temperature:** 0.7
- **Top P:** 0.95

### Prompt Template

```
Based on this user's fashion profile and analysis, provide outfit recommendations 
for someone developing their personal style.

User Analysis:
- Fashion Persona: {fashionPersona}
- Style Match Score: {styleMatchScore}%
- Suitable Colors: {suitableColors}
- Recommended Categories: {recommendedCategories}

Provide:
1. EASY OUTFIT COMBINATIONS (3-4 combinations)
   - Simple, foolproof pairings
   - Basic clothing items only
   - Minimal complexity
   
2. BEGINNER STYLING HELP
   - "Start with basic neutral colors"
   - "Add ONE accent piece"
   - "Simple rules to follow"
   
3. SAFE COLOR MATCHING TIPS
   - Monochromatic combinations
   - Neutral + one pop of color
   - Easy color coordination rules
   
4. WARDROBE IMPROVEMENT SUGGESTIONS
   - Essential pieces to buy first
   - How to build a capsule wardrobe
   - Budget-friendly options

Keep recommendations simple, confidence-building, and achievable for a beginner.
```

### Java Implementation

```java
private String buildOutfitPrompt(UserProfile profile, StyleAnalysisResponse analysis) {
    // ... basePrompt setup (same as above)
    
    if (analysis.getStyleMatchScore() < 70) {
        return basePrompt +
            "Provide:\n" +
            "1. EASY OUTFIT COMBINATIONS (3-4 combinations)\n" +
            "   - Simple, foolproof pairings\n" +
            "   - Basic clothing items only\n" +
            "   - Minimal complexity\n" +
            "2. BEGINNER STYLING HELP\n" +
            "   - Start with basic neutral colors\n" +
            "   - Add ONE accent piece\n" +
            "   - Simple rules to follow\n" +
            "3. SAFE COLOR MATCHING TIPS\n" +
            "   - Monochromatic combinations\n" +
            "   - Neutral + one pop of color\n" +
            "   - Easy color coordination rules\n" +
            "4. WARDROBE IMPROVEMENT SUGGESTIONS\n" +
            "   - Essential pieces to buy first\n" +
            "   - How to build a capsule wardrobe\n" +
            "   - Budget-friendly options\n\n" +
            "Keep recommendations simple, confidence-building, and achievable.";
    }
}
```

### Example Input

```
User Analysis:
- Fashion Persona: Understated Minimalist
- Style Match Score: 58%
- Suitable Colors: Cream, Soft Pink, Light Blue, Beige
- Recommended Categories: Casual, Weekend Wear
```

### Example Output

```
OUTFIT 1: Casual Coffee Date
- Cream linen t-shirt (Uniqlo)
- Beige A-line shorts (Gap)
- White canvas sneakers (Adidas)
- Simple drop earrings

OUTFIT 2: Weekend Brunch
- Light blue button-up shirt (H&M)
- White jeans (Levi's 501)
- White leather sneakers (Superga)
- Simple necklace

BEGINNER TIPS:
✓ Your pear shape looks amazing in A-line bottoms - use them as your foundation
✓ Cream and beige are ALWAYS a safe combination - you can't go wrong
✓ Start with solid colors, not patterns - they're easier to mix and match
✓ White is your best friend - it works with everything

BUILDING YOUR CAPSULE WARDROBE:
Essential Pieces (Buy First):
1. White t-shirt (2-3)
2. Beige or cream trousers
3. Light blue button-up
4. White sneakers
5. Denim jeans

Budget-Friendly Brands:
- Uniqlo (basics)
- H&M (trendy pieces)
- Old Navy (good fits)
- Gap (classic items)
```

---

## Prompt 3: Summary Report

### Purpose
Generate an encouraging, personalized summary explaining recommendations and building confidence.

### Model Used
- **Primary:** mistralai/Mistral-7B-Instruct-v0.3
- **Fallback:** google/gemma-2-2b-it

### Parameters
- **Max Tokens:** 500
- **Temperature:** 0.8 (slightly more creative for tone)
- **Top P:** 0.95

### Prompt Template

```
Create a brief stylist summary report based on these recommendations.

User's Fashion Persona: {fashionPersona}
Style Analysis Score: {styleMatchScore}%
Confidence Level Assessment: {confidenceScore}%
Recommendations Provided: {outfitRecommendations}

Write a 2-3 paragraph summary that includes:

1. WHY THESE RECOMMENDATIONS WERE SELECTED
   - What aspects of their profile guided the choices
   - How the recommendations align with their body type and skin tone
   
2. OVERALL STYLE EXPLANATION
   - Their fashion direction and identity
   - Key style characteristics
   - Personal style strengths
   
3. CONFIDENCE-BUILDING TIPS
   - Encouragement for fashion journey
   - How to develop personal style further
   - Tips for trying new combinations

Keep the tone warm, encouraging, and positive. Focus on empowerment.
```

### Java Implementation

```java
private String buildSummaryPrompt(UserProfile profile, 
                                 StyleAnalysisResponse analysis,
                                 String recommendations) {
    return String.format(
        "Create a brief stylist summary report based on these recommendations.\n\n" +
        "User's Fashion Persona: %s\n" +
        "Style Analysis Score: %d%%\n" +
        "Confidence Level Assessment: %d%%\n" +
        "Recommendations Provided: %s\n\n" +
        "Write a 2-3 paragraph summary that includes:\n" +
        "1. WHY THESE RECOMMENDATIONS WERE SELECTED\n" +
        "   - What aspects of their profile guided the choices\n" +
        "   - How the recommendations align with their body type and skin tone\n" +
        "2. OVERALL STYLE EXPLANATION\n" +
        "   - Their fashion direction and identity\n" +
        "   - Key style characteristics\n" +
        "   - Personal style strengths\n" +
        "3. CONFIDENCE-BUILDING TIPS\n" +
        "   - Encouragement for fashion journey\n" +
        "   - How to develop personal style further\n" +
        "   - Tips for trying new combinations\n\n" +
        "Keep the tone warm, encouraging, and positive. Focus on empowerment.",
        analysis.getFashionPersona(),
        analysis.getStyleMatchScore(),
        analysis.getConfidenceScore(),
        recommendations
    );
}
```

### Example Output

```
Your fashion persona suggests a Contemporary Elegant aesthetic with a strong foundation 
in classic styling. Your preference for well-fitted clothing combined with your body 
type creates a perfect canvas for refined, timeless pieces. The recommendations selected 
emphasize your natural strengths—your medium skin tone looks beautiful in the suggested 
color palette, and your body type shines in tailored, structured silhouettes.

Your style profile indicates a balanced approach to fashion—you appreciate timeless 
pieces with contemporary touches. This is fantastic because it means you can build a 
versatile wardrobe that works across multiple occasions, from casual weekends to 
professional settings. Your fashion confidence is already strong, and these recommendations 
will help you solidify your style direction with confidence.

As you continue your fashion journey, remember that the best outfit is one that makes 
YOU feel confident and comfortable. Our recommendations serve as starting points—don't 
be afraid to experiment, mix pieces, and adapt combinations to match your mood and 
lifestyle. Your developing style sense is an asset; trust your instincts and have fun 
with fashion!
```

---

## Prompt Engineering Best Practices Used

### 1. **Explicit Output Format**
- Specify JSON structure for Call 1
- Use numbered lists for Calls 2 & 3
- Prevents hallucination and parsing errors

### 2. **Clear Role Definition**
- "For someone with HIGH fashion confidence" (Call 2A)
- "For someone developing personal style" (Call 2B)
- Sets expectations for tone and depth

### 3. **Constraint Setting**
- "3-4 combinations" - prevents too-long responses
- "NO additional text" - forces JSON compliance
- "Keep simple" - for beginner guidance

### 4. **Contextual Information**
- Include actual user data in prompts
- Reference previous analysis results
- Creates cohesive, connected responses

### 5. **Temperature Tuning**
- 0.7 for balanced analysis and recommendations
- 0.8 for summary (slightly more personality)
- Prevents both hallucination and robotic output

### 6. **Tone Specification**
- "Warm, encouraging, and positive"
- "Confidence-building"
- Ensures user-friendly output

---

## Prompt Variations & A/B Testing

### Alternative Prompt 1 (More Structured JSON)

```json
{
  "fashion_persona": "string",
  "body_fit_recommendation": "string",
  "suitable_colors": ["string"],
  "style_match_score": 0,
  "confidence_score": 0,
  "recommended_categories": ["string"]
}
```

**Pros:** Very clear structure
**Cons:** Less natural language context

### Alternative Prompt 2 (Conversational Output)

Instead of lists, narrative format for Call 2 output. Result: More engaging but harder to parse programmatically.

---

## Monitoring & Improvement

### Key Metrics

1. **JSON Parse Success Rate:** Target >95%
2. **User Satisfaction:** Track via ratings/feedback
3. **Score Stability:** Ensure consistent 0-100 ranges
4. **Response Time:** Monitor API latency

### Feedback Loop

1. Collect user ratings of recommendations
2. Identify poorly-rated recommendations
3. Analyze patterns in low-quality responses
4. Adjust prompts and re-test
5. A/B test new prompt variations

---

## Future Prompt Enhancements

1. **Multi-language Support:** Translate prompts to Spanish, French, German
2. **Seasonal Updates:** Different prompts for winter/summer
3. **Trend Integration:** Include current fashion trends in recommendations
4. **Social Engagement:** "Share with friends" context in prompts
5. **Budget-Specific:** More granular prompts for different budget levels

---

## Prompt Testing Checklist

- [ ] Does response parse as valid JSON (Call 1)?
- [ ] Are scores within 0-100 range?
- [ ] Are colors from suitable_colors list?
- [ ] Are recommendations specific and actionable?
- [ ] Does tone match expected (professional for Call 1, warm for Call 3)?
- [ ] No secrets, API keys, or sensitive data in responses?
- [ ] Response time < 10 seconds?
- [ ] Appropriate length (not too short, not too long)?

---

## Troubleshooting Prompts

### Issue: JSON Parse Fails
**Solution:** Increase temperature slightly (0.7 → 0.75), add more explicit formatting instructions

### Issue: Recommendations Too Generic
**Solution:** Add more context to prompt, include specific user data earlier in prompt

### Issue: Scores Always 50
**Solution:** Model may be hedging. Be more specific about high/low confidence scenarios

### Issue: Response Timeout
**Solution:** Reduce max_tokens or use fallback model immediately

### Issue: Repeated Recommendations
**Solution:** Add "Use different outfit combinations than previous recommendations"
