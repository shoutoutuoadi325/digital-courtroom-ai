package com.courtroom.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "legal_queries")
public class LegalQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String queryText;

    @Enumerated(EnumType.STRING)
    private QueryType queryType; // RULE_BASED, LLM_BASED

    @Enumerated(EnumType.STRING)
    private InputType inputType; // TEXT, VOICE, OCR, DOCUMENT

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(columnDefinition = "JSON")
    private String mindMapData; // JSON for mind map visualization

    @Enumerated(EnumType.STRING)
    private QueryStatus status; // PENDING, PROCESSING, COMPLETED, UNDER_REVIEW, REVIEWED

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

    public enum QueryType { RULE_BASED, LLM_BASED }
    public enum InputType { TEXT, VOICE, OCR, DOCUMENT }
    public enum QueryStatus { PENDING, PROCESSING, COMPLETED, UNDER_REVIEW, REVIEWED }
}
