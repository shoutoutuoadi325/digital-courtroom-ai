package com.courtroom.repository;

import com.courtroom.entity.LegalCorpus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LegalCorpusRepository extends JpaRepository<LegalCorpus, Long> {
    @Query("SELECT lc FROM LegalCorpus lc WHERE lc.keywords LIKE %:keyword% OR lc.content LIKE %:keyword%")
    List<LegalCorpus> findByKeyword(@Param("keyword") String keyword);

    List<LegalCorpus> findByCategory(String category);
    List<LegalCorpus> findByVerifiedTrue();
}
