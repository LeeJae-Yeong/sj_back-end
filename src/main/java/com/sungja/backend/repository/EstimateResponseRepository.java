package com.sungja.backend.repository;

import com.sungja.backend.entity.EstimateResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstimateResponseRepository extends JpaRepository<EstimateResponse, Long> {
    List<EstimateResponse> findByEstimateIdOrderByCreatedAtDesc(Long estimateId);
    List<EstimateResponse> findByEstimateIdAndIsPublicTrueOrderByCreatedAtDesc(Long estimateId);
    List<EstimateResponse> findByAdminIdOrderByCreatedAtDesc(Long adminId);
}

