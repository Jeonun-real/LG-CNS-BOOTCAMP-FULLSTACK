# FE 7일차 핵심 개념 정리

## 오늘의 한 줄 요약

React에서는 `State`를 기준으로 화면을 다시 렌더링하고, `Props`로 컴포넌트 간 데이터를 전달하며, React Router를 통해 새로고침 없이 페이지를 전환할 수 있다.

---

# 1. React의 렌더링과 State

React 컴포넌트는 처음 실행될 때 한 번 렌더링되고, 이후 State가 변경되면 다시 렌더링된다.

```jsx
const [count, setCount] = useState(0);
```

`useState()`는 배열 형태로 두 개의 값을 반환한다.

```jsx
[
    현재 상태값,
    상태 변경 함수
]
```

따라서 다음 코드는:

```jsx
const [count, setCount] = useState(0);
```

개념적으로 다음과 같다.

```jsx
const result = useState(0);

const count = result[0];
const setCount = result[1];
```

* `count`: 현재 State 값
* `setCount`: State 변경을 요청하는 함수
* `0`: State 초기값

State 변경은 일반 변수 대입과 다르다.

```jsx
setCount(1);
```

이 코드는 현재 `count` 변수에 즉시 `1`을 대입하는 것이 아니라, React에 다음 렌더링에서 값을 변경해 달라고 요청하는 것이다.

```text
setCount 실행
→ 상태 변경 요청 등록
→ 현재 이벤트 함수 계속 실행
→ 이벤트 함수 종료
→ 컴포넌트 재렌더링
→ 변경된 State로 JSX 다시 계산
```

예를 들어:

```jsx
const handler = () => {
    setCount(count + 1);

    console.log(count);
};
```

`console.log(count)`에는 변경 전 값이 출력될 수 있다.

현재 이벤트 함수가 가지고 있는 `count`는 현재 렌더링 시점의 값이기 때문이다.

---

## 함수형 업데이트

이전 State를 기준으로 값을 변경할 때는 함수형 업데이트를 사용하는 것이 안전하다.

```jsx
setCount(prevCount => prevCount + 1);
```

또는 매개변수 이름을 짧게 작성할 수 있다.

```jsx
setCount(cnt => cnt + 1);
```

여기서 `cnt`는 현재 State의 이전 값을 의미한다.

```text
현재 count = 3
→ setCount(cnt => cnt + 1)
→ 다음 count = 4
```

---

## 여러 State 변경과 배칭

같은 이벤트 핸들러 안에서 여러 State를 변경하면 React는 보통 변경 요청을 모아서 한 번에 처리한다.

```jsx
const handler = () => {
    setEmail('test@test.com');
    setPassword('1234');
};
```

흐름은 다음과 같다.

```text
setEmail 요청
→ setPassword 요청
→ 이벤트 함수 종료
→ 두 변경을 묶어서 처리
→ 한 번의 재렌더링
```

이처럼 여러 상태 변경을 묶어서 처리하는 것을 **배칭(Batching)**이라고 한다.

단, 이메일 input과 비밀번호 input에서 각각 입력하면 서로 다른 이벤트가 발생하므로 이벤트마다 재렌더링될 수 있다.

---

# 2. React의 재렌더링 조건

React에서 재렌더링이 발생하는 대표적인 경우는 다음과 같다.

## State 변경

```jsx
setCount(count + 1);
```

State가 변경되면 해당 컴포넌트가 다시 렌더링된다.

## 부모 컴포넌트 재렌더링

부모 컴포넌트가 다시 렌더링되면 자식 컴포넌트도 기본적으로 다시 실행될 수 있다.

```jsx
const Parent = () => {
    const [count, setCount] = useState(0);

    return <Child count={count} />;
};
```

## Props 변경

부모가 자식에게 전달하는 Props가 변경되면 자식 컴포넌트도 새로운 Props를 기준으로 다시 렌더링된다.

```jsx
<Child count={count} />
```

## Router 경로 변경

```jsx
navigate('/success');
```

URL이 바뀌면 React Router가 해당 경로에 맞는 컴포넌트를 렌더링한다.

---

## 재렌더링되지 않는 경우

일반 변수만 변경하면 React는 그 사실을 알 수 없다.

```jsx
let count = 0;

count = count + 1;
```

이 경우 화면은 자동으로 바뀌지 않는다.

API 응답을 일반 변수에 넣는 것만으로도 재렌더링되지 않는다.

```jsx
const response = await api.get('/users');
const users = response.data;
```

화면에 API 데이터를 출력하려면 State에 저장해야 한다.

```jsx
const [users, setUsers] = useState([]);

const response = await api.get('/users');

setUsers(response.data);
```

---

# 3. useEffect

`useEffect()`는 렌더링을 발생시키는 함수가 아니라, **렌더링이 완료된 뒤 실행할 작업을 등록하는 Hook**이다.

```jsx
useEffect(() => {
    console.log('렌더링 이후 실행');
});
```

전체 흐름은 다음과 같다.

```text
State 변경 요청
→ 컴포넌트 재렌더링
→ 변경된 화면 반영
→ useEffect 실행
```

---

## 의존성 배열이 없는 경우

```jsx
useEffect(() => {
    console.log('렌더링될 때마다 실행');
});
```

컴포넌트가 렌더링될 때마다 실행된다.

```text
최초 렌더링
State 변경
Props 변경
부모 재렌더링
→ Effect 실행
```

---

## 빈 의존성 배열

```jsx
useEffect(() => {
    console.log('최초 렌더링 후 한 번 실행');
}, []);
```

컴포넌트가 화면에 처음 나타나는 Mount 시점에 한 번 실행된다.

---

## 값이 들어 있는 의존성 배열

```jsx
useEffect(() => {
    console.log('count 변경:', count);
}, [count]);
```

최초 렌더링 이후 한 번 실행되고, 이후 `count`가 변경된 렌더링 뒤에 실행된다.

```jsx
useEffect(() => {
    console.log('email:', email);
    console.log('password:', password);
}, [email, password]);
```

`email` 또는 `password`가 변경되면 실행된다.

---

## State가 한 글자 늦게 출력되는 이유

```jsx
const emailHandler = (e) => {
    setEmail(e.target.value);

    console.log(email);
};
```

`setEmail()`은 현재 `email` 변수를 즉시 변경하지 않는다.

```text
현재 email = ""

사용자가 "a" 입력
→ e.target.value = "a"
→ setEmail("a") 요청
→ console.log(email)에는 이전 값 "" 출력
→ 이벤트 함수 종료
→ 재렌더링
→ 다음 렌더링에서 email = "a"
```

현재 input 값을 바로 확인하려면 이벤트 객체의 값을 사용한다.

```jsx
const emailHandler = (e) => {
    const value = e.target.value;

    setEmail(value);

    console.log(value);
};
```

State가 반영된 이후 값을 확인하려면 `useEffect()`를 사용한다.

```jsx
useEffect(() => {
    console.log(email);
}, [email]);
```

---

# 4. Side Effect

Side Effect는 컴포넌트의 화면 계산 이외에 외부 환경과 상호작용하는 작업이다.

대표적인 Side Effect는 다음과 같다.

```text
API 호출
localStorage 접근
타이머 등록
DOM 직접 조작
이벤트 리스너 등록
console.log
```

예시:

```jsx
useEffect(() => {
    const userName = localStorage.getItem('userName');

    console.log(userName);
}, []);
```

---

# 5. Pure 함수와 Impure 함수

## Pure 함수

같은 입력을 주면 항상 같은 출력을 반환하고 외부 상태를 변경하지 않는 함수다.

```jsx
const add = (a, b) => {
    return a + b;
};
```

```text
add(1, 2) → 항상 3
```

Pure 함수의 특징:

```text
같은 입력에 항상 같은 출력
외부 변수 변경 없음
API 호출 없음
Side Effect 없음
```

---

## Impure 함수

같은 입력이라도 외부 상태나 실행 시점에 따라 다른 결과가 나올 수 있는 함수다.

```jsx
const getCurrentTime = () => {
    return new Date();
};
```

다른 예시는 다음과 같다.

```jsx
const randomNumber = () => {
    return Math.random();
};
```

Impure 함수의 대표적인 특징:

```text
외부 변수 수정
API 호출
localStorage 접근
현재 시간 조회
랜덤값 생성
DOM 조작
```

---

## React 컴포넌트와 Pure 함수

React 컴포넌트는 Props에 대해 가능한 한 Pure 함수처럼 동작해야 한다.

```jsx
const Greeting = ({ name }) => {
    return <h1>{name}님 환영합니다.</h1>;
};
```

같은 `name`이 들어오면 같은 UI를 반환해야 한다.

```text
name = "홍길동"
→ 항상 "홍길동님 환영합니다."
```

컴포넌트는 전달받은 Props를 직접 변경하면 안 된다.

```jsx
const Greeting = (props) => {
    props.name = '김철수'; // 잘못된 방식

    return <h1>{props.name}</h1>;
};
```

Props는 부모가 자식에게 전달한 읽기 전용 값으로 보는 것이 좋다.

---

# 6. Props

Props는 부모 컴포넌트가 자식 컴포넌트에 전달하는 데이터다.

```jsx
<Greeting flag={flag} />
```

개념적으로 다음 객체를 전달하는 것과 비슷하다.

```jsx
{
    flag: flag
}
```

자식에서는 Props 객체로 받을 수 있다.

```jsx
const Greeting = (props) => {
    return <p>{props.flag}</p>;
};
```

구조 분해 할당으로도 받을 수 있다.

```jsx
const Greeting = ({ flag }) => {
    return <p>{flag}</p>;
};
```

---

## Props로 값 전달

```jsx
<Greeting flag={flag} />
```

현재 State 값을 자식에게 전달한다.

---

## Props로 함수 전달

```jsx
<LogoutButton isLogin={setFlag} />
```

여기서는 boolean 값이 아니라 `setFlag` 함수를 전달한다.

자식이 받는 Props 구조는 다음과 같다.

```jsx
{
    isLogin: setFlag
}
```

자식에서:

```jsx
const LogoutButton = (props) => {
    return (
        <button onClick={() => props.isLogin(false)}>
            로그아웃
        </button>
    );
};
```

`props.isLogin(false)`는 실제로 부모의 다음 코드를 실행하는 것과 같다.

```jsx
setFlag(false);
```

다만 `isLogin`이라는 이름은 boolean처럼 보이기 때문에 함수라는 의미가 드러나는 이름을 사용하는 것이 더 좋다.

```jsx
<LogoutButton setFlag={setFlag} />
```

또는 부모가 동작을 완성해서 전달할 수 있다.

```jsx
<LogoutButton onLogout={() => setFlag(false)} />
```

자식에서는 다음과 같이 사용한다.

```jsx
const LogoutButton = ({ onLogout }) => {
    return (
        <button onClick={onLogout}>
            로그아웃
        </button>
    );
};
```

---

# 7. Props Drilling

컴포넌트 계층이 깊어질수록 Props를 여러 컴포넌트를 거쳐 전달해야 할 수 있다.

```text
A → B → C → D
```

A가 D에게 데이터를 전달해야 하지만 B와 C가 해당 데이터를 사용하지 않더라도 전달만 해야 하는 상황을 Props Drilling이라고 한다.

```jsx
const A = () => {
    const user = { name: '홍길동' };

    return <B user={user} />;
};

const B = ({ user }) => {
    return <C user={user} />;
};

const C = ({ user }) => {
    return <D user={user} />;
};

const D = ({ user }) => {
    return <p>{user.name}</p>;
};
```

B와 C는 `user`를 직접 사용하지 않지만 D까지 전달하기 위해 Props를 받고 다시 넘긴다.

Props Drilling이 깊어지면 다음 방법을 고려할 수 있다.

```text
Context API
Redux
Zustand
MobX
Recoil
```

React Query는 주로 서버 상태를 조회하고 캐싱하는 용도로 사용한다.

---

# 8. Composition

Composition은 컴포넌트 안에 다른 컴포넌트를 조합하여 UI를 만드는 방식이다.

```jsx
const RenderingPage = () => {
    return (
        <div>
            <Greeting />
            <LogoutButton />
        </div>
    );
};
```

React는 부모 JSX 안에 있는 자식 컴포넌트들을 모두 화면 구조에 포함한다.

첫 번째 자식 컴포넌트가 `return`한다고 해서 부모의 뒤쪽 JSX 처리가 종료되는 것은 아니다.

```jsx
<div>
    <Greeting flag={flag} />

    {flag && <LogoutButton />}
</div>
```

실행 흐름:

```text
RenderingPage 실행
→ Greeting 요소 생성
→ 조건부 LogoutButton 요소 계산
→ 전체 JSX 구성
→ 각 자식 컴포넌트 렌더링
```

---

# 9. Conditional Rendering

조건에 따라 렌더링할 컴포넌트를 다르게 결정하는 것을 조건부 렌더링이라고 한다.

## 삼항연산자

```jsx
{
    flag
        ? <LogoutButton />
        : <LoginButton />
}
```

기본 문법:

```jsx
조건 ? 참일 때 값 : 거짓일 때 값
```

예시:

```jsx
const Greeting = ({ flag }) => {
    return flag
        ? <UserGreeting />
        : <GuestGreeting />;
};
```

---

## `&&` 연산자

조건이 참일 때만 특정 컴포넌트를 렌더링하려면 `&&`를 사용할 수 있다.

```jsx
{flag && <LogoutButton />}
```

동작 방식:

```text
true && expression
→ expression 반환

false && expression
→ false 반환
→ 화면에 표시할 요소 없음
```

예시:

```jsx
{isLogin && <p>로그인 상태입니다.</p>}
```

---

## 렌더링하지 않을 때

거짓일 때 빈 문자열보다 `null`을 사용하는 것이 일반적이다.

```jsx
{
    flag
        ? <LogoutButton />
        : null
}
```

---

# 10. Truthy와 Falsy

JavaScript 조건문은 값이 반드시 `true`나 `false`인지뿐 아니라 참 또는 거짓처럼 평가되는지를 판단한다.

## Truthy

실제 boolean `true`는 아니지만 조건문에서 참으로 평가되는 값이다.

```jsx
"hello"
1
-1
[]
{}
function () {}
```

예시:

```jsx
if ('hello') {
    console.log('실행됨');
}
```

비어 있지 않은 문자열은 Truthy다.

---

## Falsy

실제 boolean `false`가 아니더라도 조건문에서 거짓으로 평가되는 값이다.

대표적인 Falsy 값:

```jsx
false
0
-0
0n
''
null
undefined
NaN
```

예시:

```jsx
if ('') {
    console.log('실행되지 않음');
}
```

---

## 빈 배열과 빈 객체

빈 배열과 빈 객체는 Truthy다.

```jsx
Boolean([]); // true
Boolean({}); // true
```

따라서 배열에 데이터가 있는지 검사할 때는 배열 자체가 아니라 `length`를 확인해야 한다.

```jsx
const users = [];

if (users.length > 0) {
    console.log('사용자가 존재함');
}
```

---

# 11. React 이벤트

일반 HTML 이벤트는 다음과 같이 작성한다.

```html
<button onclick="handler()">버튼</button>
```

React에서는 이벤트 이름을 camelCase로 작성하고 함수 자체를 전달한다.

```jsx
<button onClick={handler}>
    버튼
</button>
```

---

## 이벤트 객체

```jsx
const handler = (e) => {
    console.log(e);
};
```

React는 이벤트가 발생하면 이벤트 객체 `e`를 이벤트 핸들러의 첫 번째 인자로 전달한다.

```jsx
<input
    onChange={(e) => console.log(e.target.value)}
/>
```

* `e.target`: 실제 이벤트를 발생시킨 요소
* `e.target.value`: 해당 input의 현재 값
* `e.currentTarget`: 이벤트 핸들러가 등록된 요소

---

## 화살표 함수 사용

다음 코드는 이벤트가 발생할 때 실행할 함수를 전달한다.

```jsx
onChange={(e) => setEmail(e.target.value)}
```

별도 함수로 분리할 수도 있다.

```jsx
const emailHandler = (e) => {
    setEmail(e.target.value);
};
```

```jsx
<input onChange={emailHandler} />
```

두 코드는 같은 역할을 한다.

다음처럼 괄호를 붙이면 렌더링 중 즉시 실행되므로 주의해야 한다.

```jsx
onClick={handler()} // 일반적으로 잘못된 사용
```

함수 자체를 전달해야 한다.

```jsx
onClick={handler}
```

매개변수를 추가로 전달해야 한다면 화살표 함수로 감싼다.

```jsx
onClick={() => handler(10)}
```

---

# 12. preventDefault와 이벤트 버블링

## preventDefault

`preventDefault()`는 브라우저 요소의 기본 동작을 막는다.

```jsx
const submitHandler = (e) => {
    e.preventDefault();
};
```

`form`을 제출하면 브라우저가 기본적으로 페이지를 이동하거나 새로고침할 수 있다.

```jsx
<form onSubmit={submitHandler}>
    <button type="submit">로그인</button>
</form>
```

흐름:

```text
submit 이벤트 발생
→ e.preventDefault()
→ form의 기본 제출 동작 차단
→ React 코드로 직접 로그인 처리
```

---

## 이벤트 버블링

자식 요소에서 발생한 이벤트가 부모 요소 방향으로 전달되는 현상이다.

```jsx
<div onClick={() => console.log('부모')}>
    <button onClick={() => console.log('버튼')}>
        클릭
    </button>
</div>
```

버튼을 클릭하면:

```text
버튼 이벤트 실행
→ 부모 div 이벤트 실행
```

버블링을 막으려면 `stopPropagation()`을 사용한다.

```jsx
const buttonHandler = (e) => {
    e.stopPropagation();
};
```

중요한 차이:

```text
preventDefault()
→ 요소의 기본 동작 차단

stopPropagation()
→ 부모 방향으로 이벤트 전파되는 것을 차단
```

---

# 13. Form과 Controlled Component

Form은 사용자로부터 값을 입력받는 HTML 구조다.

React에서 State를 통해 input 값을 관리하면 제어 컴포넌트라고 한다.

```jsx
const [email, setEmail] = useState('');
```

```jsx
<input
    type="email"
    value={email}
    onChange={(e) => setEmail(e.target.value)}
/>
```

역할은 다음과 같다.

```jsx
value={email}
```

React State 값을 input에 표시한다.

```jsx
onChange={(e) => setEmail(e.target.value)}
```

input에서 바뀐 값을 State에 전달한다.

전체 흐름:

```text
사용자 입력
→ onChange 발생
→ e.target.value로 현재 input 값 확인
→ setEmail로 상태 변경 요청
→ 재렌더링
→ value={email}에 최신 값 반영
```

즉 다음과 같이 양방향으로 연결된다.

```text
State → input 화면
input 입력 → State
```

---

## 입력할 때마다 렌더링되는 이유

사용자가 `abc`를 입력하면:

```text
"a" 입력
→ setEmail("a")
→ 재렌더링

"b" 입력
→ setEmail("ab")
→ 재렌더링

"c" 입력
→ setEmail("abc")
→ 재렌더링
```

매번 `e.target.value`에는 한 글자만 들어오는 것이 아니라 현재 input의 전체 값이 들어온다.

```text
첫 번째 입력 → "a"
두 번째 입력 → "ab"
세 번째 입력 → "abc"
```

React 컴포넌트 함수는 다시 실행되지만 실제 브라우저 DOM 전체를 새로 만드는 것은 아니다.

React가 이전 JSX와 새로운 JSX를 비교하여 실제로 변경된 부분만 반영한다.

---

# 14. React Bootstrap

설치:

```bash
npm install react-bootstrap bootstrap
```

각 라이브러리의 역할:

```text
react-bootstrap
→ Button, Modal 등의 React 컴포넌트 제공

bootstrap
→ Bootstrap CSS 제공
```

사용 예시:

```jsx
import Button from 'react-bootstrap/Button';
import 'bootstrap/dist/css/bootstrap.min.css';
```

```jsx
<Button variant="primary">
    로그인
</Button>
```

---

# 15. disabled 속성

`disabled`는 버튼을 비활성화할지 결정하는 boolean Prop이다.

```jsx
<Button
    title="입장"
    disabled={full}
/>
```

```text
full === true
→ 버튼 비활성화

full === false
→ 버튼 활성화
```

예시:

```jsx
const capacity = 10;

const [count, setCount] = useState(0);
const [full, setFull] = useState(false);
const [empty, setEmpty] = useState(true);
```

```jsx
useEffect(() => {
    setFull(count >= capacity);
    setEmpty(count <= 0);
}, [count]);
```

```jsx
<Button
    title="입장"
    onClick={() => setCount(cnt => cnt + 1)}
    disabled={full}
/>

<Button
    title="퇴장"
    onClick={() => setCount(cnt => cnt - 1)}
    disabled={empty}
/>
```

`count`가 9에서 10이 될 때의 흐름:

```text
입장 버튼 클릭
→ setCount(10)
→ count 변경으로 재렌더링
→ useEffect 실행
→ setFull(10 >= 10)
→ setFull(true)
→ full 변경으로 다시 렌더링
→ disabled={true}
→ 입장 버튼 비활성화
```

`full`과 `empty`는 `count`로 계산할 수 있으므로 별도 State 없이 작성할 수도 있다.

```jsx
<Button
    title="입장"
    onClick={() => setCount(cnt => cnt + 1)}
    disabled={count >= capacity}
/>

<Button
    title="퇴장"
    onClick={() => setCount(cnt => cnt - 1)}
    disabled={count <= 0}
/>
```

이 방식은 추가 State와 `useEffect`가 필요하지 않다.

---

# 16. Axios와 API 요청

```jsx
const response = await api.get(
    `/users?email=${email}&pswd=${pswd}`
);
```

이 코드는 서버에 GET 요청을 보내는 코드다.

예를 들어:

```jsx
email = 'test@test.com';
pswd = '1234';
```

라면 실제 요청 경로는 다음과 같다.

```text
/users?email=test@test.com&pswd=1234
```

구조:

```text
/users
→ 서버 자원 경로

?
→ 쿼리스트링 시작

email=test@test.com
→ 첫 번째 파라미터

&
→ 여러 파라미터 구분

pswd=1234
→ 두 번째 파라미터
```

---

## Axios 응답 객체

```jsx
const response = await api.get('/users');
```

응답 객체는 대략 다음 정보를 가진다.

```jsx
{
    data: 서버에서 전달한 실제 데이터,
    status: HTTP 상태 코드,
    headers: 응답 헤더,
    config: 요청 설정
}
```

실제 데이터는 `response.data`로 꺼낸다.

```jsx
const users = response.data;
```

로그인 여부 판단에만 잠깐 사용할 데이터라면 일반 변수로 받아도 된다.

```jsx
const users = response.data;

if (users.length === 1) {
    const user = users[0];
}
```

화면에 계속 표시해야 하는 데이터라면 State를 사용한다.

```jsx
const [users, setUsers] = useState([]);

setUsers(response.data);
```

---

## 검색 결과가 배열인 이유

```jsx
GET /users?email=test@test.com
```

`/users` 목록을 조건으로 검색하는 API는 결과를 배열로 반환할 수 있다.

사용자가 없는 경우:

```jsx
[]
```

한 명인 경우:

```jsx
[
    {
        id: 1,
        email: 'test@test.com',
        name: '홍길동'
    }
]
```

따라서:

```jsx
const user = users[0];
```

은 배열의 첫 번째 사용자 객체를 꺼내는 코드다.

중복 데이터가 있다면 첫 번째 값만 사용하게 되므로 안전하게 검사하는 것이 좋다.

```jsx
if (users.length === 1) {
    const user = users[0];
} else if (users.length === 0) {
    console.log('사용자 없음');
} else {
    console.log('중복 사용자 존재');
}
```

실제 로그인 API는 보통 사용자 객체 하나 또는 토큰을 반환하도록 별도로 설계한다.

---

# 17. HTTP의 특징

HTTP는 클라이언트와 서버가 요청과 응답을 주고받는 통신 규칙이다.

## Stateless

HTTP는 기본적으로 이전 요청 상태를 기억하지 않는다.

```text
첫 번째 요청
→ 서버 응답

두 번째 요청
→ 이전 요청을 자동으로 기억하지 않음
```

그래서 로그인 상태를 유지하기 위해 다음과 같은 방법을 사용한다.

```text
Cookie
Session
Token
JWT
```

## Connection 관리

HTTP는 요청과 응답 단위로 통신하지만, 현대 HTTP에서는 성능을 위해 하나의 연결을 재사용할 수 있다.

따라서 단순히 항상 연결이 즉시 끊어진다고만 이해하기보다는, 애플리케이션 상태를 기억하지 않는 **Stateless** 특성이 핵심이다.

---

# 18. 인증과 인가

## 인증(Authentication)

사용자가 누구인지 확인하는 과정이다.

```text
이메일과 비밀번호 확인
→ 사용자의 신원 확인
```

## 인가(Authorization)

인증된 사용자가 특정 기능이나 데이터에 접근할 권한이 있는지 확인하는 과정이다.

```text
일반 사용자
→ 일반 페이지 접근

관리자
→ 관리자 페이지 접근
```

```text
인증
→ 누구인가?

인가
→ 무엇을 할 수 있는가?
```

---

# 19. Session, Cookie, Token

## Session

서버가 로그인 상태를 저장하고, 클라이언트는 세션 식별자를 Cookie로 전달하는 방식이다.

```text
로그인 성공
→ 서버가 Session 생성
→ 브라우저에 Session ID 전달
→ 이후 요청마다 Session ID 전송
```

서버가 여러 대로 늘어나면 Session 공유가 필요할 수 있다.

이를 위해 Redis 같은 공용 저장소를 사용할 수 있다.

---

## Token

사용자가 인증되면 서버가 토큰을 발급하고, 클라이언트는 이후 요청에 토큰을 포함해 보낸다.

```text
로그인 성공
→ 서버가 Token 발급
→ 프론트에서 Token 보관
→ API 요청 시 Header에 Token 전달
→ 서버가 Token 검증
```

예시:

```http
Authorization: Bearer eyJ...
```

토큰은 인증 결과를 증명하는 문자열이다.

JWT의 경우 사용자 식별 정보와 권한 등의 Claim이 포함될 수 있지만, 일반적으로 암호화된 비밀 데이터 저장소로 사용하면 안 된다.

---

## Access Token과 Refresh Token

### Access Token

API 요청 시 사용하는 토큰이다.

```text
유효기간이 비교적 짧음
API 요청 Header에 포함
```

### Refresh Token

Access Token이 만료됐을 때 새로운 Access Token을 발급받기 위해 사용한다.

```text
Access Token보다 유효기간이 긴 경우가 많음
서버 또는 안전한 저장소에서 별도 관리
```

정확한 만료 시간은 서비스 정책에 따라 다르며 고정된 값은 아니다.

---

# 20. HTTP 상태 코드

HTTP 상태 코드는 응답 결과를 숫자로 표현한다.

```text
100번대
→ 요청 처리 중 정보 응답

200번대
→ 요청 성공

300번대
→ 리다이렉션

400번대
→ 클라이언트 요청 오류

500번대
→ 서버 내부 오류
```

대표적인 상태 코드:

```text
200 OK
→ 요청 성공

201 Created
→ 자원 생성 성공

400 Bad Request
→ 잘못된 요청

401 Unauthorized
→ 인증 필요 또는 인증 실패

403 Forbidden
→ 권한 없음

404 Not Found
→ 요청 자원 없음

500 Internal Server Error
→ 서버 내부 오류
```

---

# 21. localStorage

`localStorage`는 React 기능이 아니라 브라우저가 제공하는 Web Storage 객체다.

데이터 저장:

```jsx
localStorage.setItem('userName', user.name);
```

```text
키: userName
값: user.name
```

데이터 조회:

```jsx
const name = localStorage.getItem('userName');
```

저장할 때 사용한 키와 조회할 때 사용하는 키가 같아야 한다.

```jsx
localStorage.setItem('userName', '홍길동');

const name = localStorage.getItem('userName');
```

```text
name = "홍길동"
```

특정 데이터 삭제:

```jsx
localStorage.removeItem('userName');
```

전체 데이터 삭제:

```jsx
localStorage.clear();
```

---

## 객체 저장

`localStorage`는 문자열 형태로 데이터를 저장한다.

객체 전체를 저장하려면 `JSON.stringify()`를 사용한다.

```jsx
localStorage.setItem(
    'user',
    JSON.stringify(user)
);
```

꺼낼 때는 `JSON.parse()`를 사용한다.

```jsx
const savedUser = JSON.parse(
    localStorage.getItem('user')
);
```

---

# 22. React Router 설치

```bash
npm install react-router-dom
```

React Router는 SPA에서 URL에 따라 렌더링할 컴포넌트를 결정한다.

페이지 전체를 새로고침하지 않고 주소를 변경하고 컴포넌트를 교체한다.

---

# 23. BrowserRouter, Routes, Route

```jsx
import {
    BrowserRouter,
    Routes,
    Route
} from 'react-router-dom';
```

라우터 설정:

```jsx
const TestRouterApp = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route
                    path="/"
                    element={<EventPage />}
                />

                <Route
                    path="/success"
                    element={<SuccessPage />}
                />

                <Route
                    path="/error"
                    element={<ErrorPage />}
                />

                <Route
                    path="/read/:id"
                    element={<ViewPage />}
                />
            </Routes>
        </BrowserRouter>
    );
};
```

---

## BrowserRouter

```jsx
<BrowserRouter>
```

브라우저의 현재 URL과 History를 관리한다.

```text
URL 변경 감지
→ 현재 경로 확인
→ 해당 컴포넌트 렌더링
```

---

## Routes

```jsx
<Routes>
```

여러 `Route` 중 현재 URL과 일치하는 경로를 찾는다.

개념적으로 `switch`문과 비슷하게 이해할 수 있다.

---

## Route

```jsx
<Route
    path="/success"
    element={<SuccessPage />}
/>
```

* `path`: URL 경로
* `element`: 해당 경로일 때 렌더링할 React 요소

```text
현재 URL이 /success
→ SuccessPage 렌더링
```

---

# 24. useNavigate

```jsx
import { useNavigate } from 'react-router-dom';
```

```jsx
const navigate = useNavigate();
```

`useNavigate()`는 페이지 이동에 사용할 함수를 반환한다.

```jsx
navigate('/success');
```

흐름:

```text
navigate 실행
→ URL 변경
→ BrowserRouter가 변경 감지
→ Routes가 경로 검색
→ 해당 컴포넌트 렌더링
```

`navigate`나 `moveUrl`은 개발자가 정한 변수명이다.

```jsx
const moveUrl = useNavigate();
```

---

# 25. navigate의 state 전달

```jsx
moveUrl('/success', {
    state: {
        user,
        from: '/signIn'
    }
});
```

`moveUrl()`에 두 개의 인자를 전달한 것이다.

```jsx
moveUrl(
    이동할 경로,
    이동 옵션 객체
);
```

두 번째 인자:

```jsx
{
    state: {
        user,
        from: '/signIn'
    }
}
```

는 중첩 객체다.

구조:

```text
옵션 객체
└── state
    ├── user
    └── from
```

여기서 `state`는 `useState()`의 상태 변수가 아니다.

React Router가 이동할 때 데이터를 전달하기 위해 사용하는 정해진 옵션 프로퍼티다.

`from`은 React Router가 정한 값이 아니라 개발자가 임의로 만든 프로퍼티다.

```jsx
from: '/signIn'
```

보통 어느 페이지에서 이동했는지 기록하기 위해 사용한다.

---

## 객체 프로퍼티 축약

```jsx
state: {
    user
}
```

는 다음 코드와 같다.

```jsx
state: {
    user: user
}
```

---

# 26. useLocation

```jsx
import { useLocation } from 'react-router-dom';
```

```jsx
const location = useLocation();
```

현재 URL 정보와 `navigate()`를 통해 전달받은 `state`를 확인한다.

`location` 객체의 기본 구조:

```jsx
{
    pathname: '/success',
    search: '',
    hash: '',
    state: {
        user: {
            id: 1,
            name: '홍길동'
        },
        from: '/signIn'
    },
    key: '...'
}
```

각 속성:

```text
pathname
→ 현재 경로

search
→ URL의 쿼리스트링

hash
→ URL의 # 뒤 값

state
→ navigate 또는 Link로 전달받은 데이터

key
→ 현재 History 위치를 구분하는 값
```

---

## location.state 구조 분해

```jsx
const { user, from } = location.state || {};
```

`location.state`에서 `user`와 `from`을 꺼내는 구조 분해 할당이다.

풀어 쓰면:

```jsx
const state = location.state || {};

const user = state.user;
const from = state.from;
```

`location.state`가 없다면 빈 객체 `{}`를 사용하여 오류를 방지한다.

```text
state가 있으면
→ 전달받은 객체 사용

state가 없으면
→ 빈 객체 사용

빈 객체에서 user와 from을 꺼내면
→ undefined
```

`??`를 사용할 수도 있다.

```jsx
const { user, from } = location.state ?? {};
```

---

# 27. Link

```jsx
import { Link } from 'react-router-dom';
```

`Link`는 사용자가 클릭해서 다른 경로로 이동할 수 있도록 하는 컴포넌트다.

```jsx
<Link to="/">
    랜딩페이지로
</Link>
```

클릭 시 `/`로 이동한다.

```jsx
<Link to="/read/10">
    상세페이지로
</Link>
```

클릭 시 `/read/10`으로 이동한다.

`Link`는 HTML의 `<a>`와 비슷하지만, 페이지 전체를 새로고침하지 않고 React Router를 통해 화면을 전환한다.

---

# 28. Query String과 useSearchParams

쿼리스트링은 URL의 `?` 뒤에 데이터를 붙여 전달하는 방식이다.

```jsx
moveUrl('/error?category=react&sort=latest');
```

구조:

```text
/error
→ 경로

?
→ 쿼리스트링 시작

category=react
→ 첫 번째 값

&
→ 값 구분

sort=latest
→ 두 번째 값
```

값을 받으려면 `useSearchParams()`를 사용한다.

```jsx
import { useSearchParams } from 'react-router-dom';
```

```jsx
const [searchParams] = useSearchParams();

const category = searchParams.get('category');
const sort = searchParams.get('sort');
```

결과:

```text
category = "react"
sort = "latest"
```

`useSearchParams()`는 배열 형태의 값을 반환한다.

```jsx
[
    searchParams,
    setSearchParams
]
```

현재는 값을 읽기만 하므로 첫 번째 요소만 꺼낸 것이다.

```jsx
const [searchParams] = useSearchParams();
```

`searchParams` 자체는 배열이 아니라 `URLSearchParams` 객체다.

---

# 29. Path Variable과 useParams

Path Variable은 URL 경로 일부를 변수처럼 사용하는 방식이다.

라우터 설정:

```jsx
<Route
    path="/read/:id"
    element={<ViewPage />}
/>
```

여기서 `:id`는 동적으로 바뀌는 경로 변수다.

```jsx
<Link to="/read/10">
    상세페이지로
</Link>
```

클릭하면 URL은 다음과 같다.

```text
/read/10
```

React Router는 다음처럼 해석한다.

```text
/read/:id
/read/10

id = "10"
```

값을 받으려면 `useParams()`를 사용한다.

```jsx
import { useParams } from 'react-router-dom';
```

```jsx
const ViewPage = () => {
    const { id } = useParams();

    return (
        <div>
            상세페이지 ID: {id}
        </div>
    );
};
```

`useParams()`가 반환하는 객체:

```jsx
{
    id: '10'
}
```

URL에서 받은 값은 기본적으로 문자열이다.

```jsx
typeof id; // "string"
```

숫자로 사용할 경우 변환한다.

```jsx
const postId = Number(id);
```

---

# 30. React Router 데이터 전달 방식

## Path Variable

```text
/read/10
```

```jsx
const { id } = useParams();
```

특정 대상을 식별할 때 사용한다.

```text
게시글 ID
사용자 ID
상품 ID
```

---

## Query String

```text
/error?category=react&sort=latest
```

```jsx
const [searchParams] = useSearchParams();
```

검색, 필터, 정렬 등의 부가 조건에 사용한다.

---

## Router State

```jsx
navigate('/success', {
    state: {
        user,
        from: '/'
    }
});
```

```jsx
const location = useLocation();
```

객체 등 복잡한 값을 URL에 노출하지 않고 전달할 때 사용할 수 있다.

단, 페이지를 직접 새로고침하거나 URL을 직접 입력하면 전달된 State가 없을 수 있다.

민감한 데이터는 Router State에 넣었다는 이유만으로 안전하다고 볼 수 없으며, 비밀번호나 토큰을 페이지 이동 State로 전달하는 방식은 피해야 한다.

---

# 31. 로그인 페이지 전체 흐름

```jsx
const [email, setEmail] = useState('');
const [pswd, setPswd] = useState('');

const navigate = useNavigate();
```

입력값 관리:

```jsx
<input
    value={email}
    onChange={(e) => setEmail(e.target.value)}
/>

<input
    value={pswd}
    onChange={(e) => setPswd(e.target.value)}
/>
```

로그인 요청:

```jsx
const signInHandler = async (e) => {
    e.preventDefault();

    try {
        const response = await api.get(
            `/users?email=${email}&pswd=${pswd}`
        );

        const users = response.data;

        if (users.length === 1) {
            const user = users[0];

            localStorage.setItem(
                'userName',
                user.name
            );

            navigate('/success', {
                state: {
                    user,
                    from: '/'
                }
            });
        } else {
            navigate(
                '/error?category=react&sort=latest'
            );
        }
    } catch (error) {
        console.log(error);
    }
};
```

전체 흐름:

```text
사용자 입력
→ email, pswd State 변경
→ 로그인 버튼 클릭
→ signInHandler 실행
→ 서버에 사용자 조회
→ response.data 확인
→ 사용자가 있으면 첫 번째 객체 추출
→ localStorage에 이름 저장
→ state와 함께 /success 이동
→ 사용자가 없으면 Query String과 함께 /error 이동
```

---

# 32. Conditional Rendering 전체 흐름

부모 컴포넌트:

```jsx
const RenderingPage = () => {
    const [flag, setFlag] = useState(true);

    return (
        <div>
            <Greeting flag={flag} />

            {
                flag
                    ? (
                        <LogoutButton
                            isLogin={setFlag}
                        />
                    )
                    : null
            }
        </div>
    );
};
```

`Greeting`:

```jsx
const Greeting = (props) => {
    return props.flag
        ? <UserGreeting />
        : <GuestGreeting />;
};
```

`UserGreeting`:

```jsx
const UserGreeting = () => {
    return (
        <div>
            <h1>환영합니다. OOO님</h1>
        </div>
    );
};
```

`LogoutButton`:

```jsx
const LogoutButton = (props) => {
    const logoutHandler = (setFlag) => {
        setFlag(false);
    };

    return (
        <Button
            title="로그아웃"
            onClick={() =>
                logoutHandler(props.isLogin)
            }
        />
    );
};
```

전체 흐름:

```text
최초 렌더링
→ flag = true

Greeting에 flag 전달
→ props.flag = true
→ UserGreeting 반환

삼항연산자 확인
→ flag가 true
→ LogoutButton 렌더링

부모의 setFlag 함수
→ isLogin이라는 Props로 자식에게 전달

로그아웃 버튼 클릭
→ logoutHandler(props.isLogin)
→ props.isLogin은 부모의 setFlag 함수
→ setFlag(false)
→ 부모 flag 변경 요청
→ 부모 컴포넌트 재렌더링

다음 렌더링
→ flag = false
→ Greeting은 GuestGreeting 반환
→ LogoutButton은 렌더링되지 않음
```

더 명확하게 작성하면 다음과 같다.

```jsx
const RenderingPage = () => {
    const [flag, setFlag] = useState(true);

    return (
        <div>
            <Greeting flag={flag} />

            {flag && (
                <LogoutButton
                    onLogout={() => setFlag(false)}
                />
            )}
        </div>
    );
};
```

```jsx
const LogoutButton = ({ onLogout }) => {
    return (
        <Button
            title="로그아웃"
            onClick={onLogout}
        />
    );
};
```

---

# 핵심 개념 최종 정리

```text
useState
→ 컴포넌트의 상태 관리
→ setState 호출 시 재렌더링 요청

setState
→ 현재 변수를 즉시 변경하지 않음
→ 현재 이벤트 작업 이후 새 상태로 렌더링

useEffect
→ 렌더링 이후 Side Effect 실행
→ 의존성 배열로 실행 시점 결정

Props
→ 부모가 자식에게 전달하는 객체
→ 값뿐 아니라 함수도 전달 가능

Props Drilling
→ 여러 중간 컴포넌트를 거쳐 Props를 전달하는 현상

Controlled Component
→ input 값을 React State로 관리

e.target.value
→ 이벤트가 발생한 input의 현재 값

preventDefault
→ 브라우저의 기본 동작 차단

stopPropagation
→ 이벤트 버블링 차단

Conditional Rendering
→ 조건에 따라 렌더링할 컴포넌트 결정

Truthy / Falsy
→ boolean이 아니어도 조건식에서 참 또는 거짓처럼 평가

React Router
→ URL에 따라 컴포넌트 전환

useNavigate
→ 코드로 페이지 이동

useLocation
→ 현재 위치와 Router State 확인

useSearchParams
→ Query String 조회 및 변경

useParams
→ Path Variable 조회

localStorage
→ 브라우저 내부에 문자열 데이터 저장

인증
→ 사용자가 누구인지 확인

인가
→ 사용자가 어떤 기능에 접근 가능한지 확인
```

## 오늘 한 줄 정리

React는 State 변경을 기준으로 화면을 다시 계산하며, Props와 Router를 통해 컴포넌트 사이의 값과 페이지 이동 흐름을 연결한다.
