package com.aistyle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {
    @NotBlank
    private String gender;

    @NotBlank
    private String ageGroup;

    @NotBlank
    private String bodyType;

    @NotBlank
    private String skinTone;

    @NotBlank
    private String budgetRange;

    @NotBlank
    private String occasionType;

    private String preferredColors;

    @NotBlank
    private String stylePreference;

    @NotBlank
    private String weather;

    @NotBlank
    private String confidenceLevel;

    private String favoriteBrands;

    @NotBlank
    private String fitPreference;

    private String wardrobePreferences;
}
