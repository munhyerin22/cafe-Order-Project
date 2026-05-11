# ☕ Cafe Order Service

> 다수 서버 환경(멀티 인스턴스)에서도 동시성 문제 없이 안정적으로 동작하는  
> **포인트 기반 커피숍 주문 시스템**

---

## 📋 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [기술 스택](#2-기술-스택)
3. [ERD 설계](#3-erd-설계)
4. [API 명세](#4-api-명세)
5. [핵심 설계 결정 및 의도](#5-핵심-설계-결정-및-의도)
6. [동시성 문제 해결 전략](#6-동시성-문제-해결-전략)
7. [패키지 구조](#7-패키지-구조)
8. [실행 방법](#8-실행-방법)

---

## 1. 프로젝트 소개

### 배경 및 목적

카운터의 QR 코드를 스캔하여 주문 페이지에 접근하고, 포인트를 충전한 뒤 음료를 주문·결제하는 시스템입니다.  
**수평 확장(Scale-out) 환경**에서도 데이터 정합성을 보장하고, 주문 데이터를 실시간으로 데이터 수집 플랫폼에 전송하며, 최근 7일 인기 메뉴 TOP 3를 추천합니다.

### 핵심 요구사항

| # | 기능 |
|---|------|
| 1 | 커피 메뉴 목록 조회 |
| 2 | 포인트 충전 (단위 제한 포함) |
| 3 | 커피 주문 및 포인트 결제 |
| 4 | 주문 데이터 실시간 외부 플랫폼 비동기 전송 |
| 5 | 최근 7일 인기 메뉴 TOP 3 조회 |

---

## 2. 기술 스택

| 분류 | 기술 | 선택 근거 |
|------|------|-----------|
| Language | Java 21 | Record, LTS 안정성 |
| Framework | Spring Boot 3.5 | 자동 설정, JPA, 트랜잭션 관리 |
| ORM | Spring Data JPA | 트랜잭션 관리, 비관적 락 지원 |
| DB | MySQL 8.0 | ACID 보장, `SELECT FOR UPDATE` 지원 |
| Cache | Redis | 메뉴 캐싱, 인기 메뉴 집계 결과 저장 |
| 인증 | Spring Security + JWT (JJWT) | Stateless 인증으로 멀티 인스턴스 대응 |
| Build | Gradle | 빠른 빌드, 의존성 관리 |
| Test | JUnit 5 + Mockito + TestContainers | 단위·통합·동시성 테스트 |
| Scheduler | ShedLock | 멀티 인스턴스 스케줄러 중복 실행 방지 |
| Container | Docker / Docker Compose | 로컬 개발 인프라 단일 명령 실행 |
| CI/CD | GitHub Actions | PR 자동 테스트, Docker 이미지 자동 빌드 |

---

## 3. ERD 설계

```
users
├── id (PK, BIGINT, AUTO_INCREMENT)
├── uuid (VARCHAR(36), UNIQUE)         -- 외부 노출용 식별자
├── phone (VARCHAR(20), UNIQUE)        -- 로그인 식별자
├── pin (VARCHAR(60))                  -- BCrypt 해싱 저장
├── point (BIGINT, DEFAULT 0)          -- 현재 보유 포인트 (비정규화)
├── created_at, updated_at

menus
├── id (PK)
├── name (VARCHAR(100))
├── price (INT)
├── is_available (BOOLEAN, DEFAULT TRUE) -- 소프트 삭제 (품절/비활성)
├── created_at, updated_at

orders
├── id (PK)
├── user_id (FK → users.id)
├── total_price (INT)
├── status (VARCHAR(20), DEFAULT 'COMPLETED')
├── created_at, updated_at

order_items
├── id (PK)
├── order_id (FK → orders.id)
├── menu_id (FK → menus.id)
├── menu_name (VARCHAR(100))   -- 주문 시점 메뉴명 스냅샷
├── menu_price (INT)           -- 주문 시점 가격 스냅샷
├── quantity (INT, DEFAULT 1)

point_history
├── id (PK)
├── user_id (FK → users.id)
├── type (VARCHAR(20))         -- CHARGE | USE | REFUND
├── amount (INT)
├── balance_after (INT)        -- 트랜잭션 후 잔액
├── reference_id (BIGINT)      -- order_id (USE 시) 또는 NULL
├── created_at, updated_at
```

### 설계 의도

- **`users.uuid`** : 내부 PK(`id`)와 외부 식별자(`uuid`)를 분리하여 보안성과 JOIN 성능을 모두 확보합니다.
- **`users.point` 비정규화** : 매번 `point_history`를 SUM하는 방식은 성능 문제가 발생하므로 현재 잔액을 별도 컬럼에 보관합니다.
- **`order_items` 스냅샷** : `menu_name`, `menu_price`를 주문 시점 기준으로 저장하여, 메뉴 가격이 변경되어도 과거 주문 내역의 금액 정합성을 유지합니다.
- **`menus.is_available` 소프트 삭제** : 실제 DELETE를 사용하지 않아 과거 주문 내역의 메뉴 참조를 보장합니다.
- **`point_history.balance_after`** : 특정 시점의 잔액 복원 및 이상 탐지가 가능합니다.

---

## 4. API 명세

### 인증

| 기능 | HTTP | Path | 인증 |
|------|------|------|------|
| 회원가입 | POST | `/api/v1/users/signup` | ❌ |
| 로그인 | POST | `/api/v1/users/login` | ❌ |

### 사용자

| 기능 | HTTP | Path | 인증 |
|------|------|------|------|
| 잔여 포인트 조회 | GET | `/api/v1/users/me/point` | ✅ |
| 전화번호 변경 | PATCH | `/api/v1/users/me/phone` | ✅ |
| PIN 변경 | PATCH | `/api/v1/users/me/pin` | ✅ |

### 메뉴

| 기능 | HTTP | Path | 인증 |
|------|------|------|------|
| 커피 메뉴 목록 조회 | GET | `/api/v1/menus` | ❌ |
| 인기 메뉴 TOP 3 조회 | GET | `/api/v1/menus/popular` | ❌ |

### 포인트

| 기능 | HTTP | Path | 인증 |
|------|------|------|------|
| 포인트 충전 | POST | `/api/v1/point/charge` | ✅ |
| 포인트 환불 | POST | `/api/v1/point/refund` | ✅ |

### 주문

| 기능 | HTTP | Path | 인증 |
|------|------|------|------|
| 커피 주문 / 포인트 결제 | POST | `/api/v1/orders` | ✅ |

---

### 공통 응답 형식

```json
// 성공
{
  "success": true,
  "data": { ... },
  "error": null
}

// 실패
{
  "success": false,
  "data": null,
  "error": {
    "code": "INSUFFICIENT_POINT",
    "message": "포인트가 부족합니다."
  }
}
```

### 에러 코드 정의

| 에러 코드 | HTTP | 설명 |
|-----------|------|------|
| `AUTH_FAILED` | 401 | 전화번호 없음 또는 PIN 불일치 |
| `INVALID_TOKEN` | 401 | JWT 유효하지 않음 (만료·위변조) |
| `USER_NOT_FOUND` | 404 | 존재하지 않는 사용자 |
| `DUPLICATE_PHONE` | 409 | 이미 등록된 전화번호 |
| `INVALID_PIN` | 401 | PIN 불일치 |
| `SAME_PHONE` | 409 | 현재와 동일한 전화번호 |
| `MENU_NOT_FOUND` | 404 | 존재하지 않는 메뉴 |
| `MENU_NOT_AVAILABLE` | 400 | 품절 또는 비활성 메뉴 |
| `INSUFFICIENT_POINT` | 400 | 포인트 잔액 부족 |
| `INVALID_CHARGE_AMOUNT` | 400 | 허용되지 않는 충전 단위 (5000/10000/30000/50000 외) |
| `POINT_LIMIT_EXCEEDED` | 400 | 충전 후 200,000P 초과 |
| `INVALID_REFUND_AMOUNT` | 400 | 허용되지 않는 환불 단위 (10,000 배수 아닌 경우) |
| `INSUFFICIENT_REFUND_POINT` | 400 | 환불 요청 금액 > 잔여 포인트 |

---

## 5. 핵심 설계 결정 및 의도

### 5-1. 인증 방식 — 전화번호 + 4자리 PIN 기반 JWT

| 방식 | 멀티 인스턴스 | UX | 보안 | 선택 |
|------|-------------|-----|------|------|
| 서버 세션 | ❌ 세션 스토어 별도 필요 | 보통 | 중간 | ❌ |
| 이메일 + 비밀번호 | ✅ | 느림 | 높음 | ❌ |
| 전화번호만 | ✅ | 빠름 | ❌ 도용 위험 | ❌ |
| 전화번호 + OTP | ✅ | 빠름 | 높음 | ❌ 외부 서비스 필요 |
| **전화번호 + 4자리 PIN** | **✅** | **빠름** | **중간** | **✅ 채택** |

- JWT Access Token (만료 1시간)만 발급. Refresh Token 미발급 — QR 주문 단기 세션에 적합
- 멀티 인스턴스 환경에서 어느 서버에 요청이 도달해도 동일하게 인증 가능 (Stateless)

### 5-2. 메뉴 목록 캐싱 — Redis `@Cacheable`

- 메뉴 목록은 변경 빈도가 낮고 조회 빈도가 높음 → Redis 캐싱 적용
- 메뉴 추가·변경 시 캐시 무효화 (`@CacheEvict`)

### 5-3. 인기 메뉴 집계 — 주기적 갱신 캐시 + ShedLock

- 7일치 데이터를 매 요청마다 DB 풀스캔하면 응답 지연 발생
- **1분 주기 스케줄러**가 DB 집계 결과를 Redis에 갱신
- **ShedLock**으로 멀티 인스턴스 환경에서 스케줄러가 단 하나의 인스턴스에서만 실행되도록 보장

### 5-4. 외부 플랫폼 실시간 전송 — `@TransactionalEventListener`

- 주문 트랜잭션 커밋 후에만 전송 시도 (`AFTER_COMMIT`)
- 롤백된 주문 데이터가 외부에 전송되는 것을 방지
- `@Async`로 비동기 처리 → 전송 실패가 주문 결과에 영향을 주지 않음

---

## 6. 동시성 문제 해결 전략

### 문제 상황

멀티 인스턴스 환경에서 JVM 수준의 `synchronized`는 프로세스 경계를 넘지 못합니다.

```
[Load Balancer]
   ↙        ↘
[App-1]    [App-2]   ← 각자 다른 JVM, synchronized 공유 불가
   ↘        ↙
    [MySQL]
```

### 해결 전략

| 전략 | 멀티 인스턴스 | 성능 | 복잡도 | 채택 |
|------|-------------|------|--------|------|
| JVM synchronized | ❌ | 높음 | 낮음 | ❌ |
| **DB 비관적 락 (FOR UPDATE)** | **✅** | **중간** | **낮음** | **✅ 1차 채택** |
| Redis 분산 락 (Redisson) | ✅ | 높음 | 높음 | 2차 확장 |

### 적용 대상

- **포인트 충전** : 동시에 여러 요청이 와도 최종 포인트 정합성 보장
- **포인트 차감 (주문 결제)** : 잔액 부족 상황에서 중복 차감 방지
- **포인트 환불** : 동시 환불 요청 시 잔액 음수 방지

```java
// 비관적 락 적용 예시
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT u FROM User u WHERE u.uuid = :uuid")
Optional<User> findByUuidWithLock(@Param("uuid") String uuid);
```

### 테스트 전략

| 레이어 | 도구 | 목적 |
|--------|------|------|
| 통합 테스트 | @SpringBootTest + TestContainers | 실제 MySQL 환경에서 전체 플로우 검증 |
| 동시성 테스트 | ExecutorService + CountDownLatch | Race Condition 검증 |

**핵심 동시성 테스트 시나리오:**
- 동일 사용자 동시 충전 10회 → 최종 포인트 정합성 검증
- 잔액 4,500P 상태에서 4,500P 메뉴를 동시 2건 주문 → 1건만 성공
- 주문 트랜잭션 실패 시 외부 전송 미발생 확인
- 동시 환불 요청 10회 → 최종 포인트 음수 미발생 검증

---

## 7. 패키지 구조

```
src/main/java/spartaclub/cafeorderservice/
├── CafeOrderServiceApplication.java
├── auth/
│   ├── controller/        # AuthController (로그인)
│   ├── dto/               # LoginRequest, LoginResponse
│   ├── security/          # JwtTokenProvider, JwtAuthenticationFilter
│   │                      # CustomUserDetails, CustomUserDetailsService
│   └── service/           # AuthService
├── common/
│   ├── config/            # SecurityConfig, JpaConfig, RedisConfig, AsyncConfig
│   ├── exception/         # CustomException, ErrorCode, GlobalExceptionHandler
│   └── response/          # ApiResponse
└── domain/
    ├── BaseEntity.java
    ├── menu/
    │   ├── controller/    # MenuController
    │   ├── dto/           # MenuResponse, PopularMenuResponse
    │   ├── entity/        # Menu
    │   ├── repository/    # MenuRepository
    │   └── service/       # MenuService
    ├── order/
    │   ├── controller/    # OrderController
    │   ├── dto/           # OrderRequest, OrderResponse
    │   ├── entity/        # Order, OrderItem
    │   ├── event/         # OrderCompletedEvent, DataPlatformEventListener
    │   ├── repository/    # OrderRepository, OrderItemRepository
    │   └── service/       # OrderService
    ├── point/
    │   ├── controller/    # PointController
    │   ├── dto/           # PointChargeRequest, PointRefundRequest, PointResponse
    │   ├── entity/        # PointHistory
    │   ├── repository/    # PointHistoryRepository
    │   └── service/       # PointService
    └── user/
        ├── controller/    # UserController
        ├── dto/           # SignupRequest, SignupResponse, UpdatePhoneRequest ...
        ├── entity/        # User
        ├── repository/    # UserRepository
        └── service/       # UserService
```

---

## 8. 실행 방법

### 사전 요구사항

- Java 21
- Docker & Docker Compose

### 로컬 개발 환경 실행

```bash
# 1. 인프라 실행 (MySQL + Redis)
docker compose -f src/main/resources/docker-compose.yml up -d

# 2. 애플리케이션 실행 (local 프로파일)
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 운영 환경 실행

```bash
# Docker 이미지 빌드
docker build -t cafe-order-service .

# 환경변수 주입 후 실행
docker run -e SPRING_DATASOURCE_URL=... \
           -e SPRING_DATASOURCE_USERNAME=... \
           -e SPRING_DATASOURCE_PASSWORD=... \
           -e SPRING_PROFILES_ACTIVE=prod \
           -p 8080:8080 cafe-order-service
```

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "클래스명"
```

---

## 인프라 구성 다이어그램

```
[클라이언트]
    ↓
[Load Balancer (Nginx / AWS ALB)]
   ↙        ↓        ↘
[App-1]  [App-2]  [App-3]   ← 동일 이미지, Stateless JWT 인증
   ↓         ↓        ↓
[MySQL Primary]              ← 모든 쓰기 / 비관적 락으로 동시성 제어
    ↕
[Redis]
  - 메뉴 목록 캐시
  - 인기 메뉴 캐시 (1분 주기 갱신 + ShedLock)
```
