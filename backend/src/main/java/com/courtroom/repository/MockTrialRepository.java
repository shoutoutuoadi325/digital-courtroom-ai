package com.courtroom.repository;

import com.courtroom.entity.MockTrial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MockTrialRepository extends JpaRepository<MockTrial, Long> {
    List<MockTrial> findByStatusOrderByCreatedAtDesc(MockTrial.TrialStatus status);
    List<MockTrial> findAllByOrderByCreatedAtDesc();
}
