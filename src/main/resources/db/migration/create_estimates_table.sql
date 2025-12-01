-- 견적 요청 테이블 생성
CREATE TABLE IF NOT EXISTS estimates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '견적 요청 ID',
    user_type VARCHAR(20) NOT NULL COMMENT '사용자 유형 (INDIVIDUAL: 개인, COMPANY: 기업)',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    phone_number VARCHAR(20) NOT NULL COMMENT '전화번호',
    email VARCHAR(100) COMMENT '이메일',
    request_details TEXT COMMENT '견적 요청 상세 내용',
    company_name VARCHAR(100) COMMENT '회사명 (기업 고객인 경우)',
    business_number VARCHAR(50) COMMENT '사업자번호 (기업 고객인 경우)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    INDEX idx_user_type (user_type),
    INDEX idx_created_at (created_at),
    INDEX idx_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견적 요청 테이블';

