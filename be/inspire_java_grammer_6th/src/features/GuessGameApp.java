package features;

import java.util.Scanner;

import features.game.GuessGame;

public class GuessGameApp {
    public static void main(String[] args) {
        GuessGame game = new GuessGame();
        // String result = game.gameFor();
        // String result = game.gameWhile();
        String result = game.gameDoWhile();
        System.out.println(result);

        

        // Scanner scan = new Scanner(System.in);
        // System.out.println("생각하는 숫자를 입력하세요>>>>> ");
        // int guess = scan.nextInt();
        // System.out.println("guess>>"+guess);1


    }
}
