package com.courtroom.dto;

import lombok.Data;

@Data
public class QueryRequest {
    private String queryText;
    private String inputType; // TEXT, VOICE, OCR, DOCUMENT
}
