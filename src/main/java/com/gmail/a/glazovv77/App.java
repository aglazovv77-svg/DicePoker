package com.gmail.a.glazovv77;

import java.util.Random;
import java.util.Scanner;

public class App {

    static void main(String[] args) {

        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        InputManagerGame inputManagerGame = new InputManagerGame(scanner);

        CombinationManager combinationManager = new CombinationManager();
        RollPrinterImpl rollPrinterImpl = new RollPrinterImpl();

        DiceRoller diceRoller = new DiceRollerImpl(random);
        DiceRerollInput diceRerollInput = new DiceRerollInput(scanner);

        HumanPlayer humanPlayer = new HumanPlayer(diceRoller, diceRerollInput, rollPrinterImpl);
        BotPlayer botPlayer = new BotPlayer(diceRoller);

        Game game = new Game(humanPlayer, botPlayer, combinationManager);

        RollPrinterImpl.printGreeting();

        String command = inputManagerGame.inputCommand();
        if(InputManagerGame.isQuit(command)) {
            System.out.println("Вы вышли из игры");
            return;
        }
        game.start();
    }
}
