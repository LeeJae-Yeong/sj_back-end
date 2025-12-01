package com.sungja.backend.repository;

import com.sungja.backend.entity.EstimateAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstimateAttachmentRepository extends JpaRepository<EstimateAttachment, Long> {
    List<EstimateAttachment> findByEstimateId(Long estimateId);
    List<EstimateAttachment> findByEstimateIdAndUploadType(Long estimateId, String uploadType);
}

