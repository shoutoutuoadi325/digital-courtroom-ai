package com.courtroom.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "mock_trials")
public class MockTrial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String caseDescription;

    @Enumerated(EnumType.STRING)
    private UserRole userRole; // PLAINTIFF, DEFENDANT, LAWYER, JUDGE

    @Enumerated(EnumType.STRING)
    private TrialStatus status; // ACTIVE, PAUSED, COMPLETED

    @Column(columnDefinition = "TEXT")
    private String currentStage; // OPENING, EVIDENCE, CROSS_EXAMINATION, CLOSING, VERDICT

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "trial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrialMessage> messages;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = TrialStatus.ACTIVE;
        currentStage = "OPENING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum UserRole { PLAINTIFF, DEFENDANT, LAWYER, JUDGE }
    public enum TrialStatus { ACTIVE, PAUSED, COMPLETED }
}
