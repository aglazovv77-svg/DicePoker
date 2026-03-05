package com.gmail.a.glazovv77;

import lombok.Getter;
import java.util.Random;

@Getter
public class BotPlayer {

    private int[] BOT_ROLL;
    private int botScore;
    private Random random;

    public BotPlayer(Random random) {
        this.BOT_ROLL = new int[5];
        this.botScore = 0;
        this.random = random;
    }

    public void botTurn() {
        for (int i = 0; i < 5; i++) {
            BOT_ROLL[i] = random.nextInt(6) + 1;
        }
    }

    public void addScore(int pointsBot) {
        botScore += pointsBot;
    }

}
