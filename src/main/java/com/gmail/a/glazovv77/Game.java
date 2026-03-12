package com.gmail.a.glazovv77;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Game implements RollPrinter {

    private final Random random;
    private final Scanner scanner;
    private final DiceRoller diceRoller;
    private final HumanPlayer humanPlayer;
    private final BotPlayer botPlayer;
    private final DiceRerollInput diceRerollInput;

    private static final String START = "Y";
    private static final String QUIT = "N";

    int[] PLAYER_ROLL;
    int[] BOT_ROLL;

    private static final int WINNING_SCORE = 100;

    public static void printGreeting() {
        log.info("Игра Покер на костях \n Вы хотите начать игру [{}] или [{}] \n", START, QUIT);
    }

    private static boolean isStart(String command) {
        return command.equals(START);
    }

    public static boolean isQuit(String command) {
        return command.equals(QUIT);
    }

    public String inputCommand() {
        while (true) {
            String command = scanner.nextLine().toUpperCase();
            if (isStart(command) || isQuit(command)) {
                return command;
            }
            log.info("Вы вводите неверное значение!!!");
        }
    }

    public void start() {

        while (!isGameOver()) {

            humanPlayer.playerTurn();
            PLAYER_ROLL = humanPlayer.getPlayerDices();
            printRoll(PLAYER_ROLL);

            int[] frequencies = countFrequencies(PLAYER_ROLL);
            List<Integer> frequencyList = toSortedFrequencyList(frequencies);

            int points = detectCombination(PLAYER_ROLL, frequencies, frequencyList);
            humanPlayer.addScore(points);

            printDetectCombination(points);
            printScore(humanPlayer.getPlayerScore());

            botPlayer.botTurn();
            BOT_ROLL = botPlayer.getBotDices();
            printRoll(BOT_ROLL);

            int[] frequenciesBots = countFrequencies(BOT_ROLL);
            List<Integer> frequencyListBot = toSortedFrequencyList(frequenciesBots);

            int pointsBot = detectCombination(BOT_ROLL, frequenciesBots, frequencyListBot);
            botPlayer.addScore(pointsBot);

            printDetectCombination(pointsBot);
            printScore(botPlayer.getBotScore());

            if (isPlayer()) {
                log.info("Поздравляем вы выиграли, набрано очков = " + humanPlayer.getPlayerScore());
            } else if (isBot()) {
                log.info("Выиграл бот, набрано очков = " + botPlayer.getBotScore());
            }
        }
    }

    private void printRoll(int[] roll) {
        StringBuilder sb = new StringBuilder();

        if (Arrays.equals(roll, PLAYER_ROLL)) {
            sb.append("Комбинация игрока = ");
        } else {
            sb.append("Комбинация бота = ");
        }
        for (int value : roll) {
            sb.append(value).append(" ");
        }
        log.info(sb.toString());
    }

    @Override
    public void print(int[] roll) {
        printRoll(roll);
    }

    private static int[] countFrequencies(int[] diceRoll) {
        int[] frequencies = new int[7];
        for (int value : diceRoll) {
            frequencies[value]++;
        }
        return frequencies;
    }

    private static List<Integer> toSortedFrequencyList(int[] frequencies) {
        List<Integer> frequencyList = new ArrayList<>();
        for (int frequency : frequencies) {
            if (frequency > 0) {
                frequencyList.add(frequency);
            }
        }
        frequencyList.sort(Collections.reverseOrder());
        return frequencyList;
    }

    private static int detectCombination(int[] diceRoll, int[] frequencies, List<Integer> list) {

        if (isSmallStraight(diceRoll)) {
            log.info("Младший стрит: ");
            return 15;
        }
        if (isLargeStraight(diceRoll)) {
            log.info("Старший стрит: ");
            return 25;
        }
        if (list.get(0) == 5) {
            log.info("Покер: ");
            return 50;
        }
        if (list.get(0) == 4) {
            log.info("Каре: ");
            return 35;
        }
        if (list.get(0) == 3 && list.get(1) == 2) {
            log.info("Фул-хауз: ");
            return 30;
        }
        if (list.get(0) == 3) {
            log.info("Сет: ");
            return 20;
        }
        if (list.get(0) == 2 && list.get(1) == 2) {
            int sumDice = 0;
            for (int i = 0; i < frequencies.length; i++) {
                if (frequencies[i] == 2) {
                    sumDice += i * 2;
                }
            }
            log.info("Две пары: ");
            return sumDice;
        }
        if (list.get(0) == 2) {
            int sumDice = 0;
            for (int i = 0; i < frequencies.length; i++) {
                if (frequencies[i] == 2) {
                    sumDice = i * 2;
                }
            }
            log.info("Пара: ");
            return sumDice;
        }
        int maxDice = Integer.MIN_VALUE;
        for (int value : diceRoll) {
            if ((value > maxDice)) {
                maxDice = value;
            }
        }
        log.info("Старшая карта: ");
        return maxDice;
    }

    private static boolean isSmallStraight(int[] dice) {
        int[] sorted = dice.clone();
        Arrays.sort(sorted);
        return Arrays.equals(sorted, new int[]{1, 2, 3, 4, 5});
    }

    private static boolean isLargeStraight(int[] dice) {
        int[] sorted = dice.clone();
        Arrays.sort(sorted);
        return Arrays.equals(sorted, new int[]{2, 3, 4, 5, 6});
    }

    private static void printDetectCombination(int points) {
        log.info("{}", points);
    }

    private void printScore(int score) {
        if (score == humanPlayer.getPlayerScore()) {
            log.info("Ваш общий счет : " + humanPlayer.getPlayerScore());
            log.info("___________________________________");
        } else {
            log.info("Общий счет бота: " + botPlayer.getBotScore());
            log.info("___________________________________");
        }
    }

    private boolean isGameOver() {
        return isPlayer() || isBot();
    }

    private boolean isPlayer() {
        return humanPlayer.getPlayerScore() >= WINNING_SCORE;

    }

    private boolean isBot() {
        return botPlayer.getBotScore() >= WINNING_SCORE;
    }
}
