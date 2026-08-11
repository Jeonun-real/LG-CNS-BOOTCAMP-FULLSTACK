# FE 9일차 핵심 개념 정리

## 0. 현재 프로젝트의 컴포넌트 구조

이 프로젝트는 기능을 크게 **앱 진입점, 라우팅, 페이지, 목록, 항목, 공용 UI, API 통신**으로 나누어 구성했다.

```text
public/index.html
└─ <div id="root">
   └─ src/index.js
      └─ <ToyApp />
         └─ BrowserRouter
            ├─ /                         → SignUpPage
            ├─ /users/signIn             → SignInPage
            ├─ /blogs/index              → BlogIndexPage
            │                              └─ BlogList
            │                                 └─ BlogItem × 게시글 수
            ├─ /blogs/write              → BlogWritePage
            │                              ├─ TextInput
            │                              └─ Button
            └─ /blogs/read/:blogId       → BlogReadPage
                                           ├─ BlogCommentList
                                           │  └─ BlogCommentItem × 댓글 수
                                           ├─ TextInput
                                           └─ Button
```

### 폴더 구조

```text
src/
├─ index.js
│  └─ React 애플리케이션의 시작점
│
├─ ToyApp.js
│  └─ URL과 페이지 컴포넌트를 연결하는 라우터
│
├─ api/
│  └─ axios.js
│     └─ Axios 인스턴스와 백엔드 기본 주소 설정
│
├─ components/
│  └─ styled/
│     ├─ Button.jsx
│     └─ TextInput.jsx
│        └─ 여러 페이지에서 재사용하는 공용 UI
│
└─ features/
   ├─ user/
   │  └─ page/
   │     ├─ SignUpPage.jsx
   │     └─ SignInPage.jsx
   │
   └─ blog/
      ├─ page/
      │  ├─ BlogIndexPage.jsx
      │  ├─ BlogWritePage.jsx
      │  └─ BlogReadPage.jsx
      │     └─ URL 하나에 대응하는 전체 화면
      │
      ├─ list/
      │  ├─ BlogList.jsx
      │  └─ BlogCommentList.jsx
      │     └─ 배열을 순회하며 Item 컴포넌트를 반복 생성
      │
      ├─ item/
      │  ├─ BlogItem.jsx
      │  └─ BlogCommentItem.jsx
      │     └─ 게시글 또는 댓글 한 건을 표시
      │
      └─ ui/
         └─ blog.css
            └─ 블로그 기능 전용 스타일
```

### 기능을 나눈 기준

| 구분 | 담당 역할 | 대표 파일 |
|---|---|---|
| 진입점 | HTML의 `root`에 React 앱을 렌더링 | `index.js` |
| 라우터 | URL에 맞는 페이지 컴포넌트 선택 | `ToyApp.js` |
| Page | API 요청, 상태, 이벤트, 화면 전체 구성 관리 | `BlogIndexPage.jsx` |
| List | 배열을 `map()`으로 순회하여 여러 Item 생성 | `BlogList.jsx` |
| Item | 게시글이나 댓글 한 건의 UI와 클릭 처리 | `BlogItem.jsx` |
| 공용 UI | 버튼과 입력창처럼 반복되는 UI 재사용 | `Button.jsx`, `TextInput.jsx` |
| API | 서버 기본 주소와 Axios 설정 관리 | `axios.js` |

### 블로그 목록의 컴포넌트 연결

```text
BlogIndexPage
│
├─ GET /blogs로 게시글 조회
├─ blogs 상태에 응답 데이터 저장
├─ selectedCategory 상태로 카테고리 관리
├─ useMemo로 filteredBlogs 계산
│
└─ <BlogList ary={filteredBlogs} />
      │
      └─ filteredBlogs.map(...)
            │
            └─ <BlogItem blog={blog} />
                  │
                  └─ 클릭 시 /blogs/read/{blog.id}로 이동
```

부모 컴포넌트가 자식에게 데이터를 전달하는 흐름은 다음과 같다.

```jsx
// BlogIndexPage.jsx
<BlogList ary={filteredBlogs} />

// BlogList.jsx
const BlogList = ({ ary }) => {
    return ary.map((blog) => (
        <BlogItem
            key={blog.id}
            blog={blog}
        />
    ));
};

// BlogItem.jsx
const BlogItem = ({ blog }) => {
    return <div>{blog.title}</div>;
};
```

```text
filteredBlogs
    ↓ ary props
BlogList
    ↓ blog props
BlogItem
```

### 게시글 상세와 댓글의 컴포넌트 연결

```text
BlogReadPage
│
├─ useParams()로 URL의 blogId 추출
├─ GET /blogs/{blogId}?_embed=comments
├─ blog 상태에 게시글 저장
├─ comments 상태에 댓글 배열 저장
│
└─ <BlogCommentList
       comments={comments}
       handler={commentDeleteHandler}
   />
      │
      └─ comments.map(...)
            │
            └─ <BlogCommentItem
                   comment={comment}
                   handler={handler}
               />
```

댓글 데이터는 부모에서 자식으로 내려가고, 댓글 삭제 이벤트는 자식이 부모에게 전달받은 함수를 호출하여 처리한다.

```text
데이터 전달: BlogReadPage → BlogCommentList → BlogCommentItem
이벤트 호출: BlogCommentItem → 부모에게 받은 삭제 handler 실행
상태 변경:   BlogReadPage가 comments 상태 변경 → 댓글 목록 재렌더링
```

### 전체 데이터 흐름

```text
db.json
   ↓ json-server
Axios API 요청
   ↓
Page 컴포넌트
   ↓ useState에 응답 저장
List 컴포넌트
   ↓ map()으로 반복
Item 컴포넌트
   ↓
화면 출력 또는 사용자 이벤트 발생
   ↓
상태 변경 / API 요청 / URL 이동
   ↓
React 재렌더링
```

> 핵심은 **Page가 데이터와 상태를 관리하고, List가 반복을 담당하며, Item이 데이터 한 건을 표현하는 구조**이다.

---

## 1. 동일 출처 정책과 CORS

브라우저는 보안을 위해 기본적으로 현재 웹 페이지와 **동일한 출처(Origin)**의 리소스에만 자유롭게 접근할 수 있다. 이를 **동일 출처 정책(Same-Origin Policy)**이라고 한다.

출처는 다음 세 가지의 조합으로 판단한다.

```text
프로토콜 + 도메인 + 포트
```

예를 들어 다음 두 주소는 포트가 다르기 때문에 서로 다른 출처이다.

```text
프론트엔드: http://localhost:3000
백엔드:     http://localhost:5000
```

### CORS

**CORS(Cross-Origin Resource Sharing)**는 서로 다른 출처 간의 리소스 요청을 서버가 허용할 수 있도록 하는 정책이다.

```text
React(:3000)
    ↓ 다른 출처로 요청
API 서버(:5000)
    ↓ CORS 허용 응답 헤더 반환
브라우저가 응답 사용을 허용
```

> `cores`, `crossorgin`이 아니라 `CORS`, `Cross-Origin`이 올바른 표기이다.

---

## 2. Preflight 요청

**Preflight**는 브라우저가 실제 요청을 보내기 전에 해당 요청이 허용되는지 확인하는 사전 요청이다.

브라우저는 서버에 `OPTIONS` 요청을 보내 다음 내용을 확인한다.

- 요청을 보낸 출처가 허용되는가?
- 사용하려는 HTTP 메서드가 허용되는가?
- 요청에 포함된 헤더가 허용되는가?

```text
브라우저
   │
   ├─ OPTIONS 요청 ──────→ 서버
   │                      CORS 허용 여부 확인
   │
   └─ POST/PUT/DELETE ───→ 서버
          실제 요청
```

모든 요청에 Preflight가 발생하는 것은 아니다. 조건을 만족하는 단순 요청은 사전 요청 없이 바로 전송될 수 있다.

---

## 3. Axios GET 요청 문법

### 3.1 Query String을 URL에 직접 작성

```jsx
api.get("/blogs?category=개발&email=user@test.com");
```

Query String의 여러 조건은 `&` 하나로 연결한다.

```text
?category=개발&email=user@test.com
```

> JavaScript의 논리 연산자 `&&`가 아니라 Query String 구분자인 `&`를 사용한다.

### 3.2 `params` 옵션 사용

검색 조건을 객체 형태로 전달할 수 있다.

```jsx
api.get("/blogs", {
    params: {
        category: "개발",
        email: "user@test.com"
    }
});
```

Axios가 이를 다음과 같은 Query String으로 변환한다.

```text
/blogs?category=개발&email=user@test.com
```

### 3.3 Path Parameter 사용

특정 게시글 한 건을 조회할 때 사용한다.

```jsx
const id = 3;

api.get(`/blogs/${id}`);
```

실제 요청 주소는 다음과 같다.

```text
/blogs/3
```

정리하면 검색 조건은 Query String을, 특정 자원 조회는 Path Parameter를 사용한다.

```jsx
// 조건 검색
api.get("/blogs", {
    params: {
        category: "개발"
    }
});

// 특정 게시글 조회
api.get(`/blogs/${id}`);
```

---

## 4. 1:N 관계와 댓글 조회

게시글 하나에는 여러 댓글이 연결될 수 있다. 따라서 게시글과 댓글은 **1:N 관계**이다.

```text
Blog 1
├─ Comment 1
├─ Comment 2
└─ Comment 3
```

댓글의 `blogId`가 게시글의 `id`를 참조한다.

```json
{
    "blogs": [
        {
            "id": 1,
            "title": "React 정리"
        }
    ],
    "comments": [
        {
            "id": 1,
            "blogId": 1,
            "comment": "좋은 글입니다."
        }
    ]
}
```

### 게시글만 조회

```jsx
api.get(`/blogs/${blogId}`);
```

### 댓글을 포함한 게시글 조회

`json-server`에서는 `_embed`를 이용해 연관된 댓글을 함께 조회할 수 있다.

```jsx
api.get(`/blogs/${blogId}?_embed=comments`);
```

`params` 옵션으로 작성할 수도 있다.

```jsx
api.get(`/blogs/${blogId}`, {
    params: {
        _embed: "comments"
    }
});
```

응답 예시는 다음과 같다.

```json
{
    "id": 1,
    "title": "React 정리",
    "content": "내용",
    "comments": [
        {
            "id": 1,
            "blogId": 1,
            "comment": "좋은 글입니다."
        }
    ]
}
```

---

## 5. `useMemo`

`useMemo`는 계산 결과를 저장하고, 의존하는 값이 변경됐을 때만 다시 계산하여 **memoized value**를 반환하는 React Hook이다.

> `memorized`가 아니라 `memoized`, `memorization`이 아니라 `memoization`이 정확한 용어이다.

### 기본 문법

```jsx
const memoizedValue = useMemo(() => {
    return 계산결과;
}, [의존값1, 의존값2]);
```

### 블로그 카테고리 필터링

```jsx
const filteredBlogs = useMemo(() => {
    return selectedCategory === "전체"
        ? blogs
        : blogs.filter(
            (blog) => blog.category === selectedCategory
        );
}, [blogs, selectedCategory]);
```

`blogs` 또는 `selectedCategory`가 변경되면 필터링을 다시 실행한다. 두 값이 변경되지 않았다면 이전 계산 결과를 재사용한다.

```text
컴포넌트 렌더링
    ↓
의존성 변경 여부 확인
    ├─ 변경됨     → 함수 재실행 → 결과 저장
    └─ 변경 안 됨 → 기존 결과 재사용
```

### 의존성 배열에 따른 실행

#### 특정 의존값을 전달

```jsx
const result = useMemo(() => {
    return calculate(blogs);
}, [blogs]);
```

`blogs`가 변경될 때만 다시 계산한다.

#### 빈 의존성 배열

```jsx
const result = useMemo(() => {
    return calculate();
}, []);
```

최초 렌더링 때 한 번 계산하고 이후에는 같은 결과를 재사용한다.

#### 의존성 배열 생략

```jsx
const result = useMemo(() => {
    return calculate(blogs);
});
```

렌더링할 때마다 다시 계산되므로 메모이제이션의 실질적인 장점이 거의 없다.

### `useMemo`를 사용하지 않는 경우

```jsx
const filteredBlogs = selectedCategory === "전체"
    ? blogs
    : blogs.filter(
        (blog) => blog.category === selectedCategory
    );
```

위 계산은 컴포넌트가 렌더링될 때마다 실행된다. 다음 상황에서는 이 방식으로도 충분하다.

- 배열이 작을 때
- 계산이 단순할 때
- 렌더링 횟수가 많지 않을 때
- 실제 성능 문제가 발생하지 않을 때

`useMemo` 자체에도 값을 저장하고 의존성을 비교하는 비용이 있으므로 모든 계산에 무조건 사용할 필요는 없다.

---

## 6. 삼항 연산자

삼항 연산자는 조건에 따라 두 값 중 하나를 반환한다.

```jsx
조건 ? 참일 때 결과 : 거짓일 때 결과
```

카테고리 필터링에서는 다음과 같이 사용한다.

```jsx
const filteredBlogs = selectedCategory === "전체"
    ? blogs
    : blogs.filter(
        (blog) => blog.category === selectedCategory
    );
```

동일한 내용을 `if` 문으로 작성하면 다음과 같다.

```jsx
let filteredBlogs;

if (selectedCategory === "전체") {
    filteredBlogs = blogs;
} else {
    filteredBlogs = blogs.filter(
        (blog) => blog.category === selectedCategory
    );
}
```

---

## 7. `filter()`

`filter()`는 조건을 만족하는 요소만 모아 **새로운 배열**을 반환한다.

```jsx
const result = 배열.filter((요소) => {
    return 조건;
});
```

카테고리 필터링 예시는 다음과 같다.

```jsx
const filteredBlogs = blogs.filter((blog) => {
    return blog.category === selectedCategory;
});
```

화살표 함수의 자동 반환을 사용하면 다음과 같이 줄일 수 있다.

```jsx
const filteredBlogs = blogs.filter(
    (blog) => blog.category === selectedCategory
);
```

`filter()`는 원본 배열을 변경하지 않는다.

```text
blogs
├─ 개발 게시글
├─ 일상 게시글
└─ 개발 게시글

filter(category === "개발")
             ↓
새 배열
├─ 개발 게시글
└─ 개발 게시글
```

---

## 8. 화살표 함수의 `return` 규칙

### 중괄호가 없으면 자동 반환

화살표 다음에 표현식을 바로 작성하면 그 결과가 자동으로 반환된다.

```jsx
const add = (a, b) => a + b;
```

위 코드는 다음 코드와 같다.

```jsx
const add = (a, b) => {
    return a + b;
};
```

`filter()`에도 적용할 수 있다.

```jsx
blogs.filter(
    (blog) => blog.category === selectedCategory
);
```

### 중괄호를 사용하면 `return` 필요

```jsx
blogs.filter((blog) => {
    return blog.category === selectedCategory;
});
```

다음 코드는 `return`이 없으므로 모든 요소에 대해 `undefined`를 반환한다. 따라서 `filter()`의 결과는 빈 배열이 된다.

```jsx
// 잘못된 예시
blogs.filter((blog) => {
    blog.category === selectedCategory;
});
```

### 객체를 바로 반환할 때

객체를 자동 반환하려면 객체를 소괄호로 감싸야 한다.

```jsx
const users = names.map(
    (name) => ({
        name: name
    })
);
```

또는 `return`을 명시한다.

```jsx
const users = names.map((name) => {
    return {
        name: name
    };
});
```

---

## 9. 전체 실행 흐름

```jsx
const filteredBlogs = useMemo(() => {
    return selectedCategory === "전체"
        ? blogs
        : blogs.filter(
            (blog) => blog.category === selectedCategory
        );
}, [blogs, selectedCategory]);
```

실행 순서는 다음과 같다.

```text
1. 컴포넌트 렌더링
2. blogs 또는 selectedCategory의 변경 여부 확인
3. 값이 변경됐으면 useMemo 내부 함수 실행
4. selectedCategory가 "전체"인지 확인
5. "전체"라면 blogs 배열 반환
6. 특정 카테고리라면 filter() 실행
7. 결과를 filteredBlogs에 저장
8. BlogList에 props로 전달
```

```jsx
<BlogList ary={filteredBlogs} />
```

---

## 10. 핵심 코드

```jsx
import { useMemo, useState } from "react";

const BlogIndexPage = () => {
    const [blogs, setBlogs] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState("전체");

    const filteredBlogs = useMemo(() => {
        return selectedCategory === "전체"
            ? blogs
            : blogs.filter((blog) => {
                return blog.category === selectedCategory;
            });
    }, [blogs, selectedCategory]);

    return <BlogList ary={filteredBlogs} />;
};
```

## 핵심 요약

| 개념 | 설명 |
|---|---|
| CORS | 서로 다른 출처 사이의 요청을 서버가 허용하는 정책 |
| Preflight | 실제 요청 전에 `OPTIONS` 요청으로 허용 여부를 확인하는 과정 |
| `params` | Axios에 검색 조건을 객체로 전달하여 Query String을 생성하는 옵션 |
| 1:N 관계 | 게시글 하나에 여러 댓글이 연결되는 관계 |
| `_embed` | `json-server`에서 연관 데이터를 함께 조회하는 옵션 |
| `useMemo` | 의존성이 변경될 때만 값을 다시 계산하는 Hook |
| `filter()` | 조건을 만족하는 요소로 새로운 배열을 만드는 메서드 |
| `() => 표현식` | 표현식의 결과를 자동으로 반환 |
| `() => { return 값; }` | 중괄호를 사용했기 때문에 `return`을 직접 작성 |
