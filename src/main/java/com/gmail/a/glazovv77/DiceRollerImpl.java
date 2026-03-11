package com.gmail.a.glazovv77;

import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class DiceRollerImpl implements DiceRoller {

    private final Random random;

    @Override
    public void rollDice(int[] dices) {
        for (int i = 0; i < 5; i++) {
            dices[i] = random.nextInt(6) + 1;
        }
    }

    @Override
    public int[] rerollSelectedDice(int[] diceRoll, int[] indexes) {
        for (int index : indexes) {
            diceRoll[index] = random.nextInt(6) + 1;
        }
        return diceRoll;
    }
}