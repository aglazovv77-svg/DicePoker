package com.gmail.a.glazovv77;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Game {

    private final Random random;
    private final Scanner scanner;
    private final DiceRoller diceRoller;
    private final HumanPlayer humanPlayer;
    private final BotPlayer botPlayer;
    private final DiceRerollInput diceRerollInput;

    private static final int WINNING_SCORE = 100;

    private static final String START = "Y";
    private static final String QUIT = "N";

    protected static final int SMALL_STRAIGHT = 15;
    protected static final int LARGE_STRAIGHT = 25;
    protected static final int FIVE_OF_A_KIND = 50;
    protected static final int FOUR_OF_A_KIND = 35;
    protected static final int FULL_HOUSE = 30;
    protected static final int THREE_OF_A_KIND = 20;
    protected static int twoPair;
    protected static int onePair;
    protected static int highCard;

    int[] PLAYER_ROLL;
    int[] BOT_ROLL;

    Printer printer = new Printer();

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

            PLAYER_ROLL = humanPlayer.getPlayerDices();
            humanPlayer.playerTurn();
            printer.printRoll("игрока ", PLAYER_ROLL);

            int[] frequencies = countFrequencies(PLAYER_ROLL);
            List<Integer> frequencyList = toSortedFrequencyList(frequencies);

            int points = detectCombination(PLAYER_ROLL, frequencies, frequencyList);
            humanPlayer.addScore(points);

            Printer.printDetectCombination(points);
            printer.printScore(" игрока", humanPlayer.getPlayerScore());

            botPlayer.botTurn();
            BOT_ROLL = botPlayer.getBotDices();
            printer.printRoll("бота ", BOT_ROLL);

            int[] frequenciesBots = countFrequencies(BOT_ROLL);
            List<Integer> frequencyListBot = toSortedFrequencyList(frequenciesBots);

            int pointsBot = detectCombination(BOT_ROLL, frequenciesBots, frequencyListBot);
            botPlayer.addScore(pointsBot);

            Printer.printDetectCombination(pointsBot);
            printer.printScore(" бота", botPlayer.getBotScore());

            if (isPlayer()) {
                log.info("Поздравляем вы выиграли, набрано очков = " + humanPlayer.getPlayerScore());
            } else if (isBot()) {
                log.info("Выиграл бот, набрано очков = " + botPlayer.getBotScore());
            }
        }
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
            return SMALL_STRAIGHT;
        }

        if (isLargeStraight(diceRoll)) {
            return LARGE_STRAIGHT;
        }

        if (list.get(0) == 5) {
            return FIVE_OF_A_KIND;
        }

        if (list.get(0) == 4) {
            return FOUR_OF_A_KIND;
        }

        if (list.get(0) == 3 && list.get(1) == 2) {
            return FULL_HOUSE;
        }

        if (list.get(0) == 3) {
            return THREE_OF_A_KIND;
        }

        if (list.get(0) == 2 && list.get(1) == 2) {
            int sumDice = 0;
            for (int i = 0; i < frequencies.length; i++) {
                if (frequencies[i] == 2) {
                    sumDice += i * 2;
                }
            }
            return twoPair = sumDice;
        }

        if (list.get(0) == 2) {
            for (int i = 0; i < frequencies.length; i++) {
                if (frequencies[i] == 2) {
                    onePair = i * 2;
                }
            }
            return onePair;
        }

        for (int value : diceRoll) {
            if ((value > highCard)) {
                highCard = value;
            }
        }
        return highCard;
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
