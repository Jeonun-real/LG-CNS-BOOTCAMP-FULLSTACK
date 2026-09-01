# FE 5일차 핵심 개념 정리

## 오늘의 한 줄 요약

스켈레톤 UI와 비동기 통신 흐름을 이해하고, ES6 문법과 React 컴포넌트·모듈 구조를 학습했다.

---

# 1. Skeleton UI

## 전체 동작 흐름

```text
페이지 실행
→ 스켈레톤 카드 출력
→ Axios 요청과 일정 시간 대기 동시 실행
→ 두 작업 모두 완료
→ 실제 데이터로 카드 생성
→ 스켈레톤 UI를 실제 카드로 교체
```

스켈레톤 UI는 서버에서 데이터를 받아오는 동안 사용자에게 빈 화면 대신 로딩 중인 화면을 보여주는 방식이다.

---

## HTML 구조

```html
<link href="../css/skeleton.css" rel="stylesheet">

<main
    id="card-container"
    class="card-container">
</main>

<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
<script src="../script/skeleton.js"></script>
```

### `rel="stylesheet"`

```html
<link href="../css/skeleton.css" rel="stylesheet">
```

* `link`: 외부 파일을 HTML과 연결
* `href`: 연결할 파일의 경로
* `rel`: 현재 HTML과 연결 파일의 관계
* `stylesheet`: 연결한 파일이 CSS 스타일시트라는 뜻

즉 다음 의미다.

```text
skeleton.css 파일을 현재 HTML의 스타일로 적용한다.
```

---

## 카드가 들어갈 공간

```html
<main id="card-container" class="card-container"></main>
```

현재는 비어 있는 HTML 요소다.

JavaScript에서 이 요소를 찾아 스켈레톤 카드 또는 실제 카드를 삽입한다.

```javascript
const container =
    document.querySelector('#card-container');
```

---

## Axios CDN

```html
<script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
```

Axios 라이브러리를 CDN 방식으로 불러온다.

Axios를 사용하는 JavaScript 파일보다 먼저 작성해야 한다.

```text
Axios 로드
→ skeleton.js 실행
→ axios.get() 사용
```

---

# 2. 데이터 로드 함수

```javascript
const loadData = async () => {
    showSkeletonUI();

    const [response] = await Promise.all([
        axios.get('../server/data.json'),
        delay(1500)
    ]);

    renderCards(response.data);
};
```

## 실행 흐름

```text
loadData() 실행
→ showSkeletonUI() 실행
→ 스켈레톤 UI 출력
→ Axios 요청과 delay 동시 실행
→ 두 작업 모두 완료
→ response.data를 renderCards()에 전달
→ 실제 카드 출력
```

---

# 3. Promise

Promise는 나중에 완료될 비동기 작업의 상태와 결과를 관리하는 객체다.

개념적으로는 다음과 비슷하게 생각할 수 있다.

```javascript
{
    state: 'pending',
    result: 아직 없음
}
```

실제 Promise의 내부 상태를 일반 객체처럼 직접 접근할 수 있는 것은 아니다.

## Promise의 상태

```text
pending
→ 작업 진행 중

fulfilled
→ 작업 성공

rejected
→ 작업 실패
```

---

## Axios와 Promise

```javascript
axios.get('../server/data.json');
```

`axios.get()`은 데이터를 즉시 반환하지 않고 Promise 객체를 반환한다.

요청 직후:

```text
상태: pending
결과: 아직 없음
```

요청 성공:

```text
상태: fulfilled
결과: Axios 응답 객체
```

Axios 응답 객체는 대략 다음과 같다.

```javascript
{
    data: [...],
    status: 200,
    statusText: 'OK',
    headers: {},
    config: {}
}
```

요청 실패:

```text
상태: rejected
결과: 에러 객체
```

---

# 4. delay 함수

```javascript
const delay = (ms) => {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
};
```

`delay()`는 지정한 시간이 지나면 완료되는 Promise를 반환한다.

```javascript
delay(1500);
```

`ms`에는 `1500`이 들어간다.

```javascript
setTimeout(resolve, 1500);
```

의미는 다음과 같다.

```text
1.5초 뒤에 resolve 함수를 실행한다.
```

풀어서 작성하면 다음 코드와 같다.

```javascript
const delay = (ms) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve();
        }, ms);
    });
};
```

`resolve()`가 실행되면 Promise 상태가 변경된다.

```text
pending → fulfilled
```

현재 `resolve()`에 값을 전달하지 않았기 때문에 완료 결과는 `undefined`다.

```javascript
const result = await delay(1500);

console.log(result);
// undefined
```

---

# 5. Promise.all

## 기본 문법

```javascript
Promise.all([
    promise1,
    promise2,
    promise3
]);
```

`Promise.all()`은 Promise 여러 개를 배열로 전달받는다.

모든 Promise가 성공할 때까지 기다린 후, 각 Promise의 결과를 배열로 반환한다.

```javascript
const results = await Promise.all([
    promise1,
    promise2,
    promise3
]);
```

결과:

```javascript
[
    promise1의 결과,
    promise2의 결과,
    promise3의 결과
]
```

---

## 현재 코드의 Promise.all

```javascript
const [response] = await Promise.all([
    axios.get('../server/data.json'),
    delay(1500)
]);
```

처음에는 다음 Promise들이 들어간다.

```text
[
    Axios의 pending Promise,
    delay의 pending Promise
]
```

Axios 요청이 성공하면:

```text
상태: fulfilled
결과: Axios 응답 객체
```

1.5초가 지나면:

```text
상태: fulfilled
결과: undefined
```

두 작업이 모두 완료된 후의 결과는 다음과 같다.

```javascript
[
    Axios 응답 객체,
    undefined
]
```

---

## await의 역할

```javascript
await Promise.all(...)
```

`await`는 Promise가 완료될 때까지 기다린다.

완료 후에는 Promise 객체 자체가 아니라 Promise의 결과값을 꺼낸다.

```text
Promise.all 실행 중
→ Promise 객체

await 이후
→ [Axios 응답 객체, undefined]
```

---

## 배열 구조 분해 할당

```javascript
const [response] = [
    Axios 응답 객체,
    undefined
];
```

`const [response]`는 배열 구조 분해 할당이다.

배열의 첫 번째 값을 꺼내 `response` 변수에 저장한다.

```text
response = Axios 응답 객체
```

다음 코드와 같은 의미다.

```javascript
const results = await Promise.all([
    axios.get('../server/data.json'),
    delay(1500)
]);

const response = results[0];
```

두 결과를 모두 받으려면 다음과 같이 작성한다.

```javascript
const [response, delayResult] =
    await Promise.all([
        axios.get('../server/data.json'),
        delay(1500)
    ]);
```

```text
response
→ Axios 응답 객체

delayResult
→ undefined
```

`const [response]`와 빈 배열 생성은 다른 문법이다.

```javascript
const [response] = results;
// 배열 첫 번째 값을 꺼냄
```

```javascript
const response = [];
// 빈 배열 생성
```

---

# 6. Axios 응답 데이터 사용

```javascript
renderCards(response.data);
```

`response`는 Axios 응답 객체다.

실제 JSON 데이터는 `data` 속성에 들어 있다.

```text
response
└─ data
   └─ 상품 객체 배열
```

예시:

```javascript
[
    {
        image: './img/laptop.jpg',
        title: '노트북',
        tag: '신상품',
        description: '고성능 노트북',
        price: 1200000
    },
    {
        image: './img/keyboard.jpg',
        title: '키보드',
        tag: '인기상품',
        description: '기계식 키보드',
        price: 89000
    }
]
```

이 배열이 `renderCards()`의 `items` 매개변수로 전달된다.

---

# 7. 스켈레톤 카드 생성

```javascript
const showSkeletonUI = () => {
    const skeletons = Array
        .from(
            { length: 6 },
            renderSkeletonCard
        )
        .join('');

    container.innerHTML = skeletons;
};
```

## `Array.from()`

```javascript
Array.from(
    { length: 6 },
    renderSkeletonCard
);
```

길이가 6인 배열을 만들고, 각 자리마다 `renderSkeletonCard()`를 실행한다.

개념적으로 다음과 같다.

```javascript
[
    renderSkeletonCard(),
    renderSkeletonCard(),
    renderSkeletonCard(),
    renderSkeletonCard(),
    renderSkeletonCard(),
    renderSkeletonCard()
]
```

함수 뒤에 괄호를 붙이지 않는 이유는 함수 실행 결과가 아니라 함수 자체를 전달하기 때문이다.

```javascript
renderSkeletonCard
// 함수 자체 전달
```

```javascript
renderSkeletonCard()
// 함수 즉시 실행
```

---

## 스켈레톤 카드 함수

```javascript
const renderSkeletonCard = () => {
    return `
        <div class="skeleton-card">
            <div class="skeleton-img"></div>

            <div class="skeleton-body">
                <div class="skeleton-line short"></div>
                <div class="skeleton-line title"></div>
                <div class="skeleton-line text"></div>
                <div class="skeleton-line text"></div>
            </div>
        </div>
    `;
};
```

이 함수는 DOM 객체를 직접 생성하는 것이 아니라 HTML 문자열을 반환한다.

6번 실행되면 다음과 같은 배열이 만들어진다.

```javascript
[
    '<div class="skeleton-card">...</div>',
    '<div class="skeleton-card">...</div>',
    '<div class="skeleton-card">...</div>',
    '<div class="skeleton-card">...</div>',
    '<div class="skeleton-card">...</div>',
    '<div class="skeleton-card">...</div>'
]
```

---

## `join('')`

```javascript
.join('');
```

문자열 배열을 하나의 문자열로 합친다.

```javascript
[
    '카드1',
    '카드2',
    '카드3'
].join('');
```

결과:

```javascript
'카드1카드2카드3'
```

최종적으로 합쳐진 HTML 문자열을 `innerHTML`에 삽입한다.

```javascript
container.innerHTML = skeletons;
```

```text
HTML 문자열
→ 브라우저가 HTML 태그로 해석
→ 스켈레톤 카드 출력
```

---

# 8. 실제 카드 렌더링

```javascript
const renderCards = (items) => {
    container.innerHTML = items
        .map(renderCard)
        .join('');
};
```

`items`는 상품 객체가 들어 있는 배열이다.

```javascript
[
    { 상품 객체 1 },
    { 상품 객체 2 },
    { 상품 객체 3 }
]
```

---

## `map()`

```javascript
items.map(renderCard);
```

배열의 각 객체를 하나씩 `renderCard()`에 전달한다.

개념적으로 다음과 같다.

```javascript
[
    renderCard(items[0]),
    renderCard(items[1]),
    renderCard(items[2])
]
```

아래 코드와 같은 의미다.

```javascript
items.map((item) => {
    return renderCard(item);
});
```

```text
items
→ 객체 여러 개가 들어 있는 배열

item
→ 배열에서 하나씩 꺼낸 상품 객체
```

### `map()`과 `forEach()` 차이

```text
forEach()
→ 배열을 순회
→ 새로운 배열을 반환하지 않음

map()
→ 배열을 순회
→ 함수 반환값으로 새 배열 생성
```

---

# 9. 상품 카드 하나 생성

```javascript
const renderCard = (item) => {
    return `
        <div class="card">
            <img
                class="card-img"
                src="${item.image}"
                alt="${item.title}"
            >

            <div class="card-body">
                ${
                    item.tag
                        ? `<span class="card-tag">${item.tag}</span>`
                        : ''
                }

                <h3 class="card-title">
                    ${item.title}
                </h3>

                <p class="card-desc">
                    ${item.description}
                </p>

                <div class="card-price">
                    ${item.price.toLocaleString()}원
                </div>
            </div>
        </div>
    `;
};
```

상품 객체 하나를 받아 카드 HTML 문자열 하나로 바꾼다.

입력 객체:

```javascript
{
    image: './img/laptop.jpg',
    title: '노트북',
    tag: '신상품',
    description: '고성능 노트북',
    price: 1200000
}
```

반환 HTML:

```html
<div class="card">
    <img
        class="card-img"
        src="./img/laptop.jpg"
        alt="노트북"
    >

    <div class="card-body">
        <span class="card-tag">
            신상품
        </span>

        <h3 class="card-title">
            노트북
        </h3>

        <p class="card-desc">
            고성능 노트북
        </p>

        <div class="card-price">
            1,200,000원
        </div>
    </div>
</div>
```

---

## 템플릿 리터럴

```javascript
${item.image}
${item.title}
${item.description}
```

백틱 문자열 안에서 `${}`를 사용하면 JavaScript 값을 문자열에 넣을 수 있다.

---

## 삼항 연산자

```javascript
item.tag
    ? `<span class="card-tag">${item.tag}</span>`
    : ''
```

```text
item.tag가 있으면
→ span 태그 반환

item.tag가 없으면
→ 빈 문자열 반환
```

---

## `toLocaleString()`

```javascript
item.price.toLocaleString();
```

숫자를 지역 형식의 문자열로 바꾼다.

```text
1200000
→ 1,200,000
```

---

# 10. CSS 애니메이션

## `@keyframes`

`@keyframes`는 CSS 애니메이션이 시작부터 끝까지 어떻게 변할지를 정의한다.

```css
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(6px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
```

```text
처음
→ 투명도 0
→ 아래쪽으로 6px 이동

끝
→ 투명도 1
→ 원래 위치
```

해당 애니메이션은 다음처럼 적용한다.

```css
.card {
    animation: fadeIn 0.4s ease;
}
```

---

## 스켈레톤 Shimmer 효과

```css
.skeleton-img,
.skeleton-line {
    background: linear-gradient(
        90deg,
        #e8e8e8 25%,
        #f2f2f2 37%,
        #e8e8e8 63%
    );

    background-size: 400% 100%;
    animation: shimmer 1.4s ease infinite;
}
```

밝은 회색 부분이 이동하면서 로딩 중인 것처럼 보인다.

```css
@keyframes shimmer {
    0% {
        background-position: 100% 0;
    }

    100% {
        background-position: -100% 0;
    }
}
```

```text
shimmer
→ 애니메이션 이름

1.4s
→ 실행 시간

ease
→ 속도 변화 방식

infinite
→ 무한 반복
```

---

# 11. 클래스 두 개 사용

```html
<div class="skeleton-line short"></div>
```

이 요소에는 두 개의 클래스가 있다.

```text
skeleton-line
→ 공통 스켈레톤 막대 스타일

short
→ 짧은 막대라는 추가 스타일
```

```css
.skeleton-line {
    height: 14px;
    border-radius: 4px;
}

.skeleton-line.short {
    width: 30%;
}
```

`.skeleton-line.short`는 두 클래스를 동시에 가진 요소를 선택한다.

```text
공통 스타일
+
개별 변형 스타일
```

---

# 12. Grid 반응형 배치

```css
.card-container {
    display: grid;

    grid-template-columns: repeat(
        auto-fit,
        minmax(260px, 1fr)
    );
}
```

## `minmax(260px, 1fr)`

```text
최소 크기
→ 260px

최대 크기
→ 남은 공간만큼
```

## `auto-fit`

현재 화면에 들어갈 수 있는 만큼 열을 자동으로 배치한다.

```text
넓은 화면
→ 한 줄에 여러 카드

좁은 화면
→ 카드가 다음 줄로 이동
```

---

# 13. JavaScript 모듈

JavaScript 모듈은 코드를 여러 파일로 나누고 필요한 함수나 변수를 가져다 사용하는 방식이다.

```text
a.js에서 export
→ b.js에서 import
```

---

## Default export

```javascript
const getTotal = () => {
    return 100;
};

export default getTotal;
```

가져올 때:

```javascript
import calculate from './a.js';
```

특징:

```text
파일당 하나만 가능
중괄호 사용하지 않음
가져오는 이름 변경 가능
```

---

## Named export

```javascript
export const a = () => {
    return 'A';
};

export const b = () => {
    return 'B';
};
```

또는:

```javascript
const a = () => {
    return 'A';
};

const b = () => {
    return 'B';
};

export { a, b };
```

가져올 때:

```javascript
import { a, b } from './a.js';
```

이름을 변경하려면 `as`를 사용한다.

```javascript
import {
    a as functionA,
    b as functionB
} from './a.js';
```

---

## `type="module"`

일반 HTML에서 `import`, `export`를 사용하려면 JavaScript 파일을 모듈로 지정해야 한다.

```html
<script
    type="module"
    src="./main.js">
</script>
```

주의:

```text
scr ❌
src ✅
```

---

# 14. ES6 주요 문법

## Spread 연산자

```javascript
const original = {
    a: 10,
    b: 20
};

const copy = {
    ...original
};
```

단순 대입은 같은 객체의 주소를 공유한다.

```javascript
const copy = original;
```

```text
original과 copy가 같은 객체를 참조
→ copy를 변경하면 original도 변경
```

Spread 연산자를 사용하면 새로운 객체로 복사할 수 있다.

```javascript
const copy = {
    ...original
};
```

배열은 다음과 같이 복사한다.

```javascript
const array = [1, 2, 3];

const newArray = [
    ...array
];
```

---

## 구조 분해 할당

객체:

```javascript
const {
    a,
    b
} = original;
```

배열:

```javascript
const [
    first,
    second
] = array;
```

---

## 옵셔널 체이닝

```javascript
const postcode =
    user.address?.postcode;
```

`address`가 없으면 오류 대신 `undefined`를 반환한다.

```text
user.address가 있으면
→ postcode 접근

user.address가 없으면
→ undefined
```

---

## 널 병합 연산자

```javascript
const city =
    user.address?.city ?? 'busan';
```

값이 `null` 또는 `undefined`이면 기본값을 사용한다.

---

## 객체 축약 문법

```javascript
const lang = 'JavaScript';

const subject = {
    lang
};
```

다음 코드의 축약형이다.

```javascript
const subject = {
    lang: lang
};
```

메서드도 축약할 수 있다.

```javascript
const subject = {
    lang,

    coding() {
        console.log('coding');
    }
};
```

---

# 15. React

React는 재사용 가능한 UI 컴포넌트를 만들기 위한 JavaScript 라이브러리다.

## 핵심 특징

```text
컴포넌트 기반
재사용 가능한 UI
상태 변화에 따른 화면 업데이트
Virtual DOM 사용
SPA 구조
```

---

## React 컴포넌트

React에서는 함수를 컴포넌트로 사용한다.

```jsx
const Book = (props) => {
    return (
        <div>
            <span>
                책이름: {props.bookName}
            </span>
        </div>
    );
};
```

```text
함수
→ Component

매개변수
→ props

반환값
→ React Element
```

---

## 페이지와 컴포넌트

```text
Page 컴포넌트
→ 여러 UI 컴포넌트를 조합

UI 컴포넌트
→ 화면의 작은 단위
```

```text
LibraryPage
└─ Book
```

---

# 16. React 시작점

React 앱은 일반적으로 `index.js`에서 시작한다.

```jsx
const root = ReactDOM.createRoot(
    document.getElementById('root')
);

root.render(
    <LibraryPage />
);
```

실행 흐름:

```text
index.html의 #root 찾기
→ React 루트 생성
→ LibraryPage 컴포넌트 실행
→ 반환된 JSX를 화면에 렌더링
```

---

## HTML 태그와 컴포넌트 구분

```jsx
<div></div>
```

실제 HTML 태그다.

```jsx
<LibraryPage />
```

직접 만든 React 컴포넌트다.

```text
소문자로 시작
→ HTML 태그

대문자로 시작
→ React 컴포넌트
```

---

# 17. LibraryPage 실행 흐름

```jsx
const LibraryPage = () => {
    const books = [
        {
            category: 'IT',
            bookName: 'java',
            price: 30000
        },
        {
            category: 'lang',
            bookName: 'javascript',
            price: 20000
        },
        {
            category: 'lang',
            bookName: 'react',
            price: 35000
        }
    ];

    return (
        <div>
            {
                books
                    .filter(
                        book =>
                            book.category === 'lang'
                    )
                    .map((book) => {
                        return (
                            <Book
                                bookName={
                                    book.bookName
                                }
                            />
                        );
                    })
            }
        </div>
    );
};
```

---

## JSX의 `{}`

JSX 안에서 JavaScript를 실행할 때 중괄호를 사용한다.

```jsx
{
    JavaScript 표현식
}
```

---

## `filter()`

```javascript
books.filter(
    book =>
        book.category === 'lang'
);
```

`category`가 `lang`인 객체만 남긴다.

결과:

```javascript
[
    {
        category: 'lang',
        bookName: 'javascript',
        price: 20000
    },
    {
        category: 'lang',
        bookName: 'react',
        price: 35000
    }
]
```

---

## React의 `map()`

```jsx
.map((book) => {
    return (
        <Book
            bookName={book.bookName}
        />
    );
})
```

각 객체를 `Book` 컴포넌트로 변환한다.

```jsx
[
    <Book bookName="javascript" />,
    <Book bookName="react" />
]
```

---

# 18. Props

```jsx
<Book bookName="javascript" />
```

`bookName`이라는 값을 `Book` 컴포넌트에 전달한다.

개념적으로 다음과 비슷하다.

```javascript
Book({
    bookName: 'javascript'
});
```

`Book` 컴포넌트에서는 다음 형태로 받는다.

```javascript
props = {
    bookName: 'javascript'
};
```

사용:

```jsx
<span>
    책이름: {props.bookName}
</span>
```
최종적으로 index.js에서는 
최종 출력:

```html

<div id="root">
    <div>
        <div>
            <div>
                <span>책이름 : javascript</span>
            </div>
        </div>

        <div>
            <div>
                <span>책이름 : react</span>
            </div>
        </div>
    </div>
</div>

```

---

# 전체 실행 흐름

```text
index.js 실행
→ LibraryPage import
→ #root에 LibraryPage 렌더링
→ LibraryPage 함수 실행
→ books 배열 생성
→ lang 카테고리만 filter
→ 각 객체를 Book 컴포넌트로 map
→ bookName을 props로 전달
→ Book 컴포넌트가 JSX 반환
→ 브라우저에 책 이름 출력
```

---

# 핵심 개념 정리

```text
Promise
→ 비동기 작업의 상태와 결과를 관리하는 객체

Promise.all
→ 여러 Promise가 모두 완료될 때까지 기다림

await
→ Promise 완료 후 결과값을 꺼냄

Array.from
→ 일정 길이의 배열 생성

map
→ 각 요소를 새로운 값으로 변환한 배열 반환

join
→ 배열을 하나의 문자열로 합침

export
→ 현재 파일의 기능을 외부로 내보냄

import
→ 다른 파일의 기능을 가져옴

JSX
→ JavaScript 안에서 HTML과 유사한 태그 사용

Component
→ 재사용 가능한 UI 단위

Props
→ 부모 컴포넌트가 자식 컴포넌트에 전달하는 데이터

ReactDOM
→ React 컴포넌트를 실제 화면에 렌더링
```













