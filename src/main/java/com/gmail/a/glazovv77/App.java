package com.gmail.a.glazovv77;

import java.util.Random;
import java.util.Scanner;

public class App {

    public static void main( String[] args ) {

        Random random = new Random();
        CombinationManager combinationManager = new CombinationManager();

        Scanner scanner = new Scanner(System.in);
        RollPrinterImpl rollPrinterImpl = new RollPrinterImpl();

        DiceRoller diceRoller = new DiceRollerImpl(random);
        DiceRerollInput diceRerollInput = new DiceRerollInput(scanner);

        HumanPlayer humanPlayer = new HumanPlayer(diceRoller, diceRerollInput, rollPrinterImpl);
        BotPlayer botPlayer = new BotPlayer(diceRoller);

        Game game = new Game(random, scanner, diceRoller, humanPlayer, botPlayer, diceRerollInput, combinationManager);

        Game.printGreeting();

        String command = game.inputCommand();
        if(Game.isQuit(command)) {
            System.out.println("Вы вышли из игры");
            return;
        }
        game.start();
    }
}
