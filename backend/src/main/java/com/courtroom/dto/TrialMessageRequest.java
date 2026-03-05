package com.courtroom.dto;

import lombok.Data;

@Data
public class TrialMessageRequest {
    private Long trialId;
    private String content;
    private String messageType; // STATEMENT, QUESTION, OBJECTION, RULING, EVIDENCE
}
