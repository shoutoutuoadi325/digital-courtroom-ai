package com.courtroom.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "legal_corpus")
public class LegalCorpus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category; // CONTRACT, CRIMINAL, CIVIL, FAMILY, PROPERTY

    @Column(columnDefinition = "TEXT")
    private String keywords; // comma-separated for rule matching

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String structuredRules; // JSON rules for rule-based engine

    private boolean verified; // true = reviewed by human

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
