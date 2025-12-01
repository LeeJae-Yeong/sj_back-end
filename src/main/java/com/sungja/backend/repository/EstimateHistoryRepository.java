package com.sungja.backend.repository;

import com.sungja.backend.entity.EstimateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstimateHistoryRepository extends JpaRepository<EstimateHistory, Long> {
    List<EstimateHistory> findByEstimateIdOrderByCreatedAtDesc(Long estimateId);
    List<EstimateHistory> findByAdminIdOrderByCreatedAtDesc(Long adminId);
}

