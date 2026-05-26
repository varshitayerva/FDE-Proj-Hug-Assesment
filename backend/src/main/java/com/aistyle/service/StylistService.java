package com.aistyle.service;

import com.aistyle.dto.StyleAnalysisResponse;
import com.aistyle.dto.StylistRecommendationResponse;
import com.aistyle.dto.UserProfileRequest;
import com.aistyle.entity.Recommendation;
import com.aistyle.entity.User;
import com.aistyle.entity.UserProfile;
import com.aistyle.exception.ResourceNotFoundException;
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
    private StylistWorkflow stylistWorkflow;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    public StylistRecommendationResponse getStylistRecommendation(Long userId, UserProfileRequest profileRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = createOrUpdateUserProfile(user, profileRequest);

        log.info("Starting AI stylist workflow for user: " + userId);

        StyleAnalysisResponse styleAnalysis = stylistWorkflow.analyzeStyle(profile);
        log.info("Style analysis completed. Score: " + styleAnalysis.getStyleMatchScore());

        String outfitRecommendations = stylistWorkflow.generateOutfitRecommendations(profile, styleAnalysis);
        log.info("Outfit recommendations generated");

        String summaryReport = stylistWorkflow.generateSummaryReport(profile, styleAnalysis, outfitRecommendations);
        log.info("Summary report generated");

        Recommendation recommendation = Recommendation.builder()
            .user(user)
            .styleAnalysis(styleAnalysis.toString())
            .outfitRecommendations(outfitRecommendations)
            .summaryReport(summaryReport)
            .styleMatchScore(styleAnalysis.getStyleMatchScore())
            .confidenceScore(styleAnalysis.getConfidenceScore())
            .numberOfLlmCalls(3)
            .isAdvancedRecommendation(styleAnalysis.getStyleMatchScore() >= 70)
            .huggingFaceModelUsed("mistralai/Mistral-7B-Instruct-v0.3")
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

    public List<Recommendation> getUserRecommendations(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return recommendationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Recommendation getRecommendationById(Long userId, Long recommendationId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
            .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found"));

        if (!recommendation.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Unauthorized access to recommendation");
        }

        return recommendation;
    }

    private UserProfile createOrUpdateUserProfile(User user, UserProfileRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
            .orElse(new UserProfile());

        profile.setUser(user);
        profile.setGender(request.getGender());
        profile.setAgeGroup(request.getAgeGroup());
        profile.setBodyType(request.getBodyType());
        profile.setSkinTone(request.getSkinTone());
        profile.setBudgetRange(request.getBudgetRange());
        profile.setOccasionType(request.getOccasionType());
        profile.setPreferredColors(request.getPreferredColors());
        profile.setStylePreference(request.getStylePreference());
        profile.setWeather(request.getWeather());
        profile.setConfidenceLevel(request.getConfidenceLevel());
        profile.setFavoriteBrands(request.getFavoriteBrands());
        profile.setFitPreference(request.getFitPreference());
        profile.setWardrobePreferences(request.getWardrobePreferences());

        return userProfileRepository.save(profile);
    }
}
