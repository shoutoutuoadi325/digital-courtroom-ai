package com.courtroom.repository;

import com.courtroom.entity.TrialMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrialMessageRepository extends JpaRepository<TrialMessage, Long> {
    List<TrialMessage> findByTrialIdOrderByCreatedAtAsc(Long trialId);
}
