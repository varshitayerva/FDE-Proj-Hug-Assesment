# LangSmith Phase 2: Evaluation Dataset

This document defines the evaluation dataset for Phase 2 analysis - 20 test cases covering various user profiles and scenarios.

## Test Cases

### Scenario 1: Budget-Conscious College Student
```json
{
  "id": "test_001",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TEENS",
    "bodyType": "PEAR",
    "skinTone": "LIGHT",
    "budgetRange": "LOW",
    "occasionType": "CASUAL",
    "preferredColors": "Black, White, Denim",
    "stylePreference": "CASUAL",
    "weather": "MODERATE",
    "confidenceLevel": "MEDIUM",
    "favoriteBrands": "H&M, Forever 21, UNIQLO",
    "fitPreference": "RELAXED",
    "wardrobePreferences": "Comfortable, Affordable"
  },
  "expected_behavior": "Budget-friendly casual outfit with affordable brands",
  "metrics_to_track": [
    "Budget compliance",
    "Brand appropriateness",
    "Style consistency"
  ]
}
```

### Scenario 2: Luxury Wedding Guest
```json
{
  "id": "test_002",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "THIRTIES",
    "bodyType": "HOURGLASS",
    "skinTone": "MEDIUM",
    "budgetRange": "HIGH",
    "occasionType": "FORMAL_EVENT",
    "preferredColors": "Gold, Burgundy, Navy",
    "stylePreference": "ELEGANT",
    "weather": "COLD",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Gucci, Calvin Klein, Zara",
    "fitPreference": "FITTED",
    "wardrobePreferences": "Sophisticated, Luxury"
  },
  "expected_behavior": "Elegant formal outfit with luxury brands suitable for wedding",
  "metrics_to_track": [
    "Occasion appropriateness",
    "Style match",
    "Budget luxury",
    "Color harmony"
  ]
}
```

### Scenario 3: Business Professional
```json
{
  "id": "test_003",
  "user_profile": {
    "gender": "Male",
    "ageGroup": "FORTIES",
    "bodyType": "RECTANGULAR",
    "skinTone": "DARK",
    "budgetRange": "MEDIUM",
    "occasionType": "BUSINESS",
    "preferredColors": "Black, Gray, Blue",
    "stylePreference": "FORMAL",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Hugo Boss, Brooks Brothers, Banana Republic",
    "fitPreference": "TAILORED",
    "wardrobePreferences": "Professional, Conservative"
  },
  "expected_behavior": "Professional business attire with neutral colors",
  "metrics_to_track": [
    "Business appropriateness",
    "Color conservatism",
    "Tailoring fit"
  ]
}
```

### Scenario 4: Beach Vacation Wear
```json
{
  "id": "test_004",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TWENTIES",
    "bodyType": "ATHLETIC",
    "skinTone": "MEDIUM",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Bright, Neon, Pastels",
    "stylePreference": "CASUAL",
    "weather": "HOT",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Shein, Urban Outfitters, Banana Republic",
    "fitPreference": "RELAXED",
    "wardrobePreferences": "Breathable, Colorful"
  },
  "expected_behavior": "Light, breathable outfits suitable for hot weather",
  "metrics_to_track": [
    "Weather appropriateness",
    "Breathing fabric recommendation",
    "Color vibrancy"
  ]
}
```

### Scenario 5: Winter Formal Event
```json
{
  "id": "test_005",
  "user_profile": {
    "gender": "Male",
    "ageGroup": "THIRTIES",
    "bodyType": "OVAL",
    "skinTone": "LIGHT",
    "budgetRange": "HIGH",
    "occasionType": "FORMAL_EVENT",
    "preferredColors": "Charcoal, Navy, Black",
    "stylePreference": "FORMAL",
    "weather": "COLD",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Dior, Tom Ford, Ralph Lauren",
    "fitPreference": "TAILORED",
    "wardrobePreferences": "Sophisticated, Warm"
  },
  "expected_behavior": "Warm formal outfit with sophisticated styling",
  "metrics_to_track": [
    "Warmth recommendation",
    "Formality level",
    "Color sophistication"
  ]
}
```

### Scenario 6: Streetwear Enthusiast
```json
{
  "id": "test_006",
  "user_profile": {
    "gender": "Male",
    "ageGroup": "TEENS",
    "bodyType": "LEAN",
    "skinTone": "DARK",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Black, White, Red, Graphic Prints",
    "stylePreference": "URBAN",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Supreme, Off-White, Stüssy, Nike",
    "fitPreference": "OVERSIZED",
    "wardrobePreferences": "Trendy, Urban, Streetwear"
  },
  "expected_behavior": "Trendy streetwear outfit with urban flair",
  "metrics_to_track": [
    "Trend accuracy",
    "Brand authenticity",
    "Oversized fit"
  ]
}
```

### Scenario 7: Plus Size Confidence Booster
```json
{
  "id": "test_007",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "FORTIES",
    "bodyType": "PEAR_PLUS",
    "skinTone": "LIGHT",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Jewel tones, Prints",
    "stylePreference": "COMFORTABLE",
    "weather": "MODERATE",
    "confidenceLevel": "MEDIUM",
    "favoriteBrands": "Torrid, Lane Bryant, Old Navy",
    "fitPreference": "FLATTERING",
    "wardrobePreferences": "Body-positive, Inclusive, Flattering"
  },
  "expected_behavior": "Flattering outfits celebrating body positivity",
  "metrics_to_track": [
    "Plus-size brand recommendation",
    "Flattering silhouettes",
    "Confidence building colors"
  ]
}
```

### Scenario 8: Minimalist Professional
```json
{
  "id": "test_008",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "THIRTIES",
    "bodyType": "RECTANGULAR",
    "skinTone": "MEDIUM",
    "budgetRange": "MEDIUM",
    "occasionType": "BUSINESS",
    "preferredColors": "White, Black, Gray, Beige",
    "stylePreference": "MINIMALIST",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "COS, Everlane, Gap, Uniqlo",
    "fitPreference": "STRUCTURED",
    "wardrobePreferences": "Minimal, Timeless, Quality"
  },
  "expected_behavior": "Minimalist professional with timeless pieces",
  "metrics_to_track": [
    "Color minimalism",
    "Quality recommendation",
    "Timelessness"
  ]
}
```

### Scenario 9: Bohemian Free Spirit
```json
{
  "id": "test_009",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TWENTIES",
    "bodyType": "HOURGLASS",
    "skinTone": "OLIVE",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Earth tones, Jewel tones, Prints",
    "stylePreference": "BOHEMIAN",
    "weather": "HOT",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Free People, Urban Outfitters, Revolve",
    "fitPreference": "LOOSE",
    "wardrobePreferences": "Free-flowing, Artistic, Eclectic"
  },
  "expected_behavior": "Bohemian artistic outfit with flowing pieces",
  "metrics_to_track": [
    "Boho style authenticity",
    "Flowing fabric recommendation",
    "Artistic color combinations"
  ]
}
```

### Scenario 10: Gym-to-Casual Athleisure
```json
{
  "id": "test_010",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TWENTIES",
    "bodyType": "ATHLETIC",
    "skinTone": "FAIR",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Black, White, Neon accents",
    "stylePreference": "ATHLETIC",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Nike, Lululemon, Athleta, Adidas",
    "fitPreference": "FITTED",
    "wardrobePreferences": "Sporty, Functional, Fashionable"
  },
  "expected_behavior": "Athleisure outfit suitable for gym and casual",
  "metrics_to_track": [
    "Athleisure balance",
    "Performance fabric",
    "Gym appropriateness"
  ]
}
```

### Scenario 11: Executive Women Leader
```json
{
  "id": "test_011",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "FORTIES",
    "bodyType": "APPLE",
    "skinTone": "MEDIUM",
    "budgetRange": "HIGH",
    "occasionType": "BUSINESS",
    "preferredColors": "Navy, Burgundy, Black, Gold",
    "stylePreference": "EXECUTIVE",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Tahari, Ann Taylor, Reiss, Theory",
    "fitPreference": "TAILORED",
    "wardrobePreferences": "Powerful, Confident, Sophisticated"
  },
  "expected_behavior": "Executive power outfit with leadership presence",
  "metrics_to_track": [
    "Power dressing",
    "Color confidence",
    "Authority projection"
  ]
}
```

### Scenario 12: Festival Fashion Lover
```json
{
  "id": "test_012",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TEENS",
    "bodyType": "HOURGLASS",
    "skinTone": "LIGHT",
    "budgetRange": "LOW",
    "occasionType": "FESTIVAL",
    "preferredColors": "Vibrant, Rainbow, Metallics",
    "stylePreference": "TRENDY",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "ASOS, PrettyLittleThing, Fashion Nova",
    "fitPreference": "FITTED",
    "wardrobePreferences": "Bold, Trendy, Festival-ready"
  },
  "expected_behavior": "Festival outfit with bold and vibrant styling",
  "metrics_to_track": [
    "Festival appropriateness",
    "Color boldness",
    "Trend accuracy"
  ]
}
```

### Scenario 13: Classic Preppy Style
```json
{
  "id": "test_013",
  "user_profile": {
    "gender": "Male",
    "ageGroup": "TWENTIES",
    "bodyType": "ATHLETIC",
    "skinTone": "FAIR",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Navy, Cream, Pastels",
    "stylePreference": "PREPPY",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Ralph Lauren, Lacoste, Banana Republic",
    "fitPreference": "CLASSIC",
    "wardrobePreferences": "Classic, Timeless, Polished"
  },
  "expected_behavior": "Classic preppy outfit with timeless appeal",
  "metrics_to_track": [
    "Preppy authenticity",
    "Color classicism",
    "Timelessness"
  ]
}
```

### Scenario 14: Sustainable Fashion Advocate
```json
{
  "id": "test_014",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "THIRTIES",
    "bodyType": "RECTANGULAR",
    "skinTone": "MEDIUM",
    "budgetRange": "MEDIUM",
    "occasionType": "CASUAL",
    "preferredColors": "Natural, Earth tones",
    "stylePreference": "SUSTAINABLE",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Patagonia, Everlane, Reformation",
    "fitPreference": "COMFORTABLE",
    "wardrobePreferences": "Eco-friendly, Quality, Durable"
  },
  "expected_behavior": "Sustainable eco-friendly outfit recommendations",
  "metrics_to_track": [
    "Sustainability focus",
    "Eco-brand knowledge",
    "Quality durability"
  ]
}
```

### Scenario 15: Date Night Glamour
```json
{
  "id": "test_015",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TWENTIES",
    "bodyType": "HOURGLASS",
    "skinTone": "DARK",
    "budgetRange": "MEDIUM",
    "occasionType": "DATE_NIGHT",
    "preferredColors": "Red, Black, Metallic",
    "stylePreference": "GLAMOROUS",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "ASOS, Revolve, Missguided",
    "fitPreference": "FITTED",
    "wardrobePreferences": "Sexy, Confident, Glamorous"
  },
  "expected_behavior": "Glamorous date night outfit with confidence",
  "metrics_to_track": [
    "Date night appropriateness",
    "Confidence colors",
    "Attraction factor"
  ]
}
```

### Scenario 16: Corporate Startup Founder
```json
{
  "id": "test_016",
  "user_profile": {
    "gender": "Male",
    "ageGroup": "THIRTIES",
    "bodyType": "LEAN",
    "skinTone": "LIGHT",
    "budgetRange": "HIGH",
    "occasionType": "BUSINESS_CASUAL",
    "preferredColors": "Black, Gray, White, Minimal",
    "stylePreference": "TECH",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Allbirds, Reiss, AllSaints, Arc'teryx",
    "fitPreference": "SLIM",
    "wardrobePreferences": "Modern, Tech-forward, Minimalist"
  },
  "expected_behavior": "Modern tech-forward startup style",
  "metrics_to_track": [
    "Tech culture fit",
    "Modern aesthetics",
    "Minimalist approach"
  ]
}
```

### Scenario 17: Party Ready Trendsetter
```json
{
  "id": "test_017",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TWENTIES",
    "bodyType": "PEAR",
    "skinTone": "MEDIUM",
    "budgetRange": "MEDIUM",
    "occasionType": "PARTY",
    "preferredColors": "Bold, Patterns, Sequins",
    "stylePreference": "TRENDY",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Fashion Nova, ASOS, Boohoo",
    "fitPreference": "STATEMENT",
    "wardrobePreferences": "Bold, Eye-catching, Trendy"
  },
  "expected_behavior": "Bold party outfit with statement pieces",
  "metrics_to_track": [
    "Party appropriateness",
    "Trend alignment",
    "Body celebration"
  ]
}
```

### Scenario 18: Casual Comfortable Parent
```json
{
  "id": "test_018",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "FORTIES",
    "bodyType": "PEAR",
    "skinTone": "LIGHT",
    "budgetRange": "LOW",
    "occasionType": "CASUAL",
    "preferredColors": "Practical, Patterns, Neutrals",
    "stylePreference": "CASUAL",
    "weather": "MODERATE",
    "confidenceLevel": "MEDIUM",
    "favoriteBrands": "Target, Costco, Old Navy",
    "fitPreference": "COMFORTABLE",
    "wardrobePreferences": "Practical, Easy-care, Comfortable"
  },
  "expected_behavior": "Practical comfortable outfit for busy parent",
  "metrics_to_track": [
    "Practicality",
    "Easy-care fabrics",
    "Budget consciousness"
  ]
}
```

### Scenario 19: Luxury Weekend Casual
```json
{
  "id": "test_019",
  "user_profile": {
    "gender": "Male",
    "ageGroup": "FORTIES",
    "bodyType": "HOURGLASS",
    "skinTone": "MEDIUM",
    "budgetRange": "VERY_HIGH",
    "occasionType": "CASUAL",
    "preferredColors": "Designer colors, Neutral luxury",
    "stylePreference": "LUXURY",
    "weather": "MODERATE",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "Hermès, Louis Vuitton, Prada, Loro Piana",
    "fitPreference": "TAILORED",
    "wardrobePreferences": "Luxury, Exclusive, Sophisticated"
  },
  "expected_behavior": "Luxury casual with exclusive designer pieces",
  "metrics_to_track": [
    "Luxury brand knowledge",
    "Exclusivity",
    "Sophisticated casual"
  ]
}
```

### Scenario 20: Outdoor Adventure Style
```json
{
  "id": "test_020",
  "user_profile": {
    "gender": "Female",
    "ageGroup": "TWENTIES",
    "bodyType": "ATHLETIC",
    "skinTone": "OLIVE",
    "budgetRange": "MEDIUM",
    "occasionType": "OUTDOOR",
    "preferredColors": "Earthy, Camouflage, Neutrals",
    "stylePreference": "OUTDOOR",
    "weather": "COLD",
    "confidenceLevel": "HIGH",
    "favoriteBrands": "The North Face, Patagonia, Arc'teryx, Salomon",
    "fitPreference": "PERFORMANCE",
    "wardrobePreferences": "Functional, Weather-ready, Technical"
  },
  "expected_behavior": "Outdoor adventure outfit with technical features",
  "metrics_to_track": [
    "Weather appropriateness",
    "Technical features",
    "Outdoor suitability"
  ]
}
```

## Evaluation Metrics

Each test case should be evaluated on:

| Metric | Description | Weight |
|--------|-------------|--------|
| Budget Compliance | Recommendations stay within stated budget | 20% |
| Occasion Match | Outfit suits the specified occasion | 20% |
| Style Consistency | Recommendations match user style preference | 20% |
| Brand Appropriateness | Suggested brands match user profile | 15% |
| Weather Suitability | Outfit appropriate for weather condition | 15% |
| Diversity | Recommended outfits differ from each other | 10% |

## Expected Outputs

For each test case, collect:

```json
{
  "test_id": "test_001",
  "trace_id": "uuid",
  "timestamp": "2024-01-15T10:30:45Z",
  "input_tokens": 150,
  "output_tokens": 250,
  "total_tokens": 400,
  "latency_ms": 3500,
  "estimated_cost_usd": 0.000090,
  "style_match_score": 85,
  "confidence_score": 78,
  "output_sample": "...",
  "evaluation_scores": {
    "budget_compliance": 0.95,
    "occasion_match": 0.88,
    "style_consistency": 0.92,
    "brand_appropriateness": 0.85,
    "weather_suitability": 0.90,
    "diversity": 0.75
  },
  "issues_detected": []
}
```

## Success Criteria

- [ ] All 20 test cases pass without errors
- [ ] Token counting is accurate (±20%)
- [ ] Cost estimation is reasonable
- [ ] Latency is <5 seconds per operation
- [ ] Style scores between 50-100
- [ ] No hallucinations detected
- [ ] All traces recorded and queryable
- [ ] Problematic runs identified correctly

## Next Steps

1. Run all 20 test cases
2. Collect traces and metrics
3. Analyze for patterns and issues
4. Proceed to Phase 3 (create evaluators)
