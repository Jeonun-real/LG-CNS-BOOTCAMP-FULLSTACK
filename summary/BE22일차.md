# BE 22일차 - 프롬프트 설계, Spring AI, RAG·MCP·Agent

## 1. 오늘의 핵심 흐름

오늘은 Redis를 사용하는 백엔드 실행 환경 위에서 생성형 AI 기능을 붙이고, 프롬프트 설계와 RAG·MCP·Agent의 역할을 학습했다.

```text
Docker Redis 컨테이너 기동
    ↓
Spring Boot 기동 → Redis 연결 (로그인 RT 저장·삭제)
    ↓
React 요청 → Controller → Service → ChatClient → OpenAI 모델
                                          ↓
                                텍스트 또는 구조화된 응답
                                          ↓
                             Service → Controller → React
```

Redis는 현재 코드에서 Refresh Token 저장소다. AI 답변용 Vector DB나 RAG 저장소로 사용한 코드는 아직 없다. Redis 연결이 필요한 기능을 확인하려면 먼저 컨테이너를 기동한다.

```bash
docker start redis-contiainer
docker ps
```

첫 명령은 이전에 `redis-contiainer`라는 이름으로 만든 컨테이너가 있을 때 사용한다. 새로 만들 때는 `docker run -d --name redis-contiainer -p 6379:6379 redis:7.2`다. 실제 컨테이너 이름은 `docker ps -a`로 확인한다.

## 2. PRD와 5W1H

PRD(Product Requirements Document)는 만들 기능의 목적·사용자·범위·성공 기준을 정리하는 문서다. AI 기능을 설계할 때도 5W1H로 요구사항을 먼저 분명히 한다.

| 질문 | 블로그 AI 본문 생성 예 |
| --- | --- |
| Who | 블로그 작성자 |
| What | 선택한 카테고리와 키워드로 초안 생성 |
| Why | 작성 시작 시간을 줄이기 위해 |
| When | 글 작성 화면에서 생성 버튼을 누를 때 |
| Where | React 작성 화면 → Spring Boot → AI 모델 |
| How | 입력 검증 → 프롬프트 구성 → 생성 → 본문 입력칸 반영 |

PRD에는 입력이 비었을 때의 처리, 최대 길이, 비용·지연, 부적절한 답변 처리, 사용자가 최종 내용을 검토해야 한다는 조건도 적는 편이 좋다.

## 3. 프롬프트 설계의 네 가지 전략

### 3-1. 명확한 지침

역할(페르소나), 작업, 제약, 출력 형식을 분리한다. `좋게 써줘`보다 `200자 이내의 블로그 초안을 작성하고, 검증되지 않은 가게 이름은 만들지 마`가 더 평가하기 쉽다. XML 태그는 조건·예시·참고자료의 경계를 표시하는 한 방법이지 필수 문법은 아니다.

```text
<role>블로그 초안 작성 도우미</role>
<task>카테고리와 키워드로 200자 이내의 초안을 작성</task>
<input><category>여행</category><keyword>강남 산책</keyword></input>
<rules>확인되지 않은 사실을 단정하지 말 것</rules>
<output>본문 텍스트만 반환</output>
```

### 3-2. 참조 텍스트와 예시 제공

- Zero-shot: 예시 없이 지침만 제공한다.
- One-shot: 입력·출력 예시 한 개를 제공한다.
- Few-shot: 여러 예시로 원하는 형식을 보여준다.

예시는 형식과 판단 기준을 구체화하지만, 예시 자체가 틀리면 모델도 잘못된 형식을 따라갈 수 있다. 내부 자료를 참고시킬 때는 자료의 출처와 사용 범위를 분명히 한다.

### 3-3. 하위 작업으로 분할

큰 작업을 `입력 검증 → 자료 찾기 → 초안 생성 → 형식 검증 → 반환`처럼 나눈다. 각 단계의 실패 원인을 구분할 수 있고, 필요한 단계에만 도구를 사용할 수 있다.

### 3-4. 검토와 검증 시간 확보

복잡한 과제에서는 판단 기준과 검토 단계를 지시하고 결과를 검증한다. 단순히 “생각할 시간을 줘”라는 문구가 정확성을 보장하지는 않는다. 예시 입력·출력으로 평가하고 프롬프트를 수정하는 과정이 중요하다.

## 4. 현재 코드: ChatClient로 AI 기능 연결

`ChatClient`는 Spring AI가 제공하는 모델 호출 인터페이스다. 현재 프로젝트는 OpenAI Starter를 연결했지만 `ChatClient` 개념 자체가 OpenAI 전용인 것은 아니다. 기존의 OkHttp 직접 호출과 달리, 모델별 HTTP 요청 구성과 응답 처리의 반복을 줄인다.

```java
RecommandResponseDTO result = chatClient
    .prompt()                     // 요청 구성 시작
    .system("JSON 형태로만 반환해줘.") // 모델의 작업 지침
    .user(prompt)                 // 사용자 입력과 구체적 작업
    .call()                       // 동기 호출 방식 선택
    .entity(RecommandResponseDTO.class); // 응답을 DTO로 변환
```

추천·퀴즈는 구조화된 DTO를 반환하므로 `.entity(DTO.class)`를 사용한다. 블로그 본문 초안은 단순 문자열이므로 `.content()`가 맞다. 실제 `BlogService.contentGenerate()`에는 `.entity(String.class)`가 남아 있어, 모델이 JSON 객체를 반환하면 문자열 역직렬화 오류가 날 수 있다. 이 MD는 코드 동작을 설명하며 소스는 수정하지 않는다.

```java
String content = chatClient
    .prompt()
    .user(prompt)
    .call()
    .content(); // 본문 문자열을 바로 받는 방식
```

`OpenAiController`의 `/openai/recommand`, `/openai/quiz`는 요청을 서비스에 전달하고 `ResponseEntity`의 body로 DTO를 돌려준다. `BlogController`의 `/blogs/ai/agent`는 `{ "category": "...", "keyword": "..." }`를 `Map`으로 받아 `BlogService.contentGenerate()`에 전달한다.

```text
React BlogWritePage.keywordHandler()
  → POST /blogs/ai/agent {category, keyword}
  → BlogController.agent(map)
  → BlogService.contentGenerate(map)
  → ChatClient → 문자열 응답
  → ResponseEntity.body(content)
  → React setContent(response.data)
```

현재 코드에서 서비스는 `map.get("catergory")`를 읽지만 React는 `category`를 보낸다. 키가 달라 카테고리가 `null`이 된다. React 성공 처리의 `Response.data`도 소문자 변수 `response.data`와 다르다. AI 호출 실패와 별개의 데이터 이동 오류다.

## 5. JSON 문자열 → DTO

두 경로를 구분한다.

```text
직접 HTTP 호출: OpenAI 응답 JSON → choices[0].message.content
               → content가 JSON 문자열이면 ObjectMapper.readValue(..., DTO.class)

Spring AI: ChatClient.call().entity(DTO.class)
           → Spring AI의 변환기가 모델 응답을 DTO로 매핑
```

`.entity()`를 쓰면 별도의 `ObjectMapper.readValue()`를 직접 호출할 필요는 없다. 단, 모델이 내놓는 JSON의 필드명·자료형은 DTO와 맞아야 한다. 현재 추천·퀴즈 DTO는 `restaurants`, `quizs`를 `List`로 정의하므로 출력 예시도 객체 중첩이 아니라 배열이어야 한다.

```json
{
  "weather": "맑음",
  "location": "강남",
  "restaurants": [
    {"name": "예시", "category": "한식", "reason": "추천 이유"}
  ]
}
```

프롬프트에서 “JSON만 반환”이라고 지시하는 것과 JSON Schema로 출력 구조를 강제하는 것은 다르다. DTO 변환 실패 가능성을 줄이려면 실제 응답을 검증하고, 모델·도구가 지원하는 구조화 출력 기능을 검토한다.

## 6. LangChain, RAG, Vector DB의 관계

LangChain은 LLM 앱의 여러 단계를 연결하는 프레임워크이며, RAG와 Vector DB는 그 안에서 선택적으로 쓸 수 있는 구성 요소다. `LLM + RAG + Vector DB = LangChain`이라는 등식은 아니다. 현재 Spring Boot 코드는 Spring AI `ChatClient`를 사용하며 LangChain이나 RAG를 직접 구현한 상태는 아니다.

```text
회사 문서 → 분할 → 임베딩 → Vector DB 저장
                                ↓
사용자 질문 → 관련 문서 검색 → 검색 결과를 프롬프트에 추가
                                ↓
                         LLM 답변 + 출처 검증
```

RAG(Retrieval-Augmented Generation)는 답변 시점에 관련 외부 자료를 검색해 모델의 입력에 제공한다. Fine-tuning은 학습 데이터를 이용해 모델의 행동·스타일 등을 조정하는 별도의 과정이다. 따라서 `RAG로 파인튜닝한다`는 표현은 정확하지 않다. RAG는 근거를 제공해 환각을 줄이는 데 도움을 줄 수 있지만, 검색 오류나 자료 자체의 오류를 없애지는 못한다. 회사의 민감한 정보는 접근 권한, 최소 전달, 보관 정책을 먼저 설계해야 한다.

## 7. API, MCP, Agent

API는 프로그램 사이에서 기능·데이터를 요청하는 인터페이스다. MCP(Model Context Protocol)는 AI 애플리케이션이 외부 도구와 자료를 발견하고 호출할 때 쓰는 표준 프로토콜이다. MCP는 모든 API·파일·DB를 자동으로 한꺼번에 연결하거나 모델에 무제한 직접 접근 권한을 주지 않는다. 서버가 노출한 도구, 인증, 허용 범위에 따라 접근이 정해진다.

```text
일반 생성 기능
React → Controller → Service → ChatClient → 모델 → 응답

도구 사용 기능(개념)
React → Controller → Agent/Service → 모델이 도구 사용 결정
                               ↓
                        허용된 Tool 또는 MCP Server
                               ↓
                        도구 결과 → 모델 → 최종 응답
```

Agent를 도입해도 Controller와 Service가 사라지는 것은 아니다. 요청 수신·인증·비즈니스 규칙은 애플리케이션이 담당하고, Agent는 허용된 도구 중 어떤 도구를 언제 호출할지 판단하는 역할을 맡는다. DB·날씨·지도는 각각 Tool로 제공할 수 있고, MCP Server를 통해 노출할 수도 있다. 모든 Tool이 MCP여야 하는 것은 아니다.

- 로컬 MCP: 같은 개발 머신이나 내부 환경에서 클라이언트와 서버를 연결하는 방식.
- 원격 MCP: 네트워크의 외부 서버에 연결하는 방식. 인증·권한·데이터 경계가 더 중요하다.

현재 `build.gradle`에는 MCP Client/Server Starter가 선언돼 있다. 그러나 프로젝트의 블로그 AI 본문 생성은 아직 `ChatClient` 호출만 하며, 모델이 MCP 도구를 선택·호출하는 구현은 확인되지 않았다. `/blogs/ai/agent`라는 URL 이름만으로 Agent 기능이 구현된 것은 아니다.

## 8. 오늘의 개념 구분

| 개념 | 하는 일 | 현재 프로젝트 |
| --- | --- | --- |
| Redis | 빠른 키-값 저장, TTL | 로그인 RT 저장·삭제 |
| ChatClient | 프롬프트와 모델 호출 | 추천·퀴즈·블로그 초안 |
| ObjectMapper | JSON과 Java 객체 변환 | 직접 HTTP 호출 경로에서 사용 |
| RAG | 외부 근거 검색 후 모델에 제공 | 개념 학습, 미구현 |
| Vector DB | 벡터 유사도 검색 | 개념 학습, 미구현 |
| MCP | AI 앱과 외부 도구·자료의 표준 연결 | 의존성 선언, 도구 호출 미구현 |
| Agent | 목적에 맞춰 도구 사용·단계 선택 | 개념 학습, 현 블로그 요청은 일반 생성 |

## 참고 자료

- [OpenAI 공식 문서: 프롬프트 구성과 예시·구분자](https://developers.openai.com/api/docs/guides/latest-model?model=gpt-4.1)
- [OpenAI 공식 API 문서: 도구, MCP, 구조화된 출력](https://developers.openai.com/api/reference/cli/resources/responses/methods/create)
- [Spring AI 공식 문서: ChatClient의 content·entity](https://docs.spring.io/spring-ai/reference/api/chatclient.html)

