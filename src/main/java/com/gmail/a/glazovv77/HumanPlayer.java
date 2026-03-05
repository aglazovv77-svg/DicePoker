package com.gmail.a.glazovv77;

import lombok.Getter;
import java.util.Random;

@Getter
public class HumanPlayer {

    private int[] PLAYER_ROLL;
    private int playerScore;
    private Random random;

    public HumanPlayer(Random random) {
        this.PLAYER_ROLL = new int[5];
        this.playerScore = 0;
        this.random = random;
    }

    public void playerTurn() {
        for (int i = 0; i < 5; i++) {
            PLAYER_ROLL[i] = random.nextInt(6) + 1;
        }
    }

    public void addScore(int points) {
        playerScore += points;
    }
}
