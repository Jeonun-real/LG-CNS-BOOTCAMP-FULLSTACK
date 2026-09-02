# BE 16일차 — 환경 변수부터 Swagger, 로그인과 FE 연동까지

오늘은 `inspire_mybatis` 프로젝트에서 DB 설정을 외부화하고, Swagger로 API를 확인한 뒤 React와 회원가입·로그인을 연결했다. 핵심은 **요청 데이터가 어디에 실리는지에 따라 Spring이 받는 방식이 달라진다**는 점이다.

> 정리 기준: 현재 저장된 BE·FE 코드. 아래 코드는 핵심 부분을 발췌했으며, 개선 예시는 별도로 표시했다. 애플리케이션 코드는 수정하지 않았다.

## 1. 오늘의 학습 흐름

```text
.env로 DB 설정 분리
  ↓
Spring Boot 실행 → MyBatis 연결
  ↓
Swagger로 API 문서 확인·요청 테스트
  ↓
회원가입: POST + JSON → @RequestBody
  ↓
로그인: GET + Query String → @RequestParam
  ↓
Controller → Service → Mapper → DB
  ↓
사용자 조회 성공 → AT·RT 생성 메서드 호출
  ↓
ResponseEntity: Header에 토큰, Body에 사용자 정보
  ↓
CORS 설정 → React Axios 연결
  ↓
FE에서 응답 Header 읽기 → localStorage 저장
```

현재 로그인은 GET 방식을 비교 학습하는 코드다. 실제 서비스에서는 비밀번호가 URL에 남지 않도록 HTTPS 환경에서 POST Body로 보내는 방향이 적절하다. POST 자체가 암호화를 제공하는 것은 아니다.

---

## 2. .env로 DB 설정 분리하기

### 2-1. Dependency와 .env 예시

현재 `build.gradle`에 다음 라이브러리가 추가돼 있다.

```gradle
implementation 'io.github.cdimascio:dotenv-java:3.0.0'
```

`.env`에는 환경별 값을 둔다. 아래 값은 설명용 예시이며 실제 비밀값이 아니다.

```dotenv
DB_DRIVER=org.mariadb.jdbc.Driver
DB_URL=jdbc:mariadb://localhost:3306/lgcns
DB_USER=your_db_user
DB_PASSWORD=your_db_password
```

라이브러리 설치만으로 Git 업로드가 차단되는 것은 아니다. 현재 프로젝트의 `.gitignore`에 `.env`가 등록돼 있으며, 실제 비밀값은 Git에 올리지 않는다. 공유용 `.env.example`에는 변수 이름과 예시값만 작성한다.

이미 Git에 추적되거나 공개된 파일은 `.gitignore`에 추가하는 것만으로 이전 기록이 지워지지 않는다.

### 2-2. main()에서 읽어 Spring에 전달

현재 `InspireMybatisApplication`의 핵심 코드다. 원본의 환경 변수 값 출력문은 보안상 아래 예시에서 제외했다.

```java
Dotenv env = Dotenv.configure()
        .ignoreIfMissing()
        .load();

env.entries().forEach(entry -> {
    System.setProperty(entry.getKey(), entry.getValue());
});

SpringApplication.run(InspireMybatisApplication.class, args);
```

- `load()`: .env 설정을 읽는다.
- `ignoreIfMissing()`: 파일이 없다는 이유만으로 Dotenv 로딩을 실패시키지 않는다.
- `entries()`: 읽은 설정의 key와 value를 순회한다.
- `System.setProperty()`: JVM 시스템 속성으로 등록한다. 운영체제의 환경 변수 자체를 변경하는 코드는 아니다.
- Spring 실행 전에 등록하므로 Spring이 설정 Placeholder를 해석할 때 사용할 수 있다.

기본 탐색 기준은 실행 작업 디렉터리다. 상위 폴더에서 실행할 때와 프로젝트 폴더에서 실행할 때 찾는 위치가 달라질 수 있다. [dotenv-java 공식 문서](https://github.com/cdimascio/dotenv-java)

`ignoreIfMissing()`을 사용해도 DB 필수 설정값까지 자동으로 생기지는 않는다. .env가 없으면 운영 환경 변수 등 다른 경로로 값을 제공해야 한다.

### 2-3. application-dev.yml에서 사용

```yaml
spring:
    datasource:
        driver-class-name: ${DB_DRIVER}
        url: ${DB_URL}
        username: ${DB_USER}
        password: ${DB_PASSWORD}

mybatis:
    mapper-locations: classpath:/mappers/**/*Mapper.xml
```

```text
.env
  ↓ Dotenv.load()
key/value
  ↓ System.setProperty()
JVM 시스템 속성
  ↓ Spring 설정 Placeholder 해석
application-dev.yml
  ↓
DataSource → HikariCP → MariaDB
```

기존 메모의 “main 실행 시 JAR로 묶어 배포한다”는 표현은 구분해야 한다. IDE 실행이나 `bootRun`은 매번 JAR 패키징을 하는 것이 아니다. 실행 가능한 JAR은 별도 빌드 작업으로 만들며, 컨테이너 배포 시에는 이를 이미지에 포함할 수 있다.

### BE .env와 FE .env의 차이

BE는 서버에서 값을 사용한다. FE의 `REACT_APP_...` 값은 브라우저용 빌드 결과에 포함되므로 비밀 저장소가 아니다. DB 비밀번호나 JWT 서명키를 FE 환경 변수에 넣으면 안 된다.

현재 main()에는 모든 설정값을 출력하는 코드가 있으므로 DB 비밀번호가 로그에 남을 수 있다. 환경 변수의 실제 값은 로그에 출력하지 않는 것이 안전하다.

---

## 3. Swagger로 API 명세와 요청 확인

### 3-1. Swagger의 역할

OpenAPI는 API 명세 형식이고 Swagger UI는 이를 화면으로 보여주며 요청을 실행하는 도구다. Postman은 요청을 직접 구성하고 응답을 확인하는 API 클라이언트다.

현재 Dependency는 다음과 같다.

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'
```

주의: 현재 Spring Boot는 4.0.8인데 위 springdoc은 2.x다. 공식 문서는 2.x를 Spring Boot 3용으로 안내하므로 이 조합을 호환성이 검증된 예제로 보지는 않는다. Swagger 구동 오류가 있다면 버전 호환성을 별도로 확인해야 한다. [springdoc 공식 문서](https://springdoc.org/v2/)

### 3-2. 실제 Controller의 문서 Annotation

```java
@Tag(name = "User API", description = "사용자 생성과 로그인 관련 API 명세서")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
```

회원가입 Method에는 다음 문서가 붙는다.

```java
@Operation(summary = "회원가입", description = "신규가입(email, password, name)")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "가입성공"),
    @ApiResponse(responseCode = "400", description = "유효성검사실패"),
    @ApiResponse(responseCode = "500", description = "가입실패")
})
@PostMapping("/signUp")
```

| Annotation | 역할 |
|---|---|
| `@Tag` | API를 기능별로 묶고 설명 |
| `@Operation` | 개별 API 요약과 설명 |
| `@ApiResponses` | 여러 응답 설명을 묶음 |
| `@ApiResponse` | 상태 코드별 응답 설명 |
| `@Content` | 요청·응답 콘텐츠 설명 |
| `@Schema` | DTO 등 데이터 구조 설명 |

이 Annotation들은 문서용이다. `responseCode="400"`을 적었다고 유효성 검사가 자동 실행되지는 않는다. 현재 DTO에는 검증 제약조건이 없고 Controller에도 `@Valid`가 없다. 실제 입력 검증과 예외 처리는 별도로 구현해야 한다.

### 3-3. Swagger UI 사용 순서

정상적으로 Swagger가 구성된 환경에서는 보통 다음 주소를 사용한다.

```text
http://localhost:8000/swagger-ui/index.html
```

1. 서버 실행 후 Swagger UI에 접속한다.
2. User API에서 POST /users/signUp을 선택한다.
3. Try it out을 눌러 JSON을 입력한다.
4. Execute를 누른다.
5. Request URL, Status, Response Body와 Headers를 확인한다.
6. 로그인은 현재 GET이므로 email/password Query Parameter를 입력한다.

Swagger에서 가입을 실행하면 실제 DB에 INSERT된다. 같은 Email로 반복 호출하면 중복 키 오류가 날 수 있다.

---

## 4. 이름이 같은 두 @RequestBody

현재 회원가입에는 두 종류의 `@RequestBody`가 등장한다.

```java
public ResponseEntity<?> signUp(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "사용자 정보를 담는 DTO",
        required = true,
        content = @Content(
            schema = @Schema(implementation = UserRequestDTO.class)
        )
    )
    @RequestBody UserRequestDTO request
) {
    // ...
}
```

| 전체 경로 | 역할 |
|---|---|
| `io.swagger.v3.oas.annotations.parameters.RequestBody` | API 문서에 Body 설명 |
| `org.springframework.web.bind.annotation.RequestBody` | 실제 HTTP Body를 Java 객체로 변환 |

Swagger Annotation만으로 JSON이 DTO로 변환되지는 않는다. 실제 변환은 Spring의 `@RequestBody`와 HTTP Message Converter가 담당한다.

현재 로그인은 Query Parameter 방식으로 바뀌었지만 Swagger의 Body 설명이 남아 있다. 따라서 문서 설명과 실제 요청 방식이 다를 수 있다. Query 설명에는 `@Parameter` 등을 사용해 실제 Method와 맞추는 것이 좋다.

---

## 5. GET·POST와 JSON·Query String 구분

가장 중요했던 질문은 “JavaScript 객체로 보내면 전부 JSON 아닌가?”였다.

**코드에서 객체를 작성하는 것과 HTTP에서 JSON Body로 전송하는 것은 다르다.**

### GET + params

FE의 현재 로그인 코드다.

```javascript
await api.get('/users/signIn', {
    params: {
        email: form.email,
        password: form.password
    }
});
```

Axios의 두 번째 인자는 설정 객체다. 그 안의 `params`를 URL로 직렬화한다.

```text
GET /users/signIn?email=user%40example.com&password=root
```

위 데이터는 JSON Body가 아니라 URL의 Query String에 있다.

### POST + data

현재 회원가입 코드다.

```javascript
const data = { ...form };
await api.post('/users/signUp', data);
```

일반 JavaScript 객체를 전달하는 이 사용 방식에서는 Axios가 JSON Body로 전송한다.

```http
POST /users/signUp
Content-Type: application/json

{
  "name": "홍길동",
  "email": "user@example.com",
  "password": "root"
}
```

### 비교

| 항목 | 현재 회원가입 | 현재 로그인 |
|---|---|---|
| HTTP Method | POST | GET |
| Axios | `post(url, data)` | `get(url, { params })` |
| 데이터 위치 | JSON Body | URL Query String |
| Spring Mapping | `@PostMapping` | `@GetMapping` |
| 데이터 수신 | `@RequestBody` | `@RequestParam` |

GET은 조회를 위한 Method이며 서버 상태를 변경하지 않는 것이 원칙이다. POST는 생성이나 처리를 요청할 때 사용한다.

단, **POST=무조건 JSON, GET=무조건 @RequestParam이라는 규칙은 아니다.** POST도 Query Parameter를 가질 수 있고 Form 데이터를 보낼 수 있다. Annotation은 HTTP Method만이 아니라 데이터 위치와 형식에 맞춰 선택한다.

---

## 6. 요청 형식별 Annotation

### JSON Body → @RequestBody

```java
@PostMapping("/signUp")
public ResponseEntity<?> signUp(@RequestBody UserRequestDTO request) {
    // JSON → DTO
}
```

### Query String 개별 값 → @RequestParam

현재 로그인 코드다.

```java
@GetMapping("/signIn")
public ResponseEntity<?> singIn(
        @RequestParam("email") String email,
        @RequestParam("password") String password) {

    UserRequestDTO request = UserRequestDTO.builder()
            .email(email)
            .password(password)
            .build();

    Map<String, Object> map = userService.signIn(request);
    // 응답 구성
}
```

`@RequestParam`은 기본적으로 필수다. Parameter가 빠지면 요청 처리 단계에서 400 응답이 발생할 수 있다.

### Query String 여러 값을 DTO로 → @ModelAttribute

다음은 비교용 예시다. 현재 구현은 위의 개별 `@RequestParam` 방식이다.

```java
@GetMapping("/signIn")
public ResponseEntity<?> signIn(@ModelAttribute UserRequestDTO request) {
    // query/form 값 → DTO Property
}
```

일반적인 복합 DTO 매개변수는 `@ModelAttribute`를 생략해도 같은 방식으로 처리된다. JSON Body를 읽는 `@RequestBody`와는 다르다.

| 데이터 | 예시 | Annotation |
|---|---|---|
| JSON Body | `{"email":"..."}` | `@RequestBody` |
| Query 개별 값 | `?email=...` | `@RequestParam` |
| Query/Form DTO | `?email=...&name=...` | `@ModelAttribute` |
| URL 경로 값 | `/users/10` | `@PathVariable` |

---

## 7. 회원가입의 실제 코드 흐름

### Controller: HTTP를 담당

```java
int signUpFlag = userService.SingUp(request);

if (signUpFlag != 0) {
    return ResponseEntity.status(HttpStatus.CREATED).body(null);
} else {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
}
```

현재 Method 이름은 실제 코드처럼 `SingUp`이다. Java의 대소문자는 구분되므로 호출부와 선언부가 일치해야 한다.

### Service: Mapper에 위임

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;

    public int SingUp(UserRequestDTO request) {
        return userMapper.save(request);
    }
}
```

### Mapper와 XML

```java
@Mapper
public interface UserMapper {
    int save(UserRequestDTO request);
    Optional<UserResponseDTO> signIn(UserRequestDTO request);
}
```

```xml
<mapper namespace="com.example.inspire_mybatis.features.users.repository.UserMapper">
    <insert id="save"
            parameterType="com.example.inspire_mybatis.features.users.domain.dto.UserRequestDTO">
        INSERT INTO Spring_USER_TBL(EMAIL, PASSWORD, NAME)
        VALUES(#{email}, #{password}, #{name})
    </insert>
</mapper>
```

```text
FE form → POST JSON
  ↓
@RequestBody UserRequestDTO
  ↓
UserService.SingUp(request)
  ↓
UserMapper.save(request)
  ↓ namespace + id
<insert id="save">
  ↓ #{} 값 바인딩
DB INSERT → 반영 행 수 → HTTP 201
```

`#{email}`은 DTO Property를 SQL Parameter로 전달한다. 조회 결과의 컬럼 이름·별칭과 DTO Property를 맞추는 결과 Mapping과 구분해야 한다.

현재 else는 “반환 행 수가 0일 때”만 실행된다. 중복 키처럼 예외가 발생하면 else로 가지 않고 예외가 전파된다.

---

## 8. userMapper가 null이었던 이유: 생성자 주입

오류가 발생했던 형태는 다음과 같다.

```java
@RequiredArgsConstructor
public class UserService {
    private UserMapper userMapper;
}
```

`@RequiredArgsConstructor`는 초기화되지 않은 `final` Field와 `@NonNull` Field 등을 매개변수로 받는 생성자를 만든다. 일반 Field는 자동으로 주입하지 않는다.

현재는 다음처럼 수정돼 있다.

```java
private final UserMapper userMapper;
private final JwtProvider jwtProvider;
```

개념적으로 다음 생성자가 만들어진다.

```java
public UserService(UserMapper userMapper, JwtProvider jwtProvider) {
    this.userMapper = userMapper;
    this.jwtProvider = jwtProvider;
}
```

단일 생성자를 통해 Spring이 필요한 Bean을 주입한다.

```text
Controller 생성자 ← UserService Bean
Service 생성자    ← UserMapper Proxy + JwtProvider Bean
```

`@Service`는 객체를 Bean으로 등록하는 표시이지, 모든 Field에 자동으로 값을 넣는 Annotation은 아니다.

---

## 9. 로그인 조회와 Optional

Service는 로그인 결과가 존재해야 다음 단계로 넘어간다.

```java
UserResponseDTO response = userMapper
        .signIn(request)
        .orElseThrow(() -> new RuntimeException("로그인실패"));
```

```xml
<select id="signIn"
        parameterType="com.example.inspire_mybatis.features.users.domain.dto.UserRequestDTO"
        resultType="com.example.inspire_mybatis.features.users.domain.dto.UserResponseDTO">
    SELECT EMAIL, PASSWORD, NAME
    FROM Spring_USER_TBL
    WHERE EMAIL = #{email}
      AND PASSWORD = #{password}
</select>
```

```text
조회 결과 있음 → UserResponseDTO → 토큰 생성 단계
조회 결과 없음 → Optional.empty() → 예외 발생
```

`orElseThrow(() -> ...)`의 Lambda는 결과가 없을 때 만들 예외를 제공한다.

현재 `RuntimeException`에는 HTTP 401 매핑이 없다. 로그인 실패를 401 등으로 응답하려면 예외 처리 정책을 별도로 구현해야 한다.

현재 SQL은 평문 비밀번호 비교를 연습하는 코드다. 실제 서비스에서는 비밀번호 해시 저장과 검증이 필요하다.

---

## 10. JWT 발급 흐름 만들기: 현재는 임시 토큰

### 현재 JwtProvider

```java
@Component
public class JwtProvider {
    public String createAT(String email) {
        return "Bearer XXXXXXXXX";
    }

    public String createRT(String email) {
        return "XXXXXXXXXXXXXXXXX";
    }
}
```

`@Component`로 Bean을 등록해 Service에서 생성자 주입받는다.

중요한 점은 **현재 코드는 실제 JWT를 만드는 구현이 아니라 고정 문자열을 반환하는 Stub**이라는 것이다. email도 토큰 내용에 사용되지 않는다.

### Service에서 생성 결과 모으기

```java
String at = jwtProvider.createAT(request.getEmail());
String rt = jwtProvider.createRT(request.getEmail());

Map<String, Object> map = new HashMap<>();
map.put("response", response);
map.put("at", at);
map.put("rt", rt);

return map;
```

- `response`: 로그인한 사용자 DTO
- `at`: Access Token 역할의 임시 문자열
- `rt`: Refresh Token 역할의 임시 문자열

`Map<String, Object>`는 문자열 키로 여러 타입의 값을 담는다. 꺼낼 때 형변환이 필요하며 키 오타를 Compiler가 잡지 못하므로 구조가 확정되면 전용 결과 DTO로 바꿀 수 있다.

### JWT 개념과 아직 구현하지 않은 부분

일반적으로 사용하는 서명 JWT는 `Header.Payload.Signature` 구조다. 인코딩은 암호화가 아니므로 Payload에 비밀번호 같은 비밀정보를 넣으면 안 된다.

- Access Token: 보호된 API 접근에 사용
- Refresh Token: Access Token 재발급에 사용; 반드시 JWT 형식일 필요는 없음
- 현재 미구현: 실제 서명, 만료시간, 토큰 검증, 재발급 API, 폐기·회전 정책
- Service의 Redis/H2 주석은 계획일 뿐 실제 저장 코드가 아니다.
- `Bearer `는 HTTP 인증 방식 접두사이지 JWT 자체의 일부가 아니다.

---

## 11. ResponseEntity로 Header와 Body 나누기

현재 로그인 Controller의 응답 코드다.

```java
HttpHeaders headers = new HttpHeaders();
headers.add("Authorization", (String) map.get("at"));
headers.add("Refresh-Token", (String) map.get("rt"));
headers.add("Access-Control-Expose-Headers", "Authorization, Refresh-Token");

return ResponseEntity
        .status(HttpStatus.OK)
        .headers(headers)
        .body((UserResponseDTO) map.get("response"));
```

```text
HTTP Response
├── Status: 200 OK
├── Headers
│   ├── Authorization: Bearer XXXXXXXXX
│   ├── Refresh-Token: XXXXXXXXXXXXXXXXX
│   └── Access-Control-Expose-Headers: Authorization, Refresh-Token
└── Body: UserResponseDTO → JSON
```

`ResponseEntity<?>`는 응답 Body의 타입을 와일드카드로 열어 둔 표현이다.

이전의 `Retresh Token`은 오타와 공백 때문에 올바른 Header 이름이 아니었다. 현재는 `Refresh-Token`으로 변경돼 있다.

현재 응답 DTO에는 password도 들어 있으므로 API 응답에 노출될 수 있다. 토큰 전달 연습과 별개로 실제 응답에서는 제외해야 한다.

---

## 12. FE와 BE 연결: Axios baseURL

현재 `fe/react-app/src/api/axios.js`다.

```javascript
import axios from 'axios';

const endPoint = process.env.REACT_APP_BACKEND_ENDPOINT;

const api = axios.create({
    baseURL: endPoint
});

export default api;
```

FE 환경 변수 예시:

```dotenv
REACT_APP_BACKEND_ENDPOINT=http://localhost:8000
```

```javascript
api.get('/users/signIn', { params: { /* ... */ } });
```

```text
baseURL + 요청 경로
http://localhost:8000 + /users/signIn
```

React 개발 환경의 .env 값을 변경했다면 개발 서버를 재시작해 반영한다. 이 변수는 공개 가능한 API 주소를 담는 용도다.

---

## 13. CORS와 응답 Header 노출은 다른 설정

현재 `CorsConfig`다.

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOriginPatterns("http://localhost:3000")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS");
    }
}
```

FE는 3000, BE는 8000 Port를 사용하므로 Origin이 다르다.

- `@Configuration`: Spring 설정 Class
- `WebMvcConfigurer`: MVC 설정을 확장하는 Interface
- `addMapping("/**")`: 모든 URL에 적용
- `allowedOriginPatterns`: 허용할 FE Origin
- `allowedHeaders`: 요청에서 허용할 Header
- `allowedMethods`: 허용할 HTTP Method

JSON POST 같은 요청은 브라우저가 먼저 OPTIONS Preflight를 보낼 수 있다.

`Access-Control-Expose-Headers`는 다른 목적이다. 응답이 도착하더라도 브라우저 JavaScript가 읽을 수 있는 응답 Header에 제한이 있으므로 토큰 Header를 노출한다.

```text
CORS Origin 허용
→ 다른 Origin에서 응답에 접근 가능

Expose-Headers
→ JavaScript가 Authorization / Refresh-Token을 읽을 수 있음
```

`allowedHeaders("*")`와 응답 Header 노출은 서로 대체할 수 없다.

---

## 14. React 회원가입과 로그인 결과 처리

### 회원가입

현재 흐름을 핵심만 발췌했다.

```javascript
const data = { ...form };

await api.post('/users/signUp', data)
    .then(response => {
        if (response.status === 201) {
            moveUrl('/users/signIn');
        }
    })
    .catch(error => {
        console.log(error);
    });
```

`{ ...form }`은 form의 속성을 복사하는 전개 문법이다. `useNavigate()`로 받은 함수가 FE 로그인 화면으로 이동시킨다.

### 로그인

```javascript
await api.get('/users/signIn', {
    params: {
        email: form.email,
        password: form.password
    }
})
.then(response => {
    if (response.status === 200) {
        localStorage.setItem('user', response.data.email);
        localStorage.setItem('at', response.headers.get("Authorization"));
        localStorage.setItem('rt', response.headers.get("Refresh-Token"));
    }
})
.catch(error => {
    console.log(error);
});
```

- `response.status`: HTTP 상태 코드
- `response.data`: 응답 JSON Body
- `response.headers`: 응답 Header
- `localStorage.setItem()`: 브라우저에 문자열 저장

현재 Axios 코드의 `headers.get()`으로 AT·RT를 읽는다. Body의 email과 Header의 토큰은 서로 다른 위치에서 꺼낸다.

### Form 관련 문법

```javascript
const { name, value } = e.target;
setForm({ ...form, [name]: value });
```

기존 입력값을 유지하면서 변경된 Input의 name 속성에 해당하는 Field만 갱신한다. `[name]`은 계산된 속성명이다.

`e.preventDefault()`는 Form 제출의 기본 페이지 이동을 막고 Axios로 처리하게 한다.

현재 코드는 `await`와 `.then()`을 같이 사용한다. 동작은 가능하지만 비동기 코드를 읽기 쉽게 하려면 `async/await + try/catch` 또는 Promise 체인 중 한 방식으로 통일할 수 있다.

### 저장했다고 인증 구현이 완료되는 것은 아니다

현재 FE는 임시 문자열을 저장할 뿐 이후 API에 토큰을 첨부하는 인터셉터나 서버 검증은 확인되지 않는다. localStorage는 JavaScript에서 읽을 수 있어 XSS에 노출되면 토큰도 탈취될 수 있다. 운영 토큰 저장 정책은 별도 보안 설계가 필요하다.

---

## 15. 전체 요청·응답 흐름

### 회원가입

```text
React form
  ↓ POST JSON
@RequestBody UserRequestDTO
  ↓
UserService.SingUp()
  ↓
UserMapper.save()
  ↓
UserMapper.xml INSERT
  ↓
MariaDB
  ↓ 반영 행 수
HTTP 201
  ↓
React에서 로그인 페이지 이동
```

### 로그인

```text
React form
  ↓ Axios GET params
/users/signIn?email=...&password=...
  ↓
@RequestParam email/password
  ↓ Builder
UserRequestDTO
  ↓
UserService.signIn()
  ↓
UserMapper.signIn() → XML SELECT → MariaDB
  ↓
Optional<UserResponseDTO>
  ├─ 없음: 예외
  └─ 있음: createAT() / createRT()
                ↓
       Map(response, at, rt)
                ↓
       Controller ResponseEntity
         ├─ Header: AT / RT
         └─ Body: 사용자 DTO
                ↓ CORS + Expose-Headers
       React Axios response
                ↓
       localStorage 저장
```

---

## 16. 오늘의 핵심과 다음 구현 단계

| 배운 내용 | 현재 코드에서 의미 |
|---|---|
| .env | DB 설정을 외부화하고 JVM 속성을 통해 YAML에 전달 |
| Swagger | API 설명과 요청 테스트; 실제 로직을 대신하지 않음 |
| POST JSON | 회원가입, Spring @RequestBody로 DTO 변환 |
| GET params | 로그인 비교 실습, @RequestParam으로 수신 |
| 생성자 주입 | @RequiredArgsConstructor + final |
| MyBatis | Interface Method와 XML namespace/id 연결 |
| Optional | 로그인 결과 없음 처리 |
| JwtProvider | 현재는 고정 문자열; 발급 흐름만 연습 |
| ResponseEntity | 상태 코드·Header·Body 구성 |
| CORS | FE와 BE의 다른 Origin 연결 |
| Expose-Headers | FE에서 토큰 응답 Header 읽기 |
| localStorage | 전달받은 문자열 저장; 인증 완성과는 별개 |

다음 단계에서는 실제 토큰 생성·검증, 비밀번호 해시, 실패 응답 처리, 비밀번호 로그·응답 제거, GET 로그인에서 POST 로그인으로 전환 등을 구분해서 구현할 수 있다.

오늘 정리는 **환경을 준비하고, 요청 형식을 맞추고, DB 처리 결과를 토큰과 함께 FE로 전달하는 연결 과정**에 초점을 맞춘다.
