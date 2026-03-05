package com.courtroom.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trial_messages")
public class TrialMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trial_id")
    private MockTrial trial;

    private String senderRole; // USER, PLAINTIFF_AGENT, DEFENDANT_AGENT, LAWYER_AGENT, JUDGE_AGENT

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType; // STATEMENT, QUESTION, OBJECTION, RULING, EVIDENCE

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum MessageType { STATEMENT, QUESTION, OBJECTION, RULING, EVIDENCE }
}
