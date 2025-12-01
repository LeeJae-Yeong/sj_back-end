package com.sungja.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateRequestDTO {
    
    @NotBlank(message = "사용자 유형은 필수입니다.")
    @Pattern(regexp = "^(INDIVIDUAL|COMPANY)$", message = "사용자 유형은 INDIVIDUAL 또는 COMPANY여야 합니다.")
    private String userType;
    
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9-]+$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    
    private String requestDetails;
    
    // 기업 고객인 경우
    private String companyName;
    
    private String businessNumber;
    
    // 견적 항목들
    private List<EstimateItemDTO> items;
}

