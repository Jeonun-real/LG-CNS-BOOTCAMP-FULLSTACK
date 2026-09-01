# [LG CNS AM INSPIRE CAMP] TIL 15일차 — Spring Boot Health Check부터 MyBatis Test와 의존성 주입까지

오늘은 Spring Boot 프로젝트 `testcase`를 새로 만들고, 서버가 정상적으로 동작하는지 확인하는 Health Check부터 시작해 MyBatis로 회원 데이터를 저장·조회하고 Controller에서 여러 Service 구현체를 선택하는 구조까지 실습했다.

단순히 Annotation을 하나씩 외우기보다 프로그램이 만들어진 순서대로 정리하면 전체 흐름을 이해하기 쉽다.

```text
1. Spring Boot 프로젝트 실행
   ↓
2. Health Check API로 서버 동작 확인
   ↓
3. DTO로 요청·응답 데이터 구조 정의
   ↓
4. UserMapper Interface에 DB 기능 정의
   ↓
5. Mapper XML에 실제 SQL 작성
   ↓
6. @SpringBootTest로 Mapper 기능 검증
   ↓
7. UserService Interface와 구현체 작성
   ↓
8. UserController에서 Service를 주입받아 사용
```

---

## 1. 프로젝트 시작점과 Spring Boot 실행

Spring Boot 프로젝트의 시작점은 `TestcaseApplication`이다.

```java
package com.example.testcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestcaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestcaseApplication.class, args);
    }
}
```

일반 Java 프로그램과 마찬가지로 `main()`에서 시작하지만, `SpringApplication.run()`이 실행되면서 Spring Container와 내장 Tomcat이 함께 시작된다.

```text
main()
  ↓
SpringApplication.run()
  ↓
설정 파일 읽기
  ↓
Component Scan
  ↓
Bean 생성과 의존성 주입
  ↓
내장 Tomcat 실행
```

### `@SpringBootApplication`

`@SpringBootApplication`은 Spring Boot 애플리케이션의 시작 설정이다.

```text
@SpringBootConfiguration
+ @EnableAutoConfiguration
+ @ComponentScan
= @SpringBootApplication
```

- `@SpringBootConfiguration`: Spring 설정 Class임을 표시한다.
- `@EnableAutoConfiguration`: Dependency와 설정을 바탕으로 필요한 Bean을 자동 구성한다.
- `@ComponentScan`: 시작 Class가 있는 Package와 그 하위 Package에서 Component를 찾는다.

현재 시작 Class의 Package가 `com.example.testcase`이므로 다음 Class들이 Scan 대상이 된다.

```text
com.example.testcase
├── TestcaseApplication
└── features
    ├── test.ctrl.HealthController
    └── users
        ├── ctrl.UserController
        ├── repository.UserMapper
        └── service.*
```

Spring이 정상적으로 시작되면 로그가 끝난 것이 아니라 서버가 요청을 기다리는 상태가 된다.

```text
Started TestcaseApplication ...
```

---

## 2. 가장 먼저 만든 Health Check

프로젝트가 실행된 다음 가장 먼저 만든 기능은 Health Check다.

```java
package com.example.testcase.features.test.ctrl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/alive")
    public String isAlive() {
        return "I'm alive!";
    }
}
```

서버 실행 후 다음 주소로 요청한다.

```text
GET http://localhost:8000/health/alive
```

응답은 다음과 같다.

```text
I'm alive!
```

### URL이 만들어지는 방식

Class Level Mapping과 Method Level Mapping을 합쳐 하나의 Endpoint가 된다.

```text
@RequestMapping("/health")
          +
@GetMapping("/alive")
          ↓
GET /health/alive
```

### 여기서 사용한 Annotation

#### `@RestController`

해당 Class를 요청을 처리하는 Controller Bean으로 등록하며, Method 반환값을 HTTP Response Body에 담는다.

```text
@Controller + @ResponseBody = @RestController
```

#### `@RequestMapping`

Class나 Method에 공통 URL을 지정한다. 현재는 `/health`가 Class 전체의 공통 경로다.

#### `@GetMapping`

HTTP GET 요청을 특정 Method와 연결한다. 조회나 상태 확인에 주로 사용한다.

Health Check가 성공했다는 것은 다음 흐름이 정상이라는 의미다.

```text
Browser
  ↓ GET /health/alive
Tomcat
  ↓
DispatcherServlet
  ↓ Mapping 탐색
HealthController.isAlive()
  ↓
HTTP Response: I'm alive!
```

---

## 3. 사용자 데이터 구조를 DTO로 정의

서버 동작을 확인한 다음 회원 저장과 조회에 사용할 데이터 구조를 DTO로 만들었다.

### UserRequestDTO

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

`UserRequestDTO`는 사용자에게 받은 값 또는 Mapper에 전달할 조건을 담는다.

```java
UserRequestDTO request = UserRequestDTO.builder()
        .email("tjwjddns1302@gmail.com")
        .password("root")
        .name("tjwddns")
        .build();
```

### UserResponseDTO

```java
@Builder
@Getter
@ToString
public class UserResponseDTO {
    private String email, password, name;
}
```

`UserResponseDTO`는 DB 조회 결과나 Service 처리 결과를 반환할 때 사용한다.

```text
Client 입력
  ↓
UserRequestDTO
  ↓
Controller · Service · Mapper
  ↓
Database 조회 결과
  ↓
UserResponseDTO
  ↓
Client 응답
```

요청과 응답 DTO를 분리하면 입력 가능한 Field와 반환할 Field를 각각 관리할 수 있다. 실제 서비스에서는 비밀번호를 Response DTO에 넣지 않는 편이 안전하다.

### Lombok 주요 문법

| Annotation | 생성되는 기능 |
|---|---|
| `@Builder` | Builder Pattern 객체 생성 |
| `@Getter` | Getter 생성 |
| `@Setter` | Setter 생성 |
| `@ToString` | 객체 출력 문자열 생성 |
| `@NoArgsConstructor` | 기본 생성자 생성 |
| `@AllArgsConstructor` | 모든 Field를 받는 생성자 생성 |

Builder를 사용하면 생성자 Parameter 순서를 외우지 않아도 Field 이름을 보며 객체를 만들 수 있다.

```java
UserRequestDTO.builder()
    .email("user@test.com")
    .password("root")
    .build();
```

---

## 4. UserMapper에 필요한 DB 기능 정의

DTO를 만든 다음 `UserMapper` Interface에 회원 기능을 정의했다.

```java
@Mapper
public interface UserMapper {

    int save(UserRequestDTO request);

    List<UserResponseDTO> findByAll();

    Optional<UserResponseDTO> login(UserRequestDTO request);
}
```

여기에는 구현 코드가 없고 필요한 기능의 이름, 매개변수, 반환 타입만 있다.

```text
save()      → 회원 저장
findByAll() → 회원 전체 조회
login()     → Email과 Password가 일치하는 회원 조회
```

### `@Mapper`

`@Mapper`가 붙은 Interface는 MyBatis가 구현 Proxy 객체를 자동으로 만든다.

```text
UserMapper Interface
  ↓ @Mapper
MyBatis가 Proxy 구현체 생성
  ↓
Spring Bean으로 등록
  ↓
@Autowired로 주입 가능
```

따라서 `UserMapperImpl` 같은 구현 Class를 직접 만들 필요가 없다.

### 반환 타입 해석

```java
int save(UserRequestDTO request);
```

`INSERT`, `UPDATE`, `DELETE`는 반영된 행 수를 `int`로 받을 수 있다. 한 명이 정상 저장되면 보통 `1`이다.

```java
List<UserResponseDTO> findByAll();
```

여러 회원을 조회하므로 `List`로 받는다. 결과가 없으면 빈 List가 반환된다.

```java
Optional<UserResponseDTO> login(UserRequestDTO request);
```

로그인 정보가 일치하는 사용자가 없을 수 있으므로 `Optional`로 표현한다.

---

## 5. Mapper XML에 실제 SQL 연결

Interface가 기능의 표준이라면 실제 SQL은 `userMapper.xml`에 작성한다.

```xml
<mapper namespace="com.example.testcase.features.users.repository.UserMapper">
```

`namespace`에는 연결할 Mapper Interface의 전체 Package 경로를 적는다.

### 회원 저장: `save`

```xml
<insert id="save"
        parameterType="com.example.testcase.features.users.domain.dto.UserRequestDTO">
    INSERT INTO Spring_USER_TBL(EMAIL, PASSWORD, NAME)
    VALUES(#{email}, #{password}, #{name})
</insert>
```

```text
UserMapper.save(request)
  ↓
namespace.UserMapper + id.save 탐색
  ↓
INSERT 실행
```

### 전체 조회: `findByAll`

```xml
<select id="findByAll"
        resultType="com.example.testcase.features.users.domain.dto.UserResponseDTO">
    SELECT EMAIL, PASSWORD, NAME
    FROM Spring_USER_TBL
</select>
```

조회된 각 행을 `UserResponseDTO`로 Mapping하고 전체 결과를 `List`로 반환한다.

### 로그인: `login`

```xml
<select id="login"
        parameterType="com.example.testcase.features.users.domain.dto.UserRequestDTO"
        resultType="com.example.testcase.features.users.domain.dto.UserResponseDTO">
    SELECT EMAIL, PASSWORD, NAME
    FROM Spring_USER_TBL
    WHERE EMAIL = #{email}
      AND PASSWORD = #{password}
</select>
```

`UserRequestDTO`의 Email과 Password를 조건으로 회원 한 명을 조회한다.

### MyBatis Binding 규칙

```text
Java Interface 전체 경로 ↔ XML namespace
Java Method 이름          ↔ XML SQL id
Java 매개변수 타입        ↔ parameterType
Java 반환 객체 타입       ↔ resultType
```

이 중 하나라도 맞지 않으면 다음과 같은 BindingException이 발생할 수 있다.

```text
Invalid bound statement (not found): UserMapper.login
```

### `#{}` 문법

```sql
VALUES(#{email}, #{password}, #{name})
```

MyBatis가 DTO Property를 PreparedStatement Parameter로 Binding한다.

```text
#{email}    ← request.getEmail()
#{password} ← request.getPassword()
#{name}     ← request.getName()
```

문자열을 SQL에 직접 연결하지 않아 타입 처리와 SQL Injection 방지에 유리하다.

---

## 6. XML 위치를 알려주는 MyBatis 설정

Mapper XML은 `src/main/resources/mappers` 아래에 있으므로 위치를 YAML에 설정했다.

```yaml
mybatis:
    mapper-locations: classpath:/mappers/**/*Mapper.xml
```

여기서 `mybatis`는 `spring`과 같은 최상위 영역에 있어야 한다.

```yaml
spring:
    datasource:
        url: jdbc:mariadb://localhost:3306/lgcns
        username: root
        password: root

mybatis:
    mapper-locations: classpath:/mappers/**/*Mapper.xml
```

다음처럼 `spring` 아래에 넣으면 Spring이 `spring.mybatis...`라는 다른 설정으로 해석하므로 MyBatis가 읽지 못한다.

```yaml
spring:
    mybatis: # 잘못된 위치
        mapper-locations: ...
```

---

## 7. `@SpringBootTest`로 MyBatis 연결 검증

Mapper와 XML을 만든 다음 바로 Controller에 연결하지 않고 Test Case로 DB 기능부터 검증했다.

```java
@SpringBootTest
public class UserApplicationTests {

    @Autowired
    private UserMapper userMapper;
}
```

### `@SpringBootTest`

실제 Spring ApplicationContext를 실행하는 통합 테스트 Annotation이다.

```text
@SpringBootTest
  ↓
TestcaseApplication 탐색
  ↓
Spring Bean 생성
  ↓
DataSource · HikariCP · MyBatis 초기화
  ↓
UserMapper Proxy 주입
  ↓
@Test Method 실행
```

### `@Autowired`

Spring Container에서 `UserMapper` 타입 Bean을 찾아 Field에 주입한다.

```java
@Autowired
private UserMapper userMapper;
```

개발자가 다음 코드를 작성하지 않아도 된다.

```java
// 직접 생성하지 않음
UserMapper userMapper = new UserMapperImpl();
```

---

## 8. Given–When–Then으로 회원 저장 테스트

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

```text
Given → 저장할 회원 DTO 준비
When  → userMapper.save() 실행
Then  → 반영된 행 수가 1인지 검증
```

### `@Test`

JUnit이 실행할 Test Method임을 표시한다.

```java
@Test
public void signUp() { }
```

### `Assertions.assertEquals()`

```java
Assertions.assertEquals(1, flag);
```

첫 번째 값은 기대값, 두 번째 값은 실제값이다.

```text
assertEquals(expected, actual)
```

같은 Email을 반복 저장하면 Email이 Primary Key이기 때문에 `DuplicateKeyException`이 발생할 수 있다. 이는 Mapper 오류가 아니라 Test가 기존 DB 데이터의 영향을 받는다는 뜻이다.

---

## 9. 전체 회원 조회 테스트

```java
@Test
public void list() {
    List<UserResponseDTO> list = userMapper.findByAll();

    list.stream()
        .forEach(System.out::println);
}
```

```text
UserMapper.findByAll()
  ↓
<select id="findByAll">
  ↓
MariaDB 조회
  ↓
각 행을 UserResponseDTO로 Mapping
  ↓
List<UserResponseDTO> 반환
```

### Method Reference

```java
.forEach(System.out::println)
```

다음 Lambda와 같은 의미다.

```java
.forEach(user -> System.out.println(user))
```

현재 Test는 출력으로 결과를 확인한다. 더 명확한 Test를 만들려면 목록 크기나 특정 Email 포함 여부를 Assertion으로 검증할 수 있다.

---

## 10. 로그인 테스트와 Optional

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

`Optional<UserResponseDTO>`는 로그인 결과가 있을 수도, 없을 수도 있음을 타입으로 표현한다.

```text
일치하는 사용자 있음 → Optional[UserResponseDTO]
일치하는 사용자 없음 → Optional.empty()
```

`get()`은 값이 없으면 `NoSuchElementException`이 발생하므로 먼저 존재 여부를 검증할 수 있다.

```java
Assertions.assertTrue(response.isPresent());
```

또는 없을 때 발생시킬 예외를 직접 지정할 수 있다.

```java
UserResponseDTO user = response.orElseThrow(
        () -> new RuntimeException("이메일 또는 비밀번호 불일치")
);
```

---

## 11. Test와 DTO, Mapper의 관계

`UserApplicationTests`는 DTO를 생성하고 Mapper를 호출하며 결과를 검증한다.

```text
UserApplicationTests
  │
  ├─ UserRequestDTO 생성
  │      ↓
  ├─ UserMapper.save(request)
  ├─ UserMapper.login(request)
  │      ↓
  ├─ userMapper.xml SQL
  │      ↓
  ├─ MariaDB
  │      ↓
  └─ UserResponseDTO 또는 반영 행 수 검증
```

이 단계에서는 Controller와 Service 없이 Persistence 계층만 먼저 확인했다. 문제가 생겼을 때 HTTP 요청 문제와 SQL 문제를 섞지 않고 Mapper 연결만 독립적으로 확인할 수 있다.

---

## 12. 다음 단계: UserService로 기능 표준화

Mapper Test 이후 Controller와 Mapper 사이에 Service 계층을 설계했다.

```java
public interface UserService {
    UserResponseDTO signIn(UserRequestDTO request);
}
```

Service Interface는 로그인 기능의 표준만 정의한다.

```text
입력  : UserRequestDTO
출력  : UserResponseDTO
기능명: signIn
```

실제 처리 방식은 두 구현체가 담당한다.

### 평문 로그인 구현체

```java
@Service
public class UserPlainServiceImpl implements UserService {

    @Override
    public UserResponseDTO signIn(UserRequestDTO request) {
        System.out.println("UserPlainServiceImpl signIn");
        System.out.println("params" + request);
        return null;
    }
}
```

### 암호화 로그인 구현체

```java
@Service
public class UserEncryptionServiceImpl implements UserService {

    @Override
    public UserResponseDTO signIn(UserRequestDTO request) {
        System.out.println("EncryptionServiceImpl signIn");
        System.out.println("params" + request);
        return null;
    }
}
```

### `implements`와 `@Override`

```java
class UserPlainServiceImpl implements UserService
```

`implements`는 Interface의 기능을 구현한다는 의미다.

```java
@Override
public UserResponseDTO signIn(...) { }
```

`@Override`는 상위 Interface에 정의된 Method를 재정의했다는 것을 Compiler에 알린다. Method 이름이나 매개변수 타입이 잘못되면 Compile 단계에서 확인할 수 있다.

### `@Service`

Service 구현 객체를 Spring Bean으로 등록한다.

```text
@Service
  ↓ Component Scan
UserPlainServiceImpl Bean 생성
UserEncryptionServiceImpl Bean 생성
```

---

## 13. 왜 Controller가 Interface 타입으로 받는가

Controller가 특정 구현체를 직접 생성한다면 강하게 결합된다.

```java
UserEncryptionServiceImpl service =
        new UserEncryptionServiceImpl();
```

이 경우 암호화 방식을 바꾸거나 평문 구현체로 교체할 때 Controller 코드도 수정해야 한다.

현재 Controller는 Interface 타입으로 의존한다.

```java
private UserService userService;
```

```text
UserController
  ↓ UserService에만 의존
UserService
  ├─ UserPlainServiceImpl
  └─ UserEncryptionServiceImpl
```

이를 통해 Controller는 구현 세부사항을 몰라도 `signIn()`이라는 공통 기능을 호출할 수 있다. 이것이 Interface를 이용한 다형성과 느슨한 결합이다.

---

## 14. UserController와 Service 주입

현재 Controller의 핵심 구조다.

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @Resource(name = "encryption")
    private UserService userService;

    @GetMapping("/signIn")
    public ResponseEntity<?> signIn(UserRequestDTO request) {
        System.out.println("UserController signIn params: " + request);
        System.out.println("UserController servicebinding " + userService);
        return null;
    }
}
```

요청 주소는 다음과 같다.

```text
GET http://localhost:8000/users/signIn
    ?email=tjwjddns1302@gmail.com
    &password=root
```

Spring MVC가 Query Parameter를 `UserRequestDTO`에 Binding한다.

```text
email Parameter    → request.email
password Parameter → request.password
```

### `ResponseEntity<?>`

HTTP 응답의 상태 코드, Header, Body를 함께 표현할 수 있다.

```text
ResponseEntity
├── Status Code
├── Header
└── Body
```

현재 Method가 `null`을 반환하는 것은 요청과 Service Binding을 확인하는 작성 중 단계다. 이후에는 `userService.signIn(request)` 결과를 Response Body에 담는 흐름으로 이어진다.

---

## 15. `@Autowired`와 `@Resource`의 차이

### `@Autowired`

```java
@Autowired
private UserMapper userMapper;
```

기본적으로 타입을 중심으로 Bean을 찾는다.

```text
필드 타입 UserMapper
  ↓
UserMapper 타입 Bean 검색
  ↓
MyBatis Proxy Bean 주입
```

### `@Resource`

```java
@Resource(name = "encryption")
private UserService userService;
```

지정한 Bean 이름을 중심으로 대상을 찾는다. `UserService` 구현체가 두 개이므로 어떤 구현체를 사용할지 구분하려는 의도다.

```text
UserService 타입 Bean
├── UserPlainServiceImpl
└── UserEncryptionServiceImpl
```

### 현재 코드에서 확인할 점

현재 구현 Class에는 이름 없는 `@Service`가 붙어 있다.

```java
@Service
public class UserEncryptionServiceImpl { }
```

이 경우 기본 Bean 이름은 Class 첫 글자를 소문자로 바꾼 이름이다.

```text
UserEncryptionServiceImpl
          ↓
userEncryptionServiceImpl
```

따라서 다음 이름은 현재 기본 Bean 이름과 일치하지 않는다.

```java
@Resource(name = "encryption")
```

`encryption`이라는 이름으로 주입하려면 Bean을 그 이름으로 등록해야 한다.

```java
@Service("encryption")
public class UserEncryptionServiceImpl implements UserService {
    // ...
}
```

학습 핵심은 구현 Class를 Controller가 직접 생성하는 것이 아니라, Spring Container가 생성한 Bean 중 원하는 구현체를 주입해 준다는 점이다.

---

## 16. 전체 프로그램 동작 흐름

### 1단계: 서버 동작 확인

```text
GET /health/alive
  ↓
HealthController
  ↓
I'm alive!
```

### 2단계: Mapper Test로 DB 계층 검증

```text
UserApplicationTests
  ↓ UserRequestDTO 생성
UserMapper
  ↓ namespace + id Binding
userMapper.xml
  ↓ SQL 실행
HikariCP
  ↓
MariaDB
  ↓
UserResponseDTO · int · Optional 반환
  ↓
Assertions 검증
```

### 3단계: Controller와 Service 연결

```text
Client
  ↓ GET /users/signIn?email=...&password=...
DispatcherServlet
  ↓
UserController
  ↓ UserRequestDTO
UserService
  ↓ 주입된 구현체 선택
UserEncryptionServiceImpl 또는 UserPlainServiceImpl
  ↓
UserMapper
  ↓
MariaDB
  ↓
UserResponseDTO
  ↓
ResponseEntity
```

---

## 17. 오늘 발생한 오류로 이해한 개념

### Java Toolchain 오류

프로젝트는 Java 17을 요구했지만 컴퓨터에는 Java 21이 설정되어 있어 Gradle Import가 실패했다. Toolchain을 Java 21로 맞춰 해결했다.

### Test Annotation이 인식되지 않은 이유

Test Class를 `src/main/java`에 만들면 `testImplementation` Dependency가 적용되지 않는다.

```text
src/main/java → 실제 애플리케이션 코드
src/test/java → JUnit Test 코드
```

### MyBatis BindingException

```text
Invalid bound statement (not found)
```

Mapper XML 위치, YAML 들여쓰기, `namespace`, SQL `id` 중 하나가 Interface와 맞지 않을 때 발생한다.

### DuplicateKeyException

회원가입 Test를 반복하면서 같은 Email을 다시 저장해 Primary Key가 중복됐다. Test는 기존 DB 상태의 영향을 받을 수 있으므로 Test Data 정리나 Transaction Rollback 전략이 필요하다.

---

## 18. 핵심 Annotation 정리

| Annotation | 역할 |
|---|---|
| `@SpringBootApplication` | Spring Boot 시작, 자동 설정, Component Scan |
| `@RestController` | HTTP 요청 처리 및 데이터 반환 |
| `@RequestMapping` | Class 공통 URL 지정 |
| `@GetMapping` | GET 요청과 Method 연결 |
| `@Service` | Service 구현체를 Bean으로 등록 |
| `@Mapper` | MyBatis Mapper Proxy 생성 |
| `@Autowired` | 주로 타입 기준 의존성 주입 |
| `@Resource` | 이름 중심 의존성 주입 |
| `@SpringBootTest` | Spring Context를 실행하는 통합 테스트 |
| `@Test` | JUnit Test Method 지정 |
| `@Override` | Interface Method 재정의 확인 |
| `@Builder` | Builder Pattern 생성 |

---

## 마무리

오늘 실습은 하나의 완성된 로그인 기능을 단번에 만든 것이 아니라 각 계층을 아래에서부터 확인하는 과정이었다.

```text
서버가 살아 있는가?
  → Health Check

DB 기능이 동작하는가?
  → Mapper + XML + Test

데이터는 어떤 객체로 이동하는가?
  → RequestDTO + ResponseDTO

비즈니스 처리 방식을 어떻게 교체하는가?
  → UserService Interface + 구현체

객체를 누가 만들고 연결하는가?
  → Spring Container + Dependency Injection
```

가장 중요한 점은 Controller가 구현체를 직접 생성하지 않고 Interface에 의존하며, Spring Container가 필요한 구현 Bean을 주입한다는 것이다. 또한 MyBatis는 Mapper Interface와 XML을 `namespace`와 `id`로 연결하고, Test Case를 통해 Controller를 만들기 전에 DB 접근 기능부터 독립적으로 검증할 수 있었다.
