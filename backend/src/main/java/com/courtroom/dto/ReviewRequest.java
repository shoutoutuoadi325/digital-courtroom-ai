package com.courtroom.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long queryId;
    private String correctedResponse;
    private String reviewerNotes;
    private String reviewerName;
    private String status; // APPROVED, CORRECTED
}
