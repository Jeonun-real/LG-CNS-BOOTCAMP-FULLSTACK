# 3일차 CSS와 JavaScript 핵심 내용 정리

## 1. 라이브러리와 프레임워크

라이브러리와 프레임워크의 가장 큰 차이는 **프로그램의 흐름을 누가 제어하는가**이다.

```text
라이브러리
= 개발자가 필요할 때 기능을 호출
= 애플리케이션의 흐름을 개발자가 제어

프레임워크
= 정해진 구조와 규칙 안에서 개발
= 전체 흐름을 프레임워크가 제어
```

React는 공식적으로 UI를 만들기 위한 **라이브러리**로 소개된다. Vue는 일반적으로 **프로그레시브 프레임워크**로 분류된다.


---

# 2. CSS의 역할

CSS는 `Cascading Style Sheets`의 약자이며, HTML 요소의 디자인과 배치를 담당한다.

```text
HTML = 구조와 내용
CSS = 디자인과 표현
JavaScript = 동작과 데이터 처리
```

HTML과 CSS를 분리하면 다음과 같은 장점이 있다.

* 여러 페이지에서 동일한 디자인을 재사용할 수 있다.
* HTML 구조와 디자인 코드를 분리할 수 있다.
* 디자인을 변경할 때 CSS 파일만 수정할 수 있다.
* 프로젝트 전체의 디자인 일관성을 유지하기 쉽다.

---

# 3. CSS 적용 방식

## 3.1 인라인 스타일

HTML 태그의 `style` 속성에 직접 CSS를 작성한다.

```html
<p style="color: red;">빨간 글자</p>
```

특정 요소에 빠르게 적용할 수 있지만, HTML과 CSS가 섞이기 때문에 재사용성과 유지보수성이 낮다.

---

## 3.2 내부 스타일 시트

HTML 문서의 `head` 안에 `style` 태그를 작성한다.

```html
<head>
    <style>
        span {
            color: red;
        }
    </style>
</head>
```

해당 HTML 문서 안에서만 사용할 수 있다.

---

## 3.3 외부 스타일 시트

CSS를 별도의 파일로 작성하고 HTML에서 `link` 태그로 연결한다.

```html
<link href="../css/card.css" rel="stylesheet">
```

여러 HTML 문서에서 같은 CSS 파일을 사용할 수 있어 실무에서 가장 보편적으로 사용한다.

---

# 4. CSS 기본 문법

CSS는 선택자, 속성, 속성값으로 구성된다.

```css
선택자 {
    속성: 속성값;
}
```

예시:

```css
.btn {
    cursor: pointer;
    border: none;
    border-radius: 8px;
    color: white;
    background-color: black;
}
```

```text
.btn
= 스타일을 적용할 대상인 선택자

background-color
= 적용할 속성

black
= 속성에 들어갈 값
```

---

# 5. CSS 선택자

## 전체 선택자

모든 요소를 선택한다.

```css
* {
    margin: 0;
}
```

## 타입 선택자

특정 HTML 태그를 선택한다.

```css
p {
    color: black;
}
```

## 클래스 선택자

특정 클래스가 적용된 요소를 선택한다.

```css
.btn {
    cursor: pointer;
}
```

HTML:

```html
<button class="btn">확인</button>
```

하나의 `class`는 여러 요소에서 사용할 수 있고, 하나의 요소에 여러 클래스를 적용할 수도 있다.

```html
<button class="btn primary">확인</button>
```

## ID 선택자

특정 ID를 가진 요소를 선택한다.

```css
#login-btn {
    color: white;
}
```

HTML:

```html
<button id="login-btn">로그인</button>
```

`id`는 한 HTML 문서 안에서 고유하게 사용하는 것이 원칙이다.


재사용 가능한 스타일은 일반적으로 `class`를 사용한다.

```text
class
= 여러 요소에 반복 적용
= CSS 스타일에 주로 사용

id
= 문서 안에서 하나의 요소 식별
= 특정 요소를 JavaScript에서 찾을 때 사용할 수 있음
```

---

# 6. 속성 선택자

HTML 속성이나 속성값을 기준으로 요소를 선택할 수 있다.

```css
input[type="email"] {
    border: 1px solid blue;
}
```

속성값의 패턴을 이용할 수도 있다.

```css
/* 특정 값으로 시작 */
a[href^="https"] {
}

/* 특정 값으로 끝 */
a[href$=".pdf"] {
}

/* 특정 값을 포함 */
a[href*="velog"] {
}
```

c로 시작하는 text 태그를 찾아서 h2를 적용

```css
h2[text^="c"]
```

---

# 7. CSS 우선순위

CSS 적용 결과는 단순히 인라인, 내부, 외부 CSS의 순서만으로 결정되지 않는다.

주요 판단 기준은 다음과 같다.

```text
1. !important 여부
2. 선택자의 명시도
3. 같은 명시도라면 나중에 선언된 스타일
```

일반적인 선택자 명시도는 다음과 같다.

```text
인라인 스타일
> ID 선택자
> 클래스·속성·가상 클래스 선택자
> 태그 선택자
> 브라우저 기본 스타일
```

`!important`는 우선순위를 강제로 높일 수 있다.

```css
p {
    color: red !important;
}
```

하지만 스타일 구조를 복잡하게 만들기 때문에 꼭 필요한 상황이 아니라면 남용하지 않는 것이 좋다.

---

# 8. 조합 선택자

HTML 요소 간의 관계를 이용해 선택할 수 있다.

## 후손 선택자

특정 요소 안에 포함된 모든 하위 요소를 선택한다.

```css
div p {
    color: red;
}
```

`div` 내부의 모든 `p`가 대상이다.

## 자식 선택자

바로 아래에 있는 직계 자식만 선택한다.

```css
div > p {
    color: blue;
}
```

## 인접 형제 선택자

기준 요소 바로 다음에 위치한 형제를 선택한다.

```css
h1 + p {
    color: green;
}
```

## 일반 형제 선택자

기준 요소 뒤에 나오는 같은 부모의 모든 형제를 선택한다.

```css
h1 ~ p {
    color: gray;
}
```

## 그룹 선택자

여러 선택자에 동일한 스타일을 적용한다.

```css
h1,
h2,
h3 {
    font-weight: bold;
}
```

직계 자식 선택자가 항상 더 안정적인 것은 아니다. HTML 구조가 변경되면 직계 관계가 깨질 수 있기 때문에, 필요한 범위에 따라 적절한 선택자를 사용해야 한다.

---

# 9. CSS 박스 모델

HTML 요소는 사각형 박스로 배치된다.

박스는 다음 네 영역으로 구성된다.

```text
content
→ padding
→ border
→ margin
```

* `content`: 실제 텍스트나 이미지가 들어가는 영역
* `padding`: 콘텐츠와 테두리 사이의 내부 여백
* `border`: 요소의 테두리
* `margin`: 다른 요소와의 외부 여백

```css
.card {
    padding: 20px;
    border: 1px solid red;
    margin: 10px;
}
```

`border`는 다음처럼 축약해 작성할 수 있다.

```css
border: 1px solid red;
```

```text
1px = border-width
solid = border-style
red = border-color
```

둥근 테두리는 `border-radius`로 지정한다.

```css
border-radius: 8px;
```

---

# 10. 여백 값 작성 순서

`margin`과 `padding`에는 값을 한 개부터 네 개까지 지정할 수 있다.

```css
/* 상하좌우 */
margin: 10px;

/* 상하 / 좌우 */
margin: 10px 20px;

/* 상 / 좌우 / 하 */
margin: 10px 20px 30px;

/* 상 / 우 / 하 / 좌 */
margin: 10px 20px 30px 40px;
```

네 개를 지정하면 위쪽부터 시계 방향으로 적용된다.

```text
top → right → bottom → left
```

메모의 `botton`과 `패징`은 각각 `bottom`, `padding`으로 수정해야 한다.

---

# 11. 공간 분할 태그

## div

`div`는 여러 요소를 하나의 영역으로 묶는 블록 요소다.

```html
<div>
    <h2>제목</h2>
    <p>내용</p>
</div>
```

기본적으로 한 줄 전체 영역을 차지한다.

## span

`span`은 텍스트 일부나 인라인 영역을 묶을 때 사용한다.

```html
<p>이 부분만 <span>강조</span>합니다.</p>
```

기본적으로 필요한 너비만 차지한다.

`div`와 `span`은 의미 자체보다는 요소를 묶거나 스타일을 적용하기 위해 사용한다.

---

# 12. 링크와 이미지

## a 태그

다른 페이지나 같은 문서의 특정 위치로 이동할 때 사용한다.

```html
<a href="https://example.com">외부 사이트</a>
```

상대 경로:

```html
<a href="../html/login.html">로그인 페이지</a>
```

같은 문서의 특정 위치:

```html
<a href="#section1">섹션 1로 이동</a>
```

이동 대상:

```html
<h2 id="section1">섹션 1</h2>
```

## img 태그

이미지를 삽입할 때 사용한다.

```html
<img src="../images/profile.png" alt="프로필 이미지">
```

* `src`: 이미지 경로
* `alt`: 이미지가 표시되지 않을 때 사용할 대체 설명

---

# 13. 목록 태그

## 순서가 없는 목록

```html
<ul>
    <li>HTML</li>
    <li>CSS</li>
</ul>
```

## 순서가 있는 목록

```html
<ol>
    <li>회원가입</li>
    <li>로그인</li>
</ol>
```

## 설명 목록

```html
<dl>
    <dt>HTML</dt>
    <dd>웹페이지의 구조를 표현한다.</dd>

    <dt>CSS</dt>
    <dd>웹페이지의 디자인을 담당한다.</dd>
</dl>
```

`dl`에서는 용어에 `dt`, 설명에 `dd`를 사용한다.

---

# 14. Form과 입력 요소

`form`은 사용자의 입력을 묶어 서버로 전송하기 위한 요소다.

```html
<form action="/users" method="post">
    <input type="text" name="name">
    <button type="submit">전송</button>
</form>
```

* `action`: 데이터를 보낼 서버 주소
* `method`: HTTP 전송 방식
* `name`: 서버에 전달할 데이터의 키
* `value`: 사용자가 입력한 실제 값

## 주요 입력 요소

```html
<input type="text">
<input type="password">
<input type="email">
<input type="checkbox">
<input type="date">
<input type="button">
<textarea></textarea>
```


형식은 설정에는 다음과 같은게 있다.

```text
여러 줄 입력 = textarea
달력 형식 = date
이메일 형식 = email
```

드롭다운 목록은 다음과 같이 작성한다.

```html
<select name="language">
    <option value="java">Java</option>
    <option value="javascript">JavaScript</option>
</select>
```

여러 항목을 선택하려면 `multiple`을 사용한다.

```html
<select name="language" multiple>
```

그룹을 나누려면 `optgroup`을 사용한다.

```html
<select>
    <optgroup label="Backend">
        <option>Java</option>
    </optgroup>

    <optgroup label="Frontend">
        <option>JavaScript</option>
    </optgroup>
</select>
```

---

# 15. Label과 Placeholder

입력창의 이름이나 의미를 표시할 때는 `label`을 사용한다.

```html
<label for="email">이메일</label>
<input
    type="email"
    id="email"
    name="email"
    placeholder="example@email.com"
>
```

* `label`: 입력 요소가 무엇을 의미하는지 설명
* `placeholder`: 입력창 안에 표시되는 입력 예시나 안내 문구

`placeholder`는 Label을 대체하는 용도로 사용하면 안 된다. 사용자가 값을 입력하면 안내 문구가 사라지기 때문이다.

---

# 16. ID와 Name의 차이

```text
id
= 현재 HTML 문서에서 요소를 식별

name
= Form 전송 시 데이터의 키로 사용
```

예시:

```html
<input
    id="user-email"
    name="email"
    value="test@email.com"
>
```

Form 전송 결과:

```text
email=test@email.com
```

`id`와 `name`을 반드시 같게 작성해야 하는 것은 아니다. 역할이 다르기 때문에 필요에 따라 서로 다른 이름을 사용할 수 있다.

SPA에서도 `name`이 전혀 중요하지 않은 것은 아니다. Form 라이브러리, 접근성, 브라우저 자동 완성, FormData 사용 등에서는 여전히 활용된다.

다만 JavaScript로 직접 객체를 만들어 API에 전송한다면 `name` 속성에 의존하지 않을 수 있다.

---

# 17. HTTP Method와 CRUD

HTTP Method는 서버에 어떤 작업을 요청하는지 나타낸다.

```text
POST
= 새로운 데이터 생성에 주로 사용

GET
= 데이터 조회에 주로 사용

PUT
= 자원 전체 수정에 주로 사용

PATCH
= 자원 일부 수정에 주로 사용

DELETE
= 데이터 삭제에 주로 사용
```

CRUD와 연결하면 다음과 같이 이해할 수 있다.

```text
Create → POST
Read   → GET
Update → PUT 또는 PATCH
Delete → DELETE
```

하지만 이것은 일반적인 REST API 설계 관례이지 절대적인 규칙은 아니다.

```text
GET
= 요청 데이터가 URL의 Query String에 포함될 수 있음

POST
= 요청 데이터를 주로 HTTP Body에 담아 전송
```

POST라고 해서 데이터가 자동으로 암호화되는 것은 아니다. 데이터를 보호하려면 HTTPS를 사용해야 한다.

---

# 18. HTML Form의 Method 제한

기본 HTML Form의 `method` 속성은 일반적으로 다음 두 값만 직접 지원한다.

```text
GET
POST
```

따라서 다음 내용은 맞다.

```text
기본 HTML Form에서는 PUT, PATCH, DELETE를 직접 사용하기 어렵다.
```

HTML Form은 GET과 POST를 사용할 수 있다.

PUT, PATCH, DELETE 요청은 JavaScript의 `fetch`, `axios` 등을 이용하거나 서버 프레임워크의 Method Override 기능을 사용할 수 있다.

---

# 19. React와 Form


React에서도 `form`을 많이 사용한다.

다만 브라우저의 기본 Form 제출 동작만 이용하기보다 JavaScript 이벤트로 제출을 제어하는 경우가 많다.

```jsx
function LoginForm() {
    const handleSubmit = (event) => {
        event.preventDefault();
        console.log("로그인 처리");
    };

    return (
        <form onSubmit={handleSubmit}>
            <input type="email" />
            <input type="password" />
            <button type="submit">로그인</button>
        </form>
    );
}
```

React에서는 `onSubmit` 이벤트를 사용해 입력값을 객체로 만들고 API 서버에 전송할 수 있다.

---

# 20. JSX의 의미

```jsx
const element = <h1>Hello</h1>;
```

```text
JSX
= JavaScript Syntax Extension
= React에서 UI 구조를 표현할 때 주로 사용
```

JSX 안에 HTML이 그대로 들어가는 것처럼 보이지만, 빌드 과정에서 JavaScript 코드로 변환된다.

CSS도 JSX 안에 자동으로 포함되는 것은 아니다. CSS 파일, 인라인 스타일, CSS Module, CSS-in-JS 등의 방식을 별도로 사용한다.

---

# 21. DOM

브라우저는 HTML 문서를 읽어 객체 구조로 변환한다. 이를 DOM이라고 한다.

```text
DOM
= Document Object Model
```

JavaScript에서 바라본 HTML 문서는 `document` 객체다.

```javascript
const title = document.querySelector("h1");
```

DOM 조작은 HTML 요소를 생성, 조회, 수정, 삭제하는 작업을 의미한다.

```javascript
const item = document.createElement("li");
item.textContent = "JavaScript";

document.querySelector("ul").appendChild(item);
```

대표적인 DOM 조작:

```text
Create
= 요소 생성

Read
= 요소 조회

Update
= 속성이나 내용 수정

Delete
= 요소 제거
```

DOM 안의 데이터 자체를 Model이라고 단정하기보다는, DOM은 문서 구조를 객체 형태로 표현한 것이라고 이해하는 것이 정확하다.

---

# 22. 동기와 비동기 통신

기존의 페이지 이동 방식에서는 사용자가 서버에 요청할 때 새 페이지 전체를 응답받는 경우가 많았다.

```text
요청
→ 서버가 새로운 페이지 생성
→ 전체 페이지 응답
→ 브라우저 전체 렌더링
```

비동기 통신을 사용하면 페이지 전체를 새로 불러오지 않고 필요한 데이터만 서버와 주고받을 수 있다.

```text
현재 페이지 유지
→ 서버에 데이터 요청
→ 데이터 응답
→ 필요한 화면만 변경
```

비동기 통신에도 요청과 응답이 모두 존재한다. 차이는 요청을 보낸 뒤 응답이 올 때까지 프로그램 전체가 멈춰서 기다리지 않는다는 것이다.

```text
동기
= 작업이 끝날 때까지 기다린 뒤 다음 작업 수행

비동기
= 작업 완료를 기다리는 동안 다른 작업 수행 가능
```

응답이 도착하면 Callback, Promise, `async/await` 등을 이용해 결과를 처리한다.

---

# 23. Fetch, Axios, Promise

`fetch`와 Axios는 서버에 HTTP 요청을 보내기 위해 사용할 수 있다.

```javascript
fetch("/users")
    .then((response) => response.json())
    .then((data) => {
        console.log(data);
    });
```

Axios 예시:

```javascript
axios.get("/users")
    .then((response) => {
        console.log(response.data);
    });
```

Promise는 fetch와 Axios 자체를 이루는 통신 방식이라기보다, **비동기 작업의 성공 또는 실패 결과를 표현하는 JavaScript 객체**다.

```text
Promise 상태

pending
= 처리 중

fulfilled
= 처리 성공

rejected
= 처리 실패
```

`async/await`을 사용하면 Promise 기반 코드를 읽기 쉽게 작성할 수 있다.

```javascript
async function getUsers() {
    const response = await fetch("/users");
    const data = await response.json();

    console.log(data);
}
```

---

# 24. SPA와 Virtual DOM

SPA는 하나의 기본 페이지를 유지하면서 필요한 화면과 데이터를 변경하는 애플리케이션 구조다.

```text
페이지 전체 이동
→ 대신 URL, 데이터, 컴포넌트 상태 변경
→ 필요한 화면 다시 렌더링
```

SPA에서는 컴포넌트가 교체될 수도 있고, 기존 컴포넌트의 데이터나 상태만 변경될 수도 있다.

Virtual DOM은 React와 같은 일부 프레임워크 또는 라이브러리가 UI 변경을 효율적으로 처리하기 위해 사용하는 방식이다.

```text
SPA
= 애플리케이션 구조

Virtual DOM
= UI 변경을 처리하는 구현 방식 중 하나
```

---

# 25. 반응형 웹과 Media Query

반응형 웹은 화면의 너비와 기기 특성에 따라 화면 배치와 크기를 조정하는 방식이다.

```css
.card-container {
    display: flex;
}

@media (max-width: 768px) {
    .card-container {
        display: block;
    }
}
```

이처럼 CSS의 Media Query를 사용해 화면 크기에 따라 다른 스타일을 적용할 수 있다.

---

# 26. px, em, rem

## px

고정된 CSS 픽셀 단위다.

```css
font-size: 16px;
```

## em

현재 요소 또는 부모 요소의 글자 크기를 기준으로 계산되는 상대 단위다.
일반적으로 반응형 웹에 쓰이는 요소

```css
font-size: 1.5em;
```

부모의 글자 크기가 16px라면 다음과 같이 계산된다.

```text
1.5em = 24px
```

## rem

HTML 루트 요소의 글자 크기를 기준으로 계산한다.

```css
font-size: 1.5rem;
```

기본 루트 글자 크기가 16px라면 `1.5rem`은 24px다.

```text
px
= 선, 아이콘, 고정 크기가 필요한 경우 유용

rem
= 전체적으로 일관된 글자 크기와 여백 설정에 유용

em
= 현재 요소의 글자 크기를 기준으로 상대 크기 적용
```

실무에서는 `rem`을 글자 크기와 여백에 많이 사용하고, Media Query와 함께 다양한 단위를 조합한다.

---

# 27. Bootstrap Container

Bootstrap에서는 콘텐츠의 폭을 제한하고 가운데 정렬할 때 `.container`를 사용한다.

```html
<div class="container">
    콘텐츠
</div>
```

화면 전체 너비를 사용하려면 `.container-fluid`를 사용한다.

```html
<div class="container-fluid">
    콘텐츠
</div>
```

```text
.container
= 화면 크기에 따라 최대 너비가 달라지는 반응형 컨테이너
= 좌우 자동 여백을 통해 중앙 정렬

.container-fluid
= 모든 화면 크기에서 width: 100%
```

---

# 28. JavaScript 변수와 자료형

JavaScript는 동적 타입 언어다.

```javascript
let value = 10;
value = "hello";
```

변수 자체에 고정된 자료형을 선언하지 않고, 실행 중 할당된 값에 따라 타입이 결정된다.

```text
JavaScript
= 동적 타입 언어

TypeScript
= JavaScript에 정적 타입 문법을 추가한 언어
```

메모의 다음 표현은 수정해야 한다.

```text
JavaScript는 변수의 타입이 존재하지 않는다.
```

변수에 고정 타입을 선언하지 않을 뿐, 값에는 타입이 존재한다.

---

# 29. JavaScript 기본 타입

대표적인 원시 타입은 다음과 같다.

```text
string
number
boolean
undefined
null
bigint
symbol
```

객체 타입에는 다음과 같은 값이 포함된다.

```text
object
array
function
```

다만 배열은 `typeof` 결과가 `object`로 나온다.

```javascript
const values = [];

console.log(typeof values); // object
console.log(Array.isArray(values)); // true
```

함수의 `typeof` 결과는 `function`이다.

```javascript
function test() {
}

console.log(typeof test); // function
```

---

# 30. var, let, const

ES6 이후에는 `var`보다 `let`과 `const` 사용이 권장된다.

## let

값을 다시 할당할 수 있다.

```javascript
let message = "hello";
message = "world";
```

같은 스코프 안에서 같은 이름으로 다시 선언할 수 없다.

```javascript
let message = "hello";
let message = "world"; // SyntaxError
```

## const

값을 다시 할당할 수 없다.

```javascript
const message = "hello";
message = "world"; // TypeError
```

객체나 배열을 `const`로 선언하면 변수 자체를 다른 값으로 재할당할 수 없다는 뜻이다. 객체 내부의 속성은 변경할 수 있다.

```javascript
const user = {
    name: "Kim"
};

user.name = "Lee"; // 가능
```

---

# 31. ==와 ===

`==`는 비교할 때 타입 변환을 수행할 수 있다.

```javascript
console.log(10 == "10"); // true
```

`===`는 값과 타입을 모두 비교한다.

```javascript
console.log(10 === "10"); // false
```

일반적으로 예상하지 못한 타입 변환을 막기 위해 `===` 사용이 권장된다.

```text
==  = 느슨한 동등 비교
=== = 엄격한 동등 비교
```

변수의 타입은 `typeof`로 확인할 수 있다.

```javascript
const message = 10;

console.log(typeof message); // number
```

---

# 32. 객체와 배열

## 객체

객체는 `key: value` 구조로 데이터를 표현한다.

```javascript
const user = {
    name: "inspire",
    age: 20
};
```

## 배열

여러 값을 순서대로 저장한다.

```javascript
const users = [
    { name: "inspire" },
    { name: "lgcns" },
    { name: "am" },
    { name: "camp6th" }
];
```

```text
/users
→ 여러 사용자 데이터를 배열로 응답할 수 있음

/users/1
→ 한 명의 사용자 객체를 응답할 수 있음
```

---

# 33. console.log와 Template Literal

객체는 쉼표를 이용해 출력하면 구조를 확인하기 쉽다.

```javascript
console.log("debug >>> object value", user);
```

Template Literal도 사용할 수 있다.

```javascript
const name = "inspire";

console.log(`debug >>> name: ${name}`);
```

다만 객체를 문자열 안에 바로 넣으면 다음처럼 출력될 수 있다.

```javascript
console.log(`debug >>> user: ${user}`);
```

출력:

```text
debug >>> user: [object Object]
```

객체 내용을 확인할 때는 다음 방식이 더 좋다.

```javascript
console.log("debug >>> user:", user);
```

또는 JSON 문자열로 변환한다.

```javascript
console.log(`debug >>> user: ${JSON.stringify(user)}`);
```

Template Literal은 ES6에서 추가된 문법이다.

---

# 34. map, filter, forEach

배열 데이터를 이용해 동적으로 화면을 만들거나 데이터를 변환할 때 배열 메서드를 사용할 수 있다.

## map

배열의 각 요소를 변환하여 새로운 배열을 반환한다.

```javascript
const names = users.map((user) => user.name);
```

React에서는 배열 데이터를 컴포넌트로 변환할 때 자주 사용한다.

```jsx
const items = users.map((user) => (
    <li key={user.id}>{user.name}</li>
));
```

## filter

조건에 맞는 요소만 모아 새로운 배열을 반환한다.

```javascript
const adults = users.filter((user) => user.age >= 20);
```

## forEach

배열의 각 요소를 순회하지만 새로운 배열을 반환하지 않는다.

```javascript
users.forEach((user) => {
    console.log(user.name);
});
```

```text
map
= 각 값을 변환해 새로운 배열 생성

filter
= 조건에 맞는 값만 골라 새로운 배열 생성

forEach
= 각 값을 순회하며 작업 수행
```

---

# 35. 오늘 내용의 전체 흐름

```text
HTML로 화면 구조 작성
→ CSS 선택자로 요소에 스타일 적용
→ 박스 모델과 레이아웃으로 화면 구성
→ Form과 Input으로 사용자 데이터 입력
→ JavaScript의 DOM 요소와 이벤트 제어
→ 입력 데이터를 객체 또는 JSON으로 구성
→ javascript 기본 구동 원리
→ 배열의 map, filter, forEach로 기초 설명
```
