package com.sungja.backend.controller;

import com.sungja.backend.dto.EstimateRequestDTO;
import com.sungja.backend.dto.EstimateUpdateDTO;
import com.sungja.backend.entity.Estimate;
import com.sungja.backend.service.EstimateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EstimateController {
    
    private final EstimateService estimateService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEstimate(
            @Valid @RequestBody EstimateRequestDTO requestDTO) {
        
        Estimate estimate = estimateService.createEstimate(requestDTO, requestDTO.getItems());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "견적 요청이 성공적으로 저장되었습니다.");
        response.put("data", Map.of(
                "id", estimate.getId(),
                "userType", estimate.getUserType(),
                "name", estimate.getName(),
                "status", estimate.getStatus(),
                "createdAt", estimate.getCreatedAt()
        ));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEstimate(@PathVariable Long id) {
        Estimate estimate = estimateService.getEstimate(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", estimate);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEstimates(
            @RequestParam(required = false) String status) {
        List<Estimate> estimates = status != null 
                ? estimateService.getEstimatesByStatus(status)
                : estimateService.getAllEstimates();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", estimates);
        response.put("count", estimates.size());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEstimate(
            @PathVariable Long id,
            @Valid @RequestBody EstimateUpdateDTO updateDTO) {
        
        Estimate estimate = estimateService.updateEstimate(id, updateDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "견적이 성공적으로 업데이트되었습니다.");
        response.put("data", estimate);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 개인 고객 견적 조회
     */
    @PostMapping("/inquiry/individual")
    public ResponseEntity<Map<String, Object>> inquiryEstimateByIndividual(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phoneNumber) {
        
        try {
            Estimate estimate = estimateService.getEstimateByIndividual(name, email, phoneNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", estimate);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * 기업 고객 견적 조회
     */
    @PostMapping("/inquiry/company")
    public ResponseEntity<Map<String, Object>> inquiryEstimateByCompany(
            @RequestParam String companyName,
            @RequestParam String email,
            @RequestParam String phoneNumber) {
        
        try {
            Estimate estimate = estimateService.getEstimateByCompany(companyName, email, phoneNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", estimate);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}

