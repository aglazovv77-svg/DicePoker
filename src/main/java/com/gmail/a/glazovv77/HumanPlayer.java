package com.gmail.a.glazovv77;

import lombok.Getter;
import lombok.Setter;

@Getter
public class HumanPlayer {

    private final int[] playerDices;
    private int playerScore;
    private int[] choiceDiceIndexes;
    private final DiceRoller diceRoller;
    private final DiceRerollInput diceRerollInput;

    @Setter
    private RollPrinter rollPrinter;

    public HumanPlayer(DiceRoller diceRoller, DiceRerollInput diceRerollInput, RollPrinter printer) {
        this.playerDices = new int[5];
        this.playerScore = 0;
        this.diceRoller = diceRoller;
        this.diceRerollInput = diceRerollInput;
        this.rollPrinter = printer;
    }

    public void playerTurn() {
        diceRoller.rollDice(playerDices);
        rollPrinter.print("игрока", playerDices);
        //TODO:
        // запрашиваем переброс - написать класс для ввода и вынести методы ввода
        choiceDiceIndexes = diceRerollInput.getRerollChoice();
        // делаем переброс - уже есть DiceRoller
        diceRoller.rerollSelectedDice(playerDices, choiceDiceIndexes);
    }

    public void addScore(int points) {
        playerScore += points;
    }
}
