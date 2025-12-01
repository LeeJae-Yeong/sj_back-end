# 성재가구 백엔드 API

Spring Boot 3를 사용한 RESTful API 서버입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - 데이터베이스 접근
- **MariaDB** - 관계형 데이터베이스
- **Lombok** - 보일러플레이트 코드 감소
- **Jakarta Validation** - 입력 데이터 검증

## 프로젝트 구조

```
back-end/
├── src/main/java/com/sungja/backend/
│   ├── BackendApplication.java          # 메인 애플리케이션 클래스
│   ├── config/
│   │   └── CorsConfig.java              # CORS 설정
│   ├── controller/                      # REST API 컨트롤러
│   │   ├── EstimateController.java     # 견적 요청 API
│   │   ├── EstimateResponseController.java  # 견적 답변 API
│   │   └── ProductController.java      # 제품 관리 API
│   ├── dto/                             # 데이터 전송 객체
│   │   ├── EstimateRequestDTO.java
│   │   ├── EstimateUpdateDTO.java
│   │   ├── EstimateItemDTO.java
│   │   ├── EstimateResponseDTO.java
│   │   └── ProductDTO.java
│   ├── entity/                          # JPA 엔티티
│   │   ├── Estimate.java                # 견적 요청
│   │   ├── EstimateItem.java           # 견적 항목
│   │   ├── EstimateHistory.java        # 견적 처리 내역
│   │   ├── EstimateAttachment.java     # 견적 첨부파일
│   │   ├── EstimateResponse.java       # 견적 답변
│   │   ├── Product.java                 # 제품
│   │   └── Admin.java                   # 관리자
│   ├── repository/                      # JPA Repository
│   │   ├── EstimateRepository.java
│   │   ├── EstimateItemRepository.java
│   │   ├── EstimateHistoryRepository.java
│   │   ├── EstimateAttachmentRepository.java
│   │   ├── EstimateResponseRepository.java
│   │   ├── ProductRepository.java
│   │   └── AdminRepository.java
│   └── service/                         # 비즈니스 로직
│       ├── EstimateService.java
│       ├── EstimateResponseService.java
│       └── ProductService.java
└── src/main/resources/
    ├── application.properties           # 애플리케이션 설정
    └── db/                              # 데이터베이스 스키마
        ├── schema.sql
        └── migration/
            └── create_estimates_table.sql
```

## API 엔드포인트

### 1. 견적 요청 API (`/api/estimates`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/estimates` | 견적 요청 생성 |
| `GET` | `/api/estimates` | 견적 목록 조회 (선택: `?status=PENDING`) |
| `GET` | `/api/estimates/{id}` | 특정 견적 조회 |
| `PUT` | `/api/estimates/{id}` | 견적 업데이트 |
| `POST` | `/api/estimates/inquiry/individual` | 개인 고객 견적 조회 (이름, 이메일, 전화번호) |
| `POST` | `/api/estimates/inquiry/company` | 기업 고객 견적 조회 (회사명, 이메일, 전화번호) |

**요청 예시 (견적 생성):**
```json
{
  "userType": "INDIVIDUAL",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "email": "hong@example.com",
  "requestDetails": "문의 내용",
  "items": [
    {
      "itemName": "책상",
      "quantity": 2,
      "unitPrice": 100000,
      "specifications": "원목 재질"
    }
  ]
}
```

### 2. 견적 답변 API (`/api/estimate-responses`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/estimate-responses?adminId={id}` | 견적 답변 생성 |
| `GET` | `/api/estimate-responses/estimate/{estimateId}` | 견적별 답변 조회 (선택: `?publicOnly=true`) |
| `PUT` | `/api/estimate-responses/{id}` | 답변 업데이트 |
| `DELETE` | `/api/estimate-responses/{id}` | 답변 삭제 |

### 3. 제품 관리 API (`/api/products`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/products` | 제품 생성 |
| `GET` | `/api/products` | 제품 목록 조회<br/>- `?category=카테고리`<br/>- `?search=검색어`<br/>- `?activeOnly=true` |
| `GET` | `/api/products/{id}` | 특정 제품 조회 |
| `PUT` | `/api/products/{id}` | 제품 업데이트 |
| `DELETE` | `/api/products/{id}` | 제품 삭제 |

## 데이터베이스 설정

### MariaDB 설정

1. MariaDB 설치 및 실행
2. 데이터베이스 생성:
```sql
CREATE DATABASE sjproject CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. `application.properties` 파일 수정:
```properties
spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/sjproject?useUnicode=true&characterEncoding=utf8
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 실행 방법

### 1. 사전 요구사항
- Java 17 이상
- Maven 3.6 이상
- MariaDB 10.x 이상

### 2. 의존성 설치 및 빌드
```bash
cd back-end
mvn clean install
```

### 3. 애플리케이션 실행
```bash
mvn spring-boot:run
```

또는 빌드된 JAR 파일 실행:
```bash
java -jar target/back-end-1.0.0.jar
```

### 4. 서버 접속
- 기본 포트: `http://localhost:1234`
- API Base URL: `http://localhost:1234/api`

## 주요 기능

### 견적 관리
- ✅ 개인/기업 고객 견적 요청 처리
- ✅ 견적 상태 관리 (PENDING, PROCESSING, COMPLETED, CANCELLED)
- ✅ 견적 항목 관리
- ✅ 견적 처리 내역 기록
- ✅ 고객 정보로 견적 조회

### 제품 관리
- ✅ 제품 CRUD 작업
- ✅ 카테고리별 제품 조회
- ✅ 제품 검색 기능
- ✅ 활성 제품만 조회

### 견적 답변
- ✅ 관리자 견적 답변 작성
- ✅ 공개/비공개 답변 관리
- ✅ 견적별 답변 조회

## 데이터베이스 엔티티 관계

```
Estimate (견적)
├── EstimateItem (견적 항목) - OneToMany
├── EstimateHistory (처리 내역) - OneToMany
├── EstimateAttachment (첨부파일) - OneToMany
├── EstimateResponse (답변) - OneToMany
└── Admin (관리자) - ManyToOne (estimatedBy)

EstimateItem
└── Product (제품) - ManyToOne
```

## CORS 설정

개발 환경을 위해 모든 origin에서의 요청을 허용하도록 설정되어 있습니다.
프로덕션 환경에서는 `CorsConfig.java`에서 특정 origin만 허용하도록 수정해야 합니다.

## 응답 형식

모든 API는 다음 형식으로 응답합니다:

**성공 응답:**
```json
{
  "success": true,
  "message": "성공 메시지",
  "data": { ... },
  "count": 0  // 목록 조회 시에만 포함
}
```

**오류 응답:**
```json
{
  "success": false,
  "message": "오류 메시지"
}
```

## 개발 환경 설정

### IntelliJ IDEA / Eclipse
1. 프로젝트를 IDE로 import
2. Maven 프로젝트로 인식
3. `BackendApplication.java`를 실행

### VS Code
1. Java Extension Pack 설치
2. 프로젝트 폴더 열기
3. 터미널에서 `mvn spring-boot:run` 실행

## 주의사항

1. **데이터베이스 연결**: MariaDB가 실행 중이어야 합니다.
2. **포트 충돌**: 포트 1234가 사용 중이면 `application.properties`에서 변경하세요.
3. **CORS**: 프로덕션 환경에서는 CORS 설정을 제한해야 합니다.
4. **JSON 순환 참조**: 엔티티 간 양방향 관계는 `@JsonIgnoreProperties`로 처리되어 있습니다.

## 라이선스

© 2024 성재가구. All rights reserved.

