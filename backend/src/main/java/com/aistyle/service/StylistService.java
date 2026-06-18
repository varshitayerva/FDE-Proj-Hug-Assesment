package com.aistyle.service;

import com.aistyle.dto.StyleAnalysisResponse;
import com.aistyle.dto.StylistRecommendationResponse;
import com.aistyle.dto.UserProfileRequest;
import com.aistyle.dto.OutfitRecommendation;
import com.aistyle.entity.Recommendation;
import com.aistyle.entity.User;
import com.aistyle.entity.UserProfile;
import com.aistyle.repository.RecommendationRepository;
import com.aistyle.repository.UserProfileRepository;
import com.aistyle.repository.UserRepository;
import com.aistyle.workflow.StylistWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class StylistService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private StylistWorkflow stylistWorkflow;

    public StylistRecommendationResponse getStylistRecommendation(UserProfileRequest profileRequest) {
        User user = getOrCreateDefaultUser();
        UserProfile profile = createOrUpdateUserProfile(user, profileRequest);

        log.info("Starting AI stylist workflow for user: " + user.getId());

        StyleAnalysisResponse styleAnalysis = stylistWorkflow.analyzeStyle(profile);
        log.info("Style analysis completed. Score: " + styleAnalysis.getStyleMatchScore());

        String outfitRecommendationsText = stylistWorkflow.generateOutfitRecommendations(profile, styleAnalysis);
        List<OutfitRecommendation> outfitRecommendations = stylistWorkflow.parseOutfitRecommendations(outfitRecommendationsText);
        log.info("Outfit recommendations generated: {} outfits", outfitRecommendations.size());

        String summaryReport = stylistWorkflow.generateSummaryReport(profile, styleAnalysis, outfitRecommendationsText);
        log.info("Summary report generated");

        Recommendation recommendation = Recommendation.builder()
            .user(user)
            .styleAnalysis(styleAnalysis.toString())
            .outfitRecommendations(outfitRecommendationsText)
            .summaryReport(summaryReport)
            .styleMatchScore(styleAnalysis.getStyleMatchScore())
            .confidenceScore(styleAnalysis.getConfidenceScore())
            .numberOfLlmCalls(3)
            .isAdvancedRecommendation(styleAnalysis.getStyleMatchScore() >= 70)
            .huggingFaceModelUsed("deepseek-ai/DeepSeek-V4-Flash")
            .build();

        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        log.info("Recommendation saved with ID: " + savedRecommendation.getId());

        return StylistRecommendationResponse.builder()
            .recommendationId(savedRecommendation.getId())
            .styleAnalysis(styleAnalysis)
            .outfitRecommendations(outfitRecommendations)
            .summaryReport(summaryReport)
            .numberOfLlmCalls(3)
            .isAdvancedRecommendation(styleAnalysis.getStyleMatchScore() >= 70)
            .message("Stylist recommendations generated successfully")
            .build();
    }

    public List<Recommendation> getUserRecommendations() {
        User user = getOrCreateDefaultUser();
        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public Recommendation getRecommendationById(Long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElse(null);
    }

    private User getOrCreateDefaultUser() {
        return userRepository.findByEmail("default@aistyle.com")
            .orElseGet(() -> {
                User defaultUser = User.builder()
                    .email("default@aistyle.com")
                    .firstName("Default")
                    .lastName("User")
                    .password("")
                    .build();
                return userRepository.save(defaultUser);
            });
    }

    private UserProfile createOrUpdateUserProfile(User user, UserProfileRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
            .orElse(new UserProfile());

        profile.setUser(user);
        profile.setGender(UserProfile.Gender.valueOf(request.getGender()));
        profile.setAgeGroup(UserProfile.AgeGroup.valueOf(request.getAgeGroup()));
        profile.setBodyType(UserProfile.BodyType.valueOf(request.getBodyType()));
        profile.setSkinTone(UserProfile.SkinTone.valueOf(request.getSkinTone()));
        profile.setBudgetRange(UserProfile.BudgetRange.valueOf(request.getBudgetRange()));
        profile.setOccasionType(UserProfile.OccasionType.valueOf(request.getOccasionType()));
        profile.setPreferredColors(request.getPreferredColors());
        profile.setStylePreference(UserProfile.StylePreference.valueOf(request.getStylePreference()));
        profile.setWeather(UserProfile.WeatherCondition.valueOf(request.getWeather()));
        profile.setConfidenceLevel(UserProfile.ConfidenceLevel.valueOf(request.getConfidenceLevel()));
        profile.setFavoriteBrands(request.getFavoriteBrands());
        profile.setFitPreference(UserProfile.FitPreference.valueOf(request.getFitPreference()));
        profile.setWardrobePreferences(request.getWardrobePreferences());

        return userProfileRepository.save(profile);
    }
}
