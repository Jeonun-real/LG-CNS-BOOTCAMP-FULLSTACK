# BE 7일차 - Stream 심화와 Blog 상세조회·입력·수정 흐름

## 1. 오늘의 전체 흐름

오늘은 먼저 `StreamApp.java`와 `BlogStreamAPP.java`를 통해 Stream의 주요 문법을 연습하고, 이 문법을 Blog 애플리케이션의 상세조회와 검색에 적용했다.

이후 입력과 수정 기능을 계층 구조에 맞게 확장하는 과정을 진행했다.

```text
Stream 기본 문법
→ Blog DTO 목록을 Stream으로 처리
→ Optional로 단건 조회 결과 처리
→ Blog 상세조회 구현
→ RequestDTO로 입력 데이터 바인딩
→ 입력·수정 CRUD 흐름 확장
```

현재 파일을 기준으로 구현 상태를 구분하면 다음과 같다.

| 기능 | 현재 상태 |
|---|---|
| 전체 목록 | View부터 DAO까지 연결됨 |
| 상세조회 | View부터 DAO까지 연결됨 |
| 검색 | View·Controller·DAO 코드는 있으나 Service 구현 시그니처 수정 필요 |
| 입력 | View·FrontController·Controller까지 연결, Service·DAO는 구현 중 |
| 수정 | Service 인터페이스에 명세만 존재, 나머지 계층은 구현 전 |

## 2. Stream 처리 구조

Stream은 Collection에 저장된 데이터를 선언적인 방식으로 처리하는 API다.

```text
Collection
→ Stream 생성
→ 중간 연산 0개 이상
→ 최종 연산 1개
```

```java
blogs.stream()
    .filter(blog -> blog.getViewCnt() >= 30)
    .map(BlogResponseDTO::getEmail)
    .forEach(System.out::println);
```

코드의 타입 흐름은 다음과 같다.

```text
List<BlogResponseDTO>
→ Stream<BlogResponseDTO>
→ filter: Stream<BlogResponseDTO>
→ map: Stream<String>
→ forEach: 각 String 출력
```

### 중간 연산과 최종 연산

| 구분 | 메서드 | 역할 |
|---|---|---|
| 중간 연산 | `filter()` | 조건에 맞는 요소 선택 |
| 중간 연산 | `map()` | 요소를 다른 타입이나 값으로 변환 |
| 중간 연산 | `mapToInt()` | 객체 Stream을 `IntStream`으로 변환 |
| 중간 연산 | `distinct()` | 중복 요소 제거 |
| 중간 연산 | `sorted()` | 요소 정렬 |
| 최종 연산 | `forEach()` | 각 요소 소비 |
| 최종 연산 | `toList()` | 처리 결과를 List로 반환 |
| 최종 연산 | `collect()` | 결과를 원하는 자료구조로 수집 |
| 최종 연산 | `average()` | 숫자 Stream의 평균 계산 |
| 최종 연산 | `findAny()` | 조건에 맞는 요소 하나 탐색 |
| 최종 연산 | `anyMatch()` | 하나라도 조건에 맞는지 확인 |
| 최종 연산 | `allMatch()` | 모두 조건에 맞는지 확인 |
| 최종 연산 | `noneMatch()` | 모두 조건에 맞지 않는지 확인 |

## 3. StreamApp - Lambda와 함수형 인터페이스 복습

### 사용자 정의 함수형 인터페이스

```java
InspireFuction func01 =
    (x, y) -> x > y ? x : y;

InspireFuction func02 =
    (x, y) -> x + y;
```

Lambda는 함수형 인터페이스의 유일한 추상 메서드가 수행할 동작을 표현한다.

```text
(x, y) → 매개변수
->     → 매개변수와 실행식 구분
실행식  → 메서드가 수행할 동작과 반환값
```

### Supplier

```java
Supplier<String> supplier =
    () -> "inspire";

System.out.println(supplier.get());
```

```text
매개변수 X
반환값 O
실행 메서드: get()
```

### Consumer

```java
Consumer<String> consumer =
    str -> System.out.println(
        str.split(" ")[1]
    );

consumer.accept("lgcns inspire");
```

```text
매개변수 O
반환값 X
실행 메서드: accept()
```

### Function

```java
Function<String, Integer> function =
    str -> str.length();

int length = function.apply("lgcns inspire");
```

```text
String 입력
→ Integer 반환
실행 메서드: apply()
```

### Predicate

```java
Predicate<String> predicate =
    str -> str.equals("lgcns");

boolean result = predicate.test("lgcns");
```

```text
String 입력
→ boolean 반환
실행 메서드: test()
```

Stream의 메서드는 이러한 함수형 인터페이스를 매개변수로 받는다.

```text
filter()  ← Predicate
map()     ← Function
forEach() ← Consumer
```

### 메서드 참조

```java
brands.forEach(value -> System.out.println(value));
brands.forEach(System.out::println);
```

이미 존재하는 메서드를 Lambda에서 그대로 호출할 때 `객체 또는 타입::메서드명`으로 줄일 수 있다.

## 4. BlogStreamAPP의 Stream 주요 문법

### 4.1 filter - 조건에 맞는 데이터 선택

```java
blogs.stream()
    .filter(blog -> blog.getViewCnt() >= 30)
    .forEach(System.out::println);
```

`filter()`는 `Predicate<BlogResponseDTO>`를 전달받는다.

```text
BlogResponseDTO 입력
→ viewCnt >= 30 검사
→ true인 객체만 다음 단계로 전달
```

`filter()`는 조건에 맞는 객체를 골라내지만 원본 List를 직접 변경하지 않는다.

### 4.2 map - 요소와 타입 변환

```java
blogs.stream()
    .filter(blog -> blog.getViewCnt() >= 30)
    .map(BlogResponseDTO::getEmail)
    .forEach(System.out::println);
```

`map()`은 객체에서 필요한 값만 꺼내거나 다른 타입으로 변환한다.

```text
Stream<BlogResponseDTO>
→ BlogResponseDTO.getEmail()
→ Stream<String>
```

다음 두 코드는 같은 의미다.

```java
.map(blog -> blog.getEmail())
.map(BlogResponseDTO::getEmail)
```

### 4.3 toList와 collect

```java
List<BlogResponseDTO> result = blogs.stream()
    .filter(blog -> blog.getEmail().equals("lim"))
    .toList();
```

```java
List<BlogResponseDTO> result = blogs.stream()
    .filter(blog -> blog.getEmail().equals("lim"))
    .collect(Collectors.toList());
```

두 방식 모두 Stream 결과를 List로 만든다.

`Stream.toList()`의 결과는 일반적으로 수정할 수 없는 List다. 결과에 `add()`나 `remove()`가 필요하다면 수집 방식이나 새로운 `ArrayList` 생성 여부를 고려해야 한다.

### 4.4 groupingBy - 특정 기준으로 그룹화

```java
Map<String, List<BlogResponseDTO>> map =
    blogs.stream()
        .collect(
            Collectors.groupingBy(
                BlogResponseDTO::getEmail
            )
        );
```

작성자 이메일을 key로 하고, 해당 작성자의 게시글 목록을 value로 가진 Map이 만들어진다.

```text
"lim"
→ [react 게시글, msa 게시글]

"kim"
→ [java 게시글]

"park"
→ [docker 게시글]
```

```java
map.get("park")
    .forEach(System.out::println);
```

### 4.5 mapToInt와 average

```java
double avg = blogs.stream()
    .mapToInt(BlogResponseDTO::getViewCnt)
    .average()
    .orElse(0);
```

```text
Stream<BlogResponseDTO>
→ mapToInt()
→ IntStream
→ average()
→ OptionalDouble
→ orElse(0)
→ double
```

데이터가 하나도 없으면 평균값을 만들 수 없기 때문에 `average()`는 단순 `double`이 아니라 값이 없을 가능성을 표현하는 `OptionalDouble`을 반환한다.

`orElse(0)`은 값이 있으면 평균을 사용하고, 없으면 기본값 `0`을 사용한다.

### 4.6 distinct - 중복 제거

```java
blogs.stream()
    .map(BlogResponseDTO::getEmail)
    .distinct()
    .forEach(System.out::println);
```

먼저 `map()`으로 이메일 문자열만 꺼낸 뒤 `distinct()`로 중복 이메일을 제거한다.

```text
lim, kim, lee, park, lim
→ lim, kim, lee, park
```

객체 자체에 `distinct()`를 적용하면 `equals()`와 `hashCode()`를 기준으로 중복 여부를 판단한다.

### 4.7 sorted와 Comparator

```java
blogs.stream()
    .sorted(
        Comparator.comparing(
            BlogResponseDTO::getViewCnt
        ).reversed()
    )
    .forEach(System.out::println);
```

```text
Comparator.comparing(getViewCnt)
→ 조회수 오름차순 정렬 기준

reversed()
→ 정렬 순서를 반대로 변경
→ 조회수 내림차순
```

원본 `blogs`의 순서는 변경되지 않고 정렬된 Stream이 만들어진다.

### 4.8 anyMatch, allMatch, noneMatch

```java
boolean isEmail = blogs.stream()
    .anyMatch(blog ->
        blog.getEmail().equals("lim")
    );
```

하나라도 작성자가 `lim`이면 `true`다.

```java
boolean isExists = blogs.stream()
    .allMatch(blog -> blog.getViewCnt() >= 20);
```

모든 게시글의 조회수가 20 이상이어야 `true`다.

```java
boolean isUnder = blogs.stream()
    .noneMatch(blog -> blog.getViewCnt() < 10);
```

조회수가 10 미만인 게시글이 하나도 없으면 `true`다.

```text
anyMatch  → 하나라도 일치하는가?
allMatch  → 모두 일치하는가?
noneMatch → 일치하는 것이 하나도 없는가?
```

## 5. Optional

Optional은 값이 있을 수도 있고 없을 수도 있는 반환 결과를 명시적으로 표현하는 컨테이너다.

Optional은 `Integer`, `Double` 같은 Wrapper Class가 아니다. null 가능성을 감싸는 별도의 클래스다.

```java
Optional<String> optional =
    Optional.of("lgcns");
```

### Optional 생성

```java
Optional.of(value)
```

반드시 null이 아닌 값을 감싼다. null을 전달하면 `NullPointerException`이 발생한다.

```java
Optional.ofNullable(value)
```

값이 null일 수도 있을 때 사용한다.

```java
Optional.empty()
```

값이 없는 Optional을 만든다.

### 값이 있을 때와 없을 때 처리

```java
optional.ifPresentOrElse(
    value -> System.out.println(value),
    () -> System.out.println("값이 없다")
);
```

```text
값 있음 → Consumer 실행
값 없음 → Runnable 실행
```

### 기본값 반환

```java
String value = optional.orElse("기본값");
```

값이 있으면 해당 값을 반환하고 없으면 기본값을 반환한다.

### 예외 발생

```java
String value = optional.orElseThrow(
    () -> new RuntimeException("값이 없다")
);
```

값이 없으면 Supplier가 만든 예외를 발생시킨다.

현재 `BlogStreamAPP`에서는 다음 코드가 항상 예외를 발생시킨다.

```java
optional = Optional.empty();
String error = optional.orElseThrow(
    () -> new RuntimeException("값이 없다")
);
```

Optional은 보통 메서드의 반환 타입으로 사용하여 “검색 결과가 없을 수 있다”는 사실을 호출자에게 알릴 때 유용하다.

## 6. Blog 상세조회 기능

상세조회는 `blogId` 하나를 전달하여 일치하는 게시글 한 건을 찾는 기능이다.

### 전체 흐름

```text
사용자
→ blogId 입력
→ ReactView.read()
→ FrontController.read(endpoint, blogId)
→ BeanFactory.getBean("read.inspire")
→ ReadController.read(blogId)
→ ReactService.read(blogId)
→ ReactServiceImpl.read(blogId)
→ BlogReactDao.findById(blogId)
→ Optional<BlogResponseDTO>
→ BlogResponseDTO 또는 null
→ ReactView 출력
```

### 6.1 ReactView - blogId 입력

```java
public void read() {
    String endPoint = "read.inspire";

    int blogId = Integer.parseInt(
        scan.nextLine()
    );

    BlogResponseDTO response =
        front.read(endPoint, blogId);

    System.out.println(
        response != null ? response : "정보없음"
    );
}
```

주요 문법은 다음과 같다.

```text
scan.nextLine()
→ 입력을 String으로 받음

Integer.parseInt()
→ 숫자 문자열을 int로 변환

조건 ? 참값 : 거짓값
→ 삼항 연산자로 응답 존재 여부 출력
```

### 6.2 FrontController - endpoint로 Controller 조회

```java
public BlogResponseDTO read(
    String endpoint,
    int blogId
) {
    Object controller =
        factory.getBean(endpoint);

    return ((ReadController) controller)
        .read(blogId);
}
```

Factory의 반환 타입이 `Object`이므로 `ReadController`로 다운캐스팅한다.

```text
endpoint
→ 어떤 Controller를 사용할지 결정

blogId
→ 상세조회에 사용할 데이터
```

Factory는 `blogId`를 처리하지 않고 endpoint에 맞는 Controller만 반환한다.

### 6.3 BeanFactory - ReadController 등록

```java
map.put(
    "read.inspire",
    new ReadController(service)
);
```

```text
key   : "read.inspire"
value : ReadController 객체
```

`ReadController` 생성 시 `ReactService`를 생성자로 주입한다.

### 6.4 ReadController - Service에 위임

```java
public class ReadController {
    private ReactService service;

    public ReadController(ReactService service) {
        this.service = service;
    }

    public BlogResponseDTO read(int blogId) {
        return service.read(blogId);
    }
}
```

Controller는 직접 List를 검색하지 않고 `blogId`를 Service에 전달한다.

### 6.5 ReactService - 기능 명세

```java
public interface ReactService {
    BlogResponseDTO read(int blogId);
}
```

인터페이스가 상세조회 기능의 매개변수와 반환 타입을 정의한다.

```text
입력  : int blogId
출력  : BlogResponseDTO
```

### 6.6 ReactServiceImpl - Optional 결과 처리

```java
@Override
public BlogResponseDTO read(int blogId) {
    return dao.findById(blogId)
        .orElse(null);
}
```

DAO는 값이 없을 가능성을 `Optional`로 반환한다. Service는 Optional을 열어 객체가 있으면 반환하고 없으면 null을 반환한다.

```text
Optional에 값 있음
→ BlogResponseDTO 반환

Optional이 비어 있음
→ null 반환
```

### 6.7 BlogReactDao - Stream으로 단건 검색

```java
public Optional<BlogResponseDTO> findById(
    int blogId
) {
    return blogs.stream()
        .filter(blog ->
            blog.getBlogId() == blogId
        )
        .findAny();
}
```

```text
blogs.stream()
→ 각 BlogResponseDTO 순회
→ blogId가 일치하는 객체만 filter
→ findAny()로 한 건 탐색
→ Optional<BlogResponseDTO> 반환
```

`blog.getBlogId()`는 `Integer`이고 `blogId`는 `int`이므로 비교 과정에서 `Integer`가 자동 Unboxing된다.

## 7. Blog 입력 기능

입력은 여러 개의 값을 받아 `BlogRequestDTO` 하나로 바인딩하고 저장 계층까지 전달하는 기능이다.

### 의도한 전체 흐름

```text
사용자
→ title, content, email 입력
→ ReactView.insert()
→ FrontController.insert(endpoint, title, content, email)
→ BeanFactory.getBean("insert.inspire")
→ InsertController.insert(title, content, email)
→ BlogRequestDTO.builder().build()
→ ReactService.insert(request)
→ ReactServiceImpl.insert(request)
→ BlogReactDao.save(request)
→ int 처리 결과 반환
→ 성공 또는 실패 출력
```

### 7.1 ReactView - 입력값 수집

```java
public void insert() {
    String endPoint = "insert.inspire";

    String title = scan.nextLine();
    String content = scan.nextLine();
    String email = scan.nextLine();

    int flag = front.insert(
        endPoint,
        title,
        content,
        email
    );

    System.out.println(
        flag == 1 ? "입력성공" : "입력실패"
    );
}
```

사용자 입력을 각각 String으로 받은 뒤 FrontController에 전달한다.

### 7.2 FrontController - InsertController 호출

```java
public int insert(
    String endPoint,
    String title,
    String content,
    String email
) {
    Object controller =
        factory.getBean(endPoint);

    return ((InsertController) controller)
        .insert(title, content, email);
}
```

endpoint는 Controller 선택에 사용하고 나머지 값은 입력 데이터로 전달한다.

### 7.3 BeanFactory - InsertController 등록

```java
map.put(
    "insert.inspire",
    new InsertController(service)
);
```

List, Read, Search Controller와 동일한 Service 객체가 생성자 주입된다.

### 7.4 InsertController - 여러 값을 DTO로 바인딩

```java
public int insert(
    String title,
    String content,
    String email
) {
    BlogRequestDTO request =
        BlogRequestDTO.builder()
            .title(title)
            .content(content)
            .email(email)
            .build();

    return service.insert(request);
}
```

여러 개의 매개변수를 `BlogRequestDTO` 하나로 묶는다.

```text
title   ┐
content ├─→ BlogRequestDTO
email   ┘
```

Controller는 요청 데이터 바인딩과 유효성 검사를 담당할 수 있고, 실제 저장은 Service와 DAO에 위임한다.

### 7.5 RequestDTO를 저장 객체로 변환

```java
public static BlogResponseDTO toEntity(
    BlogRequestDTO request
) {
    return BlogResponseDTO.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .email(request.getEmail())
        .build();
}
```

`static` 메서드이므로 객체 생성 없이 클래스 이름으로 호출할 수 있다.

```java
BlogResponseDTO blog =
    BlogRequestDTO.toEntity(request);
```

현재 메서드명은 `toEntity()`지만 실제 반환 타입은 `BlogResponseDTO`다. JPA를 적용한다면 일반적으로 별도의 `BlogEntity`를 반환하도록 역할을 구분한다.

### 7.6 처리 결과 int

CRUD의 insert, update, delete는 영향을 받은 데이터 개수를 `int`로 표현하는 경우가 있다.

```text
1 → 한 건 입력 성공
0 → 입력된 데이터 없음 또는 실패
```

현재 `ReactServiceImpl.insert()`는 아직 `UnsupportedOperationException`을 발생시키고 `BlogReactDao.save()`도 `0`만 반환하므로 실제 저장까지는 완성되지 않은 상태다.

구현 의도는 다음과 같다.

```text
ReactServiceImpl.insert(request)
→ dao.save(request)
→ RequestDTO를 저장 객체로 변환
→ blogs.add(...)
→ 성공 결과 1 반환
```

## 8. Blog 수정 기능

수정 기능은 기존 게시글을 식별할 `blogId`와 변경할 값을 함께 전달해야 한다.

현재 코드에는 Service 인터페이스의 명세만 존재한다.

```java
public int update(BlogRequestDTO request);
```

`ReactServiceImpl.update()`는 아직 미구현 예외를 발생시킨다.

```java
@Override
public int update(BlogRequestDTO request) {
    throw new UnsupportedOperationException(
        "Unimplemented method 'updata'"
    );
}
```

### 수정 기능의 전체 설계 흐름

```text
사용자
→ blogId와 변경할 title, content, email 입력
→ ReactView.update()
→ FrontController.update(endpoint, ...)
→ BeanFactory.getBean("update.inspire")
→ UpdateController.update(...)
→ BlogRequestDTO로 데이터 바인딩
→ ReactService.update(request)
→ ReactServiceImpl.update(request)
→ BlogReactDao.update(request)
→ 일치하는 게시글 변경
→ int 처리 결과 반환
```

### 수정 RequestDTO

현재 `BlogRequestDTO`에는 다음 필드가 있다.

```java
private int id;
private String title;
private String content;
private String email;
```

수정 요청에서는 `id`가 어떤 게시글을 바꿀지 식별하고, 나머지 필드가 변경값을 나타낸다.

```text
id      → 수정 대상 식별
title   → 변경할 제목
content → 변경할 내용
email   → 변경할 작성자
```

### DAO에서 Stream을 활용하는 경우

수정 대상 검색은 상세조회와 비슷하게 구성할 수 있다.

```text
blogs.stream()
→ filter로 id가 일치하는 객체 검색
→ Optional로 존재 여부 표현
→ 값이 있으면 필드 변경
→ 성공 1 반환
→ 값이 없으면 0 반환
```

다만 현재 저장 객체인 `BlogResponseDTO`에는 Lombok `@Setter`가 있으므로 찾은 객체의 필드를 변경하는 방식이 가능하지만, 실제 JPA 환경에서는 Entity의 변경 감지와 트랜잭션을 이용하는 구조로 달라진다.

## 9. 기능별 데이터 흐름 비교

### 상세조회

```text
입력
blogId

DAO 처리
filter() → findAny()

반환
Optional<BlogResponseDTO>
→ BlogResponseDTO 또는 null
```

### 검색

```text
입력
keyword

Controller 처리
keyword → BlogRequestDTO

DAO 처리
title 또는 content에 keyword 포함 여부 filter

반환
List<BlogResponseDTO>
```

### 입력

```text
입력
title, content, email

Controller 처리
여러 입력값 → BlogRequestDTO

DAO 처리
RequestDTO → 저장 객체 변환 → List에 추가

반환
int: 성공 1, 실패 0
```

### 수정

```text
입력
id, title, content, email

Controller 처리
여러 입력값 → BlogRequestDTO

DAO 처리
id가 일치하는 객체 검색 → 필드 변경

반환
int: 수정 성공 1, 대상 없음 0
```

## 10. 계층별 공통 역할

```text
ReactView
→ 사용자 입력 수집
→ FrontController에 endpoint와 데이터 전달
→ 응답 렌더링

FrontController
→ endpoint로 Controller 조회
→ 기능별 Controller로 데이터 전달

BeanFactory
→ endpoint와 Controller 객체 매핑
→ Service 의존성 주입

기능별 Controller
→ 여러 입력값을 RequestDTO로 바인딩
→ Service에 요청 위임

ReactService
→ 기능의 매개변수와 반환 타입 명세

ReactServiceImpl
→ 비즈니스 로직 처리
→ DAO 호출

BlogReactDao
→ Collection에서 데이터 조회·입력·수정

RequestDTO
→ 사용자 요청 데이터 전달

ResponseDTO
→ 처리 결과 데이터 전달
```

## 11. 오늘 사용한 주요 문법

### Stream과 Lambda

```java
blogs.stream()
    .filter(blog -> condition)
    .map(BlogResponseDTO::getEmail)
    .toList();
```

### Optional

```java
Optional<BlogResponseDTO>
optional.orElse(null)
optional.orElseThrow(...)
optional.ifPresentOrElse(...)
```

### Builder

```java
BlogRequestDTO.builder()
    .title(title)
    .content(content)
    .email(email)
    .build();
```

### 다형성과 인터페이스

```java
ReactService service =
    new ReactServiceImpl(dao);
```

### 생성자 주입

```java
new ReadController(service)
new InsertController(service)
```

### Factory Map

```java
map.put("read.inspire", readController);
map.put("insert.inspire", insertController);
```

### 다운캐스팅

```java
((ReadController) controller).read(blogId);
```

### 문자열 숫자 변환

```java
int blogId = Integer.parseInt(
    scan.nextLine()
);
```

### 삼항 연산자

```java
response != null ? response : "정보없음"
```

### 정적 변환 메서드

```java
BlogRequestDTO.toEntity(request)
```

## 12. 현재 코드에서 확인할 부분

### 검색 Service 시그니처 불일치

인터페이스는 RequestDTO를 받는다.

```java
List<BlogResponseDTO> search(
    BlogRequestDTO request
);
```

구현 클래스에는 매개변수가 없다.

```java
public List<BlogResponseDTO> search()
```

따라서 현재 상태에서는 `@Override`가 성립하지 않는다. 구현 메서드가 RequestDTO를 받아 `dao.findByKeyword(request)`를 호출하는 형태로 연결되어야 한다.

### 입력 기능 미구현

```java
ReactServiceImpl.insert()
→ UnsupportedOperationException

BlogReactDao.save()
→ 항상 0 반환
```

View부터 Controller까지의 전달은 작성되었지만 실제 List 저장은 아직 이루어지지 않는다.

### 수정 기능 미연결

현재 `ReactService.update()` 명세만 있고 다음 요소는 아직 없다.

```text
ReactView.update()
FrontController.update()
UpdateController
BeanFactory의 update.inspire 등록
BlogReactDao.update()
```

### 메뉴 번호 불일치

화면에는 수정이 4번, 검색이 6번으로 표시된다.

```text
4. 수정화면
6. 검색
```

하지만 현재 switch의 `case 4`는 `search()`를 호출한다. 수정 기능을 연결할 때 메뉴 번호와 실제 메서드 호출을 일치시켜야 한다.

### BlogStreamAPP의 강제 예외

```java
Optional.empty()
    .orElseThrow(...)
```

해당 코드는 의도적으로 예외를 발생시키므로 이후 코드가 있다면 실행되지 않는다. Optional의 예외 동작을 확인하는 학습 코드다.

### 이름 오타

- `BlogStreamAPP` → Java 관례상 `BlogStreamApp`
- `InspireFuction` → `InspireFunction`
- 출력 문자열의 `gropingby` → `groupingBy`
- 예외 메시지의 `updata` → `update`

## 13. 최종 핵심

- `filter()`는 Predicate 조건을 통과한 요소만 남긴다.
- `map()`은 요소의 값 또는 타입을 변환한다.
- `groupingBy()`는 기준값을 key로 묶어 Map을 만든다.
- `mapToInt().average()`는 숫자 평균을 계산하고 결과가 없을 가능성을 Optional로 표현한다.
- `distinct()`는 중복을 제거한다.
- `sorted(Comparator...)`는 정렬 기준을 정의한다.
- `anyMatch`, `allMatch`, `noneMatch`는 조건 일치 여부를 boolean으로 반환한다.
- `Optional`은 값이 없을 가능성이 있는 반환값을 명시적으로 표현한다.
- 상세조회는 `filter().findAny()`를 사용해 `Optional<BlogResponseDTO>`를 반환한다.
- 입력은 여러 문자열을 `BlogRequestDTO` 하나로 바인딩하여 계층 사이에 전달한다.
- 수정은 식별자와 변경값을 RequestDTO로 전달한 뒤 저장 객체를 찾아 변경하는 흐름이다.
- Controller는 입력을 DTO로 바인딩하고 Service에 위임한다.
- Service는 업무 로직을 담당하고 DAO는 실제 데이터 조회와 변경을 담당한다.

## 14. 오늘의 한 줄 요약

Stream의 `filter`, `map`, `groupingBy`, `average`, `distinct`, `sorted`, Match 연산과 Optional을 학습하고, 이를 활용해 Blog 상세조회에서 `blogId`에 맞는 단건을 찾았으며 입력과 수정 데이터를 RequestDTO로 바인딩해 View부터 DAO까지 전달하는 CRUD 구조를 확장했다.

