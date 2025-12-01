package com.sungja.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateResponseDTO {
    
    @NotNull(message = "견적 ID는 필수입니다.")
    private Long estimateId;
    
    @NotBlank(message = "답변 내용은 필수입니다.")
    private String responseContent;
    
    private Boolean isPublic;
}

