package com.sungja.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    /**
     * 파일을 저장하고 저장된 파일 경로를 반환
     */
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        
        // 파일 저장
        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }
    
    /**
     * 파일 경로로부터 파일을 읽어옴
     */
    public Path loadFile(String fileName) {
        return Paths.get(uploadDir).resolve(fileName).normalize();
    }
    
    /**
     * 파일 삭제
     */
    public void deleteFile(String fileName) throws IOException {
        Path filePath = loadFile(fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
    
    /**
     * 파일이 존재하는지 확인
     */
    public boolean fileExists(String fileName) {
        Path filePath = loadFile(fileName);
        return Files.exists(filePath);
    }
}

