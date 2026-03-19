package com.gmail.a.glazovv77;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
@RequiredArgsConstructor
public class InputManagerGame {

    protected static final String START = "Y";
    protected static final String QUIT = "N";

    private final Scanner scanner;

    public static boolean isStart(String command) {
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
}
