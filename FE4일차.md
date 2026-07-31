# FE 4일차 핵심 개념 정리

## 1. JSX와 컴포지션

### JSX

JSX는 JavaScript 안에서 HTML과 유사한 문법을 작성할 수 있도록 만든 문법 확장이다.

```jsx
const element = <h1>Hello</h1>;
```

React에서 주로 사용하며 파일 확장자는 `.jsx`를 사용할 수 있다.

```text
JSX
= JavaScript 안에서 UI 구조를 표현하는 문법
```

### 컴포지션

컴포지션은 하나의 컴포넌트 안에 다른 컴포넌트를 넣어 화면을 조립하는 방식이다.

```jsx
function App() {
    return (
        <main>
            <Header />
            <UserList />
            <Footer />
        </main>
    );
}
```

```text
작은 컴포넌트
→ 서로 조합
→ 하나의 화면 구성
```

---

## 2. DOM 요소 생성

JavaScript에서는 DOM API를 이용해 HTML 요소를 직접 생성할 수 있다.

```javascript
const card = document.createElement("div");
```

위 코드는 다음 HTML 요소를 생성한다.

```html
<div></div>
```

클래스를 지정할 수 있다.

```javascript
card.className = "card";
```

결과:

```html
<div class="card"></div>
```

`className`은 태그 안에 글자를 넣는 것이 아니라 HTML 요소의 `class` 속성을 설정한다.

---

## 3. DOM 요소에 내용 넣기

새로운 태그를 생성하고 `textContent`로 내용을 넣을 수 있다.

```javascript
const name = document.createElement("h3");
name.textContent = object.username;

const email = document.createElement("p");
email.textContent = object.email;

const city = document.createElement("p");
city.textContent =
    object.address?.city ?? "Seoul";
```

생성되는 구조:

```html
<h3>사용자 이름</h3>
<p>사용자 이메일</p>
<p>사용자 도시</p>
```

---

## 4. appendChild로 요소 조립하기

`appendChild()`는 특정 요소의 자식으로 다른 요소를 추가한다.

```javascript
card.appendChild(name);
card.appendChild(email);
card.appendChild(city);
card.appendChild(deleteButton);
```

결과:

```html
<div class="card">
    <h3>사용자 이름</h3>
    <p>사용자 이메일</p>
    <p>사용자 도시</p>
    <button>삭제</button>
</div>
```

완성된 카드를 전체 컨테이너에 추가할 수 있다.

```javascript
container.appendChild(card);
```

결과:

```html
<div class="container" id="container">
    <div class="card">
        <h3>사용자 이름</h3>
        <p>사용자 이메일</p>
        <p>사용자 도시</p>
        <button>삭제</button>
    </div>
</div>
```

전체 관계는 다음과 같다.

```text
name, email, city, button
→ card의 자식으로 추가
→ 완성된 card
→ container의 자식으로 추가
```

---

## 5. innerHTML을 이용한 요소 생성

간단한 HTML 구조는 `innerHTML`로 생성할 수도 있다.

```javascript
document.querySelector("#test").innerHTML =
    "<center>Inspire Camp</center>";
```

`innerHTML`은 문자열을 HTML 태그로 해석한다.

```text
textContent
= 문자열을 그대로 출력

innerHTML
= 문자열을 HTML로 해석
```

단순한 구조에는 편리하지만, 반복되는 복잡한 요소를 만들거나 이벤트를 연결할 때는 `createElement()`가 관리하기 쉽다.

외부 입력값을 `innerHTML`에 직접 넣으면 보안 문제가 발생할 수 있으므로 주의해야 한다.

---

## 6. Skeleton UI

Skeleton UI는 실제 데이터가 도착하기 전에 화면의 뼈대 모양을 먼저 보여주는 UI다.

```text
서버에 데이터 요청
→ Skeleton UI 표시
→ 데이터 로딩
→ Skeleton UI 제거
→ 실제 데이터 렌더링
```

비동기 통신에서는 서버 응답까지 시간이 걸릴 수 있기 때문에 사용자가 빈 화면만 보지 않도록 로딩 상태를 표시한다.

---

## 7. JSON의 역할

Java와 JavaScript는 객체를 표현하는 방식과 타입 체계가 다르다.

```text
Java
= 정적 타입 언어

JavaScript
= 동적 타입 언어
```

서버와 프론트엔드는 서로의 객체를 그대로 전달하는 것이 아니라 JSON 형식으로 데이터를 교환한다.

```text
서버 객체
→ JSON 문자열로 변환
→ 네트워크 전송
→ JavaScript 객체 또는 배열로 변환
```

예시:

```json
{
    "username": "Ironman",
    "email": "ironman@example.com"
}
```

JSON은 서로 다른 언어와 시스템이 데이터를 교환할 수 있도록 사용하는 공통 데이터 형식이다.

---

## 8. 객체, 클래스, 인스턴스

객체지향에서는 현실의 대상을 프로그램으로 표현할 수 있다.

```text
실세계의 대상
→ 클래스 설계
→ 인스턴스 생성
→ 프로그램에서 사용
```

클래스는 객체를 만들기 위한 설계도이며, 인스턴스는 클래스를 바탕으로 실제 생성된 객체다.

JavaScript에서도 ES6부터 `class` 문법을 사용할 수 있다.

```javascript
class User {
    constructor(name) {
        this.name = name;
    }
}

const user = new User("Ironman");
```

---

## 9. 비동기 처리

비동기는 오래 걸리는 작업이 끝날 때까지 프로그램 전체를 멈추지 않는 처리 방식이다.

```text
서버에 요청
→ 응답을 기다리는 동안 다른 작업 수행
→ 응답 도착
→ 결과 처리
```

웹에서는 다음과 같은 작업에 비동기 처리를 사용한다.

* 서버 데이터 요청
* 파일 읽기
* 타이머
* 데이터베이스 작업
* API 호출

---

## 10. Promise

Promise는 비동기 작업의 성공 또는 실패 결과를 표현하는 JavaScript 객체다.

```javascript
const promise = new Promise(
    (resolve, reject) => {
        // 비동기 작업
    }
);
```

Promise 생성자는 Executor 함수를 전달받는다.

```javascript
(resolve, reject) => {
}
```

* `resolve`: 작업 성공 시 호출
* `reject`: 작업 실패 시 호출

---

## 11. Promise 상태

Promise에는 세 가지 주요 상태가 있다.

```text
pending
= 비동기 작업 처리 중

fulfilled
= 비동기 작업 성공

rejected
= 비동기 작업 실패
```

작업이 성공하거나 실패하여 처리가 끝난 상태를 통틀어 `settled`라고 한다.

```text
pending
→ fulfilled

또는

pending
→ rejected
```

Promise의 결과값은 상태에 따라 달라진다.

```text
pending
= 결과 없음

fulfilled
= resolve에 전달한 값

rejected
= reject에 전달한 오류
```

---

## 12. Promise 동작 예시

```javascript
const promise = new Promise(
    (resolve, reject) => {
        setTimeout(() => {
            resolve({
                status: 200,
                data: {
                    message: "success"
                }
            });
        }, 1000);
    }
);
```

동작 과정:

```text
Promise 객체 생성
→ pending 상태
→ Executor 함수 실행
→ setTimeout으로 1초 대기
→ resolve() 호출
→ fulfilled 상태
→ 성공 결과 전달
```

---

## 13. then과 catch

Promise가 성공하면 `then()`, 실패하면 `catch()`를 실행한다.

```javascript
promise
    .then((response) => {
        console.log("response:", response);
    })
    .catch((error) => {
        console.log("error:", error);
    });
```

```text
resolve(value)
→ fulfilled
→ then의 콜백 함수 실행
→ value가 콜백 매개변수로 전달

reject(error)
→ rejected
→ catch의 콜백 함수 실행
→ error가 콜백 매개변수로 전달
```

Promise의 메서드를 이어서 작성하는 방식을 체이닝이라고 한다.

```javascript
promise
    .then(...)
    .catch(...);
```

---

## 14. async와 await

`async`와 `await`은 Promise 기반 비동기 코드를 읽기 쉽게 작성하기 위한 문법이다.

```javascript
const loadData = async () => {
    const response = await fetch("./user.json");
    const data = await response.json();

    console.log(data);
};
```

### async

함수가 비동기 함수임을 나타낸다.

```javascript
const loadData = async () => {
};
```

`async` 함수는 항상 Promise를 반환한다.

### await

Promise의 처리가 끝나 결과가 나올 때까지 해당 비동기 함수 내부의 다음 코드 실행을 기다린다.

```javascript
const response =
    await fetch("./user.json");
```

`await`이 실행된다고 프로그램 전체가 멈추는 것은 아니다.

```text
await가 있는 async 함수 내부
= 다음 코드 실행 대기

함수 외부
= 다른 작업 수행 가능
```

---

## 15. fetch 동작 과정

```javascript
fetch("./user.json")
    .then((response) => response.json())
    .then((data) => {
        console.log(data);
    })
    .catch((error) => {
        console.log(error);
    });
```

`fetch()`는 Promise를 반환한다.

첫 번째 `then()`에서는 서버의 응답 객체를 받는다.

```text
Response 객체

status
ok
headers
body
```

`response.json()`은 응답 Body의 JSON 문자열을 JavaScript 객체나 배열로 변환한다.

```javascript
.then((response) => response.json())
```

`response.json()`도 Promise를 반환하기 때문에 다음 `then()`에서 실제 데이터를 받는다.

```javascript
.then((data) => {
    console.log(data);
})
```

전체 흐름:

```text
fetch 요청
→ Response 객체 수신
→ response.json() 실행
→ JSON을 JavaScript 값으로 변환
→ data에서 사용
```

---

## 16. fetch와 Axios의 차이

### fetch

브라우저에서 기본으로 제공하는 HTTP 통신 기능이다.

```javascript
fetch("./user.json")
    .then((response) => response.json())
    .then((data) => {
        console.log(data);
    })
    .catch((error) => {
        console.log(error);
    });
```

JSON 데이터를 사용하려면 `response.json()`을 호출해야 한다.

### Axios

Axios는 HTTP 통신을 편리하게 사용할 수 있도록 만든 라이브러리다.

```javascript
axios
    .get("./user.json")
    .then((response) => {
        console.log(response.data);
    })
    .catch((error) => {
        console.log(error);
    });
```

Axios는 JSON 응답을 자동으로 JavaScript 객체로 변환해 `response.data`에 넣어준다.

```text
fetch
= response.json() 직접 호출

Axios
= response.data로 변환된 데이터 접근
```

---

## 17. Optional Chaining

Optional Chaining은 객체의 중간 속성이 `null` 또는 `undefined`여도 오류가 발생하지 않도록 안전하게 접근하는 문법이다.

```javascript
object.address?.city
```

기존 접근:

```javascript
object.address.city
```

`address`가 없다면 오류가 발생할 수 있다.

Optional Chaining을 사용하면 다음과 같이 처리된다.

```text
address가 존재
→ city 반환

address가 null 또는 undefined
→ undefined 반환
```

---

## 18. Nullish Coalescing

Null 병합 연산자인 `??`는 왼쪽 값이 `null` 또는 `undefined`일 때 기본값을 사용한다.

```javascript
object.address?.city ?? "Seoul"
```

```text
city가 존재
→ city 값 사용

city가 null 또는 undefined
→ "Seoul" 사용
```

Optional Chaining과 함께 기본값을 설정할 때 자주 사용한다.

```javascript
const city =
    user.address?.city ?? "Seoul";
```

---

## 19. 서버 데이터로 카드 생성하기

HTML:

```html
<div
    class="container"
    id="container"
></div>
```

JavaScript 데이터:

```javascript
let users = [];
```

데이터를 가져온 뒤 배열에 저장한다.

```javascript
const loadData = async () => {
    const response = await fetch(
        "https://jsonplaceholder.typicode.com/users"
    );

    users = await response.json();
};
```

카드를 만드는 함수:

```javascript
const makeCards = () => {
    const container =
        document.querySelector("#container");

    container.innerHTML = "";

    users.forEach((user, index) => {
        const card =
            document.createElement("div");

        card.className = "card";

        const name =
            document.createElement("h3");

        name.textContent = user.username;

        const email =
            document.createElement("p");

        email.textContent = user.email;

        const city =
            document.createElement("p");

        city.textContent =
            user.address?.city ?? "Seoul";

        const deleteButton =
            document.createElement("button");

        deleteButton.className = "btn";
        deleteButton.textContent = "삭제";

        deleteButton.onclick = () => {
            users.splice(index, 1);
            makeCards();
        };

        card.appendChild(name);
        card.appendChild(email);
        card.appendChild(city);
        card.appendChild(deleteButton);

        container.appendChild(card);
    });
};
```

---

## 20. 카드 생성 흐름

```text
users 배열 순회
→ 사용자마다 div.card 생성
→ h3 생성 후 username 입력
→ p 생성 후 email 입력
→ p 생성 후 city 입력
→ 삭제 버튼 생성
→ 모든 요소를 card에 추가
→ card를 container에 추가
```

생성 결과:

```html
<div class="container" id="container">
    <div class="card">
        <h3>사용자 이름</h3>
        <p>사용자 이메일</p>
        <p>사용자 도시</p>
        <button class="btn">
            삭제
        </button>
    </div>
</div>
```

---

## 21. 데이터 삭제와 다시 렌더링

삭제 버튼을 클릭하면 해당 배열 요소를 제거한다.

```javascript
deleteButton.onclick = () => {
    users.splice(index, 1);
    makeCards();
};
```

`splice()`는 배열의 특정 위치에서 요소를 삭제한다.

```javascript
users.splice(index, 1);
```

```text
index
= 삭제할 위치

1
= 삭제할 요소 개수
```

배열에서 데이터만 삭제하면 화면은 자동으로 바뀌지 않는다.

따라서 `makeCards()`를 다시 호출해 DOM을 다시 생성한다.

```text
삭제 버튼 클릭
→ users 배열에서 데이터 삭제
→ 기존 container 내용 초기화
→ 변경된 users 배열로 카드 다시 생성
```

---

## 22. 전체 데이터 로딩 흐름

```javascript
const init = async () => {
    await loadData();
    makeCards();
};

init();
```

`init()` 함수는 다음 순서로 동작한다.

```text
init 실행
→ loadData 실행
→ 서버 데이터 요청
→ 데이터 수신까지 await
→ users 배열에 저장
→ makeCards 실행
→ 사용자 카드 렌더링
```

`loadData()` 내부에서 이미 `makeCards()`를 호출한다면 `init()`에서 다시 호출할 필요는 없다.

```javascript
const init = async () => {
    await loadData();
    makeCards();
};
```

처럼 한 번만 렌더링하도록 구성하는 것이 좋다.

---

## 23. ID를 전역변수처럼 사용하는 방식

HTML 요소의 `id`가 브라우저 환경에서 전역변수처럼 접근되는 경우가 있다.

```html
<div id="container"></div>
```

```javascript
container.innerHTML = "";
```

하지만 이 방식은 브라우저 환경이나 변수 이름 충돌에 영향을 받을 수 있다.

명확하게 요소를 가져오는 방식이 권장된다.

```javascript
const container =
    document.querySelector("#container");
```

```text
권장하지 않는 방식
container.innerHTML = "";

권장 방식
document.querySelector("#container")
```

---

## 24. HTTP Method

대표적인 HTTP Method와 역할은 다음과 같다.

```text
GET
= 데이터 조회

POST
= 데이터 생성

PUT
= 데이터 전체 수정

PATCH
= 데이터 일부 수정

DELETE
= 데이터 삭제
```

POST는 데이터를 자동으로 암호화하는 방식이 아니다.

보안을 위해서는 HTTPS를 사용해야 한다.

---

## 25. Path Variable

URL 경로에 특정 자원의 식별자를 포함하는 방식이다.

```text
https://jsonplaceholder.typicode.com/posts/1
```

여기서 `1`은 특정 게시글을 식별하는 값이다.

```text
/posts
= 게시글 목록

/posts/1
= ID가 1인 게시글
```

---

## 26. JWT와 OAuth

### JWT

JWT는 인증 정보를 토큰 형태로 전달할 때 사용할 수 있다.

일반적으로 HTTP Header에 담아 전달한다.

```text
Authorization: Bearer access-token
```

대표적으로 Access Token을 이용해 사용자의 인증 상태를 확인한다.

### OAuth

OAuth는 외부 서비스의 인증 정보를 이용해 로그인할 수 있도록 하는 표준 방식이다.

예시:

* Google 로그인
* Kakao 로그인
* Naver 로그인

---

## 27. 이벤트 객체와 target

이벤트 핸들러의 매개변수로 이벤트 객체를 받을 수 있다.

```html
<button
    type="button"
    onclick="changeButton(event)"
>
    변경
</button>
```

```javascript
const changeButton = (event) => {
    event.target.textContent =
        "Inspire Coffee";
};
```

`event.target`은 실제 이벤트가 발생한 요소를 의미한다.

```text
event
= 발생한 이벤트 정보

event.target
= 실제 이벤트가 발생한 HTML 요소

event.target.textContent
= 이벤트가 발생한 요소의 글자
```

---

## 28. 함수에 인자 전달하기

HTML에서 이벤트 핸들러 함수에 값을 전달할 수 있다.

```html
<button
    type="button"
    onclick="
        changeContent(
            'LG CNS Coffee',
            'coffee-pink.jpg'
        )
    "
>
    이미지 변경
</button>
```

JavaScript:

```javascript
const changeContent = (
    title,
    imageName
) => {
    console.log(title);
    console.log(imageName);
};
```

호출할 때 전달한 실제 값은 Argument이며, 함수 정의에서 받는 변수는 Parameter다.

```text
'LG CNS Coffee'
= Argument

title
= Parameter
```

---

## 29. Media Query

Media Query는 화면 크기에 따라 서로 다른 CSS를 적용하는 기능이다.

핵심은 화면 크기에 따라 컴포넌트의 크기와 배치를 변경하는 것이다.

```css
@media screen
and (min-width: 320px)
and (max-width: 767px) {
    body {
        background-image:
            url("../img/mobile-bg.jpg");
    }
}
```

태블릿:

```css
@media screen
and (min-width: 768px)
and (max-width: 1023px) {
    body {
        background-image:
            url("../img/tablet-bg.jpg");
    }
}
```

데스크톱:

```css
@media screen
and (min-width: 1024px) {
    body {
        background-image:
            url("../img/desktop-bg.jpg");
    }
}
```

일반적인 화면 구간은 다음처럼 나눌 수 있다.

```text
Mobile
= 약 320px ~ 767px

Tablet
= 약 768px ~ 1023px

Desktop
= 1024px 이상
```

구간은 프로젝트의 디자인 기준에 따라 달라질 수 있다.

---

## 30. Flexbox와 Media Query의 차이

Flexbox는 요소를 한 방향으로 정렬하고 배치하는 CSS 레이아웃 방식이다.

```css
.container {
    display: flex;
    flex-wrap: wrap;
}
```

Media Query는 화면 크기에 따라 CSS 자체를 다르게 적용한다.

```text
Flexbox
= 요소 배치 방법

Media Query
= 화면 크기에 따른 스타일 전환
```

두 기능은 함께 사용할 수 있다.

```css
.container {
    display: flex;
}

@media (max-width: 767px) {
    .container {
        display: block;
    }
}
```

---

## 31. 전체 흐름

```text
페이지 로드
→ async 초기화 함수 실행
→ fetch를 통해 서버 데이터 요청
→ Promise pending 상태
→ 서버 응답 도착
→ Promise fulfilled 상태
→ response.json()으로 JSON 변환
→ 사용자 배열 저장
→ 배열을 forEach로 순회
→ createElement로 카드 생성
→ textContent로 데이터 입력
→ appendChild로 요소 조립
→ container에 카드 렌더링
→ 삭제 버튼 클릭
→ 배열 데이터 삭제
→ 변경된 배열 기준으로 다시 렌더링
```

---

## 핵심 요약

```text
JSX
= JavaScript 안에서 HTML과 유사한 UI 문법 작성

컴포지션
= 컴포넌트를 조합해 화면 구성

DOM Create
= createElement로 HTML 요소 생성

appendChild
= 요소를 부모 요소의 자식으로 추가

Skeleton UI
= 데이터 로딩 전 화면 뼈대 표시

JSON
= 서로 다른 시스템이 데이터를 교환하는 형식

Promise
= 비동기 작업의 성공과 실패 상태를 표현하는 객체

pending
= 비동기 작업 처리 중

fulfilled
= 작업 성공

rejected
= 작업 실패

resolve
= Promise를 성공 상태로 변경

reject
= Promise를 실패 상태로 변경

then
= Promise 성공 결과 처리

catch
= Promise 실패 결과 처리

async
= 비동기 함수를 선언

await
= Promise 결과가 나올 때까지 함수 내부 실행 대기

fetch
= 브라우저 기본 HTTP 요청 기능

Axios
= HTTP 요청 라이브러리

Optional Chaining
= null 또는 undefined를 안전하게 처리

Null 병합 연산자
= null 또는 undefined일 때 기본값 사용

splice
= 배열의 특정 요소 삭제 또는 변경

Media Query
= 화면 크기에 따라 다른 CSS 적용
```
