# BE 17일차 — 블로그와 댓글: FE 요청부터 BE 처리, 화면 갱신까지

오늘은 회원가입·로그인에 이어 블로그 목록 조회, 글 작성, 상세조회, 댓글 작성을 연결했다. 특히 **게시글 1개에 연결된 댓글 N개를 하나의 응답으로 구성하는 과정**과 **댓글 작성 후 새 댓글 객체를 반환해 React 상태에 추가하는 과정**이 핵심이다.

BE17 메모는 작성 전 비어 있어 현재 `inspire_mybatis`와 `fe/react-app`의 저장된 코드를 기준으로 정리했다. 아래 코드는 핵심 발췌이며 번호 주석은 실행 순서를 설명한다. 개선 예시는 실제 코드와 구분했다.

## 1. 기능별 요청·응답 계약

| 기능 | FE 화면·이벤트 | BE 요청 | 성공 응답 | FE 후속 처리 |
|---|---|---|---|---|
| 글 목록 | BlogIndexPage 진입 | GET /blogs/index | 200 + 게시글 배열 | setBlogs → 목록 렌더링 |
| 글 작성 | 작성 버튼 클릭 | POST /blogs/insert | 201 + 빈 Body | 목록 화면 이동 → 재조회 |
| 상세조회 | 글 카드 클릭 | GET /blogs/read/{id} | 200 + 게시글 객체와 comments 배열 | setBlog, setComments |
| 댓글 작성 | 댓글 작성 버튼 클릭 | POST /comments/insert | 201 + 새 댓글 객체 하나 | 기존 배열에 추가, 입력창 비우기 |

FE의 화면 URL과 BE API URL이 비슷해도 역할은 다르다.

```text
React Router: 어떤 컴포넌트를 보여줄까?
Axios 요청  : 어떤 서버 데이터를 가져올까?

localhost:3000/blogs/read/3 → BlogReadPage 렌더링
localhost:8000/blogs/read/3 → BlogController.read() 실행
```

현재 Java Class의 실제 이름은 `BlogContoroller`다. 이 글의 “Blog Controller”는 해당 Class를 뜻한다.

---

## 2. 공통 구조: 계층별 책임과 DTO

```text
React 이벤트 / useEffect
  ↓ Axios HTTP 요청
Controller
  ↓ 요청 데이터 바인딩, Service 호출
Service
  ↓ 조회·저장·데이터 조합
Mapper Interface
  ↓ namespace + id
Mapper XML
  ↓ JDBC / DB
조회 결과 또는 반영 행 수
  ↓
Service 결과
  ↓ Controller가 상태 코드 + Body 구성
Axios response
  ↓ 상태 갱신
React 렌더링
```

### 주요 DTO 관계

```text
BlogRequestDTO
└── title, content, category, email

BlogResponseDTO
├── id, title, content, category, email
└── List<CommentResponseDTO> comments

CommentRequestDTO
└── id, comment, email, blogId

CommentResponseDTO
└── id, comment, email, blogId
```

`CommentRequestDTO.id`는 현재 FE가 입력하는 값이 아니라 MyBatis의 `selectKey`가 채울 값이다. `blogId`는 댓글이 소속된 게시글 번호다. 둘을 혼동하지 않아야 한다.

---

## 3. 기능 ① 블로그 전체 목록 조회

### 3-1. FE: 화면 진입 시 요청

`BlogIndexPage`의 핵심 코드다.

```javascript
const loadData = async () => {
    // ① 공통 Axios baseURL에 /blogs/index를 붙여 요청한다.
    await api.get('/blogs/index')
        .then(response => {
            if (response.status === 200) {
                // ② BE가 반환한 게시글 배열을 상태에 저장한다.
                setBlogs(response.data);
            }
        })
        .catch(error => console.log(error));
};

useEffect(() => {
    // ③ 화면이 마운트된 뒤 데이터를 요청한다.
    loadData();
}, []);
```

`useEffect(..., [])`는 일반적인 마운트 시점 조회에 사용한다. 개발 환경의 Strict Mode 등에서는 Effect가 추가로 실행될 수 있으므로 “어떤 환경에서도 무조건 한 번”이라는 뜻은 아니다.

### 3-2. BE: Controller → Service → Mapper

```java
@GetMapping("/index")
public ResponseEntity<?> index() {
    // ① 게시글 목록을 Service에 요청한다.
    List<BlogResponseDTO> list = blogService.list();

    // ② List를 HTTP 200 응답 Body로 반환한다.
    return ResponseEntity.status(HttpStatus.OK).body(list);
}
```

```java
@Transactional(readOnly = true)
public List<BlogResponseDTO> list() {
    // ③ MyBatis 목록 조회 결과를 반환한다.
    return blogMapper.findByAll();
}
```

```java
// BlogMapper Interface
List<BlogResponseDTO> findByAll();
```

```xml
<select id="findByAll"
        resultType="com.example.inspire_mybatis.features.blogs.domain.dto.BlogResponseDTO">
    SELECT id, title, content, category, email
    FROM SPRING_BLOG_TBL
</select>
```

각 행은 `BlogResponseDTO`로, 전체 결과는 `List<BlogResponseDTO>`로 반환된다. 정상적인 목록 조회에서 행이 없으면 빈 List이므로 JSON은 `[]`가 된다.

현재 목록 SQL은 댓글을 별도 조회하지 않는다. 상세조회의 댓글 조합 로직과 다른 흐름이다.

### 3-3. FE: 상태에서 카드 목록으로

```javascript
const filteredBlogs = useMemo(() => {
    // ① 전체 또는 선택한 카테고리의 글만 사용한다.
    return selectedCategory === "전체"
        ? blogs
        : blogs.filter(blog => blog.category === selectedCategory);
}, [blogs, selectedCategory]);
```

```jsx
{/* ② 필터링한 배열을 목록 컴포넌트에 전달 */}
<BlogList ary={filteredBlogs || []} />
```

`BlogList`는 배열을 `map()`으로 순회해 `BlogItem` 카드를 만든다. 현재 구현은 배열 index를 key로 사용한다. 항목 식별에는 안정적인 `blog.id`를 사용하는 방식이 더 적합하다.

```text
GET /blogs/index → SELECT 전체 목록 → 200 + JSON 배열
→ setBlogs → 카테고리 필터 → BlogList → BlogItem 카드
```

---

## 4. 기능 ② 블로그 글 작성

### 4-1. FE: 입력값을 JSON Body로 전송

`BlogWritePage`는 title, content, category를 상태로 관리하고 email은 현재 localStorage의 user에서 가져온다.

```javascript
await api.post('/blogs/insert', {
    // ① 화면에서 입력한 내용을 JSON Body로 보낸다.
    title,
    content,
    category,
    email: user
})
.then(response => {
    if (response.status === 201) {
        // ② 생성 성공 후 FE 목록 화면으로 이동한다.
        moveUrl('/blogs/index');
    }
})
.catch(error => console.log(error));
```

Axios POST의 두 번째 인자는 Body 데이터다. 현재처럼 일반 객체를 보내면 JSON으로 전송된다.

### 4-2. BE: @RequestBody → DTO → INSERT

```java
@PostMapping("/insert")
public ResponseEntity<?> insert(@RequestBody BlogRequestDTO request) {
    // ① JSON을 Spring이 BlogRequestDTO로 변환한 상태다.
    int insertFlag = blogService.insert(request);

    // ② INSERT 반영 행 수에 따라 응답한다.
    if (insertFlag != 0) {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
```

```java
public int insert(BlogRequestDTO request) {
    // ③ Mapper INSERT 결과인 반영 행 수를 반환한다.
    return blogMapper.save(request);
}
```

```xml
<insert id="save"
        parameterType="com.example.inspire_mybatis.features.blogs.domain.dto.BlogRequestDTO">
    INSERT INTO SPRING_BLOG_TBL(id, TITLE, content, category, email)
    VALUES(NEXTVAL(BLOG_SEQ), #{title}, #{content}, #{category}, #{email})
</insert>
```

### 중요 문법: 컬럼명과 #{}는 다르다

```text
TITLE       → DB 컬럼명
#{title}    → Java DTO의 title Property 값
NEXTVAL(...) → 시퀀스에서 다음 ID 확보
```

DTO가 `title`인데 `#{TITLE}`로 적으면 MyBatis가 대문자 Property를 찾다가 실패한다. INSERT SQL은 `<select>`가 아니라 `<insert>`에 작성한다.

### 4-3. FE: 빈 Body로도 충분한 이유

글 작성 성공 후에는 목록으로 이동하고 다시 조회한다. 따라서 현재 FE는 응답 Body에 새 글 객체가 없어도 된다.

```text
입력 → POST JSON → @RequestBody → INSERT
→ 201 + 빈 Body
→ moveUrl('/blogs/index')
→ 목록 화면 마운트 → GET /blogs/index
→ 새 글을 포함한 목록 렌더링
```

INSERT 중 예외가 나면 Controller의 else로 자동 이동하지 않는다. 예외는 별도 처리 흐름으로 전파된다.

---

## 5. 기능 ③ 글 클릭 → 상세조회 요청

### 5-1. 카드 클릭은 FE 화면 이동

```jsx
<Wrapper
    onClick={() => {
        // ① 클릭한 글의 번호로 상세 화면 경로를 만든다.
        moveUrl(`/blogs/read/${blog.id}`);
    }}
>
    <TitleText>{blog.title}</TitleText>
</Wrapper>
```

React Router는 다음 경로에 상세 컴포넌트를 연결한다.

```jsx
<Route path="/blogs/read/:blogId" element={<BlogReadPage />} />
```

화면 이동 자체가 BE Controller 호출은 아니다. 상세 컴포넌트가 마운트된 뒤 Axios 요청을 보내야 BE가 실행된다.

### 5-2. 상세 화면에서 URL의 ID로 API 요청

```javascript
// ① React Router 경로의 :blogId를 꺼낸다. 일반적으로 문자열이다.
const { blogId } = useParams();
const at = localStorage.getItem('at');

// loadData() 내부 핵심
const id = blogId;

// ② BE는 /blogs/read/{id}를 기다린다.
await api.get(`/blogs/read/${id}`, {
    // ③ 두 번째 인자는 GET 요청 설정이다.
    headers: { Authorization: at ? at : "" }
})
.then(response => {
    if (response.status === 200) {
        // ④ 게시글과 댓글 목록을 각각의 상태에 저장한다.
        setBlog(response.data);
        setComments(response.data.comments);
    }
})
.catch(error => console.log(error));
```

이전에 `/blogs/read${id}`로 작성하면 `/blogs/read3`이 되어 `/blogs/read/3`과 다른 URL이 된다. 오타가 index로 자동 연결되는 것은 아니다.

### 5-3. BE: PathVariable과 RequestHeader

```java
@GetMapping("/read/{id}")
public ResponseEntity<?> read(
        // ① URL /blogs/read/3의 3을 Integer로 변환해 받는다.
        @PathVariable("id") Integer id,
        // ② 요청 Header에서 Authorization을 읽는다.
        @RequestHeader("Authorization") String at) {

    // ③ 게시글과 댓글을 조합한 DTO를 받는다.
    BlogResponseDTO response = blogService.read(id);

    // ④ 응답 객체를 JSON Body로 반환한다.
    return response != null
            ? ResponseEntity.status(HttpStatus.OK).body(response)
            : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
}
```

`@PathVariable("id")`로 이름을 명시하면 컴파일된 매개변수 이름 정보에 의존하지 않는다.

`@RequestHeader`가 문자열을 읽었다고 인증 검증이 완료되는 것은 아니다. 현재 이 Method는 AT를 받아 출력할 뿐 토큰의 서명·만료·권한을 검증하지 않는다.

현재 Service는 게시글이 없으면 예외를 던진다. 따라서 이 Controller의 null 분기만으로 실제 “없는 글 → 404”가 구현된 것은 아니다.

---

## 6. 핵심: 게시글 1개와 댓글 N개를 합쳐 반환하기

### 6-1. DB와 DTO에서의 1:N

```text
SPRING_BLOG_TBL
└── id = 3
       ↑ 댓글의 BLOGID로 연결
SPRING_COMMENT_TBL
├── id=21, BLOGID=3
├── id=22, BLOGID=3
└── id=23, BLOGID=3
```

게시글 번호 하나에 여러 댓글 행이 연결된다. 실제 FK 제약조건의 존재 여부와 별개로 현재 조회 코드는 이 관계를 `BLOGID` 조건으로 표현한다.

```java
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDTO {
    private Integer id;
    private String title, content, category, email;

    // 게시글 하나가 여러 댓글 DTO를 포함한다.
    private List<CommentResponseDTO> comments;
}
```

DTO에 List를 선언했다고 댓글을 자동 조회하지는 않는다. JPA 연관관계 로딩이 아니라 **MyBatis 쿼리를 Service가 각각 호출해 조합하는 방식**이다.

### 6-2. 두 Mapper를 주입받는 Service

```java
@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogMapper blogMapper;       // 게시글 조회
    private final CommentMapper commentMapper; // 댓글 목록 조회
}
```

`@RequiredArgsConstructor`가 final 필드들을 받는 생성자를 만들고 Spring이 각 Mapper Proxy Bean을 주입한다. Interface에는 `@Mapper`가 있어야 현재 설정에서 등록된다.

### 6-3. 게시글 조회 후 댓글 조회

현재 실행되는 `read()`의 핵심 코드다.

```java
@Transactional(readOnly = true)
public BlogResponseDTO read(Integer id) {
    // ① 게시글 한 개 조회: 없으면 예외를 발생시킨다.
    BlogResponseDTO blog = blogMapper.findById(id)
            .orElseThrow(() -> new RuntimeException(id + "BLOG NOT FOUND"));

    // ② 기존 게시글 값을 복사한 Builder를 만든다.
    return blog.toBuilder()
            // ③ 해당 게시글 번호로 댓글 목록을 별도로 조회해 넣는다.
            .comments(commentMapper.findByBlogId(blog.getId()))
            // ④ 게시글 + 댓글을 포함한 새 DTO를 반환한다.
            .build();
}
```

```xml
<!-- blogMapper.xml: 첫 번째 조회 -->
<select id="findById"
        parameterType="java.lang.Integer"
        resultType="com.example.inspire_mybatis.features.blogs.domain.dto.BlogResponseDTO">
    SELECT id, title, content, category, email
    FROM SPRING_BLOG_TBL
    WHERE ID = #{id}
</select>
```

```xml
<!-- CommentMapper.xml: 두 번째 조회 -->
<select id="findByBlogId"
        parameterType="java.lang.Integer"
        resultType="com.example.inspire_mybatis.features.comments.domain.dto.CommentResponseDTO">
    SELECT ID, COMMENT, EMAIL, BLOGID
    FROM SPRING_COMMENT_TBL
    WHERE BLOGID = #{blogId}
    ORDER BY ID DESC
</select>
```

```text
FE의 상세조회 요청 1회
  ↓
BlogService.read(3)
  ├─ BlogMapper.findById(3)       → 게시글 DTO 한 개
  └─ CommentMapper.findByBlogId(3) → 댓글 DTO List
                  ↓
             toBuilder()
                  ↓
     게시글 + comments 배열을 포함한 DTO
                  ↓
           HTTP 응답 1회
```

**HTTP 요청은 한 번이지만 BE 내부 SELECT는 두 번**이다. 현재 구현은 JOIN 한 번으로 가져오는 방식이 아니다.

### 6-4. toBuilder()의 의미

```java
blog.toBuilder()
    .comments(commentList)
    .build();
```

기존 id, title, content, category, email 등을 Builder에 가져오고 comments만 지정하여 새 객체를 만든다.

- `builder()`: 새 값으로 객체를 구성
- `toBuilder()`: 기존 객체의 필드 값을 바탕으로 구성
- `build()`: 새 객체 생성

`toBuilder()`는 깊은 복사를 보장하지 않는다. 또한 현재 DTO에는 `@Setter`도 있으므로 불변 객체라고 할 수 없다.

`blog.setComments(...)` 역시 기능상 가능한 방식이다. 원본을 수정할지 새 객체를 만들지의 선택이며 Setter가 항상 잘못된 것은 아니다.

### 6-5. readOnly 트랜잭션

`@Transactional(readOnly = true)`는 조회 중심 작업임을 트랜잭션 처리 계층에 알린다. 댓글을 자동 조회하거나 어떤 DB에서도 쓰기를 절대 금지하는 Annotation은 아니다. 현재는 게시글과 댓글 조회를 하나의 Service 작업 범위로 묶는다.

---

## 7. 상세 응답 JSON → 댓글 렌더링

응답 구조 예시다. 값은 설명용이다.

```json
{
  "id": 3,
  "title": "첫 번째 글",
  "content": "본문",
  "category": "개발",
  "email": "author@example.com",
  "comments": [
    {
      "id": 23,
      "blogId": 3,
      "comment": "잘 읽었습니다",
      "email": "reader@example.com"
    }
  ]
}
```

댓글이 없으면 정상적인 MyBatis List 조회 결과는 빈 목록이다. 이를 DTO에 설정하면 다음처럼 반환된다.

```json
{
  "id": 3,
  "title": "첫 번째 글",
  "comments": []
}
```

`comments: null`은 List 필드를 초기화하거나 설정하지 않은 상태일 수 있다. 이전 코드가 게시글 DTO만 반환할 때 댓글이 null이었던 이유다.

### FE 상태 분리

```javascript
// response.data는 게시글 전체 객체다.
setBlog(response.data);

// response.data.comments는 그 안의 댓글 배열이다.
setComments(response.data.comments);
```

```jsx
{/* 게시글 상태는 본문 출력에 사용한다. */}
<TitleText>{blog.title}</TitleText>
<ContentText>{blog.content}</ContentText>

{/* 댓글 상태는 별도 목록 컴포넌트에 전달한다. */}
<BlogCommentList
    comments={comments || []}
    handler={commentDeleteHandler}
    updateHandler={commentUpdateHandler}
/>
```

목록 컴포넌트의 현재 핵심 코드는 다음과 같다.

```jsx
{/* 배열의 댓글 객체마다 아이템 컴포넌트를 만든다. */}
{comments.map((comment, idx) => (
    <BlogCommentItem
        key={idx}
        comment={comment}
        handler={handler}
        updateHandler={updateHandler}
    />
))}
```

### 현재 코드에서 주의할 점

현재 comments 초기값이 `useState({})`다. 댓글은 배열로 다루므로 아래처럼 맞추는 것이 적절하다. 다음은 **개선 예시이며 소스는 변경하지 않았다.**

```javascript
const [comments, setComments] = useState([]);
setComments(response.data.comments ?? []);
```

`{}`는 truthy이므로 `comments || []`가 빈 객체를 배열로 바꿔주지 않는다. `comments.map()`과 `...comments`를 사용하려면 배열이어야 한다.

현재 상세조회 Effect의 의존성은 `[]`다. 같은 컴포넌트를 재사용한 채 blogId만 바뀌는 이동을 지원하려면 id 변경 시 재조회도 고려해야 한다.

---

## 8. 기능 ④ 댓글 작성: FE가 원하는 응답부터 보기

### 8-1. FE 요청

```javascript
await api.post('/comments/insert', {
    // ① 현재 보고 있는 글의 ID를 숫자로 변환한다.
    blogId: Number(blogId),
    comment,    // 입력창 상태
    email: user // 현재 FE에 저장된 사용자
}, {
    // ② POST의 세 번째 인자는 요청 설정이다.
    headers: { Authorization: at ? at : "" }
})
.then(response => {
    if (response.status === 201) {
        // ③ 응답은 저장 건수 1이 아니라 새 댓글 객체여야 한다.
        setComments(ary => {
            return [...ary, response.data];
        });

        // ④ 성공했을 때만 입력창을 비운다.
        setComment('');
    }
})
.catch(error => console.log(error));
```

```text
POST /comments/insert
Header: Authorization
Body:
{
  "blogId": 3,
  "comment": "새 댓글",
  "email": "reader@example.com"
}
```

현재 CommentController는 Header를 읽거나 검증하지 않는다. FE가 Authorization을 보내는 것과 BE가 인증을 수행하는 것은 다르다. 실제 작성자 정보는 인증된 사용자로 확인해야 하며 FE가 보낸 email만 신뢰하면 안 된다.

### 8-2. 왜 숫자를 Body로 보내면 안 되는가?

```javascript
setComments(ary => [...ary, response.data]);
```

`ary`는 현재 댓글 배열이다. 여기에 response.data를 하나 추가한다.

```text
기존 배열: [댓글A, 댓글B]

응답이 객체 → [댓글A, 댓글B, 새 댓글]  ← FE가 원하는 형태
응답이 1    → [댓글A, 댓글B, 1]        ← 렌더링 데이터 구조 불일치
```

그래서 BE는 HTTP 201과 함께 생성된 댓글 객체를 반환해야 한다.

---

## 9. selectKey: INSERT 반영 건수와 새 ID는 별개

현재 댓글 저장은 AUTO_INCREMENT의 generated key 방식이 아니라 **시퀀스를 먼저 조회하는 selectKey 방식**이다.

### 9-1. DTO와 Mapper

```java
// 주요 필드 발췌
public class CommentRequestDTO {
    private Integer id; // FE가 아니라 selectKey가 채운다.
    private String comment, email;
    private Integer blogId;
}
```

```java
@Mapper
public interface CommentMapper {
    List<CommentResponseDTO> findByBlogId(Integer blogId);
    int save(CommentRequestDTO request);
}
```

### 9-2. XML 실행 순서

```xml
<insert id="save"
        parameterType="com.example.inspire_mybatis.features.comments.domain.dto.CommentRequestDTO">

    <!-- ① INSERT보다 먼저 시퀀스 값을 조회한다. -->
    <selectKey keyProperty="id"
               resultType="int"
               order="BEFORE">
        SELECT NEXTVAL(comment_seq)
    </selectKey>

    <!-- ② 조회한 ID가 request.id에 설정된 뒤 아래 INSERT가 실행된다. -->
    INSERT INTO SPRING_COMMENT_TBL(id, COMMENT, EMAIL, BLOGID)
    VALUES(#{id}, #{comment}, #{email}, #{blogId})
</insert>
```

| 속성 | 현재 의미 |
|---|---|
| `keyProperty="id"` | 입력 객체 request의 id에 키를 설정 |
| `resultType="int"` | 키 조회 결과의 Java 타입 |
| `order="BEFORE"` | 키 조회를 INSERT 전에 실행 |
| `#{id}` | selectKey가 채운 ID를 INSERT에 사용 |

`BEFORE`는 키 조회 → 객체에 키 설정 → INSERT 순서다. Mapper INSERT의 반환값은 여전히 반영 행 수이며 키는 파라미터 객체에 설정된다. [MyBatis 공식 문서](https://mybatis.org/mybatis-3/java-api.html)

### 9-3. Service는 반영 건수가 아니라 ID를 반환한다

```java
public int insert(CommentRequestDTO request) {
    // ① selectKey가 request.id를 설정하고 INSERT를 실행한다.
    // save() 자체가 반환하는 반영 행 수는 현재 코드에서 사용하지 않는다.
    commentMapper.save(request);

    // ② 같은 request 객체에서 생성된 댓글 ID를 읽어 반환한다.
    return request.getId();
}
```

```text
FE가 보낸 request.id = null
  ↓ SELECT NEXTVAL(comment_seq), 예: 24
request.id = 24
  ↓ INSERT ... VALUES(24, ...)
Mapper.save() 반환값 = 1        ← 반영 행 수
Service.insert() 반환값 = 24    ← request.getId()
```

둘 다 int여도 의미가 다르다. 또한 키를 확보했다는 사실 자체는 INSERT 성공의 보장이 아니다. 현재 코드에서는 SQL 예외가 발생하면 Service가 정상 반환하지 않는다. 반영 행 수도 명시적으로 확인하는 개선을 고려할 수 있다.

---

## 10. Controller가 댓글 응답 객체를 만드는 과정

현재 댓글 Controller는 새 댓글을 DB에서 다시 조회하지 않는다. **Service에서 받은 ID와 요청 DTO의 값을 합쳐 응답 DTO를 만든다.**

```java
@PostMapping("/insert")
public ResponseEntity<?> insert(@RequestBody CommentRequestDTO request) {
    // ① JSON Body → CommentRequestDTO
    // ② Service는 INSERT 반영 건수가 아니라 새 댓글 ID를 반환한다.
    int id = commentService.insert(request);

    return id != 0
            ? ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponseDTO.builder()
                    // ③ 요청값과 서버에서 생성한 ID를 응답 객체로 묶는다.
                    .blogId(request.getBlogId())
                    .email(request.getEmail())
                    .comment(request.getComment())
                    .id(id)
                    .build())
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
}
```

정상 응답 예시:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 24,
  "blogId": 3,
  "comment": "새 댓글",
  "email": "reader@example.com"
}
```

이전에 설명한 “저장 후 ID로 재조회해서 반환”도 가능한 방식이지만 **현재 구현은 재조회하지 않는 방식**이다. DB 기본값이나 트리거로 바뀌는 필드까지 반환해야 한다면 재조회 또는 다른 반환 설계가 필요할 수 있다.

현재 `id != 0` 분기는 수업 코드의 성공 판정이다. 저장 실패를 모두 BAD_REQUEST로 분류할 수는 없고, 예외가 이 분기로 자동 변환되지도 않는다.

---

## 11. FE: 새 댓글 객체 추가 → 화면 갱신

```javascript
if (response.status === 201) {
    setComments(ary => {
        // ① ary는 업데이트 직전의 댓글 상태 배열이다.
        // ② 기존 배열을 직접 수정하지 않고 새 배열을 반환한다.
        return [...ary, response.data];
    });

    // ③ 입력창의 value로 연결된 상태를 초기화한다.
    setComment('');
}
```

```text
기존 comments
  ↓ ...ary
기존 항목을 담은 새 배열
  ↓ response.data 추가
새 comments 상태
  ↓ React 재렌더링
BlogCommentList의 map()
  ↓
새 BlogCommentItem 표시
```

이 흐름에서는 목록 API를 다시 호출하거나 브라우저를 새로고침하지 않는다. 상태 변경으로 관련 컴포넌트가 다시 렌더링되고 React가 변경된 UI를 반영한다.

댓글 입력창도 상태와 연결돼 있다.

```jsx
<TextInput
    value={comment}
    handler={e => setComment(e.target.value)}
/>
<Button title="댓글 작성" onClick={commentHandler} />
```

### 전체 조회와 새 댓글 추가는 다르다

```javascript
// 상세조회: 전체 댓글 목록으로 교체
setComments(response.data.comments);

// 댓글 생성: 새 댓글 객체 하나를 목록 끝에 추가
setComments(ary => [...ary, response.data]);
```

만약 BE가 숫자만 반환하는 계약이라면 댓글을 추가하는 대신 `loadData()`로 다시 조회해야 한다. 현재 코드는 객체를 반환하므로 직접 추가 방식이다.

### 현재 정렬 순서의 차이

댓글 SELECT는 `ORDER BY ID DESC`인데 FE는 새 댓글을 배열 끝에 추가한다. 따라서 새로고침 전후 순서가 달라질 수 있다.

최신 댓글을 항상 앞에 보여주려면 다음 방식으로 맞출 수 있다. 이는 개선 예시다.

```javascript
setComments(ary => [response.data, ...ary]);
```

현재 목록은 `key={idx}`를 사용한다. 댓글 추가·삭제·정렬 시에는 `key={comment.id}`처럼 안정적인 ID를 사용하는 것이 특히 중요하다.

---

## 12. 상세조회와 댓글 등록을 이어 보면

```text
[글 클릭]
BlogItem → FE /blogs/read/:blogId
  ↓
BlogReadPage → GET /blogs/read/{id}
  ↓
Blog Controller.read()
  ↓
BlogService
  ├─ 게시글 SELECT → BlogResponseDTO
  └─ 댓글 SELECT → List<CommentResponseDTO>
  ↓ toBuilder().comments(...).build()
200 + { 게시글, comments: [...] }
  ↓
setBlog / setComments
  ↓
본문과 댓글 목록 렌더링

[댓글 작성]
입력 comment + 현재 blogId + email
  ↓ POST JSON /comments/insert
CommentController → CommentService
  ↓
selectKey: 댓글 ID 확보 → request.id 설정
  ↓
댓글 INSERT
  ↓
Service에서 ID 반환
  ↓
Controller에서 CommentResponseDTO 구성
  ↓
201 + 새 댓글 객체
  ↓
setComments(기존 배열 + 새 객체)
setComment('')
  ↓
댓글 표시 + 입력창 비우기
```

**상세조회는 여러 데이터를 조합해 전체 화면을 구성하고, 댓글 작성은 새 데이터 하나를 반환해 기존 화면 상태에 반영한다.**

---

## 13. 오늘 오류와 연결되는 주요 문법

| 오류·상황 | 원인과 핵심 |
|---|---|
| `No getter for TITLE` | #{}는 DB 컬럼명이 아닌 DTO Property다. title과 TITLE을 구분 |
| INSERT를 select 태그에 작성 | SQL 종류에 맞춰 insert 태그 사용 |
| `java.lang.Interger` | Integer 철자 오류 |
| `NEXVAL does not exist` | NEXTVAL의 T 누락 |
| `Mapped Statements ... already contains key` | 같은 namespace + id 중복 등록 |
| CommentMapper Bean을 못 찾음 | 현재 방식에서는 Interface에 @Mapper 필요 |
| final commentService 초기화 오류 | @RequiredArgsConstructor 또는 명시적 생성자 누락 |
| 매개변수 이름 정보를 못 찾음 | @PathVariable("id")처럼 이름 명시 |
| comments가 null | 댓글을 조회·설정하지 않은 DTO Field 기본값 |
| 댓글 배열에 숫자가 들어감 | INSERT 건수를 Body로 보내 FE 객체 계약과 불일치 |
| FE가 201에서만 반응 | BE가 200을 반환하면 해당 조건문은 실행되지 않음 |

### Mapper의 namespace

```xml
<!-- 게시글 SQL -->
<mapper namespace="com.example.inspire_mybatis.features.blogs.repository.BlogMapper">

<!-- 댓글 SQL: 별도 파일에서 사용 -->
<mapper namespace="com.example.inspire_mybatis.features.comments.repository.CommentMapper">
```

SQL ID가 같아도 namespace가 다르면 서로 다른 Statement다. XML 파일명이 달라도 namespace와 id가 모두 같으면 중복이다.

### Annotation 역할 정리

| Annotation·문법 | 역할 |
|---|---|
| @RestController | 요청 처리와 응답 Body 반환 |
| @GetMapping / @PostMapping | HTTP Method·경로 연결 |
| @RequestBody | JSON Body → DTO |
| @PathVariable("id") | URL 경로 값 → 매개변수 |
| @RequestHeader("Authorization") | 요청 Header 읽기; 검증과는 별개 |
| @Service / @Mapper | Service Bean / MyBatis Proxy 등록 |
| @RequiredArgsConstructor + final | 생성자 주입 |
| @Transactional(readOnly = true) | 읽기 중심 트랜잭션 설정 |
| @Builder(toBuilder = true) | 기존 값 기반 Builder 생성 지원 |
| Optional.orElseThrow | 조회 결과 없음 처리 |
| selectKey | INSERT 전후 키 조회 및 파라미터 객체에 설정 |
| setState(이전값 => 새값) | 직전 상태를 바탕으로 갱신 |
| 전개 문법 ... | 기존 배열 요소를 새 배열에 펼침 |

---

## 14. 오늘의 핵심 정리

1. React Router 이동과 BE API 호출은 서로 다른 단계다.
2. 목록 조회는 배열을 반환하고 FE가 map()으로 렌더링한다.
3. 글 작성은 201만 확인한 뒤 목록으로 이동해 재조회한다.
4. 상세조회는 게시글 1개와 댓글 N개를 두 번 조회해 하나의 DTO로 조합한다.
5. DTO에 List 필드를 선언한다고 댓글이 자동으로 조회되지는 않는다.
6. toBuilder()로 기존 게시글 값을 유지하며 comments를 넣은 새 DTO를 만들 수 있다.
7. 댓글 Mapper INSERT의 반환값은 저장 건수, 현재 Service 반환값은 selectKey로 만든 댓글 ID다.
8. Controller는 ID와 요청값을 조합해 새 댓글 객체를 반환한다.
9. FE는 그 객체를 기존 배열에 추가하고 입력 상태를 비워 화면을 갱신한다.
10. API 설계는 “저장에 성공했는가?”뿐 아니라 “FE가 응답으로 무엇을 할 것인가?”까지 함께 맞춰야 한다.

정리 범위는 코드 분석과 문서 작성이다. 등록 API나 테스트를 실행해 DB 데이터를 추가하지 않았고, BE·FE 애플리케이션 소스도 변경하지 않았다.
