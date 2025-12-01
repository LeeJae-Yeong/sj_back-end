package com.sungja.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estimates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estimate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_type", nullable = false, length = 20)
    private String userType; // "INDIVIDUAL" or "COMPANY"
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "request_details", columnDefinition = "TEXT")
    private String requestDetails;
    
    @Column(name = "company_name", length = 100)
    private String companyName; // 기업 고객인 경우
    
    @Column(name = "business_number", length = 50)
    private String businessNumber; // 사업자번호
    
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING"; // PENDING, PROCESSING, COMPLETED, CANCELLED
    
    @Column(name = "estimated_price", precision = 15, scale = 2)
    private BigDecimal estimatedPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimated_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Admin estimatedBy;
    
    @Column(name = "estimated_at")
    private LocalDateTime estimatedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("estimate")
    @Builder.Default
    private List<EstimateItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("estimate")
    @Builder.Default
    private List<EstimateHistory> histories = new ArrayList<>();
    
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("estimate")
    @Builder.Default
    private List<EstimateAttachment> attachments = new ArrayList<>();
    
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("estimate")
    @Builder.Default
    private List<EstimateResponse> responses = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

