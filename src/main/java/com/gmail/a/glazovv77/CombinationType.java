package com.gmail.a.glazovv77;

public enum CombinationType {
    SMALL_STRAIGHT("Младший стрит"),
    LARGE_STRAIGHT("Старший стрит"),
    FIVE_OF_A_KIND("Покер"),
    FOUR_OF_A_KIND("Каре"),
    FULL_HOUSE("Фул-хайз"),
    THREE_OF_A_KIND("Сет"),
    TWO_PAIR("Две пары"),
    ONE_PAIR("Одна пара"),
    HIGH_CARD("Старшая карта");

    private final String russianText;

    CombinationType(String russianText) {
        this.russianText = russianText;
    }

    public String getRussianText() {
        return russianText;
    }
}