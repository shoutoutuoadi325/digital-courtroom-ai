package com.courtroom.service;

import com.courtroom.dto.ReviewRequest;
import com.courtroom.entity.HitlReview;
import com.courtroom.entity.LegalCorpus;
import com.courtroom.entity.LegalQuery;
import com.courtroom.exception.EntityNotFoundException;
import com.courtroom.repository.HitlReviewRepository;
import com.courtroom.repository.LegalCorpusRepository;
import com.courtroom.repository.LegalQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HitlService {

    @Autowired
    private HitlReviewRepository hitlReviewRepository;

    @Autowired
    private LegalQueryRepository queryRepository;

    @Autowired
    private LegalCorpusRepository corpusRepository;

    public List<HitlReview> getPendingReviews() {
        return hitlReviewRepository.findByStatusOrderByCreatedAtDesc(HitlReview.ReviewStatus.PENDING);
    }

    public List<HitlReview> getAllReviews() {
        return hitlReviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public HitlReview submitReview(ReviewRequest request) {
        LegalQuery query = queryRepository.findById(request.getQueryId())
            .orElseThrow(() -> new EntityNotFoundException("Query not found: " + request.getQueryId()));

        // Find pending review for this query
        HitlReview review = hitlReviewRepository.findByStatusOrderByCreatedAtDesc(HitlReview.ReviewStatus.PENDING)
            .stream()
            .filter(r -> r.getQuery().getId().equals(request.getQueryId()))
            .findFirst()
            .orElseGet(() -> {
                HitlReview newReview = new HitlReview();
                newReview.setQuery(query);
                newReview.setOriginalResponse(query.getResponse());
                return newReview;
            });

        review.setCorrectedResponse(request.getCorrectedResponse());
        review.setReviewerNotes(request.getReviewerNotes());
        review.setReviewerName(request.getReviewerName());
        review.setStatus(HitlReview.ReviewStatus.valueOf(request.getStatus()));
        review.setReviewedAt(LocalDateTime.now());

        // Update the query with corrected response if provided
        if ("CORRECTED".equals(request.getStatus()) && request.getCorrectedResponse() != null) {
            query.setResponse(request.getCorrectedResponse());
            query.setStatus(LegalQuery.QueryStatus.REVIEWED);
            expandLegalCorpus(query, request.getCorrectedResponse(), request.getReviewerNotes());
        } else {
            query.setStatus(LegalQuery.QueryStatus.REVIEWED);
        }
        queryRepository.save(query);

        return hitlReviewRepository.save(review);
    }

    private void expandLegalCorpus(LegalQuery query, String correctedResponse, String notes) {
        LegalCorpus corpus = new LegalCorpus();
        corpus.setTitle("Human-Reviewed: " + query.getQueryText().substring(0, Math.min(50, query.getQueryText().length())));
        corpus.setCategory("REVIEWED");
        corpus.setKeywords(extractKeywords(query.getQueryText()));
        corpus.setContent(correctedResponse);
        corpus.setVerified(true);
        corpusRepository.save(corpus);
    }

    private String extractKeywords(String text) {
        // Simple keyword extraction - take first 5 significant words
        String[] words = text.split("\\s+");
        StringBuilder keywords = new StringBuilder();
        int count = 0;
        for (String word : words) {
            if (word.length() > 3) {
                if (count > 0) keywords.append(",");
                keywords.append(word.toLowerCase().replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", ""));
                count++;
                if (count >= 5) break;
            }
        }
        return keywords.toString();
    }
}
