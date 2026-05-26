# AI-Stylist Workflow Documentation

## Overview

AI-Stylist uses a **2-3 LLM call workflow** to generate personalized fashion recommendations. The workflow intelligently adapts based on user confidence levels, ensuring recommendations are appropriately tailored.

**Constraint:** Maximum 3 LLM calls per recommendation request.

---

## Workflow Execution Flow

```
┌──────────────────────────────────────────────────────────────┐
│                 USER SUBMITS PROFILE                         │
│  (Gender, Body Type, Skin Tone, Style, Budget, etc.)        │
└────────────────────┬─────────────────────────────────────────┘
                     │
                     ▼
      ┌──────────────────────────────────┐
      │   LLM CALL 1: STYLE ANALYSIS     │
      │  (Analyze user & generate scores)│
      └────────────┬─────────────────────┘
                   │
                   ├─ Prompt: "Analyze user style profile"
                   ├─ Model: Mistral-7B or Gemma-2-2b
                   ├─ Max Tokens: 500
                   └─ Response: JSON with analysis
                   │
                   ▼
      ┌──────────────────────────────────────────┐
      │  PARSE RESPONSE: StyleAnalysisResponse   │
      │  {                                       │
      │    fashionPersona: "...",               │
      │    bodyFitRecommendation: "...",        │
      │    suitableColors: [...],               │
      │    styleMatchScore: 0-100,              │
      │    confidenceScore: 0-100,              │
      │    recommendedCategories: [...]         │
      │  }                                      │
      └────────────┬─────────────────────────────┘
                   │
         ┌─────────▼──────────┐
         │  DECISION POINT    │
         │ IF/ELSE LOGIC      │
         └─────────┬──────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        │ >= 70%             │ < 70%
        │                     │
   ┌────▼────────┐      ┌────▼────────┐
   │  ADVANCED   │      │  BEGINNER   │
   │ Confidence  │      │ Guidance    │
   └────┬────────┘      └────┬────────┘
        │                     │
        ▼                     ▼
    LLM CALL 2           LLM CALL 2
    (Different           (Different
     Prompt)             Prompt)
        │                     │
        └──────────┬──────────┘
                   │
                   ▼
      ┌──────────────────────────────────┐
      │ LLM CALL 2: GENERATE RECOMMENDATIONS
      │ - Advanced OR Beginner based on score
      │ - Max Tokens: 1000
      │ - Response: Outfit combinations
      └────────────┬─────────────────────┘
                   │
                   ▼
      ┌──────────────────────────────────┐
      │   OPTIONAL: LLM CALL 3           │
      │   Generate Summary Report        │
      │   Max Tokens: 500                │
      └────────────┬─────────────────────┘
                   │
                   ▼
      ┌──────────────────────────────────┐
      │  SAVE RECOMMENDATION TO DATABASE │
      │  - All responses                 │
      │  - Scores and metadata           │
      │  - Timestamp                     │
      └────────────┬─────────────────────┘
                   │
                   ▼
      ┌──────────────────────────────────┐
      │   RETURN TO FRONTEND             │
      │   StylistRecommendationResponse  │
      └──────────────────────────────────┘
```

---

## Detailed LLM Call Specifications

### LLM Call 1: Style Analysis

**Purpose:** Analyze user's profile and generate quantified style assessment

**Prompt Template:**
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

**Expected Response:**
```json
{
  "fashion_persona": "Contemporary Elegant with Classic Undertones",
  "body_fit_recommendation": "Well-fitted, tailored pieces that accentuate curves",
  "suitable_colors": ["Navy Blue", "White", "Cream", "Rust Orange", "Burgundy"],
  "style_match_score": 82,
  "confidence_score": 78,
  "recommended_categories": ["Casual", "Business Casual", "Evening Wear"]
}
```

**Validation Rules:**
- Must be valid JSON
- style_match_score: 0-100 (default: 75)
- confidence_score: 0-100 (default: 70)
- suitable_colors: non-empty array
- recommended_categories: non-empty array

**Max Tokens:** 500
**Temperature:** 0.7 (balanced creativity)
**Model:** Mistral-7B (primary) or Gemma-2-2b (fallback)

---

### LLM Call 2: Conditional Recommendations

#### IF Branch: Advanced Recommendations (style_match_score >= 70)

**Purpose:** Provide expert-level, personalized outfit combinations for confident users

**Prompt Template:**
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

Include brand suggestions aligned with their budget level and style preferences.
```

**Expected Response:**
```
OUTFIT 1: Weekend Brunch
- Navy blazer with structured silhouette
- White fitted t-shirt
- Tailored cream trousers
- Burgundy loafers
- Gold minimalist jewelry

OUTFIT 2: Evening Dinner
...

ADVANCED TIPS:
- Navy + Cream is a timeless combination...
- For your body type, structured shoulders...
```

#### ELSE Branch: Beginner-Friendly Guidance (style_match_score < 70)

**Purpose:** Provide simplified, easy-to-follow styling guidance for confidence-building

**Prompt Template:**
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

**Expected Response:**
```
OUTFIT 1: Casual Friday
- Navy jeans (your suitable color!)
- White button-up shirt
- White sneakers
- Simple pendant necklace

BEGINNER TIPS:
- Navy and white are ALWAYS a safe pair
- Start with solid colors, not patterns
- Add ONE accessory at a time
...
```

**Max Tokens:** 1000
**Temperature:** 0.7
**Model:** Mistral-7B (primary) or Gemma-2-2b (fallback)

---

### LLM Call 3: Optional Summary Report

**Purpose:** Generate an encouraging, personalized summary explaining recommendations

**Prompt Template:**
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

**Expected Response:**
```
Your fashion persona suggests a Contemporary Elegant aesthetic with a strong 
foundation in classic styling. Based on your preference for well-fitted clothing 
and your skin tone undertones, we've selected outfit combinations that highlight 
your natural strengths while keeping you comfortable and confident.

Your style profile indicates a balanced approach to fashion—you appreciate timeless 
pieces with contemporary touches. This is fantastic because it means you can build 
a versatile wardrobe that works across multiple occasions...

As you build your personal style, remember that the best outfit is one that makes 
YOU feel confident and comfortable. Our recommendations serve as starting points. 
Don't be afraid to experiment with the color combinations we suggest...
```

**Max Tokens:** 500
**Temperature:** 0.8 (slightly more creative for tone)
**Model:** Mistral-7B (primary) or Gemma-2-2b (fallback)

---

## Fallback & Error Handling

### Model Fallback Strategy

```
Primary Model Attempt: mistralai/Mistral-7B-Instruct-v0.3
    ↓
    If success → Use response
    ↓
    If timeout/error:
        ↓
        Attempt Fallback: google/gemma-2-2b-it
            ↓
            If success → Use response
            ↓
            If error → Throw AIServiceException
```

### JSON Parsing Fallback

If LLM Call 1 response is malformed JSON:

```java
try {
    parse JSON response
} catch (Exception) {
    return DEFAULT_STYLE_ANALYSIS {
        fashionPersona: "Contemporary Classic",
        styleMatchScore: 75,
        confidenceScore: 70,
        // ... other defaults
    }
}
```

### Token Count Management

- LLM Call 1: 500 tokens (analysis, JSON is concise)
- LLM Call 2: 1000 tokens (detailed recommendations)
- LLM Call 3: 500 tokens (summary prose)

**Total:** ~2000 tokens per recommendation (cost-controlled)

---

## Performance Metrics

### Timing Expectations
- LLM Call 1: 3-5 seconds (analysis)
- LLM Call 2: 5-10 seconds (detailed recommendations)
- LLM Call 3: 3-5 seconds (summary)
- Database save: 200ms
- **Total:** ~15-20 seconds per recommendation

### API Rate Limits (Hugging Face Free Tier)
- Request limit: Reasonable (Hugging Face managed)
- Timeout: 120 seconds per API call
- Retry: Automatic with exponential backoff

### Quality Metrics
- JSON parse success rate: >95%
- Score validity (0-100): 100%
- Response completeness: 99%

---

## Workflow Decision Logic (IF/ELSE)

### Score Interpretation

**IF styleMatchScore >= 70:**
- User has clear style identity
- Provides excellent starting point
- Can accommodate more complex, trendy suggestions
- User is ready for advanced combinations

**ELSE styleMatchScore < 70:**
- User is still discovering style
- Needs simplified, foundational guidance
- Capsule wardrobe approach
- Confidence-building is priority

### Confidence Score Usage

**confidenceScore >= 70:** Advanced styling available
**confidenceScore < 70:** Beginner-friendly approach
**confidenceScore 30-50:** Extra encouragement in summary

---

## Example Workflow Execution

### Scenario: New User, Developing Style

**Input:**
```json
{
  "gender": "FEMALE",
  "ageGroup": "TWENTIES",
  "bodyType": "PEAR",
  "skinTone": "MEDIUM",
  "budgetRange": "MODERATE",
  "occasionType": "CASUAL",
  "preferredColors": "Pastels, Neutral Tones",
  "stylePreference": "MINIMALIST",
  "weather": "WARM",
  "confidenceLevel": "LOW"
}
```

**LLM Call 1 Output:**
```json
{
  "fashion_persona": "Understated Minimalist",
  "bodyFitRecommendation": "A-line and straight-cut bottoms to balance pear shape",
  "suitableColors": ["Cream", "Soft Pink", "Light Blue", "Beige"],
  "styleMatchScore": 58,
  "confidenceScore": 45,
  "recommendedCategories": ["Casual", "Weekend Wear"]
}
```

**Decision:**
- styleMatchScore (58) < 70 → **Use BEGINNER Branch**

**LLM Call 2 Output (Beginner Branch):**
```
OUTFIT 1: Casual Coffee Date
- Cream linen t-shirt
- Beige A-line shorts
- Cream canvas sneakers
- Simple drop earrings

BEGINNER TIPS:
- Your pear shape looks amazing in A-line bottoms
- Cream and beige are ALWAYS a safe combination...
```

**LLM Call 3 Output (Summary):**
```
Your minimalist aesthetic is perfect for creating a versatile, stress-free wardrobe. 
The beauty of minimalism is that fewer pieces, chosen thoughtfully, create more 
outfit combinations. As you explore your style, start with the basics we recommended...
```

**Database Save:**
```sql
INSERT INTO recommendations VALUES (
  null,
  userId,
  "Understated Minimalist...",
  "OUTFIT 1: Casual Coffee Date...",
  "Your minimalist aesthetic...",
  58,
  45,
  numberOfLlmCalls: 3,
  isAdvancedRecommendation: false,
  "mistralai/Mistral-7B-Instruct-v0.3",
  NOW()
);
```

---

## Testing the Workflow

### Postman Test Flow

1. **Signup & Login** → Get JWT token
2. **POST /stylist/recommend** with test profile
3. **Check Response:**
   - ✓ Recommendation ID returned
   - ✓ styleAnalysis populated
   - ✓ outfitRecommendations populated
   - ✓ numberOfLlmCalls = 2 or 3
   - ✓ isAdvancedRecommendation boolean correct

### Frontend Test Flow

1. Fill stylist form completely
2. Submit form
3. Monitor network tab (should see 1 API call)
4. Verify response appears in UI
5. Navigate to recommendations list
6. Click on recommendation detail

### Edge Cases to Test

- **High confidence user (score 90+):** Should get advanced recommendations
- **Low confidence user (score 30-50):** Should get beginner guidance  
- **Malformed JSON response:** Should use default analysis
- **Timeout from primary model:** Should automatically use fallback
- **Both models fail:** Should return error to user with graceful message

---

## Future Improvements

1. **Image-based style detection:** Use computer vision for wardrobe analysis
2. **Shopping integration:** Link recommendations to actual products
3. **Seasonal updates:** Rotate recommendations based on season
4. **Trend analysis:** Incorporate current fashion trends
5. **Wardrobe inventory:** Track owned items and suggest new combos
6. **Real-time chat:** Conversational AI for quick style questions
7. **Virtual try-on:** AR integration for visualizing outfits
8. **Community styling:** Share recommendations with friends
