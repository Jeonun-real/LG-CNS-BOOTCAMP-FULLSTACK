# BE 20일차 - JWT 인증과 Spring Data JPA CRUD

## 1. 오늘 학습한 핵심

오늘은 `inspire_jpa` 프로젝트에서 JWT 인증 흐름을 구성하고, 사용자 기능 이후의 게시글·댓글 기능을 Spring Data JPA 방식으로 연결했다.

- MyBatis와 JPA의 데이터 접근 방식 비교
- JPA, Hibernate, Entity, Flyway의 역할
- Tomcat과 Spring MVC의 요청 처리 흐름
- DispatcherServlet 앞에서 동작하는 Filter
- JWT의 구조와 서명 검증
- Access Token과 Refresh Token의 역할
- `@Value`를 이용한 Secret Key 주입
- White List와 CORS Preflight 처리
- 게시글 목록·작성·상세 조회
- 댓글 작성·삭제·수정
- `JpaRepository`의 기본 CRUD
- `Optional`, Stream, Method Reference
- JPQL과 Fetch Join
- `@Transactional`과 Dirty Checking

전체 요청 흐름은 다음과 같다.

```text
React
  ↓ HTTP Request + Authorization Header
Tomcat
  ↓
JwtFilter
  ├─ 공개 요청 → 통과
  └─ 보호 요청 → JWT 검증
          ↓ 성공
DispatcherServlet
  ↓ HandlerMapping
Controller
  ↓ Request DTO
Service
  ↓ Entity 변환 및 Transaction
JpaRepository
  ↓
Hibernate
  ↓ SQL
MariaDB
  ↓ Entity
Response DTO
  ↓ ResponseEntity
React State 변경 및 재렌더링
```

---

## 2. MyBatis와 JPA 비교

### 2.1 MyBatis

MyBatis는 SQL 중심의 SQL Mapper Framework다. 개발자가 SQL을 직접 작성하고 Mapper Interface와 Mapper XML을 연결한다.

```text
Service
  ↓ Mapper Interface Method 호출
Mapper Interface
  ↓ namespace + statement id 연결
Mapper XML
  ↓ 직접 작성한 SQL
Database
  ↓ 조회 결과 Mapping
DTO
```

```java
// Mapper Interface: Java에서 사용할 데이터 접근 명세
public interface BlogMapper {
    List<BlogResponseDTO> findByAll();
    Optional<BlogResponseDTO> findById(Integer id);
    int insert(BlogRequestDTO request);
}
```

```xml
<!-- Mapper XML: Interface Method와 연결되는 실제 SQL -->
<select id="findById"
        parameterType="int"
        resultType="BlogResponseDTO">
    SELECT  id, title, content, category, email
    FROM    blogs
    WHERE   id = #{id}
</select>
```

MyBatis에서는 다음 항목이 정확히 일치해야 한다.

```text
Mapper XML namespace ↔ Mapper Interface 전체 경로
SQL statement id     ↔ Interface Method 이름
#{property}          ↔ DTO·매개변수 Property 이름
조회 Column          ↔ 결과 DTO Property
```

### 2.2 JPA와 Hibernate

JPA는 객체와 관계형 DB를 Mapping하는 표준 명세이고, Hibernate는 이를 구현한 대표적인 ORM Framework다.

```text
Service
  ↓ Repository Method 호출
JpaRepository
  ↓ Entity Mapping 분석
Hibernate
  ↓ SQL 생성
Database
  ↓
Entity
```

```java
// 구현 클래스를 직접 만들지 않아도 기본 CRUD가 제공된다.
public interface BlogRepository
        extends JpaRepository<BlogEntity, Integer> {
}
```

Hibernate는 `BlogEntity`의 Annotation과 필드를 보고 Table, PK, Column, FK 관계를 파악한다.

```java
@Entity
@Table(name = "SPRING_JPA_BLOG_TBL")
public class BlogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer blogId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "email")
    private UserEntity author;
}
```

### 2.3 핵심 차이

| 구분 | MyBatis | JPA |
| --- | --- | --- |
| 중심 | SQL | Entity와 객체 관계 |
| SQL | 개발자가 직접 작성 | Hibernate가 Mapping을 바탕으로 생성 |
| 명세 | Mapper Interface + XML | Repository Interface + Entity |
| 결과 처리 | SQL 결과를 DTO 등에 Mapping | Entity를 영속성 컨텍스트에서 관리 |
| 복잡한 SQL | 직접 제어하기 쉬움 | JPQL, Query Method, Native Query 사용 |
| 수정 | UPDATE SQL 직접 작성 | 영속 Entity의 Dirty Checking 가능 |

둘의 차이는 단순히 SQL 자동 작성 여부만은 아니다. MyBatis는 SQL 실행과 결과 Mapping이 중심이고, JPA는 Entity의 상태와 관계를 영속성 컨텍스트에서 관리하는 것이 중심이다.

---

## 3. Hibernate DDL과 Flyway

Hibernate는 Entity를 보고 DDL까지 생성할 수 있다.

```yaml
spring:
  jpa:
    hibernate:
      # create, create-drop, update, validate, none
      ddl-auto: none
```

| 값 | 동작 |
| --- | --- |
| `create` | 서버 시작 시 기존 Table을 제거하고 생성 |
| `create-drop` | 시작 시 생성하고 종료 시 제거 |
| `update` | Entity에 맞춰 Schema 갱신 |
| `validate` | Entity와 Schema 일치 여부 검사 |
| `none` | Hibernate가 DDL을 변경하지 않음 |

학습 환경에서는 Entity를 기반으로 테이블을 자동 생성하면 편리하지만, 운영 환경에서는 의도하지 않은 Schema 변경을 피해야 한다. 이 때문에 Flyway 같은 Migration 도구로 SQL과 변경 이력을 관리한다.

```text
서버 시작
  ↓
Flyway가 Schema History 확인
  ↓
현재 적용 Version 확인
  ↓
적용되지 않은 V1, V2, V3... SQL 실행
  ↓
Schema Version 기록
```

```text
V1__create_users.sql
V2__create_blogs.sql
V3__create_comments.sql
```

Flyway는 SQL을 없애는 도구가 아니라 **직접 작성한 DDL을 순서와 Version에 따라 안전하게 적용하고 이력을 관리하는 도구**다.

---

## 4. Tomcat과 Spring MVC 요청 처리 구조

Spring Boot Web Application에서는 내장 Tomcat이 HTTP 요청을 받는다.

```text
Browser
  ↓ HTTP
Tomcat Web Server
  ↓
Catalina Servlet Container
  ↓
Filter Chain
  ↓
DispatcherServlet
  ↓
Controller
```

- **Tomcat**: Web Server와 Servlet Container 역할
- **Catalina**: Tomcat의 Servlet Container
- **Jasper**: JSP를 Servlet Java Code로 변환하고 Compile하는 JSP Engine
- **DispatcherServlet**: Spring MVC의 Front Controller

JSP를 사용하는 전통적인 흐름은 다음과 같다.

```text
Browser
→ Tomcat
→ JSP 요청
→ Jasper가 JSP를 Servlet으로 변환·Compile
→ Catalina가 Servlet 실행
→ HTML 응답
```

현재 React + REST API 구조는 JSP를 Rendering하지 않고 JSON을 반환하므로 Jasper가 핵심 흐름에 포함되지는 않는다.

```text
React
→ Tomcat
→ Filter
→ DispatcherServlet
→ @RestController
→ JSON 응답
```

Spring Container는 Controller, Service, Repository 등의 Bean을 관리한다. DispatcherServlet은 HandlerMapping을 이용해 요청 URL과 HTTP Method에 맞는 Controller Method를 찾는다.

---

## 5. Filter, Interceptor, AOP의 위치

인증이 필요한 모든 Controller에서 JWT를 반복 검사하면 중복 코드가 많아진다.

```java
// 각 Controller에서 이 검사를 반복하는 것은 비효율적이다.
if (token == null || !isValid(token)) {
    return unauthorized;
}
```

공통 인증 검사는 DispatcherServlet 앞의 Filter에서 처리할 수 있다.

```text
Client
  ↓
Filter
  ↓
DispatcherServlet
  ↓
Interceptor
  ↓
Controller
  ↓
Service Proxy / AOP
```

| 구분 | 주요 위치와 용도 |
| --- | --- |
| Filter | Servlet Container 영역, DispatcherServlet 진입 전 요청·응답 처리 |
| Interceptor | Spring MVC 영역, Controller 실행 전후 처리 |
| AOP | Spring Bean Method 실행 전후의 공통 관심사 처리 |

JWT 인증은 요청이 Controller에 도달하기 전에 차단할 수 있어 Filter와 잘 맞는다.

---

## 6. JWT의 구조

JWT(JSON Web Token)는 다음 세 부분으로 구성된다.

```text
Header.Payload.Signature
```

```text
Header
→ Token Type과 서명 Algorithm

Payload
→ subject, 발급 시간, 만료 시간, 권한 등의 Claims

Signature
→ Header와 Payload가 변경되지 않았는지 검증
```

중요한 점은 일반적인 JWT Payload가 **암호화된 것이 아니라 Base64URL로 Encoding된 것**이라는 점이다. 누구나 Decode하여 내용을 읽을 수 있으므로 비밀번호나 Secret 같은 민감정보를 넣으면 안 된다.

```text
Encoding ≠ Encryption
Signature 검증 ≠ Payload 비공개
```

현재 프로젝트는 Secret Key를 사용하는 HMAC 방식으로 서명한다.

```text
Header + Payload
  ↓ Secret Key로 서명
Signature 생성
  ↓
Token 위·변조 여부 검증
```

---

## 7. Access Token과 Refresh Token

로그인이 성공하면 두 종류의 Token을 발급할 수 있다.

| Token | 역할 | 예시 유효시간 |
| --- | --- | --- |
| Access Token(AT) | API 요청 인증에 사용 | 30분 |
| Refresh Token(RT) | AT 재발급에 사용 | 7일 |

```text
로그인 성공
  ↓
AT 발급: 짧은 유효시간
RT 발급: 긴 유효시간
  ↓
Client가 AT로 API 요청
  ↓ AT 만료
RT 검증
  ↓
새 AT 발급
```

AT의 수명을 짧게 두는 이유는 탈취되었을 때 사용할 수 있는 시간을 줄이기 위해서다. RT는 더 오래 유지되므로 Client에만 맡기지 않고 Redis에 저장해 다음 정보를 관리할 수 있다.

```text
사용자별 Refresh Token
Token 만료시간
Logout 또는 강제 만료 상태
재발급 가능 여부
```

Redis를 Docker Container로 실행할 수 있지만 Docker는 가상화된 실행 환경을 제공하는 도구이고, Token 관리 자체는 Redis가 담당한다.

---

## 8. `@Value`로 Secret Key 주입

Secret Key는 Source Code에 직접 작성하지 않고 설정값으로 관리한다.

```yaml
jwt:
  # 실제 값은 .env의 JWT_SECRET_KEY에서 읽는다.
  secret: ${JWT_SECRET_KEY}
```

```java
@Component
public class JwtProvider {

    // application.yml의 jwt.secret 값을 주입
    @Value("${jwt.secret}")
    private String secret;

    // 밀리초 단위: 30분
    private final long ACCESS_TOKEN_EXPIRY
        = 1000L * 60 * 30;

    // 밀리초 단위: 7일
    private final long REFRESH_TOKEN_EXPIRY
        = 1000L * 60 * 60 * 24 * 7;

    private Key getSecretKey() {
        // 문자열 Secret을 HMAC 서명용 Key로 변환
        return Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}
```

`@Value`는 Spring Environment에서 설정값을 찾아 Field에 주입한다.

```text
.env
  ↓
application.yml의 ${JWT_SECRET_KEY}
  ↓
@Value("${jwt.secret}")
  ↓
JwtProvider.secret
```

Secret Key를 Console에 출력하면 안 된다. 로그가 노출되면 공격자가 유효한 Token을 임의로 만들 수 있기 때문이다.

---

## 9. JWT 생성 코드

### Access Token 생성

```java
public String createAT(String email) {
    return Jwts.builder()
        // Token의 주체: 로그인 사용자 email
        .setSubject(email)

        // Token 발급 시각
        .setIssuedAt(new Date())

        // 현재 시각에서 30분 뒤 만료
        .setExpiration(
            new Date(
                System.currentTimeMillis()
                + ACCESS_TOKEN_EXPIRY
            )
        )

        // Secret Key로 Token 서명
        .signWith(getSecretKey())

        // Header.Payload.Signature 문자열 완성
        .compact();
}
```

### Refresh Token 생성

```java
public String createRT(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())

        // 현재 시각에서 7일 뒤 만료
        .setExpiration(
            new Date(
                System.currentTimeMillis()
                + REFRESH_TOKEN_EXPIRY
            )
        )

        .signWith(getSecretKey())
        .compact();
}
```

현재 사용하는 JJWT `0.11.5`에서는 `setSubject()`, `setIssuedAt()`, `setExpiration()`, `parserBuilder()` 문법을 사용할 수 있다.

---

## 10. JwtFilter 초기화

Filter는 JWT를 검증할 Key를 미리 만들어 둔다.

```java
@Component
public class JwtFilter implements Filter {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    private void init() {
        // Dependency 주입이 끝난 뒤 한 번 실행
        this.key = Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}
```

`@PostConstruct`는 객체 생성과 Dependency 주입이 완료된 뒤 실행된다. 따라서 `@Value`로 주입된 `secret`을 이용해 검증 Key를 만들 수 있다.

---

## 11. White List

회원가입, 로그인, Swagger 문서는 Token 없이 접근해야 한다.

```java
private static final List<String> WHITE_LIST = List.of(
    "/users/**",
    "/swagger-ui/**",
    "/v3/api-docs/**"
);
```

```java
private final AntPathMatcher matcher = new AntPathMatcher();

public boolean isPath(String path) {
    return WHITE_LIST.stream()

        // 하나의 Pattern이라도 요청 경로와 일치하면 true
        .anyMatch(pattern ->
            matcher.match(pattern, path)
        );
}
```

```java
if (isPath(endPoint)) {
    // 인증이 필요 없는 요청은 다음 Filter로 전달
    chain.doFilter(request, response);
    return;
}
```

`chain.doFilter()`를 호출해야 다음 Filter 또는 DispatcherServlet로 요청이 이동한다. 호출하지 않고 반환하면 요청 흐름이 그 자리에서 종료된다.

---

## 12. CORS Preflight 처리

React가 다른 Origin의 Spring Boot Server에 요청하면 Browser가 실제 요청 전에 `OPTIONS` Preflight 요청을 보낼 수 있다.

```java
if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
    // Preflight 요청에 허용할 Origin과 Method, Header 전달
    res.setStatus(HttpServletResponse.SC_OK);
    res.setHeader(
        "Access-Control-Allow-Origin",
        "http://localhost:3000"
    );
    res.setHeader(
        "Access-Control-Allow-Methods",
        "GET, POST, PUT, PATCH, DELETE, OPTIONS"
    );
    res.setHeader(
        "Access-Control-Allow-Headers",
        "Authorization, Content-Type, Refresh-Token"
    );

    chain.doFilter(request, response);
    return;
}
```

Preflight 요청에는 일반적으로 JWT가 없으므로 Token 검증보다 먼저 처리해야 한다.

현재 프로젝트에는 `CorsConfig`도 존재한다. 실제 프로젝트에서는 Filter와 MVC 설정에 CORS 책임을 중복시키기보다 한 곳에서 일관되게 관리하는 편이 좋다.

---

## 13. Authorization Header에서 Token 꺼내기

보호된 API 요청은 Header에 Access Token을 포함한다.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Filter에서는 Header 존재 여부와 `Bearer ` 접두사를 확인한다.

```java
String header = req.getHeader("Authorization");

if (header == null || !header.startsWith("Bearer ")) {
    // Header가 없거나 형식이 다르면 401
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}

// "Bearer " 일곱 글자를 제거하고 JWT 문자열만 추출
String token = header.substring(7);
```

---

## 14. JWT 검증 후 Controller로 이동

현재 프로젝트는 JJWT `0.11.5` 구문으로 Token을 검증한다.

```java
try {
    Jwts.parserBuilder()

        // Token 생성 때 사용한 것과 같은 Key로 서명 검증
        .setSigningKey(key)

        // JWT Parser 완성
        .build()

        // 서명, 만료시간, Token 형식을 검증
        .parseClaimsJws(token);

    // 검증 성공: DispatcherServlet과 Controller로 이동
    chain.doFilter(request, response);

} catch (Exception e) {
    // 검증 실패: 요청 중단
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
}
```

검증 과정에서는 다음을 확인한다.

```text
Token 형식이 정상인가?
서명이 같은 Secret Key로 만들어졌는가?
Payload가 변조되지 않았는가?
만료시간이 지나지 않았는가?
```

`chain.doFilter()`까지 넓은 `try-catch`로 감싸면 Controller나 DB에서 발생한 예외도 JWT 검증 실패로 오해할 수 있다. Token Parsing만 `try-catch` 안에 두고, 검증 성공 후 `chain.doFilter()`를 호출하는 구조가 더 명확하다.

---

## 15. 게시글 목록 조회

### Controller

```java
@GetMapping("/index")
public ResponseEntity<?> index() {
    // Service는 Entity가 아니라 Response DTO 목록을 반환
    List<BlogResponseDTO> list = blogService.list();

    // 빈 배열도 정상적인 조회 결과이므로 200으로 반환
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(list);
}
```

### Service

```java
@Transactional(readOnly = true)
public List<BlogResponseDTO> list() {
    return blogRepository.findAll()       // List<BlogEntity>
        .stream()                         // Stream<BlogEntity>
        .map(BlogResponseDTO::fromEntity) // Entity → DTO
        .toList();                        // List<BlogResponseDTO>
}
```

```text
GET /blogs/index
→ JwtFilter에서 AT 검증
→ BlogController.index()
→ BlogService.list()
→ blogRepository.findAll()
→ Hibernate SELECT
→ List<BlogEntity>
→ List<BlogResponseDTO>
→ 200 OK
```

---

## 16. 게시글 작성

게시글은 작성자와 N:1 관계이므로, 게시글 Entity를 만들기 전에 작성자 `UserEntity`를 조회한다.

### Request DTO → Entity

```java
public BlogEntity toEntity(UserEntity user) {
    return BlogEntity.builder()
        .title(this.title)
        .content(this.content)
        .category(this.category)

        // email 문자열만 넣는 대신 조회한 UserEntity와 관계 설정
        .author(user)
        .build();
}
```

### Service

```java
@Transactional
public BlogResponseDTO insert(BlogRequestDTO request) {
    return userRepository

        // 1. 요청 email을 PK로 사용자 조회
        .findById(request.getEmail())

        // 2. 사용자가 있으면 게시글 Entity 생성 및 저장
        .map(user -> {
            BlogEntity blog = blogRepository.save(
                request.toEntity(user)
            );

            // 3. 저장된 Entity를 Response DTO로 변환
            return BlogResponseDTO.fromEntity(blog);
        })

        // 4. 작성자가 DB에 없으면 저장하지 않고 예외
        .orElseThrow(() ->
            new RuntimeException("Blog Insert Fail!!")
        );
}
```

```text
POST /blogs/insert
→ BlogRequestDTO
→ email로 UserEntity 조회
→ BlogRequestDTO.toEntity(user)
→ BlogEntity.author에 사용자 연결
→ blogRepository.save()
→ INSERT
→ BlogResponseDTO
→ 201 Created
```

---

## 17. 게시글과 댓글을 Fetch Join으로 조회

게시글 하나를 조회한 뒤 댓글을 따로 조회하면 게시글 수만큼 추가 Query가 발생할 수 있다.

```text
게시글 조회 1회
→ 각 게시글의 댓글 조회 N회
→ 1 + N Query 문제
```

JPQL Fetch Join으로 게시글과 댓글을 한 번에 조회할 수 있다.

```java
public interface BlogRepository
        extends JpaRepository<BlogEntity, Integer> {

    @Query("""
        SELECT    b
        FROM      BlogEntity b
        LEFT JOIN FETCH b.comments
        WHERE     b.blogId = :blogId
    """)
    Optional<BlogEntity> findByComments(
        @Param("blogId") Integer blogId
    );
}
```

핵심 문법은 다음과 같다.

- `BlogEntity`: DB 테이블명이 아니라 Entity Class 이름
- `b.comments`: Column이 아니라 Entity의 연관관계 필드
- `b.blogId`: DB의 `blog_id`가 아니라 Entity 필드명
- `:blogId`: 이름 기반 JPQL Parameter
- `@Param("blogId")`: Method Parameter와 JPQL Parameter 연결
- `LEFT JOIN`: 댓글이 없는 게시글도 조회
- `FETCH`: 연관된 댓글까지 함께 Loading

Service에서는 `Optional`을 DTO로 변환한다.

```java
@Transactional(readOnly = true)
public BlogResponseDTO read(Integer id) {
    return blogRepository.findByComments(id)

        // 게시글과 댓글 Entity를 상세 응답 DTO로 변환
        .map(BlogResponseDTO::fromEntityWithComments)

        // 해당 PK의 게시글이 없으면 예외
        .orElseThrow(() ->
            new RuntimeException(id + " BLOG NOT FOUND")
        );
}
```

---

## 18. 1:N 관계를 중첩 DTO로 변환

게시글 상세 응답에는 댓글 배열이 필요하다.

```java
public static BlogResponseDTO fromEntityWithComments(
        BlogEntity entity) {

    return BlogResponseDTO.builder()
        .blogId(entity.getBlogId())
        .title(entity.getTitle())
        .content(entity.getContent())
        .category(entity.getCategory())
        .email(entity.getAuthor().getEmail())

        // List<CommentEntity> → List<CommentResponseDTO>
        .comments(
            entity.getComments()
                .stream()
                .map(CommentResponseDTO::fromEntity)
                .toList()
        )
        .build();
}
```

```text
BlogEntity
├─ blogId
├─ title
├─ content
└─ List<CommentEntity>
          ↓ 각각 DTO 변환
BlogResponseDTO
├─ blogId
├─ title
├─ content
└─ List<CommentResponseDTO>
```

---

## 19. 댓글 작성

댓글도 게시글과 N:1 관계이므로 먼저 부모 게시글을 조회한다.

```java
@Transactional
public CommentResponseDTO insert(CommentRequestDTO request) {
    // 1. 댓글이 속할 게시글 조회
    BlogEntity blog = blogRepository
        .findById(request.getBlogId())
        .orElseThrow(() ->
            new RuntimeException(
                request.getBlogId() + " BLOG NOT FOUND"
            )
        );

    // 2. Request DTO → CommentEntity
    //    동시에 CommentEntity.blog에 부모 게시글 연결
    CommentEntity entity = request.toEntity(blog);

    // 3. 댓글 저장
    CommentEntity saved = commentRepository.save(entity);

    // 4. Entity → Response DTO
    return CommentResponseDTO.fromEntity(saved);
}
```

```java
public CommentEntity toEntity(BlogEntity blog) {
    return CommentEntity.builder()
        .comment(this.comment)
        .email(this.email)
        .blog(blog) // FK 값 대신 연관 Entity 연결
        .build();
}
```

```text
CommentRequestDTO.blogId
→ BlogRepository.findById()
→ BlogEntity
→ CommentEntity.blog에 주입
→ CommentRepository.save()
→ CommentResponseDTO
```

---

## 20. 댓글 삭제

`JpaRepository`가 제공하는 `deleteById()`를 사용하면 별도 DELETE SQL을 작성하지 않아도 된다.

```java
@Transactional
public int delete(Integer commentId) {
    // PK에 해당하는 댓글 삭제
    commentRepository.deleteById(commentId);

    // 현재 학습 코드에서는 성공 Flag로 1 반환
    return 1;
}
```

실무적으로는 없는 ID를 구분해야 한다면 먼저 존재 여부를 검사할 수 있다.

```java
if (!commentRepository.existsById(commentId)) {
    throw new RuntimeException("COMMENT NOT FOUND");
}

commentRepository.deleteById(commentId);
```

삭제 성공 후 반환할 Body가 없다면 Controller는 `204 No Content`를 사용할 수 있다.

```java
return ResponseEntity
    .status(HttpStatus.NO_CONTENT)
    .build();
```

---

## 21. 댓글 수정과 Dirty Checking

JPA에서는 수정 SQL을 직접 작성하지 않고, Transaction 안에서 영속 Entity의 값을 변경할 수 있다.

```java
// Entity가 자신의 상태를 변경하는 Method
public void updateComment(String comment) {
    this.comment = comment;
}
```

```java
@Transactional
public int update(Map<String, Object> map) {
    // 1. findById()로 조회된 Entity가 영속성 컨텍스트에 들어간다.
    CommentEntity entity = commentRepository
        .findById((Integer) map.get("id"))
        .orElseThrow(() ->
            new RuntimeException("COMMENT NOT FOUND")
        );

    // 2. 영속 Entity의 상태 변경
    entity.updateComment(
        (String) map.get("comment")
    );

    // 3. Transaction 종료 시 현재 상태와 Snapshot 비교
    // 4. 값이 다르면 Hibernate가 UPDATE 실행
    return 1;
}
```

Dirty Checking 흐름은 다음과 같다.

```text
Transaction 시작
→ findById()
→ Entity가 영속 상태가 됨
→ 최초 Snapshot 보관
→ updateComment()로 현재 상태 변경
→ Transaction 종료
→ 현재 상태와 Snapshot 비교
→ 변경 감지
→ UPDATE SQL
→ COMMIT
```

영속 상태 Entity를 수정하는 경우 `save()`를 다시 호출하지 않아도 Transaction Commit 시점에 UPDATE될 수 있다.

---

## 22. 현재 구현에서 주의할 점

### Token과 Secret을 로그에 출력하지 않기

다음 값은 Console에 출력하면 안 된다.

```text
JWT Secret Key
Access Token
Refresh Token
사용자 비밀번호
```

### JWT 검증 실패에는 401 반환하기

```java
catch (Exception e) {
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}
```

### Token Parsing과 후속 요청 예외 분리하기

```java
try {
    // JWT 자체만 검증
    Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token);
} catch (Exception e) {
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}

// Controller나 DB 예외를 JWT 실패로 잡지 않도록 try 밖에서 실행
chain.doFilter(request, response);
```

### API와 DTO의 ID 이름 통일하기

Backend가 `blogId`를 반환하면 Frontend도 `blog.blogId`를 사용해야 한다.

```javascript
moveUrl(`/blogs/read/${blog.blogId}`);
```

### 빈 Category 검증하기

DB의 `CHECK` 조건에 없는 빈 문자열을 저장하면 제약조건 오류가 발생한다.

```java
String category = request.getCategory();

if (category == null || category.isBlank()) {
    category = "전체";
}
```

---

## 23. 오늘의 최종 흐름

### 인증 흐름

```text
로그인 성공
→ JwtProvider가 AT·RT 생성
→ Client가 Token 저장
→ 보호 API 호출 시 Authorization Header에 AT 포함
→ JwtFilter가 Bearer Token 추출
→ 서명·만료시간 검증
→ 성공하면 DispatcherServlet로 전달
→ 실패하면 401 반환
```

### 게시글 CRUD 흐름

```text
Controller
→ BlogRequestDTO
→ 작성자 UserEntity 조회
→ BlogEntity 관계 설정
→ BlogRepository.save()
→ Hibernate INSERT
→ BlogResponseDTO
```

```text
BlogRepository.findByComments(blogId)
→ JPQL LEFT JOIN FETCH
→ BlogEntity + List<CommentEntity>
→ 중첩 Response DTO 변환
→ 게시글 상세 JSON 반환
```

### 댓글 CRUD 흐름

```text
댓글 작성
→ 부모 BlogEntity 조회
→ CommentEntity.blog 관계 설정
→ save()

댓글 삭제
→ deleteById(commentId)

댓글 수정
→ findById(commentId)
→ 영속 Entity 값 변경
→ Dirty Checking
→ UPDATE
```

오늘의 핵심은 인증과 데이터 접근을 각각 공통 구조로 분리한 것이다.

```text
JwtFilter
→ 모든 보호 요청의 Token 검증 담당

Controller
→ HTTP 요청·응답 담당

Service + @Transactional
→ 기능 흐름과 영속성 관리

JpaRepository
→ Entity 기반 CRUD

Hibernate
→ Mapping 분석과 SQL 생성

Flyway
→ Schema 변경 SQL과 Version 이력 관리
```
