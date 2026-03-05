package com.courtroom.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hitl_reviews")
public class HitlReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "query_id")
    private LegalQuery query;

    @Column(columnDefinition = "TEXT")
    private String originalResponse;

    @Column(columnDefinition = "TEXT")
    private String correctedResponse;

    @Column(columnDefinition = "TEXT")
    private String reviewerNotes;

    private String reviewerName;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status; // PENDING, APPROVED, CORRECTED

    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ReviewStatus.PENDING;
    }

    public enum ReviewStatus { PENDING, APPROVED, CORRECTED }
}
