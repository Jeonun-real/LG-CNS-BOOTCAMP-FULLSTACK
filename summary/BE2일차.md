# BE 2일차 — Java 핵심 문법

has a 방식
class <- class
        instance(var, method) 

App 는 start, end point를 가짐

[{}, {}, {}] array => collection
json의 객체 하나가 DTO
사용자로 부터 보내는 요청정보 request
값을 담아서 사용자로부터 보내는 정보 response


source action으로 constructore로 바로 생성자 생성가능


public BlogResponseDTO register(BlogRequestDTO request)
반환값은 주소값을 가지는 객체도 가능
매개변수도 객체형태로 받기 가능

사용자가 요청정보를 props로 묶어서 보내면 

instance -> method 에서 instance 생성 -> instance가 반환
각 인스턴스별로 연결로직 이해


하나의 클래스에 같은 타입을 같은 methon 호출 가능?
가능 -> 메서드 오버로딩 
단 매개변수의 타입과 개수가 달라야됨

연산자
반복문

메모리 내 heap 영역에 인스턴스 생성
인스턴스와의 참조가 끊어지면
jvm의 gc가 thread 단위로 동작하면서 인스턴스 삭제

execution stack (LIFO)
static 
literal pool : key-value 형태

static은 jvm에 의해 자동호출
static 변수는 메모리 내 별도의 static 영역에 먼저 올라온는데

따라서 main 메서드는 statick 영역에 들어감

static이 붙은건 instance의 소유가 아닌 class 소유임!!
제일 먼저 static 영역에 올라옴
실행되면 execution stack으로 들어감
main이 execution stack에서 빠지면 프로그램 종료

main 메서드가 static 영역에 올라간 이후 인스턴스를 생성하면
인스턴스 주소를 main이 참조하게 되고
프로그램이 끝나면 참조가 끊기고 gc가 처리


class 안에 class 정의 가능

상수는 반드시 대명사. 선언과 동시에 초기화
상수는 수정 불가능

static으로 언

static
staticmessagee      -> new xx();
pi                      message
classmethod             

java -> math ()
static으로 선언
인스턴스 생성안하고 그냥 가져다 쓰면 됨

반복이 중첩되었으때 break 쓰면 가장 가까운 블록 탈출

네스팅된 되서 빠져나갈때