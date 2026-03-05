package com.gmail.a.glazovv77;

public class App {

    private static Game game;

    public static void main( String[] args ) {

        game = new Game();

        Game.printGreeting();

        String command = Game.inputCommand();
        if(Game.isQuit(command)) {
            System.out.println("Вы вышли из игры");
            return;
        }
        game.start();
    }
}
