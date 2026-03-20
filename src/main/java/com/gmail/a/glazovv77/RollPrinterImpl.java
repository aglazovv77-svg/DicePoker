package com.gmail.a.glazovv77;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RollPrinterImpl implements RollPrinter {

    private static final String SEPARATOR = "___________________________________";
    private static final String SMALL_STRAIGHT_TEXT = "Младший стрит: ";
    private static final String LARGE_STRAIGHT_TEXT = "Старший стрит: ";
    private static final String FIVE_OF_A_KIND_TEXT = "Покер: ";
    private static final String FOUR_OF_A_KIND_TEXT = "Каре: ";
    private static final String FULL_HOUSE_TEXT = "Фул-Хауз: ";
    private static final String THREE_OF_A_KIND_TEXT = "Сет: ";
    private static final String TWO_PAIR_TEXT = "Две пары: ";
    private static final String ONE_PAIR_TEXT = "Пара: ";
    private static final String HIGH_CARD_TEXT = "Старшая карта: ";
    private static final String TOTAL_SCORE_TEXT = "Общий счет: ";


    protected static final String START = "Y";
    protected static final String QUIT = "N";

    public static void printGreeting() {
        log.info("Игра Покер на костях \n Вы хотите начать игру [{}] или [{}] \n", START, QUIT);
    }

    protected void printRoll(String owner, int[] roll) {
        StringBuilder sb = new StringBuilder();

        sb.append("Комбинация ").append(owner).append(" = ");
        for (int value : roll) {
            sb.append(value).append(" ");
        }
        log.info(sb.toString());
    }

    @Override
    public void print(String owner, int[] roll) {
        printRoll(owner, roll);
    }

    static void printDetectCombination(CombinationResult result) {
       log.info(result.toString());
    }

    void printScore(String owner, int score) {
            log.info(TOTAL_SCORE_TEXT + owner + ": " + score);
            log.info(SEPARATOR);
    }
}
