package com.sungja.backend.repository;

import com.sungja.backend.entity.EstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstimateItemRepository extends JpaRepository<EstimateItem, Long> {
    List<EstimateItem> findByEstimateId(Long estimateId);
    void deleteByEstimateId(Long estimateId);
}

