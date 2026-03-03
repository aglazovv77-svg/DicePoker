package com.gmail.a.glazovv77;

import java.util.*;

public class Game {

    private static final String START = "Y";
    private static final String QUIT = "N";

    private static int[] PLAYER_ROLL = new int[5];
    private static int[] BOT_ROLL = new int[5];

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    private static final int WINNING_SCORE = 100;

    private static int playerScore = 0;
    private static int botScore = 0;

    public static void printGreeting() {
        System.out.printf("Игра Покер на костях \n Вы хотите начать игру [%s] или [%s] \n", START, QUIT);
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
            System.out.println("Вы вводите неверное значение!!!");
        }
    }

    public static void start() {

        while (!isGameOver()) {

            PLAYER_ROLL = playerTurn();
            printRoll(PLAYER_ROLL);

            int[] choiceDiceIndexes = getRerollChoice();
            int[] diceRoll = rerollSelectedDice(PLAYER_ROLL, choiceDiceIndexes);
            printRoll(diceRoll);

            int[] frequencies = countFrequencies(diceRoll);
            List<Integer> frequencyList = toSortedFrequencyList(frequencies);

            int points = detectCombination(diceRoll, frequencies, frequencyList);
            playerScore += points;

            printDetectCombination(points);
            printScore(playerScore);

            BOT_ROLL = botTurn();
            printRoll(BOT_ROLL);

            int[] frequenciesBots = countFrequencies(BOT_ROLL);
            List<Integer> frequencyListBot = toSortedFrequencyList(frequenciesBots);

            int pointsBot = detectCombination(BOT_ROLL, frequenciesBots, frequencyListBot);
            botScore += pointsBot;

            printDetectCombination(pointsBot);
            printScore(botScore);

            if (isPlayer()) {
                System.out.println("Поздравляем вы выиграли, набрано очков = " + playerScore);
            } else if (isBot()) {
                System.out.println("Выиграл бот, набрано очков = " + botScore);
            }
        }
    }

    private static int[] playerTurn() {
        for (int i = 0; i < 5; i++) {
            PLAYER_ROLL[i] = random.nextInt(6) + 1;
        }
        return PLAYER_ROLL;
    }

    private static void printRoll(int[] roll) {
        if (roll == PLAYER_ROLL) {
            System.out.print("Комбинация игрока = ");
        } else {
            System.out.print("Комбинация бота = ");
        }
        for (int value : roll) {
            System.out.print(value);
        }
        System.out.println();
    }

    private static int[] getRerollChoice() {
        System.out.printf("Вы хотите перебросить какие-нибудь кости? Введите только [%s] или [%s] \n", START, QUIT);
        String answer = scanner.nextLine().toUpperCase();

        if (answer.equals(QUIT)) {
            return new int[0];
        }
        System.out.println("Какие кость(и)(индекс(ы)) хотите перебросить?");
        System.out.println("Если несколько то укажите через пробел от 1 до 5 (например 1 3 5)");

        String input = scanner.nextLine().trim();
        String[] parts = input.split(" ");
        int[] choiceDiceIndexes = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            choiceDiceIndexes[i] = Integer.parseInt(parts[i]) - 1;
        }
        return choiceDiceIndexes;
    }

    private static int[] rerollSelectedDice(int[] diceRoll, int[] indexes) {
        for (int index : indexes) {
            diceRoll[index] = random.nextInt(6) + 1;
        }
        return diceRoll;
    }

    private static int[] botTurn() {
        for (int i = 0; i < 5; i++) {
            BOT_ROLL[i] = random.nextInt(6) + 1;
        }
        return BOT_ROLL;
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
            System.out.print("Младший стрит: ");
            return 15;
        }
        if (isLargeStraight(diceRoll)) {
            System.out.print("Старший стрит: ");
            return 25;
        }
        if (list.get(0) == 5) {
            System.out.print("Покер: ");
            return 50;
        }
        if (list.get(0) == 4) {
            System.out.print("Каре: ");
            return 35;
        }
        if (list.get(0) == 3 && list.get(1) == 2) {
            System.out.print("Фул-хауз: ");
            return 30;
        }
        if (list.get(0) == 3) {
            System.out.print("Сет: ");
            return 20;
        }
        if (list.get(0) == 2 && list.get(1) == 2) {
            int sumDice = 0;
            for (int i = 0; i < frequencies.length; i++) {
                if (frequencies[i] == 2) {
                    sumDice += i * 2;
                }
            }
            System.out.print("Две пары: ");
            return sumDice;
        }
        if (list.get(0) == 2) {
            int sumDice = 0;
            for (int i = 0; i < frequencies.length; i++) {
                if (frequencies[i] == 2) {
                    sumDice = i * 2;
                }
            }
            System.out.print("Пара: ");
            return sumDice;
        }
        int maxDice = Integer.MIN_VALUE;
        for (int value : diceRoll) {
            if ((value > maxDice)) {
                maxDice = value;
            }
        }
        System.out.print("Старшая карта: ");
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
        System.out.println(points);
    }

    private static void printScore(int score) {
        if (score == playerScore) {
            System.out.println("Ваш общий счет : " + playerScore);
            System.out.println("___________________________________");
        } else {
            System.out.println("Общий счет бота: " + botScore);
            System.out.println("___________________________________");
        }
    }

    private static boolean isGameOver() {
        return isPlayer() || isBot();
    }

    private static boolean isPlayer() {
        return playerScore >= WINNING_SCORE;

    }

    private static boolean isBot() {
        return botScore >= WINNING_SCORE;
    }
}
