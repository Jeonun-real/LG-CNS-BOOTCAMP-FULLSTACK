# FE 8일차 핵심 개념 정리

## 오늘의 한 줄 요약

React에서 **Controlled Component 방식으로 Form 데이터를 관리하고**, Axios의 HTTP 메서드를 이용해 글과 댓글의 CRUD 기능을 구현하는 흐름을 학습했다.

---

## 1. CRUD란?

CRUD는 대부분의 웹 서비스에서 데이터를 관리할 때 사용하는 기본 기능이다.

| 기능     | HTTP Method    | 의미         | 예시               |
| ------ | -------------- | ---------- | ---------------- |
| Create | `POST`         | 새로운 데이터 저장 | 글 작성, 회원가입       |
| Read   | `GET`          | 데이터 조회     | 글 목록 조회, 상세 글 조회 |
| Update | `PUT`, `PATCH` | 기존 데이터 수정  | 글 수정             |
| Delete | `DELETE`       | 데이터 삭제     | 글 삭제             |

Axios에서는 다음과 같이 사용한다.

```javascript
api.get("/blogs");
api.post("/blogs", data);
api.put(`/blogs/${blogId}`, data);
api.patch(`/blogs/${blogId}`, data);
api.delete(`/blogs/${blogId}`);
```

### PUT과 PATCH의 차이

* `PUT`: 기존 데이터를 전체적으로 교체하는 방식
* `PATCH`: 기존 데이터의 일부만 수정하는 방식

예를 들어 제목만 수정한다면 `PATCH`가 더 적합할 수 있다.

```javascript
api.patch(`/blogs/${blogId}`, {
    title: "수정된 제목",
});
```

---

## 2. GET과 POST의 차이

### GET

서버에서 데이터를 조회할 때 사용한다.

```javascript
api.get("/blogs");
```

특정 조건에 맞는 데이터만 조회하려면 Query String 또는 `params`를 사용할 수 있다.

```javascript
api.get(`/users?email=${email}&password=${password}`);
```

조금 더 안전하고 가독성 있게 작성하려면 다음 방식이 좋다.

```javascript
api.get("/users", {
    params: {
        email: formData.email,
        password: formData.password,
    },
});
```

Axios가 다음과 같은 URL을 만들어 준다.

```text
/users?email=user@test.com&password=1234
```

### POST

새로운 데이터를 서버로 전달하고 저장할 때 사용한다.

```javascript
api.post("/blogs", {
    title: "React 공부",
    content: "Controlled Component를 학습했다.",
    category: "개발",
});
```

정리하면 다음과 같다.

```text
GET  → 서버에서 데이터를 가져온다.
POST → 서버에 새로운 데이터를 저장한다.
```

단, GET이 항상 필터링을 의미하는 것은 아니다.

```javascript
api.get("/blogs");
```

위 코드는 전체 글을 조회하고,

```javascript
api.get("/blogs", {
    params: {
        category: "개발",
    },
});
```

위 코드는 조건에 맞는 글만 조회한다.

즉, GET의 핵심은 **조회**이고, Query String은 조회 조건을 전달하는 방법이다.

---

## 3. SQL과 API 요청의 관계

프론트엔드에서 다음과 같이 요청한다고 가정한다.

```javascript
api.get("/users", {
    params: {
        email: formData.email,
        password: formData.password,
    },
});
```

서버에서는 전달받은 값을 이용해 다음과 비슷한 SQL을 실행할 수 있다.

```sql
SELECT name, email, password
FROM users
WHERE email = ?
  AND password = ?;
```

전체 흐름은 다음과 같다.

```text
React Form 입력
        ↓
Axios GET 또는 POST 요청
        ↓
Spring Boot Controller
        ↓
Service
        ↓
Repository 또는 Mapper
        ↓
JPA / MyBatis
        ↓
Database
```

프론트엔드는 SQL을 직접 실행하는 것이 아니라, 서버의 API에 요청을 전달한다.

---

## 4. MVP란?

MVP는 `Minimum Viable Product`의 약자로, 서비스의 핵심 기능만 먼저 구현한 최소 기능 제품을 의미한다.

완성된 서비스를 처음부터 모두 개발하는 것이 아니라, 가장 중요한 기능부터 빠르게 구현하고 검증하는 방식이다.

블로그 서비스의 MVP는 다음과 같이 구성할 수 있다.

* 글 목록 보기
* 글 상세 보기
* 댓글 보기
* 글 작성
* 댓글 작성

이후 다음 기능을 추가할 수 있다.

* 글 수정
* 글 삭제
* 댓글 수정
* 댓글 삭제
* 로그인 및 권한 관리
* 카테고리 검색
* 페이지네이션

---

## 5. API 명세란?

API 명세는 프론트엔드와 백엔드가 통신하기 위한 규칙을 정리한 문서이다.

주로 다음 내용을 포함한다.

* 요청 URL
* HTTP Method
* 요청 파라미터
* Request Body
* Response Body
* 상태 코드
* 오류 응답

예시:

```text
기능: 글 상세 조회
Method: GET
URL: /blogs/{blogId}

Response:
{
    "id": 1,
    "title": "React 공부",
    "content": "오늘 배운 내용",
    "category": "개발"
}
```

API 명세 확인 및 테스트에는 다음 도구를 사용할 수 있다.

* Postman
* Swagger

Postman은 API 요청을 직접 보내 테스트하는 도구이고, Swagger는 백엔드에서 작성된 API 명세를 문서 형태로 확인하고 테스트할 수 있는 도구이다.

---

## 6. React Router의 동적 경로

특정 글의 상세 페이지로 이동하려면 URL에 글의 ID를 포함할 수 있다.

```jsx
<Route
    path="/blogs/read/:blogId"
    element={<BlogReadPage />}
/>
```

여기서 `:blogId`는 고정된 문자열이 아니라 동적으로 변경되는 값이다.

```text
/blogs/read/1
/blogs/read/2
/blogs/read/10
```

상세 페이지에서는 `useParams()`를 사용해 URL의 값을 꺼낼 수 있다.

```javascript
import { useParams } from "react-router-dom";

const BlogReadPage = () => {
    const { blogId } = useParams();

    console.log(blogId);

    return <div>글 상세 페이지</div>;
};
```

그리고 해당 ID를 이용해 상세 데이터를 조회한다.

```javascript
const loadBlog = async () => {
    const response = await api.get(`/blogs/${blogId}`);
    setBlog(response.data);
};
```

### 왜 ID를 사용하는가?

글 제목은 중복될 수 있고 수정될 수도 있지만, ID는 데이터를 구분하는 고유한 값으로 사용할 수 있다.

따라서 글과 댓글처럼 데이터 간 관계가 존재할 때 ID를 이용한다.

예를 들어 댓글 데이터가 다음과 같을 수 있다.

```javascript
{
    id: 1,
    blogId: 3,
    content: "좋은 글입니다."
}
```

여기서 `blogId: 3`은 이 댓글이 3번 글에 속한다는 의미다.

```text
하나의 글
    ↓
여러 개의 댓글
```

이것을 1:N 관계라고 한다.

---

## 7. 전역 상태와 지역 상태

상태를 무조건 전역으로 관리하는 것이 좋은 것은 아니다.

특정 페이지나 특정 컴포넌트에서만 사용하는 데이터라면 지역 상태로 관리하는 것이 더 단순하다.

### 지역 상태가 적합한 경우

* 글 작성 페이지의 입력값
* 선택한 카테고리
* 특정 페이지의 로딩 상태
* 현재 수정 중인 글 데이터

```javascript
const [formData, setFormData] = useState({
    title: "",
    content: "",
    category: "",
});
```

### 전역 상태가 적합한 경우

* 로그인한 사용자 정보
* 인증 토큰
* 전체 애플리케이션에서 사용하는 테마
* 여러 페이지가 공유하는 장바구니 정보

```text
특정 도메인이나 컴포넌트에서만 사용
→ 지역 상태

여러 페이지에서 공통으로 사용
→ 전역 상태
```

전역 상태를 과도하게 사용하면 데이터 흐름이 복잡해질 수 있으므로 필요한 범위에서만 관리하는 것이 좋다.

---

## 8. Controlled Component

React에서 입력값을 State로 관리하는 Form 방식을 Controlled Component라고 한다.

핵심은 `value`와 `onChange`이다.

```jsx
<Input
    type="text"
    name="name"
    value={formData.name}
    onChange={changeHandler}
    placeholder="이름을 입력하세요"
    required
/>
```

### value

현재 State 값을 Input에 보여준다.

```jsx
value={formData.name}
```

### onChange

사용자가 입력할 때마다 변경된 값을 State에 저장한다.

```jsx
onChange={changeHandler}
```

전체 흐름은 다음과 같다.

```text
사용자 입력
    ↓
onChange 발생
    ↓
changeHandler 실행
    ↓
setFormData 실행
    ↓
State 변경
    ↓
컴포넌트 재렌더링
    ↓
변경된 값이 Input에 표시
```

---

## 9. 여러 Input을 객체 State로 관리하기

Input이 하나라면 State를 하나만 만들어도 된다.

```javascript
const [name, setName] = useState("");
```

하지만 이름, 이메일, 비밀번호처럼 Input이 여러 개라면 각각 State를 만들 수 있다.

```javascript
const [name, setName] = useState("");
const [email, setEmail] = useState("");
const [password, setPassword] = useState("");
```

입력 항목이 많아질수록 State와 Handler가 계속 증가한다.

따라서 하나의 객체 State로 묶어서 관리할 수 있다.

```javascript
const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
});
```

여기서 바깥쪽 배열은 `useState()`가 반환한 값을 구조 분해 할당하기 위한 문법이다.

```javascript
const [현재상태, 상태변경함수] = useState(초기값);
```

초기값으로 객체를 전달했기 때문에 `formData` 안에는 다음 객체가 들어 있다.

```javascript
{
    name: "",
    email: "",
    password: ""
}
```

값에 접근할 때는 객체의 프로퍼티로 접근한다.

```javascript
formData.name
formData.email
formData.password
```

---

## 10. 하나의 changeHandler로 여러 Input 처리하기

각 Input에 `name` 속성을 설정한다.

```jsx
<Input
    type="text"
    name="name"
    value={formData.name}
    onChange={changeHandler}
/>

<Input
    type="email"
    name="email"
    value={formData.email}
    onChange={changeHandler}
/>

<Input
    type="password"
    name="password"
    value={formData.password}
    onChange={changeHandler}
/>
```

이벤트가 발생하면 `e.target`에서 입력한 요소의 `name`과 `value`를 꺼낸다.

```javascript
const changeHandler = (e) => {
    const { name, value } = e.target;

    setFormData({
        ...formData,
        [name]: value,
    });
};
```

### `const { name, value } = e.target`

이벤트가 발생한 Input에서 `name`과 `value`를 구조 분해 할당으로 꺼낸다.

예를 들어 이메일 Input에 값을 입력하면 다음과 같다.

```javascript
name = "email";
value = "user@test.com";
```

### `...formData`

기존 객체의 값을 복사한다.

```javascript
{
    name: "",
    email: "",
    password: ""
}
```

### `[name]: value`

변수 `name`에 들어 있는 값을 객체의 Key로 사용한다.

```javascript
[name]: value
```

`name`이 `"email"`이라면 다음과 같이 동작한다.

```javascript
email: "user@test.com"
```

최종 결과는 다음과 같다.

```javascript
{
    name: "",
    email: "user@test.com",
    password: ""
}
```

기존 State를 기반으로 변경할 때는 함수형 업데이트를 사용할 수도 있다.

```javascript
setFormData((prevFormData) => ({
    ...prevFormData,
    [name]: value,
}));
```

이 방식은 이전 State를 기준으로 안전하게 값을 변경할 수 있어 권장된다.

---

## 11. Form의 submit 처리

버튼의 `type`이 `submit`이면 버튼 클릭 시 Form의 `onSubmit` 이벤트가 실행된다.

```jsx
<FormBox onSubmit={submitHandler}>
    <Input
        type="text"
        name="title"
        value={formData.title}
        onChange={changeHandler}
    />

    <button type="submit">
        글 작성하기
    </button>
</FormBox>
```

Submit Handler는 다음과 같이 작성한다.

```javascript
const submitHandler = async (e) => {
    e.preventDefault();

    await api.post("/blogs", formData);
};
```

### `e.preventDefault()`

`preventDefault()`는 이벤트 버블링을 막는 것이 아니다.

Form이 Submit될 때 발생하는 브라우저의 기본 동작을 막는다.

Form의 기본 동작은 현재 페이지를 새로고침하거나 `action` 주소로 이동하는 것이다.

```javascript
e.preventDefault();
```

React에서는 페이지 새로고침 없이 Axios 요청을 보내기 위해 사용한다.

이벤트 버블링을 막으려면 다음 메서드를 사용한다.

```javascript
e.stopPropagation();
```

정리하면 다음과 같다.

```text
e.preventDefault()
→ 브라우저 기본 동작 방지

e.stopPropagation()
→ 이벤트가 부모 요소로 전달되는 버블링 방지
```

---

## 12. 블로그 글 작성 Form 예시

```jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../../api/api";

const BlogWritePage = () => {
    const moveUrl = useNavigate();

    const [formData, setFormData] = useState({
        title: "",
        content: "",
        category: "",
    });

    const changeHandler = (e) => {
        const { name, value } = e.target;

        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    const categoryHandler = (category) => {
        setFormData((prevFormData) => ({
            ...prevFormData,
            category,
        }));
    };

    const submitHandler = async (e) => {
        e.preventDefault();

        try {
            const response = await api.post("/blogs", formData);

            if (response.status === 201 || response.status === 200) {
                moveUrl("/blogs/index");
            }
        } catch (error) {
            console.error("글 작성 실패:", error);
        }
    };

    return (
        <form onSubmit={submitHandler}>
            <div>
                {["개발", "생활", "취미", "일상"].map((category) => (
                    <button
                        key={category}
                        type="button"
                        onClick={() => categoryHandler(category)}
                    >
                        {category}
                    </button>
                ))}
            </div>

            <input
                type="text"
                name="title"
                value={formData.title}
                onChange={changeHandler}
                placeholder="제목을 입력하세요."
                required
            />

            <textarea
                name="content"
                value={formData.content}
                onChange={changeHandler}
                placeholder="내용을 입력하세요."
                required
            />

            <button type="submit">
                글 작성하기
            </button>

            <button
                type="button"
                onClick={() => moveUrl("/blogs/index")}
            >
                이전
            </button>
        </form>
    );
};

export default BlogWritePage;
```

여기서 중요한 부분은 다음과 같다.

```text
글 작성하기 버튼
→ type="submit"
→ Form의 onSubmit 실행

이전 버튼
→ type="button"
→ submit이 발생하지 않고 페이지 이동만 실행
```

---

## 13. 카테고리 선택 상태 관리

카테고리 버튼도 State와 연결할 수 있다.

```javascript
const [formData, setFormData] = useState({
    title: "",
    content: "",
    category: "",
});
```

카테고리 버튼 클릭 시 선택한 값을 저장한다.

```javascript
const categoryHandler = (category) => {
    setFormData((prevFormData) => ({
        ...prevFormData,
        category,
    }));
};
```

```jsx
{CATEGORIES.map((category) => (
    <CategoryChip
        key={category}
        type="button"
        $active={formData.category === category}
        onClick={() => categoryHandler(category)}
    >
        {category}
    </CategoryChip>
))}
```

선택된 카테고리와 현재 버튼의 값이 같으면 `$active`가 `true`가 된다.

```javascript
formData.category === category
```

Styled Components에서는 `$active` 값을 이용해 스타일을 변경할 수 있다.

```javascript
const CategoryChip = styled.button`
    border: 1px solid
        ${(props) => (props.$active ? "#6366f1" : "#e5e7eb")};

    background:
        ${(props) => (props.$active ? "#6366f1" : "#ffffff")};

    color:
        ${(props) => (props.$active ? "#ffffff" : "#4b5563")};
`;
```

---

## 14. 글 목록 조회와 useEffect

글 목록은 컴포넌트가 처음 렌더링된 후 서버에서 가져올 수 있다.

```javascript
const [blogs, setBlogs] = useState([]);

const loadData = async () => {
    try {
        const response = await api.get("/blogs");

        if (response.status === 200) {
            setBlogs(response.data);
        }
    } catch (error) {
        console.error(error);
    }
};

useEffect(() => {
    loadData();
}, []);
```

빈 의존성 배열을 사용했으므로 최초 렌더링 이후 한 번 실행된다.

```javascript
useEffect(() => {
    loadData();
}, []);
```

`setBlogs()`가 실행되면 State가 변경되면서 컴포넌트가 다시 렌더링되고, 받아온 데이터가 화면에 표시된다.

```jsx
{blogs.map((blog) => (
    <div key={blog.id}>
        <h3>{blog.title}</h3>
        <p>{blog.content}</p>
    </div>
))}
```

### 중요한 구조

`useEffect()`는 `loadData()` 함수 안에 작성하면 안 된다.

잘못된 구조:

```javascript
const loadData = async () => {
    await api.get("/blogs");

    useEffect(() => {
        loadData();
    }, []);
};
```

올바른 구조:

```javascript
const loadData = async () => {
    const response = await api.get("/blogs");
    setBlogs(response.data);
};

useEffect(() => {
    loadData();
}, []);
```

Hook은 컴포넌트 함수의 최상위에서 호출해야 한다.

---

## 15. 상세 페이지 이동

글 목록에서 특정 글을 클릭하면 해당 글의 ID를 URL에 포함해 이동할 수 있다.

```jsx
<Button
    title="상세보기"
    onClick={() => {
        moveUrl(`/blogs/read/${blog.id}`);
    }}
/>
```

라우터에서는 다음과 같이 받는다.

```jsx
<Route
    path="/blogs/read/:blogId"
    element={<BlogReadPage />}
/>
```

상세 페이지에서는 다음과 같이 조회한다.

```javascript
const { blogId } = useParams();

useEffect(() => {
    const loadBlog = async () => {
        const response = await api.get(`/blogs/${blogId}`);
        setBlog(response.data);
    };

    loadBlog();
}, [blogId]);
```

전체 흐름은 다음과 같다.

```text
상세보기 버튼 클릭
        ↓
/blogs/read/3 이동
        ↓
useParams()로 blogId 추출
        ↓
GET /blogs/3 요청
        ↓
3번 글 데이터 조회
        ↓
State 저장
        ↓
상세 화면 렌더링
```

---

## 16. CRUD 버튼의 역할

글 상세 페이지에서 CRUD 버튼을 배치하면 다음과 같이 연결할 수 있다.

### 목록

```javascript
moveUrl("/blogs/index");
```

### 수정 페이지 이동

```javascript
moveUrl(`/blogs/update/${blogId}`);
```

### 삭제

```javascript
const deleteHandler = async () => {
    try {
        await api.delete(`/blogs/${blogId}`);
        moveUrl("/blogs/index");
    } catch (error) {
        console.error("삭제 실패:", error);
    }
};
```

### 작성

```javascript
await api.post("/blogs", formData);
```

### 수정

```javascript
await api.put(`/blogs/${blogId}`, formData);
```

---

## 17. Styled Components를 이용한 Form 스타일링

React Form을 스타일링하는 방법에는 여러 가지가 있다.

* Styled Components
* Bootstrap
* Material UI
* 일반 CSS 또는 CSS Module

Styled Components를 사용하면 JavaScript 파일 안에서 스타일이 적용된 컴포넌트를 만들 수 있다.

```javascript
const FormBox = styled.form`
    display: flex;
    flex-direction: column;
    gap: 16px;
`;

const Input = styled.input`
    width: 100%;
    padding: 12px;
    box-sizing: border-box;
`;

const ButtonRow = styled.div`
    display: flex;
    justify-content: flex-end;
    gap: 12px;
`;
```

```jsx
<FormBox onSubmit={submitHandler}>
    <Input
        type="text"
        name="title"
        value={formData.title}
        onChange={changeHandler}
    />

    <ButtonRow>
        <Button title="글 작성하기" type="submit" />
        <Button
            title="이전"
            type="button"
            onClick={() => moveUrl("/blogs/index")}
        />
    </ButtonRow>
</FormBox>
```

버튼 간 간격은 `&nbsp;`보다 `gap`을 사용하는 것이 좋다.

```css
display: flex;
gap: 12px;
```

---

## 18. 패키지 버전 확인

설치된 패키지 목록과 버전은 다음 명령어로 확인할 수 있다.

```bash
npm ls
```

특정 패키지만 확인하려면 패키지명을 함께 입력한다.

```bash
npm ls react
npm ls react-dom
npm ls styled-components
npm ls @mui/material
```

React와 관련 라이브러리의 버전이 맞지 않으면 `useContext` 오류와 같은 문제가 발생할 수 있다.

패키지를 삭제할 때는 다음 명령어를 사용한다.

```bash
npm uninstall 패키지명
```

다시 설치할 때는 다음과 같이 작성한다.

```bash
npm install 패키지명
```

예시:

```bash
npm uninstall @mui/material
npm install @mui/material @emotion/react @emotion/styled
```

프로젝트의 전체 의존성이 꼬였을 때는 `node_modules`와 lock 파일을 삭제한 뒤 다시 설치하는 방법도 있다.

Windows:

```bash
rmdir /s /q node_modules
del package-lock.json
npm install
```

다만 무조건 삭제하기보다는 먼저 오류 메시지와 `npm ls` 결과를 확인하는 것이 좋다.

---

## 19. 오늘 헷갈렸던 부분

### 1. `preventDefault()`는 버블링 방지가 아니다

```javascript
e.preventDefault();
```

Form 제출 시 새로고침되는 기본 동작을 막는다.

버블링을 막는 것은 다음 코드다.

```javascript
e.stopPropagation();
```

### 2. GET은 무조건 필터링이 아니다

GET의 핵심은 조회다.

```javascript
api.get("/blogs");
```

전체 조회도 GET이고,

```javascript
api.get("/blogs", {
    params: {
        category: "개발",
    },
});
```

조건 조회도 GET이다.

### 3. `type: button`은 CSS가 아니다

Styled Components 내부에 다음과 같이 작성하면 안 된다.

```css
type: button;
```

`type`은 JSX 속성으로 전달해야 한다.

```jsx
<CategoryChip type="button">
    개발
</CategoryChip>
```

### 4. Button을 같은 줄에 배치하려면 부모가 필요하다

```jsx
<ButtonRow>
    <Button title="글 작성하기" />
    <Button title="이전" />
</ButtonRow>
```

```javascript
const ButtonRow = styled.div`
    display: flex;
    gap: 12px;
`;
```

### 5. Hook은 함수 내부나 조건문 안에 작성하면 안 된다

`useEffect()`는 `loadData()` 내부가 아니라 컴포넌트 최상위에 작성해야 한다.

---

## 20. 전체 처리 흐름

### 글 목록 조회

```text
BlogListPage 렌더링
        ↓
useEffect 실행
        ↓
GET /blogs 요청
        ↓
서버에서 글 목록 반환
        ↓
setBlogs(response.data)
        ↓
화면 재렌더링
```

### 글 작성

```text
사용자가 제목·내용·카테고리 입력
        ↓
onChange 실행
        ↓
formData State 변경
        ↓
글 작성하기 버튼 클릭
        ↓
Form onSubmit 실행
        ↓
e.preventDefault()
        ↓
POST /blogs 요청
        ↓
DB에 글 저장
        ↓
글 목록 페이지로 이동
```

### 글 상세 조회

```text
상세보기 버튼 클릭
        ↓
/blogs/read/{blogId} 이동
        ↓
useParams로 blogId 추출
        ↓
GET /blogs/{blogId}
        ↓
글 상세 데이터 반환
        ↓
화면 렌더링
```

### 글 삭제

```text
삭제 버튼 클릭
        ↓
DELETE /blogs/{blogId}
        ↓
서버 및 DB에서 데이터 삭제
        ↓
글 목록 페이지로 이동
```

---

## 핵심 개념 정리

```text
CRUD
Create → POST
Read   → GET
Update → PUT / PATCH
Delete → DELETE
```

```text
Controlled Component
Input value → State의 값
Input onChange → State 변경
```

```text
여러 Input 관리
Input의 name 속성 사용
e.target에서 name, value 추출
객체 State를 Spread 문법으로 복사
[name]: value로 해당 프로퍼티 변경
```

```text
Form Submit
type="submit"
→ Form의 onSubmit 실행

e.preventDefault()
→ 페이지 새로고침 방지
```

```text
동적 라우팅
/blogs/read/:blogId
→ useParams()로 blogId 추출
→ 해당 ID의 데이터 조회
```

---

## 오늘 한 줄 정리

React Form은 `value`와 `onChange`를 이용한 Controlled Component로 관리하고, 입력 데이터를 객체 State로 묶어 Axios의 `POST`, `GET`, `PUT`, `DELETE` 요청과 연결하면 CRUD 기능을 구현할 수 있다.
