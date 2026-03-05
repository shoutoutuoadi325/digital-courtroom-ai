package com.courtroom.controller;

import com.courtroom.dto.ReviewRequest;
import com.courtroom.entity.HitlReview;
import com.courtroom.service.HitlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class HitlController {

    private static final Logger log = LoggerFactory.getLogger(HitlController.class);

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
        log.info("Review submitted for queryId={} by reviewer={}", request.getQueryId(), request.getReviewerName());
        HitlReview review = hitlService.submitReview(request);
        return ResponseEntity.ok(review);
    }
}
