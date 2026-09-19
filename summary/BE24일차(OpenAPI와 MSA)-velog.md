# [LG CNS AM INSPIRE CAMP] TIL 24일차 - Java OpenAPI 통신과 MSA·Spring Cloud

오늘은 Spring Boot에서 기상청 OpenAPI를 호출하고, JSON 응답을 DTO 목록으로 변환해 React 화면에 출력하는 흐름을 구현했다. 이후 하나의 애플리케이션으로 구성된 Monolithic Architecture의 한계를 살펴보고, MSA와 Spring Cloud의 주요 구성요소를 학습했다.

오늘 학습한 전체 흐름은 다음과 같다.

```text
React
  ↓ 해수욕장·날짜·시간 선택
Spring Boot Controller
  ↓ Request DTO
ForecastService
  ↓ HTTP GET
기상청 OpenAPI
  ↓ JSON Text
ObjectMapper
  ↓ Response DTO 목록
Stream + Enum으로 데이터 가공
  ↓ JSON Response
React 화면 렌더링
```

후반부에는 이와 같은 기능들을 독립된 Service로 분리했을 때 필요한 구조를 학습했다.

```text
Monolithic
  ↓ 기능별 독립 배포와 확장의 필요성
MSA
  ↓
Gateway · Eureka · Config Server · Cloud Bus
```

---

## 1. Java는 어떻게 외부 API와 통신할까?

Java가 외부 HTTP 서버에 접근하지 못하는 것은 아니다. 다만 Browser의 `fetch()`처럼 자동으로 통신하는 것이 아니라, JDK의 Network API 또는 별도의 HTTP Client를 사용해야 한다.

오늘 실습에서는 JDK가 제공하는 `java.net` Package를 사용했다.

```java
URL url = new URL(requestUrl);

// URL 주소와 HTTP 연결 객체를 생성한다.
HttpURLConnection http =
        (HttpURLConnection) url.openConnection();

// 외부 서버가 반환한 HTTP 상태 코드를 확인한다.
int status = http.getResponseCode();

if (status == 200) {
    // 응답 Body를 byte Stream으로 받아 다음 단계에 전달한다.
    return readString(http.getInputStream());
}
```

`java.net`은 Framework가 아니라 Java가 기본으로 제공하는 표준 Package다.

```text
java.net.URL
→ 요청할 주소 표현

HttpURLConnection
→ HTTP 연결과 응답 상태 처리

InputStream
→ 응답 Body를 byte Stream으로 제공
```

실무에서는 다음과 같은 방법도 사용할 수 있다.

```text
Java 표준 API
├─ HttpURLConnection
└─ java.net.http.HttpClient

외부 HTTP Client
├─ OkHttp
└─ Apache HttpClient
```

---

## 2. Controller에서 요청 DTO 받기

React는 선택한 해수욕장 번호, 날짜와 발표 시간을 JSON으로 전송한다.

```json
{
  "beach_num": "1",
  "base_date": "20260917",
  "base_time": "1100"
}
```

Spring Controller는 `@RequestBody`로 JSON을 DTO에 Mapping한다.

```java
@RestController
@RequestMapping("/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping("/fcst")
    public ResponseEntity<?> fcst(
            @RequestBody ForecastRequsetDTO request) {

        // React에서 받은 JSON이 ForecastRequestDTO로 변환된다.
        System.out.println(request);

        // 실제 외부 API 통신은 Service에 위임한다.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(forecastService.connection(request));
    }
}
```

요청 데이터는 다음과 같이 바뀐다.

```text
React JSON
{
  "beach_num": "1",
  "base_date": "20260917",
  "base_time": "1100"
}
        ↓ Jackson 역직렬화
ForecastRequestDTO
(
  beach_num=1,
  base_date=20260917,
  base_time=1100
)
```

### 주요 Annotation

| Annotation | 역할 |
| --- | --- |
| `@RestController` | 반환 객체를 JSON Response Body로 변환한다. |
| `@RequestMapping` | Controller의 공통 URL을 지정한다. |
| `@PostMapping` | POST 요청을 특정 Method에 연결한다. |
| `@RequestBody` | 요청 JSON을 Java 객체로 변환한다. |
| `@RequiredArgsConstructor` | `final` 필드를 받는 생성자를 생성한다. |

---

## 3. UriComponentsBuilder로 요청 URL 만들기

외부 OpenAPI는 GET 방식과 Query String으로 호출한다.

```java
String requestUrl = UriComponentsBuilder
        // 기상청 OpenAPI 기본 주소
        .fromUriString(endPoint)

        // Query Parameter를 하나씩 추가한다.
        .queryParam("serviceKey", key)
        .queryParam("beach_num", request.getBeach_num())
        .queryParam("base_date", request.getBase_date())
        .queryParam("base_time", request.getBase_time())
        .queryParam("dataType", type)

        // 이미 Encoding된 인증키를 다시 Encoding하지 않는다.
        .build(true)

        // 최종 URL 문자열로 변환한다.
        .toUriString();
```

`UriComponentsBuilder`는 GET 요청을 전송하는 객체가 아니다. URL과 Query Parameter를 조립한다.

```text
UriComponentsBuilder
→ URL 생성

HttpURLConnection
→ 생성된 URL로 실제 HTTP 통신
```

완성되는 URL은 다음과 같은 형태다.

```text
https://apis.data.go.kr/.../getVilageFcstBeach
?serviceKey=인증키
&beach_num=1
&base_date=20260917
&base_time=1100
&dataType=JSON
```

인증키가 이미 Encoding된 상태라면 `%2B`가 `%252B`로 변하지 않는지도 확인해야 한다. `%25`가 한 번 더 붙었다면 이중 Encoding된 것이다.

---

## 4. InputStream을 바로 문자열로 사용하지 않는 이유

외부 서버의 응답 Body는 `InputStream`으로 들어온다.

```java
InputStream stream = http.getInputStream();
```

`InputStream`은 문자 Stream이 아니라 byte Stream이다. UTF-8 한글은 여러 byte로 구성되므로 Encoding을 지정하지 않고 문자로 처리하면 글자가 깨질 수 있다.

그래서 다음과 같이 Stream을 연결한다.

```java
BufferedReader br = new BufferedReader(
        new InputStreamReader(stream, "UTF-8")
);
```

```text
InputStream
→ byte 읽기

InputStreamReader
→ UTF-8 규칙으로 byte를 문자로 변환

BufferedReader
→ 문자를 Buffer에 모아 한 줄씩 읽기
```

전체 코드는 다음과 같다.

```java
public List<ForecastResponseDTO> readString(
        InputStream stream) throws Exception {

    // byte Stream을 UTF-8 문자 Stream으로 바꾸고
    // BufferedReader로 감싸 한 줄씩 읽는다.
    BufferedReader br = new BufferedReader(
        new InputStreamReader(stream, "UTF-8")
    );

    String input;
    StringBuilder result = new StringBuilder();

    // 응답이 끝날 때까지 한 줄씩 읽는다.
    while ((input = br.readLine()) != null) {
        result.append(input);
    }

    br.close();

    // 완성된 JSON 문자열을 Parsing 단계로 전달한다.
    return parseJson(result.toString());
}
```

데이터 형태는 다음 순서로 바뀐다.

```text
HTTP Response Body
→ byte Stream
→ UTF-8 문자 Stream
→ 한 줄씩 읽은 문자열
→ 하나로 합쳐진 JSON 문자열
```

---

## 5. ObjectMapper로 JSON 문자열 Parsing하기

기상청에서 받은 데이터는 다음과 같은 JSON 문자열이다.

```json
{
  "response": {
    "header": {
      "resultCode": "00",
      "resultMsg": "NORMAL_SERVICE"
    },
    "body": {
      "items": {
        "item": [
          {
            "category": "TMP",
            "fcstValue": "23"
          }
        ]
      }
    }
  }
}
```

`ObjectMapper.readTree()`를 사용하면 JSON을 Tree 구조로 탐색할 수 있다.

```java
// JSON 문자열을 JsonNode Tree로 변환한다.
JsonNode node = objectMapper.readTree(result);

// Tree 내부의 item 배열을 찾는다.
JsonNode item = node.findValue("item");
```

`item`이 배열이면 DTO 배열로 변환한다.

```java
List<ForecastResponseDTO> list = null;

if (item.isArray()) {
    // JSON 배열 → ForecastResponseDTO[]
    ForecastResponseDTO[] array = objectMapper.treeToValue(
        item,
        ForecastResponseDTO[].class
    );

    // DTO 배열 → List
    list = Arrays.asList(array);
}
```

전체 변환 흐름은 다음과 같다.

```text
JSON 문자열
  ↓ ObjectMapper.readTree()
JsonNode
  ↓ findValue("item")
JSON 예보 배열
  ↓ treeToValue()
ForecastResponseDTO[]
  ↓ Arrays.asList()
List<ForecastResponseDTO>
```

`ObjectMapper`가 Text를 JSON으로 만드는 것이 아니다. 외부 API에서 받은 Text가 이미 JSON 형식이며, `ObjectMapper`는 이를 Java 객체로 역직렬화한다.

---

## 6. API는 200인데 데이터가 없을 수도 있다

HTTP `200 OK`는 HTTP 요청과 응답이 성공했다는 의미다. 실제 조회 데이터가 존재한다는 뜻은 아니다.

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

이 응답에는 `item` 배열이 없다. 곧바로 다음 코드를 실행하면 `NullPointerException`이 발생할 수 있다.

```java
item.isArray();
```

따라서 업무 결과 코드를 먼저 확인해야 한다.

```java
JsonNode node = objectMapper.readTree(result);

String resultCode = node
        .at("/response/header/resultCode")
        .asText();

// 기상청 API의 정상 결과가 아니면 빈 목록을 반환한다.
if (!"00".equals(resultCode)) {
    return List.of();
}

JsonNode item = node.at("/response/body/items/item");

// item이 없거나 배열이 아니어도 빈 목록을 반환한다.
if (!item.isArray()) {
    return List.of();
}
```

```text
HTTP Status
→ Network 요청 자체의 성공 여부

resultCode
→ 기상청 업무 처리 결과
```

---

## 7. Stream과 Enum으로 Code를 사용자 데이터로 바꾸기

기상청 응답은 다음처럼 Code 중심이다.

```text
TMP = 기온
POP = 강수확률
PTY = 강수형태
SKY = 하늘상태
```

이를 화면에 그대로 보여주면 사용자가 의미를 알기 어렵다. 그래서 `CategoryCode` Enum에 Code별 이름과 단위를 저장했다.

```java
public enum CategoryCode {
    POP("강수확률", "%"),
    PTY("강수형태", ""),
    PCP("1시간 강수량", "mm"),
    REH("습도", "%"),
    SKY("하늘상태", ""),
    TMP("1시간 기온", "℃"),
    WSD("풍속", "m/s");

    private final String name;
    private final String unit;
}
```

DTO 목록은 `Stream.map()`으로 가공한다.

```java
list = list.stream()
        .map(dto -> {
            // category="TMP"를 TMP Enum 상수로 변환한 뒤
            // 사용자에게 보여줄 이름을 꺼낸다.
            CategoryCode category =
                    CategoryCode.valueOf(dto.getCategory());

            dto.setCategoryName(category.getName());

            // PTY와 SKY처럼 숫자를 의미로 바꿔야 하는 항목을 변환한다.
            String value = CategoryCode.getCodeValue(
                dto.getCategory(),
                dto.getFcstValue()
            );

            // 변환된 값 뒤에 단위를 붙인다.
            dto.setFcstValue(value + category.getUnit());

            // 변경한 DTO를 다음 Stream 단계로 반환한다.
            return dto;
        })
        .toList();
```

데이터 하나가 변하는 과정은 다음과 같다.

```text
변환 전
{
  "category": "TMP",
  "fcstValue": "23",
  "categoryName": null
}

CategoryCode.valueOf("TMP")
→ TMP("1시간 기온", "℃")

변환 후
{
  "category": "TMP",
  "fcstValue": "23℃",
  "categoryName": "1시간 기온"
}
```

`Stream`은 목록의 데이터를 하나씩 Pipeline에 전달하고, `map()`은 각 DTO를 원하는 형태로 바꿔 새로운 목록을 만든다.

---

## 8. React와 카카오맵까지 이어지는 흐름

React에서는 해수욕장 이름, 날짜와 발표 시간을 선택한다.

```text
해수욕장 이름 선택
→ 해수욕장 번호 확인
→ 위도·경도로 카카오맵 이동

날짜 선택
→ 오늘·어제·그저께만 선택 가능

발표 시간 선택
→ 02·05·08·11·14·17·20·23시
```

프론트가 보내는 요청 객체는 다음과 같다.

```javascript
const requestData = {
    // 이름 선택 시 함께 찾은 기상청 해변 Code
    beach_num: beach_num.trim(),

    // date Input의 2026-09-17을 20260917로 변환
    base_date: base_date.replaceAll("-", ""),

    // 기상청 발표 시각
    base_time: base_time.trim()
};

const response = await api.post(
    "/forecast/fcst",
    requestData
);
```

응답 배열은 State에 저장하고 `ForecastList`에 전달한다.

```javascript
const responseList = Array.isArray(response.data)
    ? response.data
    : [];

setForecastList(responseList);
```

```text
response.data
→ ForecastPage의 forecastList State
→ ForecastList
→ map()
→ ForecastItem
```

해수욕장을 선택하면 별첨 위경도 자료에서 해당 위치를 찾아 카카오맵의 중심과 Marker를 이동한다.

```javascript
<KakaoMap
    moveTo={selectedBeach
        ? {
            lat: selectedBeach.lat,
            lng: selectedBeach.lng
        }
        : null}
/>
```

---

## 9. Monolithic Architecture의 한계

기존 프로젝트는 User, Blog, Comment와 Forecast 기능이 하나의 Spring Boot 애플리케이션에 들어 있다.

```text
Client
  ↓
Spring Boot Application
  ├─ User
  ├─ Blog
  ├─ Comment
  └─ Forecast
  ↓
Database
```

이러한 구조를 Monolithic Architecture라고 한다. 처음에는 구조와 배포가 단순하지만 서비스가 커질수록 문제가 생길 수 있다.

- 한 기능의 장애가 전체 애플리케이션에 영향을 줄 수 있다.
- 기능 사이의 결합도가 높아질 수 있다.
- 작은 수정에도 전체 애플리케이션을 다시 배포해야 한다.
- 특정 기능만 Traffic이 증가해도 전체 애플리케이션을 복제해야 한다.
- 기능별로 독립적인 기술과 배포 주기를 적용하기 어렵다.

예를 들어 Blog에만 Traffic이 몰려도 다음처럼 모든 기능을 함께 복제한다.

```text
Instance 1: User + Blog + Comment + Forecast
Instance 2: User + Blog + Comment + Forecast
Instance 3: User + Blog + Comment + Forecast
```

사용량이 적은 기능까지 함께 복제되므로 Resource가 낭비될 수 있다.

---

## 10. MSA와 Scale Out

MSA는 업무 Domain을 기준으로 애플리케이션을 독립된 Service로 분리한다.

```text
Client
  ↓
API Gateway
  ├─ User Service     → User DB
  ├─ Blog Service     → Blog DB
  ├─ Comment Service  → Comment DB
  └─ Forecast Service → 외부 OpenAPI
```

Blog Service에만 Traffic이 증가하면 Blog Instance만 늘릴 수 있다.

```text
Blog Service 1
Blog Service 2
Blog Service 3

User Service 1
Comment Service 1
```

이를 Scale Out이라고 한다.

```text
Scale Up
→ 한 Server의 CPU와 Memory 성능을 높인다.

Scale Out
→ 같은 Service Instance의 개수를 늘린다.
```

Kubernetes는 Container 상태를 확인하고 필요한 Instance를 늘리거나 줄이며, 장애가 난 Container를 다시 실행하는 역할을 수행할 수 있다.

### MSA의 장점

- Service별 독립 배포
- 필요한 Service만 Scale Out
- 장애 범위 격리
- Service별 기술과 DB 선택
- Domain 중심의 개발 조직 구성

### MSA의 단점

- 운영과 배포 구조가 복잡하다.
- Network 지연과 통신 실패를 고려해야 한다.
- 로그가 여러 Service에 분산된다.
- Service별 DB 분리로 Transaction 유지가 어렵다.
- 데이터 정합성과 장애 복구 전략이 필요하다.

Spring Cloud가 이 문제를 전부 자동 해결하지는 않는다. Spring Cloud는 분산 시스템을 구현하는 데 필요한 여러 도구를 제공한다.

---

## 11. Service 통신과 Kafka·RabbitMQ

분리된 Service는 Network를 통해 통신한다.

### 동기 통신

```text
Service A
  ↓ HTTP 요청
Service B
  ↓ 즉시 응답
Service A가 다음 작업 진행
```

REST API, OpenFeign, WebClient 등을 사용할 수 있다.

### 비동기 통신

```text
Service A
  ↓ Event 발행
Kafka 또는 RabbitMQ
  ↓ Event 소비
Service B
```

Service A는 Service B의 작업이 끝날 때까지 기다리지 않는다. Kafka와 RabbitMQ는 이러한 비동기 통신을 지원하는 Message Broker다.

| 구분 | Kafka | RabbitMQ |
| --- | --- | --- |
| 중심 개념 | Event Stream과 Log | Message Queue와 Routing |
| 특징 | 높은 처리량과 Event 재처리 | 유연한 Routing과 작업 Queue |
| 활용 | Event Streaming, Log, Pipeline | 명령 전달, 작업 분배 |

Kafka와 RabbitMQ가 모든 MSA에서 필수인 것은 아니다. 비동기 Event 처리와 Service 결합도 완화가 필요할 때 선택한다.

---

## 12. API Gateway

Client가 모든 Service 주소를 직접 알고 있다면 Service가 늘어날수록 Client도 복잡해진다.

API Gateway는 외부 요청의 단일 진입점이다.

```text
Client
  ↓
API Gateway
  ├─ /users/**    → User Service
  ├─ /blogs/**    → Blog Service
  └─ /forecast/** → Forecast Service
```

Gateway에서는 다음 기능을 공통으로 처리할 수 있다.

- URL에 따른 Routing
- 인증과 인가
- 외부망과 내부망 분리
- Header와 CORS 처리
- 요청 제한
- Logging
- Load Balancing 연계

Spring 환경에서는 Spring Cloud Gateway를 사용할 수 있다.

---

## 13. Eureka Service Discovery

Service가 Scale Out되면 Instance의 IP와 Port가 계속 달라질 수 있다.

```text
BLOG-SERVICE
├─ 10.0.1.11:8080
├─ 10.0.1.12:8080
└─ 10.0.1.13:8080
```

주소를 코드에 직접 작성하면 Instance가 바뀔 때마다 설정을 변경해야 한다. Eureka Server는 실행 중인 Service Instance의 위치를 등록하고 조회하는 Service Discovery 역할을 한다.

```text
Microservice
  ↓ 자신의 Service 이름과 위치 등록
Eureka Server
  ↑ 실행 가능한 Instance 조회
Gateway 또는 다른 Microservice
```

서로 다른 Server나 Container라면 IP가 달라도 같은 Port 번호를 사용할 수 있다.

Gateway와 Eureka는 다음 순서로 연결된다.

```text
Client 요청
→ Gateway가 목적 Service 결정
→ Eureka에서 실행 중인 Instance 조회
→ 선택한 Instance로 요청 전달
```

---

## 14. Config Server와 YAML 중앙 관리

각 Service가 같은 설정을 따로 가지고 있으면 중복과 불일치가 발생한다.

```text
User Service application.yml
Blog Service application.yml
Comment Service application.yml
```

Spring Cloud Config Server는 Service 설정을 외부 Repository에서 중앙 관리하도록 지원한다.

```text
Git Config Repository
  ↓ YAML
Config Server
  ↓ 설정 제공
각 Microservice
```

환경별 설정도 분리할 수 있다.

```text
user-service-dev.yml
user-service-prod.yml
blog-service-dev.yml
blog-service-prod.yml
```

단, 인증키와 비밀번호를 공개 Git Repository에 평문으로 저장하면 안 된다. 환경변수, Secret Manager 또는 암호화된 설정을 사용해야 한다.

---

## 15. Spring Cloud Bus

Config 설정이 변경될 때 모든 Service Instance를 하나씩 갱신하는 것은 번거롭다.

Spring Cloud Bus는 Message Broker를 이용해 설정 변경 Event를 여러 Service에 전달할 수 있다.

```text
Config Git Repository 변경
  ↓
Config Server
  ↓ Refresh Event
Spring Cloud Bus
  ↓ Broadcast
각 Service Instance 설정 갱신
```

Cloud Bus는 일반적으로 Kafka 또는 RabbitMQ와 연결한다. Git Push만으로 무조건 Broadcast되는 것은 아니며, Webhook이나 Monitor Endpoint 등의 Trigger 구성이 필요하다.

---

## 16. Spring Cloud 전체 흐름

```text
                          Git Config Repository
                                   ↓
                             Config Server
                                   ↓
                    Cloud Bus + Kafka/RabbitMQ
                                   ↓

Client → API Gateway → Eureka Server에서 Instance 조회
              │
              ├─ User Service     → User DB
              ├─ Blog Service     → Blog DB
              ├─ Comment Service  → Comment DB
              └─ Forecast Service → 기상청 OpenAPI
```

| 구성요소 | 역할 |
| --- | --- |
| API Gateway | 외부 요청의 공통 진입점과 Routing |
| Eureka Server | Service Instance 등록과 검색 |
| Config Server | YAML 설정의 중앙 관리 |
| Spring Cloud Bus | 설정 변경 Event 전파 |
| Kafka/RabbitMQ | 비동기 Message 전달 |
| Kubernetes | Container 배포, 복구와 Scale Out |

---

## 마무리

오늘 구현에서는 React의 입력값이 Spring Boot를 거쳐 기상청 OpenAPI 요청으로 변환되고, JSON 문자열이 다시 DTO 목록으로 변환되어 화면에 출력되는 전체 흐름을 확인했다.

```text
UriComponentsBuilder
→ URL과 Query Parameter 구성

HttpURLConnection
→ 외부 HTTP 통신

InputStreamReader
→ byte를 UTF-8 문자로 변환

BufferedReader
→ 응답 문자열 읽기

ObjectMapper
→ JSON을 Java 객체로 역직렬화

Stream.map()
→ DTO 가공

Enum
→ Code를 이름과 단위로 변환
```

MSA에서는 각 기능이 독립된 Service가 되면서 개별 배포와 Scale Out이 가능해진다. 대신 Network 장애, 설정 관리, Service 탐색과 분산 Transaction처럼 Monolithic에서는 크게 고려하지 않았던 문제가 생긴다.

Spring Cloud의 Gateway, Eureka, Config Server와 Cloud Bus는 이러한 분산 환경을 구성하기 위한 도구다. 중요한 점은 도구를 먼저 사용하는 것이 아니라, Service를 왜 분리해야 하는지와 분리 이후 어떤 문제가 생기는지를 이해하는 것이다.
