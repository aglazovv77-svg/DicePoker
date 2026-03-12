package com.gmail.a.glazovv77;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@RequiredArgsConstructor
@Slf4j
public class DiceRerollInput {

    private final Scanner scanner;

    private static final String QUIT = "N";
    private static final String START = "Y";

    public int[] getRerollChoice() {
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
}
