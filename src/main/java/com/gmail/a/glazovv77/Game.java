package com.gmail.a.glazovv77;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Game {

    private final HumanPlayer humanPlayer;
    private final BotPlayer botPlayer;
    private final CombinationManager combinationManager;

    private static final int WINNING_SCORE = 100;

    int[] PLAYER_ROLL;
    int[] BOT_ROLL;

    RollPrinterImpl rollPrinterImpl = new RollPrinterImpl();

    public void start() {

        while (!isGameOver()) {

            PLAYER_ROLL = humanPlayer.getPlayerDices();
            humanPlayer.playerTurn();
            rollPrinterImpl.printRoll("игрока ", PLAYER_ROLL);

            int[] frequencies = combinationManager.countFrequencies(PLAYER_ROLL);
            List<Integer> frequencyList = combinationManager.toSortedFrequencyList(frequencies);
            int points = combinationManager.detectCombination(PLAYER_ROLL, frequencies, frequencyList);

            humanPlayer.addScore(points);

            RollPrinterImpl.printDetectCombination(points);
            rollPrinterImpl.printScore(" игрока", humanPlayer.getPlayerScore());

            botPlayer.botTurn();
            BOT_ROLL = botPlayer.getBotDices();
            rollPrinterImpl.printRoll("бота ", BOT_ROLL);

            int[] frequenciesBots = combinationManager.countFrequencies(BOT_ROLL);
            List<Integer> frequencyListBot = combinationManager.toSortedFrequencyList(frequenciesBots);
            int pointsBot = combinationManager.detectCombination(BOT_ROLL, frequenciesBots, frequencyListBot);

            botPlayer.addScore(pointsBot);

            RollPrinterImpl.printDetectCombination(pointsBot);
            rollPrinterImpl.printScore(" бота", botPlayer.getBotScore());

            if (isDraw()) {
                log.info("Ничья! Оба игрока набрали больше 100 очков");
            } else if (isPlayerWin()) {
                 log.info("Поздравляем вы выиграли, набрано очков = " + humanPlayer.getPlayerScore());
            } else if (isBotWin()) {
                log.info("Выиграл бот, набрано очков = " + botPlayer.getBotScore());
            }
        }
    }

    private boolean isGameOver() {
        return isPlayerWin() || isBotWin();
    }

    private boolean isPlayerWin() {
        return humanPlayer.getPlayerScore() >= WINNING_SCORE;
    }

    private boolean isBotWin() {
        return botPlayer.getBotScore() >= WINNING_SCORE;
    }

    private boolean isDraw() {
        return isPlayerWin() && isBotWin();
    }
}
