package com.gmail.a.glazovv77;

import java.util.Random;
import java.util.Scanner;

public class App {

    public static void main( String[] args ) {

        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        DiceRoller diceRoller = new DiceRollerImpl(random);
        DiceRerollInput diceRerollInput = new DiceRerollInput(scanner);
        RollPrinter rollPrinter = roll -> {};

        HumanPlayer humanPlayer = new HumanPlayer(diceRoller, diceRerollInput, rollPrinter);
        BotPlayer botPlayer = new BotPlayer(diceRoller);

        Game game = new Game(random, scanner, diceRoller, humanPlayer, botPlayer, diceRerollInput);

        humanPlayer.setRollPrinter(game);

        Game.printGreeting();

        String command = game.inputCommand();
        if(Game.isQuit(command)) {
            System.out.println("Вы вышли из игры");
            return;
        }
        game.start();
    }
}
