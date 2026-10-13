# BE 25일차 - Microservice와 Spring Cloud

## 1. 오늘의 핵심

오늘은 Monolithic Architecture와 Microservice Architecture의 차이를 비교하고, 여러 Microservice를 운영하기 위해 필요한 Spring Cloud의 구성요소를 학습했다.

```text
Monolithic
하나의 애플리케이션 안에 모든 기능을 구성
        ↓
일부 기능을 수정해도 전체 애플리케이션을 다시 빌드·배포

Microservice Architecture
비즈니스 기능을 독립된 서비스로 분리
        ↓
변경된 서비스만 빌드·테스트·배포·확장 가능
```

MSA는 단순히 Spring Boot 프로젝트를 여러 개 만드는 것이 아니다. 서비스가 분리되면서 생기는 주소 관리, 설정 관리, 서비스 간 통신, 장애 전파, 데이터 일관성, 모니터링 문제까지 함께 해결해야 한다.

오늘 학습한 전체 구조는 다음과 같다.

```text
Client
  ↓ 하나의 진입점
API Gateway
  ├─ Service Discovery에서 서비스 위치 조회
  ├─ User Service
  ├─ Order Service
  └─ Catalog Service

공통 설정      : Config Server
동기 통신      : REST API
비동기 통신    : Kafka · RabbitMQ
실행·격리      : Container
배포 자동화    : CI/CD
관측           : Log · Metric · Trace
```

---

## 2. Software Architecture란?

Software Architecture는 소프트웨어를 구성하는 요소와 요소 사이의 관계를 정의한 전체 구조다.

```text
구성요소 + 구성요소 사이의 관계 = Software Architecture
```

Architecture는 단순히 기능을 구현하는 방법만 설명하지 않는다. 개발자, 설계자, 운영자, 사용자 등 서로 다른 이해관계자가 시스템의 전체 구조와 호출 관계를 함께 이해하도록 돕는 의사소통 도구이기도 하다.

대표적인 관점은 다음과 같다.

| 관점 | 확인하는 내용 |
| --- | --- |
| Logical View | 기능과 주요 객체의 구조 |
| Process View | 실행 과정과 동시성 |
| Implementation View | 코드와 모듈의 구성 |
| Deployment View | 서버와 실행 환경의 배치 |
| Use Case View | 사용자의 기능 이용 흐름 |

---

## 3. Monolithic Architecture

Monolith는 여러 업무 기능을 하나의 애플리케이션으로 패키징하고, 대체로 하나의 배포 단위로 운영하는 구조다.

```text
Client
  ↓
Monolithic Application
  ├─ User
  ├─ Blog
  ├─ Comment
  └─ Order
  ↓
Database
```

예를 들어 사용자 기능만 수정해도 일반적으로 전체 애플리케이션을 다시 빌드하고 배포해야 한다.

```text
User 기능 수정
   ↓
전체 Test
   ↓
전체 Build
   ↓
전체 Application 배포
```

### 장점

- 코드 탐색과 Debugging이 비교적 쉽다.
- 하나의 프로세스 안에서 호출하므로 Network Latency가 적다.
- Transaction과 데이터 일관성을 관리하기 쉽다.
- 작은 서비스에서는 개발과 배포 구조가 단순하다.

### 단점

- 기능 사이의 결합도가 커지기 쉽다.
- 일부 기능만 변경해도 전체를 다시 배포해야 한다.
- 특정 기능에만 Traffic이 몰려도 전체 애플리케이션을 확장해야 한다.
- 하나의 장애가 전체 시스템 장애로 전파될 수 있다.
- 기술 Stack을 부분적으로 바꾸기 어렵다.

Monolith가 나쁜 구조인 것은 아니다. 규모가 작고 강한 Transaction 일관성이 중요한 서비스라면 오히려 더 단순하고 효율적일 수 있다.

---

## 4. Microservice Architecture

Microservice는 하나의 애플리케이션을 비즈니스 역량에 따라 작고 자율적인 서비스들로 구성하는 방식이다. 각 서비스는 독립된 프로세스로 실행되고 가벼운 통신 방식으로 협력한다.

```text
Client
  ↓
Frontend
  ↓
API Gateway
  ├─ User Service    → User DB
  ├─ Blog Service    → Blog DB
  └─ Order Service   → Order DB
```

사용자 기능이 변경되었다면 User Service만 테스트하고 배포할 수 있다.

```text
User Service 수정
   ↓
User Service Test
   ↓
User Service Build
   ↓
User Service만 배포
```

### 핵심 특징

- 작은 서비스: 하나의 명확한 비즈니스 기능에 집중한다.
- 독립된 서비스: 구현, 실행, 배포, 확장이 독립적이다.
- 응집된 서비스: 서로 관련된 기능은 하나의 서비스 경계 안에 둔다.
- 자율적 서비스: 담당 Team이 개발부터 배포와 운영까지 책임진다.
- 독립된 데이터: 서비스가 자신의 데이터에 대한 Ownership을 가진다.
- API Contract: 서비스는 내부 구현 대신 API 계약으로 통신한다.

### Bounded Context

MSA의 서비스 경계는 단순히 Controller 수나 Table 수로 나누지 않는다. Domain Driven Design의 `Bounded Context`를 기준으로 같은 의미와 업무 규칙을 공유하는 기능을 하나의 경계로 묶는다.

```text
CRM 전체
  ├─ 고객 서비스
  ├─ 계약 서비스
  └─ VOC 서비스
```

같은 업무를 담당하는 구성원이 동일한 용어를 사용하는 것을 `Ubiquitous Language`라고 한다. 서비스 경계를 정할 때 기능, 데이터, 사용자 흐름, 조직의 Ownership을 함께 살펴야 한다.

---

## 5. Monolith와 MSA 비교

| 기준 | Monolith | MSA |
| --- | --- | --- |
| 배포 단위 | 전체 Application | 개별 Service |
| 확장 | 전체를 함께 확장 | 필요한 Service만 확장 |
| 호출 | 주로 Process 내부 호출 | Network 기반 호출 |
| 데이터 | 공유 DB 구성이 쉬움 | Service별 DB 소유 권장 |
| Transaction | 상대적으로 단순 | 분산 Transaction이 어려움 |
| 장애 | 전체로 전파될 가능성 | 경계를 잘 설계하면 격리 가능 |
| 기술 Stack | 통일되기 쉬움 | Service별 선택 가능 |
| 운영 복잡도 | 비교적 낮음 | 매우 높음 |

### MSA의 장점

- Loose Coupling: 서비스 사이의 결합도를 낮출 수 있다.
- Independent Life Cycle: 서비스별 개발·테스트·배포가 가능하다.
- Independent Scalability: 요청이 많은 서비스만 확장할 수 있다.
- Isolated Failure: 장애 전파를 제한할 수 있다.
- Polyglot Technology: 서비스 특성에 맞는 언어와 DB를 선택할 수 있다.

### MSA의 단점

- 서비스 호출 시 Network Latency가 발생한다.
- 호출 대상의 장애로 전체 가용성이 낮아질 수 있다.
- 서비스별 DB를 사용하면 데이터 일관성과 Transaction 관리가 어렵다.
- 서비스 경계와 객체를 설계하는 복잡도가 높다.
- 배포, 로그, 인증, Monitoring 등 운영 대상이 증가한다.

분산 환경의 보상 Transaction이나 Saga Pattern은 실패한 작업을 되돌리는 흐름을 제공하지만, 하나의 DB Transaction과 같은 강한 일관성을 항상 보장하는 것은 아니다.

---

## 6. SOA와 MSA

SOA와 MSA는 모두 Service 단위 설계를 사용하지만 지향점과 Service의 크기가 다르다.

| 구분 | SOA | MSA |
| --- | --- | --- |
| 주된 목적 | Service 재사용으로 비용 절감 | 결합도를 낮추고 변화에 빠르게 대응 |
| Service 크기 | 상대적으로 큼 | 작고 명확한 업무에 집중 |
| 통합 방식 | ESB 중심 | REST API·Message Broker 중심 |
| 공유 성향 | 공통 Service 공유 | Service 공유를 최소화 |
| Ownership | 조직과 역할이 나뉠 수 있음 | Service 담당 Team이 전체 수명주기 담당 |

---

## 7. Cloud Native Architecture

Cloud Native는 Cloud 환경의 확장성과 자동화를 적극 활용해 Application을 개발하고 운영하는 방식이다.

### 네 가지 핵심 요소

```text
Cloud Native Application
├─ Microservices
├─ Containers
├─ CI/CD
└─ DevOps
```

### Architecture의 목표

1. 확장 가능한 Architecture
   - 수평 확장에 유연해야 한다.
   - 부하를 여러 Instance로 분산한다.
   - Service별로 Packaging하고 확장할 수 있어야 한다.

2. 탄력적인 Architecture
   - Service의 추가와 삭제를 자동으로 감지한다.
   - 요청을 현재 실행 중인 Instance로 동적으로 전달한다.
   - Stateless 통신을 활용해 Instance 교체를 쉽게 한다.

3. 장애 격리
   - 특정 Service의 장애가 다른 Service로 퍼지는 것을 제한한다.
   - Timeout, Retry, Circuit Breaker 등을 사용해 연쇄 장애를 방지한다.

---

## 8. Antifragile System

수업 자료에서는 현대의 Cloud Native System을 `Antifragile`이라는 관점으로 설명했다.

```text
Antifragile을 위한 요소
├─ Auto Scaling
├─ Microservices
├─ Chaos Engineering
└─ Continuous Deployment
```

### Resilience와 Antifragile

- Resilience: 장애가 발생해도 원래 상태로 회복하는 능력
- Antifragile: 변화와 충격을 겪으며 시스템이 더 적응력 있게 발전하는 성질

실제 Software 설계에서는 장애를 예상하고 자동 복구할 수 있는 `Resilience`가 우선적인 목표가 된다.

### Chaos Engineering

CPU 부하, Network 지연, Instance 종료, 순간 Traffic 증가 같은 장애를 의도적으로 발생시켜 시스템이 실제로 복구되는지 검증하는 방법이다.

```text
장애 가설 수립
   ↓
통제된 장애 주입
   ↓
Monitoring
   ↓
복구 여부 확인
   ↓
취약점 개선
```

---

## 9. Scale Up과 Scale Out

| 방식 | 의미 | 예시 |
| --- | --- | --- |
| Scale Up | 한 서버의 사양을 높임 | CPU 2 Core → 8 Core |
| Scale Out | 서버 Instance 수를 늘림 | 2대 → 10대 |
| Scale Down | 서버 Instance 수를 줄임 | 10대 → 2대 |

Cloud Native와 MSA에서는 요청량에 따라 Service Instance 수를 조절하는 Scale Out·Down 방식이 중요하다.

```text
평상시
User Service × 2

Traffic 증가
User Service × 8
Order Service × 2

→ 필요한 User Service만 확장
```

---

## 10. API Gateway

MSA에서는 Service마다 서로 다른 IP와 Port가 존재할 수 있다. Client가 모든 Service의 주소를 직접 알고 있으면 Service가 추가되거나 주소가 변경될 때 Client도 함께 수정해야 한다.

API Gateway는 Client 요청을 받는 단일 진입점이다.

```text
Client
  │ https://api.example.com
  ↓
API Gateway
  ├─ /users/**    → User Service
  ├─ /orders/**   → Order Service
  └─ /catalogs/** → Catalog Service
```

Client는 Gateway의 외부 주소만 알면 된다. 내부 Service의 위치와 Port는 Gateway 뒤에 숨길 수 있다.

### 주요 역할

- 요청 Routing
- 인증·인가의 공통 처리
- Load Balancing
- 외부망과 내부망의 분리
- Logging과 요청 추적
- 요청·응답 변환

Gateway 자체를 배포하면 하나의 접근 주소를 가질 수 있지만, 운영 환경에서는 여러 Gateway Instance와 Load Balancer를 함께 구성해 Gateway가 단일 장애점이 되지 않도록 해야 한다.

---

## 11. Service Discovery

Service Instance는 배포와 Auto Scaling에 따라 IP와 Port가 계속 변할 수 있다. 이를 코드에 직접 작성하면 매번 설정을 변경해야 한다.

```text
User Service Instance 시작
       ↓ 자신의 위치 등록
Service Discovery
       ↓ 위치 조회
API Gateway 또는 다른 Service
       ↓
User Service 호출
```

### 핵심 기능

- Service 등록
- Service 위치 검색
- Heartbeat를 통한 상태 확인
- 종료되거나 비정상인 Instance 제거

중요한 점은 API Gateway가 모든 Service 주소를 직접 등록하는 것이 아니라, 일반적으로 각 Service Instance가 Discovery Server에 자신을 등록한다는 것이다. Gateway는 Discovery Server를 조회해 현재 이용 가능한 Service를 찾는다.

Eureka Client는 기본 설정에서 주기적으로 Heartbeat를 전송하며 대표적인 주기가 약 30초다. 이는 모든 변경 사항을 `PATCH` 요청으로 갱신한다는 의미가 아니라, 임대 정보 갱신과 Registry 동기화가 주기적으로 수행된다는 의미다.

---

## 12. Configuration Service

Service마다 `application.yml`을 따로 가지고 있으면 DB 주소, Token 설정, Logging 설정 같은 공통 설정이 중복된다.

```text
User Service/application.yml
Order Service/application.yml
Catalog Service/application.yml
```

Config Server는 여러 Service의 설정을 중앙에서 관리한다.

```text
Git Configuration Repository
            ↓
      Config Server
       ├─ User Service
       ├─ Order Service
       └─ Catalog Service
```

### 장점

- 환경별 설정을 한곳에서 관리한다.
- 중복 설정을 줄인다.
- 설정 변경 이력을 Git으로 관리할 수 있다.
- 개발·테스트·운영 환경 설정을 분리할 수 있다.

Spring Cloud Bus를 함께 사용하면 설정 변경 Event를 Message Broker를 통해 여러 Service에 전달할 수 있다. 단, 모든 설정이 Runtime에 즉시 적용되는 것은 아니며 Refresh 지원 범위와 보안 정보 관리 방식을 함께 고려해야 한다.

---

## 13. Service 간 통신

분리된 Service도 하나의 업무를 완료하기 위해 서로 통신해야 한다.

### 동기 통신 - REST API

```text
Order Service
   │ User 정보 요청
   ↓
User Service
   │ 응답이 올 때까지 대기
   ↓
Order Service 작업 계속
```

- 응답을 즉시 받아 다음 작업을 해야 할 때 적합하다.
- 흐름이 직관적이다.
- 호출 대상이 느리거나 장애가 발생하면 호출 Service도 영향을 받는다.
- Timeout, Retry, Circuit Breaker가 필요하다.

### 비동기 통신 - Message Broker

```text
Order Service(Publisher)
        ↓ 주문 Event 발행
Kafka 또는 RabbitMQ
        ↓ Event 구독
Inventory Service(Subscriber)
```

- 발행자는 소비자의 처리를 기다리지 않는다.
- 순간 Traffic을 Queue에 저장해 완충할 수 있다.
- Service 간 결합도를 낮출 수 있다.
- Event 중복, 순서, 재처리, 최종적 일관성을 고려해야 한다.

수업 메모의 `NEVIGATE`는 Message Queuing 제품명이 아니라 오기이며, 대표적인 Message Broker로 `Kafka`와 `RabbitMQ`를 사용한다.

---

## 14. Service Mesh

Service Mesh는 Microservice 사이의 Network 통신 기능을 Application 코드와 분리해 Infrastructure 계층에서 처리하는 방식이다.

```text
Control Plane
  ├─ Configuration
  ├─ Routing
  ├─ Monitoring
  └─ Policy

Data Plane
  Service A ↔ Proxy ↔ Proxy ↔ Service B
```

### 담당 기능

- Service Discovery
- Routing과 Load Balancing
- 인증·인가와 암호화
- Retry, Timeout, Circuit Breaking
- Traffic 관측과 분산 추적

Service Discovery와 API Gateway가 Service Mesh와 관련은 있지만 같은 개념은 아니다. Gateway는 주로 외부 Client의 진입점을 담당하고, Service Mesh는 주로 내부 Service 간 통신을 관리한다.

---

## 15. MSA 표준 구성요소

| 구성요소 | 역할 |
| --- | --- |
| API Gateway / Service Router | 요청을 알맞은 Service로 Routing |
| Service Discovery | 실행 중인 Service의 위치 등록·조회 |
| Config Store | 공통 설정을 중앙 관리 |
| Runtime Platform | JRE 등 Service 실행 환경 제공 |
| Container Management | Container 배포·복구·확장 |
| Persistence | Service의 데이터 저장 |
| MOM | Message 기반 비동기 통신 |
| Telemetry | Log·Metric·Trace 수집과 시각화 |
| CI/CD Automation | Build·Test·배포 자동화 |
| Image Repository | Container Image 저장 |
| IaC | 코드로 Infrastructure 구성 |

`MOM`은 Message-Oriented Middleware를 의미하며, Publisher가 Message를 발행하고 Subscriber가 이를 소비하는 Pub/Sub 구조를 지원한다.

---

## 16. DevOps

DevOps는 Development와 Operations를 분리된 단계가 아니라 하나의 지속적인 흐름으로 연결하는 문화와 실천 방식이다.

```text
요구사항
  ↓
설계
  ↓
개발
  ↓
Test
  ↓
배포
  ↓
Monitoring
  ↓ Feedback
다음 요구사항과 개선
```

전통적인 방식에서는 분석부터 배포까지 긴 주기로 진행되어 고객의 변경 사항을 늦게 반영할 수 있다. DevOps와 Agile은 작은 변경 단위를 빠르게 개발하고 검증하며, 운영 결과를 다시 개발에 반영한다.

MSA에서는 Service를 담당하는 작은 Team이 기획, 개발, Test, 배포와 운영까지 Ownership을 가지는 구조가 중요하다. 흔히 작은 자율 조직을 `Two-Pizza Team`으로 설명한다.

---

## 17. CI/CD

### CI - Continuous Integration

개발자의 변경 사항을 Source Repository에 자주 통합하고 자동으로 Build와 Test를 수행한다.

```text
Developer Push
      ↓
Git Repository
      ↓
Build
      ↓
Automated Test
      ↓
Artifact 생성
```

### Continuous Delivery

실행 가능한 JAR나 Image를 배포 가능한 상태까지 자동으로 전달하지만, 운영 환경에 반영하는 최종 단계는 사람이 승인할 수 있다.

```text
Build → Test → Artifact 전달 → 수동 승인 → 운영 반영
```

### Continuous Deployment

검증을 통과한 변경을 운영 환경까지 자동으로 배포한다.

```text
Build → Test → Artifact 전달 → 운영 자동 배포
```

### Canary Deployment

새 버전을 일부 사용자에게 먼저 제공하고 문제가 없으면 Traffic을 단계적으로 확대한다.

```text
1단계  Old 95% / New 5%
2단계  Old 50% / New 50%
3단계  Old 0%  / New 100%
```

### Blue-Green Deployment

기존 환경과 새 환경을 동시에 준비하고 Routing만 새 환경으로 전환한다.

```text
Blue  : 현재 운영 Version
Green : 새 Version 배포·검증
             ↓
Traffic을 Green으로 전환
             ↓ 문제 발생
Traffic을 Blue로 Rollback
```

---

## 18. Container 가상화

### Traditional Deployment

```text
Hardware
└─ Operating System
   ├─ App A
   ├─ App B
   └─ App C
```

### Virtual Machine

```text
Hardware
└─ Host OS
   └─ Hypervisor
      ├─ Guest OS + App A
      └─ Guest OS + App B
```

VM마다 Guest OS가 필요하므로 격리 수준은 높지만 상대적으로 많은 Resource를 사용한다.

### Container

```text
Hardware
└─ Host OS
   └─ Container Runtime
      ├─ Container A + App A
      ├─ Container B + App B
      └─ Container C + App C
```

Container는 Host OS의 Kernel을 공유하면서 Process 실행 영역을 격리한다. 따라서 VM보다 가볍고 빠르게 시작할 수 있다.

```text
Dockerfile
   ↓ Build
Image
   ↓ Run
Container
```

- Image: Application과 실행에 필요한 File을 묶은 불변 Template
- Container: Image를 실행한 Instance
- Container Runtime: Container의 생성과 실행을 담당하는 환경

Java Application도 JRE/JDK 같은 Runtime이 필요하듯 Docker Image도 Base Image와 필요한 Library를 포함한다.

---

## 19. Twelve-Factor App과 확장 원칙

Cloud Native Application은 Code와 설정을 분리하고 환경 차이를 줄이며 자동 배포하기 쉬운 구조로 만들어야 한다.

| 원칙 | 의미 |
| --- | --- |
| Codebase | 하나의 Codebase를 Version Control로 관리 |
| Dependencies | 외부 의존성을 명시적으로 선언·격리 |
| Config | 설정을 Code와 분리 |
| Backing Services | DB·Queue 등을 교체 가능한 외부 Resource로 취급 |
| Build, Release, Run | Build·Release·실행 단계를 분리 |
| Processes | Stateless Process로 실행 |
| Port Binding | Service가 Port를 통해 기능 공개 |
| Concurrency | Process 확장으로 동시성 처리 |
| Disposability | 빠르게 시작하고 안전하게 종료 |
| Dev/Prod Parity | 개발·Test·운영 환경 차이를 최소화 |
| Logs | Log를 Event Stream으로 처리하고 외부에서 수집 |
| Admin Processes | 관리 작업을 일회성 Process로 실행 |

수업 자료에서는 Cloud Native 환경을 고려해 다음 세 가지를 더한 `12 Factors + 3`도 다뤘다.

- API First: 구현 전에 API Contract를 우선 정의한다.
- Telemetry: Log, Metric, Trace를 수집해 상태를 관측한다.
- Authentication and Authorization: 인증과 인가를 기본 설계에 포함한다.

---

## 20. RESTful API

RESTful API는 Resource를 URI로 표현하고 HTTP Method로 행위를 구분한다.

| HTTP Method | 역할 | 예시 |
| --- | --- | --- |
| `GET` | Resource 조회 | `GET /users/1` |
| `POST` | Resource 생성 | `POST /users` |
| `PUT` | Resource 전체 수정 | `PUT /users/1` |
| `PATCH` | Resource 일부 수정 | `PATCH /users/1` |
| `DELETE` | Resource 삭제 | `DELETE /users/1` |

메모에 적힌 `PUSH`가 아니라 `PUT`이 올바른 HTTP Method다.

### URI 설계 원칙

- 동사보다 명사를 사용한다.
- 단수보다 복수 Resource명을 사용한다.
- URI에 Password나 Token 같은 민감 정보를 넣지 않는다.
- 상태 코드를 일관성 있게 사용한다.

```text
권장     GET /users/1
비권장   GET /getUser?id=1
```

대표적인 상태 코드는 다음과 같다.

| 상태 코드 | 의미 |
| --- | --- |
| `200 OK` | 요청 성공 |
| `201 Created` | Resource 생성 성공 |
| `400 Bad Request` | 잘못된 요청 |
| `401 Unauthorized` | 인증 필요 또는 인증 실패 |
| `404 Not Found` | Resource 없음 |

---

## 21. Library와 Framework의 차이

핵심 차이는 실행 흐름의 주도권이 누구에게 있는가이다.

```text
Library
내 코드가 필요한 시점에 Library를 호출

Framework
Framework가 전체 실행 흐름을 관리하고
정해진 시점에 내 코드를 호출
```

Spring Framework에서는 Controller, Service, Repository 객체를 개발자가 직접 생성하고 호출하는 대신 Spring Container가 Bean의 생성과 의존성 주입, 요청 Mapping 등을 관리한다. 이를 제어의 역전, `IoC(Inversion of Control)`라고 한다.

---

## 22. Spring Cloud

Spring Cloud는 Spring Boot 기반 분산 시스템에서 반복되는 Architecture Pattern을 구현할 수 있도록 지원하는 Project 모음이다.

```text
Spring Boot
  └─ 개별 Microservice 개발과 실행

Spring Cloud
  └─ 분산 환경의 설정·검색·Routing·통신·장애 처리 지원
```

### 대표 Project와 역할

| 해결할 문제 | Spring Cloud 기술 |
| --- | --- |
| 중앙 설정 관리 | Spring Cloud Config |
| Service 등록·검색 | Spring Cloud Netflix Eureka, Consul |
| API Routing | Spring Cloud Gateway |
| 선언적 REST Client | OpenFeign |
| 장애 격리 | Spring Cloud Circuit Breaker |
| 설정 변경 Event 전파 | Spring Cloud Bus |
| Message 기반 통신 | Spring Cloud Stream |
| 계약 Test | Spring Cloud Contract |
| Kubernetes 연동 | Spring Cloud Kubernetes |

과거에는 Ribbon, Hystrix, Zuul이 Spring Cloud Netflix 구성에서 많이 사용되었다. 현재 Project를 구성할 때는 Spring Cloud LoadBalancer, Spring Cloud Circuit Breaker, Spring Cloud Gateway처럼 유지되는 대안을 확인해야 한다.

### Spring Cloud가 해결하려는 흐름

```text
1. Service가 시작된다.
2. 자신의 위치를 Discovery Server에 등록한다.
3. Client는 API Gateway에 요청한다.
4. Gateway는 Discovery 정보를 사용해 Service를 찾는다.
5. 여러 Instance 중 하나로 요청을 분산한다.
6. Service는 Config Server에서 환경 설정을 가져온다.
7. Log·Metric·Trace로 전체 호출 흐름을 관측한다.
8. 장애 발생 시 Timeout·Circuit Breaker로 전파를 제한한다.
```

Spring Cloud가 MSA의 모든 문제를 자동으로 해결하는 것은 아니다. Service 경계, 데이터 일관성, 장애 정책, 운영 자동화는 시스템 요구사항에 맞게 별도로 설계해야 한다.

---

## 23. Inner Architecture와 Outer Architecture

```text
Outer Architecture
├─ API Gateway
├─ Service Discovery
├─ Config Server
├─ Message Broker
├─ Monitoring
└─ CI/CD
        ↓ 지원
Inner Architecture
├─ Controller
├─ Service
├─ Repository
└─ Domain Model
```

- Inner Architecture: 개별 Microservice 내부의 실제 비즈니스 기능과 계층 구조
- Outer Architecture: 여러 Service가 함께 운영되도록 지원하는 분산 Infrastructure

좋은 MSA를 만들려면 개별 Service의 내부 구조뿐 아니라 Service 검색, 통신, 설정, 배포, 관측 같은 외부 구조도 함께 설계해야 한다.

---

## 24. 최종 정리

```text
Monolith의 변경·확장 단위가 커짐
        ↓
Business Capability 기준으로 Service 분리
        ↓
각 Service를 독립적으로 개발·배포·확장
        ↓
분산으로 인해 새 문제 발생
  ├─ 주소 변경
  ├─ 공통 설정 중복
  ├─ Network 장애
  ├─ 데이터 일관성
  └─ 운영 복잡도
        ↓
Spring Cloud와 Cloud Native 구성요소 적용
  ├─ Gateway
  ├─ Discovery
  ├─ Config
  ├─ REST · Message Broker
  ├─ Circuit Breaker
  ├─ Container
  └─ CI/CD · Telemetry
```

오늘 학습의 핵심은 `MSA = 작은 Spring Boot Project 여러 개`가 아니라는 점이다. MSA는 Business 경계를 기준으로 Service를 나누고 각 Service를 독립적으로 배포·확장하는 Architecture이며, 그 과정에서 발생하는 분산 시스템의 복잡성을 Spring Cloud와 Cloud Native 기술로 관리한다.

또한 MSA가 항상 정답인 것은 아니다. Service의 규모, 변경 속도, Team 구조, Traffic, 데이터 일관성 요구와 운영 역량을 고려해 Monolith와 MSA 중 적절한 방식을 선택해야 한다.
