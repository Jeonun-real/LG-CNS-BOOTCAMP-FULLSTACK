# BE 18일차 - 댓글 삭제·수정, 예외 처리, 유효성 검증

## 1. 오늘 학습한 핵심

오늘은 기존 블로그 상세보기의 댓글 기능을 확장하면서 다음 내용을 학습했다.

- React에서 댓글 삭제·수정 요청 보내기
- `DELETE`, `PATCH`와 `@PathVariable`
- Controller → Service → Mapper → Mapper XML 계층 구조
- MyBatis가 반환하는 영향받은 행의 개수 활용
- `ResponseEntity`로 `204`, `400`, `401`, `404`, `500` 구분
- `@RestControllerAdvice`와 `ErrorResponse`를 이용한 전역 예외 처리
- `@Valid`와 `BindingResult`를 이용한 DTO 유효성 검증
- 검증 오류를 필드별 JSON으로 반환하는 방법

전체 흐름은 다음과 같다.

```text
React 이벤트
  ↓ Axios 요청
Filter
  ↓
DispatcherServlet
  ↓ HandlerMapping
Controller
  ↓
Service
  ↓
MyBatis Mapper Interface
  ↓
Mapper XML
  ↓
Database
  ↓ 영향받은 행 수
ResponseEntity
  ↓
React State 변경·재렌더링
```

---

## 2. Spring 요청 처리 구조 복습

Spring Boot는 내장 Tomcat을 이용해 HTTP 요청을 받고, `DispatcherServlet`이 모든 요청의 단일 진입점인 Front Controller 역할을 한다.

```text
Client → Tomcat → Filter → DispatcherServlet → Controller
```

Spring Container는 `@RestController`, `@Service`, `@Mapper` 등이 붙은 객체를 생성하고 관리한다. 객체 생성 제어권을 Container에 맡기는 것이 IoC이고, 필요한 객체를 외부에서 전달받는 것이 DI다.

```java
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
}
```

`@RequiredArgsConstructor`는 `final` 필드가 포함된 생성자를 만들고, Spring은 Container의 `CommentService` Bean을 주입한다. Controller가 Service 객체를 직접 생성하지 않으므로 계층 간 결합이 줄어든다.

---

## 3. 댓글 삭제 기능

### 3.1 FE에서 댓글 ID 전달

```jsx
<Button
    title="삭제"
    onClick={(e) => handler(e, comment.id)}
/>
```

댓글 내용은 중복될 수 있으므로 DB의 기본키인 `comment.id`로 삭제 대상을 식별한다.

### 3.2 Axios DELETE 요청

```javascript
await api.delete(`/comments/delete/${id}`, {
    headers: {
        Authorization: at ? at : ""
    }
});
```

- 자원 삭제이므로 `DELETE`를 사용한다.
- 삭제할 ID는 URL 경로에 넣는다.
- Axios 설정 이름은 `header`가 아니라 `headers`다.
- 인증이 필요하면 Access Token을 Header에 담는다.

```text
DELETE /comments/delete/10
Authorization: access-token
```

### 3.3 Controller

```java
@DeleteMapping("/delete/{id}")
public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
    int deleteFlag = commentService.delete(id);

    return deleteFlag != 0
        ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
}
```

클래스의 `@RequestMapping("/comments")`와 합쳐져 `/comments/delete/{id}`가 된다. `@PathVariable("id")`는 URL의 `{id}`를 Java 변수에 바인딩한다.

### 3.4 Service, Mapper, SQL

```java
public int delete(Integer id) {
    return commentMapper.delete(id);
}
```

```java
public interface CommentMapper {
    int delete(Integer id);
}
```

```xml
<delete id="delete" parameterType="java.lang.Integer">
    DELETE FROM SPRING_COMMENT_TBL
    WHERE ID = #{id}
</delete>
```

MyBatis는 Mapper XML의 `namespace`와 statement의 `id`로 SQL을 찾는다.

```text
namespace = CommentMapper 전체 패키지 경로
id        = Java Mapper 메서드 이름 delete
```

DELETE 결과는 영향받은 행의 개수다.

```text
대상 존재 → deleteFlag = 1 → 204 No Content
대상 없음 → deleteFlag = 0 → 404 Not Found
```

### 3.5 삭제 후 React State 변경

```javascript
if (response.status === 204) {
    setComments(comments.filter(comment => comment.id !== id));
}
```

DB 삭제 성공 후 같은 ID를 가진 댓글을 State 배열에서도 제외한다. 목록 전체를 다시 요청하지 않고 댓글 영역만 재렌더링할 수 있다.

```text
[1, 2, 3] → filter(id !== 2) → [1, 3]
```

---

## 4. 댓글 수정 기능

### 4.1 수정 모드와 입력값

```javascript
const [isEdit, setIsEdit] = useState(false);
const [mention, setMention] = useState("");

useEffect(() => {
    setMention(comment.comment);
}, []);
```

- `isEdit`: 입력창 활성화 여부
- `mention`: 사용자가 수정 중인 내용
- `useEffect`: 최초 렌더링 시 기존 댓글을 입력창에 설정

```javascript
if (!isEdit) {
    setIsEdit(true);
} else {
    updateHandler(comment.id, mention);
    setIsEdit(false);
}
```

첫 클릭은 수정 모드를 켜고, 두 번째 클릭은 ID와 변경된 내용을 상위 Handler로 전달한다.

### 4.2 Axios PATCH 요청

현재 실습에서는 ID와 댓글 내용을 Path Variable로 전달했다.

```javascript
await api.patch(
    `/comments/update/${id}/${encodeURIComponent(mention)}`,
    null,
    { headers: { Authorization: at ? at : "" } }
);
```

`PATCH`는 객체 전체가 아닌 댓글 내용 일부를 수정한다는 의미다. `encodeURIComponent()`는 한글, 공백, `?`, `#` 등이 URL을 깨뜨리지 않도록 인코딩한다.

> 현재 실제 `BlogReadPage.jsx`에는 ``/comments/update${id}/...``처럼 `update`와 ID 사이 `/`가 빠져 있다. 실행할 때는 반드시 ``/comments/update/${id}/...`` 형태여야 한다.

### 4.3 여러 Path Variable 받기

```java
@PatchMapping("/update/{id}/{comment}")
public ResponseEntity<?> update(
        @PathVariable("id") Integer id,
        @PathVariable("comment") String comment) {

    Map<String, Object> map = new HashMap<>();
    map.put("id", id);
    map.put("comment", comment);

    int updateFlag = commentService.update(map);

    return updateFlag != 0
        ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
}
```

URL 템플릿과 `@PathVariable` 이름은 반드시 같아야 한다.

```java
{comment} ↔ @PathVariable("comment")  // 정상
{commnet} ↔ @PathVariable("comment")  // 오류
```

이름이 다르면 `MissingPathVariableException`이 발생한다.

### 4.4 Map과 UPDATE SQL

수정 SQL에 필요한 `id`, `comment`를 Map 하나로 묶었다.

```java
Map<String, Object> map = new HashMap<>();
map.put("id", id);
map.put("comment", comment);
```

```xml
<update id="update" parameterType="java.util.HashMap">
    UPDATE SPRING_COMMENT_TBL
    SET COMMENT = #{comment}
    WHERE ID = #{id}
</update>
```

```text
#{comment} → map.get("comment")
#{id}      → map.get("id")
```

Map은 간단하지만 key 오타를 컴파일 시점에 찾을 수 없다. 구조와 타입을 명확하게 관리하려면 `id`, `comment` 필드를 가진 DTO로 묶을 수도 있다.

### 4.5 수정 후 부분 재렌더링

```javascript
if (response.status === 204) {
    setComments(comments =>
        comments.map(comment =>
            comment.id === id
                ? { ...comment, comment: mention }
                : comment
        )
    );
}
```

`map()`으로 대상 댓글만 새 객체로 교체한다. 기존 State를 직접 수정하지 않고 새 배열과 객체를 만드는 이유는 불변성을 유지해 React가 변경을 인식하도록 하기 위해서다.

---

## 5. `body()`와 `build()`의 차이

삭제·수정 후 FE가 별도 데이터를 받을 필요가 없다면 `204 No Content`와 `build()`를 사용한다.

```java
ResponseEntity.status(HttpStatus.NO_CONTENT).build();
```

댓글 작성 직후에는 새로 생성된 ID가 포함된 댓글 객체가 필요하므로 body를 반환한다.

```java
return ResponseEntity.status(HttpStatus.CREATED)
    .body(CommentResponseDTO.builder()
        .id(id)
        .blogId(request.getBlogId())
        .email(request.getEmail())
        .comment(request.getComment())
        .build());
```

```text
POST 성공 → 201 + 댓글 객체 → FE 배열에 response.data 추가
DELETE/PATCH 성공 → 204 + body 없음 → FE가 알고 있는 ID로 State 변경
```

---

## 6. 구현 중 확인한 오류

### Invalid bound statement

```text
Invalid bound statement (not found): CommentMapper.delete
```

Mapper Interface에는 `delete()`가 있지만 XML에 `<delete id="delete">`가 없으면 실행할 SQL을 찾지 못한다. 소스 XML을 수정했어도 실행 Classpath의 `build/resources/main`이 갱신되지 않으면 이전 오류가 계속될 수 있으므로 Resource 반영과 서버 재시작도 확인해야 한다.

### Endpoint 불일치로 인한 404

```text
FE: /comments/{id}/{comment}
BE: /comments/update/{id}/{comment}
```

URL이 다르면 Controller에 도달하지 못한다. Controller 로그가 없으면 Mapping 문제이고, 로그가 있지만 404이면 UPDATE 결과가 0인지 확인한다.

### Path Variable 오타

`{commnet}`과 `@PathVariable("comment")`처럼 이름이 다르면 `MissingPathVariableException`이 발생한다. 문자열 기반 Endpoint는 FE와 BE 양쪽을 함께 비교해야 한다.

### Axios Header 오타

```javascript
{ header:  { Authorization: at } } // 잘못됨
{ headers: { Authorization: at } } // 정상
```

---

## 7. 전역 예외 처리

Controller마다 `try-catch`를 반복하면 중복 코드가 생기고 오류 응답 형식도 달라진다. Spring에서는 예외를 위로 전달하고 `@RestControllerAdvice`에서 공통으로 처리할 수 있다.

```text
Service에서 예외 발생
  ↓
DispatcherServlet
  ↓
HandlerExceptionResolver
  ↓
@RestControllerAdvice
  ↓
일치하는 @ExceptionHandler
```

### 사용자 정의 예외

```java
public class LoginFailException extends RuntimeException {
    public LoginFailException(String message) {
        super(message);
    }
}
```

```java
UserResponseDTO response = userMapper.signIn(request)
    .orElseThrow(() -> new LoginFailException("로그인실패"));
```

일반 예외보다 실패 의미를 명확하게 표현할 수 있다.

### GlobalExceptionHandler와 ErrorResponse

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<?> handlerLoginFail(LoginFailException e) {
        ErrorResponse error = new ErrorResponse(e.getMessage());

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(error);
    }
}
```

```java
@Builder
@Getter
@ToString
public class ErrorResponse {
    private String message;
}
```

오류를 단순 문자열이 아닌 객체로 감싸면 FE가 일관된 JSON 구조로 처리할 수 있다.

```json
{
  "message": "로그인실패"
}
```

프로젝트의 `ErrorResponse`를 사용해야 하며, 이름이 같은 `org.springframework.web.ErrorResponse`를 잘못 import하지 않도록 주의한다.

---

## 8. DTO 유효성 검증

### 8.1 DTO에 검증 규칙 선언

```java
public class UserRequestDTO {

    @Email(message = "이메일 형식을 지켜주세요!!")
    @NotBlank(message = "이메일은 필수항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    @Size(
        min = 4,
        max = 20,
        message = "비밀번호는 4자 이상 20자 이하로 입력해야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수항목입니다.")
    private String name;
}
```

- `@NotBlank`: `null`, 빈 문자열, 공백 문자열 차단
- `@Email`: 이메일 형식 검사
- `@Size`: 문자열 길이 검사
- `message`: 검증 실패 시 전달할 메시지

검증 규칙을 DTO에 선언하면 입력 조건을 한곳에서 관리할 수 있다.

### 8.2 `@Valid`와 `BindingResult`

```java
public ResponseEntity<?> signUp(
        @Valid @RequestBody UserRequestDTO request,
        BindingResult bindingResult) {
```

```text
JSON Body
  ↓ Jackson
UserRequestDTO
  ↓ @Valid
Annotation 검사
  ↓
BindingResult에 결과 저장
```

`BindingResult`는 검증 대상 DTO 바로 뒤에 둔다.

### 8.3 검증 실패 응답

```java
if (bindingResult.hasErrors()) {
    Map<String, String> errorMap = new HashMap<>();

    bindingResult.getFieldErrors().forEach(error -> {
        errorMap.put(error.getField(), error.getDefaultMessage());
    });

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorMap);
}
```

검증에 실패하면 즉시 반환하므로 Service와 DB INSERT는 실행되지 않는다. 잘못된 데이터가 Persistence 계층까지 이동하는 것을 요청 경계에서 차단하는 것이다.

```text
유효하지 않은 요청
  ↓
hasErrors() == true
  ↓
400 + 오류 JSON
  ✕ Service
  ✕ Mapper
  ✕ Database
```

Java Map은 Jackson에 의해 다음 JSON으로 변환된다.

```json
{
  "email": "이메일 형식을 지켜주세요!!",
  "password": "비밀번호는 4자 이상 20자 이하로 입력해야 합니다.",
  "name": "이름은 필수항목입니다."
}
```

FE에서는 다음 값으로 확인한다.

```javascript
catch (error) {
    console.log(error.response?.status); // 400
    console.log(error.response?.data);   // 필드별 오류 JSON
}
```

### 8.4 잘못된 상태 코드

```java
ResponseEntity.status(0) // 잘못된 코드
```

HTTP 상태 코드는 세 자리 양의 정수여야 하므로 위 코드는 다음 예외를 발생시킨다.

```text
IllegalArgumentException:
Status code '0' should be a three-digit positive integer
```

유효성 검증 실패에는 의미에 맞는 `400 Bad Request`를 사용한다.

```java
ResponseEntity.badRequest().body(errorMap);
```

---

## 9. Filter, Interceptor, AOP, 예외 처리 위치

```text
Client
  ↓
Filter                JWT 존재·서명·만료 검사
  ↓
DispatcherServlet
  ↓
Interceptor           Controller 전후 공통 처리
  ↓
Controller
  ↓
AOP                    Service 전후 공통 관심사 처리 가능
  ↓
Service → Mapper → DB

예외 발생
  ↓
HandlerExceptionResolver
  ↓
@RestControllerAdvice
```

회원가입·로그인처럼 Token 없이 접근해야 하는 Endpoint는 White List로 관리하고, 댓글 작성·수정·삭제처럼 사용자 확인이 필요한 기능은 Filter 검사를 거치게 구성할 수 있다.

---

## 10. 설계 이유 정리

### 계층을 나눈 이유

```text
Controller  → HTTP 요청·응답
Service     → 기능 흐름과 비즈니스 판단
Mapper      → Java 메서드 형태의 데이터 접근 표준
Mapper XML  → 실제 SQL
```

각 계층의 책임을 분리하면 HTTP 형식과 SQL 중 하나가 변경될 때 다른 계층에 미치는 영향을 줄일 수 있다.

### 삭제·수정에 ID를 사용한 이유

기본키는 각 댓글을 유일하게 구분한다. 따라서 `WHERE ID = #{id}`로 정확한 한 행을 대상으로 삼을 수 있다.

### Mapper가 `int`를 반환하는 이유

INSERT, UPDATE, DELETE는 영향받은 행의 개수를 반환한다. 이를 이용해 성공과 대상 없음의 응답을 구분한다.

### 수정에 PATCH를 사용한 이유

댓글 객체 전체가 아니라 `comment` 속성 일부만 바꾸기 때문이다.

### 오류를 객체나 Map으로 반환한 이유

구조화된 JSON은 FE에서 필드나 속성 단위로 처리하기 쉽다.

```text
error.response.data.message
error.response.data.email
error.response.data.password
```

### 검증 실패 시 바로 반환한 이유

잘못된 입력을 Service와 DB까지 보내지 않아 불필요한 SQL 실행과 잘못된 데이터 저장을 예방하기 위해서다.

---

## 11. 최종 정리

댓글 삭제와 수정은 다음 계층을 모두 연결하는 작업이었다.

```text
FE에서 ID와 변경값 전달
→ HTTP Method와 Endpoint Mapping
→ Controller 요청값 바인딩
→ Service 기능 흐름
→ Mapper Interface와 XML 동기화
→ SQL 영향 행 수로 성공 판단
→ ResponseEntity 상태 코드 반환
→ FE State를 불변 방식으로 변경
→ 댓글 영역 재렌더링
```

실패 흐름도 함께 구성했다.

```text
DTO 입력 오류
→ @Valid
→ BindingResult
→ 400 + 필드별 오류 JSON

로그인 실패
→ LoginFailException
→ HandlerExceptionResolver
→ GlobalExceptionHandler
→ 401 + ErrorResponse
```

핵심은 FE와 BE 사이의 URL·HTTP Method·데이터 형식을 정확히 맞추고, Mapper Interface와 XML의 `namespace`, statement `id`, 매개변수를 동기화하는 것이다. 정상 응답뿐 아니라 검증 실패와 예외 응답도 일정한 JSON 구조로 설계해야 FE가 상황에 맞는 화면을 제공할 수 있다.
