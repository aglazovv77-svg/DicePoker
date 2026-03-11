package com.gmail.a.glazovv77;

import lombok.Getter;

@Getter
public class HumanPlayer {

    private int[] playerDices;
    private int playerScore;
    private DiceRoller diceRoller;

    public HumanPlayer(DiceRoller diceRoller) {
        this.playerDices = new int[5];
        this.playerScore = 0;
        this.diceRoller = diceRoller;
    }

    public void playerTurn() {
        diceRoller.rollDice(playerDices);
        //TODO:
        // запрашиваем переброс - написать класс для ввода и вынести методы ввода
        // делаем переброс - уже есть DiceRoller
    }

    public void addScore(int points) {
        playerScore += points;
    }
}
