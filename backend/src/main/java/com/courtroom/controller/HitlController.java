package com.courtroom.controller;

import com.courtroom.dto.ReviewRequest;
import com.courtroom.entity.HitlReview;
import com.courtroom.service.HitlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class HitlController {

    @Autowired
    private HitlService hitlService;

    @GetMapping("/pending")
    public ResponseEntity<List<HitlReview>> getPendingReviews() {
        return ResponseEntity.ok(hitlService.getPendingReviews());
    }

    @GetMapping
    public ResponseEntity<List<HitlReview>> getAllReviews() {
        return ResponseEntity.ok(hitlService.getAllReviews());
    }

    @PostMapping
    public ResponseEntity<HitlReview> submitReview(@RequestBody ReviewRequest request) {
        try {
            HitlReview review = hitlService.submitReview(request);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
