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

    private int num;
    public GuessGame(){
        num = (int)(Math.random()*100)+1;
        System.out.println("num>>"+num);

    }



    public String gameFor(){
        String result = null;
        int cnt = 0;
        boolean isFlag = false;

        Scanner scan = new Scanner(System.in);

        for(int i=0; i<10;i++){
            cnt  =  i +1;
            System.out.print("생각하는 숫자를 입력하세요>>>>> ");
            int guess = scan.nextInt();
            if(num>guess){
                System.out.println("UP");


            }else if(num<guess){
                System.out.println("DOWN");

            }else{
                isFlag = true;
                break;
            }
        }
        result = (isFlag) ? cnt+"번만에 정답을 맞췄습니다" : "10번의 기회를 전부 소진했습니다";
        return result;
    }


    public String gameWhile(){

        int num = (int)(Math.random()*100)+1;
        System.out.println("num>>"+num);
        int i = 0;
        while(i <10){
            System.out.print("생각하는 숫자를 입력하세요>>>>> ");
            Scanner scan = new Scanner(System.in);
            int guess = scan.nextInt();
            if(guess == num&&guess<100&&guess>0){
                return "게임성공";
            }else if(guess<100&&guess>0){
                if(i==9){
                    break;
                }
                System.out.println("다시시도하세요");
            }else{
                System.out.println("1~100사이에 수 입력다시");
            }
            i++;
        }
        return "게임실패";
    
    
    }


    public String gameDoWhile(){
        int num = (int)(Math.random()*100)+1;
        int i = 0;
        System.out.println(num);
        do{
            System.out.print("생각하는 숫자를 입력하세요>>>>> ");
            Scanner scan = new Scanner(System.in);
            int guess = scan.nextInt();
            if(guess == num&&guess<100&&guess>0){
                return "게임성공";
            }else if(guess<100&&guess>0){
                if(i==9){
                    break;
                }
                System.out.println("다시시도하세요");
            }else{
                System.out.println("1~100사이에 수 입력다시");
            }
            i++;
        }while(i<10);
        return "게임실패";

    }

}
