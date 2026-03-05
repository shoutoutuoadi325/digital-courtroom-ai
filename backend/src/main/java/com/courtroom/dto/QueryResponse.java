package com.courtroom.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QueryResponse {
    private Long id;
    private String queryText;
    private String queryType;
    private String inputType;
    private String response;
    private Object mindMapData;
    private String status;
    private LocalDateTime createdAt;
}
