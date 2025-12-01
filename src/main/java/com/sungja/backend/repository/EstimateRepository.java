package com.sungja.backend.repository;

import com.sungja.backend.entity.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    List<Estimate> findByStatus(String status);
    List<Estimate> findByUserType(String userType);
    List<Estimate> findByStatusAndUserType(String status, String userType);
    List<Estimate> findByEstimatedById(Long adminId);
    
    // 개인 고객 견적 조회: 이름, 이메일, 전화번호
    Optional<Estimate> findByNameAndEmailAndPhoneNumber(String name, String email, String phoneNumber);
    
    // 기업 고객 견적 조회: 회사명, 이메일, 전화번호
    Optional<Estimate> findByCompanyNameAndEmailAndPhoneNumber(String companyName, String email, String phoneNumber);
}

