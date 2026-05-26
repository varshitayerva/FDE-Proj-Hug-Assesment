package com.aistyle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.aistyle.entity.UserProfile.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Age group is required")
    private AgeGroup ageGroup;

    @NotNull(message = "Body type is required")
    private BodyType bodyType;

    @NotNull(message = "Skin tone is required")
    private SkinTone skinTone;

    @NotNull(message = "Budget range is required")
    private BudgetRange budgetRange;

    @NotNull(message = "Occasion type is required")
    private OccasionType occasionType;

    private String preferredColors;

    @NotNull(message = "Style preference is required")
    private StylePreference stylePreference;

    @NotNull(message = "Weather condition is required")
    private WeatherCondition weather;

    @NotNull(message = "Confidence level is required")
    private ConfidenceLevel confidenceLevel;

    private String favoriteBrands;

    @NotNull(message = "Fit preference is required")
    private FitPreference fitPreference;

    private String wardrobePreferences;
}
