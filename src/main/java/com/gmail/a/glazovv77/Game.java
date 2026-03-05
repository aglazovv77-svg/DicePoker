package com.gmail.a.glazovv77;

import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Game {

    private static final String START = "Y";
    private static final String QUIT = "N";

    int[] PLAYER_ROLL;
    int[] BOT_ROLL;

    private static final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();

    private static final int WINNING_SCORE = 100;

    // Refactored
    HumanPlayer humanPlayer = new HumanPlayer(random);
    BotPlayer botPlayer = new BotPlayer(random);

    public static void printGreeting() {
        log.info("Игра Покер на костях \n Вы хотите начать игру [{}] или [{}] \n", START, QUIT);
    }

    private static boolean isStart(String command) {
        return command.equals(START);
    }

    public static boolean isQuit(String command) {
        return command.equals(QUIT);
    }

    public static String inputCommand() {
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
            PLAYER_ROLL = humanPlayer.getPLAYER_ROLL();
            printRoll(PLAYER_ROLL);

            int[] choiceDiceIndexes = getRerollChoice();
            int[] diceRoll = rerollSelectedDice(PLAYER_ROLL, choiceDiceIndexes);
            printRoll(diceRoll);

            int[] frequencies = countFrequencies(diceRoll);
            List<Integer> frequencyList = toSortedFrequencyList(frequencies);

            int points = detectCombination(diceRoll, frequencies, frequencyList);
            humanPlayer.addScore(points);

            printDetectCombination(points);
            printScore(humanPlayer.getPlayerScore());

            botPlayer.botTurn();
            BOT_ROLL = botPlayer.getBOT_ROLL();
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

    private static int[] getRerollChoice() {
        log.info("Вы хотите перебросить какие-нибудь кости? Введите только [{}] или [{}] ", START, QUIT);
        String answer = scanner.nextLine().toUpperCase();

        if (answer.equals(QUIT)) {
            return new int[0];
        }
        log.info("Какие кость(и)(индекс(ы)) хотите перебросить?");
        log.info("Если несколько то укажите через пробел от 1 до 5 (например 1 3 5)");

        String input = scanner.nextLine().trim();
        String[] parts = input.split(" ");
        int[] choiceDiceIndexes = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            choiceDiceIndexes[i] = Integer.parseInt(parts[i]) - 1;
        }
        return choiceDiceIndexes;
    }

    private int[] rerollSelectedDice(int[] diceRoll, int[] indexes) {
        for (int index : indexes) {
            diceRoll[index] = random.nextInt(6) + 1;
        }
        return diceRoll;
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
