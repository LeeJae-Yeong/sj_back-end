# back-end

Write your command content here.

This command will be available in chat with /back-end

// Cursor AI 프롬프트 (Java/Spring Boot Backend - Estimate API)

**[Context]**
나는 성재가구의 백엔드 개발자입니다. 스택은 **Java 21, Spring Boot 3, MariaDB**를 사용하며, JPA를 통해 DB에 접근합니다.

**[Goal]**
프론트엔드로부터 견적 요청 데이터를 받아 MariaDB에 저장하는 RESTful API 엔드포인트를 구현해야 합니다.

**[Steps]**
1.  **Entity (`Estimate.java`):** 견적 요청을 저장할 JPA 엔티티를 설계합니다. 필드는 개인/기업 고객 데이터를 모두 포함할 수 있도록 `userType`, `name`, `phoneNumber`, `email`, `requestDetails` 등이 포함되어야 합니다.
2.  **Repository:** JPA Repository 인터페이스를 생성합니다.
3.  **Service (`EstimateService.java`):** 견적 요청 DTO를 받아 엔티티로 변환하고 Repository를 통해 저장하는 비즈니스 로직을 구현합니다.
4.  **Controller (`EstimateController.java`):**
    * `/api/estimates` POST 엔드포인트를 구현합니다.
    * JSON 요청 본문을 매핑하는 DTO를 정의하고, 입력 유효성 검사(@Valid)를 추가합니다.
    * CORS 설정을 추가하여 Vue.js 프론트엔드からの 요청을 허용합니다.

**[Focus]**
RESTful 원칙 준수, 깔끔한 계층 구조(Controller-Service-Repository), 그리고 데이터베이스 연동(MariaDB)을 위한 JPA 설정에 중점을 둡니다.