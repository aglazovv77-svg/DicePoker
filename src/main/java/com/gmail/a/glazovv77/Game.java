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

    private static final int SMALL_STRAIGHT = 15;
    private static final int LARGE_STRAIGHT = 25;
    private static final int FIVE_OF_A_KIND = 50;
    private static final int FOUR_OF_A_KIND = 35;
    private static final int FULL_HOUSE = 30;
    private static final int THREE_OF_A_KIND = 20;

    private static final String SEPARATOR = "___________________________________";

    private static final String START = "Y";
    private static final String QUIT = "N";

    int[] PLAYER_ROLL;
    int[] BOT_ROLL;

    private static int twoPair;
    private static int onePair;
    private static int highCard;

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

            PLAYER_ROLL = humanPlayer.getPlayerDices();
            humanPlayer.playerTurn();
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

        if (roll == PLAYER_ROLL) {
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

    private static void printDetectCombination(int points) {
        if(points == SMALL_STRAIGHT) {
            log.info("Младший стрит: " + SMALL_STRAIGHT);
        }
        else if(points == LARGE_STRAIGHT) {
            log.info("Старший стрит: " + LARGE_STRAIGHT);
        }
        else if(points == FIVE_OF_A_KIND) {
            log.info("Покер: " + FIVE_OF_A_KIND);
        }
        else if(points == FOUR_OF_A_KIND) {
            log.info("Каре: " + FOUR_OF_A_KIND);
        }
        else if(points == FULL_HOUSE) {
            log.info("Фул-Хауз: " + FULL_HOUSE);
        }
        else if(points == THREE_OF_A_KIND) {
            log.info("Сет: " + THREE_OF_A_KIND);
        }
        else if(points == twoPair) {
            log.info("Две пары: " + twoPair);
        }
        else if(points == onePair) {
            log.info("Пара: " + onePair);
        }
        else {
            log.info("Старшая карта: " + highCard);
        }
    }

    private void printScore(int score) {
        if (score == humanPlayer.getPlayerScore()) {
            log.info("Ваш общий счет : " + humanPlayer.getPlayerScore());
            log.info(SEPARATOR);
        } else {
            log.info("Общий счет бота: " + botPlayer.getBotScore());
            log.info(SEPARATOR);
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
