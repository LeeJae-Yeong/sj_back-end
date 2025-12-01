package com.sungja.backend.service;

import com.sungja.backend.dto.ProductDTO;
import com.sungja.backend.entity.Product;
import com.sungja.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public Product createProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .category(productDTO.getCategory())
                .description(productDTO.getDescription())
                .basePrice(productDTO.getBasePrice())
                .imageUrl(productDTO.getImageUrl())
                .isActive(productDTO.getIsActive() != null ? productDTO.getIsActive() : true)
                .build();
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
        
        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getCategory() != null) {
            product.setCategory(productDTO.getCategory());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        if (productDTO.getBasePrice() != null) {
            product.setBasePrice(productDTO.getBasePrice());
        }
        if (productDTO.getImageUrl() != null) {
            product.setImageUrl(productDTO.getImageUrl());
        }
        if (productDTO.getIsActive() != null) {
            product.setIsActive(productDTO.getIsActive());
        }
        
        return productRepository.save(product);
    }
    
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByNameOrDescription(keyword);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}

