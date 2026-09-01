# 2일차 웹 작동 원리 내용 분류

## 1. 웹 애플리케이션의 기본 구조

웹 애플리케이션은 크게 클라이언트와 서버로 구성된다.

* 클라이언트(Client)

  * 사용자가 이용하는 브라우저
  * 프론트엔드 영역
  * 화면을 보여주고 사용자의 입력을 받음

* 서버(Server)

  * 백엔드 영역
  * 클라이언트의 요청을 처리
  * 데이터베이스와 통신
  * 처리 결과를 클라이언트에 응답

기본적인 통신 흐름은 다음과 같다.

클라이언트 요청(Request)
→ 서버 처리
→ 서버 응답(Response)
→ 브라우저 화면 렌더링

서버를 실행하면 특정 포트가 할당되고, 사용자는 IP 주소와 포트 번호를 이용해 서버에 접근할 수 있다.

예시:

```text
http://127.0.0.1:5500/fe/front/html/greeting.html
```

이 주소는 브라우저가 특정 서버의 특정 자원에 접근하기 위한 URL 또는 엔드포인트다.

---

## 2. 정적 웹과 웹 애플리케이션

### 정적 웹

HTML, CSS, JavaScript 파일을 미리 작성해 두고 그대로 사용자에게 제공하는 방식이다.

```text
.html + .css + .js
```

정적 파일은 서버에서 별도의 데이터 처리를 하지 않아도 브라우저에서 표시할 수 있다.

### 웹 애플리케이션

HTML에 미리 작성된 내용만 보여주는 것이 아니라, 프로그램과 서버를 통해 동적인 데이터를 가져와 화면에 표시하는 형태다.

예를 들어 서버나 데이터베이스에서 사용자 정보를 가져와 HTML 요소에 표시한다면 웹 애플리케이션이라고 볼 수 있다.

---

## 3. Full Browsing과 SPA

### Full Browsing 방식

사용자가 다른 페이지를 요청할 때마다 서버가 새로운 페이지를 응답하는 방식이다.

서버는 다음과 같은 페이지를 만들어 브라우저에 전달할 수 있다.

* HTML
* JSP
* Servlet을 통해 생성된 화면

페이지가 100개라면 각각에 해당하는 페이지를 만들어 관리해야 할 수 있다.

흐름:

```text
페이지 요청
→ 서버에서 새로운 HTML 생성
→ 브라우저에 전체 페이지 응답
→ 전체 화면 다시 렌더링
```

### SPA

SPA는 Single Page Application의 약자다.

브라우저에 기본 HTML 페이지 하나를 띄운 뒤, 페이지가 바뀔 때마다 전체 HTML을 새로 받는 것이 아니라 화면 내부의 컴포넌트만 변경한다.

흐름:

```text
브라우저에 기본 페이지 로드
→ 서버 또는 API에 데이터 요청
→ JSON 데이터 응답
→ 컴포넌트 변경
→ 화면 일부 다시 렌더링
```

컴포넌트는 서버나 API로부터 JSON 데이터를 받아 그 데이터를 바탕으로 HTML 요소를 렌더링한다.

대표적인 SPA 기술:

* React
* Vue
* Next.js

단, Next.js는 SPA 기능뿐 아니라 서버 사이드 렌더링 등 다양한 렌더링 방식을 지원한다.

---

## 4. HTML의 역할과 구조

HTML은 웹페이지의 구조를 담당한다.

HTML5는 브라우저에서 사용하는 HTML 표준 문법이다.

HTML 문서는 태그와 텍스트로 구성되며, 태그 안에 다른 태그가 중첩되는 구조를 가진다.

기본 구조:

```html
<html>
    <head>
    </head>

    <body>
    </body>
</html>
```

관계:

* `html`: 부모 요소
* `head`, `body`: 자식 요소

브라우저는 HTML 코드를 파싱한 뒤 화면에 렌더링한다.

### HTML 속성

HTML 태그에는 속성을 추가할 수 있다.

예시:

```html
<meta charset="UTF-8">
```

여기서 `charset`은 속성이고, `UTF-8`은 속성값이다.

속성은 일반적으로 다음 형태로 작성한다.

```text
속성명="속성값"
```

---

## 5. HTML, CSS, JavaScript의 역할

### HTML

웹페이지의 구조와 내용을 담당한다.

```text
HTML = 구조
```

예시:

* 제목
* 입력창
* 버튼
* 이미지
* 문단

### CSS

HTML 요소의 디자인과 표현을 담당한다.

```text
CSS = 표현
```

CSS 적용 방식:

* 인라인 스타일
* 내부 스타일
* 외부 CSS 파일 연결

외부 CSS 연결 예시:

```html
<link href="../css/greeting.css" rel="stylesheet">
```

CSS는 선택자와 속성·값의 형태로 작성한다.

```css
선택자 {
    속성: 값;
}
```

### JavaScript

HTML 요소의 동작과 이벤트 처리를 담당한다.

```text
JavaScript = 동작
```

예시:

* 버튼 클릭 처리
* 입력값 확인
* 서버로 데이터 전송
* 화면 내용 변경
* 콘솔에 값 출력

외부 JavaScript 연결 예시:

```html
<script src="../js/greeting.js"></script>
```

---

## 6. HTML 요소와 선택자

HTML 태그가 브라우저에서 객체로 표현되면 이를 요소(Element)라고 한다.

JavaScript에서 특정 요소에 접근하려면 요소를 구분할 수 있는 값이 필요하다.

대표적으로 다음 속성을 사용한다.

* `id`
* `class`

### ID

하나의 고유한 요소를 구분할 때 사용한다.

```html
<input id="email">
```

CSS에서는 `#`으로 접근한다.

```css
#email {
}
```

JavaScript에서도 특정 요소를 찾는 용도로 자주 사용한다.

### Class

여러 요소에 공통된 스타일이나 기능을 적용할 때 사용한다.

```html
<button class="btn">
```

CSS에서는 `.`으로 접근한다.

```css
.btn {
}
```

일반적으로 다음과 같이 많이 활용한다.

* `class`: CSS 스타일 적용
* `id`: JavaScript 요소 식별

하지만 `class`와 `id` 모두 CSS와 JavaScript에서 사용할 수 있다.

---

## 7. 이벤트 처리

이벤트는 사용자가 웹페이지에서 수행하는 동작이다.

예시:

* 클릭
* 입력
* 마우스 이동
* 키보드 입력
* 페이지 로드

### 이벤트 구성

* 이벤트(Event)

  * 발생한 동작

* 이벤트 소스(Event Source)

  * 이벤트가 발생한 대상

* 이벤트 핸들러(Event Handler)

  * 이벤트 발생 시 실행되는 함수

예시:

```javascript
function handleClick() {
    console.log("버튼 클릭");
}
```

버튼 클릭 이벤트가 발생하면 이벤트 핸들러 내부의 비즈니스 로직이 실행된다.

---

## 8. Form과 Action을 이용한 데이터 전달

HTML은 form 태그를 이용해 사용자가 입력한 데이터를 서버로 전달할 수 있다.

전체적인 흐름은 다음과 같다.

사용자가 input에 값 입력
→ form이 입력값 수집
→ action에 지정된 목적지로 전송
→ 백엔드가 요청 데이터 수신

예시:

```html
<form action="/server-destination" method="get">
    <input type="email" name="email">
    <input type="password" name="pswd">
    <button type="submit">전송</button>
</form>
```

### Form의 역할

`form`은 사용자의 입력을 하나로 묶어 서버에 전달하는 입력 양식이다.

```text
form = 입력 양식
```

사용자가 전송 버튼을 누르면 `form` 내부에 있는 입력값들이 `action`에 지정된 주소로 이동한다.

### Action의 역할

`action`은 입력 데이터를 전달할 서버의 목적지다.

```html
<form action="/server-destination">
```

위 코드에서는 사용자가 입력한 데이터가 `/server-destination`으로 전달된다.

```text
form 입력값
→ action에 명시된 destination
→ 백엔드 서버
```

### Input의 Type

`input`의 `type` 속성에 따라 입력 양식이 달라진다.

```html
<input type="text">
<input type="email">
<input type="password">
<input type="number">
```

대표적인 입력 타입은 다음과 같다.

* `text`: 일반 문자열
* `email`: 이메일
* `password`: 비밀번호
* `number`: 숫자
* `submit`: Form 전송 버튼

### Name의 역할

`name` 속성은 서버로 데이터를 전달할 때 사용하는 데이터의 이름이다.

```html
<input type="email" name="email">
```

사용자가 입력한 값은 `name`을 기준으로 서버에 전달된다.

예를 들어 사용자가 다음과 같이 입력했다고 가정한다.

```text
email 입력값 = test@email.com
pswd 입력값 = 1234
```

서버에는 다음과 같은 형태로 전달될 수 있다.

```text
email=test@email.com
pswd=1234
```

따라서 `name`은 데이터를 담아 전달하기 위한 변수명 또는 데이터의 키와 비슷한 역할을 한다.

다만 실제 입력값은 `name`에 저장되는 것이 아니라 해당 `input` 요소의 `value`에 저장된다.

```text
name = 데이터의 이름 또는 키
value = 사용자가 입력한 실제 값
```

### Form Action 전체 흐름

```text
사용자가 input에 데이터 입력
→ input의 value에 값 저장
→ name을 데이터의 키로 사용
→ submit 이벤트 발생
→ form이 데이터를 수집
→ action에 지정된 destination으로 Request 전송
→ 백엔드가 데이터 수신
```

예시:

```text
email 입력창
name="email"
value="test@email.com"

비밀번호 입력창
name="pswd"
value="1234"
```

전송 결과:

```text
/server-destination?email=test%40email.com&pswd=1234
```

`method="get"`을 사용하는 경우 입력값이 Query String 형태로 URL에 포함될 수 있다.

---


## 9. Query String과 Path Variable

### Query String

URL 뒤에 `?`를 붙여 데이터를 전달하는 방식이다.

예시:

```text
http://127.0.0.1:5500/server-destination?email=test@email.com&pswd=1234
```

구조:

```text
URL?key=value&key=value
```

* `?`: URL과 Query String 구분
* `=`: 키와 값 구분
* `&`: 여러 데이터 구분

Form에서 GET 방식으로 데이터를 전송하면 Query String 형태로 전달될 수 있다.

### Path Variable

URL의 경로 자체에 데이터를 포함하는 방식이다.

예시:

```text
/users/1224
```

여기서 `1224`는 특정 사용자나 자원의 ID로 사용할 수 있다.

SPA만 Path Variable을 사용하는 것은 아니며, 일반적인 웹 애플리케이션과 REST API에서도 널리 사용한다.

---

## 10. 클라이언트에서 데이터베이스까지의 처리 흐름

웹 애플리케이션에서 사용자가 입력한 데이터는 다음과 같은 흐름으로 처리된다.

```text
Client Event 발생
→ Form에서 사용자 입력 수집
→ Frontend가 Backend로 Request 전송
→ Request Parameter 전달
→ Backend가 값을 수신
→ 비즈니스 로직 수행
→ Database 저장 또는 조회
→ Backend가 Frontend로 Response 반환
→ 브라우저가 결과 렌더링
```

### 1. Client Event

사용자가 버튼을 클릭하거나 입력값을 제출하면 이벤트가 발생한다.

```text
Client Event = Click
```

예시:

```html
<button type="submit">로그인</button>
```

버튼을 클릭하면 `submit` 이벤트가 발생하고, `form`의 데이터가 서버로 전달된다.

### 2. Form에서 입력값 수집

`form`은 사용자의 입력을 받는 입력 양식이다.

```text
Form = 입력 양식
```

예시:

```html
<form action="/login">
    <input type="email" name="email">
    <input type="password" name="pswd">
</form>
```

`form` 내부에서 입력한 값은 `name`과 `value`의 조합으로 정리된다.

```text
email=test@email.com
pswd=1234
```

### 3. Backend로 Request 전달

프론트엔드는 입력 데이터를 HTTP Request에 담아 백엔드로 전달한다.

```text
Frontend
→ Request
→ Backend
```

Request에는 사용자가 입력한 데이터가 포함된다.

```text
Request Parameter

email=test@email.com
pswd=1234
```

여기서 사용자의 입력 데이터는 일반적으로 요청 파라미터 또는 `Request Parameter`라고 부른다.

### 4. Backend에서 값 수신

백엔드는 Request Parameter를 컨트롤러나 메서드의 Parameter에 연결해 받는다.

예시:

```java
public void login(String email, String pswd) {
}
```

요청 데이터가 다음과 같다면,

```text
email=test@email.com
pswd=1234
```

실제 메서드 호출은 개념적으로 다음과 같이 이해할 수 있다.

```java
login("test@email.com", "1234");
```

용어를 구분하면 다음과 같다.

```text
Parameter
= 메서드를 정의할 때 선언하는 변수

Argument
= 메서드를 호출할 때 실제로 전달하는 값
```

예시:

```java
public void login(String email) {
}
```

여기서 `email`은 Parameter다.

```java
login("test@email.com");
```

여기서 `"test@email.com"`은 Argument다.

웹 요청 흐름에서는 다음과 같이 이해할 수 있다.

```text
Request Parameter가 전달됨
→ Backend가 Request Parameter를 읽음
→ 메서드 Parameter에 값을 연결
→ 실제 값이 Argument처럼 전달되어 메서드 실행
```

따라서 다음 표현보다,

```text
백엔드가 Parameter를 Argument로 받는다
```

다음과 같이 표현하는 것이 더 정확하다.

```text
백엔드가 Request Parameter의 값을
메서드 Parameter에 바인딩하여 받는다
```

### 5. Database 저장 또는 조회

백엔드는 전달받은 값을 이용해 비즈니스 로직을 수행한다.

예시:

```text
회원가입
→ 사용자 정보 DB 저장

로그인
→ DB에서 사용자 정보 조회

회원정보 수정
→ DB 데이터 변경
```

데이터베이스에는 Parameter나 Argument라는 용어가 저장되는 것이 아니라, 이를 통해 전달된 실제 데이터가 저장된다.

```text
Request Parameter
→ Backend 변수에 저장
→ 비즈니스 로직 수행
→ 실제 값을 Database에 저장
```

예시:

```text
email = test@email.com
pswd = 1234
```

위와 같은 실제 값이 데이터베이스 컬럼에 저장된다.

### 6. Backend의 Response

백엔드는 데이터베이스 처리 결과를 프론트엔드에 Response로 전달한다.

```text
Backend
→ Response
→ Frontend
```

Response에는 다음과 같은 내용이 포함될 수 있다.

* HTML 페이지
* JSP 페이지
* JSON 데이터
* 처리 성공 또는 실패 상태
* 오류 메시지

### 7. Response 방식에 따른 화면 구성

서버가 무엇을 응답하는지에 따라 화면 처리 방식이 달라질 수 있다.

#### Full Browsing

서버가 완성된 페이지를 응답한다.

```text
Backend
→ HTML 또는 JSP 페이지 Response
→ 브라우저가 전체 페이지 다시 렌더링
```

#### SPA

서버가 주로 JSON 데이터를 응답한다.

```text
Backend
→ JSON 데이터 Response
→ JavaScript 또는 Component가 데이터 수신
→ 필요한 화면만 변경
```

### 전체 흐름 정리

```text
1. Client에서 Click 이벤트 발생

2. Form이 사용자의 입력값 수집

3. Form의 action에 지정된 Backend 주소로 Request 전송

4. 입력값은 Request Parameter 형태로 전달

5. Backend가 Request Parameter를
   메서드 Parameter에 바인딩하여 수신

6. 전달된 실제 값은 메서드 실행 시 Argument로 사용

7. Backend가 비즈니스 로직 수행

8. 필요한 데이터를 Database에 저장하거나 조회

9. Backend가 Frontend로 Response 전달

10. Full Browsing이면 페이지 전체를 응답하고,
    SPA이면 주로 JSON 데이터를 응답

11. 브라우저가 결과를 화면에 렌더링
```

한 줄로 정리하면 다음과 같다.

```text
Client Event
→ Form
→ Action
→ Request
→ Request Parameter
→ Backend Method Parameter
→ Argument로 값 사용
→ Database
→ Response
→ Full Browsing 또는 SPA 렌더링
```

## 11. JSON 데이터

JSON은 클라이언트와 서버가 데이터를 주고받을 때 많이 사용하는 형식이다.

예시:

```json
{
    "email": "test@email.com",
    "name": "홍길동"
}
```

SPA에서는 서버로부터 HTML 페이지 전체를 받기보다 JSON 데이터를 받아 화면을 구성하는 경우가 많다.

```text
서버에서 JSON 응답
→ JavaScript가 데이터 수신
→ 컴포넌트에 데이터 적용
→ HTML 요소 렌더링
```

---

## 12. Bootstrap과 반응형 웹

반응형 웹은 화면 크기에 따라 웹페이지의 구조와 디자인이 조정되는 방식이다.

예시:

* PC 화면
* 태블릿 화면
* 모바일 화면

Bootstrap은 반응형 웹페이지를 쉽게 만들 수 있도록 미리 작성된 CSS와 JavaScript를 제공하는 프론트엔드 라이브러리다.

Bootstrap 사용 방식:

* 파일을 직접 내려받아 사용
* CDN 링크로 연결

단순 HTML 파일도 CSS 미디어 쿼리를 사용하면 반응형으로 만들 수 있다. Bootstrap은 반응형 구현을 쉽게 도와주는 도구다.

---

## 13. CDN

CDN은 Content Delivery Network의 약자다.

인터넷을 통해 외부 서버에 저장된 CSS, JavaScript, 이미지 등의 파일을 가져와 사용하는 방식이다.

Bootstrap CDN 사용 방식:

```html
<link href="Bootstrap CSS 주소" rel="stylesheet">
<script src="Bootstrap JavaScript 주소"></script>
```

장점:

* 별도 파일 다운로드 없이 사용 가능
* 빠르게 라이브러리를 적용할 수 있음
* 가까운 CDN 서버에서 파일을 받을 수 있음

주의점:

* 인터넷 연결이 필요할 수 있음
* 외부 CDN 서비스에 영향을 받을 수 있음

---

## 14. 웹 표준

웹 개발에서 사용하는 대표적인 표준은 다음과 같다.

* HTML5

  * 웹페이지 구조 표준

* CSS3

  * 웹페이지 디자인 표준

* ES6

  * JavaScript의 주요 표준 버전

ES6는 ECMAScript 2015라고도 부른다.

---

## 15. CRUD

CRUD는 데이터를 처리하는 기본 기능을 의미한다.

* Create

  * 생성

* Read

  * 조회

* Update

  * 수정

* Delete

  * 삭제

예시:

```text
회원가입 = Create
회원정보 조회 = Read
회원정보 수정 = Update
회원 탈퇴 = Delete
```

---

## 16. 코딩 컨벤션

코딩 컨벤션은 코드의 작성 규칙과 이름을 정하는 기준이다.

### Pascal Case

각 단어의 첫 글자를 대문자로 작성한다.

```text
SignIn
UserController
MainPage
```

클래스 이름 등에 많이 사용한다.

### Camel Case

첫 단어는 소문자로 시작하고 이후 단어의 첫 글자를 대문자로 작성한다.

```text
signIn
userName
getUserData
```

변수명과 메서드명에 많이 사용한다.

### Snake Case

단어 사이를 밑줄로 구분한다.

```text
sign_in
user_name
get_user_data
```

Python 변수명이나 데이터베이스 컬럼명 등에 많이 사용한다.

파일명에 어떤 표기법을 사용할지는 프로젝트 규칙에 따라 달라질 수 있다.

---

## 17. 컴파일, 바이트코드, 바이너리 코드

소스코드는 실행 방식에 따라 바이트코드나 기계어로 변환될 수 있다.

### 바이트코드

가상머신에서 실행되는 중간 코드다.

대표적인 예:

```text
Java 소스코드
→ 컴파일
→ 바이트코드
→ JVM에서 실행
```

JVM은 Java Virtual Machine의 약자다.

### 바이너리 코드

0과 1로 표현된 데이터다.

실행 가능한 기계어는 CPU가 직접 처리할 수 있다.

### 인터프리터

코드를 실행 시점에 해석하면서 실행하는 프로그램이다.

JavaScript는 브라우저의 JavaScript 엔진이 코드를 해석하고 실행한다.

현대 JavaScript 엔진은 단순 인터프리터 방식뿐 아니라 JIT 컴파일도 함께 사용한다.

---

## 18. HTML과 JavaScript 오류 처리

HTML은 일반적인 프로그래밍 언어처럼 위에서 아래로 명령을 한 줄씩 실행하는 구조라기보다, 브라우저가 문서 전체를 파싱하여 DOM을 구성한다.

일부 HTML 태그에 문제가 있어도 브라우저가 가능한 범위에서 문서를 복구해 화면을 표시할 수 있다.

JavaScript는 실행 도중 오류가 발생하면 해당 코드 이후의 실행이 중단될 수 있다.

따라서 다음 내용을 구분해야 한다.

* HTML 오류

  * 브라우저가 일부 자동 보정 가능

* JavaScript 오류

  * 실행 흐름이 중단될 수 있음

---

## 19. 파일 확장자와 화면 구성

웹페이지는 다양한 파일과 기술을 이용해 구성할 수 있다.

대표적인 예:

* `.html`

  * 일반 HTML 문서

* `.jsx`

  * JavaScript 안에서 HTML과 유사한 문법을 작성하는 형식
  * React에서 주로 사용

* `.css`

  * 스타일 파일

* `.js`

  * JavaScript 파일

각 파일의 기본 역할:

```text
HTML 또는 JSX = 구조
CSS = 표현
JavaScript = 동작
```
