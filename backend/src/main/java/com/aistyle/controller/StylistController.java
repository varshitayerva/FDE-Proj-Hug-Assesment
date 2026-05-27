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
            @Valid @RequestBody UserProfileRequest profileRequest) {
        StylistRecommendationResponse response = stylistService.getStylistRecommendation(profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Stylist recommendations generated successfully", response, 201));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<Recommendation>>> getUserRecommendations() {
        List<Recommendation> recommendations = stylistService.getUserRecommendations();
        return ResponseEntity.ok(new ApiResponse<>(true, "Recommendations retrieved successfully", recommendations, 200));
    }

    @GetMapping("/recommendation/{id}")
    public ResponseEntity<ApiResponse<Recommendation>> getRecommendation(
            @PathVariable Long id) {
        Recommendation recommendation = stylistService.getRecommendationById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recommendation retrieved successfully", recommendation, 200));
    }
}
