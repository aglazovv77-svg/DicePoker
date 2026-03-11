package com.gmail.a.glazovv77;

import java.util.Random;

public class App {

    public static void main( String[] args ) {

        Random random = new Random();
        DiceRoller diceRoller = new DiceRollerImpl(random);
        HumanPlayer humanPlayer = new HumanPlayer(diceRoller);
        BotPlayer botPlayer = new BotPlayer(diceRoller);
        Game game = new Game(random, diceRoller,humanPlayer, botPlayer);

        game.printGreeting();

        String command = Game.inputCommand();
        if(Game.isQuit(command)) {
            System.out.println("Вы вышли из игры");
            return;
        }
        game.start();
    }
}
