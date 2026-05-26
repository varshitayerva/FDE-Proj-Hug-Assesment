package com.aistyle.controller;

import com.aistyle.dto.ApiResponse;
import com.aistyle.dto.StylistRecommendationResponse;
import com.aistyle.dto.UserProfileRequest;
import com.aistyle.entity.Recommendation;
import com.aistyle.service.StylistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stylist")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class StylistController {

    @Autowired
    private StylistService stylistService;

    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<StylistRecommendationResponse>> getStylistRecommendation(
            @Valid @RequestBody UserProfileRequest profileRequest,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        StylistRecommendationResponse response = stylistService.getStylistRecommendation(userId, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Stylist recommendations generated successfully", response, 201));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<Recommendation>>> getUserRecommendations(
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<Recommendation> recommendations = stylistService.getUserRecommendations(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recommendations retrieved successfully", recommendations, 200));
    }

    @GetMapping("/recommendation/{id}")
    public ResponseEntity<ApiResponse<Recommendation>> getRecommendation(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Recommendation recommendation = stylistService.getRecommendationById(userId, id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recommendation retrieved successfully", recommendation, 200));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return Long.parseLong(userDetails.getUsername().split("@")[0]);
    }
}
