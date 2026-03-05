package com.courtroom.repository;

import com.courtroom.entity.HitlReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HitlReviewRepository extends JpaRepository<HitlReview, Long> {
    List<HitlReview> findByStatusOrderByCreatedAtDesc(HitlReview.ReviewStatus status);
    List<HitlReview> findAllByOrderByCreatedAtDesc();
}
