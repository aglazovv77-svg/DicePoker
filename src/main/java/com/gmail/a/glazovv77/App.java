package com.gmail.a.glazovv77;

public class App
{
    public static void main( String[] args ) {

        Game.printGreeting();

        String command = Game.inputCommand();
        if(Game.isQuit(command)) {
            System.out.println("Вы вышли из игры");
            return;
        }
        Game.start();

    }
}
