package com.sungja.backend.service;

import com.sungja.backend.dto.EstimateItemDTO;
import com.sungja.backend.dto.EstimateRequestDTO;
import com.sungja.backend.dto.EstimateUpdateDTO;
import com.sungja.backend.entity.*;
import com.sungja.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EstimateService {
    
    private final EstimateRepository estimateRepository;
    private final EstimateItemRepository estimateItemRepository;
    private final EstimateHistoryRepository estimateHistoryRepository;
    private final EstimateAttachmentRepository estimateAttachmentRepository;
    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final FileStorageService fileStorageService;
    
    public Estimate createEstimate(EstimateRequestDTO requestDTO, List<EstimateItemDTO> items) {
        Estimate estimate = Estimate.builder()
                .userType(requestDTO.getUserType())
                .name(requestDTO.getName())
                .phoneNumber(requestDTO.getPhoneNumber())
                .email(requestDTO.getEmail())
                .requestDetails(requestDTO.getRequestDetails())
                .companyName(requestDTO.getCompanyName())
                .businessNumber(requestDTO.getBusinessNumber())
                .status("PENDING")
                .build();
        
        final Estimate savedEstimate = estimateRepository.save(estimate);
        
        // 견적 항목 추가
        if (items != null && !items.isEmpty()) {
            List<EstimateItem> estimateItems = items.stream().map(itemDTO -> {
                EstimateItem item = EstimateItem.builder()
                        .estimate(savedEstimate)
                        .itemName(itemDTO.getItemName())
                        .quantity(itemDTO.getQuantity())
                        .unitPrice(itemDTO.getUnitPrice())
                        .specifications(itemDTO.getSpecifications())
                        .build();
                
                if (itemDTO.getProductId() != null) {
                    productRepository.findById(itemDTO.getProductId())
                            .ifPresent(item::setProduct);
                }
                
                return item;
            }).collect(Collectors.toList());
            
            estimateItemRepository.saveAll(estimateItems);
            savedEstimate.getItems().addAll(estimateItems);
        }
        
        // 처리 내역 기록
        createHistory(savedEstimate, null, "CREATED", null, "PENDING", "견적 요청이 생성되었습니다.");
        
        return savedEstimate;
    }
    
    /**
     * 견적에 파일 첨부
     */
    public EstimateAttachment attachFile(Long estimateId, MultipartFile file) throws IOException {
        System.out.println("attachFile 호출 - estimateId: " + estimateId + ", fileName: " + file.getOriginalFilename());
        
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new RuntimeException("견적을 찾을 수 없습니다. ID: " + estimateId));
        
        // 파일 저장
        String fileName = fileStorageService.storeFile(file);
        String originalFileName = file.getOriginalFilename();
        
        System.out.println("파일 저장 완료 - 저장된 파일명: " + fileName + ", 원본 파일명: " + originalFileName);
        
        // 첨부파일 엔티티 생성
        EstimateAttachment attachment = EstimateAttachment.builder()
                .estimate(estimate)
                .fileName(originalFileName != null ? originalFileName : "unknown")
                .filePath(fileName)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .uploadType("CUSTOMER")
                .build();
        
        EstimateAttachment saved = estimateAttachmentRepository.save(attachment);
        System.out.println("첨부파일 엔티티 저장 완료 - ID: " + saved.getId());
        
        return saved;
    }
    
    /**
     * 견적에 여러 파일 첨부
     */
    public List<EstimateAttachment> attachFiles(Long estimateId, List<MultipartFile> files) throws IOException {
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        return attachFile(estimateId, file);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
    }
    
    public Estimate updateEstimate(Long id, EstimateUpdateDTO updateDTO) {
        Estimate estimate = estimateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("견적을 찾을 수 없습니다."));
        
        String previousStatus = estimate.getStatus();
        
        if (updateDTO.getStatus() != null) {
            estimate.setStatus(updateDTO.getStatus());
        }
        
        if (updateDTO.getEstimatedPrice() != null) {
            estimate.setEstimatedPrice(updateDTO.getEstimatedPrice());
        }
        
        if (updateDTO.getEstimatedById() != null) {
            Admin admin = adminRepository.findById(updateDTO.getEstimatedById())
                    .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
            estimate.setEstimatedBy(admin);
            estimate.setEstimatedAt(LocalDateTime.now());
        }
        
        estimate = estimateRepository.save(estimate);
        
        // 처리 내역 기록
        createHistory(estimate, estimate.getEstimatedBy(), "UPDATED", previousStatus, estimate.getStatus(), "견적이 업데이트되었습니다.");
        
        return estimate;
    }
    
    public Estimate getEstimate(Long id) {
        return estimateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("견적을 찾을 수 없습니다."));
    }
    
    public List<Estimate> getAllEstimates() {
        return estimateRepository.findAll();
    }
    
    public List<Estimate> getEstimatesByStatus(String status) {
        return estimateRepository.findByStatus(status);
    }
    
    /**
     * 개인 고객 견적 조회
     */
    public Estimate getEstimateByIndividual(String name, String email, String phoneNumber) {
        return estimateRepository.findByNameAndEmailAndPhoneNumber(name, email, phoneNumber)
                .orElseThrow(() -> new RuntimeException("견적을 찾을 수 없습니다. 입력한 정보를 확인해주세요."));
    }
    
    /**
     * 기업 고객 견적 조회
     */
    public Estimate getEstimateByCompany(String companyName, String email, String phoneNumber) {
        return estimateRepository.findByCompanyNameAndEmailAndPhoneNumber(companyName, email, phoneNumber)
                .orElseThrow(() -> new RuntimeException("견적을 찾을 수 없습니다. 입력한 정보를 확인해주세요."));
    }
    
    /**
     * 첨부파일 조회
     */
    public EstimateAttachment getAttachment(Long attachmentId) {
        return estimateAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("첨부파일을 찾을 수 없습니다."));
    }
    
    /**
     * 견적의 첨부파일 목록 조회
     */
    public List<EstimateAttachment> getAttachmentsByEstimateId(Long estimateId) {
        System.out.println("getAttachmentsByEstimateId 호출 - estimateId: " + estimateId);
        try {
            List<EstimateAttachment> attachments = estimateAttachmentRepository.findByEstimateId(estimateId);
            System.out.println("Repository에서 조회된 첨부파일 개수: " + (attachments != null ? attachments.size() : 0));
            return attachments != null ? attachments : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("첨부파일 조회 중 예외 발생:");
            e.printStackTrace();
            throw e;
        }
    }
    
    private void createHistory(Estimate estimate, Admin admin, String actionType, 
                              String previousStatus, String currentStatus, String note) {
        EstimateHistory history = EstimateHistory.builder()
                .estimate(estimate)
                .admin(admin)
                .actionType(actionType)
                .previousStatus(previousStatus)
                .currentStatus(currentStatus)
                .note(note)
                .build();
        
        estimateHistoryRepository.save(history);
    }
}

