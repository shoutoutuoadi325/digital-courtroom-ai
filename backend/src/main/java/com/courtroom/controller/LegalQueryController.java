package com.courtroom.controller;

import com.courtroom.dto.QueryRequest;
import com.courtroom.dto.QueryResponse;
import com.courtroom.service.LegalQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/queries")
@CrossOrigin(origins = "http://localhost:5173")
public class LegalQueryController {

    @Autowired
    private LegalQueryService queryService;

    @PostMapping
    public ResponseEntity<QueryResponse> submitQuery(@RequestBody QueryRequest request) {
        try {
            QueryResponse response = queryService.processQuery(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<QueryResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("inputType") String inputType) {
        try {
            String extractedText = extractTextFromFile(file, inputType);
            QueryRequest request = new QueryRequest();
            request.setQueryText(extractedText);
            request.setInputType(inputType);
            QueryResponse response = queryService.processQuery(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<QueryResponse>> getAllQueries() {
        return ResponseEntity.ok(queryService.getAllQueries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QueryResponse> getQuery(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(queryService.getQueryById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String extractTextFromFile(MultipartFile file, String inputType) throws Exception {
        // In production, integrate with OCR service (e.g., Tesseract) or audio transcription
        String filename = file.getOriginalFilename();
        if ("OCR".equals(inputType)) {
            // Simulated OCR extraction
            return "Legal document content extracted via OCR from: " + filename +
                ". The document appears to contain information about a legal matter. " +
                "Please provide additional context for a complete analysis.";
        } else if ("VOICE".equals(inputType)) {
            // Simulated voice transcription
            return "Voice input transcribed: Legal query regarding court proceedings and legal rights. " +
                "The audio input has been processed and converted to text for legal analysis.";
        } else if ("DOCUMENT".equals(inputType)) {
            // Read text from uploaded document
            return new String(file.getBytes()) + " [Uploaded document: " + filename + "]";
        }
        return new String(file.getBytes());
    }
}
