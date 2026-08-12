package features.game;

import java.util.Scanner;

/*
Q) 숫자를 맞추는 게임
    - 난수(1~100) : anwer
    - 주어진 기회는 10번 (up, down)

    콘솔로부터 데이터를 입력받기 위한 scanner 존재

    반환값
    - 성공) x번째 정답을 맞췄습니다
    - 실패) 10번의 기회를 전부 소진했습니다
*/
public class GuessGame {
    public GuessGame(){

    }



    public String gameFor(){
        System.out.print("생각하는 숫자를 입력하세요>>>>> ");
        Scanner scan = new Scanner(System.in);
        int guess = scan.nextInt();
        System.out.println("guess>>"+guess);

        return null;
    }
    public String gameWhile(){
        return null;

    }
    public String gmaeDoWhile(){
        return null;
    }

}
