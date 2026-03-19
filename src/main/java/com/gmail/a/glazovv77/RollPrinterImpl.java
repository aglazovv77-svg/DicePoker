package com.gmail.a.glazovv77;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RollPrinterImpl implements RollPrinter {

    private static final String SEPARATOR = "___________________________________";
    private static final String SMALL_STRAIGHT_TEXT = "Младший стрит: ";

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

    static void printDetectCombination(int points) {
        if (points == Game.SMALL_STRAIGHT_SCORE) {
            log.info(SMALL_STRAIGHT_TEXT + points);
            //TODO: вынести все строки в константы
        } else if (points == Game.LARGE_STRAIGHT) {
            log.info("Старший стрит: " + points);
        } else if (points == Game.FIVE_OF_A_KIND) {
            log.info("Покер: " + points);
        } else if (points == Game.FOUR_OF_A_KIND) {
            log.info("Каре: " + points);
        } else if (points == Game.FULL_HOUSE) {
            log.info("Фул-Хауз: " + points);
        } else if (points == Game.THREE_OF_A_KIND) {
            log.info("Сет: " + points);
        } else if (points == Game.twoPair) {
            log.info("Две пары: " + points);
        } else if (points == Game.onePair) {
            log.info("Пара: " + points);
        } else {
            log.info("Старшая карта: " + points);
        }
    }

    void printScore(String owner, int score) {
            log.info("Общий счет" + owner + ": " + score);
            log.info(SEPARATOR);
    }
}
