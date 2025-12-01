package com.sungja.backend.controller;

import com.sungja.backend.dto.EstimateRequestDTO;
import com.sungja.backend.dto.EstimateUpdateDTO;
import com.sungja.backend.entity.Estimate;
import com.sungja.backend.entity.EstimateAttachment;
import com.sungja.backend.service.EstimateService;
import com.sungja.backend.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EstimateController {
    
    private final EstimateService estimateService;
    private final FileStorageService fileStorageService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEstimate(
            @Valid @RequestBody EstimateRequestDTO requestDTO) {
        
        Estimate estimate = estimateService.createEstimate(requestDTO, requestDTO.getItems());
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", estimate.getId());
        data.put("userType", estimate.getUserType());
        data.put("name", estimate.getName());
        data.put("status", estimate.getStatus());
        data.put("createdAt", estimate.getCreatedAt());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "견적 요청이 성공적으로 저장되었습니다.");
        response.put("data", data);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 견적에 파일 첨부
     */
    @PostMapping("/{id}/attachments")
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            EstimateAttachment attachment = estimateService.attachFile(id, file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "파일이 성공적으로 업로드되었습니다.");
            response.put("data", Map.of(
                    "id", attachment.getId(),
                    "fileName", attachment.getFileName(),
                    "fileSize", attachment.getFileSize(),
                    "fileType", attachment.getFileType()
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 견적에 여러 파일 첨부
     */
    @PostMapping("/{id}/attachments/multiple")
    public ResponseEntity<Map<String, Object>> uploadAttachments(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        
        System.out.println("=== 파일 업로드 API 호출 ===");
        System.out.println("견적 ID: " + id);
        System.out.println("파일 개수: " + (files != null ? files.size() : 0));
        
        if (files == null || files.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "업로드할 파일이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            List<EstimateAttachment> attachments = estimateService.attachFiles(id, files);
            System.out.println("파일 업로드 성공, 저장된 첨부파일 개수: " + attachments.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", files.size() + "개의 파일이 성공적으로 업로드되었습니다.");
            response.put("data", attachments);
            response.put("count", attachments.size());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            System.err.println("파일 업로드 중 RuntimeException 발생: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IOException 발생: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 파일 다운로드
     */
    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            EstimateAttachment attachment = estimateService.getAttachment(attachmentId);
            Path filePath = fileStorageService.loadFile(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType = attachment.getFileType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 견적의 첨부파일 목록 조회
     */
    @GetMapping("/{id}/attachments")
    public ResponseEntity<Map<String, Object>> getAttachments(@PathVariable Long id) {
        try {
            System.out.println("=== 첨부파일 조회 API 호출 ===");
            System.out.println("견적 ID: " + id);
            
            List<EstimateAttachment> attachments = estimateService.getAttachmentsByEstimateId(id);
            System.out.println("조회된 첨부파일 개수: " + attachments.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", attachments);
            response.put("count", attachments.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("첨부파일 조회 중 오류 발생:");
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "첨부파일 조회 중 오류가 발생했습니다: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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

