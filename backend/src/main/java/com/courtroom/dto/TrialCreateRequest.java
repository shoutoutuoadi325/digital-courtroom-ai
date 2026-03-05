package com.courtroom.dto;

import lombok.Data;

@Data
public class TrialCreateRequest {
    private String title;
    private String caseDescription;
    private String userRole; // PLAINTIFF, DEFENDANT, LAWYER, JUDGE
}
