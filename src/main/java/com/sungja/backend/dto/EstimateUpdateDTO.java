package com.sungja.backend.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateUpdateDTO {
    
    @Pattern(regexp = "^(PENDING|PROCESSING|COMPLETED|CANCELLED)$", message = "상태는 PENDING, PROCESSING, COMPLETED, CANCELLED 중 하나여야 합니다.")
    private String status;
    
    private BigDecimal estimatedPrice;
    
    private Long estimatedById;
}

