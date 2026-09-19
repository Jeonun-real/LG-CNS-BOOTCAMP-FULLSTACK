# BE 24일차 - OpenAPI 통신과 MSA·Spring Cloud

## 1. 오늘의 핵심

오늘은 크게 두 흐름을 학습했다.

```text
외부 데이터 연동
React → Spring Boot → 기상청 OpenAPI → JSON → DTO → React

분산 시스템 설계
Monolithic의 한계 → MSA → Spring Cloud 구성요소
```

첫 번째 흐름에서는 Java 애플리케이션이 HTTP로 외부 OpenAPI를 호출하고, 응답 JSON을 DTO 목록으로 변환하는 과정을 구현했다. 두 번째 흐름에서는 하나의 애플리케이션을 여러 독립 서비스로 분리할 때 필요한 Gateway, Service Discovery, Config Server와 비동기 메시징 개념을 학습했다.

---

## 2. Java와 외부 API 통신

Java가 외부 HTTP 통신을 못 하는 것은 아니다. Java에서는 표준 네트워크 API 또는 별도의 HTTP Client 라이브러리를 사용해야 한다.

```text
Java 표준 API
├─ java.net.URL
├─ HttpURLConnection
└─ java.net.http.HttpClient

외부 라이브러리
├─ OkHttp
└─ Apache HttpClient
```

현재 프로젝트에서는 `java.net.URL`과 `HttpURLConnection`을 사용했다. `java.net`은 Framework가 아니라 JDK가 제공하는 표준 Package다.

```java
URL url = new URL(requestUrl);
HttpURLConnection http =
        (HttpURLConnection) url.openConnection();

int status = http.getResponseCode();
```

### 코드 해석

- `new URL(requestUrl)`: 문자열 주소를 URL 객체로 만든다.
- `openConnection()`: 해당 주소와 연결할 객체를 생성한다.
- `HttpURLConnection`: HTTP 요청과 응답을 처리한다.
- `getResponseCode()`: `200`, `400`, `403`과 같은 HTTP 상태 코드를 얻는다.

현재 코드에서는 응답이 `200 OK`일 때 응답 본문을 읽는다.

```java
if (status == 200) {
    return readString(http.getInputStream());
}
```

---

## 3. OpenAPI 요청 URL 생성

현재 `ForecastService`는 `UriComponentsBuilder`로 기상청 API 주소와 Query Parameter를 조립한다.

```java
String requestUrl = UriComponentsBuilder
        .fromUriString(endPoint)
        .queryParam("serviceKey", key)
        .queryParam("beach_num", request.getBeach_num())
        .queryParam("base_date", request.getBase_date())
        .queryParam("base_time", request.getBase_time())
        .queryParam("dataType", type)
        .build(true)
        .toUriString();
```

`UriComponentsBuilder`가 GET 요청을 실행하는 것은 아니다. 주소와 Query String을 안전하게 조립하고 최종 URL 문자열을 만드는 역할이다. 실제 HTTP 통신은 `HttpURLConnection`이 수행한다.

```text
endPoint
  + serviceKey
  + beach_num
  + base_date
  + base_time
  + dataType
        ↓
완성된 요청 URL
        ↓
HttpURLConnection으로 GET 요청
```

요청 URL은 다음과 같은 형태가 된다.

```text
https://apis.data.go.kr/.../getVilageFcstBeach
?serviceKey=인증키
&beach_num=1
&base_date=20260917
&base_time=1100
&dataType=JSON
```

`build(true)`는 이미 Encoding된 인증키를 다시 Encoding하지 않도록 사용했다. `%2B`가 `%252B`로 바뀌면 이중 Encoding이므로 실제 출력 URL을 확인해야 한다.

---

## 4. InputStream에서 문자열까지

HTTP 응답 본문은 `InputStream`으로 받는다.

```text
기상청 응답 Body
    ↓ byte
InputStream
    ↓ byte를 문자로 Decode
InputStreamReader(UTF-8)
    ↓ 문자 단위 읽기 기능 보강
BufferedReader
    ↓ 한 줄씩 결합
StringBuilder
    ↓
JSON 문자열
```

`InputStream`이 한 번에 반드시 1byte만 읽는다는 뜻은 아니다. 중요한 점은 `InputStream`이 문자 Stream이 아니라 byte Stream이라는 것이다. UTF-8 한글은 여러 byte로 구성되므로 Encoding 정보 없이 byte를 문자처럼 처리하면 한글이 깨질 수 있다.

```java
BufferedReader br = new BufferedReader(
        new InputStreamReader(stream, "UTF-8")
);

String input;
StringBuilder result = new StringBuilder();

while ((input = br.readLine()) != null) {
    result.append(input);
}

br.close();
```

### Stream Chaining

```java
new BufferedReader(
    new InputStreamReader(stream, "UTF-8")
)
```

각 객체가 앞 단계의 객체를 감싸며 기능을 추가한다.

| 객체 | 역할 |
| --- | --- |
| `InputStream` | HTTP 응답을 byte로 읽음 |
| `InputStreamReader` | byte를 UTF-8 문자로 변환 |
| `BufferedReader` | 문자를 Buffer에 모아 한 줄씩 효율적으로 읽음 |
| `StringBuilder` | 읽은 문자열을 계속 이어 붙임 |

---

## 5. JSON 문자열을 DTO 목록으로 변환

외부 API에서 받은 문자열은 이미 JSON 형식의 Text다. `ObjectMapper`는 이 JSON 문자열을 Java의 `JsonNode` 또는 DTO로 역직렬화한다.

```java
JsonNode node = objectMapper.readTree(result);
JsonNode item = node.findValue("item");
```

- `readTree()`: JSON 문자열을 Tree 구조로 읽는다.
- `JsonNode`: JSON 객체·배열·값을 탐색할 수 있는 Node다.
- `findValue("item")`: 하위 구조에서 이름이 `item`인 Node를 찾는다.

`item`이 배열이면 DTO 배열로 변환한다.

```java
if (item.isArray()) {
    list = Arrays.asList(
        objectMapper.treeToValue(
            item,
            ForecastResponseDTO[].class
        )
    );
}
```

데이터 변환 흐름은 다음과 같다.

```text
JSON 문자열
  ↓ readTree()
JsonNode 전체 응답
  ↓ findValue("item")
예보 JSON 배열
  ↓ treeToValue()
ForecastResponseDTO[]
  ↓ Arrays.asList()
List<ForecastResponseDTO>
```

`treeToValue()`가 JSON을 만드는 것이 아니라 JSON Tree를 Java 객체로 변환한다.

---

## 6. Stream과 Enum으로 응답 가공

기상청은 `TMP`, `POP`, `SKY`처럼 Code 중심으로 응답한다. 현재 코드는 `Stream.map()`과 `CategoryCode` Enum을 사용해 사용자에게 보여줄 이름과 단위를 추가한다.

```java
list = list.stream()
        .map(dto -> {
            dto.setCategoryName(
                CategoryCode.valueOf(dto.getCategory()).getName()
            );

            String value = CategoryCode.getCodeValue(
                dto.getCategory(),
                dto.getFcstValue()
            );

            String unit = CategoryCode
                    .valueOf(dto.getCategory())
                    .getUnit();

            dto.setFcstValue(value + unit);
            return dto;
        })
        .toList();
```

예를 들어 다음 원본 DTO가 들어온다.

```text
ForecastResponseDTO(
    category=TMP,
    fcstValue=23,
    categoryName=null
)
```

`map()`을 통과하면 다음처럼 바뀐다.

```text
ForecastResponseDTO(
    category=TMP,
    fcstValue=23℃,
    categoryName=1시간 기온
)
```

`CategoryCode.valueOf("TMP")`는 이름이 `TMP`인 Enum 상수를 찾는다.

```java
TMP("1시간 기온", "℃")
```

`PTY`와 `SKY`는 숫자 값도 사람이 읽을 수 있는 문자열로 변환한다.

```text
PTY=0 → 없음
PTY=1 → 비
PTY=3 → 눈

SKY=1 → 맑음
SKY=3 → 구름많음
SKY=4 → 흐림
```

---

## 7. 현재 OpenAPI 전체 데이터 흐름

```text
React ForecastPage
  ↓ 해수욕장 이름·발표 날짜·발표 시간 선택
  ↓ POST /forecast/fcst
  ↓ JSON Request Body
ForecastController
  ↓ @RequestBody ForecastRequestDTO
ForecastService.connection()
  ↓ UriComponentsBuilder
기상청 요청 URL 생성
  ↓ HttpURLConnection
기상청 OpenAPI
  ↓ InputStream
InputStreamReader(UTF-8)
  ↓ BufferedReader
JSON 문자열
  ↓ ObjectMapper.readTree()
JsonNode
  ↓ treeToValue()
List<ForecastResponseDTO>
  ↓ Stream.map() + CategoryCode
이름·단위가 추가된 DTO 목록
  ↓ ResponseEntity Body
React ForecastList
  ↓ map()
ForecastItem 렌더링
```

Controller는 HTTP 요청과 응답을 담당한다.

```java
@RestController
@RequestMapping("/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping("/fcst")
    public ResponseEntity<?> fcst(
            @RequestBody ForecastRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(forecastService.connection(request));
    }
}
```

### 주요 Annotation

| Annotation | 역할 |
| --- | --- |
| `@RestController` | 반환 객체를 JSON Response Body로 변환 |
| `@RequestMapping("/forecast")` | Controller 공통 URL |
| `@PostMapping("/fcst")` | POST 요청 Mapping |
| `@RequestBody` | 요청 JSON을 DTO로 역직렬화 |
| `@RequiredArgsConstructor` | `final` 필드 생성자 생성 |
| `@Value` | YAML·환경변수 설정값 주입 |

---

## 8. 외부 API 오류를 처리할 때 주의할 점

HTTP 상태가 `200`이어도 데이터 조회가 성공했다는 뜻은 아니다.

```json
{
  "response": {
    "header": {
      "resultCode": "03",
      "resultMsg": "NO_DATA"
    }
  }
}
```

이 응답에는 `item`이 없다. 다음 코드를 바로 실행하면 `NullPointerException`이 발생할 수 있다.

```java
JsonNode item = node.findValue("item");
item.isArray();
```

따라서 업무 결과 코드와 `item` 존재 여부를 먼저 검사해야 한다.

```java
String resultCode = node
        .at("/response/header/resultCode")
        .asText();

if (!"00".equals(resultCode)) {
    return List.of();
}

JsonNode item = node.at("/response/body/items/item");

if (!item.isArray()) {
    return List.of();
}
```

```text
HTTP 상태 코드
→ 서버와 HTTP 통신이 성공했는지 확인

resultCode
→ 실제 API 업무 처리가 성공했는지 확인
```

---

## 9. Monolithic Architecture

Monolithic Architecture는 여러 기능을 하나의 애플리케이션으로 묶어 배포하는 구조다.

```text
Client
  ↓
하나의 Spring Boot Application
  ├─ User
  ├─ Blog
  ├─ Comment
  └─ Forecast
  ↓
하나의 Database
```

초기 개발과 배포가 단순하지만 서비스가 커질수록 다음 문제가 발생할 수 있다.

- 특정 기능의 장애가 전체 애플리케이션에 영향을 줄 수 있다.
- 기능 사이의 결합도가 높아질 수 있다.
- 작은 변경도 전체 애플리케이션을 다시 Build·배포해야 할 수 있다.
- 특정 기능에만 Traffic이 몰려도 전체 Application을 Scale Out해야 한다.
- 기능별로 다른 기술이나 배포 주기를 적용하기 어렵다.

예를 들어 Blog 기능에만 Traffic이 몰렸는데 하나의 애플리케이션으로 묶여 있다면 사용량이 적은 User와 Comment 기능까지 함께 복제된다.

```text
Monolithic Instance 1
User + Blog + Comment

Monolithic Instance 2
User + Blog + Comment
```

---

## 10. MSA가 필요한 이유

MSA는 애플리케이션을 업무 기준의 독립된 Service로 분리한다.

```text
Client
  ↓
API Gateway
  ├─ User Service    → User DB
  ├─ Blog Service    → Blog DB
  ├─ Comment Service → Comment DB
  └─ Forecast Service→ 외부 OpenAPI
```

각 Service는 독립적으로 개발·배포·확장할 수 있다.

```text
Blog Traffic 증가
  ↓
Blog Service Instance만 추가
  ↓
다른 Service는 기존 수량 유지
```

이를 Scale Out이라고 한다.

```text
Scale Up
→ 기존 서버의 CPU·Memory 성능을 높임

Scale Out
→ 같은 Service Instance 수를 늘림
```

Kubernetes는 Container의 배포, 복구와 Scale Out·In을 관리할 수 있다.

---

## 11. MSA의 장점과 단점

### 장점

- Service별 독립 배포가 가능하다.
- Traffic이 많은 Service만 Scale Out할 수 있다.
- 장애 범위를 특정 Service로 격리할 수 있다.
- Service별로 다른 기술과 Database를 선택할 수 있다.
- 조직을 업무 Domain 중심으로 분리할 수 있다.

### 단점

- Service 수가 늘어 운영과 배포가 복잡해진다.
- 네트워크 통신 실패와 지연을 고려해야 한다.
- 로그와 장애 원인이 여러 Service에 분산된다.
- Service별 DB가 분리되면 하나의 ACID Transaction으로 묶기 어렵다.
- 데이터 정합성과 분산 Transaction 전략이 필요하다.

Spring Cloud가 이러한 단점을 모두 자동으로 해결하는 것은 아니다. Spring Cloud는 Service Discovery, Gateway, Config와 메시지 연동 등 분산 시스템 구현에 필요한 도구를 제공한다. 분산 Transaction은 Saga, 보상 Transaction, Outbox Pattern 같은 별도의 설계가 필요하다.

---

## 12. Service 사이의 통신

물리적으로 분리된 Service는 Network를 통해 통신한다.

### 동기 통신

```text
User Service
  ↓ HTTP 요청
Blog Service
  ↓ 즉시 응답
User Service가 다음 작업 진행
```

대표적으로 REST API, OpenFeign, WebClient 등을 사용할 수 있다.

### 비동기 통신

```text
Order Service
  ↓ Event 발행
Kafka 또는 RabbitMQ
  ↓ Event 전달
Payment·Notification Service
```

Kafka와 RabbitMQ는 MSA의 필수조건이 아니다. Service를 느슨하게 연결하고 비동기 Event 처리가 필요할 때 사용하는 Message Broker다.

| 구분 | Kafka | RabbitMQ |
| --- | --- | --- |
| 중심 개념 | Event Stream과 Log | Message Queue와 Routing |
| 특징 | 높은 처리량, Event 보존·재처리 | 유연한 Routing, 작업 Queue |
| 활용 | Event Streaming, Log, 데이터 Pipeline | 명령 전달, 작업 분배, 비동기 처리 |

모든 Service가 반드시 하나의 Cluster 안에 있어야 하는 것은 아니다. 중요한 것은 Service가 서로 접근할 수 있는 Network와 주소 발견, 인증, 장애 처리 방식이 갖춰지는 것이다.

---

## 13. API Gateway

Client가 모든 Service의 주소를 직접 알게 하면 결합도가 커지고 보안 정책도 중복된다. API Gateway는 외부 요청의 공통 진입점 역할을 한다.

```text
Client
  ↓ 하나의 공개 주소
API Gateway
  ├─ /users/**    → User Service
  ├─ /blogs/**    → Blog Service
  └─ /forecast/** → Forecast Service
```

Gateway가 담당할 수 있는 기능은 다음과 같다.

- 요청 경로에 따른 Routing
- 인증과 인가의 공통 처리
- 내부망과 외부망의 경계 구성
- CORS와 Header 처리
- 요청 제한과 Logging
- Load Balancing 연계

Spring Cloud Gateway는 Spring 기반의 API Gateway 구현을 지원한다.

---

## 14. Eureka Service Discovery

MSA에서는 Service Instance가 Scale Out·In되면서 IP와 Port가 계속 바뀔 수 있다. 주소를 코드에 고정하면 변경할 때마다 설정을 수정해야 한다.

```text
User Service Instance
  ↓ 자신의 위치 등록
Eureka Server
  ↑ Service 이름으로 조회
API Gateway 또는 다른 Service
```

Eureka는 Netflix에서 공개한 Service Discovery 기술이며 Spring Cloud Netflix를 통해 사용할 수 있다.

```text
FORECAST-SERVICE
├─ 10.0.1.11:8080
├─ 10.0.1.12:8080
└─ 10.0.1.13:8080
```

물리적 IP가 달라도 같은 Port를 사용할 수 있다. 서로 다른 Server·Container·Pod는 각자 독립된 Network 환경을 가질 수 있기 때문이다.

Gateway는 Eureka에서 Service 이름에 해당하는 Instance를 찾고 요청을 전달할 수 있다.

```text
Client
→ Gateway
→ Eureka에서 BLOG-SERVICE 검색
→ 선택된 Blog Instance로 Routing
```

---

## 15. Config Server

Service마다 동일한 설정을 각각 보관하면 중복과 불일치가 발생할 수 있다.

```text
User Service application.yml
Blog Service application.yml
Comment Service application.yml

공통 설정
├─ Logging
├─ Message Broker
├─ 공통 보안 정책
└─ 환경별 주소
```

Spring Cloud Config Server는 여러 Service의 외부 설정을 중앙에서 관리한다.

```text
Git Repository
  ↓ YAML 설정 저장
Config Server
  ↓ 설정 제공
각 Microservice
```

일반적으로 개발·테스트·운영 Profile에 따라 설정을 분리한다.

```text
user-service-dev.yml
user-service-prod.yml
blog-service-dev.yml
blog-service-prod.yml
```

비밀번호와 Secret Key를 공개 Git Repository에 평문으로 저장해서는 안 된다. Secret Manager, 환경변수 또는 암호화된 설정을 사용해야 한다.

---

## 16. Spring Cloud Bus

Config Repository의 설정이 바뀌었을 때 모든 Service를 하나씩 갱신하면 운영이 번거롭다. Spring Cloud Bus는 Message Broker를 통해 설정 변경 Event를 여러 Instance에 전달할 수 있다.

```text
개발자가 Config Git Repository에 Push
  ↓
Config Server가 변경된 설정 제공
  ↓ Refresh Event
Spring Cloud Bus
  ↓ Broadcast
User·Blog·Comment Service가 설정 갱신
```

Cloud Bus는 보통 RabbitMQ 또는 Kafka와 연결한다. Git Push 자체만으로 자동 Broadcast되는 것은 아니며 Webhook, Monitor Endpoint 또는 Refresh 요청 같은 Trigger 구성이 필요하다.

---

## 17. Spring Cloud 전체 구조

```text
                         Git Config Repository
                                  ↓
                            Config Server
                                  ↓
                  Spring Cloud Bus + Message Broker
                                  ↓
Client → API Gateway → Eureka Server에서 Instance 검색
              │
              ├─ User Service    → User DB
              ├─ Blog Service    → Blog DB
              ├─ Comment Service → Comment DB
              └─ Forecast Service→ 기상청 OpenAPI
```

| 구성요소 | 역할 |
| --- | --- |
| API Gateway | 외부 요청의 단일 진입점과 Routing |
| Eureka Server | Service 등록과 검색 |
| Config Server | YAML 등 외부 설정의 중앙 관리 |
| Spring Cloud Bus | 설정 변경 Event를 여러 Instance에 전달 |
| Kafka/RabbitMQ | 비동기 Message와 Event 전달 |
| Kubernetes | Container 배포, 복구, Scale Out·In |

Gateway와 Eureka는 함께 사용할 수 있다.

```text
Gateway
→ 요청 경로를 보고 목적 Service 결정
→ Eureka에서 현재 실행 중인 Instance 검색
→ 선택한 Instance로 요청 전달
```

---

## 18. 오늘의 결론

오늘 구현한 OpenAPI 흐름은 외부 시스템과 통신할 때 필요한 기본 구조를 보여준다.

```text
UriComponentsBuilder → 요청 URL 조립
HttpURLConnection    → HTTP 통신
InputStreamReader    → byte를 UTF-8 문자로 변환
BufferedReader       → 응답 문자열 읽기
ObjectMapper         → JSON을 Java 객체로 역직렬화
Stream.map()         → DTO 데이터 가공
Enum                 → Code를 이름·단위로 변환
```

MSA는 단순히 애플리케이션을 여러 프로젝트로 나누는 것이 아니다. 독립 배포와 확장이 가능한 Service를 만들고, 이들이 Network 장애와 데이터 정합성을 고려하면서 협력하도록 설계하는 Architecture다.

```text
Monolithic
→ 하나의 배포 단위

MSA
→ 독립 Service와 DB
→ Gateway로 요청 진입
→ Eureka로 Service 발견
→ Config Server로 설정 중앙화
→ 필요하면 Kafka/RabbitMQ로 비동기 통신
→ Kubernetes로 Instance 확장과 복구
```

Spring Cloud는 이러한 분산 시스템 Pattern을 Spring 환경에서 구현하기 위한 도구 모음이다.
