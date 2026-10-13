# BE 23일차 - Spring AI Agent와 Tool Calling

## 1. 오늘의 핵심

이전에는 `ChatClient`에 질문을 보내고 문자열이나 DTO를 돌려받는 단순 생성 기능을 만들었다. 오늘은 여기에 애플리케이션의 메서드를 **Tool**로 제공하여, 모델이 상황에 따라 조회와 저장 작업을 선택하는 Agent 구조를 학습했다.

```text
일반 ChatClient
사용자 입력 → Prompt → AI 모델 → 텍스트 또는 DTO 반환

Agent + Tool Calling
사용자 입력 → Agent에게 목표 전달
                    ↓
              사용할 Tool 판단
                    ↓
       Java Tool 실행 → DB·외부 API 접근
                    ↓
             Tool 실행 결과를 모델에 전달
                    ↓
              다음 행동 또는 최종 응답
```

모델이 데이터베이스에 직접 접속하는 것은 아니다. 모델은 Tool 이름과 인수를 선택하고, 실제 Java 메서드 실행과 데이터 접근은 Spring 애플리케이션이 담당한다. Tool은 크게 정보를 가져오는 조회 Tool과 시스템 상태를 바꾸는 실행 Tool로 나눌 수 있다. [Spring AI 공식 문서](https://docs.spring.io/spring-ai/reference/api/tools.html)도 Tool Calling을 모델이 애플리케이션 함수 호출을 요청하고, 애플리케이션이 실행 결과를 다시 모델에 전달하는 Agent의 핵심 구성요소로 설명한다.

## 2. 프로젝트의 전체 구조

```text
React
  │
  ├─ AI 본문 생성 요청 ──────────────┐
  │                                  ↓
  └─ 게시글 저장 요청            BlogController
                                      ↓
                                 BlogAIAgent
                                      ↓
                           ChatClient(blogchatclient)
                                      ↓
                           모델이 필요한 Tool 선택
                            ┌─────────┴─────────┐
                            ↓                   ↓
                 searchBlogkeyword          saveBlog
                      조회 Tool              저장 Tool
                            ↓                   ↓
                         BlogAITool → BlogService → BlogRepository → DB
                            ↑                                      │
                            └──────────── Tool 실행 결과 ──────────┘
                                      ↓
                              모델의 최종 응답
                                      ↓
                         Controller → React 화면 반영
```

각 클래스의 역할은 다음과 같다.

| 구성요소 | 역할 |
| --- | --- |
| `BlogController` | HTTP 요청 수신과 응답 반환 |
| `BlogAIAgent` | 목표·절차·제약을 Prompt로 구성 |
| `BlogAIAgentConfig` | Agent 전용 `ChatClient`와 Tool 등록 |
| `BlogAITool` | 모델이 호출할 수 있는 Java 기능 공개 |
| `BlogService` | 조회·저장 비즈니스 로직 수행 |
| `BlogRepository` | JPA를 이용해 DB 조회·저장 |

## 3. Agent 전용 ChatClient 구성

`BlogAIAgentConfig`에서는 `BlogAITool`을 `ChatClient`의 기본 Tool로 등록했다.

```java
@Configuration
public class BlogAIAgentConfig {

    @Bean
    public ChatClient blogchatclient(
            ChatClient.Builder builder,
            BlogAITool blogAITool,
            ObjectProvider<ToolCallbackProvider> objectProvider) {

        // BlogAITool의 @Tool 메서드를 모든 Agent 요청에 제공한다.
        builder = builder.defaultTools(blogAITool);

        // 연결된 MCP Tool 제공자가 있으면 함께 등록한다.
        ToolCallbackProvider mcpTools = objectProvider.getIfAvailable();

        if (mcpTools != null) {
            builder = builder.defaultToolCallbacks(
                    mcpTools.getToolCallbacks()
            );
        }

        return builder.build();
    }
}
```

### 주요 문법

- `@Configuration`: Bean 설정 클래스임을 나타낸다.
- `@Bean`: 반환 객체를 Spring Container에 등록한다.
- `ChatClient.Builder`: Spring AI 자동 설정이 제공하는 Builder다.
- `defaultTools(blogAITool)`: 해당 클라이언트의 모든 요청에 로컬 Tool을 제공한다.
- `ObjectProvider<T>`: Bean이 존재할 때만 안전하게 가져온다.
- `ToolCallbackProvider`: 여러 Tool Callback을 제공하는 객체다.

`defaultTools()`에 등록된 Tool은 같은 `ChatClient`의 모든 호출에서 사용 가능하다. 따라서 조회만 해야 하는 `generate()`에서도 `saveBlog()`가 모델에 노출된다. 저장처럼 부작용이 있는 Tool은 호출별로 제한하거나 조회 전용·저장 전용 클라이언트를 나누는 것이 안전하다. Spring AI 공식 문서도 위험하거나 파괴적인 Tool은 기본 Tool보다 호출별 Tool 등록을 권장한다.

## 4. Java 메서드를 Tool로 공개하기

`BlogAITool`은 일반 Java 메서드에 `@Tool`을 붙여 모델이 사용할 수 있는 기능으로 공개한다.

```java
@Component
@RequiredArgsConstructor
public class BlogAITool {

    private final BlogService blogService;

    @Tool(
        description = "카테고리와 키워드로 기존 블로그 게시글을 검색한다."
    )
    public List<BlogResponseDTO> searchBlogkeyword(
            Map<String, Object> map) {

        // 조회는 Service에 위임한다.
        return blogService.searchByKeyword(map);
    }

    @Tool(
        description = "사용자가 작성한 블로그 게시글을 실제 DB에 저장한다."
    )
    public BlogResponseDTO saveBlog(BlogRequestDTO request) {

        // 실제 저장은 기존 비즈니스 로직에 위임한다.
        return blogService.insert(request);
    }
}
```

### Tool 설명이 중요한 이유

모델은 메서드 구현 코드를 읽고 판단하는 것이 아니라 Tool의 이름, 설명, 입력 Schema를 보고 호출 여부와 인수를 결정한다.

```text
Tool 이름        → 어떤 기능인지 구분
description      → 언제 사용해야 하는지 판단
매개변수          → 모델이 만들어야 하는 입력 JSON Schema
반환값            → 다음 판단에 사용할 실행 결과
```

따라서 Tool 이름은 Prompt에서 사용하는 이름과 정확히 일치해야 한다.

```text
실제 메서드: searchBlogkeyword
Prompt:       searchBlogKeyword   ← 대소문자가 다르면 혼란 발생
```

또한 `Map<String, Object>`보다 명시적인 매개변수나 Tool 전용 DTO를 사용하면 모델이 입력 구조를 이해하기 쉽다.

```java
public List<BlogResponseDTO> searchBlogkeyword(
        String keyword,
        String category) {
    // ...
}
```

## 5. 기능 1 - 조회 후 본문만 생성하기

본문 생성 기능의 목표는 기존 게시글을 참고하되 DB에는 저장하지 않고, 생성된 문자열만 작성 화면에 전달하는 것이다.

```text
React 작성 화면
  ↓ category, keyword
POST /blogs/ai/agent
  ↓ @RequestBody Map<String, Object>
BlogController
  ↓
BlogAIAgent.generate(map)
  ↓
모델이 searchBlogkeyword 호출 결정
  ↓
BlogAITool → BlogService → BlogRepository
  ↓ 기존 글 목록
모델이 중복을 피한 본문 생성
  ↓ .content()
Controller가 HTTP 201과 문자열 반환
  ↓
React setContent(response.data)
```

```java
public String generate(Map<String, Object> map) {
    String systemPrompt = """
        당신은 SEO를 고려한 블로그 작성 에이전트입니다.

        <절차>
        1. searchBlogkeyword 도구로 기존 글을 검색한다.
        2. 검색 결과를 참고하여 중복되지 않는 본문을 생성한다.
        3. 생성된 본문만 반환한다.
        </절차>

        <규칙>
        - 본문은 300자 이내로 작성한다.
        - saveBlog 도구를 호출하지 않는다.
        - 데이터베이스에 저장하지 않는다.
        </규칙>
        """;

    String userPrompt = """
        %s 카테고리에서 %s 키워드를 기반으로 글을 작성해줘.
        """.formatted(
            (String) map.get("category"),
            (String) map.get("keyword")
        );

    return blogchatclient
            .prompt()              // 이번 AI 요청 구성
            .system(systemPrompt)  // 역할·절차·제약
            .user(userPrompt)      // 현재 요청의 실제 데이터
            .call()                // 모델 호출과 Tool Calling Loop 실행
            .content();            // 최종 본문을 String으로 반환
}
```

`system`은 Agent가 지켜야 할 공통 정책이고, `user`는 이번 요청에서 달라지는 카테고리와 키워드를 담는다. `.formatted()`는 `%s` 자리에 값을 순서대로 삽입한다.

중요한 점은 Prompt의 “`saveBlog`를 호출하지 않는다”가 보조 지침일 뿐, 시스템 수준의 접근 제어는 아니라는 것이다. 확실히 저장을 막으려면 생성 요청에는 `saveBlog` Tool 자체를 제공하지 않는 구조가 필요하다.

## 6. 기능 2 - 중복 검사 후 게시글 저장하기

글 작성 버튼에서는 조회만 하는 것이 아니라 중복 여부를 판단하고, 중복이 없을 때 실제 DB 저장까지 실행한다.

```text
React 글 작성 버튼
  ↓ category, title, content, email
POST /blogs/insert
  ↓ BlogRequestDTO
BlogController.insert()
  ↓
BlogAIAgent.insert(request)
  ↓
① searchBlogkeyword 호출
  ↓
기존 글 존재?
  ├─ YES → saveBlog 호출 금지 → 기존 글 반환
  └─ NO  → saveBlog(request) 호출
                         ↓
                    BlogService.insert()
                         ↓
                 인증 사용자 조회
                         ↓
                 BlogRepository.save()
                         ↓
                  BlogResponseDTO 반환
                         ↓
② Tool 결과를 모델에 다시 전달
  ↓
최종 응답을 BlogResponseDTO로 변환
  ↓
Controller → HTTP 201 → React 목록 화면 이동
```

```java
public BlogResponseDTO insert(BlogRequestDTO request) {
    String systemPrompt = """
        당신은 게시글 중복 여부를 확인하고 저장하는 발행 에이전트입니다.

        <절차>
        1. searchBlogkeyword로 유사 게시글을 검색한다.
        2. 결과가 있으면 saveBlog를 호출하지 않는다.
        3. 결과가 없을 때만 saveBlog를 호출한다.
        4. 저장 결과를 최종 응답으로 반환한다.
        </절차>

        <규칙>
        - category, title, content, email을 임의로 변경하지 않는다.
        - 각 Tool은 최대 한 번만 호출한다.
        </규칙>
        """;

    String userPrompt = """
        <게시글>
            <category>%s</category>
            <title>%s</title>
            <content>%s</content>
            <email>%s</email>
        </게시글>
        """.formatted(
            request.getCategory(),
            request.getTitle(),
            request.getContent(),
            request.getEmail()
        );

    return blogchatclient
            .prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .call()
            .entity(BlogResponseDTO.class);
}
```

### `.content()`와 `.entity()`

| 목적 | 메서드 | 반환 타입 |
| --- | --- | --- |
| 생성한 본문을 그대로 사용 | `.content()` | `String` |
| 필드가 정해진 게시글 결과 사용 | `.entity(BlogResponseDTO.class)` | `BlogResponseDTO` |

`.entity()`는 AI 응답을 DTO로 변환하는 기능이다. 이 메서드 자체가 DB에 데이터를 저장하는 것은 아니다. 실제 저장은 모델이 `saveBlog`를 요청하고 애플리케이션이 그 Tool을 실행했을 때 발생한다.

## 7. Agent Loop

Tool Calling은 한 번의 모델 호출로 끝나지 않을 수 있다.

```text
1. 애플리케이션이 Prompt와 Tool 명세를 모델에 전달
2. 모델이 Tool 호출 여부와 인수 결정
3. Spring AI가 해당 Java 메서드 실행
4. Tool 결과를 대화 기록에 추가
5. 결과가 포함된 내용을 모델에 다시 전달
6. 모델이 다음 Tool을 호출하거나 최종 응답 생성
```

블로그 저장 기능에서는 다음과 같은 반복이 가능하다.

```text
모델 → searchBlogkeyword 요청
앱   → 검색 실행 후 빈 목록 반환
모델 → saveBlog 요청
앱   → 저장 후 BlogResponseDTO 반환
모델 → 최종 결과 생성
```

OpenAI 공식 API에서도 모델 요청에 Custom Function Tool을 제공할 수 있으며, Tool 선택을 모델에 맡기거나 특정 Tool 사용을 제한할 수 있다고 설명한다. [OpenAI 공식 Tool 문서](https://developers.openai.com/api/reference/cli/resources/responses/methods/create)

## 8. 순환 의존성이 발생했던 이유

초기에는 다음 의존 관계 때문에 Spring Bean 생성이 실패했다.

```text
BlogAIAgent
  → blogchatclient
      → BlogAITool
          → BlogService
              → ChatClient
                  → 다시 blogchatclient
```

`blogchatclient`를 생성하려면 `BlogAITool`이 필요하고, `BlogAITool`을 생성하려면 `BlogService`가 필요한데, `BlogService`가 다시 같은 `ChatClient`를 요구하면서 고리가 생긴 것이다.

현재는 기존 `BlogService`의 `ChatClient` 의존성을 제거하고 역할을 분리했다.

```text
BlogAIAgent → ChatClient 담당
BlogAITool  → Agent가 호출할 기능 제공
BlogService → DB 비즈니스 로직 담당
```

Tool이 사용하는 Service는 다시 Agent나 Agent용 ChatClient를 의존하지 않도록 설계해야 한다.

## 9. 외부 OpenAPI 연결 흐름

오늘은 기상청 해변 예보 OpenAPI 연결 과정도 확인했다. 현재 날씨 기능은 아직 `@Tool`로 등록하지 않았지만, 이후 날씨 조회 Tool로 확장할 수 있다.

```text
POST /openapi/fcst
  ↓ ForecastRequestDTO
ForecastController
  ↓
ForecastService.connection()
  ↓ UriComponentsBuilder로 URL 생성
기상청 OpenAPI
  ↓ JSON 응답
readString()
  ↓ 문자열 결합
parseJson()
  ↓ ObjectMapper + JsonNode
List<ForecastResponseDTO>
  ↓
Controller → React
```

### HTTP 상태와 업무 결과는 다르다

```text
HTTP 200
→ 외부 서버 연결과 응답 수신 성공

resultCode 03 / NO_DATA
→ 요청 조건에 해당하는 예보 데이터가 없음
```

`NO_DATA` 응답에는 `item`이 없으므로 곧바로 `item.isArray()`를 호출하면 `NullPointerException`이 발생한다.

```java
JsonNode resultCode = node.at("/response/header/resultCode");

if (!"00".equals(resultCode.asText())) {
    return List.of(); // 데이터 없음 또는 API 업무 오류
}

JsonNode items = node.at("/response/body/items/item");

if (!items.isArray()) {
    return List.of();
}
```

외부 API도 Tool로 등록하면 Agent가 사용자의 질문을 보고 날씨 조회가 필요한지 판단한 뒤 `ForecastService`를 호출할 수 있다. 다만 API Key는 모델에 전달하지 않고 서버 설정에서 관리해야 한다.

## 10. Java에서 외부 HTTP API를 호출하는 방법

Java가 HTTP에 접근할 수 없는 것은 아니다. 네트워크 통신을 하려면 표준 라이브러리 또는 외부 HTTP Client가 필요하다.

```text
Java 표준 라이브러리
├─ java.net.HttpURLConnection
└─ java.net.http.HttpClient

외부 라이브러리
└─ OkHttpClient
```

오늘 기상청 API 실습에서는 `java.net.URL`과 `HttpURLConnection`으로 연결하고, 응답 문자열은 Jackson의 `ObjectMapper`로 DTO에 매핑했다.

```text
Java 객체
  ↓ URL·Query Parameter 구성
HTTP Request
  ↓ 외부 서버
JSON 문자열
  ↓ ObjectMapper
JsonNode 또는 Response DTO
```

실무에서는 OkHttp로 HTTP 통신을 구성하고 Jackson `ObjectMapper`로 JSON을 객체화하는 형태도 자주 사용한다. `ChatClient`는 이보다 상위 수준에서 Prompt, 모델 호출, 구조화된 응답과 Tool Calling을 다루는 Spring AI 추상화다. 현재 프로젝트의 제공자가 OpenAI일 뿐 `ChatClient` 자체가 GPT 전용인 것은 아니다.

## 11. LangChain과 RAG의 개념적 흐름

LangChain은 Prompt, Retriever, Model, Tool 등을 연결하여 LLM 애플리케이션의 흐름을 구성하는 프레임워크다. 오늘 프로젝트에서는 Spring AI를 사용했으며 LangChain 코드를 직접 작성한 것은 아니지만, RAG의 흐름은 다음과 같이 이해할 수 있다.

```text
사용자 질문
  ↓ Embedding 변환
검색 질의 Vector
  ↓ 유사도 검색
Vector DB에 저장된 문서 Chunk와 Embedding
  ↓ 관련 Chunk 검색
사내 문서·도메인 정보를 Prompt Context에 추가
  ↓
LLM이 질문 + 검색 자료를 바탕으로 답변
```

Vector DB의 데이터가 단순한 1차원 값이라는 뜻은 아니다. 문서 Chunk를 의미를 표현하는 고차원 숫자 배열인 Embedding Vector로 변환하고, 질문 Vector와 가까운 문서를 검색한다. RAG는 모델 자체를 다시 학습시키는 Fine-tuning과 다르게, 답변 시점에 관련 자료를 찾아 입력 Context를 보강한다. OpenAI Vector Store 검색도 질의를 받아 관련 문서 Chunk와 유사도 점수를 반환한다.

## 12. Agent 기반 CRUD 구조

기존 CRUD와 오늘의 Agent 구조를 비교하면 다음과 같다.

```text
기존 CRUD
React → Controller → Service → Repository → DB

Agent 적용 CRUD
React → Controller → Agent → CRUD Tool → Service → Repository → DB
```

`BlogAIAgent`는 사용자의 목표를 해석하고 Tool 실행 순서를 조정하는 역할이므로 현재 코드에서는 `@Service` Bean으로 등록했다. 다만 JPA 저장 로직 자체는 Agent에 작성하지 않고 `BlogService`와 `BlogRepository`에 남겨 역할을 분리했다.

## 13. INSERT와 쓰기 Tool의 안전성

조회 Tool은 데이터를 읽지만 `saveBlog` 같은 쓰기 Tool은 DB 상태를 바꾼다.

```text
조회 Tool
searchBlogkeyword → 기존 게시글 조회 → 상태 변경 없음

쓰기 Tool
saveBlog → INSERT 실행 → DB 상태 변경
```

오늘 코드에서는 INSERT 요청을 바로 `BlogService.insert()`로 보내지 않고 Agent가 먼저 중복 조회를 수행한 뒤, 조건을 만족할 때 `saveBlog`를 호출하도록 설계했다.

```text
POST /blogs/insert
  ↓
BlogAIAgent.insert()
  ↓
searchBlogkeyword
  ├─ 중복 있음 → 저장 중단
  └─ 중복 없음 → saveBlog → BlogService.insert()
```

하지만 이것만으로 쓰기 작업이 완전히 안전해지는 것은 아니다. LLM의 판단과 Prompt는 애플리케이션의 강제 규칙이 아니며, 현재 코드는 모델이 `saveBlog`를 선택하면 실제 INSERT를 실행한다. 더 안전하게 만들려면 다음 책임을 Java 코드가 가져야 한다.

- 요청 DTO와 인증 사용자 검증
- 중복 여부의 최종 판단
- 저장 Tool에 전달되는 값 검증
- Tool별 허용 범위 제한
- 중복 호출을 막는 멱등성 또는 DB Constraint
- 중요한 작업의 사용자 승인

```text
LLM
→ 저장 요청 제안 또는 Tool 호출 요청

Application
→ 입력·권한·중복·정책 검증
→ 허용된 경우에만 실제 INSERT
```

프롬프트에 `saveBlog를 호출하지 마`라고 적는 것보다 생성 요청에서 저장 Tool 자체를 제외하는 편이 강한 통제다. 공식 OpenAI 문서도 전체 Tool 중 현재 호출에서 허용할 Tool만 제한하면 예측 가능성과 안전성을 높일 수 있다고 설명한다.

## 14. Docker에서 EKS까지의 배포 흐름

```text
Spring Boot 애플리케이션
  ↓ Dockerfile로 Build
Docker Image
  ↓ Registry Push
AWS ECR
  ↓ Image Pull
AWS EKS의 Kubernetes Pod
  ↓
Container 실행
```

- Docker Image: 애플리케이션과 실행 환경을 담은 불변 배포 단위
- Container: Image를 실행한 프로세스
- ECR: AWS의 Container Image Registry
- EKS: AWS가 관리하는 Kubernetes 환경
- Kubernetes: Container 배포, 확장, 복구와 네트워킹을 관리하는 Orchestrator

## 15. 현재 코드에서 확인할 부분

### Tool 이름 일치

`searchBlogkeyword`와 `searchBlogKeyword`가 섞여 있으므로 실제 Tool 이름과 Prompt를 통일해야 한다.

### Tool 입력 일치

생성 요청은 `keyword`, `category`를 보내지만 저장 요청은 `title`, `content`, `category`, `email`을 보낸다. 검색 Tool이 어떤 값을 키워드로 사용할지 명확히 정해야 한다.

### 저장 Tool 노출 범위

`defaultTools(blogAITool)` 때문에 `generate()`에도 `saveBlog()`가 제공된다. 조회 전용 요청에는 조회 Tool만 제공하는 방식이 더 안전하다.

### DTO와 최종 JSON 구조 일치

`.entity(BlogResponseDTO.class)`를 사용한다면 최종 응답의 필드명이 다음 DTO 구조와 맞아야 한다.

```text
blogId, title, content, category, email, comments
```

### 인증 사용자 정보 사용

클라이언트가 보낸 `email`을 그대로 신뢰하기보다 Spring Security Context의 인증 사용자 이메일을 기준으로 저장해야 한다.

## 16. 오늘의 결론

오늘 구현한 핵심은 ChatClient로 텍스트만 생성하는 단계를 넘어, 모델이 애플리케이션 기능을 선택하도록 만든 것이다.

```text
Prompt       → Agent의 목표·절차·제약
ChatClient   → 모델 호출과 Tool Calling Loop
@Tool        → 모델에 공개할 애플리케이션 기능
BlogAITool   → 조회·저장 기능의 진입점
BlogService  → 실제 비즈니스 로직
Repository   → 실제 데이터 영속성 처리
```

Agent는 모든 작업을 직접 처리하는 객체가 아니다. **어떤 기능이 필요한지 판단하고, 애플리케이션이 허용한 Tool을 선택하며, 그 실행 결과를 바탕으로 다음 행동이나 최종 응답을 만드는 조정자**로 이해할 수 있다.
