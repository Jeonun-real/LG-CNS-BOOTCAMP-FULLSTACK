# BE 14일차 - JDBC에서 Spring Boot까지

## 1. 오늘의 핵심

오늘은 Java 애플리케이션이 데이터베이스와 통신하는 JDBC 흐름을 직접 구현하고, 이를 기반으로 Gradle이 관리하는 Spring Boot 프로젝트를 생성했다.

```text
React / Browser
  ↓ HTTP
Embedded Tomcat
  ↓
DispatcherServlet
  ↓
Controller → Service
  ↓
JDBC / MyBatis / JPA
  ↓
MariaDB
```

### 주요 키워드

- Servlet, DispatcherServlet, Tomcat
- JDBC Driver
- `Connection`, `PreparedStatement`, `ResultSet`
- `executeQuery()`, `executeUpdate()`
- DTO Mapping
- Gradle Dependency
- Spring Initializr
- `@SpringBootApplication`
- `SpringApplication.run()`
- Profile과 `application.yml`
- DevTools
- MyBatis, JPA, Hibernate
- 실행 JAR

---

## 2. 웹 요청과 Spring MVC

Servlet은 HTTP 요청과 응답을 처리하는 Java 서버 컴포넌트다. Spring MVC에서는 `DispatcherServlet`이 모든 요청을 먼저 받는 Front Controller 역할을 한다.

```text
HTTP Request
  ↓
Tomcat
  ↓
DispatcherServlet
  ↓ 요청 매핑
Controller Method
  ↓
Response
```

이전에 콘솔 Blog 프로젝트에서 직접 만든 `FrontController`와 개념적으로 비슷하지만, Spring에서는 Framework가 요청 탐색과 실행 흐름을 관리한다.

### Tomcat, Catalina, Jasper

- Tomcat: Servlet을 실행하고 HTTP 요청을 처리하는 Container
- Catalina: Tomcat의 Servlet Container 구현
- Jasper: JSP를 Servlet으로 변환·컴파일하는 JSP Engine

Spring Boot Web은 내장 Tomcat을 사용할 수 있어 외부 WAS에 WAR를 직접 배포하지 않고 실행 JAR만으로 서버를 시작할 수 있다.

---

## 3. SPA와 서버 페이지 방식

### SPA

React나 Vue가 화면을 구성하고 Spring Boot는 JSON 데이터를 반환한다.

```text
React → 비동기 HTTP 요청 → Spring Boot
React ← JSON 응답       ← Controller
```

### 서버 페이지 방식

JSP나 Template Engine으로 서버가 완성된 HTML을 만들어 반환한다.

```text
Browser → Spring MVC → JSP / Template + Data
Browser ← 완성된 HTML
```

Frontend와 Backend는 하나의 Spring Boot JAR로 묶을 수도 있고, React는 정적 Hosting에, Spring Boot는 Server나 Container에 별도로 배포할 수도 있다.

---

## 4. JDBC가 필요한 이유

DBMS마다 Driver 구현은 다르다. Java 코드가 특정 벤더 API에 직접 의존하지 않도록 JDBC가 표준 Interface를 제공한다.

```text
Java Application
  ↓ JDBC API
Connection / PreparedStatement / ResultSet
  ↓ 벤더 JDBC Driver
MariaDB / MySQL / Oracle
```

Java는 Interface를 정의하고 DB 벤더는 이를 구현한 Driver JAR를 제공한다.

현재 순수 Java 프로젝트에서는 다음 Driver가 `lib`에 있다.

```text
mariadb-java-client-3.5.10.jar
```

Spring Boot에서는 Gradle로 선언한다.

```gradle
runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
```

---

## 5. JDBC 처리 순서

```text
1. Driver 준비
2. Connection 생성
3. SQL 작성
4. PreparedStatement 생성
5. Parameter Binding
6. SQL 실행
7. ResultSet 또는 변경 행 수 처리
8. 자원 close
```

주요 타입은 `java.sql` Package에 있다.

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
```

---

## 6. MariaDB 연결 정보

현재 `MariadbDao`는 다음 연결 정보를 사용한다.

```java
private static final String DRIVER =
    "org.mariadb.jdbc.Driver";

private static final String USER = "root";
private static final String PASSWORD = "root";

private static final String URL =
    "jdbc:mariadb://localhost:3306/lgcns";
```

```text
jdbc:mariadb://localhost:3306/lgcns
│     │         │         │    └─ Database
│     │         │         └────── Port
│     │         └──────────────── Host
│     └────────────────────────── DBMS
└──────────────────────────────── JDBC Protocol
```

ID와 Password를 소스코드에 저장하면 Git을 통해 노출될 수 있으므로 실제 프로젝트에서는 환경 변수나 외부 Secret으로 분리한다.

---

## 7. Driver 로딩

```java
Class.forName("org.mariadb.jdbc.Driver");
```

Driver Class를 JVM에 로딩해 `DriverManager`에서 사용할 수 있게 한다.

JDBC 4.0 이후에는 Driver JAR의 Service Provider 설정을 이용해 자동 등록되므로 최신 환경에서는 `Class.forName()`을 생략할 수 있다. 단, Driver가 Runtime Classpath에 있어야 한다.

현재 생성자의 빈 `catch`는 오류를 숨기므로 예외를 기록하거나 상위 계층으로 전달해야 한다.

---

## 8. Connection

```java
Connection conn =
    DriverManager.getConnection(
        URL,
        USER,
        PASSWORD
    );
```

`Connection`은 Java와 DB 사이의 연결을 나타낸다. 하나의 Connection으로 여러 SQL을 실행할 수 있고 Transaction도 Connection 단위로 관리한다.

```text
Connection
  ├─ PreparedStatement 1
  ├─ PreparedStatement 2
  └─ Transaction
```

실무 Spring 애플리케이션에서는 매번 물리 연결을 생성하기보다 Connection Pool에서 빌리고 작업 후 반환한다.

---

## 9. PreparedStatement

```java
String sql = """
    UPDATE department
    SET dept_name = ?
    WHERE dept_id = ?
    """;

PreparedStatement pstmt =
    conn.prepareStatement(sql);
```

`?`는 값을 Binding할 자리다.

```java
pstmt.setString(1, request.getDept_name());
pstmt.setString(2, request.getDept_id());
```

Parameter 번호는 1부터 시작한다. 문자열 연결 방식보다 타입 처리가 편하고 SQL Injection 방어에 유리하다.

---

## 10. SELECT와 ResultSet

```java
String sql = """
    SELECT dept_id,
           dept_name,
           loc_id
    FROM department
    """;

PreparedStatement pstmt =
    conn.prepareStatement(sql);

ResultSet rset = pstmt.executeQuery();
```

SELECT는 `executeQuery()`로 실행하고 결과를 `ResultSet`으로 받는다.

처음 Cursor는 첫 번째 행 이전에 있다.

```java
while (rset.next()) {
    String deptId =
        rset.getString("dept_id");
}
```

```text
첫 행 이전
  ↓ next()
1번 Record
  ↓ next()
2번 Record
  ↓
행이 없으면 false
```

현재 코드는 `getString(1)`처럼 순서를 사용하지만 Column 이름을 사용하면 의미가 분명하고 SELECT 순서 변경에 덜 취약하다.

---

## 11. ResultSet을 DTO로 Mapping

```java
DepartmentResponseDTO response =
    DepartmentResponseDTO.builder()
        .dept_id(rset.getString("dept_id"))
        .dept_name(rset.getString("dept_name"))
        .loc_id(rset.getString("loc_id"))
        .build();
```

```text
DB Column        DTO Field
dept_id    ───>  dept_id
dept_name  ───>  dept_name
loc_id     ───>  loc_id
```

Mapping 과정에서 DB 타입을 Java 타입으로 변환한다.

| Database | Java |
|---|---|
| VARCHAR | String |
| INT | int / Integer |
| DATE | LocalDate |
| DATETIME | LocalDateTime |

Java Field는 일반적으로 `deptId`, `deptName`처럼 Camel Case로 작성하고 Mapping 단계에서 DB의 Snake Case와 연결한다.

---

## 12. Request DTO와 Response DTO

### Request DTO

수정 요청을 DAO로 전달한다.

```java
DepartmentRequestDTO request =
    DepartmentRequestDTO.builder()
        .dept_id("90")
        .dept_name("해외영업3팀")
        .build();
```

### Response DTO

조회 결과를 애플리케이션으로 반환한다.

```java
List<DepartmentResponseDTO> list =
    dao.departments();
```

```text
RequestDTO
Application → DAO → DB

ResponseDTO
DB → DAO → Application
```

DTO가 테이블과 비슷한 구조를 가질 수 있지만 요청과 응답 목적에 필요한 Field만 포함할 수도 있다.

---

## 13. UPDATE와 executeUpdate

```java
String sql = """
    UPDATE department
    SET dept_name = ?
    WHERE dept_id = ?
    """;

pstmt.setString(1, request.getDept_name());
pstmt.setString(2, request.getDept_id());

int flag = pstmt.executeUpdate();
```

DML은 `executeUpdate()`로 실행하며 영향받은 행 수를 반환한다.

```text
한 행 변경       → 1
대상 행 없음     → 0
여러 행 변경     → 변경된 행 수
```

`jdbcApp`은 반환값을 이용해 실행 결과를 확인한다.

---

## 14. JDBC 자원 해제

JDBC 자원은 생성의 역순으로 닫는다.

```text
생성: Connection → PreparedStatement → ResultSet
해제: ResultSet → PreparedStatement → Connection
```

원본 `MariadbDao`는 Connection만 닫고 있다. ResultSet과 PreparedStatement도 닫아야 한다.

try-with-resources를 사용하면 자동으로 해제된다.

```java
try (
    Connection conn =
        DriverManager.getConnection(
            URL,
            USER,
            PASSWORD
        );
    PreparedStatement pstmt =
        conn.prepareStatement(sql);
    ResultSet rset =
        pstmt.executeQuery()
) {
    while (rset.next()) {
        // DTO Mapping
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

세 객체는 `AutoCloseable`을 구현한다.

---

## 15. JDBC에서 MyBatis와 JPA로

순수 JDBC는 기능마다 연결, 실행, Mapping, 자원 해제 코드를 반복한다.

```text
Connection
  ↓
PreparedStatement
  ↓
Parameter Binding
  ↓
SQL 실행
  ↓
ResultSet
  ↓
DTO Mapping
  ↓
close
```

### MyBatis

MyBatis는 SQL Mapper Framework다.

- SQL을 개발자가 직접 작성
- SQL 결과와 객체 Mapping
- 동적 SQL 지원
- JDBC 반복 코드 감소

완전한 ORM보다는 SQL Mapper로 분류하며 SQL 이해가 중요하다.

### JPA와 Hibernate

JPA는 Java 객체와 관계형 DB 사이의 Mapping을 위한 표준 명세이고 Hibernate는 대표적인 JPA 구현체다.

```text
JPA 표준
  ↓
Hibernate 구현
  ↓
JDBC
  ↓
Database
```

`JPA = 자동`, `MyBatis = 수동`으로만 구분하기보다 JPA는 객체 중심, MyBatis는 SQL 중심으로 이해하는 편이 정확하다.

---

## 16. Gradle의 역할

Spring Boot 프로젝트에는 Web, Tomcat, Jackson, JPA, MyBatis, Driver 등 많은 JAR가 필요하다.

Gradle은 필요한 라이브러리와 그 라이브러리가 다시 필요로 하는 전이 Dependency를 관리한다.

```text
build.gradle
  ↓ Dependency 선언
Maven Central
  ↓ 다운로드
Gradle Cache
  ↓
Compile · Test · BootRun · Packaging
```

Dependency 좌표는 일반적으로 다음 형태다.

```text
group : artifact : version
```

```gradle
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.1'
```

---

## 17. Gradle Configuration

| Configuration | 역할 |
|---|---|
| `implementation` | Compile과 Runtime에 사용 |
| `compileOnly` | Compile에만 사용 |
| `runtimeOnly` | Runtime에만 사용 |
| `developmentOnly` | 개발 환경에서 사용 |
| `annotationProcessor` | Compile 시 Annotation 처리 |
| `testImplementation` | Test에서 사용 |

MariaDB Driver가 `runtimeOnly`인 이유는 애플리케이션 코드가 주로 JDBC Interface를 기준으로 Compile되고 실제 Driver 구현은 실행할 때 필요하기 때문이다.

---

## 18. 현재 build.gradle

### Plugin

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.0.8'
    id 'io.spring.dependency-management' version '1.1.7'
}
```

- Java Compile과 Test
- Spring Boot 실행과 Boot JAR 생성
- Dependency 버전 관리

### Java Toolchain

```gradle
java {
    toolchain {
        languageVersion =
            JavaLanguageVersion.of(21)
    }
}
```

현재 설치된 JDK에 맞춰 Java 21을 사용한다.

### Repository

```gradle
repositories {
    mavenCentral()
}
```

Dependency를 Maven Central에서 찾는다.

---

## 19. 현재 Dependency 분석

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

implementation 'org.springframework.boot:spring-boot-starter-webmvc'

implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.1'

runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'

compileOnly 'org.projectlombok:lombok'

developmentOnly 'org.springframework.boot:spring-boot-devtools'

annotationProcessor 'org.projectlombok:lombok'

annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
```

- JPA Starter: JPA와 Hibernate 기반 데이터 접근
- Web MVC Starter: DispatcherServlet, JSON, 내장 Tomcat
- MyBatis Starter: MyBatis와 Spring 연결
- MariaDB Driver: MariaDB JDBC 구현체
- Lombok: Getter, Builder 등 Compile 시 생성
- DevTools: 개발 중 자동 재시작
- Configuration Processor: 설정 Metadata와 IDE 자동 완성 지원

---

## 20. Spring Initializr

VS Code에서 다음 순서로 프로젝트를 생성할 수 있다.

```text
Ctrl + Shift + P
  ↓
Spring Initializr: Create a Gradle Project
  ↓
Spring Boot Version
  ↓
Java / Group / Artifact / Package
  ↓
Java Version
  ↓
Dependency 선택
  ↓
프로젝트 생성
```

오늘 선택한 주요 Dependency는 Web MVC, DevTools, Configuration Processor, JPA, MyBatis, MariaDB Driver와 Lombok이다.

---

## 21. Spring Boot 진입점

```java
@SpringBootApplication(
    exclude = DataSourceAutoConfiguration.class
)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            DemoApplication.class,
            args
        );
    }
}
```

### @SpringBootApplication

다음 핵심 기능을 묶은 Annotation이다.

- Spring Boot 설정 Class 표시
- Auto Configuration 활성화
- 현재 Package와 하위 Package Component Scan

Controller와 Service는 일반적으로 `com.example.demo` 또는 하위 Package에 작성해야 자동 탐색된다.

### SpringApplication.run()

Spring Container를 만들고 Bean을 등록하며 Web 환경에서는 내장 Tomcat을 시작한다.

---

## 22. DataSourceAutoConfiguration 제외 이유

JPA, MyBatis와 MariaDB Driver가 Classpath에 있으면 Spring Boot는 DataSource를 자동 설정하려 한다.

하지만 아직 URL, Username과 Password가 Spring 설정에 없으므로 실행 오류가 발생할 수 있다.

```text
JPA + MyBatis + Driver
  ↓
DataSource 자동 설정 시도
  ↓
연결 설정 없음
  ↓
실행 실패 가능
  ↓
현재는 자동 설정 제외
```

그래서 현재 다음 옵션을 사용한다.

```java
exclude = DataSourceAutoConfiguration.class
```

나중에 DataSource 설정을 작성하면 이 제외 옵션을 제거해야 JPA와 MyBatis가 DB를 사용할 수 있다.

---

## 23. application.yml과 Profile

공통 설정은 다음과 같다.

```yaml
spring:
  profiles:
    default: dev

server:
  port: 8000
```

- 별도 Profile이 없으면 `dev` 사용
- 내장 Tomcat을 8000번 Port로 실행

접속 주소는 다음과 같다.

```text
http://localhost:8000
```

### application-dev.yml

```yaml
spring:
  config:
    activate:
      on-profile: dev
```

### application-prod.yml

```yaml
spring:
  config:
    activate:
      on-profile: prod
```

```text
application.yml      → 공통 설정
application-dev.yml  → 개발 환경
application-prod.yml → 운영 환경
```

DB Password와 API Key는 YML에 직접 넣지 않고 환경 변수나 Secret Manager를 이용하는 것이 안전하다.

---

## 24. Spring Boot 실행 로그

정상 실행은 다음 순서로 나타났다.

```text
Starting DemoApplication using Java 21
  ↓
default profile: dev
  ↓
DevTools active
  ↓
Tomcat initialized with port 8000
  ↓
ApplicationContext initialized
  ↓
Tomcat started on port 8000
  ↓
Started DemoApplication
```

`Started DemoApplication`은 실행이 멈춘 것이 아니다. 서버가 정상 시작되어 HTTP 요청을 기다리는 상태다.

Controller가 아직 없다면 `http://localhost:8000/`에 접속했을 때 404가 발생할 수 있다. 서버 오류가 아니라 해당 URL을 처리할 Handler가 없다는 뜻이다.

DevTools 때문에 실행 Thread가 `restartedMain`으로 표시될 수 있다.

---

## 25. Gradle 실행과 Build

프로젝트 폴더에서 실행한다.

```powershell
cd .\be\demo
.\gradlew.bat bootRun
```

Test:

```powershell
.\gradlew.bat test
```

Build:

```powershell
.\gradlew.bat build
```

Build 결과 JAR는 일반적으로 다음 위치에 생성된다.

```text
be/demo/build/libs/
```

실행 JAR는 다음처럼 실행한다.

```powershell
java -jar .\build\libs\demo-0.0.1-SNAPSHOT.jar
```

---

## 26. Spring Boot가 줄여주는 작업

```text
순수 Java
  ├─ JAR 직접 관리
  ├─ JDBC 연결 반복 코드
  ├─ Servlet 등록
  ├─ 외부 WAS 설정
  ├─ 객체 생성과 연결
  └─ 환경 설정 처리

Spring Boot
  ├─ Starter Dependency
  ├─ Auto Configuration
  ├─ Component Scan
  ├─ Embedded Tomcat
  ├─ Profile
  └─ Executable JAR
```

Spring Boot가 SQL이나 Business Logic을 대신 설계하는 것은 아니다. 환경 구성과 반복 기반 코드를 줄여 기능 구현에 집중하도록 돕는다.

---

## 27. 현재 프로젝트와 다음 단계

현재 상태:

```text
DemoApplication
  ↓
Spring Boot 실행
  ↓
dev Profile
  ↓
Tomcat 8000
  ↓
HTTP 요청 대기
```

다음 흐름:

```text
Controller
  ↓
Service
  ↓
Mapper 또는 Repository
  ↓
DataSource
  ↓
MariaDB
  ↓
DTO / JSON Response
```

JPA 또는 MyBatis를 사용하려면 다음 작업이 필요하다.

1. `application-dev.yml`에 DataSource 설정
2. `DataSourceAutoConfiguration` 제외 제거
3. Controller와 Service 작성
4. JPA Repository 또는 MyBatis Mapper 작성
5. Endpoint와 JSON 응답 확인

---

## 28. 오늘 코드에서 확인할 점

### JDBC 자원 전체 해제

원본 DAO는 Connection만 닫는다. ResultSet과 PreparedStatement도 닫거나 try-with-resources를 사용해야 한다.

### 빈 catch 제거

Driver 로딩 실패를 무시하면 이후 오류의 원인을 찾기 어렵다.

### SELECT Column 명시

```sql
SELECT dept_id,
       dept_name,
       loc_id
FROM department
```

`SELECT *`보다 Mapping 대상이 명확하다.

### Column 이름으로 읽기

```java
rset.getString("dept_id")
```

순번보다 의미가 명확하다.

### 민감 정보 분리

DB 계정과 Password는 환경 변수로 이동해야 한다.

### JPA와 MyBatis 기준 결정

두 기술을 동시에 사용할 수 있지만 기능별 사용 기준과 Transaction 경계를 명확히 해야 한다.

---

## 29. 최종 흐름

```text
Database
  ↑ JDBC Driver
Connection
  ↑
PreparedStatement
  ↑
ResultSet → DTO
  ↑
DAO / Mapper / Repository
  ↑
Service
  ↑
Controller
  ↑
DispatcherServlet
  ↑
Embedded Tomcat
  ↑
React / Browser
```

오늘 학습의 핵심은 Spring Boot 실행 명령만 익히는 것이 아니다. JDBC를 통해 Java가 DB와 통신하는 원리를 이해하고, Spring Boot가 Gradle, 내장 Tomcat, 자동 설정과 Profile로 주변의 반복 작업을 어떻게 줄이는지 연결해서 이해하는 것이다.
