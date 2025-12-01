package com.sungja.backend.controller;

import com.sungja.backend.dto.EstimateResponseDTO;
import com.sungja.backend.entity.EstimateResponse;
import com.sungja.backend.service.EstimateResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estimate-responses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EstimateResponseController {
    
    private final EstimateResponseService estimateResponseService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createResponse(
            @Valid @RequestBody EstimateResponseDTO responseDTO,
            @RequestParam(required = false) Long adminId) {
        
        EstimateResponse response = estimateResponseService.createResponse(responseDTO, adminId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "답변이 성공적으로 생성되었습니다.");
        result.put("data", response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @GetMapping("/estimate/{estimateId}")
    public ResponseEntity<Map<String, Object>> getResponsesByEstimate(
            @PathVariable Long estimateId,
            @RequestParam(required = false, defaultValue = "false") Boolean publicOnly) {
        
        List<EstimateResponse> responses = publicOnly
                ? estimateResponseService.getPublicResponsesByEstimateId(estimateId)
                : estimateResponseService.getResponsesByEstimateId(estimateId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", responses);
        result.put("count", responses.size());
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateResponse(
            @PathVariable Long id,
            @Valid @RequestBody EstimateResponseDTO responseDTO) {
        
        EstimateResponse response = estimateResponseService.updateResponse(id, responseDTO);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "답변이 성공적으로 업데이트되었습니다.");
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteResponse(@PathVariable Long id) {
        estimateResponseService.deleteResponse(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "답변이 성공적으로 삭제되었습니다.");
        
        return ResponseEntity.ok(result);
    }
}

