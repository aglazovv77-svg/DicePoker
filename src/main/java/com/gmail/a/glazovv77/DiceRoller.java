package com.gmail.a.glazovv77;

public interface DiceRoller {

    void rollDice(int[] dices);

    int[] rerollSelectedDice(int[] diceRoll, int[] indexes);
}
