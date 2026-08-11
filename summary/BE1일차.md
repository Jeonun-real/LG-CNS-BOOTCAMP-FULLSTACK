# BE 1일차 — Java 핵심 문법

## 1. Java의 실행 구조

Java 소스 코드는 다음 순서로 실행된다.

```text
App.java ── javac(컴파일) ──> App.class ── java(JVM에서 실행) ──> 결과
```

- **JDK**: Java 개발 도구 모음. 컴파일러(`javac`), 실행 도구(`java`), JRE 등을 포함한다.
- **JRE**: Java 프로그램 실행에 필요한 환경이다.
- **JVM**: `.class`의 바이트코드를 운영체제에 맞게 실행한다.
- **`javac`**: `.java`를 `.class`로 컴파일한다.
- **`java`**: JVM을 구동하여 컴파일된 클래스를 실행한다.

Java는 운영체제가 아닌 JVM을 대상으로 실행되므로, 운영체제마다 적절한 JVM만 설치되어 있으면 동일한 바이트코드를 실행할 수 있다.

현재 프로젝트에서는 `src`에 소스 코드가 있고, 컴파일 결과인 `.class` 파일은 `bin`에 생성된다.

### VS Code에서 Java 프로젝트 생성

이번 일반 Java 프로젝트는 VS Code의 명령 팔레트에서 생성했다.

1. `Ctrl + Shift + P`로 명령 팔레트를 연다.
2. `Java: Create Java Project`를 검색해 실행한다.
3. `No build tools`를 선택한다.
4. 프로젝트를 생성할 상위 폴더를 선택한다.
5. 프로젝트 이름을 입력한다.
6. 생성된 프로젝트 폴더를 VS Code로 연다.

프로젝트가 생성되면 기본적으로 `src`, `lib`, `bin`, `.vscode/settings.json`, `App.java` 등이 구성된다.

## 2. 가장 중요한 개념: 클래스와 인스턴스

### 클래스는 설계도다

클래스는 어떤 객체가 가져야 할 **상태(필드)**와 **동작(메서드)**을 정의한 설계도다. 클래스를 작성한 것만으로 실제 객체가 생기는 것은 아니다.

```java
public class Car {
    private String brand; // 상태
    private String model; // 상태

    public String carInfo() { // 동작
        return brand + "\t" + model;
    }
}
```

`Car` 클래스는 자동차 객체가 `brand`, `model`이라는 상태와 `carInfo()`라는 동작을 가진다고 정의한다.

### 인스턴스는 설계도로 만든 실체다

클래스를 바탕으로 메모리에 실제 생성된 객체를 **인스턴스**라고 한다.

```java
Car benz = new Car("Benz", "c200");
```

이 코드는 세 부분으로 나누어 이해해야 한다.

```text
Car        benz          = new Car("Benz", "c200");
클래스 타입  참조 변수       힙에 Car 인스턴스 생성
```

- `Car`: 참조 변수가 가리킬 수 있는 객체의 타입이다.
- `new Car(...)`: 생성자를 호출하여 힙 메모리에 인스턴스를 만든다.
- `benz`: 생성된 인스턴스를 가리키는 참조를 저장한다.

정확히 말하면 `benz` 변수 안에 객체 전체가 들어가는 것이 아니라, 생성된 객체를 찾아갈 수 있는 **참조값**이 들어간다.

```text
참조 변수                         힙 메모리의 인스턴스

benz ──────────────────────────> Car 인스턴스
                                  brand = "Benz"
                                  model = "c200"
```

### 하나의 클래스로 여러 인스턴스를 만들 수 있다

```java
Car benz = new Car("Benz", "c200");
Car bmw = new Car("BMW", "520i");
```

`benz`와 `bmw`는 같은 `Car` 클래스로 만들었지만 서로 다른 인스턴스다. 따라서 각 객체의 필드값은 독립적으로 유지된다.

그 이유는 `new`를 호출할 때마다 JVM이 힙 메모리에 **새로운 독립 공간을 할당**하고, 그 공간에 각각의 객체를 생성하기 때문이다. 한 객체의 필드를 변경해도 다른 객체의 필드에는 영향을 주지 않는다.

```text
benz ──> Car { brand: "Benz", model: "c200" }
bmw  ──> Car { brand: "BMW",  model: "520i" }
```

클래스는 하나지만 인스턴스는 필요한 만큼 생성할 수 있다는 점이 핵심이다.

```java
benz.setBrand("Mercedes-Benz");

System.out.println(benz.getBrand()); // Mercedes-Benz
System.out.println(bmw.getBrand());  // BMW
```

`benz`의 상태를 바꾸어도 `bmw`는 별도 메모리 공간에 생성된 객체이므로 영향을 받지 않는다.

### 객체와 인스턴스의 표현 차이

- **객체(object)**: 속성과 동작을 가진 대상을 넓게 부르는 말이다.
- **인스턴스(instance)**: 특정 클래스를 기반으로 실제 생성된 객체임을 강조하는 말이다.
- `benz는 객체다`와 `benz가 가리키는 객체는 Car의 인스턴스다`는 모두 자연스러운 표현이다.

### 참조 변수가 없거나 같은 객체를 가리키는 경우

```java
Car car = null; // 현재 가리키는 인스턴스가 없음
```

`null`인 참조 변수로 메서드나 필드에 접근하면 `NullPointerException`이 발생한다.

```java
Car first = new Car("Benz", "c200");
Car second = first;
```

위 코드에서는 인스턴스를 두 개 만든 것이 아니다. `first`와 `second`가 **동일한 인스턴스 하나**를 가리킨다. 따라서 한 참조를 통해 객체의 상태를 바꾸면 다른 참조에서 조회해도 바뀐 상태가 보인다.

### `main` 메서드는 프로그램의 

인스턴스를 생성하고 사용하는 코드는 현재 예제처럼 `main` 메서드에서 실행할 수 있다.

```java시작점이다
public class App {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

- Java 프로그램은 일반적으로 `main` 메서드에서 시작한다.
- `public`: JVM이 어디서든 접근할 수 있다.
- `static`: 객체를 만들지 않고 클래스 이름을 통해 실행할 수 있다.
- `void`: 반환값이 없다.
- `String[] args`: 실행할 때 전달받는 문자열 인자 배열이다.
- `System`과 `String`은 `java.lang` 패키지에 있어 별도 `import`가 필요 없다.
- `main`이 있는 클래스도 내부에서 다른 객체를 생성할 수 있다.

클래스 이름은 일반적으로 대문자로 시작하는 PascalCase를 사용하며, `public class`의 이름과 파일 이름은 같아야 한다.

## 3. 변수와 자료형

### 기본형(primitive type)

Java의 기본형은 총 8개다.

| 분류 | 자료형 | 예시 |
|---|---|---|
| 정수 | `byte`, `short`, `int`, `long` | `int age = 20;` |
| 실수 | `float`, `double` | `double height = 178.0;` |
| 문자 | `char` | `char gender = 'm';` |
| 논리 | `boolean` | `boolean isMarriage = true;` |

```java
String name = "서정운";
int age = 20;
double height = 178.0;
char gender = 'm';
boolean isMarriage = true;
```

`Strinng`은 기본형이 아니라 **참조형 클래스**다. 문자열 리터럴을 간편하게 대입할 수 있어 기본형처럼 보일 뿐이다.

### 변수의 위치와 범위

```java
public class Teacher {
    private String name; // 필드(멤버 변수)

    public void printName() {
        String message = "이름: " + name; // 지역 변수
        System.out.printl(message);
    }
}
```

- **필드**: 클래스 블록에 선언하며 객체의 상태를 나타낸다.
- **지역 변수**: 메서드나 블록 안에 선언하며 해당 블록에서만 사용할 수 있다.
- 지역 변수에는 `public`, `private` 같은 접근 제한자를 붙이지 않는다.

## 4. 형 변환(casting)

숫자형의 일반적인 크기 관계는 다음과 같다.

```text
byte → short → int → long → float → double
          char → int
```

작은 타입에서 큰 타입으로 변환하는 **자동 형 변환**은 보통 생략할 수 있다.

```java
int number = 10;
double result = number; // int → double 자동 변환
```

큰 타입에서 작은 타입으로 변환하는 **강제 형 변환**은 타입을 명시해야 하며, 값 손실이 발생할 수 있다.

```java
byte x = 10;
byte y = 10;
byte sum = (byte) (x + y);
```

`byte + byte`의 연산 결과는 `int`로 승격되므로 다시 `byte`에 넣으려면 캐스팅해야 한다.

## 5. Wrapper 클래스와 오토박싱

각 기본형에는 대응되는 참조형 Wrapper 클래스가 있다.

```text
int ↔ Integer
long ↔ Long
double ↔ Double
boolean ↔ Boolean
char ↔ Character
```

```java
Integer number = 10;              // 오토박싱: int → Integer
System.out.println(10 + number);  // 언박싱: Integer → int
```

- **박싱**: 기본형을 Wrapper 객체로 변환한다.
- **언박싱**: Wrapper 객체를 기본형으로 변환한다.
- 컴파일러가 자동으로 처리하는 것을 오토박싱·오토언박싱이라고 한다.

## 6. 패키지와 `import`

패키지는 관련 클래스를 묶고 동일한 클래스 이름의 충돌을 방지한다.

```java
package features.var;

public class Teacher {
}
```

다른 패키지의 클래스를 사용할 때는 `import`한다.

```java
import features.var.Teacher;

Teacher teacher = new Teacher();
```

좋은 패키지와 클래스 설계는 관련 기능끼리 높은 **응집도**를 유지하고, 서로 불필요하게 의존하는 **결합도**를 낮춘다.

## 7. 인스턴스의 구성요소 사용

```java
Teacher teacher = new Teacher();
```

- `Teacher` 클래스는 객체의 구조와 기능을 정의한다.
- `new Teacher()`가 실제 인스턴스를 생성한다.
- `teacher` 참조 변수를 통해 해당 인스턴스에 접근한다.

인스턴스가 생성된 뒤에는 참조 변수와 `.` 연산자로 해당 인스턴스의 필드와 메서드에 접근한다.

```java
teacher.setName("서정운");
String name = teacher.getName();
```

여기서 `setName()`과 `getName()`은 클래스 파일에 한 번 정의되지만, 호출될 때는 `teacher`가 가리키는 특정 인스턴스의 `name` 필드를 대상으로 동작한다.

## 8. 생성자와 오버로딩

생성자는 객체를 생성하면서 필드를 초기화하는 특별한 구문이다.

```java
public class Car {
    private String brand;
    private String model;

    public Car() {
    }

    public Car(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }
}
```

생성자의 특징은 다음과 같다.

- 클래스 이름과 동일하다.
- 반환 타입을 작성하지 않는다. `void`도 쓰지 않는다.
- `new` 연산자 뒤에서 호출한다.
- 생성자를 하나도 선언하지 않으면 컴파일러가 매개변수 없는 기본 생성자를 제공한다.
- 생성자를 하나라도 직접 선언하면 컴파일러는 기본 생성자를 자동으로 만들지 않는다.

매개변수의 개수나 타입을 다르게 하여 같은 이름의 생성자를 여러 개 선언하는 것을 **생성자 오버로딩**이라고 한다.

```java
Car car = new Car();
Car benz = new Car("Benz", "c200");
```

두 코드는 서로 다른 오버로딩 생성자를 사용하지만, 더 중요한 판단 기준은 `new`의 호출 횟수다. `new`가 두 번 호출되었으므로 힙에 서로 다른 `Car` 인스턴스 두 개가 생성된다.

```text
car  ──> Car { brand: null,   model: null }
benz ──> Car { brand: "Benz", model: "c200" }
```

즉, **생성자 오버로딩은 객체를 생성하는 방법을 여러 개 제공하는 것**이지 같은 객체를 재사용하는 기능이 아니다.

```java
Car first = new Car("Benz", "c200"); // 새 객체 생성
Car second = first;                   // new 없음: 같은 객체 참조
Car third = new Car("Benz", "c200"); // 값은 같지만 새 객체 생성
```

- `first == second`: `true` — 두 변수가 같은 인스턴스를 가리킨다.
- `first == third`: `false` — 필드값은 같아도 서로 다른 인스턴스다.
- 참조형에 `==`를 사용하면 일반적으로 객체의 내용이 아니라 같은 객체를 가리키는지 비교한다.

### `this`

`this`는 현재 생성된 객체 자신을 가리킨다. 필드와 매개변수의 이름이 같을 때 구분하는 용도로 자주 사용한다.

```java
public Car(String brand) {
    this.brand = brand;
}
```

왼쪽의 `this.brand`는 객체의 필드이고, 오른쪽의 `brand`는 생성자의 매개변수다.

## 9. 메서드

메서드의 기본 형식은 다음과 같다.

```java
접근제한자 반환타입 메서드명(매개변수) {
    // 실행문
    return 반환값;
}
```

현재 `Car` 코드의 예시는 다음과 같다.

```java
public void setBrand(String brand) {
    this.brand = brand;
}

public String getBrand() {
    return brand;
}

public String carInfo() {
    return brand + "\t" + model;
}
```

- `setBrand`: 매개변수가 있고 반환값은 없다.
- `getBrand`: 매개변수가 없고 `String`을 반환한다.
- 반환값이 없으면 반환 타입으로 `void`를 사용한다.
- 하나의 `return`은 하나의 값만 반환하며, 여러 정보를 반환하려면 객체·배열·컬렉션 등으로 묶어야 한다.

## 10. 캡슐화와 접근 제한자

객체의 필드를 `public`으로 공개하면 외부에서 값을 임의로 변경할 수 있다.

```java
teacher.name = "inspire";
```

일반적으로 필드는 `private`으로 숨기고 필요한 접근만 메서드로 제공한다.

```java
private String brand;

public String getBrand() {
    return brand;
}

public void setBrand(String brand) {
    this.brand = brand;
}
```

- `private`: 현재 클래스 내부에서만 접근할 수 있다.
- `public`: 어디서든 접근할 수 있다.
- **Getter**: 값을 조회한다.
- **Setter**: 값을 변경한다.

무조건 Setter를 만들기보다는 객체의 값이 실제로 변경되어야 하는지 판단하여 필요한 메서드만 공개하는 것이 좋다.

### VS Code Source Action으로 Getter/Setter 생성

필드를 먼저 선언한 후 VS Code의 Java `Source Action`을 사용하면 Getter와 Setter를 한 번에 생성할 수 있다.

```java
public class Teacher {
    private String name;
    private int age;
    private char gender;
    private String job;
}
```

생성 순서는 다음과 같다.

1. 클래스 파일 내부에서 마우스 오른쪽 버튼을 누른다.
2. `Source Action...`을 선택한다.
3. `Generate Getters and Setters...`를 선택한다.
4. 메서드를 만들 필드를 체크한다.
5. `OK`를 누른다.

클래스 내부에서 `Ctrl + .`을 눌러 `Source Action` 메뉴를 열 수도 있다.

선택한 필드를 기준으로 다음과 같은 코드가 클래스에 직접 삽입된다.

```java
public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public int getAge() {
    return age;
}

public void setAge(int age) {
    this.age = age;
}
```

Source Action과 Lombok의 차이는 다음과 같다.

```text
Source Action          → 실제 Getter/Setter 코드를 소스 파일에 삽입
Lombok @Getter/@Setter → 소스에는 보이지 않고 컴파일 과정에서 생성
```

처음 학습할 때는 Source Action으로 생성된 코드를 읽으며 구조를 익히고, 반복 코드가 많아지면 Lombok을 활용할 수 있다.

캡슐화는 객체지향의 주요 특징인 캡슐화(은닉), 상속, 다형성, 추상화 중 하나다.

## 11. DTO

DTO(Data Transfer Object)는 계층이나 시스템 사이에서 데이터를 전달하기 위한 객체다.

```java
public class BlogRequestDTO {
    private int id;
    private String title;
    private String content;
    private String email;
}
```

일반적으로 요청과 응답의 목적을 명확하게 구분한다.

```text
BlogRequestDTO  : 블로그 요청 데이터
BlogResponseDTO : 블로그 응답 데이터
```

DTO는 주로 데이터를 담는 역할을 하며, 핵심 비즈니스 로직은 넣지 않는 편이다. Entity는 데이터베이스의 영속 상태를 표현하는 객체이므로 DTO와 역할이 다르다.

## 12. Lombok과 Builder 패턴

Lombok은 반복되는 생성자, Getter, Setter, Builder 코드를 컴파일 과정에서 생성해 주는 라이브러리다.

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BlogRequestDTO {
    private int id;
    private String title;
    private String content;
    private String email;
}
```

| 어노테이션 | 생성되는 기능 |
|---|---|
| `@Getter` | 필드의 Getter |
| `@Setter` | 필드의 Setter |
| `@NoArgsConstructor` | 매개변수 없는 생성자 |
| `@AllArgsConstructor` | 모든 필드를 받는 생성자 |
| `@Builder` | Builder 방식의 객체 생성 코드 |

각 어노테이션이 대신 생성하는 코드를 직접 작성하면 다음과 같은 형태다.

### `@Getter`

각 필드의 값을 반환하는 Getter 메서드를 대신한다.

```java
public int getId() {
    return id;
}

public String getTitle() {
    return title;
}

public String getContent() {
    return content;
}

public String getEmail() {
    return email;
}
```

### `@Setter`

각 필드의 값을 변경하는 Setter 메서드를 대신한다.

```java
public void setTitle(String title) {
    this.title = title;
}
```

실제로는 `id`, `title`, `content`, `email` 각각에 대응하는 Setter가 생성된다.

### `@NoArgsConstructor`

매개변수가 없는 생성자를 대신한다.

```java
public BlogRequestDTO() {
}
```

사용 예시는 다음과 같다.

```java
BlogRequestDTO request = new BlogRequestDTO();
```

### `@AllArgsConstructor`

선언된 모든 필드를 매개변수로 받는 생성자를 대신한다. 필드가 선언된 순서대로 매개변수가 만들어진다.

```java
public BlogRequestDTO(int id, String title, String content, String email) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.email = email;
}
```

사용 예시는 다음과 같다.

```java
BlogRequestDTO request =
        new BlogRequestDTO(1, "자바", "재밌다", "example@example.com");
```

### `@Builder`

Builder 객체와 필드별 설정 메서드, 마지막에 DTO를 생성하는 `build()` 메서드를 대신한다. 개념적으로 다음과 같은 코드를 자동 생성한다.

```java
public static BlogRequestDTOBuilder builder() {
    return new BlogRequestDTOBuilder();
}

public static class BlogRequestDTOBuilder {
    private int id;
    private String title;
    private String content;
    private String email;

    public BlogRequestDTOBuilder title(String title) {
        this.title = title;
        return this;
    }

    public BlogRequestDTOBuilder content(String content) {
        this.content = content;
        return this;
    }

    public BlogRequestDTOBuilder email(String email) {
        this.email = email;
        return this;
    }

    public BlogRequestDTO build() {
        return new BlogRequestDTO(id, title, content, email);
    }
}
```

위 코드는 원리를 보여주기 위해 단순화한 것이다. Lombok이 실제 생성하는 Builder에는 `id()` 등 모든 필드의 설정 메서드와 추가 구현이 포함된다.

Builder를 사용하면 매개변수의 의미를 명확히 표현하면서 객체를 만들 수 있다.

```java
BlogRequestDTO request = BlogRequestDTO.builder()
        .title("자바")
        .content("재밌다")
        .email("example@example.com")
        .build();

System.out.println(request.getTitle());
```

Builder도 내부적으로 객체를 생성한다. `new`를 사용하지 않는 것이 아니라, 생성 과정을 Builder가 감싸서 호출 코드에 직접 드러나지 않는 것이다.

## 13. 1일차 핵심 요약

1. `javac`는 컴파일하고 `java`는 JVM에서 클래스를 실행한다.
2. Java 기본형은 8개이며 `String`은 참조형이다.
3. 클래스는 객체의 설계도이고, 객체는 `new 생성자()`로 만든다.
4. 필드는 객체의 상태, 메서드는 객체의 동작을 나타낸다.
5. 생성자는 반환 타입이 없으며 객체 생성과 초기화를 담당한다.
6. `this`는 현재 객체를 가리킨다.
7. 필드는 `private`으로 숨기고 필요한 Getter나 동작을 제공하는 것이 기본이다.
8. DTO는 데이터를 전달하는 객체이며 Entity와 역할이 다르다.
9. Lombok은 반복 코드를 생성하고, Builder는 객체 생성을 읽기 쉽게 만든다.
