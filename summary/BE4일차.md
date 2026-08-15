# BE 4일차 - Java OOP 핵심 정리

오늘 학습의 전체 흐름은 다음과 같다.

```text
매개변수의 다형성
→ enum을 이용한 타입 구분
→ 추상 클래스
→ 인터페이스
→ 싱글톤
→ 팩토리
→ 결합도가 낮은 프로그램
```

## 1. 매개변수의 다형성

### 클래스 상속 구조

```text
              PersonDTO
            /     |      \
   StudentDTO TeacherDTO ManagerDTO
```

```java
public class StudentDTO extends PersonDTO
public class TeacherDTO extends PersonDTO
public class ManagerDTO extends PersonDTO
```

세 클래스는 모두 `PersonDTO`의 자식이므로 `PersonDTO` 타입으로 자동 형변환할 수 있다.

```java
StudentDTO student = new StudentDTO(...);
PersonDTO person = student; // 자동 업캐스팅
```

### OopService의 setAry()

```java
public class OopService {
    private PersonDTO[] ary;
    private int idx;

    public OopService() {
        ary = new PersonDTO[10];
        idx = 0;
    }

    public void setAry(PersonDTO per) {
        ary[idx++] = per;
    }
}
```

`setAry()`의 매개변수는 `PersonDTO`이지만 모든 자식 객체를 받을 수 있다.

```java
service.setAry(student);
service.setAry(teacher);
service.setAry(manager);
```

```text
StudentDTO ──┐
TeacherDTO ──┼── 자동 업캐스팅 → PersonDTO per
ManagerDTO ──┘
```

이것이 **매개변수의 다형성**이다. 자식 클래스마다 별도의 메서드를 만들지 않고 부모 타입 하나로 처리할 수 있다.

```java
// 자식별 메서드를 만들 필요가 없다.
setStudent(StudentDTO student);
setTeacher(TeacherDTO teacher);
setManager(ManagerDTO manager);

// 부모 타입 메서드 하나로 처리한다.
setAry(PersonDTO person);
```

> 여러 자식 객체를 부모 타입 매개변수로 받는 것은 오버로딩이 아니라 매개변수의 다형성이다.

## 2. OopApp으로 이어지는 로직

### 2.1 서비스 객체와 배열 생성

```java
OopService service = new OopService();
```

생성자가 실행되면서 `PersonDTO` 배열이 생성된다.

```java
private PersonDTO[] ary; // 배열 참조변수 선언
ary = new PersonDTO[10]; // 실제 배열 객체 생성
```

### 2.2 makePerson() 호출

```java
service.makePerson(Flag.STUDENT, "서", 27, "SEOUL", "2026");
service.makePerson(Flag.TEACHER, "정", 26, "SEOUL", "JAVA");
service.makePerson(Flag.MANAGER, "정", 25, "SEOUL", "교육팀");
```

`makePerson()`은 `Flag` 값에 따라 서로 다른 자식 객체를 생성한다.

```text
Flag.STUDENT → StudentDTO 객체
Flag.TEACHER → TeacherDTO 객체
Flag.MANAGER → ManagerDTO 객체
```

생성된 객체는 모두 부모 타입 변수에 저장할 수 있다.

```java
PersonDTO per = StudentDTO.builder().build();
PersonDTO per = TeacherDTO.builder().build();
PersonDTO per = ManagerDTO.builder().build();
```

마지막에는 동일한 `setAry()`를 호출한다.

```java
setAry(per);
```

### 2.3 부모 타입 배열에 저장

```text
PersonDTO[] ary

ary[0] → StudentDTO 객체
ary[1] → TeacherDTO 객체
ary[2] → ManagerDTO 객체
```

부모 타입 배열 하나로 서로 다른 자식 객체를 함께 관리할 수 있다.

### 2.4 오버라이딩된 메서드 실행

```java
PersonDTO[] result = service.getAry();

for (PersonDTO person : result) {
    if (person == null) {
        break;
    }
    System.out.println(person.PerosnInfo());
}
```

반복문의 변수 타입은 `PersonDTO`이지만 실제 객체에 따라 자식이 오버라이딩한 메서드가 실행된다.

```text
StudentDTO 객체 → StudentDTO.PerosnInfo()
TeacherDTO 객체 → TeacherDTO.PerosnInfo()
ManagerDTO 객체 → ManagerDTO.PerosnInfo()
```

이를 **동적 바인딩을 통한 메서드 다형성**이라고 한다.

### OopApp 전체 실행 흐름

```text
OopApp
  ↓ makePerson(Flag, ...)
OopService
  ↓ Flag에 맞는 객체 생성
StudentDTO / TeacherDTO / ManagerDTO
  ↓ PersonDTO로 자동 업캐스팅
PersonDTO per
  ↓ setAry(per)
PersonDTO[]에 저장
  ↓ getAry()
OopApp에서 반복
  ↓ person.PerosnInfo()
실제 자식 객체의 오버라이딩 메서드 실행
```

## 3. enum

`enum`은 서로 관련된 정해진 상수들을 하나의 타입으로 묶는 **특수한 클래스**다.

### 3.1 Flag enum 선언

```java
public enum Flag {
    STUDENT(1),
    TEACHER(2),
    MANAGER(3);

    private final int flag;

    private Flag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return this.flag;
    }
}
```

각 enum 상수에는 정수값이 연결되어 있다.

```text
Flag.STUDENT → 1
Flag.TEACHER → 2
Flag.MANAGER → 3
```

`STUDENT(1)`이 만들어질 때 enum 생성자에 `1`이 전달되고 `flag` 필드에 저장된다. `flag`가 `final`이므로 생성 후에는 값을 바꿀 수 없다.

enum 생성자는 외부에서 직접 호출할 수 없으며, 선언된 상수만 사용할 수 있다.

```java
new Flag(1); // 불가능
Flag.STUDENT; // 가능
```

### 3.2 EnumApp의 기본 사용

```java
Flag flag = Flag.STUDENT;

System.out.println(flag);           // STUDENT
System.out.println(flag.getFlag()); // 1
```

`flag` 변수에는 아무 문자열이나 숫자가 아니라 `Flag`에 선언된 값만 저장할 수 있다.

```java
Flag flag = Flag.STUDENT; // 가능
Flag flag = 1;            // 불가능
Flag flag = "STUDENT";    // 불가능
```

이처럼 허용된 값의 범위를 컴파일 단계에서 제한하는 것을 **타입 안정성**이라고 한다.

### 3.3 switch 표현식과 enum

enum은 `switch`에서 명확하게 분기할 수 있다.

```java
switch (flag) {
    case STUDENT -> System.out.println("학생");
    case TEACHER -> System.out.println("강사");
    case MANAGER -> System.out.println("매니저");
}
```

`flag`가 이미 `Flag` 타입이므로 `case`에서는 `Flag.STUDENT`가 아니라 상수 이름인 `STUDENT`만 적는다.

모든 상수가 코드에 드러나기 때문에 숫자나 문자열을 이용한 조건문보다 의미가 분명하다.

```java
// 의미를 바로 파악하기 어려움
if (flagNumber == 1) { }

// 의미가 명확함
if (flag == Flag.STUDENT) { }
```

### 3.4 enum의 == 비교

```java
if (flag == Flag.STUDENT) {
    System.out.println("학생");
}
```

enum 상수는 각각 하나의 고정된 객체로 관리되므로 `==`로 안전하게 비교할 수 있다.

문자열처럼 다음과 같이 `equals()` 사용 여부를 고민할 필요가 없다.

```java
flag == Flag.STUDENT
```

### 3.5 OopApp 및 OopService와의 연결

`OopApp`은 생성할 객체의 종류를 `Flag`로 전달한다.

```java
service.makePerson(Flag.STUDENT, "서", 27, "SEOUL", "2026");
service.makePerson(Flag.TEACHER, "정", 26, "SEOUL", "JAVA");
service.makePerson(Flag.MANAGER, "정", 25, "SEOUL", "교육팀");
```

`OopService.makePerson()`은 전달받은 enum 값에 따라 자식 객체를 선택한다.

```text
OopApp
  ↓ Flag.STUDENT 전달
OopService.makePerson()
  ↓ Flag 값 확인
StudentDTO 생성
  ↓ PersonDTO로 업캐스팅
PersonDTO[]에 저장
```

정수로 구분할 경우 잘못된 값도 전달될 수 있다.

```java
makePerson(7, ...); // 7이 무엇인지 알 수 없고 잘못된 값도 허용됨
```

enum을 사용하면 선언된 값만 전달할 수 있다.

```java
makePerson(Flag.STUDENT, ...);
```

따라서 `Flag`는 다음 역할을 한다.

- 생성할 객체의 종류를 명확하게 표현
- 잘못된 값 전달 방지
- 숫자와 문자열에 대한 의존 감소
- `switch` 및 조건문의 가독성 향상
- `OopApp`과 `OopService` 사이의 안전한 데이터 전달

> 현재 `makePerson()`은 `flag.getFlag() == 1`처럼 내부 정수값을 다시 비교한다. enum의 장점을 더 살리려면 `switch (flag)` 또는 `flag == Flag.STUDENT`처럼 enum 상수를 직접 비교하는 편이 좋다.

## 4. 추상 클래스

```java
public abstract class Animal {
    private String name;

    public void eating(String food) {
        System.out.println(food + "를 먹고 살아갑니다");
    }
}
```

추상 클래스는 여러 자식 클래스가 공유할 **공통 상태와 공통 로직**을 제공하는 부모 클래스다.

- `name`: 공통 상태
- `eating()`: 공통 구현
- 직접 객체 생성 불가능
- 부모 타입으로는 사용 가능

```java
Animal animal = new Animal();   // 불가능
Animal animal = new SuperMan(); // 가능
Animal[] ary = new Animal[10];  // 타입 및 배열 선언 가능
```

추상 클래스에는 구현부가 없는 추상 메서드도 선언할 수 있다.

```java
public abstract class Animal {
    public abstract void move();
}
```

일반 자식 클래스는 상속받은 추상 메서드를 모두 오버라이딩해야 한다. 구현하지 않으면 자식 클래스도 `abstract`로 선언해야 한다.

## 5. 인터페이스

```java
public interface Flyer {
    void fly();
    void takeOff();
    void landing();
}
```

인터페이스는 구현 클래스가 지켜야 할 **기능의 표준 또는 계약**을 정의한다.

```java
public class SuperMan extends Animal implements Flyer {
    @Override
    public void fly() { }

    @Override
    public void takeOff() { }

    @Override
    public void landing() { }
}
```

```text
SuperMan is an Animal → Animal 상속
SuperMan can Fly      → Flyer 구현
```

- `extends Animal`: 공통 상태와 공통 구현 상속
- `implements Flyer`: 비행 기능의 표준 구현
- 클래스 상속은 하나만 가능
- 인터페이스는 여러 개 구현 가능

### 추상 클래스와 인터페이스 비교

| 구분 | 추상 클래스 | 인터페이스 |
|---|---|---|
| 목적 | 공통 상태와 로직 제공 | 기능의 표준 제공 |
| 문법 | `extends` | `implements` |
| 인스턴스 필드 | 가능 | 불가능, 기본적으로 상수 |
| 일반 구현 메서드 | 가능 | `default`, `static` 메서드 등 가능 |
| 사용 개수 | 하나만 상속 | 여러 개 구현 가능 |
| 직접 객체 생성 | 불가능 | 불가능 |

```text
공통 업무와 상태를 물려준다 → 추상 클래스
지켜야 할 기능의 규격을 만든다 → 인터페이스
```

### 인터페이스 타입과 형변환

```java
Flyer superman = new SuperMan();
```

`Flyer`에 선언된 메서드는 바로 호출할 수 있다.

```java
superman.fly();
```

`eating()`은 `Flyer`에 없고 `SuperMan`이 `Animal`에서 상속받은 메서드이므로 현재 참조변수로는 바로 호출할 수 없다.

```java
superman.eating("빵");             // 컴파일 오류
((SuperMan) superman).eating("빵"); // 다운캐스팅 후 호출
```

## 6. TV 인터페이스를 이용한 다형성

```java
public interface TV {
    void turnOn();
}
```

```java
public class SamsungTV implements TV {
    @Override
    public void turnOn() {
        System.out.println("samsung tv power on");
    }
}

public class LgTV implements TV {
    @Override
    public void turnOn() {
        System.out.println("lg tv power on");
    }
}
```

두 클래스가 같은 인터페이스를 구현하므로 `TV` 타입으로 사용할 수 있다.

```java
TV tv1 = SamsungTV.getInstance();
TV tv2 = LgTV.getInstance();

tv1.turnOn(); // SamsungTV의 구현 실행
tv2.turnOn(); // LgTV의 구현 실행
```

`turnOn()`은 부모 인터페이스인 `TV`에 선언되어 있으므로 형변환이 필요하지 않다.

클라이언트는 제조사별 내부 구현을 몰라도 `TV`에 정의된 기능만 사용하면 된다. 구현체를 쉽게 교체할 수 있으므로 클래스 간 결합도가 낮아진다.

## 7. 싱글톤 패턴

싱글톤은 특정 클래스의 객체를 **하나만 생성하고 계속 재사용**하는 패턴이다.

```java
public class SamsungTV implements TV {
    private static SamsungTV instance;

    private SamsungTV() {
    }

    public static SamsungTV getInstance() {
        if (instance == null) {
            instance = new SamsungTV();
        }
        return instance;
    }
}
```

### 싱글톤의 세 가지 구성 요소

1. `private` 생성자로 외부의 `new` 차단
2. `static` 필드에 유일한 객체 보관
3. `static getInstance()`로 객체 반환

```java
SamsungTV tv1 = SamsungTV.getInstance();
SamsungTV tv2 = SamsungTV.getInstance();

System.out.println(tv1 == tv2); // true
```

최초 요청 때만 객체를 생성하고 이후에는 기존 객체를 반환한다. 현재 코드에서는 `SamsungTV`, `LgTV`, `BeanFactory`에 싱글톤이 적용되었다.

> 현재 구현은 싱글 스레드 학습용이다. 멀티스레드 환경에서는 동시 생성 방지를 위한 추가 처리가 필요하다.

## 8. 팩토리 패턴

팩토리는 객체의 생성, 보관, 선택 책임을 클라이언트 대신 담당한다.

```java
public class BeanFactory {
    private static BeanFactory instance;
    private TV[] ary;

    private BeanFactory() {
        ary = new TV[2];
        ary[0] = SamsungTV.getInstance();
        ary[1] = LgTV.getInstance();
    }

    public static BeanFactory getInstance() {
        if (instance == null) {
            instance = new BeanFactory();
        }
        return instance;
    }

    public TV getBrand(String brandName) {
        return brandName.equalsIgnoreCase("samsung")
            ? ary[0]
            : ary[1];
    }
}
```

### 팩토리의 역할

- TV 객체 생성 및 조회 담당
- 객체를 내부 배열에서 관리
- 구체 클래스를 클라이언트로부터 숨김
- `SamsungTV` 또는 `LgTV`를 공통 인터페이스인 `TV` 타입으로 반환

```java
public TV getBrand(String brandName)
```

반환되는 실제 객체는 제조사별 TV지만 클라이언트는 `TV` 인터페이스만 알면 된다.

## 9. TvClientApp 실행 흐름

```java
BeanFactory factory = BeanFactory.getInstance();

TV tv = factory.getBrand("lg");
TV tv1 = factory.getBrand("samsung");

tv.turnOn();
tv1.turnOn();
```

```text
1. BeanFactory.getInstance() 호출
   ↓
2. 최초 호출이면 BeanFactory 객체 생성
   ↓
3. SamsungTV와 LgTV의 싱글톤 객체 확보
   ↓
4. TV[] 배열에 두 객체 저장
   ↓
5. getBrand("lg") 호출
   ↓
6. LgTV 객체를 TV 타입으로 반환
   ↓
7. tv.turnOn() 호출
   ↓
8. 실제 객체인 LgTV의 turnOn() 실행
```

클라이언트는 구체 클래스를 직접 생성하거나 관리하지 않는다.

```java
// 구체 클래스에 직접 의존
SamsungTV tv = SamsungTV.getInstance();

// 팩토리와 인터페이스에 의존
TV tv = factory.getBrand("samsung");
```

객체 생성과 선택의 책임이 클라이언트에서 팩토리로 이동하여 결합도가 낮아진다. 이는 간단한 형태의 **IoC(제어의 역전)** 개념으로도 볼 수 있다.

## 10. 전체 개념 연결

### OopApp의 다형성

```text
PersonDTO 부모 타입
  ↓
StudentDTO, TeacherDTO, ManagerDTO를 모두 전달
  ↓
PersonDTO[] 하나로 관리
  ↓
실제 자식의 오버라이딩 메서드 실행
```

### 추상 클래스와 인터페이스

```text
Animal 추상 클래스
  ↓ 공통 상태와 공통 구현 상속
SuperMan
  ↑ 비행 기능 구현
Flyer 인터페이스
```

### TV, 싱글톤, 팩토리

```text
TvClientApp
    ↓ 객체 요청
BeanFactory 싱글톤
    ↓ TV 타입 반환
TV 인터페이스
   ├── SamsungTV 싱글톤
   └── LgTV 싱글톤
```

## 11. 최종 핵심

- **매개변수의 다형성**: 부모 타입 매개변수 하나로 여러 자식 객체를 받는다.
- **메서드의 다형성**: 부모 타입으로 호출해도 실제 자식의 오버라이딩 메서드가 실행된다.
- **enum**: 정해진 상수만 허용하여 객체 종류를 명확하고 안전하게 구분한다.
- **추상 클래스**: 자식에게 공통 상태와 공통 구현을 제공한다.
- **인터페이스**: 구현 클래스가 지켜야 할 기능의 표준을 정의한다.
- **싱글톤**: 객체를 한 번만 생성해 재사용한다.
- **팩토리**: 객체 생성과 선택을 전담하고 구체 클래스를 클라이언트로부터 숨긴다.
- **최종 목적**: 객체 교체가 쉽고 클래스 간 결합도가 낮은 프로그램을 만든다.
