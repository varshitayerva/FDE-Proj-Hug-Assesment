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
@Table(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "LONGTEXT")
    private String styleAnalysis;

    @Column(columnDefinition = "LONGTEXT")
    private String outfitRecommendations;

    @Column(columnDefinition = "LONGTEXT")
    private String summaryReport;

    private Integer styleMatchScore;

    private Integer confidenceScore;

    @Column(columnDefinition = "LONGTEXT")
    private String usedPrompt;

    @Column(columnDefinition = "LONGTEXT")
    private String huggingFaceModelUsed;

    private Integer numberOfLlmCalls;

    private Boolean isAdvancedRecommendation;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
