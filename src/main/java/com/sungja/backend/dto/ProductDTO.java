package com.sungja.backend.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ProductDTO {
    
    @NotBlank(message = "제품명은 필수입니다.")
    private String name;
    
    private String category;
    
    private String description;
    
    private BigDecimal basePrice;
    
    private String imageUrl;
    
    private Boolean isActive;
}

