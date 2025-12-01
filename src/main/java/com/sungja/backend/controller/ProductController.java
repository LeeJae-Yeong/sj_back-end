package com.sungja.backend.controller;

import com.sungja.backend.dto.ProductDTO;
import com.sungja.backend.entity.Product;
import com.sungja.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(
            @Valid @RequestBody ProductDTO productDTO) {
        
        Product product = productService.createProduct(productDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "제품이 성공적으로 생성되었습니다.");
        response.put("data", product);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", product);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        
        List<Product> products;
        
        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
        } else if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else if (activeOnly) {
            products = productService.getActiveProducts();
        } else {
            products = productService.getAllProducts();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        response.put("count", products.size());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        
        Product product = productService.updateProduct(id, productDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "제품이 성공적으로 업데이트되었습니다.");
        response.put("data", product);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "제품이 성공적으로 삭제되었습니다.");
        
        return ResponseEntity.ok(response);
    }
}

