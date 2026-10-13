# BE 3일차 — 배열, 계층 구조, 상속과 다형성

## 1. 오늘 학습한 내용

- 배열(Array)의 특징과 사용법
- 객체 배열과 `null`
- 일반 `for`문과 향상된 `for`문
- App → Service → Repository 계층 구조
- DTO와 Lombok Builder
- 객체지향의 상속
- `super()`와 부모 생성자
- `private`, `protected`, `final`
- 변수 타입의 다형성
- 업캐스팅과 다운캐스팅
- `instanceof`를 이용한 실제 타입 확인
- 메서드 오버라이딩과 메서드의 다형성
- `for`, `while`, `do-while` 반복문을 이용한 숫자 맞히기 게임

> 매개변수의 다형성은 오늘 학습 범위에서 제외한다.

---

# 배열(Array)

## 2. 배열이란?

배열은 같은 타입의 여러 데이터를 하나로 묶어서 관리하는 참조 타입이다.

```java
int[] ary = new int[10];
```

배열의 특징은 다음과 같다.

- 한 배열에는 동일한 타입의 데이터만 저장할 수 있다.
- 배열을 생성할 때 길이가 결정된다.
- 실행 중에 배열의 길이가 자동으로 늘어나지 않는다.
- 인덱스는 `0`부터 시작한다.
- 배열의 길이는 `length` 속성으로 확인한다.
- 기본형뿐 아니라 객체 타입도 배열로 만들 수 있다.

```java
ary[0] = 10;
ary[1] = 20;

System.out.println(ary.length); // 10
```

길이가 10인 배열에서 사용할 수 있는 인덱스는 `0~9`이다.

---

## 3. 배열의 기본값

배열을 생성하면 각 칸은 데이터 타입에 맞는 기본값으로 초기화된다.

```java
int[] numbers = new int[3];
// [0, 0, 0]

boolean[] flags = new boolean[3];
// [false, false, false]

BlogResponseDTO[] blogs = new BlogResponseDTO[3];
// [null, null, null]
```

객체 배열을 만들었다고 각 칸의 객체가 자동으로 생성되는 것은 아니다. 객체를 참조할 수 있는 공간만 생기며, 초기값은 `null`이다.

```java
BlogResponseDTO[] blogsAry = new BlogResponseDTO[10];
```

```text
blogsAry
├─ [0] → null
├─ [1] → null
├─ [2] → null
└─ ...
```

---

## 4. 객체 배열에 객체 저장하기

`AryApp`에서는 `BlogResponseDTO` 객체를 생성한 뒤 배열에 저장했다.

```java
BlogResponseDTO[] blogsAry = new BlogResponseDTO[10];

BlogResponseDTO response = BlogResponseDTO.builder()
    .status(200)
    .message("good")
    .build();

blogsAry[0] = response;
blogsAry[1] = response;
blogsAry[2] = response;
```

세 칸에는 서로 다른 객체가 아니라 동일한 `response` 객체의 참조가 저장된다.

```text
blogsAry[0] ─┐
blogsAry[1] ─┼─→ response 객체
blogsAry[2] ─┘
```

---

## 5. 배열 순회

### 일반 `for`문

인덱스가 필요하거나 배열의 특정 위치를 제어해야 할 때 사용한다.

```java
for (int i = 0; i < blogsAry.length; i++) {
    BlogResponseDTO data = blogsAry[i];

    if (data == null) {
        break;
    }

    System.out.println(data.getMessage());
}
```

현재 배열에는 `0~2`번에만 객체가 들어 있다. `3`번부터는 `null`이므로 `break`를 이용해 반복문을 종료한다.

### 향상된 `for`문

인덱스가 필요 없고 배열의 값을 처음부터 끝까지 순서대로 꺼낼 때 사용한다.

```java
for (BlogResponseDTO data : blogsAry) {
    if (data == null) {
        break;
    }

    System.out.println(data.getMessage());
}
```

---

## 6. 배열의 한계

배열은 생성할 때 길이가 고정된다.

```java
BlogResponseDTO[] blogs = new BlogResponseDTO[10];
```

실행 중 데이터가 많아져도 배열의 길이는 자동으로 늘어나지 않는다. 이러한 한계를 보완하기 위해 이후에는 Collection API를 사용한다.

```text
배열
└─ 길이가 고정됨

Collection API
└─ 데이터 개수에 따라 크기를 유연하게 관리

Stream API
└─ Collection의 데이터를 필터링·변환·가공
```

대표적인 Collection에는 `List`, `Set`, `Map`이 있다.

---

# DTO와 애플리케이션 계층 구조

## 7. DTO와 Lombok Builder

DTO는 계층 사이에서 데이터를 전달하는 객체이다.

```java
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BlogResponseDTO {
    private int status;
    private String message;
}
```

Lombok 어노테이션의 역할은 다음과 같다.

| 어노테이션 | 역할 |
|---|---|
| `@Builder` | 빌더 방식의 객체 생성 지원 |
| `@NoArgsConstructor` | 매개변수가 없는 기본 생성자 생성 |
| `@AllArgsConstructor` | 모든 필드를 받는 생성자 생성 |
| `@Getter` | getter 생성 |
| `@Setter` | setter 생성 |

빌더는 다음 순서로 실행된다.

```java
BlogResponseDTO.builder()
    .status(200)
    .message("good")
    .build();
```

```text
builder()
→ 빌더 객체 생성

status(), message()
→ 빌더에 값 저장

build()
→ 실제 BlogResponseDTO 객체 생성
```

`build()`를 호출한 뒤에는 빌더가 아니라 완성된 `BlogResponseDTO` 객체가 반환된다.

---

## 8. App → Service → Repository

블로그 목록 실습에서는 다음과 같은 계층 구조를 만들었다.

```text
AryApp
  ↓
BlogService
  ↓
BlogRepository
  ↓
BlogResponseDTO[]
```

각 계층의 역할은 다음과 같다.

| 계층 | 역할 |
|---|---|
| App 또는 Controller | 사용자의 요청을 받고 Service를 호출 |
| Service | 비즈니스 로직 처리 |
| Repository | 데이터 접근 및 CRUD 처리 |
| Domain/DTO | 계층 사이에서 전달할 데이터 보관 |

실제 웹 애플리케이션에서는 다음과 같은 흐름으로 확장된다.

```text
React
  ↓ 요청
Controller
  ↓
Service
  ↓
Repository
  ↓
Database
```

응답은 반대 방향으로 돌아온다.

```text
Database
  ↓ 데이터
Repository
  ↓
Service
  ↓
Controller
  ↓ JSON
React
```

---

## 9. 현재 블로그 목록 조회 흐름

`AryApp`이 `BlogService`를 생성하고 `blogs()`를 호출한다.

```java
BlogResponseDTO[] resultAry =
    BlogService.builder().build().blogs();
```

`BlogService`는 비즈니스 로직을 담당하고 `BlogRepository`를 호출한다.

```java
public BlogResponseDTO[] blogs() {
    System.out.println("businesslogic blogs()");

    return BlogRepository.builder()
        .build()
        .blogs();
}
```

`BlogRepository`는 자신이 가진 `list` 배열을 반환한다.

```java
private BlogResponseDTO[] list = new BlogResponseDTO[10];

public BlogResponseDTO[] blogs() {
    System.out.println("BlogRepository blogs()");
    return list;
}
```

전체 호출과 반환 흐름은 다음과 같다.

```text
AryApp
  → BlogService.blogs()
  → BlogRepository.blogs()
  → list 반환
  → BlogService가 배열 반환
  → AryApp의 resultAry에 저장
```

현재 `list`에는 DTO를 넣지 않았으므로 길이는 10이지만 모든 값이 `null`이다.

```text
[null, null, null, null, null, null, null, null, null, null]
```

---

# 객체지향과 상속

## 10. 객체지향의 주요 특징

- 은닉화
- 상속
- 다형성
- 추상화

오늘 실습에서는 상속과 다형성을 코드로 확인했다.

---

## 11. 상속 계층

상속은 기존 클래스의 공통 속성과 기능을 새로운 클래스가 물려받는 것이다.

현재 실습 코드의 상속 계층은 다음과 같다.

```text
Object
  └─ PersonDTO
       ├─ StudentDTO
       │    └─ ssn
       ├─ TeacherDTO
       │    └─ subject
       └─ ManagerDTO
            └─ dept
```

Java의 모든 클래스는 명시적인 부모가 없다면 최상위 클래스인 `Object`를 암묵적으로 상속한다.

부모 클래스인 `PersonDTO`는 사람들의 공통 정보를 관리한다.

```java
public class PersonDTO {
    private String name;
    private int age;
    private String address;
}
```

각 자식 클래스는 `extends`를 사용해 `PersonDTO`를 상속하고 고유한 필드를 추가한다.

```java
public class StudentDTO extends PersonDTO {
    private String ssn;
}

public class TeacherDTO extends PersonDTO {
    private String subject;
}

public class ManagerDTO extends PersonDTO {
    private String dept;
}
```

---

## 12. 객체 내부의 부모 영역과 자식 영역

`StudentDTO` 객체를 만들면 `StudentDTO`의 고유 영역뿐 아니라 부모인 `PersonDTO` 영역도 함께 만들어진다.

```text
StudentDTO 객체
├─ PersonDTO 영역
│  ├─ name
│  ├─ age
│  └─ address
└─ StudentDTO 영역
   └─ ssn
```

```text
TeacherDTO 객체
├─ PersonDTO 영역
│  ├─ name
│  ├─ age
│  └─ address
└─ TeacherDTO 영역
   └─ subject
```

```text
ManagerDTO 객체
├─ PersonDTO 영역
│  ├─ name
│  ├─ age
│  └─ address
└─ ManagerDTO 영역
   └─ dept
```

상속을 사용하면 여러 클래스에 중복되는 필드와 메서드를 부모 클래스로 분리하여 관리할 수 있다.

---

## 13. `is-a` 관계

상속은 `is-a` 관계가 성립할 때 사용한다.

```text
StudentDTO is a PersonDTO
TeacherDTO is a PersonDTO
ManagerDTO is a PersonDTO
```

하지만 반대 방향은 항상 성립하지 않는다.

```text
PersonDTO is a StudentDTO → 항상 참이 아님
```

상속 계층에서 위로 갈수록 공통적이고 추상적이며, 아래로 갈수록 구체적이다.

```text
PersonDTO       ← 공통적·추상적
    ↓
StudentDTO      ← 고유하고 구체적
```

---

## 14. `super()`와 부모 생성자

자식 객체를 생성할 때는 부모 영역도 함께 초기화해야 한다.

```java
public StudentDTO(
    String name,
    int age,
    String address,
    String ssn
) {
    super(name, age, address);
    this.ssn = ssn;
}
```

`super(name, age, address)`는 부모인 `PersonDTO`의 생성자를 호출한다.

```java
public PersonDTO(String name, int age, String address) {
    this.name = name;
    this.age = age;
    this.address = address;
}
```

실행 순서는 다음과 같다.

```text
new StudentDTO(...)
  ↓
PersonDTO 생성자 실행
  ↓
name, age, address 초기화
  ↓
StudentDTO 생성자 실행
  ↓
ssn 초기화
```

자식 생성자의 첫 줄에서 부모 생성자를 직접 호출하지 않으면 컴파일러가 기본적으로 `super()`를 넣는다. 부모에게 기본 생성자가 없고 매개변수 생성자만 있다면 자식 생성자에서 적절한 `super(...)`를 명시해야 한다.

---

## 15. 접근 제어와 상속

자식 객체는 부모의 구성 요소를 포함하지만 부모의 `private` 필드에는 직접 접근할 수 없다.

```java
public class PersonDTO {
    private String name;
}
```

다음과 같은 직접 접근은 불가능하다.

```java
super.name = "서정운"; // 컴파일 오류
```

대신 부모 생성자나 부모가 제공하는 getter/setter를 사용한다.

```java
super(name, age, address);

setName("서정운");
String name = getName();
```

`protected`는 같은 패키지와 상속 관계의 자식 클래스에서 접근할 수 있도록 허용하는 접근 제어자이다. 다만 필드는 가능하면 `private`으로 보호하고 필요한 동작을 메서드로 제공하는 방식이 객체의 상태를 안전하게 관리하기 좋다.

---

## 16. 상속과 `final`

`final`은 선언되는 위치에 따라 제한하는 대상이 달라진다.

### `final` 메서드

```java
public class PersonDTO {
    public final String getName() {
        return name;
    }
}
```

`final` 메서드도 자식에게 상속되며 자식 객체가 사용할 수 있다. 다만 자식 클래스에서 오버라이딩할 수 없다.

```java
public class StudentDTO extends PersonDTO {
    @Override
    public String getName() { // 컴파일 오류
        return "학생";
    }
}
```

```text
final 메서드
→ 자식에게 상속됨
→ 자식이 사용할 수 있음
→ 자식에서 오버라이딩할 수 없음
```

### `final` 클래스

클래스 자체에 `final`을 붙이면 다른 클래스가 해당 클래스를 상속할 수 없다.

```java
public final class PersonDTO {
}
```

```java
public class StudentDTO extends PersonDTO {
    // 컴파일 오류
}
```

```text
final class
→ 해당 클래스를 부모로 사용할 수 없음
→ 자식 클래스를 만들 수 없음
```

### `final` 비교

| 선언 | 의미 |
|---|---|
| `final class` | 해당 클래스를 상속할 수 없음 |
| `final method` | 해당 메서드를 오버라이딩할 수 없음 |
| `final variable` | 초기화 후 값을 다시 대입할 수 없음 |

현재의 `PersonDTO → StudentDTO/TeacherDTO/ManagerDTO` 상속 계층을 유지하려면 `PersonDTO`를 `final class`로 선언하면 안 된다.

---

# 오늘 학습한 다형성

## 17. 다형성의 구분

오늘은 다음 두 가지 다형성을 실습했다.

```text
다형성
├─ 변수 타입의 다형성
└─ 메서드의 다형성
```

---

## 18. 변수 타입의 다형성

부모 타입 변수 하나로 여러 종류의 자식 객체를 참조할 수 있다.

```java
PersonDTO student =
    new StudentDTO("서정운", 20, "서울", "2026");

PersonDTO teacher =
    new TeacherDTO("이하연", 20, "서울", "React");

PersonDTO manager =
    new ManagerDTO("이세연", 20, "서울", "교육팀");
```

```text
PersonDTO student → StudentDTO 객체
PersonDTO teacher → TeacherDTO 객체
PersonDTO manager → ManagerDTO 객체
```

자식 객체를 부모 타입 변수로 참조하는 것을 업캐스팅이라고 한다.

```java
PersonDTO manager = new ManagerDTO(...);
```

이때 변수의 선언 타입과 실제 객체 타입을 구분해야 한다.

```text
변수의 선언 타입: PersonDTO
실제 객체 타입: ManagerDTO
```

컴파일 시점에는 변수의 선언 타입을 기준으로 접근 가능한 메서드를 검사한다.

```java
manager.getName();    // 가능: PersonDTO에 정의됨
manager.getAge();     // 가능: PersonDTO에 정의됨
manager.getAddress(); // 가능: PersonDTO에 정의됨
manager.getDept();    // 컴파일 오류: ManagerDTO에만 정의됨
```

---

## 19. 부모 타입 배열과 자식 객체

부모 타입 배열에는 부모를 상속한 여러 종류의 자식 객체를 함께 저장할 수 있다.

```java
PersonDTO[] ary = new PersonDTO[3];

ary[0] = new StudentDTO(
    "서정운", 20, "서울", "2026"
);

ary[1] = new ManagerDTO(
    "이세연", 20, "서울", "교육팀"
);

ary[2] = new TeacherDTO(
    "이하연", 20, "서울", "React"
);
```

```text
PersonDTO[] ary
├─ [0] → StudentDTO 객체
├─ [1] → ManagerDTO 객체
└─ [2] → TeacherDTO 객체
```

중요한 점은 `StudentDTO` 객체가 배열에 들어가는 순간 `PersonDTO` 객체로 바뀌는 것이 아니라는 것이다.

```text
배열이 관리하는 타입: PersonDTO
실제로 생성된 객체: StudentDTO
```

포장지만 부모 타입이고 내용물은 여전히 자식 객체라고 이해할 수 있다.

```text
PersonDTO 포장지
└─ 실제 내용물: StudentDTO 객체

배열이 `PersonDTO[]`로 선언되었기 때문에 값을 꺼낼 때는 기본적으로 `PersonDTO` 타입으로 받는다.

```java
PersonDTO per01 = ary[0];
```
```

```text
per01의 선언 타입: PersonDTO
per01의 실제 객체: StudentDTO
```

부모의 메서드는 바로 호출할 수 있다.

```java
per01.getName();
per01.getAge();
per01.getAddress();
per01.PerosnInfo();
```

하지만 `StudentDTO`에만 존재하는 `getSsn()`은 바로 호출할 수 없다.

```java
per01.getSsn(); // 컴파일 오류
```

---

## 20. 다운캐스팅

부모 타입 변수에서 자식의 고유 기능을 사용하려면 실제 자식 타입으로 형변환해야 한다.

```java
System.out.println(
    ((StudentDTO) per01).getSsn()
);
```

이를 다운캐스팅이라고 한다.

다운캐스팅은 변수의 선언 타입이 아니라 실제 객체 타입과 일치해야 한다.

```java
PersonDTO person = new StudentDTO(...);

((StudentDTO) person).getSsn();     // 가능
((TeacherDTO) person).getSubject(); // ClassCastException
```

상속 계층을 보면 이유를 알 수 있다.

```text
       PersonDTO
       /       \
StudentDTO   TeacherDTO
```

`StudentDTO`와 `TeacherDTO`는 형제 관계이므로 서로 형변환할 수 없다.

다음 코드에서 `per01`의 실제 객체는 `ary[0]`에 저장된 `StudentDTO`이다.

```java
PersonDTO per01 = ary[0];
```

따라서 다음 캐스팅은 잘못되었다.

```java
System.out.println(
    ((TeacherDTO) per01).getSubject()
); // ClassCastException
```

실제 타입에 맞게 `StudentDTO`로 변환해야 한다.

```java
System.out.println(
    ((StudentDTO) per01).getSsn()
);
```

교사 정보가 필요하다면 `TeacherDTO`가 들어 있는 `ary[2]`를 꺼내야 한다.

```java
PersonDTO per03 = ary[2];

System.out.println(
    ((TeacherDTO) per03).getSubject()
);
```

---

## 21. `instanceof`

`instanceof`는 객체의 실제 타입을 실행 시점에 확인한다.

```java
PersonDTO per = ary[0];

if (per instanceof StudentDTO) {
    System.out.println(
        ((StudentDTO) per).getSsn()
    );
}
```

부모 타입 배열에 서로 다른 자식 객체가 섞여 있을 때 안전하게 다운캐스팅할 수 있다.

```java
for (int i = 0; i < ary.length; i++) {
    PersonDTO per = ary[i];

    if (per instanceof StudentDTO) {
        System.out.println(
            ((StudentDTO) per).getSsn()
        );
    }

    if (per instanceof ManagerDTO) {
        System.out.println(
            ((ManagerDTO) per).getDept()
        );
    }

    if (per instanceof TeacherDTO) {
        System.out.println(
            ((TeacherDTO) per).getSubject()
        );
    }
}
```

```text
PersonDTO[]에서 객체를 꺼냄
  ↓
PersonDTO 타입으로 받음
  ↓
instanceof로 실제 객체 타입 확인
  ↓
확인된 자식 타입으로 다운캐스팅
  ↓
자식 고유 메서드 호출
```

---

## 22. 메서드 오버라이딩

오버라이딩은 상속 관계에서 부모에게 정의된 메서드를 자식이 같은 형태로 다시 구현하는 것이다.

부모인 `PersonDTO`는 공통 정보를 반환한다.

```java
public String PerosnInfo() {
    return "name=" + name
        + ", age=" + age
        + ", address=" + address;
}
```

`StudentDTO`는 부모의 정보에 `ssn`을 추가한다.

```java
@Override
public String PerosnInfo() {
    return super.PerosnInfo()
        + ", ssn=" + ssn;
}
```

`TeacherDTO`는 `subject`를 추가한다.

```java
@Override
public String PerosnInfo() {
    return super.PerosnInfo()
        + ", subject=" + subject;
}
```

`ManagerDTO`는 `dept`를 추가한다.

```java
@Override
public String PerosnInfo() {
    return super.PerosnInfo()
        + ", dept=" + dept;
}
```

`@Override`를 붙이면 컴파일러가 부모 메서드를 올바르게 재정의했는지 검사해 준다.

---

## 23. 메서드의 다형성

부모 타입 변수로 메서드를 호출해도 실제 객체가 오버라이딩한 메서드가 실행된다.

```java
for (int i = 0; i < ary.length; i++) {
    PersonDTO per = ary[i];
    System.out.println(per.PerosnInfo());
}
```

호출 코드는 모두 같다.

```java
per.PerosnInfo();
```

하지만 실행되는 메서드는 실제 객체에 따라 달라진다.

```text
ary[0]의 실제 객체: StudentDTO
→ StudentDTO.PerosnInfo()

ary[1]의 실제 객체: ManagerDTO
→ ManagerDTO.PerosnInfo()

ary[2]의 실제 객체: TeacherDTO
→ TeacherDTO.PerosnInfo()
```

```text
PersonDTO 타입으로 PerosnInfo() 호출
  ↓
실행 시점에 실제 객체 타입 확인
  ↓
해당 자식이 오버라이딩한 메서드 실행
```

이처럼 실행 시점에 실제 객체를 기준으로 호출할 메서드가 결정되는 것을 동적 바인딩이라고 한다.

메서드의 다형성을 이용하면 자식별로 직접 캐스팅하지 않아도 각 객체에 맞는 동작을 실행할 수 있다.

---

## 24. `super.메서드()`와 재귀 호출

자식의 오버라이딩 메서드에서 부모 구현을 사용하려면 `super`를 붙인다.

```java
@Override
public String PerosnInfo() {
    return super.PerosnInfo()
        + ", ssn=" + ssn;
}
```

다음과 같이 `this`를 사용하면 현재 메서드가 자기 자신을 계속 호출한다.

```java
return this.PerosnInfo() + ", ssn=" + ssn;
```

```text
PerosnInfo()
  → PerosnInfo()
    → PerosnInfo()
      → ...
      → StackOverflowError
```

부모의 메서드를 호출하려는 목적이라면 `super.PerosnInfo()`를 사용해야 한다.

---

# 최종 정리

## 28. 배열과 계층 구조의 연결

```text
BlogResponseDTO 객체 생성
  ↓
BlogResponseDTO[]에 저장
  ↓
AryApp에서 BlogService 호출
  ↓
BlogService에서 BlogRepository 호출
  ↓
BlogRepository가 DTO 배열 반환
```

## 29. 상속과 다형성의 연결

```text
Object
  └─ PersonDTO
       ├─ StudentDTO
       ├─ TeacherDTO
       └─ ManagerDTO
```

```text
자식 객체 생성
  ↓
부모 타입 변수 또는 배열에 저장(업캐스팅)
  ↓
실제 객체 타입은 그대로 유지
  ↓
부모에게 정의된 기능은 바로 사용
  ↓
자식 고유 기능은 instanceof 확인 후 다운캐스팅
```

오버라이딩된 공통 메서드를 호출하는 경우에는 다음과 같다.

```text
PersonDTO 타입으로 PerosnInfo() 호출
  ↓
실제 자식 객체가 오버라이딩한 메서드 실행
  ↓
메서드의 다형성
```

## 30. 오늘의 핵심 문장

> 배열과 계층 구조로 여러 객체를 관리하고, 상속으로 공통 요소를 부모 클래스로 묶은 다음, 변수 타입과 메서드의 다형성을 이용해 서로 다른 자식 객체를 하나의 부모 타입으로 처리하는 방법을 학습했다.

> 자식 객체를 부모 타입 배열에 저장해도 실제 객체 타입은 유지된다. 다만 부모 타입으로 꺼내기 때문에 자식 고유 기능을 사용하려면 실제 타입을 확인하고 다운캐스팅해야 한다.

> `final` 메서드는 오버라이딩을 막고, `final` 클래스는 상속 자체를 막는다.
