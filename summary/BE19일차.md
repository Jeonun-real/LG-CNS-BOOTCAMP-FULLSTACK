# BE 19일차 - Spring Data JPA와 Entity 관계 매핑

## 1. 오늘 학습한 핵심

오늘은 기존 MyBatis 기반 프로젝트와 별도로 `inspire_jpa` 프로젝트를 만들고, Spring Data JPA가 객체를 중심으로 관계형 데이터베이스를 다루는 방식을 학습했다.

- JPA, Hibernate, ORM의 관계
- Entity를 이용한 테이블 매핑
- `ddl-auto`를 이용한 DDL 관리 방식
- Java의 camelCase와 DB의 snake_case 매핑
- Request DTO → Entity → Response DTO 변환
- `@Id`, `@GeneratedValue`, `@Column`, `@Table`
- `@OneToMany`, `@ManyToOne`, `@JoinColumn`, `mappedBy`
- `JpaRepository`를 상속한 CRUD 처리
- `Optional`, 람다식, `map()`, `orElseThrow()`
- Service의 `@Transactional`과 영속성 컨텍스트
- 사용자 회원가입 요청이 DB에 저장되고 DTO로 반환되는 흐름

이번 정리에서는 JWT 발급과 인증 필터는 제외하고 JPA의 기본 구조와 사용자 기능에 집중한다.

```text
Client
  ↓ HTTP 요청
UserController
  ↓ UserRequestDTO
UserService
  ↓ DTO → Entity
UserRepository
  ↓ Spring Data JPA
JPA 표준
  ↓
Hibernate
  ↓ SQL
MariaDB
  ↓ Entity
UserResponseDTO
  ↓ HTTP 응답
Client
```

---

## 2. JPA와 Hibernate

### 2.1 JPA

JPA(Java Persistence API)는 Java 객체와 관계형 데이터베이스 테이블을 매핑하기 위한 표준 명세다. JPA 자체가 SQL을 직접 실행하는 프로그램이라기보다, 영속성 관리를 어떤 방식으로 제공해야 하는지 정의한 규칙에 가깝다.

### 2.2 Hibernate

Hibernate는 JPA 명세를 실제로 구현한 대표적인 ORM Framework다.

```text
애플리케이션
  ↓ Entity와 Repository 사용
JPA 표준
  ↓ 실제 구현
Hibernate
  ↓ SQL 생성 및 JDBC 사용
MariaDB
```

ORM(Object Relational Mapping)은 객체와 관계형 테이블 사이의 차이를 Mapping으로 연결한다.

```text
Java Class       ↔ DB Table
Java Field       ↔ DB Column
Java Reference   ↔ Foreign Key
Java Object      ↔ Table Row
```

프로젝트에서는 다음 Starter로 Spring Data JPA와 Hibernate를 사용한다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
```

---

## 3. `ddl-auto`와 테이블 생성 전략

Hibernate는 Entity 정보를 이용해 DDL을 생성하거나 기존 Schema를 검사할 수 있다.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none
```

| 설정 | 의미 |
| --- | --- |
| `create` | 실행할 때 기존 테이블을 제거하고 다시 생성 |
| `create-drop` | 실행 시 생성하고 애플리케이션 종료 시 제거 |
| `update` | Entity와 비교하여 부족한 Schema를 갱신 |
| `validate` | Entity와 Schema가 일치하는지만 검사 |
| `none` | Hibernate가 DDL을 생성하거나 변경하지 않음 |

현재 프로젝트의 `ddl-auto: none`은 Hibernate가 테이블을 자동 생성하지 않는다는 뜻이다. 따라서 Flyway 또는 직접 작성한 DDL로 테이블을 준비해야 한다.

```text
ddl-auto = create/update
Entity → Hibernate가 DDL 생성·변경

ddl-auto = none
Entity → SQL 실행에만 사용
테이블 → Flyway 또는 수동 DDL로 별도 생성
```

개발 중에는 `update`가 편리할 수 있지만, 운영 환경에서 의도하지 않은 Schema 변경이 발생할 수 있다. 운영에서는 보통 `validate` 또는 `none`과 Migration 도구를 사용한다.

---

## 4. camelCase와 snake_case

Java 필드는 일반적으로 camelCase를 사용하고 DB 컬럼은 snake_case를 많이 사용한다.

```text
Java               Database
blogId      →      blog_id
commentId   →      comment_id
createdAt   →      created_at
```

물리 Naming Strategy를 설정하면 Entity 필드와 실제 컬럼 이름을 변환하는 규칙을 지정할 수 있다.

```yaml
spring:
  jpa:
    hibernate:
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

또는 필드에 컬럼명을 명시하면 Mapping이 더욱 분명해진다.

```java
@Column(name = "blog_id")
private Integer blogId;
```

중요한 것은 Java 필드명, Naming Strategy, 실제 DB 컬럼명이 서로 일치해야 한다는 점이다.

```text
Entity의 blogId
  ↓ Naming Strategy
DB의 blog_id
```

JPQL에서는 DB 컬럼명이 아니라 Entity 필드명을 사용한다.

```java
// JPQL에서는 b.blog_id가 아니라 b.blogId
WHERE b.blogId = :blogId
```

---

## 5. Entity의 역할

Entity는 Hibernate에 테이블 구조와 관계 정보를 전달하고, 영속성 컨텍스트에서 관리되는 객체다.

### 5.1 `UserEntity`

```java
@Entity
@Table(name = "SPRING_JPA_USER_TBL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    private String password;
    private String name;

    // 인가에 사용할 선택 권한: USER, ADMIN 등
    private String role;

    @OneToMany(mappedBy = "author", orphanRemoval = false)
    private List<BlogEntity> blogs = new ArrayList<>();
}
```

주요 Annotation의 의미는 다음과 같다.

| Annotation | 역할 |
| --- | --- |
| `@Entity` | 해당 클래스를 JPA Entity로 등록 |
| `@Table` | Entity가 연결될 테이블명 지정 |
| `@Id` | 기본키 필드 지정 |
| `@Column` | 컬럼명, 길이, NULL 허용 여부 등 지정 |

`email`은 사용자 테이블의 기본키다. `role`은 인증된 사용자가 어떤 기능을 수행할 수 있는지 판단하는 인가 권한으로 사용할 수 있다.

```text
USER  → 일반 사용자 권한
ADMIN → 관리자 권한
```

현재 `role`에는 필수 제약이 없으므로 선택값이다. 기본 권한이 필요하다면 DTO 변환 또는 Entity 생성 시 명시적으로 값을 넣어야 한다.

### 5.2 자동 증가 기본키

게시글과 댓글은 정수형 기본키를 자동 증가시킨다.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer blogId;
```

`GenerationType.IDENTITY`는 DB의 `AUTO_INCREMENT` 같은 기능으로 PK 생성을 위임한다.

```text
INSERT 전: blogId = null
INSERT 실행
DB가 PK 생성
INSERT 후: Entity에 생성된 blogId 반영
```

---

## 6. Entity 관계 설정

현재 프로젝트의 관계는 다음과 같다.

```text
UserEntity 1 ───── N BlogEntity 1 ───── N CommentEntity
            email                blog_id
```

### 6.1 사용자와 게시글

사용자 한 명은 여러 게시글을 작성할 수 있다.

```java
// UserEntity
@OneToMany(mappedBy = "author", orphanRemoval = false)
private List<BlogEntity> blogs = new ArrayList<>();
```

게시글 여러 개는 각각 한 명의 작성자를 참조한다.

```java
// BlogEntity
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "email")
private UserEntity author;
```

```text
SPRING_JPA_USER_TBL.email(PK)
               1
               │
               N
SPRING_JPA_BLOG_TBL.email(FK)
```

`@JoinColumn(name = "email")`이 붙은 `BlogEntity.author`가 FK를 관리하는 연관관계의 주인이다. `UserEntity.blogs`의 `mappedBy = "author"`는 상대 Entity의 `author` 필드가 관계를 관리한다는 뜻이다.

### 6.2 게시글과 댓글

게시글 하나에는 댓글이 여러 개 존재할 수 있다.

```java
// BlogEntity
@OneToMany(mappedBy = "blog", orphanRemoval = false)
private List<CommentEntity> comments = new ArrayList<>();
```

각 댓글은 하나의 게시글을 참조한다.

```java
// CommentEntity
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "blogId")
private BlogEntity blog;
```

```text
SPRING_JPA_BLOG_TBL.blog_id(PK)
                1
                │
                N
SPRING_JPA_COMMENT_TBL.blog_id(FK)
```

`FetchType.LAZY`는 연관 객체를 처음부터 모두 조회하지 않고 실제로 사용할 때 조회하도록 지연시키는 전략이다.

---

## 7. DTO와 Entity를 분리하는 이유

JPA는 Entity를 기준으로 영속성을 관리하지만, Client의 요청과 응답에 Entity를 직접 사용하는 것은 피하는 편이 좋다.

```text
요청 JSON
  ↓
Request DTO
  ↓ toEntity()
Entity
  ↓ Repository
Database
  ↓ 조회 결과 Entity
Response DTO
  ↓ 응답 JSON
Client
```

DTO를 분리하면 다음 장점이 있다.

- API에 필요한 데이터만 입력·출력할 수 있다.
- Entity 구조가 API에 그대로 노출되는 것을 막는다.
- Request 유효성 검증을 별도로 적용할 수 있다.
- 연관관계의 순환 참조와 불필요한 Lazy Loading을 줄일 수 있다.
- DB 구조와 API 응답 구조를 독립적으로 변경하기 쉽다.

### 7.1 Request DTO → Entity

```java
public static UserEntity toEntity(UserRequestDTO request) {
    return UserEntity.builder()
        .email(request.getEmail())
        .password(request.getPassword())
        .name(request.getName())
        .role(request.getRole())
        .build();
}
```

`UserRequestDTO`는 Client가 보낸 데이터를 받고, `toEntity()`는 DB에 저장할 `UserEntity`를 반환한다.

### 7.2 Entity → Response DTO

```java
public static UserResponseDTO fromEntity(UserEntity entity) {
    return UserResponseDTO.builder()
        .email(entity.getEmail())
        .password(entity.getPassword())
        .name(entity.getName())
        .role(entity.getRole())
        .build();
}
```

`UserResponseDTO`는 Repository가 반환한 Entity를 받아 Client에 반환할 객체를 만든다.

실제 서비스에서는 비밀번호를 응답 DTO에 포함하지 않는 것이 안전하다.

```java
public class UserResponseDTO {
    private String email;
    private String name;
    private String role;
}
```

---

## 8. Spring Data JPA Repository

Repository는 구현 클래스가 아니라 Interface로 작성한다.

```java
public interface UserRepository
        extends JpaRepository<UserEntity, String> {
}
```

Generic 타입의 의미는 다음과 같다.

```text
JpaRepository<Entity 타입, 기본키 타입>
              UserEntity   String(email)
```

Interface만 선언해도 Spring Data JPA가 실행 시점에 구현 객체를 만들고 Spring Bean으로 관리한다.

기본 제공 CRUD Method는 다음과 같다.

| Method | 역할 |
| --- | --- |
| `save(entity)` | Entity 저장 또는 병합 |
| `findAll()` | 전체 조회 |
| `findById(id)` | 기본키로 단건 조회 |
| `existsById(id)` | 기본키 존재 여부 확인 |
| `deleteById(id)` | 기본키로 삭제 |
| `count()` | 전체 행 개수 조회 |

### 8.1 Query Method

Method 이름을 규칙에 맞게 작성하면 Spring Data JPA가 Query를 생성한다.

```java
Optional<UserEntity> findByEmailAndPassword(
    String email,
    String password
);
```

개념적으로 다음 조건을 표현한다.

```sql
SELECT *
FROM SPRING_JPA_USER_TBL
WHERE email = ?
  AND password = ?;
```

---

## 9. `save()`와 영속성 관리

`save()`는 Entity의 신규 여부에 따라 내부적으로 `persist()` 또는 `merge()`를 사용한다.

```text
새 Entity
  ↓ save()
persist 중심
  ↓
INSERT

기존 Entity
  ↓ save() 또는 Dirty Checking
merge 또는 변경 감지
  ↓
UPDATE
```

다만 `save()`가 언제나 먼저 SELECT한 뒤 INSERT와 UPDATE를 결정한다고 단정하면 정확하지 않다.

- 자동 생성 PK가 `null`인 새 Entity는 일반적으로 신규 객체로 판단한다.
- 직접 할당한 PK는 신규 여부 판단과 `merge()` 과정에서 SELECT가 발생할 수 있다.
- 트랜잭션 안에서 조회한 Entity는 영속 상태이므로 값을 변경하면 Dirty Checking으로 UPDATE될 수 있다.

```java
@Transactional
public void updateName(String email, String name) {
    UserEntity user = userRepository.findById(email)
        .orElseThrow();

    user.updateName(name);
    // 영속 Entity의 변경을 감지하여 Transaction 종료 시 UPDATE
}
```

즉, JPA의 핵심은 단순히 `save()`가 SQL을 대신 실행하는 것이 아니라 Entity의 상태를 영속성 컨텍스트에서 관리한다는 점이다.

---

## 10. `@Transactional`과 Service

Service는 Controller와 Repository 사이에서 업무 흐름을 구성한다.

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO signUp(UserRequestDTO request) {
        UserEntity entity = UserRequestDTO.toEntity(request);
        UserEntity saved = userRepository.save(entity);
        return UserResponseDTO.fromEntity(saved);
    }
}
```

`@Transactional`이 붙은 Method는 하나의 Transaction 범위에서 실행된다.

```text
Transaction 시작
  ↓
DTO → Entity
  ↓
Repository.save()
  ↓
정상 종료 → COMMIT
예외 발생 → ROLLBACK
```

조회 전용 Method에는 다음처럼 표시할 수 있다.

```java
@Transactional(readOnly = true)
public List<UserResponseDTO> findAll() {
    // 조회 로직
}
```

Service의 반환 타입을 Response DTO로 두면 Controller가 Entity를 직접 다루거나 외부에 노출하지 않아도 된다.

---

## 11. 사용자 회원가입 전체 흐름

### 11.1 Controller

```java
@PostMapping("/signUp")
public ResponseEntity<?> signUp(
        @RequestBody @Valid UserRequestDTO request,
        BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
        Map<String, String> errMap = new HashMap<>();

        bindingResult.getAllErrors().forEach(error -> {
            FieldError field = (FieldError) error;
            errMap.put(field.getField(), error.getDefaultMessage());
        });

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errMap);
    }

    UserResponseDTO response = userService.signUp(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
}
```

요청 JSON은 `@RequestBody`를 통해 `UserRequestDTO`로 역직렬화되고, `@Valid`가 DTO의 검증 Annotation을 실행한다.

```java
@Email(message = "이메일 형식을 지켜주세요!!!")
private String email;

@NotBlank(message = "비밀번호는 필수입력 항목입니다.")
@Size(min = 4, max = 20)
private String password;

@NotBlank(message = "이름은 필수입력 항목입니다.")
private String name;
```

### 11.2 Service와 Repository

```java
@Transactional
public UserResponseDTO signUp(UserRequestDTO request) {
    UserEntity entity = UserRequestDTO.toEntity(request);

    return UserResponseDTO.fromEntity(
        userRepository.save(entity)
    );
}
```

전체 데이터 이동 과정은 다음과 같다.

```text
POST /users/signUp
  ↓ JSON
{
  "email": "user@test.com",
  "password": "1234",
  "name": "사용자"
}
  ↓ @RequestBody
UserRequestDTO
  ↓ @Valid
유효성 검사
  ↓ toEntity()
UserEntity
  ↓ userRepository.save()
Hibernate가 INSERT 실행
  ↓ 저장된 UserEntity
UserResponseDTO.fromEntity()
  ↓
201 Created + JSON 응답
```

---

## 12. Optional과 람다식으로 구성하는 CRUD

`findById()`는 결과가 없을 수 있으므로 `Optional<Entity>`를 반환한다.

### 12.1 기본키로 단건 조회

```java
@Transactional(readOnly = true)
public UserResponseDTO findByEmail(String email) {
    return userRepository.findById(email)
        .map(UserResponseDTO::fromEntity)
        .orElseThrow(() ->
            new RuntimeException("USER NOT FOUND")
        );
}
```

```text
findById(email)
  ↓
Optional<UserEntity>
  ├─ 값 존재 → map() → UserResponseDTO
  └─ 값 없음 → orElseThrow() → 예외
```

### 12.2 전체 조회 후 DTO Mapping

```java
@Transactional(readOnly = true)
public List<UserResponseDTO> findAll() {
    return userRepository.findAll()
        .stream()
        .map(UserResponseDTO::fromEntity)
        .toList();
}
```

```text
List<UserEntity>
  ↓ stream()
각 Entity를 Response DTO로 변환
  ↓ toList()
List<UserResponseDTO>
```

### 12.3 중복 회원 검사 후 저장

```java
@Transactional
public UserResponseDTO signUp(UserRequestDTO request) {
    return Optional.of(request)
        .filter(req -> !userRepository.existsById(req.getEmail()))
        .map(UserRequestDTO::toEntity)
        .map(userRepository::save)
        .map(UserResponseDTO::fromEntity)
        .orElseThrow(() ->
            new RuntimeException("이미 존재하는 사용자입니다.")
        );
}
```

이 방식은 다음 과정을 하나의 흐름으로 표현한다.

```text
Request DTO
  ↓ email 중복 검사
Request DTO → UserEntity
  ↓ save
저장된 UserEntity
  ↓ fromEntity
UserResponseDTO 반환
```

---

## 13. REST API와 Repository 동작 연결

사용자 CRUD를 REST API로 확장하면 다음처럼 연결할 수 있다.

| HTTP 요청 | Controller | Repository | 반환 |
| --- | --- | --- | --- |
| `GET /users` | 사용자 전체 조회 | `findAll()` | `List<UserResponseDTO>` |
| `GET /users/{email}` | 사용자 단건 조회 | `findById(email)` | `UserResponseDTO` |
| `POST /users` | 사용자 생성 | `save(entity)` | 생성된 `UserResponseDTO` |
| `DELETE /users/{email}` | 사용자 삭제 | `deleteById(email)` | `204 No Content` |

현재 프로젝트의 회원가입 API는 다음 경로를 사용한다.

```text
POST /users/signUp
```

`GET /users`, `GET /users/{email}` 예시는 Repository의 기본 CRUD Method가 Controller와 Service에 어떻게 연결되는지를 설명하기 위한 확장 형태다.

---

## 14. 오늘의 핵심 정리

```text
JPA
→ 객체와 관계형 DB Mapping을 위한 표준

Hibernate
→ JPA를 구현하고 SQL과 영속성 관리를 담당하는 ORM Framework

Entity
→ 테이블·컬럼·PK·연관관계 정보를 표현하고 영속성 컨텍스트에서 관리

Request DTO
→ Client 요청을 받고 Entity로 변환

Response DTO
→ Entity를 받아 API 응답 형태로 변환

JpaRepository<Entity, ID>
→ Interface 상속만으로 기본 CRUD 제공

@Transactional
→ Service 로직을 하나의 Transaction으로 관리

@OneToMany ↔ @ManyToOne
→ 객체 참조와 DB Foreign Key 관계를 연결

Optional + Lambda
→ 값의 존재·부재에 따른 변환과 예외 흐름을 표현
```

오늘 구현한 구조의 중심은 다음 한 문장으로 정리할 수 있다.

> Controller가 Request DTO를 받고, Service가 DTO를 Entity로 변환해 Repository에 전달하며, Hibernate가 Entity를 영속화한 뒤 Response DTO로 변환해 반환한다.
