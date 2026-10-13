# BE 21일차 - Redis와 Spring Security 기반 인증·로그아웃

## 1. 오늘 학습한 핵심

오늘은 기존 JWT 인증을 Spring Security의 Filter Chain과 연결하고, Refresh Token을 Redis에 저장하여 로그인 상태와 로그아웃을 관리하는 흐름을 구현했다.

- Docker로 Redis와 RedisInsight 실행
- `RedisTemplate`을 이용한 문자열 데이터 저장
- TTL을 적용한 Refresh Token 관리
- Spring Security의 인증과 인가
- `SecurityFilterChain`을 이용한 URL 접근 정책
- `OncePerRequestFilter` 기반 JWT 인증 Filter
- JWT Claims에서 사용자와 권한 추출
- `UsernamePasswordAuthenticationToken` 생성
- `SecurityContextHolder`에 인증 정보 저장
- BCrypt를 이용한 비밀번호 Hashing과 비교
- 로그아웃 시 Redis의 Refresh Token 삭제
- React의 Token 제거와 화면 이동

오늘 구현의 전체 흐름은 다음과 같다.

```text
회원가입
React → UserController → BCrypt Hashing → UserService → MariaDB

로그인
React → UserController → PasswordEncoder.matches()
      → AT·RT 생성 → RT를 Redis에 저장 → Token 응답

인증 요청
React + AT → Spring Security Filter Chain
           → JwtAuthenticationFilter
           → JWT 검증 및 Claims 추출
           → SecurityContextHolder 저장
           → Controller → Service

로그아웃
React + AT → JwtAuthenticationFilter
           → SecurityContextHolder에 사용자 저장
           → UserService.signOut()
           → Redis의 RT 삭제
           → 204 No Content
           → React localStorage Token 삭제 및 화면 이동
```

---

## 2. Redis를 사용하는 이유

Redis는 데이터를 주로 메모리에서 처리하는 Key-Value 저장소다. 일반적인 관계형 DB처럼 Table과 Join을 중심으로 사용하기보다 빠른 조회와 만료가 필요한 임시 데이터를 관리하는 데 적합하다.

```text
Key                  Value
RTuser@example.com   eyJhbGciOiJIUzI1NiJ9...
```

인증 시스템에서는 다음과 같은 데이터를 Redis로 관리할 수 있다.

- Refresh Token
- 인증번호와 제한시간
- 로그인 Session
- 일회용 Token
- 요청 횟수 제한 정보
- 짧은 수명의 Cache

Refresh Token을 Client에만 저장하면 Server가 특정 Token을 직접 폐기하기 어렵다. Redis에 사용자별 RT를 저장하면 로그아웃, 강제 로그아웃, 재발급 가능 여부를 Server가 통제할 수 있다.

```text
로그인 성공
→ 사용자별 RT를 Redis에 저장

AT 재발급 요청
→ Client가 보낸 RT와 Redis의 RT 비교
→ 일치하고 만료되지 않았으면 새 AT 발급

로그아웃
→ Redis에서 사용자 RT 삭제
→ 이후 해당 RT로 재발급 불가
```

Redis가 메모리 중심 저장소인 것은 맞지만 SQLite와는 성격이 다르다. SQLite는 파일 기반 Embedded 관계형 DB이고, Redis는 별도 Server Process로 실행하는 메모리 중심 Key-Value 저장소다.

---

## 3. Docker와 Redis 실행

Redis는 Linux 환경을 중심으로 제공되므로 Windows에서는 Docker Desktop 또는 WSL을 이용해 실행할 수 있다.

### Redis Container 실행

```bash
docker run -d \
  --name redis-container \
  -p 6379:6379 \
  redis:7.2
```

- `docker run`: Image로 Container 생성 및 실행
- `-d`: Background 실행
- `--name`: Container 이름 지정
- `-p 6379:6379`: Host와 Container의 Port 연결
- `redis:7.2`: Docker Hub에서 사용할 Image와 Tag

```text
localhost:6379
      ↓ Port Mapping
Redis Container:6379
```

### Redis CLI 접속

```bash
docker exec -it redis-container redis-cli
```

`docker exec`는 실행 중인 Container 내부에서 명령을 실행한다. `--name`은 `docker run`에서 사용하는 옵션이므로 `docker exec`에서는 Container 이름만 전달한다.

```bash
KEYS RT*
GET RTuser@example.com
TTL RTuser@example.com
```

### RedisInsight 실행

```bash
docker run -d \
  --name redis-insight \
  -p 8001:5540 \
  redis/redisinsight:latest
```

Browser에서 다음 주소로 접속한다.

```text
http://localhost:8001
```

RedisInsight Container에서 Host PC의 Redis에 접근해야 한다면 Host로 `host.docker.internal`을 사용할 수 있다. Redis와 RedisInsight를 같은 Docker Network에 배치했다면 Redis Container 이름으로 연결하는 방법도 있다.

---

## 4. 가상화, Docker, DevOps

### Bare Metal, VM, Container

```text
Bare Metal
→ 물리 Server Hardware에서 OS와 Application을 직접 실행

Hypervisor 기반 VM
→ 하나의 물리 Server에 여러 Guest OS 실행

Container
→ Host OS Kernel을 공유하면서 Application 실행환경을 격리
```

VM은 각각 Guest OS를 포함하지만 Container는 Host Kernel을 공유하므로 상대적으로 가볍고 빠르게 생성할 수 있다.

### Image와 Container

```text
Dockerfile
  ↓ Build
Docker Image
  ↓ Run
Docker Container
```

- Image: OS 사용자 공간, Runtime, Library, Application을 포함한 실행 Template
- Container: Image를 기반으로 실행된 격리 Process
- Docker Hub: 공개 Image Registry
- AWS ECR: AWS의 Container Image Registry
- AWS EC2: Image로 만든 Container를 실행할 수 있는 Server

```text
Source Code
→ CI Build·Test
→ Docker Image 생성
→ ECR Push
→ EC2 또는 Container Platform에 배포
→ Monitoring과 Scale Out
```

DevOps는 Development와 Operations의 협업과 자동화를 의미한다. CI/CD Pipeline, 배포, Monitoring, 장애 대응, Traffic 증가에 따른 Scale Out 등이 포함된다.

---

## 5. Redis Dependency와 연결 설정

### Gradle Dependency

```gradle
implementation "org.springframework.boot:spring-boot-starter-data-redis"
implementation "org.springframework.boot:spring-boot-starter-security"
```

- `spring-boot-starter-data-redis`: Redis 연결, `RedisTemplate`, Spring Data Redis 제공
- `spring-boot-starter-security`: Security Filter Chain, 인증·인가, PasswordEncoder 제공

### application-dev.yml

```yaml
spring:
  data:
    redis:
      # Spring Boot가 연결할 Redis Server
      host: localhost
      port: 6379
      password:

      # 연결이 완료되지 않을 때 기다릴 최대시간
      timeout: 6000ms
```

Server가 시작되면 Spring Boot가 이 설정을 읽어 `RedisConnectionFactory`를 자동 구성한다.

```text
application-dev.yml
→ RedisAutoConfiguration
→ RedisConnectionFactory
→ RedisTemplate
→ Redis Server
```

---

## 6. RedisTemplate 구성

현재 프로젝트의 `RedisConfig`는 Connection Factory를 주입받아 문자열 직렬화 방식의 `RedisTemplate` Bean을 만든다.

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory rcf) {

        RedisTemplate<String, Object> template =
            new RedisTemplate<>();

        // Spring Boot가 구성한 Redis 연결 객체 등록
        template.setConnectionFactory(rcf);

        // Key를 읽을 수 있는 문자열로 직렬화
        template.setKeySerializer(
            new StringRedisSerializer()
        );

        // Value도 문자열로 직렬화
        template.setValueSerializer(
            new StringRedisSerializer()
        );

        return template;
    }
}
```

Java 객체는 그대로 Network를 통해 Redis에 저장할 수 없으므로 Byte 형태로 변환하는 직렬화가 필요하다.

```text
Java String
→ StringRedisSerializer
→ Byte Data
→ Redis 저장
```

현재는 RT 문자열을 저장하므로 `StringRedisSerializer`가 적합하다. 복잡한 객체를 저장한다면 JSON 직렬화를 고려할 수 있다.

---

## 7. Refresh Token 저장과 TTL

현재 `RedisService`는 사용자 Email을 Key로 사용해 RT를 저장한다.

```java
@Service
@RequiredArgsConstructor
public class RedisService {

    // 초 단위: 7일
    private static final long RT_TTL
        = 60L * 60 * 24 * 7;

    private final RedisTemplate<String, Object>
        redisTemplate;

    public void saveToken(String email, String rt) {
        redisTemplate.opsForValue().set(
            "RT" + email,       // Key
            rt,                 // Value
            RT_TTL,             // 만료시간
            TimeUnit.SECONDS    // 시간 단위
        );
    }
}
```

`opsForValue()`는 Redis의 단순 String Value 명령을 사용하기 위한 API다.

```text
SET RTuser@example.com {refresh-token}
EXPIRE RTuser@example.com 604800
```

TTL(Time To Live)을 적용하면 정해진 시간이 지난 Key를 Redis가 자동으로 제거한다.

```text
60 × 60 × 24 × 7
= 604800초
= 7일
```

JWT 자체의 만료시간과 Redis TTL을 함께 맞추면 만료된 RT가 Redis에 불필요하게 남는 것을 줄일 수 있다.

---

## 8. 인증과 인가

Spring Security에서 두 개념은 구분해야 한다.

```text
인증(Authentication)
→ 요청한 사용자가 누구인지 확인
→ 예: 로그인, JWT 서명과 만료 검증

인가(Authorization)
→ 인증된 사용자가 해당 기능에 접근할 권한이 있는지 확인
→ 예: ADMIN만 /admin/** 접근 허용
```

```java
.requestMatchers("/admin/**")
.hasRole("ADMIN")
```

`hasRole("ADMIN")`은 내부적으로 `ROLE_ADMIN` 권한을 검사한다.

```text
사용자의 GrantedAuthority: ROLE_ADMIN
Security 설정: hasRole("ADMIN")
→ 접근 허용
```

---

## 9. SecurityFilterChain 구성

기존 Servlet `Filter`를 직접 등록하는 대신 Spring Security가 관리하는 Filter Chain에 JWT Filter를 연결했다.

```java
@Configuration
@RequiredArgsConstructor
public class SecretConfig {

    private final JwtAuthenticationFilter
        jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterchain(
            HttpSecurity http) throws Exception {

        http
            // 등록된 CorsConfigurationSource 사용
            .cors(Customizer.withDefaults())

            // Stateless REST API에서 CSRF 비활성화
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // 로그인, 회원가입, Swagger는 인증 없이 허용
                .requestMatchers(
                    "/users/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // CORS Preflight 허용
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()

                // 관리자 권한 필요
                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )

            // Server Session을 만들지 않는 JWT 방식
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            );

        // 기본 ID/PW 인증 Filter보다 JWT Filter를 먼저 실행
        http.addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
```

```text
HTTP Request
→ Spring Security Filter Chain
→ JwtAuthenticationFilter
→ UsernamePasswordAuthenticationFilter
→ Authorization 검사
→ DispatcherServlet
→ Controller
```

`permitAll()`은 해당 URL의 최종 접근을 허용한다는 의미다. Custom Filter 자체가 실행되지 않는다는 의미는 아니므로, Token이 없는 요청도 `filterChain.doFilter()`로 다음 단계에 전달해야 한다.

---

## 10. CORS를 Spring Security에서 처리

React와 Spring의 Scheme, Host, Port 중 하나라도 다르면 Cross-Origin 요청이 된다.

```text
React  : http://localhost:3200
Spring : http://localhost:8000
→ Port가 다르므로 Cross-Origin
```

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration =
        new CorsConfiguration();

    configuration.setAllowedOrigins(List.of(
        "http://localhost:3200",
        "http://127.0.0.1:3200"
    ));

    configuration.setAllowedMethods(List.of(
        "GET", "POST", "PUT",
        "PATCH", "DELETE", "OPTIONS"
    ));

    // Browser가 Server로 보낼 수 있는 Header
    configuration.setAllowedHeaders(List.of(
        "Authorization",
        "Content-Type",
        "Refresh-Token"
    ));

    // JavaScript가 Server 응답에서 읽을 수 있는 Header
    configuration.setExposedHeaders(List.of(
        "Authorization",
        "Refresh-Token"
    ));

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
        "/**",
        configuration
    );

    return source;
}
```

```text
AllowedHeaders
→ Client가 Request에 포함할 수 있는 Header

ExposedHeaders
→ Client JavaScript가 Response에서 읽을 수 있는 Header
```

JSON 요청은 `Content-Type: application/json`을 사용하므로 `Content-Type`이 허용되지 않으면 실제 POST 전에 OPTIONS Preflight가 차단된다.

---

## 11. BCrypt 비밀번호 Hashing

비밀번호는 복호화가 가능하면 안 되므로 단방향 Hashing을 사용한다.

```text
Hashing
→ 단방향, 원문 복구 불가
→ 비밀번호 저장에 사용
→ BCrypt, Argon2 등

Encryption
→ Key를 이용한 복호화 가능
→ 원문 복구가 필요한 데이터에 사용
→ AES 등
```

### PasswordEncoder Bean

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 회원가입

```java
@Transactional
public UserResponseDTO signUp(UserRequestDTO request) {
    // 기존 DTO를 복사하고 password만 Hash 값으로 교체
    UserRequestDTO hashingDTO = request.toBuilder()
        .password(
            passwordEncoder.encode(
                request.getPassword()
            )
        )
        .build();

    UserEntity entity =
        UserRequestDTO.toEntity(hashingDTO);

    return UserResponseDTO.fromEntity(
        userRepository.save(entity)
    );
}
```

`toBuilder()`는 원본 DTO를 변경하지 않고 복사된 Builder를 만든다. 따라서 `.build()` 결과를 `hashingDTO`에 저장해 Service의 다음 단계로 전달해야 한다.

### 로그인

BCrypt는 같은 평문을 Hash해도 매번 결과가 달라질 수 있으므로 DB Query에서 평문과 Hash를 직접 비교하면 안 된다.

```java
UserEntity entity = userRepository
    .findById(request.getEmail())
    .orElseThrow(() ->
        new LoginFailException("SignIn Fail!!")
    );

boolean matches = passwordEncoder.matches(
    request.getPassword(), // 사용자가 입력한 평문
    entity.getPassword()   // DB에 저장된 BCrypt Hash
);

if (!matches) {
    throw new LoginFailException("SignIn Fail!!");
}
```

```text
입력 Password
→ BCrypt의 matches()
← DB의 Hash Password
→ 일치 여부만 확인
```

---

## 12. 로그인과 AT·RT 발급

비밀번호가 일치하면 JWT를 발급한다.

```java
String at = jwtProvider.createAT(entity.getEmail());
String rt = jwtProvider.createRT(entity.getEmail());

// 재발급과 로그아웃 통제를 위해 RT만 Redis에 저장
redisService.saveToken(entity.getEmail(), rt);

Map<String, Object> map = new HashMap<>();
map.put("response", UserResponseDTO.fromEntity(entity));
map.put("at", at);
map.put("rt", rt);
```

Controller는 Token을 Response Header로 반환한다.

```java
HttpHeaders headers = new HttpHeaders();

headers.add(
    "Authorization",
    "Bearer " + map.get("at")
);

headers.add(
    "Refresh-Token",
    (String) map.get("rt")
);

return ResponseEntity
    .status(HttpStatus.OK)
    .headers(headers)
    .body(map.get("response"));
```

```text
AT
→ 매 API 요청의 Authorization Header에 사용
→ 짧은 수명

RT
→ AT가 만료됐을 때 재발급에 사용
→ 긴 수명
→ Redis에서 Server가 상태 관리
```

현재 코드에서는 AT 재발급 Endpoint와 Redis RT 비교 로직은 아직 구현되지 않았다. RT 저장까지 구현됐으며, 다음 단계에서 만료된 AT를 RT로 재발급하는 흐름을 연결할 수 있다.

---

## 13. OncePerRequestFilter 기반 JWT 인증

`JwtAuthenticationFilter`는 `OncePerRequestFilter`를 상속한다.

```java
@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        // JWT 인증 처리
    }
}
```

`OncePerRequestFilter`는 하나의 요청 Dispatch에서 Filter 로직이 중복 실행되는 것을 방지하기 위한 Spring Filter 기반 클래스다. Security Filter Chain 전체가 한 개의 Filter만 가진다는 뜻은 아니다.

```text
Spring Security Filter Chain
├─ CorsFilter
├─ 여러 Security Filter
├─ JwtAuthenticationFilter
└─ UsernamePasswordAuthenticationFilter
```

---

## 14. Authorization Header 처리

```java
String header = request.getHeader("Authorization");

if (header == null || !header.startsWith("Bearer ")) {
    // Token이 없으면 인증정보를 만들지 않고 다음 Filter로 이동
    filterChain.doFilter(request, response);
    return;
}

// "Bearer " 7글자를 제거
String token = header.substring(7);
```

Token이 없는 요청을 곧바로 차단하지 않는 이유는 공개 URL인지 최종적으로 `SecurityFilterChain`의 인가 정책이 판단하도록 하기 위해서다.

```text
/users/signIn + Token 없음
→ Filter 통과
→ permitAll()
→ Controller 실행

/blogs/index + Token 없음
→ Filter 통과
→ authenticated()
→ 인증정보가 없으므로 접근 거부
```

---

## 15. JWT Claims와 인증 객체

JWT 검증 후 Payload의 Claims를 가져온다.

```java
Claims claims = Jwts.parserBuilder()
    .setSigningKey(key)
    .build()
    .parseClaimsJws(token)
    .getBody();

String email = claims.getSubject();
String role = claims.get("role", String.class);
```

Claims에서 가져온 정보를 Spring Security가 이해할 수 있는 인증 객체로 변환한다.

```java
UsernamePasswordAuthenticationToken authenticationToken =
    new UsernamePasswordAuthenticationToken(
        email, // principal: 인증된 사용자 식별값
        null,  // credentials: 인증 후 비밀번호는 보관하지 않음
        role != null
            ? List.of(() -> "ROLE_" + role)
            : List.of()
    );
```

세 번째 생성자에 권한 Collection을 넘기면 Spring Security는 이 객체를 인증 완료된 `Authentication`으로 취급한다.

```text
principal   → 누구인가? email
credentials → 인증에 사용한 비밀정보, 인증 후에는 null
authorities → 무엇을 할 수 있는가? ROLE_USER, ROLE_ADMIN
```

단, 현재 `JwtProvider.createAT()`에는 `role` Claim을 넣는 코드가 없다. 따라서 Filter에서 `claims.get("role")`을 호출해도 `null`이 나오고 `/admin/**` 접근 권한은 만들어지지 않는다. Role 기반 인가를 사용하려면 Token 발급 시 Role Claim을 포함해야 한다.

```java
// 개념 예시
Jwts.builder()
    .setSubject(email)
    .claim("role", role)
    // issuedAt, expiration, signWith...
    .compact();
```

---

## 16. SecurityContextHolder

만든 인증 객체를 Security Context에 저장한다.

```java
authenticationToken.setDetails(
    new WebAuthenticationDetailsSource()
        .buildDetails(request)
);

SecurityContextHolder
    .getContext()
    .setAuthentication(authenticationToken);

filterChain.doFilter(request, response);
```

```text
JWT Claims
→ UsernamePasswordAuthenticationToken
→ SecurityContextHolder
→ Controller·Service에서 현재 사용자 조회 가능
```

Client가 Body의 Email을 조작하더라도 Server는 검증된 JWT에서 얻은 사용자 정보를 Security Context에 저장해 사용할 수 있다.

```java
Authentication auth = SecurityContextHolder
    .getContext()
    .getAuthentication();

String email = auth.getName();
```

현재 `BlogService`도 게시글 작성자를 Request Body의 Email만 신뢰하지 않고 Security Context의 인증 사용자로 판단하는 방향으로 연결할 수 있다.

---

## 17. 로그아웃 구현

### 17.1 React 요청

```javascript
await api.post('/users/signOut', null, {
    headers: {
        Authorization: at
    }
});
```

로그아웃 요청에도 AT를 포함해야 Filter가 사용자 Email을 Security Context에 저장할 수 있다.

### 17.2 JWT Filter

```text
Authorization: Bearer {AT}
→ Bearer 접두사 제거
→ JWT 서명·만료 검증
→ subject에서 email 추출
→ Authentication 생성
→ SecurityContextHolder 저장
```

### 17.3 Controller

```java
@PostMapping("/signOut")
public ResponseEntity<?> signOut() {
    userService.signOut();

    // 성공했지만 반환할 Body는 없음
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
}
```

`204 No Content`는 요청 처리는 성공했지만 Response Body가 없다는 뜻이다.

### 17.4 Service에서 인증 사용자 조회

```java
public void signOut() {
    Authentication auth = SecurityContextHolder
        .getContext()
        .getAuthentication();

    // Filter가 JWT subject를 principal로 저장했으므로 email 반환
    String email = auth.getName();

    redisService.deleteToken(email);
}
```

Request Parameter로 Email을 다시 받지 않고 검증된 Security Context에서 사용자를 가져오므로 조작 위험을 줄일 수 있다.

### 17.5 Redis에서 RT 삭제

```java
public void deleteToken(String email) {
    redisTemplate.delete("RT" + email);
}
```

```text
email = user@example.com
→ Key = RTuser@example.com
→ Redis DEL
→ 해당 RT로 AT 재발급 불가
```

### 17.6 React Local Storage 정리

Server의 Redis RT 삭제와 Browser의 Token 삭제는 서로 다른 작업이다.

```javascript
if (response.status === 204) {
    localStorage.removeItem('at');
    localStorage.removeItem('rt');
    localStorage.removeItem('user');

    moveUrl('/');
}
```

```text
Redis RT 삭제
→ Server가 재발급을 거부할 수 있게 함

localStorage 삭제
→ Browser가 기존 Token을 더 이상 보내지 않게 함
```

백엔드가 `204`를 반환하는데 프론트가 `response.status === 200`만 확인하면 로그아웃은 처리돼도 화면 이동 조건은 실행되지 않는다.

---

## 18. 로그아웃 전체 데이터 흐름

```text
[React]
localStorage에서 AT 조회
  ↓
POST /users/signOut
Authorization: Bearer {AT}
  ↓
[Spring Security Filter Chain]
JwtAuthenticationFilter
  ↓
AT 검증
  ↓
Claims.subject → email
  ↓
UsernamePasswordAuthenticationToken 생성
  ↓
SecurityContextHolder 저장
  ↓
[UserController]
userService.signOut()
  ↓
[UserService]
SecurityContextHolder에서 email 조회
  ↓
[RedisService]
DEL RT{email}
  ↓
[UserController]
204 No Content
  ↓
[React]
at·rt·user 삭제
  ↓
메인 화면 이동
```

---

## 19. AT 만료와 RT 재발급 방향

RT를 발급하는 핵심 이유는 AT의 수명을 짧게 유지하면서 사용자가 매번 로그인하지 않도록 하기 위해서다.

```text
API 요청 + AT
→ AT 정상: 요청 계속
→ AT 만료: RT 재발급 절차 시작
```

재발급 흐름은 다음처럼 구성할 수 있다.

```text
Client가 만료된 AT 또는 재발급 요청 전송
→ Client RT 확인
→ RT 서명·만료 검증
→ Redis의 RT 조회
→ Client RT와 Redis RT 비교
→ 일치하면 새 AT 발급
→ Response Header로 전달
→ Client가 AT 교체 후 원래 요청 재시도
```

사용자가 보기에는 AT만 보내는 것처럼 보이더라도, Client의 Axios Interceptor 또는 Server의 인증 구조가 중간에서 재발급 절차를 처리할 수 있다. 현재 코드에는 이 재발급 Interceptor와 Redis 조회·비교 로직이 아직 추가되지 않았다.

---

## 20. 현재 코드에서 확인해야 할 점

### 20.1 `/users/**` 범위

현재 설정은 모든 사용자 URL을 공개한다.

```java
.requestMatchers("/users/**").permitAll()
```

이 경우 `/users/signOut`도 최종 인가 단계에서는 공개 URL이다. 로그아웃에서 반드시 인증 사용자를 요구하려면 공개 범위를 회원가입과 로그인으로 좁히는 것이 더 명확하다.

```java
// 개념 예시
.requestMatchers(
    "/users/signUp",
    "/users/signIn"
).permitAll()
.anyRequest().authenticated()
```

### 20.2 JWT 예외 처리

현재 Filter의 `catch`는 요청을 중단하지만 Status를 지정하지 않는다.

```java
catch (Exception e) {
    response.setStatus(
        HttpServletResponse.SC_UNAUTHORIZED
    );
    return;
}
```

JWT가 만료됐거나 위조된 경우 `401 Unauthorized`를 명확히 반환해야 Client가 재발급 또는 로그인 이동을 판단할 수 있다.

### 20.3 Token과 비밀번호 Logging 금지

다음 값은 Console에 출력하면 안 된다.

```text
JWT Secret
Access Token
Refresh Token
사용자 평문 Password
```

### 20.4 Response DTO에 Password 제외

비밀번호가 Hash 값이어도 Client에 반환할 필요가 없다. `UserResponseDTO`에서는 Password 필드를 제외하는 편이 안전하다.

### 20.5 Header 이름 통일

다음 표기가 섞이지 않도록 하나로 통일한다.

```text
Refresh-Token
Refresh-token
refresh-token
```

HTTP Header 이름은 대소문자를 구분하지 않지만 코드와 CORS 설정을 같은 표기로 유지하면 혼동을 줄일 수 있다.

---

## 21. 오늘의 핵심 문법

| 문법 | 역할 |
| --- | --- |
| `@Configuration` | 설정 Class를 Spring Bean으로 등록 |
| `@Bean` | Method가 반환한 객체를 Spring Container에 등록 |
| `RedisConnectionFactory` | Redis 연결 생성과 관리 |
| `RedisTemplate<K, V>` | Redis 데이터 명령 실행 |
| `opsForValue()` | 단순 Key-Value 데이터 처리 |
| `TimeUnit.SECONDS` | TTL의 시간 단위 지정 |
| `PasswordEncoder.encode()` | 평문 비밀번호 Hashing |
| `PasswordEncoder.matches()` | 평문과 저장된 Hash 비교 |
| `SecurityFilterChain` | Spring Security Filter와 접근 정책 구성 |
| `SessionCreationPolicy.STATELESS` | Server Session을 사용하지 않는 JWT 방식 |
| `OncePerRequestFilter` | 요청마다 한 번 실행되는 Custom Filter 기반 Class |
| `parseClaimsJws()` | JWT 서명·만료 검증 및 Claims Parsing |
| `UsernamePasswordAuthenticationToken` | Spring Security 인증 객체 |
| `SecurityContextHolder` | 현재 요청의 인증 정보 저장·조회 |
| `hasRole("ADMIN")` | `ROLE_ADMIN` 권한 검사 |
| `204 No Content` | 성공했지만 반환 Body가 없는 응답 |

---

## 22. 최종 정리

오늘 구현의 핵심은 JWT 문자열을 검증하는 수준에서 더 나아가, 인증 결과를 Spring Security의 표준 인증 객체와 Context로 연결한 것이다.

```text
JWT
→ 사용자의 신원을 증명하는 Token

JwtAuthenticationFilter
→ Token을 검증하고 Claims를 읽는 입구

UsernamePasswordAuthenticationToken
→ Spring Security가 이해하는 인증 객체

SecurityContextHolder
→ 현재 요청의 인증 사용자와 권한을 보관

SecurityFilterChain
→ URL별 인증·인가 정책 결정

Redis
→ RT의 상태와 만료시간을 Server에서 관리

로그아웃
→ Security Context에서 사용자 확인
→ Redis RT 삭제
→ Browser Token 삭제
```

최종 인증 흐름은 다음과 같다.

```text
회원가입: BCrypt Hash 저장
로그인: matches() 검증 → AT·RT 발급 → Redis RT 저장
API 요청: AT 검증 → SecurityContextHolder 인증 저장
인가: URL과 ROLE에 따라 접근 허용 또는 차단
로그아웃: 인증 사용자 확인 → Redis RT 삭제 → Client Token 삭제
```
