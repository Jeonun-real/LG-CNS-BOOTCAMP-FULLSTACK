# BE 15일차 — Spring Boot 기본 구조, Annotation, MyBatis와 Test

## 1. 오늘 실습 프로젝트

오늘은 Gradle 기반 Spring Boot 프로젝트 `testcase`를 만들고 다음 내용을 실습했다.

- Spring Boot 프로젝트의 구조와 실행 과정
- Spring Container, Bean, 의존성 주입
- Controller, Service, Mapper 계층 분리
- DTO와 요청 Parameter Binding
- MyBatis Interface와 XML SQL 연결
- JUnit, `@SpringBootTest`, Given–When–Then 테스트
- Profile, DataSource, HikariCP
- Health Check API

```text
testcase
├── build.gradle
└── src
    ├── main
    │   ├── java/com/example/testcase
    │   │   ├── TestcaseApplication.java
    │   │   └── features
    │   │       ├── test/ctrl/HealthController.java
    │   │       └── users
    │   │           ├── ctrl/UserController.java
    │   │           ├── domain/dto
    │   │           ├── repository/UserMapper.java
    │   │           └── service
    │   └── resources
    │       ├── application.yml
    │       ├── application-dev.yml
    │       └── mappers/userMapper.xml
    └── test/java/com/example/testcase
        ├── TestcaseApplicationTests.java
        └── UserApplicationTests.java
```

---

## 2. Gradle과 프로젝트 실행

Gradle은 `build.gradle`에 선언된 라이브러리를 내려받고 Compile, Test, 실행, Packaging을 담당한다.

```text
build.gradle
    ↓ Dependency 확인
Maven Central
    ↓
Gradle Cache
    ↓
Compile · Test · BootRun · JAR 생성
```

현재 프로젝트의 주요 Dependency다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-webmvc'
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.1'
runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
compileOnly 'org.projectlombok:lombok'
developmentOnly 'org.springframework.boot:spring-boot-devtools'
testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
testImplementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter-test:4.0.1'
```

- Web MVC: DispatcherServlet, JSON 변환, 내장 Tomcat
- JPA: JPA와 Hibernate 기반 데이터 접근
- MyBatis: Mapper Interface와 XML SQL 연결
- MariaDB Driver: MariaDB JDBC 구현체
- Lombok: Builder, Getter 등을 Compile 시 생성
- DevTools: 개발 중 자동 재시작
- Test Dependency: `src/test/java`의 Spring Test와 JUnit 실행

현재 설치된 JDK에 맞춰 Java 21 Toolchain을 사용한다.

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

---

## 3. Spring Boot 시작점

```java
@SpringBootApplication
public class TestcaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestcaseApplication.class, args);
    }
}
```

`main()`이 Java 프로그램의 시작점이고 `SpringApplication.run()`이 Spring Container와 내장 Tomcat을 실행한다.

```text
main()
  ↓
SpringApplication.run()
  ↓
설정 및 Classpath 확인
  ↓
Component Scan
  ↓
Bean 생성과 의존성 주입
  ↓
DataSource · MyBatis · JPA 초기화
  ↓
내장 Tomcat 실행
```

`@SpringBootApplication`은 다음 기능을 묶은 Annotation이다.

```text
@SpringBootConfiguration
+ @EnableAutoConfiguration
+ @ComponentScan
= @SpringBootApplication
```

- Configuration: Spring 설정의 시작점
- Auto Configuration: Dependency와 설정을 보고 필요한 객체 자동 구성
- Component Scan: 현재 Package와 하위 Package의 Component 탐색

시작 Class가 `com.example.testcase`에 있으므로 `features.test`, `features.users` 등 하위 Package가 Scan 대상이다.

---

## 4. Spring Container와 Bean

일반 Java에서는 필요한 객체를 직접 생성한다.

```java
UserService service = new UserPlainServiceImpl();
```

Spring에서는 Annotation으로 등록 대상을 표시하면 Container가 객체를 생성하고 관리한다. 이 객체를 Bean이라고 한다.

```text
Class + Annotation
    ↓ Component Scan
Spring이 객체 생성
    ↓
Container에 Bean 등록
    ↓
필요한 곳에 의존성 주입
```

Spring Bean은 기본적으로 Singleton Scope다. 개발자가 별도의 Singleton Factory를 만들지 않아도 Container가 하나의 객체와 생명주기를 관리한다.

| Annotation | 계층 | 역할 |
|---|---|---|
| `@Component` | 공통 | 일반 Class를 Bean으로 등록 |
| `@Controller` | Presentation | 요청 처리 후 주로 View 반환 |
| `@RestController` | Presentation | 요청 처리 후 데이터·JSON 반환 |
| `@Service` | Business | 비즈니스 로직 Bean 등록 |
| `@Repository` | Persistence | 데이터 접근 Bean, 예외 변환 |
| `@Mapper` | Persistence | MyBatis Mapper Proxy 등록 |

---

## 5. Controller와 URL Mapping

### Health Check 실제 코드

```java
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/alive")
    public String isAlive() {
        return "I'm alive!";
    }
}
```

Class와 Method의 Mapping을 합쳐 전체 URL을 만든다.

```text
@RequestMapping("/health")
+ @GetMapping("/alive")
= GET http://localhost:8000/health/alive
```

응답은 `I'm alive!`다. Health Check는 서버가 HTTP 요청을 정상 처리하는지 확인하는 API다.

| Annotation | HTTP 의미 |
|---|---|
| `@GetMapping` | 조회 |
| `@PostMapping` | 생성 |
| `@PutMapping` | 전체 수정 |
| `@PatchMapping` | 일부 수정 |
| `@DeleteMapping` | 삭제 |

`@Controller`의 문자열 반환은 일반적으로 View 이름으로 해석된다.

```text
"index" → prefix + index + suffix → /WEB-INF/view/index.jsp
```

`@RestController`의 반환값은 Response Body에 기록된다.

```text
@Controller + @ResponseBody = @RestController
```

---

## 6. DispatcherServlet과 요청 구조

요청은 Controller로 바로 들어가지 않고 Front Controller인 `DispatcherServlet`을 거친다.

```text
Client
  ↓ HTTP Request
Tomcat
  ↓
DispatcherServlet
  ↓ URL과 Method 탐색
Controller
  ↓
Service
  ↓
Mapper
  ↓
Database
```

DispatcherServlet은 URL Mapping, Parameter Binding, 요청·응답 변환, 예외 처리, View 또는 JSON 변환 같은 공통 작업을 담당한다.

React가 `localhost:3000`, Spring이 `localhost:8000`에서 실행되면 Origin이 다르므로 통신 시 CORS 설정이 필요할 수 있다.

---

## 7. 사용자 기능의 계층 구조

```text
UserController
    ↓ HTTP 요청 전달
UserService
    ↓ 기능 표준
UserPlainServiceImpl 또는 UserEncryptionServiceImpl
    ↓ 데이터 접근 요청
UserMapper
    ↓ XML SQL 실행
MariaDB
```

| 계층 | 현재 코드 | 책임 |
|---|---|---|
| Presentation | `UserController` | 요청과 응답 처리 |
| Business | `UserService`, 구현체 | 로그인 등 비즈니스 로직 |
| Persistence | `UserMapper`, XML | SQL 실행과 결과 Mapping |
| DTO | Request/Response DTO | 계층 간 데이터 전달 |

---

## 8. Interface와 다형성

```java
public interface UserService {
    UserResponseDTO signIn(UserRequestDTO request);
}
```

Interface는 기능의 매개변수와 반환 타입을 정의하고 구현 Class가 실제 처리 방법을 결정한다.

```java
@Service
public class UserPlainServiceImpl implements UserService {
    @Override
    public UserResponseDTO signIn(UserRequestDTO request) {
        return null;
    }
}
```

```java
@Service
public class UserEncryptionServiceImpl implements UserService {
    @Override
    public UserResponseDTO signIn(UserRequestDTO request) {
        return null;
    }
}
```

```text
                 ┌─ UserPlainServiceImpl
UserService  ←───┤
                 └─ UserEncryptionServiceImpl
```

Controller가 구현체가 아닌 `UserService`에 의존하면 구현 방법을 교체하기 쉬워지고 결합도가 낮아진다.

---

## 9. 의존성 주입과 Bean 선택

```java
@Autowired
private UserMapper userMapper;
```

`@Autowired`는 기본적으로 타입을 기준으로 Bean을 찾는다. MyBatis가 만든 `UserMapper` Proxy Bean이 주입된다.

반면 현재 Controller는 다음과 같다.

```java
@Resource(name = "encryption")
private UserService userService;
```

`@Resource(name=...)`는 지정한 이름의 Bean을 찾는다. 이름을 따로 지정하지 않은 `UserEncryptionServiceImpl`의 기본 Bean 이름은 Class 첫 글자를 소문자로 바꾼 `userEncryptionServiceImpl`이다.

```text
@Service + UserEncryptionServiceImpl
→ 기본 Bean 이름: userEncryptionServiceImpl
```

따라서 `@Resource(name="encryption")`을 쓰려면 실제로 `encryption`이라는 이름의 Bean이 등록돼 있어야 한다. 같은 `UserService` 구현 Bean이 둘이므로 `@Qualifier`, `@Primary` 또는 명시적인 Bean 이름으로 주입 대상을 구분해야 한다.

---

## 10. Request DTO와 Response DTO

### 요청 DTO

```java
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private String email, password, name;
}
```

```java
UserRequestDTO request = UserRequestDTO.builder()
        .email("tjwjddns1302@gmail.com")
        .password("root")
        .name("tjwddns")
        .build();
```

### 응답 DTO

```java
@Builder
@Getter
@ToString
public class UserResponseDTO {
    private String email, password, name;
}
```

Request DTO는 Client 입력과 검색·저장 조건을 전달하고 Response DTO는 조회·처리 결과를 전달한다. 실제 서비스에서는 비밀번호를 응답에 포함하지 않는 편이 안전하다.

| Lombok Annotation | 기능 |
|---|---|
| `@Builder` | Builder Pattern 생성 |
| `@Getter`, `@Setter` | 접근자 생성 |
| `@ToString` | 문자열 표현 생성 |
| `@NoArgsConstructor` | 기본 생성자 생성 |
| `@AllArgsConstructor` | 전체 Field 생성자 생성 |

---

## 11. Query Parameter와 DTO Binding

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/signIn")
    public ResponseEntity<?> signIn(UserRequestDTO request) {
        System.out.println(request);
        return null;
    }
}
```

다음 요청이 들어오면 Spring MVC가 Parameter 이름과 DTO Property 이름을 기준으로 Binding한다.

```text
GET /users/signIn?email=user@test.com&password=root

email    → request.email
password → request.password
```

`ResponseEntity<?>`는 HTTP 상태 코드, Header, Body를 함께 표현한다.

---

## 12. Profile과 YAML

```yaml
# application.yml
spring:
    profiles:
        default: dev
```

```yaml
# application-dev.yml
server:
    port: 8000

spring:
    config:
        activate:
            on-profile: dev

    datasource:
        driver-class-name: org.mariadb.jdbc.Driver
        url: jdbc:mariadb://localhost:3306/lgcns
        username: root
        password: root
```

```text
application.yml
  ↓ default: dev
application-dev.yml 활성화
  ↓
Port 8000 + MariaDB lgcns 연결
```

Profile을 이용하면 개발과 운영 환경의 DB 주소, Port 등을 분리할 수 있다.

---

## 13. DataSource와 HikariCP

Spring Boot가 `spring.datasource`와 JDBC Driver를 발견하면 DataSource를 자동 구성하며 기본 Connection Pool로 HikariCP를 사용한다.

```text
DataSource 설정 발견
  ↓
MariaDB Driver 로딩
  ↓
HikariCP 생성
  ↓
DB Connection을 Pool에 보관
```

```text
요청 → Connection 대여 → SQL 실행 → Pool에 반환
```

```text
HikariPool-1 - Starting...
HikariPool-1 - Added connection ...
HikariPool-1 - Start completed.
```

위 로그는 Pool이 DB Connection을 만들고 정상 시작했다는 뜻이다.

---

## 14. MyBatis Mapper Interface

```java
@Mapper
public interface UserMapper {
    int save(UserRequestDTO request);
    List<UserResponseDTO> findByAll();
    Optional<UserResponseDTO> login(UserRequestDTO request);
}
```

`@Mapper`를 붙이면 MyBatis가 Interface의 구현 Proxy를 만들어 Spring Bean으로 등록한다. 개발자가 구현 Class를 직접 만들지 않아도 된다.

- `int`: INSERT·UPDATE·DELETE 반영 행 수
- `List<UserResponseDTO>`: 여러 행 조회
- `Optional<UserResponseDTO>`: 없을 수 있는 단일 행 조회

---

## 15. Mapper XML Binding

```yaml
mybatis:
    mapper-locations: classpath:/mappers/**/*Mapper.xml
```

`mybatis`는 `spring` 하위가 아니라 YAML 최상위에 있어야 한다.

Java와 XML은 다음 값이 일치해야 한다.

```text
Interface 전체 경로 ↔ XML namespace
Method 이름         ↔ SQL id
매개변수 타입       ↔ parameterType
반환 객체 타입      ↔ resultType
```

```java
public interface UserMapper {
    int save(UserRequestDTO request);
}
```

```xml
<mapper namespace="com.example.testcase.features.users.repository.UserMapper">
    <insert id="save"
            parameterType="com.example.testcase.features.users.domain.dto.UserRequestDTO">
        INSERT INTO Spring_USER_TBL(EMAIL, PASSWORD, NAME)
        VALUES(#{email}, #{password}, #{name})
    </insert>
</mapper>
```

```text
UserMapper.save(request)
  ↓ namespace + id
com.example.testcase.features.users.repository.UserMapper.save
  ↓
INSERT 실행
```

`#{email}` 등은 DTO Property를 PreparedStatement Parameter로 Binding한다.

```text
#{email}    ← request.getEmail()
#{password} ← request.getPassword()
#{name}     ← request.getName()
```

---

## 16. 저장·조회·로그인 SQL

### 회원 저장

```xml
<insert id="save" parameterType="...UserRequestDTO">
    INSERT INTO Spring_USER_TBL(EMAIL, PASSWORD, NAME)
    VALUES(#{email}, #{password}, #{name})
</insert>
```

```java
int flag = userMapper.save(request);
```

한 행을 저장하면 일반적으로 `1`이 반환된다. 같은 Primary Key Email을 다시 저장하면 `DuplicateKeyException`이 발생한다.

### 전체 조회

```xml
<select id="findByAll" resultType="...UserResponseDTO">
    SELECT EMAIL, PASSWORD, NAME
    FROM Spring_USER_TBL
</select>
```

```java
List<UserResponseDTO> list = userMapper.findByAll();
```

### 로그인

```xml
<select id="login"
        parameterType="...UserRequestDTO"
        resultType="...UserResponseDTO">
    SELECT EMAIL, PASSWORD, NAME
    FROM Spring_USER_TBL
    WHERE EMAIL = #{email}
      AND PASSWORD = #{password}
</select>
```

```java
Optional<UserResponseDTO> response = userMapper.login(request);
```

일치하는 회원이 있으면 DTO, 없으면 빈 `Optional`을 반환할 수 있다.

---

## 17. `@SpringBootTest`와 JUnit

테스트 Class는 `src/test/java`에 둔다. Test Dependency는 Test Source Set에 적용되기 때문이다.

```java
@SpringBootTest
class TestcaseApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

`@SpringBootTest`는 실제 Spring ApplicationContext를 실행한다.

```text
@SpringBootTest
  ↓ 시작 Class 탐색
Bean 생성과 주입
  ↓
DataSource · MyBatis 초기화
  ↓
@Test Method 실행
```

단순 Java 단위 테스트보다 무겁지만 Bean과 DB 연결을 포함한 통합 테스트가 가능하다. `@Test`는 JUnit이 실행할 Method를 표시한다.

---

## 18. Given–When–Then

```text
Given : 데이터와 조건 준비
When  : 검증할 기능 실행
Then  : 결과 검증
```

### 회원가입 테스트

```java
@Test
public void signUp() {
    // given
    UserRequestDTO request = UserRequestDTO.builder()
            .email("tjwjddns1302@gmail.com")
            .password("root")
            .name("tjwddns")
            .build();

    // when
    int flag = userMapper.save(request);

    // then
    Assertions.assertEquals(1, flag);
}
```

### 전체 조회 테스트

```java
@Test
public void list() {
    List<UserResponseDTO> list = userMapper.findByAll();

    list.stream()
        .forEach(System.out::println);
}
```

현재는 출력으로 확인하지만 목록 크기나 특정 데이터 포함 여부를 Assertion으로 검증하면 더 명확하다.

### 로그인 테스트

```java
@Test
public void signIn() {
    // given
    UserRequestDTO request = UserRequestDTO.builder()
            .email("tjwjddns1302@gmail.com")
            .password("root")
            .build();

    // when
    Optional<UserResponseDTO> response = userMapper.login(request);

    // then
    Assertions.assertNotNull(response.get());
    Assertions.assertEquals(
            "tjwjddns1302@gmail.com",
            response.get().getEmail()
    );
}
```

`Optional.get()`은 값이 없으면 예외가 발생하므로 존재 여부를 먼저 검증하거나 `orElseThrow()`를 사용할 수 있다.

---

## 19. 오늘 확인한 오류

### Java Toolchain

```text
Cannot find a Java installation matching languageVersion=17
```

프로젝트 요구 버전과 설치 JDK가 달라 발생했다. Java 21 Toolchain으로 맞췄다.

### `Main method not found`

`main()` 부재가 아니라 Toolchain 오류로 Gradle Import가 실패하면서 VS Code가 시작 Class를 인식하지 못해 파생된 표시였다.

### Test Annotation 미인식

Test Class가 `src/main/java`에 있으면 Test Dependency가 적용되지 않는다.

```text
src/main/java → 애플리케이션 코드
src/test/java → 테스트 코드
```

### MyBatis BindingException

```text
Invalid bound statement (not found): UserMapper.login
```

Interface Method에 대응하는 XML SQL을 등록하지 못했다는 뜻이다. 다음을 확인한다.

- `mapper-locations`와 실제 XML 위치
- YAML에서 `mybatis`의 들여쓰기
- Interface 전체 경로와 XML `namespace`
- Method 이름과 SQL `id`
- XML 저장 및 Resource 재Build 여부

### DuplicateKeyException

```text
Duplicate entry 'tjwjddns1302@gmail.com' for key 'PRIMARY'
```

회원가입 Test를 반복하면서 동일 Email을 다시 INSERT하여 발생한다. Test Data 정리나 Transaction Rollback 전략이 필요하다.

### 404 Not Found

요청 URL과 Mapping이 일치하지 않거나 JSP 경로와 ViewResolver 설정이 맞지 않을 때 발생한다. REST API에서는 Class와 Method Mapping을 합친 전체 URL을 확인한다.

---

## 20. 전체 데이터 흐름

```text
Client
  │ GET /users/signIn?email=...&password=...
  ▼
Tomcat
  ▼
DispatcherServlet
  ▼
UserController
  │ Parameter → UserRequestDTO Binding
  ▼
UserService
  ▼
UserPlainServiceImpl 또는 UserEncryptionServiceImpl
  ▼
UserMapper.login(request)
  ▼
userMapper.xml의 <select id="login">
  ▼
HikariCP에서 Connection 대여
  ▼
MariaDB Spring_USER_TBL 조회
  ▼
UserResponseDTO Mapping
  ▼
Service → Controller → HTTP Response
```

현재 Controller와 Service의 반환 부분은 아직 `null`이므로 계층 연결을 만드는 단계이며, Mapper Test로 DB 접근 계층부터 검증하고 있다.

---

## 21. 핵심 Annotation 표

| Annotation | 현재 위치 | 의미 |
|---|---|---|
| `@SpringBootApplication` | 시작 Class | 자동 설정과 Component Scan |
| `@RestController` | Controller | 데이터 응답 Controller |
| `@RequestMapping` | Controller Class | 공통 URL |
| `@GetMapping` | Controller Method | GET 요청 Mapping |
| `@Service` | Service 구현체 | Business Bean 등록 |
| `@Mapper` | Mapper Interface | MyBatis Proxy Bean 생성 |
| `@Autowired` | Test Field | 타입 기준 의존성 주입 |
| `@Resource` | Controller Field | 이름 중심 의존성 주입 |
| `@SpringBootTest` | Test Class | Spring Context 통합 테스트 |
| `@Test` | Test Method | JUnit Test 표시 |
| `@Builder` | DTO | Builder 생성 |
| `@Getter`, `@Setter` | DTO | 접근자 생성 |

---

## 22. 오늘의 핵심 정리

1. `SpringApplication.run()`이 Container와 내장 WAS를 실행한다.
2. `@SpringBootApplication`은 자동 설정과 Component Scan의 시작점이다.
3. Spring은 Annotation이 붙은 객체를 Bean으로 생성하고 관리한다.
4. Controller, Service, Mapper 분리로 요청, 비즈니스, 데이터 접근 책임을 나눈다.
5. Interface 타입으로 의존하면 구현체 교체가 쉽지만 구현 Bean이 여러 개면 주입 대상을 지정해야 한다.
6. MyBatis는 Interface의 Method와 XML의 `namespace`, `id`를 연결한다.
7. DataSource 설정 시 Spring Boot가 HikariCP와 DB 연결을 자동 구성한다.
8. `@SpringBootTest`는 Spring Context, Bean, DB 연동을 함께 검증한다.
9. Test는 `src/test/java`에 두고 Given–When–Then으로 작성한다.
10. Test 성공 여부는 출력문보다 Assertion으로 판단한다.
