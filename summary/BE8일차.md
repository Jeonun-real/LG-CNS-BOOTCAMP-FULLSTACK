# BE 8일차 - Java I/O와 Blog 데이터 영속화

## 1. 오늘의 핵심

오늘은 `IOStreamApp`에서 Java의 입출력 흐름을 연습한 뒤, 그 내용을 Blog 애플리케이션에 적용했다.

기존 게시글은 `BlogReactDao`의 `List<BlogResponseDTO>`에만 저장되므로 JVM이 종료되면 사라진다. 이를 해결하기 위해 게시글 목록을 파일에 저장하고 다음 실행 때 다시 불러오는 기능을 추가했다.

```text
IOStreamApp
  ├─ 콘솔 입력
  ├─ 문자 파일 읽기·쓰기
  └─ 객체 직렬화·역직렬화
             ↓ 적용
Blog 애플리케이션
  ├─ 시작 → blogs.txt 읽기 → DAO 목록 복구
  └─ 종료 → DAO 목록 조회 → blogs.txt 저장
```

### 핵심 키워드

- Stream: 데이터가 이동하는 통로
- Input / Output: 외부 데이터를 읽거나 외부로 내보내는 것
- Reader / Writer: 문자 데이터를 다루는 입출력 클래스
- Buffer: 데이터를 모아 처리하는 보조 공간
- try-with-resources: 사용한 자원을 자동으로 닫는 문법
- 직렬화: 객체를 저장 가능한 바이트 형태로 변환
- 역직렬화: 바이트 데이터를 다시 객체로 복원
- `Serializable`: 직렬화 가능 여부를 표시하는 마커 인터페이스

---

## 2. Stream의 구분

```text
입력: 키보드·파일 ── Stream ──> Java 애플리케이션
출력: Java 애플리케이션 ── Stream ──> 파일·외부 시스템
```

| 구분 | 입력 | 출력 | 용도 |
|---|---|---|---|
| 바이트 기반 | `InputStream` | `OutputStream` | 이미지, 영상, 직렬화 객체 등 |
| 문자 기반 | `Reader` | `Writer` | 텍스트 데이터 |

`Reader`가 무조건 데이터를 2바이트씩 읽는다는 의미는 아니다. 파일의 바이트를 문자 인코딩에 따라 해석해 Java 문자로 제공한다고 이해하는 것이 정확하다.

---

## 3. IOStreamApp 문법

### 3.1 System.in으로 바이트 읽기

```java
try {
    int input = System.in.read();
    System.out.println(input);
} catch (IOException e) {
    e.printStackTrace();
}
```

`System.in`은 `InputStream`이며 `read()`는 한 바이트를 읽어 `int`로 반환한다. 한글처럼 여러 바이트로 표현되는 문자를 직접 처리하기 불편하므로 문자열 입력에는 문자 스트림을 연결한다.

### 3.2 InputStreamReader와 BufferedReader
s
```java
BufferedReader br =
    new BufferedReader(new InputStreamReader(System.in));

String input = br.readLine();
```

```text
System.in
  ↓ 바이트 입력
InputStreamReader
  ↓ 바이트를 문자로 변환
BufferedReader
  ↓ 버퍼링 및 readLine() 제공
String
```

- `InputStreamReader`: 바이트 스트림과 문자 스트림을 연결하는 변환 스트림
- `BufferedReader`: 문자를 버퍼에 모으고 한 줄 단위로 읽는 기능 제공
- `readLine()`: 한 줄을 `String`으로 반환하며 더 읽을 내용이 없으면 `null` 반환

### 3.3 try-catch-finally와 자원 해제

```java
try {
    String input = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    try {
        if (br != null) {
            br.close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

입출력 자원은 사용 후 `close()`해야 한다. 하지만 `close()`도 예외가 발생할 수 있어 `finally` 안에 다시 예외처리가 필요하다.

### 3.4 try-with-resources

```java
try (BufferedReader br =
        new BufferedReader(new InputStreamReader(System.in))) {

    System.out.println(br.readLine());
} catch (Exception e) {
    e.printStackTrace();
}
```

소괄호 안에서 생성한 자원은 `try`가 끝날 때 자동으로 닫힌다. 이 문법에 사용할 객체는 `AutoCloseable`을 구현해야 하며 Java의 주요 스트림 클래스가 이를 구현한다.

```text
자원 생성 → 데이터 처리 → 정상 종료 또는 예외 발생 → 자동 close()
```

---

## 4. 문자 파일 읽기와 쓰기

### 4.1 FileReader로 읽기

```java
String path = "./be/inspire_java_grammer_6th/test.txt";

try (BufferedReader br = new BufferedReader(
        new FileReader(new File(path), StandardCharsets.UTF_8))) {

    String input = null;

    while ((input = br.readLine()) != null) {
        System.out.println(input);
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

```text
test.txt
  ↓ FileReader: UTF-8 기준 문자 읽기
BufferedReader: 버퍼링 및 한 줄 읽기
  ↓ readLine()
String → 콘솔 출력
```

`while ((input = br.readLine()) != null)`은 한 줄을 읽어 변수에 대입한 뒤, 파일 끝이 아닐 동안 반복한다. UTF-8을 명시하면 운영체제의 기본 인코딩 차이로 한글이 깨지는 문제를 줄일 수 있다.

### 4.2 FileWriter로 쓰기

```java
String path = "./be/inspire_java_grammer_6th/test.txt";

try (BufferedWriter bw = new BufferedWriter(
        new FileWriter(new File(path), StandardCharsets.UTF_8))) {

    bw.write("덮어쓴다");
} catch (Exception e) {
    e.printStackTrace();
}
```

```text
String → BufferedWriter → FileWriter → test.txt
```

현재 생성자처럼 append 옵션을 주지 않으면 기존 내용을 덮어쓴다.

---

## 5. 객체 직렬화와 역직렬화

문자 파일은 사람이 읽는 텍스트에 적합하다. `List<BlogResponseDTO>` 같은 객체 구조를 그대로 저장하려면 객체 직렬화를 사용한다.

### 5.1 Serializable

```java
public class BlogResponseDTO implements Serializable {
    private Integer blogId;
    private String title, content, email;
    private Integer viewCnt;
}
```

`Serializable`은 구현할 메서드가 없는 마커 인터페이스다. JVM에 이 객체를 직렬화할 수 있음을 알려준다.

`ArrayList` 자체도 `Serializable`을 구현하지만 내부 요소인 `BlogResponseDTO`도 직렬화 가능해야 전체 목록을 저장할 수 있다.

### 5.2 ObjectOutputStream으로 저장

```java
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream(new File(path)))) {

    oos.writeObject(blogs);
}
```

```text
List<BlogResponseDTO>
  ↓ writeObject()
ObjectOutputStream: 객체 직렬화
  ↓
FileOutputStream: 파일로 바이트 출력
```

확장자가 `.txt`여도 저장 내용은 일반 텍스트가 아니라 Java 직렬화 형식의 바이너리 데이터다.

### 5.3 ObjectInputStream으로 복원

```java
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream(new File(path)))) {

    List<BlogResponseDTO> list =
        (List<BlogResponseDTO>) ois.readObject();

    list.forEach(System.out::println);
}
```

```text
파일 → FileInputStream → ObjectInputStream
                           ↓ readObject()
                         Object
                           ↓ 명시적 형변환
                  List<BlogResponseDTO>
```

`readObject()`의 반환 타입은 `Object`이므로 원래 타입으로 명시적 형변환한다. 실제 저장한 타입과 읽을 때의 타입이 일치해야 한다.

---

## 6. IOStreamApp에서 Blog로 이어지는 흐름

### 6.1 적용 목적

```text
기존
프로그램 실행 → DAO의 List 사용 → 종료 → 데이터 소멸

파일 적용 후
프로그램 실행 → 파일 load → DAO의 List 사용
              → 종료 시 save → 다음 실행에서도 유지
```

### 6.2 전체 계층 구조

```text
BlogReactApp
  ↓
ReactView
  ↓ endpoint: file.inspire
FrontController
  ↓ BeanFactory.getBean("file.inspire")
FileController
  ↓
ReactService 인터페이스
  ↓ 구현
ReactServiceImpl
  ↓
BlogReactDao의 List<BlogResponseDTO>
  ↕
blogs.txt
```

View가 파일 스트림을 직접 다루지 않고 Service 구현체가 실제 파일 저장과 로드를 담당한다.

---

## 7. 계층별 코드 분석

### 7.1 ReactService: 기능 표준 선언

```java
public interface ReactService {
    boolean saveToFile();
    boolean loadToFile();
}
```

인터페이스에는 필요한 기능만 선언하고 실제 스트림 처리는 `ReactServiceImpl`이 구현한다. 반환 타입이 `boolean`이므로 View에서 성공과 실패를 구분할 수 있다.

### 7.2 ReactServiceImpl: DAO 목록 저장

```java
@Override
public boolean saveToFile() {
    String path = "./be/inspire_java_grammer_6th/blogs.txt";

    try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(new File(path)))) {

        oos.writeObject(dao.findByAll());
        return true;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
```

```text
dao.findByAll()
  ↓ 현재 게시글 목록
List<BlogResponseDTO>
  ↓ writeObject()
직렬화 → blogs.txt 저장
  ↓
성공 true / 실패 false
```

### 7.3 ReactServiceImpl: 파일에서 DAO 목록 복구

```java
@Override
public boolean loadToFile() {
    String path = "./be/inspire_java_grammer_6th/blogs.txt";

    try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(new File(path)))) {

        dao.setBlogs((List<BlogResponseDTO>) ois.readObject());
        return true;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
```

```text
blogs.txt
  ↓ ObjectInputStream
역직렬화
  ↓
List<BlogResponseDTO>
  ↓ dao.setBlogs(...)
DAO의 기존 목록을 파일에서 읽은 목록으로 교체
```

`setBlogs()`는 요소를 하나씩 복사하는 것이 아니라 읽어 온 List의 참조를 DAO 필드에 대입한다.

### 7.4 FileController: 요청과 Service 연결

```java
public boolean save() {
    return service.saveToFile();
}

public boolean load() {
    return service.loadToFile();
}
```

Controller는 요청을 받아 적절한 Service 메서드를 호출한다. 직접 스트림을 만들거나 파일을 처리하지 않는다.

### 7.5 BeanFactory: endpoint 등록

```java
map.put("file.inspire", new FileController(service));
```

```text
"file.inspire" → FileController 객체
```

목록·입력·수정·삭제 Controller와 FileController가 같은 Service를 전달받으므로 같은 DAO의 게시글 목록을 사용한다.

### 7.6 FrontController: save와 load 분기

```java
public boolean file(String endPoint, String action) {
    FileController controller =
        (FileController) factory.getBean(endPoint);

    if (action.equals("save")) {
        return controller.save();
    } else {
        return controller.load();
    }
}
```

```text
front.file("file.inspire", "save") → FileController.save()
front.file("file.inspire", "load") → FileController.load()
```

`getBean()`의 반환 타입이 `Object`이므로 `FileController`로 명시적 형변환한 뒤 고유 메서드를 호출한다.

---

## 8. 프로그램 시작과 종료 흐름

### 8.1 시작 시 load

`ReactView.landingPage()`는 메뉴를 출력하기 전에 파일을 읽는다.

```java
boolean isLoad = front.file("file.inspire", "load");
System.out.println(
    isLoad ? "데이터 로딩완료!!" : "데이터 로딩 등 실패"
);
```

```text
BlogReactApp.main()
  ↓
ReactView.landingPage()
  ↓ front.file("file.inspire", "load")
FrontController
  ↓ BeanFactory
FileController.load()
  ↓
ReactServiceImpl.loadToFile()
  ↓ blogs.txt 역직렬화
BlogReactDao.setBlogs()
  ↓
이전에 저장한 게시글 복구
```

### 8.2 종료 시 save

사용자가 99를 입력하고 저장 여부에 `y`를 입력하면 현재 목록을 저장한다.

```java
if (yesOrNo.equalsIgnoreCase("y")) {
    String endPoint = "file.inspire";

    System.out.println(
        front.file(endPoint, "save")
            ? "데이터 저장 완료!!"
            : "데이터 저장 실패"
    );
}
System.exit(1);
```

```text
99 입력 → y 입력
  ↓ front.file("file.inspire", "save")
FrontController → FileController.save()
  ↓
ReactServiceImpl.saveToFile()
  ↓ dao.findByAll()
현재 List 직렬화 → blogs.txt 저장 → 종료
```

`equalsIgnoreCase("y")`를 사용하므로 `y`와 `Y`를 모두 저장 동의로 처리한다.

---

## 9. 게시글 데이터의 전체 생명주기

```text
[첫 실행]
BlogReactDao 생성
  ↓ 기본 게시글 생성
입력·수정·삭제
  ↓ 종료 시 y
blogs.txt에 직렬화

[다음 실행]
BlogReactDao 생성
  ↓ landingPage에서 blogs.txt 로드
역직렬화한 List로 DAO 목록 교체
  ↓
이전 실행의 작업 결과 유지
```

실제 DB를 사용하지 않아도 프로그램 실행 사이에 데이터를 유지하는 영속성의 기초를 연습한 것이다.

---

## 10. 실행 시 주의할 점

### 10.1 object.txt와 blogs.txt는 다른 파일

`IOStreamApp`은 다음 파일을 사용한다.

```java
String path = "./be/inspire_java_grammer_6th/object.txt";
```

Blog 애플리케이션은 다음 파일을 사용한다.

```java
String path = "./be/inspire_java_grammer_6th/blogs.txt";
```

따라서 `object.txt`가 있어도 Blog의 `loadToFile()`은 `blogs.txt`를 찾는다.

### 10.2 최초 실행에는 blogs.txt가 없을 수 있음

아직 한 번도 저장하지 않았다면 `FileNotFoundException`이 발생하고 로드는 `false`가 된다.

```text
최초 실행 → load 실패 가능 → 기본 DAO 데이터 사용
         → 99 → y → blogs.txt 최초 생성
```

### 10.3 상대 경로는 실행 위치 기준

`./`는 Java 소스 파일의 위치가 아니라 프로그램을 실행한 현재 작업 디렉터리를 기준으로 한다. IDE 실행 위치가 달라지면 같은 코드라도 파일을 찾지 못할 수 있다.

### 10.4 직렬화 파일과 클래스 구조

저장 후 `BlogResponseDTO`의 구조가 바뀌면 기존 파일을 읽는 과정에서 호환성 문제가 생길 수 있다. 실무에서는 명시적인 `serialVersionUID`를 선언해 버전을 관리하기도 한다.

---

## 11. 최종 정리

```text
1. Stream으로 데이터 이동 원리 이해
2. Reader / Writer로 문자 파일 처리
3. Buffered 계열로 효율성과 편의성 확보
4. try-with-resources로 자원 자동 해제
5. Serializable과 ObjectStream으로 객체 저장·복원
6. Blog의 Controller-Service-DAO 구조에 파일 기능 연결
7. 시작 시 load, 종료 시 save로 메모리 데이터 유지
```

> `IOStreamApp`에서 학습한 객체 직렬화와 역직렬화를 Blog 애플리케이션의 Service 계층에 적용하여, DAO의 게시글 목록을 프로그램 종료 후에도 유지하도록 만들었다.

