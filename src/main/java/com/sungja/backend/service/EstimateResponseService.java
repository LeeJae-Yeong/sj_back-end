package com.sungja.backend.service;

import com.sungja.backend.dto.EstimateResponseDTO;
import com.sungja.backend.entity.Estimate;
import com.sungja.backend.entity.EstimateResponse;
import com.sungja.backend.repository.EstimateRepository;
import com.sungja.backend.repository.EstimateResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EstimateResponseService {
    
    private final EstimateResponseRepository estimateResponseRepository;
    private final EstimateRepository estimateRepository;
    
    public EstimateResponse createResponse(EstimateResponseDTO responseDTO, Long adminId) {
        Estimate estimate = estimateRepository.findById(responseDTO.getEstimateId())
                .orElseThrow(() -> new RuntimeException("견적을 찾을 수 없습니다."));
        
        EstimateResponse response = EstimateResponse.builder()
                .estimate(estimate)
                .admin(adminId != null ? estimate.getEstimatedBy() : null)
                .responseContent(responseDTO.getResponseContent())
                .isPublic(responseDTO.getIsPublic() != null ? responseDTO.getIsPublic() : true)
                .build();
        
        return estimateResponseRepository.save(response);
    }
    
    public List<EstimateResponse> getResponsesByEstimateId(Long estimateId) {
        return estimateResponseRepository.findByEstimateIdOrderByCreatedAtDesc(estimateId);
    }
    
    public List<EstimateResponse> getPublicResponsesByEstimateId(Long estimateId) {
        return estimateResponseRepository.findByEstimateIdAndIsPublicTrueOrderByCreatedAtDesc(estimateId);
    }
    
    public EstimateResponse updateResponse(Long id, EstimateResponseDTO responseDTO) {
        EstimateResponse response = estimateResponseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));
        
        if (responseDTO.getResponseContent() != null) {
            response.setResponseContent(responseDTO.getResponseContent());
        }
        if (responseDTO.getIsPublic() != null) {
            response.setIsPublic(responseDTO.getIsPublic());
        }
        
        return estimateResponseRepository.save(response);
    }
    
    public void deleteResponse(Long id) {
        estimateResponseRepository.deleteById(id);
    }
}

