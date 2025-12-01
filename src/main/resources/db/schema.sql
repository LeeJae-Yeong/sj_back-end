-- ============================================
-- 성재가구 백엔드 데이터베이스 스키마
-- MariaDB용 CREATE 문
-- ============================================

-- 데이터베이스 생성 (필요한 경우)
-- CREATE DATABASE IF NOT EXISTS sjproject CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE sjproject;

-- ============================================
-- 견적 요청 테이블 (estimates)
-- ============================================
CREATE TABLE IF NOT EXISTS estimates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '견적 요청 ID',
    user_type VARCHAR(20) NOT NULL COMMENT '사용자 유형 (INDIVIDUAL: 개인, COMPANY: 기업)',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    phone_number VARCHAR(20) NOT NULL COMMENT '전화번호',
    email VARCHAR(100) COMMENT '이메일',
    request_details TEXT COMMENT '견적 요청 상세 내용',
    company_name VARCHAR(100) COMMENT '회사명 (기업 고객인 경우)',
    business_number VARCHAR(50) COMMENT '사업자번호 (기업 고객인 경우)',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '견적 상태 (PENDING: 대기중, PROCESSING: 처리중, COMPLETED: 완료, CANCELLED: 취소)',
    estimated_price DECIMAL(15, 2) COMMENT '견적 가격',
    estimated_by BIGINT COMMENT '견적 처리한 관리자 ID',
    estimated_at DATETIME COMMENT '견적 처리 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_user_type (user_type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_phone_number (phone_number),
    INDEX idx_estimated_by (estimated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견적 요청 테이블';

-- ============================================
-- 관리자 테이블 (admins)
-- ============================================
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '관리자 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '관리자 아이디',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화)',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    email VARCHAR(100) COMMENT '이메일',
    phone_number VARCHAR(20) COMMENT '전화번호',
    role VARCHAR(20) NOT NULL DEFAULT 'STAFF' COMMENT '권한 (ADMIN: 관리자, STAFF: 직원)',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='관리자 테이블';

-- ============================================
-- 제품 테이블 (products)
-- ============================================
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '제품 ID',
    name VARCHAR(200) NOT NULL COMMENT '제품명',
    category VARCHAR(100) COMMENT '카테고리 (책상, 의자, 침대 등)',
    description TEXT COMMENT '제품 설명',
    base_price DECIMAL(15, 2) COMMENT '기본 가격',
    image_url VARCHAR(500) COMMENT '제품 이미지 URL',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_category (category),
    INDEX idx_name (name),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='제품 테이블';

-- ============================================
-- 견적 항목 테이블 (estimate_items)
-- ============================================
CREATE TABLE IF NOT EXISTS estimate_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '견적 항목 ID',
    estimate_id BIGINT NOT NULL COMMENT '견적 요청 ID',
    product_id BIGINT COMMENT '제품 ID (제품이 있는 경우)',
    item_name VARCHAR(200) NOT NULL COMMENT '항목명',
    quantity INT NOT NULL DEFAULT 1 COMMENT '수량',
    unit_price DECIMAL(15, 2) COMMENT '단가',
    total_price DECIMAL(15, 2) COMMENT '총 가격',
    specifications TEXT COMMENT '사양/옵션',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    FOREIGN KEY (estimate_id) REFERENCES estimates(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
    INDEX idx_estimate_id (estimate_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견적 항목 테이블';

-- ============================================
-- 견적 처리 내역 테이블 (estimate_histories)
-- ============================================
CREATE TABLE IF NOT EXISTS estimate_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '처리 내역 ID',
    estimate_id BIGINT NOT NULL COMMENT '견적 요청 ID',
    admin_id BIGINT COMMENT '처리한 관리자 ID',
    action_type VARCHAR(50) NOT NULL COMMENT '처리 유형 (CREATED, UPDATED, STATUS_CHANGED, PRICE_SET, COMPLETED 등)',
    previous_status VARCHAR(20) COMMENT '이전 상태',
    current_status VARCHAR(20) COMMENT '현재 상태',
    note TEXT COMMENT '처리 메모',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    FOREIGN KEY (estimate_id) REFERENCES estimates(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE SET NULL,
    INDEX idx_estimate_id (estimate_id),
    INDEX idx_admin_id (admin_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견적 처리 내역 테이블';

-- ============================================
-- 파일 첨부 테이블 (estimate_attachments)
-- ============================================
CREATE TABLE IF NOT EXISTS estimate_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '첨부파일 ID',
    estimate_id BIGINT NOT NULL COMMENT '견적 요청 ID',
    file_name VARCHAR(255) NOT NULL COMMENT '파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '파일 경로',
    file_size BIGINT COMMENT '파일 크기 (bytes)',
    file_type VARCHAR(100) COMMENT '파일 타입 (MIME)',
    upload_type VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER' COMMENT '업로드 유형 (CUSTOMER: 고객, ADMIN: 관리자)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    FOREIGN KEY (estimate_id) REFERENCES estimates(id) ON DELETE CASCADE,
    INDEX idx_estimate_id (estimate_id),
    INDEX idx_upload_type (upload_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견적 첨부파일 테이블';

-- ============================================
-- 견적 답변 테이블 (estimate_responses)
-- ============================================
CREATE TABLE IF NOT EXISTS estimate_responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '답변 ID',
    estimate_id BIGINT NOT NULL COMMENT '견적 요청 ID',
    admin_id BIGINT COMMENT '답변한 관리자 ID',
    response_content TEXT NOT NULL COMMENT '답변 내용',
    is_public BOOLEAN NOT NULL DEFAULT TRUE COMMENT '고객 공개 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (estimate_id) REFERENCES estimates(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE SET NULL,
    INDEX idx_estimate_id (estimate_id),
    INDEX idx_admin_id (admin_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견적 답변 테이블';
