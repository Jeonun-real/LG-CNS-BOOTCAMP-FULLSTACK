# FE 6일차 핵심 개념 정리
# FE 6일차 핵심 개념 정리

## React Component · Props · State · useEffect · API 통신

## 오늘의 핵심 한 줄

> React에서는 컴포넌트로 UI를 분리하고, `props`로 데이터와 함수를 전달하며, `state`로 변경되는 데이터를 관리하고, `useEffect`로 API 통신과 같은 부수 작업의 실행 시점을 관리한다.

---

# 1. React 컴포넌트란?

React 컴포넌트는 화면에 보여줄 JSX를 반환하는 함수다.

```tsx
const LibraryPage = () => {
    return (
        <div>
            도서 목록
        </div>
    );
};
```

JSX에서는 다음과 같이 컴포넌트를 사용할 수 있다.

```tsx
<LibraryPage />
```

컴포넌트는 일반 HTML 태그와 비슷하게 생겼지만, 이름의 첫 글자로 구분할 수 있다.

```text
<div />
<button />
img />

→ 소문자로 시작
→ 실제 HTML 태그
```

```text
<Book />
<LibraryPage />
<MaterialButton />

→ 대문자로 시작
→ 개발자가 만든 React 컴포넌트
```

---

# 2. Page와 Component를 분리하는 이유

한 페이지에 모든 UI 코드를 작성해도 동작은 한다.

```tsx
const LibraryPage = () => {
    return (
        <div>
            <h3>javascript</h3>
            <p>20,000원</p>
        </div>
    );
};
```

하지만 책 한 권의 UI를 `Book` 컴포넌트로 분리하면 역할이 명확해진다.

```text
LibraryPage
→ 어떤 책을 보여줄지 관리
→ 배열, filter, map 처리

Book
→ 책 한 권을 어떻게 보여줄지 관리
→ 책 이름, 가격, 이미지 출력
```

예시:

```tsx
const LibraryPage = () => {
    return (
        <div>
            <Book />
            <Book />
        </div>
    );
};
```

컴포넌트를 분리하면 다음 장점이 있다.

```text
코드 재사용 가능
파일별 역할 구분
유지보수 편리
페이지 코드가 간결해짐
UI 수정 범위가 명확해짐
```

---

# 3. `export`와 `import`

컴포넌트를 다른 파일에서 사용하려면 해당 컴포넌트를 `export`해야 한다.

## Default Export

```tsx
const Book = () => {
    return <div>Book</div>;
};

export default Book;
```

다른 파일에서는 중괄호 없이 가져온다.

```tsx
import Book from "./Book";
```

Default Export는 가져오는 파일에서 이름을 자유롭게 정할 수 있다.

```tsx
import MyBook from "./Book";
```

```tsx
<MyBook />
```

`export default Book`에서 내보내는 것은 HTML 결과가 아니라 `Book`이라는 컴포넌트 함수 자체다.

```text
Book 함수 정의
→ export default로 함수 참조 내보내기
→ 다른 파일에서 import
→ <Book />으로 사용
→ React가 컴포넌트 함수를 실행
→ 반환된 JSX를 렌더링
```

---

## Named Export

```tsx
export const CommentPage = () => {
    return <div>댓글 페이지</div>;
};
```

다른 파일에서는 중괄호를 사용한다.

```tsx
import { CommentPage } from "./CommentPage";
```

Named Export의 이름을 변경하려면 `as`를 사용한다.

```tsx
import {
    CommentPage as Application
} from "./CommentPage";
```

```tsx
<Application />
```

정리하면 다음과 같다.

```tsx
// Default Export
export default CommentPage;

import Application from "./CommentPage";
```

```tsx
// Named Export
export const CommentPage = () => {};

import {
    CommentPage as Application
} from "./CommentPage";
```

---

# 4. 배열을 컴포넌트로 출력하기

다음은 책 정보가 저장된 배열이다.

```tsx
const books = [
    {
        category: "IT",
        bookName: "java",
        price: 30000
    },
    {
        category: "lang",
        bookName: "javascript",
        price: 20000
    },
    {
        category: "lang",
        bookName: "react",
        price: 35000
    }
];
```

`filter()`를 사용하면 조건에 맞는 데이터만 선택할 수 있다.

```tsx
books.filter(
    (book) => book.category === "lang"
);
```

결과:

```tsx
[
    {
        category: "lang",
        bookName: "javascript",
        price: 20000
    },
    {
        category: "lang",
        bookName: "react",
        price: 35000
    }
]
```

`map()`을 사용하면 각각의 객체를 React 컴포넌트로 바꿀 수 있다.

```tsx
books
    .filter((book) => book.category === "lang")
    .map((book, idx) => (
        <Book
            key={idx}
            book={book}
        />
    ));
```

전체 흐름:

```text
books 배열
→ filter로 lang 카테고리 선택
→ map으로 객체를 하나씩 꺼냄
→ 객체마다 Book 컴포넌트 생성
→ 각 Book에 책 객체 하나 전달
```

---

# 5. Props란?

Props는 부모 컴포넌트가 자식 컴포넌트에 전달하는 데이터다.

```tsx
<Book
    book={book}
/>
```

왼쪽 `book`은 Props 이름이다.

```tsx
book=
```

오른쪽 `{book}`은 실제로 전달할 JavaScript 변수다.

```tsx
{book}
```

즉 다음 의미다.

```text
현재 book 변수의 값을
book이라는 Props 이름으로
Book 컴포넌트에 전달한다.
```

예를 들어 현재 `book`이 다음 객체라면:

```tsx
{
    category: "lang",
    bookName: "javascript",
    price: 20000
}
```

자식 컴포넌트가 받는 Props 객체는 개념적으로 다음과 같다.

```tsx
{
    book: {
        category: "lang",
        bookName: "javascript",
        price: 20000
    }
}
```

---

# 6. 배열 전체가 아니라 객체 하나씩 전달된다

다음 코드에서:

```tsx
books.map((book, idx) => (
    <Book
        key={idx}
        book={book}
    />
));
```

`books`는 객체가 여러 개 들어 있는 배열이다.

```tsx
[
    {},
    {},
    {}
]
```

하지만 `map()` 안의 `book`은 현재 반복에서 꺼낸 객체 하나다.

```tsx
{
    category: "lang",
    bookName: "javascript",
    price: 20000
}
```

따라서 하나의 `Book` 컴포넌트에는 배열 전체가 아니라 객체 하나가 전달된다.

```text
books
→ 배열 전체

map의 book
→ 객체 하나

<Book book={book} />
→ Book 컴포넌트에 객체 하나 전달
```

댓글 데이터도 동일하다.

```tsx
comments.map((comment, idx) => (
    <Comment
        key={idx}
        data={comment}
    />
));
```

`Comment` 컴포넌트 하나에는 댓글 객체 하나가 전달된다.

---

# 7. Props 구조분해 할당

자식 컴포넌트는 Props 객체 전체를 받을 수 있다.

```tsx
const Book = (props) => {
    return (
        <div>
            <p>{props.book.bookName}</p>
            <p>{props.book.price}</p>
        </div>
    );
};
```

Props에서 `book`만 꺼내 구조분해 할당으로 받을 수도 있다.

```tsx
const Book = ({ book }) => {
    return (
        <div>
            <p>{book.bookName}</p>
            <p>{book.price}</p>
        </div>
    );
};
```

다음 두 코드는 같은 의미다.

```tsx
const Book = (props) => {
    const book = props.book;
};
```

```tsx
const Book = ({ book }) => {
};
```

---

# 8. `key`는 일반 Props가 아니다

다음 코드에는 속성이 두 개 보인다.

```tsx
<Book
    key={idx}
    book={book}
/>
```

하지만 `Book`이 일반 Props로 받는 것은 `book` 하나다.

```text
key
→ React가 반복된 컴포넌트를 구별하기 위해 사용
→ 자식의 props.key로 접근할 수 없음

book
→ 자식 컴포넌트에 전달되는 일반 Props
```

```tsx
const Book = (props) => {
    console.log(props.book); // 사용 가능
    console.log(props.key);  // undefined
};
```

고유한 `id`가 있다면 인덱스보다 `id`를 사용하는 것이 좋다.

```tsx
<Book
    key={book.id}
    book={book}
/>
```

---

# 9. JSX에서 JavaScript 값 사용하기

JSX 안에서 JavaScript 변수나 표현식을 사용하려면 중괄호를 사용한다.

```tsx
const title = "React";

return (
    <h1>{title}</h1>
);
```

Props로 JavaScript 변수를 전달할 때도 중괄호를 사용한다.

```tsx
<Book book={book} />
```

문자열을 직접 전달할 때는 중괄호를 생략할 수 있다.

```tsx
<Book title="React" />
```

```text
title="React"
→ 문자열 직접 전달

title={title}
→ JavaScript 변수 전달
```

---

# 10. Props로 함수 전달하기

Props에는 문자열, 숫자, 객체뿐 아니라 함수도 전달할 수 있다.

부모 컴포넌트:

```tsx
const ButtonPage = () => {
    const saveHandler = () => {
        console.log("글작성 버튼 클릭");
    };

    return (
        <MaterialButton
            title="글작성"
            onClick={saveHandler}
        />
    );
};
```

자식 컴포넌트:

```tsx
const MaterialButton = (props) => {
    return (
        <button onClick={props.onClick}>
            {props.title}
        </button>
    );
};
```

개념적인 Props 구조:

```tsx
{
    title: "글작성",
    onClick: saveHandler 함수 참조
}
```

함수 코드가 복사되어 전달되는 것이 아니라, 함수 객체를 가리키는 참조가 전달된다.

```text
saveHandler ───────┐
                   ├─ 같은 함수 객체 참조
props.onClick ─────┘
```

따라서 자식 컴포넌트가:

```tsx
props.onClick();
```

을 실행하면 부모에서 정의한 `saveHandler` 함수가 실행된다.

---

# 11. 함수 전달과 함수 호출의 차이

함수 자체를 전달하려면 괄호를 쓰지 않는다.

```tsx
onClick={saveHandler}
```

버튼이 클릭될 때 함수가 실행된다.

```tsx
onClick={() => saveHandler()}
```

이 방식도 클릭할 때 실행된다.

현재처럼 추가 매개변수가 없다면 다음 방식이 더 간단하다.

```tsx
onClick={saveHandler}
```

다음 코드는 주의해야 한다.

```tsx
onClick={saveHandler()}
```

괄호가 있기 때문에 렌더링되는 순간 함수가 바로 실행된다.

정리:

```tsx
onClick={saveHandler}
// 함수 참조 전달
// 클릭할 때 실행
```

```tsx
onClick={() => saveHandler()}
// 화살표 함수 전달
// 클릭할 때 내부의 saveHandler 실행
```

```tsx
onClick={saveHandler()}
// 렌더링 중 즉시 실행
```

매개변수가 필요하다면 화살표 함수를 사용한다.

```tsx
onClick={() => saveHandler(10)}
```

---

# 12. MUI Button까지 함수가 전달되는 흐름

부모 컴포넌트:

```tsx
<MaterialButton
    title="글작성"
    onClick={saveHandler}
/>
```

중간 컴포넌트:

```tsx
import Button from "@mui/material/Button";

const MaterialButton = (props) => {
    return (
        <Button onClick={props.onClick}>
            {props.title}
        </Button>
    );
};

export default MaterialButton;
```

전체 흐름:

```text
ButtonPage에서 saveHandler 생성
→ MaterialButton에 onClick Props로 전달
→ props.onClick이 saveHandler를 참조
→ MUI Button의 onClick에 연결
→ 사용자가 버튼 클릭
→ MUI Button이 props.onClick 실행
→ 부모의 saveHandler 실행
```

구조분해 할당으로 작성하면 더 간단하다.

```tsx
const MaterialButton = ({ title, onClick }) => {
    return (
        <Button onClick={onClick}>
            {title}
        </Button>
    );
};
```

---

# 13. Axios 인스턴스와 `baseURL`

Axios 공통 설정을 별도 파일로 만들 수 있다.

```tsx
import axios from "axios";

const endPoint =
    process.env.REACT_APP_BACKEND_ENDPOINT;

const api = axios.create({
    baseURL: endPoint
});

export default api;
```

`axios.create()`는 공통 설정을 가진 새로운 Axios 인스턴스를 만든다.

```text
api
→ baseURL 설정을 가진 Axios 객체
→ get, post, put, delete 등의 메서드 사용 가능
```

환경변수에 다음 값이 있다고 가정한다.

```env
REACT_APP_BACKEND_ENDPOINT=http://localhost:5000
```

다른 파일에서:

```tsx
api.get("/comment");
```

를 실행하면 실제 요청 주소는 다음과 같다.

```text
http://localhost:5000/comment
```

`baseURL`은 데이터를 자동으로 가져오는 기능이 아니다.

```text
baseURL
→ 요청 주소 앞에 붙일 기본 주소 설정

api.get("/comment")
→ 실제 API 요청 실행
```

---

# 14. `.env` 파일 구별 방법

Create React App에서는 실행 환경에 따라 환경변수 파일을 자동으로 선택한다.

```text
npm start
→ development 모드
→ .env와 .env.development 사용

npm run build
→ production 모드
→ .env와 .env.production 사용

npm test
→ test 모드
→ .env와 .env.test 사용
```

예시:

```env
# .env
REACT_APP_NAME=Comment App
```

```env
# .env.development
REACT_APP_BACKEND_ENDPOINT=http://localhost:5000
```

```env
# .env.production
REACT_APP_BACKEND_ENDPOINT=https://api.example.com
```

코드는 동일하게 작성한다.

```tsx
const endPoint =
    process.env.REACT_APP_BACKEND_ENDPOINT;
```

CRA가 실행 명령에 따라 적절한 값을 넣어준다.

```text
.env
→ 모든 환경에서 사용하는 공통값

.env.development
→ 개발 환경에서 사용하는 값

.env.production
→ 배포 환경에서 사용하는 값
```

CRA 환경변수는 `REACT_APP_`으로 시작해야 한다.

```env
REACT_APP_BACKEND_ENDPOINT=http://localhost:5000
```

Vite에서는 `VITE_` 접두사를 사용한다.

```env
VITE_BACKEND_ENDPOINT=http://localhost:5000
```

```tsx
const endPoint =
    import.meta.env.VITE_BACKEND_ENDPOINT;
```

---

# 15. `"/comment"`와 `<Comment />`의 차이

두 표현은 이름은 비슷하지만 완전히 다른 의미다.

```tsx
api.get("/comment");
```

여기서 `"/comment"`는 문자열이며 백엔드 API 경로다.

```tsx
<Comment />
```

여기서 `Comment`는 React UI 컴포넌트다.

```text
"/comment"
→ 문자열
→ 서버의 API Endpoint

<Comment />
→ React 컴포넌트
→ 댓글 UI 생성
```

---

# 16. JSON Server와 `db.json`

`db.json`이 다음과 같다고 가정한다.

```json
{
    "comment": [
        {
            "writer": "임창섭",
            "comment": "즐거운 React 수업입니다."
        },
        {
            "writer": "서정운",
            "comment": "React 공부 중입니다."
        }
    ]
}
```

JSON Server를 실행하면 `comment` 키를 이용해 API 주소가 생성된다.

```bash
npx json-server --watch db.json --port 5000
```

생성되는 Endpoint:

```text
GET http://localhost:5000/comment
```

Axios 요청:

```tsx
api.get("/comment");
```

응답 데이터:

```tsx
response.data
```

전체 흐름:

```text
db.json의 comment 배열
→ JSON Server가 /comment API 생성
→ api.get("/comment") 요청
→ response.data로 댓글 배열 수신
```

JSON Server는 실제 데이터베이스라기보다 `db.json` 파일을 REST API처럼 사용할 수 있게 해주는 개발용 가짜 서버다.

---

# 17. 일반 변수로 API 데이터를 관리하면 안 되는 이유

다음 코드를 살펴본다.

```tsx
export const CommentPage = () => {
    let comments = [];

    const loadData = async () => {
        await api.get("/comment")
            .then((response) => {
                comments = response.data;
            });
    };

    loadData();

    return (
        <div>
            {comments.map(/* ... */)}
        </div>
    );
};
```

실행 순서:

```text
1. CommentPage 실행

2. comments = [] 생성

3. loadData() 호출

4. 서버에 API 요청

5. API 응답을 기다리는 동안 return 실행

6. 빈 comments 배열로 첫 화면 렌더링

7. 나중에 API 응답 도착

8. comments = response.data 실행

9. 일반 변수 값만 변경

10. React 재렌더링은 발생하지 않음
```

`await`가 있어도 React 컴포넌트 전체 렌더링을 기다리게 하지 않는다.

```text
await
→ loadData 함수 내부의 다음 코드를 기다림

await
→ React 렌더링을 중단시키는 기능은 아님
```

`loadData()`는 비동기 함수이므로 호출 즉시 Promise를 반환한다.

```tsx
const result = loadData();

console.log(result);
// Promise
```

---

# 18. State란?

State는 React 컴포넌트가 관리하는 변경 가능한 데이터다.

```text
일반 변수
→ 값만 변경됨
→ React가 변경을 감지하지 않음

State
→ 값 변경
→ React가 변경을 감지
→ 컴포넌트 재렌더링
→ 화면 갱신
```

State는 다음 역할을 가진다.

```text
값 저장
+
변경 감지
+
재렌더링 요청
+
화면 갱신
```

---

# 19. `useState()` 문법

`useState`는 React 패키지에서 Named Export된 함수다.

```tsx
import {
    useState
} from "react";
```

기본 문법:

```tsx
const [상태값, 상태변경함수] =
    useState(초기값);
```

예시:

```tsx
const [cnt, setCnt] = useState(0);
```

각 부분의 의미:

```text
cnt
→ 현재 State 값

setCnt
→ cnt를 변경하는 함수

0
→ cnt의 초기값
```

`useState()`는 개념적으로 배열을 반환한다.

```tsx
const result = useState(0);

const cnt = result[0];
const setCnt = result[1];
```

이를 배열 구조분해 할당으로 간단히 작성한 것이다.

```tsx
const [cnt, setCnt] = useState(0);
```

---

# 20. TypeScript의 State 타입 추론

TypeScript에서 다음과 같이 초기값으로 `0`을 주면:

```tsx
const [cnt, setCnt] = useState(0);
```

TypeScript는 `cnt`를 `number`로 추론한다.

```text
cnt
→ number

setCnt
→ number 값을 받는 상태 변경 함수
```

따라서 다음 코드는 가능하다.

```tsx
setCnt(10);
setCnt(cnt + 1);
setCnt((previousCnt) => previousCnt + 1);
```

문자열은 넣을 수 없다.

```tsx
setCnt("10");
// TypeScript 오류
```

타입을 명시할 수도 있다.

```tsx
const [cnt, setCnt] =
    useState<number>(0);
```

초기값만으로 타입을 알 수 있다면 일반적으로 생략한다.

---

# 21. State는 직접 수정하지 않는다

다음처럼 직접 대입하면 안 된다.

```tsx
cnt = cnt + 1;
```

State 변경 함수를 사용해야 한다.

```tsx
setCnt(cnt + 1);
```

이전 값을 기준으로 변경한다면 함수형 업데이트가 더 안전하다.

```tsx
setCnt(
    (previousCnt) => previousCnt + 1
);
```

```text
previousCnt
→ 변경 직전의 State 값
```

예시:

```tsx
const upCntHandler = () => {
    setCnt(
        (previousCnt) => previousCnt + 1
    );
};
```

```tsx
const downCntHandler = () => {
    setCnt(
        (previousCnt) => previousCnt - 1
    );
};
```

---

# 22. 왜 `const`로 State를 선언하는가?

다음처럼 `const`를 사용하는 것이 일반적이다.

```tsx
const [cnt, setCnt] = useState(0);
```

`cnt` 값은 변경되지만 현재 실행 중인 컴포넌트 함수 안에서 직접 다시 대입하는 것은 아니다.

```tsx
cnt = 10;
// 사용하지 않음
```

`setCnt()`가 React에 다음 렌더링에서 사용할 새로운 State를 전달한다.

```text
현재 렌더링의 cnt
→ 직접 수정하지 않음

setCnt 실행
→ React에 새로운 값 요청

다음 렌더링
→ 새로운 cnt 값 제공
```

따라서 구조분해한 변수 자체는 `const`로 선언한다.

---

# 23. State 변경과 재렌더링

```tsx
setCnt(
    (previousCnt) => previousCnt + 1
);
```

실행 흐름:

```text
버튼 클릭
→ upCntHandler 실행
→ setCnt 실행
→ cnt State 변경 요청
→ React가 CapacityPage 재실행
→ 새로운 cnt 값으로 JSX 생성
→ 화면 갱신
```

컴포넌트는 재렌더링될 때 함수가 다시 실행된다.

```text
CapacityPage 함수 다시 실행
→ useState에서 현재 State 값 가져오기
→ 이벤트 함수 다시 생성
→ JSX 다시 생성
→ 화면 비교 및 업데이트
```

---

# 24. State를 컴포넌트 본문의 API 요청과 함께 쓰면 무한 반복될 수 있다

다음 코드는 문제가 될 수 있다.

```tsx
const CommentPage = () => {
    const [comments, setComments] =
        useState([]);

    const loadData = async () => {
        const response =
            await api.get("/comment");

        setComments(response.data);
    };

    loadData();

    return <div />;
};
```

실행 흐름:

```text
첫 렌더링
→ loadData 실행
→ API 응답
→ setComments 실행
→ State 변경
→ 재렌더링
→ loadData 다시 실행
→ API 응답
→ setComments 다시 실행
→ 계속 반복
```

서버에서 받는 배열은 내용이 같아도 일반적으로 새로운 배열 객체다.

```tsx
[] === []
// false
```

React는 객체 내부 내용을 하나씩 깊게 비교하는 것이 아니라 객체 참조를 비교한다.

따라서 API가 매번 새로운 배열을 반환하면 React는 새로운 State라고 판단할 수 있다.

또한 부모 컴포넌트의 재렌더링이나 개발 환경의 Strict Mode 등으로 인해 API 요청이 예상보다 자주 실행될 수 있다.

React의 동일 값 검사는 API 요청의 실행 시점을 제어하는 장치가 아니다.

---

# 25. Side Effect란?

컴포넌트의 기본 역할은 Props와 State를 이용해 JSX를 계산하는 것이다.

```tsx
const Profile = ({ userName }) => {
    return (
        <div>{userName}</div>
    );
};
```

하지만 실제 애플리케이션에서는 화면 계산 이외의 작업도 필요하다.

```text
API 통신
타이머 등록
이벤트 리스너 등록
localStorage 사용
DOM 직접 조작
외부 라이브러리 연동
```

이런 작업을 Side Effect, 즉 부수 효과라고 한다.

---

# 26. `useEffect()` 문법

`useEffect`는 렌더링이 끝난 뒤 Side Effect를 실행하는 Hook이다.

```tsx
import {
    useEffect
} from "react";
```

기본 문법:

```tsx
useEffect(() => {
    // 렌더링 후 실행할 작업
}, [의존성]);
```

구조:

```text
첫 번째 매개변수
→ 실행할 Effect 함수

두 번째 매개변수
→ Effect 실행 조건을 정하는 의존성 배열
```

---

# 27. 의존성 배열에 따른 실행 차이

## 의존성 배열을 생략한 경우

```tsx
useEffect(() => {
    console.log("모든 렌더링 후 실행");
});
```

```text
최초 렌더링 후 실행
모든 재렌더링 후 실행
```

---

## 빈 배열을 전달한 경우

```tsx
useEffect(() => {
    console.log("최초 한 번 실행");
}, []);
```

```text
컴포넌트가 처음 렌더링된 후 실행
이후 재렌더링에서는 실행하지 않음
```

초기 API 조회에 많이 사용한다.

```tsx
useEffect(() => {
    loadData();
}, []);
```

---

## 특정 State를 전달한 경우

```tsx
useEffect(() => {
    console.log(`cnt 변경: ${cnt}`);
}, [cnt]);
```

실행 시점:

```text
최초 렌더링 후
cnt가 변경된 후
```

다른 State가 바뀌어도 `cnt`가 그대로라면 해당 Effect는 다시 실행되지 않는다.

---

# 28. 왜 API 요청을 `useEffect`에서 실행하는가?

API 요청을 컴포넌트 본문에 작성하면 렌더링될 때마다 실행된다.

```tsx
loadData();
```

`useEffect`를 사용하면 API 요청의 실행 시점을 제한할 수 있다.

```tsx
useEffect(() => {
    loadData();
}, []);
```

전체 흐름:

```text
CommentPage 최초 실행
→ comments 초기값은 []
→ JSX 반환
→ 빈 댓글 목록으로 첫 화면 렌더링
→ 렌더링 완료
→ useEffect 실행
→ loadData 실행
→ API 데이터 수신
→ setComments 실행
→ comments State 변경
→ CommentPage 재렌더링
→ useEffect는 다시 실행되지 않음
→ 댓글 목록 화면 출력
```

역할을 구분하면 다음과 같다.

```text
loadData
→ 실제 API 요청을 담당

useEffect
→ loadData를 언제 실행할지 관리

useState
→ 받아온 데이터를 저장하고 화면을 갱신
```

---

# 29. State와 Effect를 이용한 댓글 API 통신

```tsx
import {
    useEffect,
    useState
} from "react";

import api from "../../api/axios";
import Comment from "../../components/sample/Comment";

export const CommentPage = () => {
    const [comments, setComments] =
        useState([]);

    const loadData = async () => {
        try {
            const response =
                await api.get("/comment");

            console.log(
                "response data:",
                response.data
            );

            setComments(response.data);
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    return (
        <div>
            {comments.map(
                (comment, idx) => (
                    <Comment
                        key={idx}
                        data={comment}
                    />
                )
            )}
        </div>
    );
};
```

전체 처리 흐름:

```text
1. CommentPage 최초 실행

2. comments State의 초기값은 []

3. 빈 배열 기준으로 첫 화면 렌더링

4. 렌더링 후 useEffect 실행

5. loadData 실행

6. api.get("/comment") 요청

7. JSON Server에서 댓글 배열 응답

8. setComments(response.data) 실행

9. comments State 변경

10. CommentPage 재렌더링

11. comments.map() 실행

12. Comment 컴포넌트에 댓글 객체 하나씩 전달

13. 댓글 목록 화면 출력
```

---

# 30. `await`와 `.then()` 사용 방식

다음처럼 `await`와 `.then()`을 함께 사용할 수는 있다.

```tsx
await api.get("/comment")
    .then((response) => {
        setComments(response.data);
    });
```

하지만 두 방식 중 하나만 사용하는 편이 읽기 좋다.

## `async/await` 방식

```tsx
const response =
    await api.get("/comment");

setComments(response.data);
```

## `.then()` 방식

```tsx
api.get("/comment")
    .then((response) => {
        setComments(response.data);
    })
    .catch((error) => {
        console.error(error);
    });
```

---

# 31. Hook이란?

Hook은 함수형 컴포넌트에서 React 기능을 사용할 수 있게 해주는 함수다.

대표적인 Hook:

```text
useState
→ State 관리

useEffect
→ Side Effect 관리

useContext
→ Context 데이터 사용

useRef
→ 값 또는 DOM 참조 저장
```

Hook 함수는 일반적으로 `use`로 시작한다.

```tsx
import {
    useState,
    useEffect
} from "react";
```

중괄호를 사용한 이유는 `useState`와 `useEffect`가 React 패키지에서 Named Export된 함수이기 때문이다.

개념적으로는 다음과 비슷하다.

```tsx
export const useState = () => {};
export const useEffect = () => {};
```

---

# 32. 컴포넌트 생명주기

컴포넌트는 화면에 나타나고, 변경되고, 사라지는 과정을 가진다.

```text
Mount
→ 컴포넌트가 처음 화면에 나타남

Update
→ Props 또는 State 변경으로 재렌더링됨

Unmount
→ 컴포넌트가 화면에서 제거됨
```

쉽게 표현하면:

```text
Mount
→ 등장

Update
→ 변경

Unmount
→ 제거
```

---

# 33. Hook과 생명주기가 같이 나오는 이유

과거 클래스 컴포넌트에서는 생명주기 메서드를 사용했다.

```text
componentDidMount
→ 컴포넌트가 처음 나타난 후 실행

componentDidUpdate
→ 컴포넌트가 업데이트된 후 실행

componentWillUnmount
→ 컴포넌트가 사라지기 전에 실행
```

함수형 컴포넌트에서는 Hook을 사용해 State와 렌더링 이후 작업을 처리한다.

```text
useState
→ 상태를 관리
→ 상태 변경 시 Update 발생

useEffect
→ Mount 또는 Update 이후 필요한 작업 실행
→ 정리 함수를 통해 Unmount 관련 작업 처리
```

즉 Hook은 함수형 컴포넌트를 React의 상태 및 생명주기 흐름에 연결해 주는 함수라고 이해할 수 있다.

---

# 34. `useEffect`의 정리 함수

`useEffect` 안에서 함수를 반환하면 정리 작업을 할 수 있다.

```tsx
useEffect(() => {
    const timer = setInterval(() => {
        console.log("타이머 실행");
    }, 1000);

    return () => {
        clearInterval(timer);
    };
}, []);
```

실행 흐름:

```text
컴포넌트 Mount
→ setInterval 등록

컴포넌트 Unmount
→ 반환한 정리 함수 실행
→ clearInterval 실행
```

정리 함수는 다음 작업에 사용한다.

```text
타이머 제거
이벤트 리스너 제거
구독 해제
네트워크 요청 취소
```

---

# 35. CapacityPage 예제로 State와 Effect 이해하기

```tsx
import Button from "../../components/styled/Button";
import {
    useEffect,
    useState
} from "react";

const CapacityPage = () => {
    console.log(
        "debug: CapacityPage render"
    );

    const [cnt, setCnt] = useState(0);

    const upCntHandler = () => {
        setCnt(
            (previousCnt) =>
                previousCnt + 1
        );
    };

    const downCntHandler = () => {
        setCnt(
            (previousCnt) =>
                previousCnt - 1
        );
    };

    useEffect(() => {
        console.log(
            "CapacityPage Effect 실행"
        );

        console.log(
            `현재 입장 인원: ${cnt}`
        );
    }, [cnt]);

    return (
        <div>
            <p>입장인원: {cnt}</p>

            <Button
                title="입장"
                onClick={upCntHandler}
                disabled={cnt >= 10}
            />

            <Button
                title="퇴장"
                onClick={downCntHandler}
                disabled={cnt <= 0}
            />
        </div>
    );
};

export default CapacityPage;
```

실행 흐름:

```text
CapacityPage 최초 실행
→ cnt 초기값 0
→ 화면에 입장인원 0 출력
→ 첫 렌더링 완료
→ useEffect 실행

입장 버튼 클릭
→ upCntHandler 실행
→ setCnt 실행
→ cnt State 증가
→ CapacityPage 재렌더링
→ 변경된 cnt 화면 출력
→ cnt가 바뀌었으므로 useEffect 실행

cnt가 10
→ 입장 버튼 비활성화

cnt가 0
→ 퇴장 버튼 비활성화
```

---

# 36. 컴포넌트 본문 로그와 `useEffect` 로그의 차이

```tsx
console.log(
    "CapacityPage render"
);
```

컴포넌트 본문에 있는 코드는 컴포넌트가 렌더링될 때마다 실행된다.

```tsx
useEffect(() => {
    console.log(
        "CapacityPage Effect"
    );
}, [cnt]);
```

`useEffect` 내부 코드는 렌더링이 화면에 반영된 후 실행된다.

순서:

```text
State 변경
→ CapacityPage 함수 실행
→ 컴포넌트 본문의 console.log 실행
→ JSX 생성
→ 화면 업데이트
→ useEffect 실행
```

---

# 37. React의 단방향 데이터 흐름

React의 데이터는 기본적으로 부모에서 자식으로 내려간다.

```text
부모 State
↓
자식 Props
```

자식이 부모의 데이터를 직접 수정하지 않는다.

자식에서 변경이 필요하면 부모가 Props로 전달한 함수를 실행한다.

```text
자식 버튼 클릭
→ 부모가 전달한 함수 실행
→ 부모 State 변경
→ 부모 재렌더링
→ 자식에게 새로운 Props 전달
→ 자식 화면 갱신
```

State를 양방향 통신이라고 이해하면 혼동될 수 있다.

```text
State
→ 변경되는 데이터를 React가 관리하는 방식

Props
→ 부모가 자식에게 전달하는 읽기 전용 값

함수 Props
→ 자식이 부모에게 변경을 요청하는 방법
```

---

# 38. State와 Props의 관계

부모의 State를 자식에게 Props로 전달할 수 있다.

```tsx
const Parent = () => {
    const [count, setCount] =
        useState(0);

    return (
        <div>
            <Child count={count} />

            <button
                onClick={() => {
                    setCount(
                        (previousCount) =>
                            previousCount + 1
                    );
                }}
            >
                증가
            </button>
        </div>
    );
};
```

```tsx
const Child = ({ count }) => {
    return <p>{count}</p>;
};
```

실행 흐름:

```text
부모 count State는 0
→ Child에 count={0} 전달
→ 화면에 0 출력

버튼 클릭
→ setCount 실행
→ 부모 State 변경
→ 부모 재렌더링
→ Child에 count={1} 전달
→ 자식 재렌더링
→ 화면에 1 출력
```

Props가 스스로 변화를 감지하는 것은 아니다.

부모 State가 변경되어 부모가 다시 렌더링되기 때문에 새로운 Props가 자식에게 전달되는 것이다.

---

# 39. 핵심 개념 비교

## 일반 변수

```tsx
let comments = [];
```

```text
값 변경 가능
React가 변경 감지하지 않음
변경되어도 자동 재렌더링되지 않음
재렌더링 시 다시 초기화될 수 있음
```

## Props

```tsx
<Comment data={comment} />
```

```text
부모가 자식에게 전달
읽기 전용 데이터
문자열, 숫자, 객체, 배열, 함수 전달 가능
```

## State

```tsx
const [comments, setComments] =
    useState([]);
```

```text
컴포넌트가 관리하는 변경 가능한 데이터
setState 실행 시 재렌더링 요청
렌더링 사이에도 값 유지
```

## Effect

```tsx
useEffect(() => {
    loadData();
}, []);
```

```text
렌더링 이후 실행
API 요청, 타이머, 이벤트 등록 등 처리
의존성 배열로 실행 시점 제어
```

---

# 40. 전체 React 처리 흐름

```text
main.tsx 또는 index.js
→ Page 컴포넌트 렌더링 시작
→ State 초기값 준비
→ JSX 반환
→ 첫 화면 렌더링
→ useEffect 실행
→ API 요청
→ 서버 응답 수신
→ setState 실행
→ State 변경
→ Page 컴포넌트 재렌더링
→ map으로 데이터 순회
→ 자식 Component에 Props 전달
→ 자식 Component가 JSX 반환
→ 화면 갱신
```

---

# 41. 오늘의 핵심 용어

```text
Component
= JSX를 반환하는 함수 또는 클래스

Props
= 부모가 자식에게 전달하는 읽기 전용 데이터

State
= 컴포넌트가 관리하는 변경 가능한 데이터

setState
= State 변경과 재렌더링을 요청하는 함수

Rendering
= Props와 State를 기준으로 JSX를 계산하는 과정

Re-rendering
= State나 Props 변화로 컴포넌트가 다시 실행되는 과정

Hook
= 함수형 컴포넌트에서 React 기능을 사용하는 함수

useState
= 함수형 컴포넌트에서 State를 관리하는 Hook

useEffect
= 렌더링 이후 Side Effect를 실행하는 Hook

Side Effect
= API 통신, 타이머, 이벤트 등록 등 렌더링 외부 작업

Mount
= 컴포넌트가 처음 화면에 나타남

Update
= Props나 State 변경으로 다시 렌더링됨

Unmount
= 컴포넌트가 화면에서 제거됨

Endpoint
= 클라이언트가 서버에 요청하는 특정 주소

baseURL
= Axios 요청 앞에 자동으로 붙는 기본 서버 주소
```

---

# 오늘의 최종 정리

> 컴포넌트는 UI를 역할별로 나누는 단위이며, 부모 컴포넌트는 Props를 통해 자식에게 객체나 함수의 참조를 전달한다. 일반 변수는 값이 변경되어도 React가 이를 감지하지 않지만, State는 변경될 때 컴포넌트를 다시 렌더링한다. API 통신처럼 렌더링 외부에서 발생하는 작업은 `useEffect`에서 실행하며, 의존성 배열을 통해 최초 한 번 또는 특정 State가 변경될 때만 실행되도록 제어한다.
