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
    private static final String IS_DRAW_TEXT = "НИЧЬЯ! Оба игрока набрали больше 100 очков";
    private static final String IS_PLAYER_WIN_TEXT = "Поздравляем вы выиграли, набрано очков = ";
    private static final String IS_BOT_WIN_TEXT = "Выиграл бот, набрано очков = ";

    int[] PLAYER_ROLL;
    int[] BOT_ROLL;

    RollPrinterImpl rollPrinterImpl = new RollPrinterImpl();

    public void start() {

        while (!isGameOver()) {
            //бросили
            PLAYER_ROLL = humanPlayer.getPlayerDices();
            //походили
            humanPlayer.playerTurn();
            //намечатали
            rollPrinterImpl.printRoll("игрока ", PLAYER_ROLL);

            //подготовили данные к подсчету и посчитали очки
            int[] frequencies = combinationManager.countFrequencies(PLAYER_ROLL);
            List<Integer> frequencyList = combinationManager.toSortedFrequencyList(frequencies);
            CombinationResult result = combinationManager.detectCombination(PLAYER_ROLL, frequencies, frequencyList);
            //добавили очки
            humanPlayer.addScore(result.getScore());
            //напечатали какая комбинация и сколько очков
            RollPrinterImpl.printDetectCombination(result);
            rollPrinterImpl.printScore(" игрока", humanPlayer.getPlayerScore());

            //ДУБЛИРОВАНИЕ логики как у игрока - вынести код и выше и этот в метод, использовать метод
            botPlayer.botTurn();
            BOT_ROLL = botPlayer.getBotDices();
            rollPrinterImpl.printRoll("бота ", BOT_ROLL);

            int[] frequenciesBots = combinationManager.countFrequencies(BOT_ROLL);
            List<Integer> frequencyListBot = combinationManager.toSortedFrequencyList(frequenciesBots);
            CombinationResult pointsBot = combinationManager.detectCombination(BOT_ROLL, frequenciesBots, frequencyListBot);

            botPlayer.addScore(pointsBot.getScore());

            RollPrinterImpl.printDetectCombination(pointsBot);
            rollPrinterImpl.printScore(" бота", botPlayer.getBotScore());

            if (isDraw()) {
                log.info(IS_DRAW_TEXT);
            } else if (isPlayerWin()) {
                 log.info(IS_PLAYER_WIN_TEXT + humanPlayer.getPlayerScore());
            } else if (isBotWin()) {
                log.info(IS_BOT_WIN_TEXT + botPlayer.getBotScore());
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
