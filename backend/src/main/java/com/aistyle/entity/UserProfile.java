package com.aistyle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    private SkinTone skinTone;

    @Enumerated(EnumType.STRING)
    private BudgetRange budgetRange;

    @Enumerated(EnumType.STRING)
    private OccasionType occasionType;

    @Column(columnDefinition = "TEXT")
    private String preferredColors;

    @Enumerated(EnumType.STRING)
    private StylePreference stylePreference;

    @Enumerated(EnumType.STRING)
    private WeatherCondition weather;

    @Enumerated(EnumType.STRING)
    private ConfidenceLevel confidenceLevel;

    @Column(columnDefinition = "TEXT")
    private String favoriteBrands;

    @Enumerated(EnumType.STRING)
    private FitPreference fitPreference;

    @Column(columnDefinition = "TEXT")
    private String wardrobePreferences;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Gender { MALE, FEMALE, NON_BINARY, PREFER_NOT_TO_SAY }
    public enum AgeGroup { TEENS, TWENTIES, THIRTIES, FORTIES, FIFTIES_PLUS }
    public enum BodyType { PETITE, PEAR, HOURGLASS, RECTANGLE, APPLE, INVERTED_TRIANGLE }
    public enum SkinTone { FAIR, LIGHT, MEDIUM, OLIVE, DEEP, DARK }
    public enum BudgetRange { BUDGET, MODERATE, PREMIUM, LUXURY }
    public enum OccasionType { CASUAL, BUSINESS, PARTY, EVENING, WEEKEND, VACATION }
    public enum StylePreference { CLASSIC, TRENDY, SPORTY, BOHEMIAN, MINIMALIST, VINTAGE, EDGY, ROMANTIC }
    public enum WeatherCondition { HOT, WARM, COOL, COLD, RAINY, SNOWY }
    public enum ConfidenceLevel { VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH }
    public enum FitPreference { SLIM, REGULAR, LOOSE, OVERSIZED }
}
