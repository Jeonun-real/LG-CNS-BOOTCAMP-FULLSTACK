# BE 5일차 - Collection, Generic, Lambda, Stream

## 1. 오늘의 전체 흐름

```text
DTO 객체 생성
→ Collection에 객체 저장
→ Generic으로 타입 제한
→ Lambda로 처리 동작 전달
→ Stream으로 필터링·변환·출력
```

오늘 학습의 중심은 데이터를 **저장하고**, **타입을 안전하게 관리하고**, **선언적으로 처리하는 방법**이다.

## 2. 오늘의 핵심 정리

### Collection API

Collection API는 여러 객체를 저장하고 관리하기 위한 Java의 자료구조 API다.

```text
List
- 중복 허용
- 저장 순서 존재
- index를 이용한 접근 가능
- 요소로 객체 저장
- 가변 길이

Set
- 중복 허용하지 않음
- 저장 순서를 보장하지 않음
- index가 없음
- 요소로 객체 저장
- 가변 길이

Map
- 데이터를 key-value 쌍으로 저장
- 하나의 key에는 하나의 value가 연결됨
- key는 중복될 수 없고 value는 중복 가능
- {key:value}, {key:value}, ... 형태
```

대표적인 선언은 다음과 같다.

```java
List<String> list = new ArrayList<>();
Set<String> set = new HashSet<>();
Map<String, String> map = new HashMap<>();
```

Collection은 객체를 요소로 다룬다. 기본 타입은 Wrapper Class를 통한 Boxing으로 객체처럼 저장된다.

```java
List<Integer> numbers = new ArrayList<>();
numbers.add(10); // int가 Integer로 Autoboxing
```

### Stream API

Stream API는 Collection의 데이터를 저장하는 자료구조가 아니라, 데이터를 필터링·변환·출력하기 위한 **처리 흐름**이다.

```text
Collection
→ Stream 생성
→ 중간 연산 0개 이상
→ 최종 연산 1개
```

```java
personList.stream()
    .filter(person -> person.getName().length() > 5)
    .forEach(person ->
        System.out.println(person.PerosnInfo())
    );
```

- `filter()`: 조건에 맞는 요소만 선택하는 중간 연산
- `map()`: 요소를 다른 형태로 변환하는 중간 연산
- `forEach()`: 각 요소를 소비하는 최종 연산
- `collect()`: 처리 결과를 List 등으로 수집하는 최종 연산
- 원본 Collection을 직접 변경하지 않음
- 최종 연산이 끝난 Stream은 다시 사용할 수 없음

### 함수형 인터페이스 타입

함수형 인터페이스는 추상 메서드가 하나인 인터페이스이며 Lambda 식의 기준이 된다.

| 타입 | 매개변수 | 반환값 | 실행 메서드 | 역할 |
|---|---:|---:|---|---|
| `Supplier<T>` | X | O | `get()` | 값을 공급 |
| `Consumer<T>` | O | X | `accept()` | 값을 소비 |
| `Function<T, R>` | O | O | `apply()` | 값을 다른 값으로 변환 |
| `Predicate<T>` | O | `boolean` | `test()` | 조건 검사 |

```java
// Supplier: 매개변수 X, 반환값 O
Supplier<String> supplier = () -> "inspire";

// Consumer: 매개변수 O, 반환값 X
Consumer<String> consumer =
    value -> System.out.println(value);

// Function: 매개변수 O, 반환값 O
Function<String, Integer> function =
    value -> value.length();

// Predicate: 매개변수 O, boolean 반환
Predicate<String> predicate =
    value -> value.equals("lgcns");
```

Stream의 주요 메서드도 이러한 함수형 인터페이스 타입의 Lambda를 전달받는다.

```text
filter()  ← Predicate
map()     ← Function
forEach() ← Consumer
```

## 3. Collection API

Collection의 차이는 `CollectionApp.java`에서 실제로 데이터를 넣고 꺼내는 코드를 보면 더 명확하다.

### 3.1 List 실제 코드

```java
List<String> list = new ArrayList<String>();

list.add("String");

System.out.println("list size: " + list.size());
System.out.println("list: " + list);

for (int idx = 0; idx < list.size(); idx++) {
    String data = list.get(idx);
    System.out.println(data);
}
```

코드를 한 줄씩 해석하면 다음과 같다.

```java
List<String> list = new ArrayList<String>();
```

- `List<String>`: 참조변수 타입이다. `String` 요소만 다루겠다는 의미다.
- `new ArrayList<String>()`: 실제 생성되는 객체다.
- `List`는 인터페이스라서 직접 객체를 만들 수 없다.
- `ArrayList`는 `List`를 구현한 클래스다.
- 부모 인터페이스 타입에 구현 객체를 대입한 변수 타입의 다형성이다.

```java
list.add("String");
```

`add()`로 요소를 추가한다. 배열과 달리 정해진 길이 없이 요소를 추가하면서 크기가 변한다.

```java
list.size();   // 현재 요소 개수
list.get(idx); // 해당 index의 요소 조회
```

List는 저장 순서를 유지하고 index가 있으므로 일반 for문으로 순회할 수 있다.

```text
list.add("A")
list.add("B")
list.add("A")

결과: [A, B, A]
       0  1  2
```

같은 값 `A`를 두 번 넣어도 모두 저장되므로 중복을 허용한다.

### 3.2 Set 실제 코드

```java
Set<String> set = new HashSet<>();

set.add("jslim");
set.add("inspire");
set.add("lgcns");
set.add("jslim");

System.out.println(set);

Object[] setAry = set.toArray();
for (Object data : setAry) {
    System.out.println(data);
}
```

```java
Set<String> set = new HashSet<>();
```

- `Set<String>`: 참조변수 타입이며 `String`만 저장한다.
- `HashSet<>`: 실제 생성되는 구현 객체다.
- `Set`도 인터페이스이므로 `new Set<>()`은 불가능하다.

`"jslim"`을 두 번 추가했지만 Set은 중복을 허용하지 않으므로 한 번만 남는다.

```text
추가 시도: jslim, inspire, lgcns, jslim
저장 결과: jslim, inspire, lgcns
```

`HashSet`은 저장 순서를 보장하지 않고 index가 없다.

```java
set.get(0); // 컴파일 오류: Set에는 get(index)가 없음
```

현재 코드에서는 `toArray()`로 배열을 만든 뒤 향상된 for문으로 순회했다. 배열로 바꾸지 않고 Set을 바로 순회해도 된다.

```java
for (String data : set) {
    System.out.println(data);
}
```

### 3.3 Map 실제 코드

```java
List<StudentDTO> studentList = new ArrayList<>();
List<TeacherDTO> teacherList = new ArrayList<>();
List<ManagerDTO> managerList = new ArrayList<>();

Map<String, List<? extends PersonDTO>> map = new HashMap<>();

map.put("student", studentList);
map.put("teacher", teacherList);
map.put("manager", managerList);
```

Map 선언을 부분별로 나누면 다음과 같다.

```text
Map<
    String,                     ← key 타입
    List<? extends PersonDTO>   ← value 타입
>
```

`key`는 `"student"`, `"teacher"`, `"manager"`처럼 목록의 이름을 나타낸다. `value`에는 각 DTO 목록이 저장된다.

```text
"student" → List<StudentDTO>
"teacher" → List<TeacherDTO>
"manager" → List<ManagerDTO>
```

실제 데이터를 넣고 조회하면 다음과 같다.

```java
studentList.add(
    StudentDTO.builder()
        .name("inspire")
        .build()
);

List<? extends PersonDTO> result = map.get("student");

for (PersonDTO person : result) {
    System.out.println(person.PerosnInfo());
}
```

```java
map.put(key, value); // key-value 저장
map.get(key);        // key에 연결된 value 조회
```

동일한 key로 다시 `put()`하면 value가 교체된다. key는 중복될 수 없지만 서로 다른 key의 value는 같을 수 있다.

### 실제 코드로 비교

| 자료구조 | 오늘 생성한 객체 | 데이터 추가 | 데이터 조회 | 특징 |
|---|---|---|---|---|
| List | `new ArrayList<>()` | `add(value)` | `get(index)` | 순서 O, 중복 O |
| Set | `new HashSet<>()` | `add(value)` | index 조회 불가 | 중복 X, 순서 보장 X |
| Map | `new HashMap<>()` | `put(key, value)` | `get(key)` | key-value 구조 |

### 배열과 List

| 배열 | List |
|---|---|
| 생성할 때 길이 결정 | 요소 추가에 따라 크기 변경 |
| `length` | `size()` |
| `ary[index]` | `get(index)` |

## 4. List와 DTO의 다형성

### CollectionApp.java

```java
List<PersonDTO> personList = new ArrayList<>();

StudentDTO student = StudentDTO.builder()
    .name("inspire").build();
TeacherDTO teacher = TeacherDTO.builder()
    .name("jslim").build();
ManagerDTO manager = ManagerDTO.builder()
    .name("lgcns").build();

personList.add(student);
personList.add(teacher);
personList.add(manager);
```

`StudentDTO`, `TeacherDTO`, `ManagerDTO`는 모두 `PersonDTO`를 상속하므로 `List<PersonDTO>` 하나에 저장할 수 있다.

```text
List<PersonDTO>
├── StudentDTO
├── TeacherDTO
└── ManagerDTO
```

`add(PersonDTO element)`의 매개변수에 자식 객체가 자동 업캐스팅되어 전달된다. 이전 시간에 배운 **매개변수의 다형성**이 Collection에서도 사용된 것이다.

```java
PersonDTO person = personList.get(0);
```

요소 타입이 `PersonDTO`로 지정되어 있으므로 꺼낼 때 불필요한 다운캐스팅도 필요하지 않다.

오늘 DTO에는 다음 설정도 추가되었다.

```java
@ToString(callSuper = true)
```

자식 객체의 `toString()`을 만들 때 부모인 `PersonDTO`의 필드까지 포함하라는 의미다.

## 5. Set

```java
Set<String> set = new HashSet<>();
set.add("jslim");
set.add("inspire");
set.add("lgcns");
set.add("jslim");
```

`Set`은 중복을 허용하지 않으므로 `"jslim"`은 한 번만 저장된다. `HashSet`은 저장 순서와 index를 보장하지 않는다.

```java
set.get(0); // 불가능
```

향상된 for문이나 `forEach()`로 순회할 수 있다.

```java
for (String data : set) {
    System.out.println(data);
}
```

중복 단어를 제거하는 특성 때문에 자연어 토큰이나 고유값 처리에 활용할 수 있다.

## 6. Map

```java
Map<String, List<? extends PersonDTO>> map = new HashMap<>();

map.put("student", studentList);
map.put("teacher", teacherList);
map.put("manager", managerList);
```

타입을 해석하면 다음과 같다.

```text
key   : String
value : PersonDTO 또는 자식 타입을 요소로 가진 List
```

따라서 `List<StudentDTO>`, `List<TeacherDTO>`, `List<ManagerDTO>`를 하나의 Map에 저장할 수 있다.

Map은 JSON과 형태가 비슷하지만 동일한 개념은 아니다. Map은 Java 객체이고 JSON은 데이터 표현 형식이다.

## 7. Wrapper Class와 Boxing

Collection은 객체를 저장하지만 다음 코드는 기본 타입 `int`도 추가할 수 있다.

```java
List<Integer> numbers = new ArrayList<>();
numbers.add(10);                // int → Integer
int number = numbers.get(0);   // Integer → int
```

```text
primitive type ↔ Wrapper class

int     ↔ Integer
char    ↔ Character
boolean ↔ Boolean
double  ↔ Double
```

- Autoboxing: 기본 타입을 Wrapper 객체로 자동 변환
- Unboxing: Wrapper 객체를 기본 타입으로 자동 변환

## 8. Generic

Generic은 클래스나 Collection에서 사용할 타입을 외부에서 지정하는 문법이다.

```java
List<String> list = new ArrayList<>();

list.add("String"); // 가능
list.add(10);       // 컴파일 오류
list.add(true);     // 컴파일 오류
```

장점은 다음과 같다.

- 컴파일 시점의 타입 안정성
- 잘못된 타입 저장 방지
- 불필요한 다운캐스팅 제거
- 하나의 클래스와 메서드를 여러 타입에 재사용

자주 사용하는 타입 매개변수 이름은 다음과 같다.

```text
T : Type
E : Element
K : Key
V : Value
N : Number
```

## 9. Generic 클래스 - ResponseTemplate<T>

### ResponseTemplate.java

```java
public class ResponseTemplate<T> {
    private int code;
    private String message;
    private T data;

    public ResponseTemplate(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
```

`code`와 `message`는 고정된 타입이지만 `data`는 요청에 따라 달라질 수 있다.

```java
ResponseTemplate<PersonDTO> createResponse;
ResponseTemplate<List<PersonDTO>> listResponse;
ResponseTemplate<String> messageResponse;
```

### GenericsApp.java

```java
ResponseTemplate<List<PersonDTO>> response =
    new ResponseTemplate<>(200, "ok", personList);

List<PersonDTO> list = response.getData();
```

`T`를 `List<PersonDTO>`로 지정했으므로 `data`와 `getData()`의 타입도 `List<PersonDTO>`로 결정된다.

```text
ResponseTemplate<T>
       T = List<PersonDTO>
              ↓
private List<PersonDTO> data
public List<PersonDTO> getData()
```

단일 DTO, DTO 목록, 문자열 등 다양한 데이터를 같은 응답 구조에 담을 수 있다.

## 10. Generic Wildcard

Wildcard `?`는 정확한 타입 하나가 아니라 허용 가능한 타입의 범위를 표현한다.

### `? extends T`

```java
List<? extends PersonDTO> list;
```

`PersonDTO` 또는 자식 타입의 List를 참조할 수 있다.

```java
PersonDTO person = list.get(0); // 안전하게 읽기 가능
list.add(new StudentDTO());     // 컴파일 오류
```

실제 요소 타입이 무엇인지 확정할 수 없으므로 일반적으로 새 요소를 추가할 수 없다.

### `? super T`

```java
List<? super PersonDTO> list;
```

`PersonDTO` 또는 부모 타입의 List를 참조한다. `PersonDTO`와 자식 객체를 추가할 수 있다.

```java
list.add(new PersonDTO(...));
list.add(new StudentDTO(...));
Object data = list.get(0);
```

꺼낸 값의 정확한 타입은 보장할 수 없으므로 `Object`로 읽는다.

```text
PECS
Producer Extends : 값을 제공하는 쪽은 extends
Consumer Super   : 값을 받아 저장하는 쪽은 super
```

`extends`를 완전한 읽기 전용, `super`를 완전한 쓰기 전용으로 정의하기보다 어떤 연산을 **타입 안전하게 수행할 수 있는지**로 이해해야 한다.

## 11. 함수형 인터페이스

함수형 인터페이스는 추상 메서드를 하나만 가지는 인터페이스다.

### InspireFuction.java

```java
@FunctionalInterface
public interface InspireFuction {
    int max(int x, int y);
}
```

`@FunctionalInterface`는 추상 메서드가 하나인지 컴파일러가 검사하게 한다. 함수형 인터페이스는 Lambda가 구현할 메서드의 입력과 반환 형태를 결정한다.

```text
int max(int x, int y)
↓
입력: int 2개
출력: int 1개
```

## 12. Lambda

Lambda는 함수형 인터페이스의 유일한 추상 메서드를 식으로 구현하는 문법이다.

```java
InspireFuction maxFunction =
    (x, y) -> x > y ? x : y;

InspireFuction addFunction =
    (x, y) -> x + y;
```

```java
System.out.println(maxFunction.max(100, 200)); // 200
System.out.println(addFunction.max(100, 200)); // 300
```

기본 형태는 다음과 같다.

```java
(매개변수) -> 실행문
```

실행문이 한 줄이면 중괄호와 `return`을 생략할 수 있다.

```java
(x, y) -> x + y
```

중괄호를 쓰면 반환값이 있는 경우 `return`이 필요하다.

```java
(x, y) -> {
    return x + y;
}
```

## 13. 표준 함수형 인터페이스

### Supplier - 입력 X, 반환 O

```java
Supplier<String> supplier = () -> "inspire";
String value = supplier.get();
```

값을 공급하며 실행 메서드는 `get()`이다.

### Consumer - 입력 O, 반환 X

```java
Consumer<String> consumer =
    str -> System.out.println(str.split(" ")[1]);

consumer.accept("lgcns inspire");
```

값을 출력하거나 저장하는 등 소비하며 실행 메서드는 `accept()`이다.

```java
consumer
    .andThen(System.out::println)
    .accept("lgcns inspire");
```

`andThen()`으로 Consumer 동작을 순서대로 연결할 수 있다.

### Function - 입력 O, 반환 O

```java
Function<String, Integer> function =
    str -> str.length();

int length = function.apply("lgcns inspire");
```

값을 다른 값으로 변환하며 실행 메서드는 `apply()`다.

### Predicate - 입력 O, boolean 반환

```java
Predicate<String> predicate =
    str -> str.equals("lgcns");

boolean result = predicate.test("lgcns");
```

조건을 검사하며 실행 메서드는 `test()`다. Stream의 `filter()`에서 자주 사용된다.

## 14. Method Reference

기존 메서드를 Lambda에서 그대로 호출할 때 메서드 참조로 줄일 수 있다.

```java
brands.forEach(value -> System.out.println(value));
brands.forEach(System.out::println);
```

```text
객체 또는 타입::메서드명
```

`System.out::println`은 즉시 호출하는 것이 아니라 `forEach()`가 요소마다 호출할 동작을 전달한다.

## 15. Stream API

Stream은 Collection 데이터를 선언적으로 처리하는 API다. 데이터를 직접 저장하는 자료구조는 아니다.

```java
List<String> brands = Arrays.asList(
    "samsung", "lg", "lgcns", "inspire", "camp", "6th"
);

Stream<String> stream = brands.stream();
```

```text
Collection
→ Stream 생성
→ 중간 연산 0개 이상
→ 최종 연산 1개
```

### 중간 연산

중간 연산은 데이터를 가공하고 새로운 Stream을 반환한다.

```java
filter() // 조건에 맞는 요소 선택
map()    // 요소를 다른 값으로 변환
```

### 최종 연산

최종 연산은 파이프라인을 실행하고 결과를 만든다.

```java
forEach() // 각 요소에 동작 수행
collect() // 처리 결과를 Collection 등으로 수집
```

## 16. 명령형 처리와 선언형 처리

### 명령형 처리

```java
List<String> filteringList = new ArrayList<>();

for (int i = 0; i < personList.size(); i++) {
    PersonDTO person = personList.get(i);

    if (person.getName().startsWith("j")) {
        filteringList.add(
            person.getName().toUpperCase()
        );
    }
}
```

반복, 조건, 변환, 저장 방법을 직접 작성한다.

### Stream을 이용한 선언형 처리

```java
List<String> filteringList = personList.stream()
    .filter(person -> person.getName().startsWith("j"))
    .map(person -> person.getName().toUpperCase())
    .collect(Collectors.toList());
```

```text
personList.stream()
→ filter : 이름이 j로 시작하는 객체 선택
→ map    : 이름을 대문자로 변환
→ collect: 결과를 List로 수집
```

반복 방법보다 **어떤 결과를 원하는지**를 연산 단위로 표현한다.

현재 실행 코드에도 Stream과 Lambda가 연결되어 있다.

```java
personList.stream()
    .filter(person -> person.getName().length() > 5)
    .forEach(person ->
        System.out.println(person.PerosnInfo())
    );
```

`filter()`는 `Predicate<PersonDTO>`, `forEach()`는 `Consumer<PersonDTO>` 형태의 Lambda를 전달받는다.

## 17. Stream의 일회성

Stream은 최종 연산이 끝나면 닫히므로 재사용할 수 없다.

```java
Stream<String> stream = brands.stream();
stream.forEach(System.out::println);

stream.forEach(System.out::println); // 오류
```

다시 처리하려면 Collection에서 새 Stream을 생성한다.

```java
stream = brands.stream();
stream.forEach(System.out::println);
```

Stream이 닫혀도 원본 Collection은 사라지거나 변경되지 않는다.

## 18. Stream과 병렬 처리

일반 `stream()`은 순차 Stream이다.

```java
brands.stream();
```

병렬 처리를 원할 때 별도로 병렬 Stream을 만들 수 있다.

```java
brands.parallelStream();
```

따라서 Stream이 항상 스레드 기반으로 병렬 실행되는 것은 아니다. 병렬 Stream도 데이터 크기와 작업 비용에 따라 오히려 느릴 수 있으므로 상황에 맞게 선택한다.

## 19. 백엔드 계층과 Collection

```text
Presentation Layer
→ 요청 수신과 응답 반환

Business Layer
→ 비즈니스 로직 처리

Persistence Layer
→ 데이터 저장과 조회
```

DTO는 계층 사이에서 데이터를 전달하는 객체다. 데이터베이스 레코드와 비슷한 구조를 가질 수 있지만 DTO와 테이블·레코드가 항상 일치하는 것은 아니다.

현재 프로젝트에는 Controller, Repository 같은 계층별 클래스가 완전히 구현되어 있지는 않다. 따라서 아래 내용은 `CollectionApp`이 메모리에서 DTO를 관리하는 과정을 계층의 역할에 대응시킨 것이다.

```text
Presentation 역할
CollectionApp.main()
→ 테스트 데이터 생성과 결과 출력

Business 역할
filter(), map()에 전달된 조건과 변환 로직
→ 이름 길이 검사, 대문자 변환

Persistence 역할
List<PersonDTO> personList
→ DB 대신 DTO 객체를 메모리에 저장
```

실제 코드의 실행 흐름은 다음과 같다.

```java
// 1. DTO 생성
StudentDTO student = StudentDTO.builder()
    .name("inspire")
    .build();

// 2. 메모리 Collection에 저장
List<PersonDTO> personList = new ArrayList<>();
personList.add(student);

// 3. 저장된 데이터를 조건에 따라 조회하고 출력
personList.stream()
    .filter(person -> person.getName().length() > 5)
    .forEach(person ->
        System.out.println(person.PerosnInfo())
    );
```

```text
StudentDTO 생성
→ List<PersonDTO>.add()로 저장
→ stream()으로 처리 흐름 생성
→ filter()로 조건에 맞는 DTO 선택
→ forEach()로 결과 출력
```

나중에 실제 계층 구조를 적용하면 `List<PersonDTO>`가 담당한 임시 저장 역할은 Repository와 데이터베이스로 이동하고, 필터링 같은 업무 규칙은 Service에서 처리하게 된다.

Generic은 특정 계층 전용 문법이 아니라 Collection, 응답 객체, 서비스 메서드 등 애플리케이션 전반에서 타입 안정성을 위해 사용한다.

## 20. 오늘 코드의 연결 구조

```text
StudentDTO / TeacherDTO / ManagerDTO
              ↓ 업캐스팅
        List<PersonDTO>
              ↓ stream()
        Stream<PersonDTO>
              ↓ Predicate
           filter()
              ↓ Consumer
          forEach()
```

응답 객체까지 연결하면 다음과 같다.

```text
List<PersonDTO>
       ↓ data로 전달
ResponseTemplate<List<PersonDTO>>
       ↓ getData()
List<PersonDTO>
```

## 21. 핵심 키워드 모음

| 키워드 | 핵심 의미 | 사용 코드 |
|---|---|---|
| Collection | 여러 객체를 가변적으로 저장 | `CollectionApp` |
| List | 순서 O, 중복 O | `List<PersonDTO>` |
| Set | 중복 X, 순서 보장 X | `HashSet<String>` |
| Map | key-value 쌍으로 저장 | `Map<String, List<? extends PersonDTO>>` |
| Wrapper Class | 기본 타입을 객체 타입으로 감쌈 | `int ↔ Integer` |
| Boxing / Unboxing | 기본 타입과 Wrapper 객체의 자동 변환 | `numbers.add(10)` |
| Generic | 사용할 타입을 외부에서 지정 | `List<String>`, `ResponseTemplate<T>` |
| Wildcard | 제네릭 타입의 허용 범위 표현 | `? extends`, `? super` |
| 함수형 인터페이스 | 추상 메서드가 하나인 인터페이스 | `InspireFuction` |
| Lambda | 함수형 인터페이스의 구현을 식으로 전달 | `(x, y) -> x + y` |
| Supplier | 입력 없이 값 공급 | `Supplier<String>` |
| Consumer | 입력값을 소비하고 반환하지 않음 | `Consumer<String>` |
| Function | 입력값을 다른 값으로 변환 | `Function<String, Integer>` |
| Predicate | 입력값의 조건을 검사 | `Predicate<String>` |
| Method Reference | 기존 메서드를 간결하게 전달 | `System.out::println` |
| Stream | Collection 데이터를 선언적으로 처리 | `filter`, `map`, `forEach` |
| 중간 연산 | Stream을 가공하고 Stream 반환 | `filter`, `map` |
| 최종 연산 | Stream 처리를 실행하고 결과 생성 | `forEach`, `collect` |

## 22. 현재 코드에서 확인할 부분

- `StreamApp`에서 최종 연산 후 `brands.stream()`을 다시 호출한 것은 Stream의 일회성을 보여준다.
- `List<ManagerDTO> ManagerList`는 변수명을 `managerList`로 작성하는 것이 Java 관례에 맞다.
- `InspireFuction`은 `InspireFunction`의 오타로 보인다.
- `PerosnInfo()`는 `PersonInfo()`의 오타로 보인다.
- 사용하지 않는 import는 정리할 수 있다.

## 23. 최종 핵심

- **Collection**: 여러 객체를 가변적으로 저장하고 관리한다.
- **List**: 순서와 중복이 모두 존재을 실행하고 결과를 만든다.
- **Stream 일회성**: 최종 연산을 수행한한다.
- **Set**: 중복을 허용하지 않고 순서를 보장하지 않는다.
- **Map**: key-value 구조로 데이터를 저장한다.
- **Wrapper Class**: 기본 타입을 객체처럼 사용할 수 있게 한다.
- **Generic**: 타입을 지정해 컴파일 시점의 안정성을 확보한다.
- **Wildcard**: 제네릭이 허용할 타입의 범위를 표현한다.
- **함수형 인터페이스**: Lambda가 구현할 하나의 추상 메서드를 정의한다.
- **Lambda**: 처리할 동작을 간결한 식으로 전달한다.
- **Method Reference**: 기존 메서드를 `::` 문법으로 전달한다.
- **Stream**: Collection 데이터를 선언적인 연산 흐름으로 처리한다.
- **중간 연산**: `filter`, `map`처럼 Stream을 가공한다.
- **최종 연산**: `forEach`, `collect`처럼 연산 Stream은 다시 사용할 수 없다.
- **다형성과 Generic의 연결**: `List<PersonDTO>` 하나로 여러 자식 DTO를 타입 안전하게 관리한다.
