package com.gmail.a.glazovv77;

import lombok.Getter;

@Getter
public class BotPlayer {

    private final DiceRoller diceRoller;

    private final int[] botDices;
    private int botScore;

    public BotPlayer(DiceRoller diceRoller) {
        this.botDices = new int[5];
        this.botScore = 0;
        this.diceRoller = diceRoller;
    }

    public void botTurn() {
        diceRoller.rollDice(botDices);
    }

    public void addScore(int pointsBot) {
        botScore += pointsBot;
    }
}
