# FE 10일차 핵심 개념 정리

## 0. 오늘 구현한 전체 구조

오늘은 OpenWeather API와 Kakao Maps API를 React에 연동했다.

기능을 다음과 같이 나누었다.

```text
WeatherPage
├─ 날씨와 선택 도시 상태 관리
├─ 현재 위치 조회
├─ 도시명을 좌표로 변환
├─ 좌표를 이용한 날씨 API 호출
├─ WeatherBox에 날씨 데이터 전달
├─ WeatherButton에 도시와 이벤트 핸들러 전달
└─ KakaoMap에 지도 이동 좌표와 날씨 조회 함수 전달

WeatherBox
└─ 도시, 온도, 날씨 설명 출력

WeatherButton
└─ 도시 버튼 반복 생성 및 클릭 이벤트 전달

KakaoMap
├─ Kakao 지도 객체 생성
├─ 마커 객체 생성
├─ 지도와 마커를 ref에 저장
├─ 지도 클릭 좌표 추출
└─ 외부 좌표가 변경되면 지도와 마커 이동
```

### 전체 데이터 흐름

```text
[페이지 최초 실행]
navigator.geolocation
→ 현재 위도·경도 획득
→ OpenWeather 좌표 기반 조회
→ 날씨 상태 변경
→ WeatherBox 재렌더링

[도시 버튼 클릭]
WeatherButton
→ cityHandler(city)
→ Kakao Geocoder로 도시명을 좌표로 변환
→ setMoveTo({ lat, lng })
→ getWeatherByCoords(lat, lng)
→ KakaoMap 중심과 마커 이동
→ WeatherBox 날씨 정보 변경

[지도 클릭]
KakaoMap 클릭 이벤트
→ 클릭한 lat, lng 추출
→ 기존 마커 위치 변경
→ 부모에게 받은 날씨 조회 함수 호출
→ WeatherPage의 weather 상태 변경
→ WeatherBox 재렌더링
```

---

## 1. 컴포넌트의 역할 분리

React 페이지를 만들 때 하나의 컴포넌트에 모든 코드를 작성하기보다 역할에 따라 기능을 분리한다.

| 컴포넌트 | 역할 |
|---|---|
| `WeatherPage` | 데이터, state, API 요청, 이벤트 처리 관리 |
| `WeatherBox` | 날씨 데이터 표시 |
| `WeatherButton` | 도시 버튼 표시 및 클릭 이벤트 발생 |
| `KakaoMap` | 지도·마커 생성과 지도 이벤트 처리 |

`WeatherPage`가 상태를 소유하고 필요한 데이터와 함수를 자식에게 props로 전달한다.

```jsx
<WeatherBox weather={weather} />

<WeatherButton
    cities={cities}
    city={city}
    handler={cityHandler}
/>

<KakaoMap
    setWeatherByCoords={getWeatherByCoords}
    moveTo={moveTo}
/>
```

### 핵심 원칙

```text
Page   → 상태와 기능의 흐름 관리
Box    → 전달받은 데이터 표시
Button → 사용자 이벤트 발생
Map    → 외부 지도 객체와 지도 이벤트 관리
```

---

## 2. 부모에서 자식으로 데이터와 함수 전달

React에서는 부모가 자식에게 props로 값뿐만 아니라 함수도 전달할 수 있다.

### 데이터 전달

```jsx
<WeatherBox weather={weather} />
```

```jsx
const WeatherBox = ({ weather }) => {
    return <div>{weather.name}</div>;
};
```

### 이벤트 핸들러 전달

```jsx
<WeatherButton handler={cityHandler} />
```

```jsx
const WeatherButton = ({ handler }) => {
    return (
        <button onClick={(e) => handler(e, "서울")}>
            서울
        </button>
    );
};
```

### 지도 컴포넌트에 API 호출 함수 전달

```jsx
<KakaoMap
    setWeatherByCoords={getWeatherByCoords}
/>
```

자식인 `KakaoMap`은 지도 클릭 좌표를 부모 함수에 전달한다.

```jsx
setWeatherByCoords(lat, lng);
```

실제 `weather` 상태를 변경하는 주체는 부모인 `WeatherPage`이다.

```text
KakaoMap에서 클릭 발생
→ 부모에게 받은 함수 호출
→ WeatherPage에서 API 요청
→ setWeather(data)
→ WeatherBox 재렌더링
```

이처럼 **데이터는 부모에서 자식으로 내려가고, 자식의 이벤트는 부모에게 받은 함수를 호출해 전달**할 수 있다.

---

## 3. `navigator.geolocation`

브라우저의 현재 위치를 가져올 때 사용한다.

```jsx
navigator.geolocation.getCurrentPosition((position) => {
    const lat = position.coords.latitude;
    const lon = position.coords.longitude;

    getCurrentWeather(lat, lon);
});
```

### 주요 값

```text
position.coords.latitude  → 위도
position.coords.longitude → 경도
```

### 성공과 실패 콜백

사용자가 위치 권한을 거부할 수도 있으므로 실패 처리도 추가할 수 있다.

```jsx
navigator.geolocation.getCurrentPosition(
    (position) => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        getWeatherByCoords(lat, lon);
    },
    (error) => {
        console.log("현재 위치 조회 실패", error);
    }
);
```

### 주의할 점

- 사용자가 브라우저 위치 권한을 허용해야 한다.
- 위치 조회는 비동기로 처리된다.
- 위도와 경도의 순서를 바꾸지 않아야 한다.
- OpenWeather에서는 `lon`, Kakao 지도에서는 관습적으로 `lng`라는 이름을 사용한다.

```text
OpenWeather: lat, lon
Kakao Maps:  lat, lng
```

---

## 4. OpenWeather API 연동

### 좌표 기반 날씨 조회

```jsx
const getWeatherByCoords = async (lat, lng) => {
    const endPoint =
        "https://api.openweathermap.org/data/2.5/weather" +
        `?lat=${lat}&lon=${lng}&appid=${key}`;

    await fetch(endPoint)
        .then((response) => {
            console.log("fetch response", response);
            return response.json();
        })
        .then((data) => {
            console.log("weather data", data);
            setWeather(data);
        })
        .catch((error) => {
            console.log("fetch error", error);
        });
};
```

### 요청 흐름

```text
lat, lng 전달
→ 요청 URL 생성
→ fetch()로 GET 요청
→ Response를 JSON으로 변환
→ setWeather(data)
→ WeatherBox 재렌더링
```

### 섭씨와 한글 설명 옵션

요청 URL에 다음 옵션을 넣으면 온도를 직접 변환하지 않아도 된다.

```jsx
const endPoint =
    "https://api.openweathermap.org/data/2.5/weather" +
    `?lat=${lat}&lon=${lng}` +
    `&appid=${key}&units=metric&lang=kr`;
```

```text
units=metric → 섭씨 온도
lang=kr      → 날씨 설명을 한국어로 요청
```

### 응답 데이터 사용

```jsx
weather.name
weather.main?.temp
weather.weather?.[0]?.description
```

API 응답이 도착하기 전에는 중첩 속성이 존재하지 않을 수 있으므로 Optional Chaining을 사용한다.

```jsx
<div>{weather.main?.temp}</div>
<div>{weather.weather?.[0]?.description}</div>
```

---

## 5. Promise의 `.then().catch()` 문법

비동기 처리는 `try/catch`뿐 아니라 Promise 체이닝으로 구성할 수 있다.

```jsx
fetch(endPoint)
    .then((response) => {
        return response.json();
    })
    .then((data) => {
        setWeather(data);
    })
    .catch((error) => {
        console.log(error);
    });
```

각 단계의 역할은 다음과 같다.

```text
fetch()      → Promise<Response> 반환
첫 번째 then → Response를 JSON으로 변환
두 번째 then → 변환된 데이터 사용
catch        → 네트워크 또는 코드 실행 오류 처리
```

### `return response.json()`이 필요한 이유

```jsx
.then((response) => {
    return response.json();
})
```

`response.json()`도 Promise를 반환한다. 이를 `return`해야 다음 `.then()`에서 변환된 데이터를 받을 수 있다.

### HTTP 오류 확인

`fetch()`는 404나 500 응답에서 자동으로 `.catch()`로 이동하지 않는다. 따라서 `response.ok`를 확인하는 것이 안전하다.

```jsx
fetch(endPoint)
    .then((response) => {
        if (!response.ok) {
            throw new Error(`HTTP 오류: ${response.status}`);
        }

        return response.json();
    })
    .then((data) => {
        setWeather(data);
    })
    .catch((error) => {
        console.log(error);
    });
```

### `async/await`와 함께 사용

현재 코드처럼 다음 작성도 동작한다.

```jsx
const getWeather = async () => {
    await fetch(endPoint)
        .then(...)
        .catch(...);
};
```

하지만 `.then().catch()`만 사용할 경우 `async`와 `await`는 필수가 아니다.

```jsx
const getWeather = () => {
    return fetch(endPoint)
        .then(...)
        .catch(...);
};
```

---

## 6. 환경 변수와 API 키

API 키를 코드에 직접 작성하지 않고 `.env` 파일로 분리할 수 있다.

```text
REACT_APP_WEATHER_API_KEY=발급받은_API_KEY
```

React 코드에서는 다음처럼 읽는다.

```jsx
const key = process.env.REACT_APP_WEATHER_API_KEY;
```

Create React App에서는 브라우저에서 사용할 환경 변수 이름이 `REACT_APP_`으로 시작해야 한다.

### `.gitignore`

실제 키가 저장된 `.env`는 Git에 올라가지 않도록 설정한다.

```gitignore
.env
.env.local
```

### 중요한 보안 주의점

프론트엔드의 `.env`는 소스 코드에서 키를 분리할 뿐, 브라우저 사용자에게 키를 완전히 숨기지는 못한다. 빌드 결과와 네트워크 요청에서 확인할 수 있기 때문이다.

```text
프론트엔드 .env
→ 저장소 실수 방지와 환경별 설정에는 유용
→ 완전한 비밀 보관 수단은 아님
```

정말 외부에 노출되면 안 되는 비밀 키는 백엔드에서 관리하고 프론트엔드는 백엔드 API를 호출해야 한다.

---

## 7. Kakao 지도 생성

지도는 HTML 컨테이너와 지도 옵션을 이용해 생성한다.

```jsx
const container = document.getElementById("map");

const centerPosition = new window.kakao.maps.LatLng(
    37.5665,
    126.9780
);

const map = new window.kakao.maps.Map(container, {
    center: centerPosition,
    level: 3
});
```

### 지도 컨테이너

지도를 표시할 요소에는 반드시 너비와 높이가 있어야 한다.

```jsx
<div
    id="map"
    style={{
        width: "100%",
        height: "400px"
    }}
/>
```

높이가 없으면 지도 객체가 생성돼도 화면에 보이지 않을 수 있다.

---

## 8. Kakao 지도 마커 생성

```jsx
const marker = new window.kakao.maps.Marker({
    position: centerPosition
});

marker.setMap(map);
```

```text
new Marker()  → 마커 객체 생성
marker.setMap → 마커를 특정 지도에 표시
```

마커 위치를 변경할 때는 새로운 마커를 계속 생성하지 않고 기존 마커의 위치만 변경한다.

```jsx
marker.setPosition(position);
```

---

## 9. `useState`와 `useRef`의 차이

### `useState`

화면에 표시되는 데이터처럼 값이 변경됐을 때 React 재렌더링이 필요한 경우 사용한다.

```jsx
const [weather, setWeather] = useState({});
const [city, setCity] = useState("");
const [moveTo, setMoveTo] = useState({});
```

```text
setWeather(data)
→ weather 상태 변경
→ WeatherPage 재렌더링
→ WeatherBox에 새로운 props 전달
```

### `useRef`

렌더링 사이에서 값을 유지하지만 값이 바뀌어도 React 재렌더링이 필요하지 않은 경우 사용한다.

```jsx
const mapRef = useRef(null);
const markerRef = useRef(null);
```

외부 SDK가 만든 객체를 ref에 저장한다.

```jsx
mapRef.current = map;
markerRef.current = marker;
```

이후 새로운 객체를 만들지 않고 기존 객체를 사용한다.

```jsx
mapRef.current.setCenter(position);
markerRef.current.setPosition(position);
```

### 차이 정리

| 구분 | `useState` | `useRef` |
|---|---|---|
| 값 변경 시 재렌더링 | 발생 | 발생하지 않음 |
| 화면에 표시되는 값 | 적합 | 일반적으로 부적합 |
| DOM 참조 | 부적합 | 적합 |
| 외부 라이브러리 객체 보관 | 경우에 따라 가능 | 적합 |
| 접근 방법 | 상태 변수 | `ref.current` |

### 지도와 마커에 `useRef`가 적합한 이유

Kakao 지도와 마커는 React가 만든 JSX 객체가 아니라 Kakao SDK가 생성한 JavaScript 객체이다.

```text
지도 중심 변경
→ React 화면 전체를 다시 그릴 필요 없음
→ 기존 map 객체의 setCenter() 호출

마커 위치 변경
→ 새 Marker를 만들 필요 없음
→ 기존 marker 객체의 setPosition() 호출
```

따라서 지도와 마커의 인스턴스는 ref에 저장하고 재사용한다.

> 현재 구현은 마커가 화면 중앙에 고정된 방식이 아니다. 마커 객체 하나를 고정적으로 재사용하면서 도시 선택 또는 지도 클릭 좌표로 위치를 변경하는 방식이다.

---

## 10. 지도 클릭 이벤트

```jsx
window.kakao.maps.event.addListener(
    map,
    "click",
    function (mouseEvent) {
        const lat = mouseEvent.latLng.getLat();
        const lng = mouseEvent.latLng.getLng();

        const position = new window.kakao.maps.LatLng(
            lat,
            lng
        );

        marker.setPosition(position);
        setWeatherByCoords(lat, lng);
    }
);
```

### 실행 흐름

```text
사용자가 지도 클릭
→ mouseEvent 발생
→ 클릭 좌표 추출
→ 기존 마커를 클릭 좌표로 이동
→ 부모에게 받은 날씨 조회 함수 호출
→ 해당 좌표의 날씨 정보 표시
```

```jsx
mouseEvent.latLng.getLat(); // 위도
mouseEvent.latLng.getLng(); // 경도
```

---

## 11. 외부 좌표로 지도와 마커 이동

부모의 `moveTo`가 변경되면 `KakaoMap`의 `useEffect`가 실행된다.

```jsx
useEffect(() => {
    if (!moveTo) return;
    if (!mapRef.current || !markerRef.current) return;

    const lat = parseFloat(moveTo.lat);
    const lng = parseFloat(moveTo.lng);

    const position = new window.kakao.maps.LatLng(
        lat,
        lng
    );

    mapRef.current.setCenter(position);
    markerRef.current.setPosition(position);
}, [moveTo]);
```

### 방어 코드

지도와 마커가 생성되기 전에 접근하면 오류가 발생할 수 있다.

```jsx
if (!mapRef.current || !markerRef.current) return;
```

좌표가 문자열일 수도 있으므로 숫자로 변환한다.

```jsx
const lat = parseFloat(moveTo.lat);
const lng = parseFloat(moveTo.lng);
```

더 안전하게 사용하려면 숫자 여부도 검사한다.

```jsx
if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
    return;
}
```

---

## 12. Kakao Geocoder로 도시명을 좌표로 변환

도시 버튼에는 `서울`, `부산` 같은 문자열이 들어 있다. Kakao Geocoder를 이용하면 주소 문자열을 위도와 경도로 변환할 수 있다.

```jsx
const getCoordsByCity = (cityName) => {
    const geocoder =
        new window.kakao.maps.services.Geocoder();

    geocoder.addressSearch(cityName, (result, status) => {
        if (
            status ===
            window.kakao.maps.services.Status.OK
        ) {
            const lat = parseFloat(result[0].y);
            const lng = parseFloat(result[0].x);

            setMoveTo({
                lat,
                lng,
                time: Date.now()
            });

            getWeatherByCoords(lat, lng);
        }
    });
};
```

### `x`, `y` 좌표 주의

```text
result[0].x → 경도(lng)
result[0].y → 위도(lat)
```

### `time: Date.now()`를 넣은 이유

React state에 이전과 완전히 동일한 객체 값이 들어가는 상황에서도 새로운 이동 요청임을 명확하게 표현할 수 있다.

```jsx
setMoveTo({
    lat,
    lng,
    time: Date.now()
});
```

다만 매번 새로운 객체를 생성하므로 현재 구조에서는 `time`이 없어도 대부분 `useEffect`가 실행된다.

### 정확한 청사 위치를 사용하려면

`addressSearch("서울")`은 특정 청사가 아닌 대표 검색 결과를 반환할 수 있다. 청사를 기준으로 이동하려면 구체적인 주소를 사용해야 한다.

```jsx
const cities = [
    {
        label: "서울",
        address: "서울특별시 중구 세종대로 110"
    },
    {
        label: "부산",
        address: "부산광역시 연제구 중앙대로 1001"
    }
];
```

```jsx
getCoordsByCity(selectedCity.address);
```

---

## 13. 현재 코드에서 정리할 부분

현재 `cityHandler`는 좌표 기반 조회를 실행한다.

```jsx
const cityHandler = (e, city) => {
    setCity(city);
    getCoordsByCity(city);
};
```

그런데 `city` 상태가 바뀌면 아래 `useEffect`도 실행된다.

```jsx
useEffect(() => {
    if (city !== "") {
        getCityWeather();
    }
}, [city]);
```

따라서 도시 버튼 한 번에 요청이 중복될 수 있다.

```text
cityHandler
├─ getCoordsByCity(city)
│  └─ getWeatherByCoords(lat, lng)
│
└─ setCity(city)
   └─ useEffect
      └─ getCityWeather()
```

또한 `getCityWeather()`는 한글 도시명을 OpenWeather의 `q`에 전달하므로 404가 발생할 가능성이 있다.

좌표 기반으로 통일했다면 해당 `useEffect`와 `getCityWeather()`를 제거하고 다음 흐름만 사용하는 것이 좋다.

```text
도시 버튼
→ Kakao Geocoder
→ 좌표 획득
→ getWeatherByCoords()
```

현재 위치 조회 함수도 `getCurrentWeather()`와 `getWeatherByCoords()`가 같은 좌표 기반 API를 호출하므로 하나로 통합할 수 있다.

```jsx
navigator.geolocation.getCurrentPosition((position) => {
    const lat = position.coords.latitude;
    const lng = position.coords.longitude;

    setMoveTo({ lat, lng });
    getWeatherByCoords(lat, lng);
});
```

---

## 14. `PUT`과 `PATCH`

둘 다 데이터를 수정할 때 사용하는 HTTP 메서드이지만 수정 범위가 다르다.

### `PUT`: 전체 교체

```jsx
api.put(`/blogs/${id}`, {
    title,
    content,
    category,
    email
});
```

기존 자원의 전체 데이터를 새로운 데이터로 교체한다. 서버 구현에 따라 전달하지 않은 속성이 사라지거나 기본값으로 변경될 수 있다.

### `PATCH`: 일부 수정

```jsx
api.patch(`/blogs/${id}`, {
    title
});
```

기존 데이터 중 전달한 일부 속성만 변경한다.

| 메서드 | 의미 | 예시 |
|---|---|---|
| `PUT` | 자원 전체 교체 | 게시글 전체 수정 |
| `PATCH` | 자원 일부 변경 | 게시글 제목만 수정 |

```text
PUT   = 전체 교체
PATCH = 부분 수정
```

---

## 15. 1:N 관계와 삭제 제약조건

게시글 하나에 여러 댓글이 연결되는 관계는 1:N 관계이다.

```text
Blog 1
├─ Comment 1
├─ Comment 2
└─ Comment 3
```

댓글의 `blogId`는 게시글의 `id`를 참조한다.

```text
blogs.id ← comments.blogId
```

### 부모 데이터 삭제 문제

댓글이 남아 있는 상태에서 게시글을 삭제하면 댓글이 참조할 게시글이 사라진다.

```text
게시글 삭제
→ 댓글의 blogId가 존재하지 않는 게시글을 참조
→ 데이터 무결성 문제 발생
```

이를 방지하기 위해 외래 키와 삭제 제약조건을 설정한다.

### 대표적인 삭제 정책

| 정책 | 동작 |
|---|---|
| `RESTRICT` | 자식 데이터가 있으면 부모 삭제 금지 |
| `CASCADE` | 부모 삭제 시 자식도 함께 삭제 |
| `SET NULL` | 부모 삭제 시 자식의 외래 키를 `NULL`로 변경 |

예시:

```sql
FOREIGN KEY (BLOG_ID)
REFERENCES BLOG(ID)
ON DELETE CASCADE
```

이 설정에서는 게시글을 삭제하면 연결된 댓글도 함께 삭제된다.

> 어떤 정책을 사용할지는 서비스 요구사항에 따라 결정해야 한다. 항상 `CASCADE`가 정답인 것은 아니다.

---

## 16. 핵심 코드 연결

```jsx
const WeatherPage = () => {
    const [weather, setWeather] = useState({});
    const [moveTo, setMoveTo] = useState({});

    const getWeatherByCoords = (lat, lng) => {
        const endPoint =
            "https://api.openweathermap.org/data/2.5/weather" +
            `?lat=${lat}&lon=${lng}` +
            `&appid=${key}&units=metric&lang=kr`;

        return fetch(endPoint)
            .then((response) => response.json())
            .then((data) => {
                setWeather(data);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    const getCoordsByCity = (cityName) => {
        const geocoder =
            new window.kakao.maps.services.Geocoder();

        geocoder.addressSearch(cityName, (result, status) => {
            if (
                status ===
                window.kakao.maps.services.Status.OK
            ) {
                const lat = parseFloat(result[0].y);
                const lng = parseFloat(result[0].x);

                setMoveTo({ lat, lng });
                getWeatherByCoords(lat, lng);
            }
        });
    };

    return (
        <>
            <KakaoMap
                moveTo={moveTo}
                setWeatherByCoords={getWeatherByCoords}
            />

            <WeatherBox weather={weather} />
        </>
    );
};
```

---

## 17. 핵심 요약

| 개념 | 핵심 내용 |
|---|---|
| Geolocation | 브라우저에서 현재 위도·경도 획득 |
| OpenWeather API | 도시 또는 좌표를 기반으로 날씨 조회 |
| Kakao Geocoder | 주소 문자열을 지도 좌표로 변환 |
| Kakao Map | 좌표를 중심으로 지도 표시 |
| Marker | 지도 위에서 선택 위치 표현 |
| props | 부모가 자식에게 데이터와 함수 전달 |
| `useState` | 변경 시 화면 재렌더링이 필요한 값 관리 |
| `useRef` | 재렌더링 없이 지도·마커 객체 유지 |
| `useEffect` | 마운트 또는 의존값 변경 시 부수 효과 실행 |
| Promise 체이닝 | `.then().then().catch()`로 비동기 처리 |
| `.env` | 환경별 설정과 키를 소스 코드에서 분리 |
| `PUT` | 데이터 전체 교체 |
| `PATCH` | 데이터 일부 수정 |
| 1:N 제약조건 | 부모 삭제 시 자식 데이터의 무결성 보호 |

## 오늘의 한 줄 정리

> React state로 화면의 날씨와 이동 좌표를 관리하고, ref로 Kakao 지도와 마커 객체를 재사용하면 API 응답, 사용자 이벤트, 외부 SDK를 하나의 흐름으로 연결할 수 있다.
