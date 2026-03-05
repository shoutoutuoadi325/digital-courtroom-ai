package com.courtroom.repository;

import com.courtroom.entity.LegalQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LegalQueryRepository extends JpaRepository<LegalQuery, Long> {
    List<LegalQuery> findByStatusOrderByCreatedAtDesc(LegalQuery.QueryStatus status);
    List<LegalQuery> findAllByOrderByCreatedAtDesc();
}
