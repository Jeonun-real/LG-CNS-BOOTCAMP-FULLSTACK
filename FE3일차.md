# FE 3일차 핵심 개념 정리

## 1. JavaScript로 할 수 있는 것

JavaScript를 사용하면 다음과 같은 작업을 수행할 수 있다.

* HTML 요소 탐색 및 수정
* 브라우저 이벤트 처리
* 사용자 입력값 확인
* `fetch`, Axios를 이용한 서버 통신
* Promise 기반 비동기 처리
* JSON 데이터 수신 및 가공
* 조건에 따른 화면 렌더링
* 프론트엔드 데이터 유효성 검사

전체적인 흐름은 다음과 같다.

```text
사용자 이벤트 발생
→ 이벤트 핸들러 실행
→ 입력값 취득
→ 데이터 검증
→ 서버에 비동기 요청
→ JSON 응답 수신
→ DOM 또는 컴포넌트 렌더링
```

---

## 2. DOM과 BOM

### DOM

DOM은 `Document Object Model`의 약자다.

브라우저가 HTML 문서를 읽고 각 태그를 트리 구조의 객체로 변환한 것이다.

```text
HTML 문서
→ 브라우저가 파싱
→ 객체 형태의 DOM 생성
```
HTML문서 정보가 담겨있는 객체가 `document`로 저장되며 구조는 다음과 같은 트리형로 저장된다.


JavaScript는 `document` 객체를 통해 HTML 요소를 탐색하고 수정할 수 있다.

document 객체는 다음과 같이 트리형태로 저장한다. 
```text
window
└─ document
   └─ html
      ├─ head
      └─ body
         └─ button
```

```javascript
const title = document.querySelector("h1");

title.textContent = "변경된 제목";
```

DOM을 통해 수행할 수 있는 대표 작업은 다음과 같다.

```text
Create = 요소 생성
Read = 요소 조회
Update = 내용 또는 속성 수정
Delete = 요소 제거
Event = 요소의 이벤트 처리
```

### BOM

BOM은 `Browser Object Model`의 약자다.

HTML 문서가 아니라 브라우저 자체를 제어하기 위한 객체 모델이다.

대표적인 BOM 객체는 다음과 같다.

```text
window
= 브라우저의 최상위 객체

location
= 현재 URL 확인 및 페이지 이동

navigator
= 브라우저와 사용자 환경 정보

screen
= 사용자 화면 크기 정보

history
= 방문 기록과 페이지 이동 제어
```

예시:

```javascript
window.alert("알림창");

console.log(window.location.href);
console.log(window.screen.width);
```

```text
DOM = 웹 문서 제어
BOM = 브라우저 환경 제어
```

---

## 3. 함수와 메서드

### 함수

함수는 특정 기능을 수행하는 코드 블록이다.

```javascript
function add(x, y) {
    return x + y;
}
```

함수 이름을 통해 독립적으로 호출할 수 있다.

```javascript
add(10, 20);
```

JavaScript는 동적 타입 언어이기 때문에 매개변수에 자료형을 지정하지 않아도 된다.

```javascript
function test(x, y) {
}
```

### 메서드

객체의 속성으로 저장된 함수를 메서드라고 한다.

```javascript
const user = {
    name: "jslim",

    study: function () {
        return "JavaScript";
    }
};
```

호출할 때는 객체를 통해 접근한다.

```javascript
user.study();
```

```text
함수
= 독립적으로 호출하는 기능

메서드
= 객체에 포함되어 객체를 통해 호출하는 함수
```

---

## 4. 함수도 값이다

JavaScript에서 함수는 숫자나 문자열처럼 변수에 저장할 수 있다.

```javascript
const message = "hello";
const number = 10;

const handler = function () {
    console.log("버튼 클릭");
};
```

화살표 함수도 변수에 저장할 수 있다.

```javascript
const handler = () => {
    console.log("버튼 클릭");
};
```

즉, JavaScript에서 함수는 다음과 같은 곳에 저장할 수 있다.

* 변수
* 객체의 속성
* 배열
* 다른 함수의 매개변수
* HTML 요소 객체의 이벤트 핸들러 프로퍼티

이 특징 때문에 함수를 다른 함수에 전달하거나 이벤트에 연결할 수 있다.

---

## 5. 익명 함수와 화살표 함수

### 익명 함수

이름이 없는 함수를 변수에 저장할 수 있다.

```javascript
const buttonHandler = function () {
    console.log("버튼 클릭");
};
```

함수 자체에는 이름이 없지만 `buttonHandler`라는 변수를 통해 호출할 수 있다.

```javascript
buttonHandler();
```

### 화살표 함수

ES6부터 화살표 함수를 사용할 수 있다.

```javascript
const buttonHandler = () => {
    console.log("버튼 클릭");
};
```

매개변수가 있는 경우:

```javascript
const add = (a, b) => {
    return a + b;
};
```

중괄호를 생략하면 화살표 뒤의 값이 자동으로 반환된다.

```javascript
const add = (a, b) => a + b;
```

다음 코드는 값을 반환하지 않는다.

```javascript
const add = (a, b) => {
    a + b;
};
```

중괄호를 사용했다면 `return`을 직접 작성해야 한다.

```javascript
const add = (a, b) => {
    return a + b;
};
```

---

## 6. 객체 안의 함수

객체의 속성으로 함수를 저장할 수 있다.

```javascript
const obj = {
    name: "jslim",

    study: function () {
        return {
            html: "HTML",
            script: "JavaScript",
            css: "CSS"
        };
    }
};
```

`study`는 `obj` 객체에 포함된 함수이므로 메서드다.

```javascript
const result = obj.study();

console.log(result.html);
console.log(result.script);
console.log(result.css);
```

---

## 7. 객체 구조 분해 할당

객체의 값을 각각의 변수로 꺼낼 때 구조 분해 할당을 사용할 수 있다.

기존 방식:

```javascript
const result = obj.study();

const html = result.html;
const script = result.script;
const css = result.css;
```

구조 분해 할당:

```javascript
const { html, script, css } = obj.study();
```

객체는 키를 기준으로 값을 꺼내기 때문에 순서는 중요하지 않다.

```javascript
const { css, html, script } = obj.study();
```

다른 변수 이름으로 저장하려면 다음과 같이 작성한다.

```javascript
const { html: htmlValue } = obj.study();
```

---

## 8. 객체 속성 축약 문법

변수 이름과 객체의 키 이름이 같다면 한 번만 작성할 수 있다.

```javascript
const html = "HTML";
const script = "JavaScript";
const css = "CSS";
```

기존 방식:

```javascript
const result = {
    html: html,
    script: script,
    css: css
};
```

축약 문법:

```javascript
const result = {
    html,
    script,
    css
};
```

---

## 9. 배열 반복

```javascript
const loopAry = [
    "HTML",
    "CSS",
    "JavaScript"
];
```

### 일반 for문

```javascript
for (let idx = 0; idx < loopAry.length; idx++) {
    console.log(
        `idx=${idx}, value=${loopAry[idx]}`
    );
}
```

### forEach

```javascript
loopAry.forEach((value, idx) => {
    console.log(
        `idx=${idx}, value=${value}`
    );
});
```

* `value`: 현재 배열 요소
* `idx`: 현재 요소의 인덱스

### for...of

배열의 값을 순회한다.

```javascript
for (const value of loopAry) {
    console.log(value);
}
```

### for...in

배열의 인덱스나 객체의 키를 순회한다.

```javascript
for (const idx in loopAry) {
    console.log(
        `idx=${idx}, value=${loopAry[idx]}`
    );
}
```

배열의 값을 순회할 때는 보통 `forEach`나 `for...of`가 직관적이다.

---

## 10. map, filter, reduce

배열 메서드에는 함수를 인자로 전달할 수 있다.

### map

배열의 각 요소를 변환하여 새로운 배열을 만든다.

```javascript
const ages = [10, 20, 30];

const doubledAges = ages.map(
    (age) => age * 2
);
```

결과:

```javascript
[20, 40, 60]
```

### filter

조건에 맞는 요소만 골라 새로운 배열을 만든다.

```javascript
const evenAges = ages.filter(
    (age) => age % 2 === 0
);
```

### reduce

배열을 순회하면서 하나의 결과값으로 누적한다.

```javascript
const userAry = [
    {
        email: "naver.com",
        nickName: "Ironman"
    },
    {
        email: "google.com",
        nickName: "Bob"
    },
    {
        email: "daum.com",
        nickName: "Alice"
    }
];
```

이 배열을 이메일을 키로 사용하는 객체로 변경할 수 있다.

```javascript
const userObj = userAry.reduce(
    (acc, user) => {
        acc[user.email] = user.nickName;
        return acc;
    },
    {}
);
```

결과:

```javascript
{
    "naver.com": "Ironman",
    "google.com": "Bob",
    "daum.com": "Alice"
}
```

동작 흐름:

```text
초기 누적값 {}
→ 첫 번째 email과 nickName 추가
→ 두 번째 email과 nickName 추가
→ 세 번째 email과 nickName 추가
→ 최종 객체 반환
```

---

## 11. DOM 요소 탐색

### getElementById

HTML의 `id`를 기준으로 하나의 요소를 찾는다.

```javascript
const display =
    document.getElementById("display");
```

내용 변경:

```javascript
document
    .getElementById("display")
    .textContent = "아침부터 재미있다";
```

### querySelector

CSS 선택자 문법으로 첫 번째 요소 하나를 찾는다.

```javascript
const email =
    document.querySelector("#email");

const button =
    document.querySelector(".btn");
```

### querySelectorAll

조건에 맞는 모든 요소를 찾는다.

```javascript
const buttons =
    document.querySelectorAll(".btn");
```

반환값은 배열과 비슷한 `NodeList`다.

```javascript
buttons.forEach((button) => {
    console.log(button);
});
```

---

## 12. 요소와 입력값의 차이

다음 코드는 HTML 요소 자체를 가져온다.

```javascript
const emailElement =
    document.querySelector("#email");
```

입력창에 작성된 실제 값은 `value`로 가져온다.

```javascript
const email =
    document.querySelector("#email").value;
```

```text
document.querySelector("#email")
= input 요소 객체

document.querySelector("#email").value
= input 요소 안에 입력된 실제 값
```

---

## 13. 브라우저 이벤트

이벤트는 사용자가 브라우저에서 수행하거나 브라우저에서 발생하는 동작이다.

대표적인 이벤트는 다음과 같다.

* 클릭
* 더블 클릭
* 입력
* 값 변경
* 키보드 입력
* 페이지 로드
* Form 제출

이벤트는 다음 요소로 구분할 수 있다.

```text
이벤트
= 발생한 동작

이벤트 소스
= 이벤트가 발생한 대상

이벤트 핸들러
= 이벤트가 발생했을 때 실행되는 함수
```

버튼 클릭을 예로 들면 다음과 같다.

```text
이벤트 = click
이벤트 소스 = button
이벤트 핸들러 = 클릭 시 실행되는 함수
```

---

## 14. click, onclick, 이벤트 핸들러

오늘 가장 헷갈렸던 부분은 다음 코드였다.

```javascript
document.querySelector("#handler").onclick = () => {
    window.alert("handler button click");
};
```

이 코드는 다음 두 단계로 나누어 이해할 수 있다.

```javascript
const button =
    document.querySelector("#handler");

button.onclick = () => {
    window.alert("handler button click");
};
```

### 요소 찾기

```javascript
document.querySelector("#handler")
```

HTML에서 `id="handler"`인 요소를 찾아 HTML 요소 객체로 반환한다.

```html
<button id="handler">클릭</button>
```

따라서 `button`에는 버튼 요소 객체가 저장된다.

```javascript
const button =
    document.querySelector("#handler");
```

### onclick에 함수 저장하기

```javascript
button.onclick = () => {
    window.alert("handler button click");
};
```

`onclick`은 버튼 객체가 가지고 있는 **이벤트 핸들러 프로퍼티**다.

일반 객체의 속성에 값을 넣는 것과 비슷하게 생각할 수 있다.

```javascript
const user = {
    name: "Ironman"
};

user.name = "Batman";
```

`user.name`에는 문자열을 저장한다.

```javascript
button.onclick = () => {
    window.alert("handler button click");
};
```

`button.onclick`에는 함수를 저장한다.

```text
user.name
= 문자열을 저장하는 객체의 속성

button.onclick
= 클릭 이벤트가 발생했을 때 실행할 함수를 저장하는 속성
```

따라서 다음 부분을 변수처럼 이해할 수 있다.

```javascript
button.onclick
```

하지만 정확히는 일반 변수가 아니라 **버튼 요소 객체의 프로퍼티**다.

---

## 15. click이 onclick으로 변하는 것인가?

`click`과 `onclick`은 서로 관련은 있지만 같은 것은 아니다.

```text
click
= 클릭이라는 이벤트의 이름

onclick
= click 이벤트 발생 시 실행할 함수를 저장하는 프로퍼티
```

`click` 앞에 `on`이 붙으면서 이벤트가 변수로 변하는 것은 아니다.

`onclick`이라는 프로퍼티가 미리 존재하고, 그곳에 함수를 저장하는 것이다.

```text
click
= 이벤트

onclick
= 클릭 이벤트 핸들러 함수를 저장하는 속성

() => {}
= 실제로 실행되는 이벤트 핸들러 함수
```

전체 흐름:

```text
사용자가 버튼을 클릭
→ click 이벤트 발생
→ 브라우저가 button.onclick 확인
→ onclick에 저장된 함수 호출
→ alert 실행
```

---

## 16. 이벤트 구성 요소 정리

다음 코드에서 각각의 역할은 다음과 같다.

```javascript
document.querySelector("#handler").onclick = () => {
    window.alert("handler button click");
};
```

```text
이벤트 소스
= id가 handler인 버튼

이벤트
= click

이벤트 핸들러 프로퍼티
= onclick

이벤트 핸들러 함수
= () => { window.alert(...) }
```

한 줄로 정리하면 다음과 같다.

```text
버튼의 onclick 프로퍼티에 함수를 저장하면,
브라우저가 click 이벤트를 감지했을 때 해당 함수를 실행한다.
```

---

## 17. 객체 관점에서 onclick 이해하기

버튼 요소도 JavaScript에서는 하나의 객체다.

개념적으로 다음과 비슷하게 생각할 수 있다.

```javascript
const button = {
    id: "handler",
    onclick: null
};
```

처음에는 `onclick`에 함수가 저장되지 않은 상태다.

```javascript
button.onclick = null;
```

다음 코드를 실행하면 `onclick`에 함수가 들어간다.

```javascript
button.onclick = () => {
    window.alert("handler button click");
};
```

개념적으로 다음과 같은 객체가 된다.

```javascript
const button = {
    id: "handler",

    onclick: () => {
        window.alert("handler button click");
    }
};
```

이후 사용자가 버튼을 클릭하면 브라우저가 개념적으로 다음처럼 호출한다.

```javascript
button.onclick();
```

직접 호출하는 것은 아니며 브라우저가 클릭 이벤트를 감지한 뒤 자동으로 실행한다.

---

## 18. 함수 저장과 함수 호출의 차이

이벤트에는 함수의 실행 결과가 아니라 **함수 자체를 저장해야 한다.**

### 함수 자체 저장

```javascript
button.onclick = handleClick;
```

클릭 이벤트가 발생했을 때 `handleClick`이 실행된다.

### 함수 즉시 호출

```javascript
button.onclick = handleClick();
```

이 코드는 클릭을 기다리지 않고 현재 시점에 `handleClick()`을 실행한다.

그리고 함수의 반환값이 `onclick`에 저장된다.

이벤트 연결에는 보통 함수 자체를 전달한다.

```javascript
button.onclick = handleClick;
```

또는 화살표 함수로 감싼다.

```javascript
button.onclick = () => {
    handleClick();
};
```

---

## 19. HTML의 onclick 속성

HTML 태그 안에 직접 이벤트 핸들러를 작성할 수도 있다.

```html
<button
    type="button"
    class="btn btn-danger"
    onclick="signInHandler(event)"
>
    Sign In
</button>
```

JavaScript:

```javascript
const signInHandler = (event) => {
    window.alert("Sign In 버튼 클릭");

    console.log(event.target);
    console.log(event.target.value);
};
```

```text
onclick
= click 이벤트 발생 시 실행할 코드 지정

signInHandler
= 실행할 이벤트 핸들러 함수

event
= 발생한 이벤트 정보를 담은 객체

event.target
= 실제 이벤트가 발생한 HTML 요소
```

---

## 20. addEventListener

이벤트 등록에는 `addEventListener()` 방식도 많이 사용한다.

```javascript
document
    .querySelector("#handler")
    .addEventListener(
        "click",
        () => {
            window.alert(
                "handler button click"
            );
        }
    );
```

구조:

```javascript
요소.addEventListener(
    "이벤트 이름",
    이벤트 핸들러 함수
);
```

여기서는 이벤트 이름인 `click`을 문자열로 전달한다.

```text
"click"
= 감지할 이벤트 종류

() => {}
= 이벤트 발생 시 실행할 함수
```

`onclick` 방식과 비교하면 다음과 같다.

```javascript
button.onclick = handleClick;
```

```javascript
button.addEventListener(
    "click",
    handleClick
);
```

`addEventListener()`의 장점:

* HTML과 JavaScript 분리 가능
* 하나의 요소에 여러 이벤트 핸들러 등록 가능
* 이벤트 관리와 제거가 편리함

---

## 21. 페이지 로드 이벤트

페이지가 로드되는 것도 하나의 이벤트다.

DOM 구성이 완료된 뒤 JavaScript를 실행하려면 다음과 같이 작성할 수 있다.

```javascript
document.addEventListener(
    "DOMContentLoaded",
    () => {
        console.log(
            "HTML 문서 로드 완료"
        );
    }
);
```

또는 `script` 태그를 `body`의 마지막에 작성한다.

```html
<body>
    <button id="handler">
        클릭
    </button>

    <script src="./main.js"></script>
</body>
```

HTML 요소가 먼저 만들어진 후 JavaScript가 실행되므로 안전하게 요소에 접근할 수 있다.

---

## 22. DOM을 이용한 CSS 변경

HTML:

```html
<div
    id="err_empty_email_pwd"
    style="display: none;"
>
    이메일과 비밀번호를 입력하세요.
</div>
```

JavaScript:

```javascript
const errorMessage =
    document.querySelector(
        "#err_empty_email_pwd"
    );

errorMessage.style.display = "block";
errorMessage.style.color = "red";
```

대표적인 `display` 값:

```text
none
= 화면에서 숨김

block
= 블록 요소로 표시

inline
= 인라인 요소로 표시

flex
= Flexbox 레이아웃으로 표시
```

---

## 23. 값을 화면에 넣고 가져오기

### 값을 넣기

JavaScript의 값을 입력 요소에 표시할 수 있다.

```javascript
document.querySelector("#email").value =
    user.email;

document.querySelector("#pwd").value =
    user.password;
```

개념적으로 화면에 값을 설정하는 `set` 작업이다.

### 값을 가져오기

사용자가 입력한 값을 변수에 저장한다.

```javascript
const email =
    document.querySelector("#email").value;

const password =
    document.querySelector("#pwd").value;
```

개념적으로 화면에서 값을 읽는 `get` 작업이다.

---

## 24. className과 classList

JavaScript에서 요소의 전체 클래스 문자열을 변경할 때 `className`을 사용할 수 있다.

```html
<div class="container"></div>
```

```javascript
const div =
    document.querySelector("div");

div.className = "error";
```

기존 클래스에 새로운 클래스를 추가하거나 제거할 때는 `classList`가 편리하다.

```javascript
div.classList.add("error");
div.classList.remove("container");
div.classList.toggle("active");
```

```text
className
= 요소의 전체 class 문자열 변경

classList
= 특정 class 추가, 제거, 전환
```

---

## 25. textContent와 innerHTML

### textContent

문자열을 텍스트 그대로 출력한다.

```javascript
document.querySelector("#display")
    .textContent = "<h2>Hello</h2>";
```

화면:

```text
<h2>Hello</h2>
```

HTML 태그로 해석하지 않는다.

### innerHTML

문자열을 HTML 코드로 해석한다.

```javascript
document.querySelector("#display")
    .innerHTML = "<h2>Hello</h2>";
```

화면에는 `h2` 요소로 변환된 `Hello`가 표시된다.

외부 입력값을 `innerHTML`에 직접 넣으면 보안 문제가 발생할 수 있으므로 주의해야 한다.

---

## 26. 조건문

프론트엔드에서는 서버에서 받은 데이터를 조건에 따라 다르게 렌더링할 수 있다.

```javascript
const age = 18;
const price = 48000;

if (age >= 19) {
    console.log(
        `성인 인증되었습니다. 가격은 ${price}원입니다.`
    );
} else {
    console.log(
        `청소년 인증되었습니다. 가격은 ${price / 2}원입니다.`
    );
}
```

프론트엔드에서도 1차 데이터 검증을 수행하지만, 백엔드에서도 반드시 다시 검증해야 한다.

```text
Frontend 검증
= 사용자 편의와 빠른 오류 안내

Backend 검증
= 데이터 무결성과 보안 유지
```

---

## 27. 문자열을 숫자로 변환하기

`prompt()`가 반환하는 값은 문자열이다.

```javascript
const choice = window.prompt(
    "자판기에서 원하는 메뉴 번호를 선택하세요."
);
```

숫자로 사용하려면 `Number()` 또는 `parseInt()`를 사용한다.

```javascript
const choice = Number(
    window.prompt(
        "자판기에서 원하는 메뉴 번호를 선택하세요."
    )
);
```

또는:

```javascript
const choice = parseInt(
    window.prompt(
        "자판기에서 원하는 메뉴 번호를 선택하세요."
    ),
    10
);
```

---

## 28. switch문

하나의 값을 여러 경우와 비교할 때 사용할 수 있다.

```javascript
switch (choice) {
    case 1:
        console.log("토레타");
        break;

    case 2:
        console.log("콜라");
        break;

    case 3:
        console.log("사이다");
        break;

    default:
        console.log(
            "올바른 메뉴 번호를 입력하세요."
        );
}
```

`break`가 없으면 일치한 `case` 이후 코드까지 계속 실행된다.

```text
if...else
= 범위나 복합 조건 비교에 적합

switch
= 하나의 값을 여러 경우와 비교할 때 적합
```

---

## 29. 삼항 연산자

삼항 연산자는 간단한 조건에 따라 값을 선택할 때 사용한다.

```javascript
const result =
    age >= 19
        ? "성인"
        : "청소년";
```

문법:

```text
조건식 ? 참일 때 값 : 거짓일 때 값
```

삼항 연산자는 속도를 높이기 위한 문법이라기보다 간단한 조건을 짧게 표현하기 위한 문법이다.

복잡한 조건에서는 `if...else`나 `switch`를 사용하는 것이 읽기 쉽다.

---

## 30. 일반 JavaScript와 React의 차이

일반 JavaScript에서는 변수값만 변경한다고 화면이 자동으로 바뀌지 않는다.

```javascript
let message = "Hello";

message = "World";
```

화면을 바꾸려면 DOM도 직접 수정해야 한다.

```javascript
document.querySelector("#display")
    .textContent = message;
```

React나 Vue에서는 상태값을 기반으로 화면을 다시 렌더링할 수 있다.

```text
일반 JavaScript
= 데이터 변경 후 DOM을 직접 수정

React·Vue
= 상태 변경을 감지해 화면 다시 렌더링
```

화면과 연결되어 렌더링에 영향을 주는 데이터를 `state`라고 한다.

---

## 31. 서버 데이터와 프론트엔드 렌더링

백엔드는 데이터를 주로 JSON 형식으로 전달한다.

```json
{
    "name": "Ironman",
    "age": 30
}
```

프론트엔드는 JSON을 받은 뒤 데이터를 가공하고 화면에 출력한다.

```text
Backend
→ JSON 응답

Frontend
→ JSON 수신
→ 조건문과 배열 메서드로 가공
→ DOM 또는 컴포넌트 렌더링
```

---

## 32. Bootstrap Grid

Bootstrap의 Grid 시스템은 한 행을 기본적으로 12개의 칸으로 나눈다.

```html
<div class="row">
    <div class="col-3">
        3칸
    </div>

    <div class="col-9">
        9칸
    </div>
</div>
```

```text
col-12
= 한 행 전체

col-6
= 한 행의 절반

col-4
= 한 행의 3분의 1

col-3
= 한 행의 4분의 1
```

`.container`는 Bootstrap 레이아웃을 담는 반응형 영역이다.

```html
<div class="container">
    <div class="row">
        <div class="col-12">
            콘텐츠
        </div>
    </div>
</div>
```

---

## 33. 전체 흐름

```text
브라우저가 HTML 문서 로드
→ DOM 객체 생성
→ JavaScript가 DOM 요소 탐색
→ 이벤트 핸들러 프로퍼티 또는 addEventListener에 함수 등록
→ 사용자가 버튼 클릭 또는 값 입력
→ click 등의 이벤트 발생
→ 브라우저가 등록된 이벤트 핸들러 함수 호출
→ input의 value 취득
→ 조건문으로 데이터 검증
→ fetch 또는 Axios로 서버 요청
→ Promise를 통해 JSON 응답 처리
→ map, filter, reduce로 데이터 가공
→ textContent, innerHTML, style 등으로 화면 변경
```

---

## 핵심 요약

```text
DOM
= JavaScript가 HTML 문서를 제어하기 위한 객체 구조

BOM
= JavaScript가 브라우저를 제어하기 위한 객체 구조

함수
= 특정 기능을 수행하는 코드 블록

메서드
= 객체에 포함된 함수

함수도 값
= 변수나 객체의 프로퍼티에 저장 가능

click
= 클릭 이벤트의 이름

onclick
= click 이벤트 발생 시 실행할 함수를 저장하는 프로퍼티

이벤트 소스
= 이벤트가 발생한 HTML 요소

이벤트 핸들러
= 이벤트 발생 시 실제로 실행되는 함수

button.onclick = 함수
= 버튼 객체의 onclick 프로퍼티에 함수 저장

addEventListener
= 이벤트 이름과 이벤트 핸들러 함수를 등록하는 방식

querySelector
= CSS 선택자로 첫 번째 요소 탐색

querySelectorAll
= CSS 선택자로 모든 요소 탐색

value
= 입력 요소의 실제 값

textContent
= 문자열을 텍스트 그대로 출력

innerHTML
= 문자열을 HTML로 해석

className
= 전체 class 문자열 변경

classList
= class 추가, 제거, 전환

map
= 배열의 값을 변환해 새로운 배열 반환

filter
= 조건에 맞는 요소만 새로운 배열로 반환

reduce
= 배열을 하나의 결과값으로 누적

state
= 화면 렌더링과 연결된 데이터
```

### 오늘 가장 중요하게 이해한 내용

```text
document.querySelector("#handler")
= id가 handler인 버튼 요소 객체

document.querySelector("#handler").onclick
= 해당 버튼 객체의 이벤트 핸들러 프로퍼티

() => {}
= onclick에 저장되는 이벤트 핸들러 함수

사용자가 버튼을 클릭
→ click 이벤트 발생
→ 브라우저가 onclick에 저장된 함수 실행
```
