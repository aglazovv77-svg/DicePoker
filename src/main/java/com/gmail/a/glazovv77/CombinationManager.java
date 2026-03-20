package com.gmail.a.glazovv77;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.gmail.a.glazovv77.CombinationScoreStorage.*;
import static com.gmail.a.glazovv77.CombinationType.*;

public class CombinationManager {

    protected static int twoPairScore;
    protected static int onePairScore;




    public int[] countFrequencies(int[] diceRoll) {
        int[] frequencies = new int[7];
        for (int value : diceRoll) {
            frequencies[value]++;
        }
        return frequencies;
    }

    public List<Integer> toSortedFrequencyList(int[] frequencies) {
            List<Integer> frequencyList = new ArrayList<>();
            for (int frequency : frequencies) {
                if (frequency > 0) {
                    frequencyList.add(frequency);
                }
            }
            frequencyList.sort(Collections.reverseOrder());
            return frequencyList;
        }

    public CombinationResult detectCombination(int[] diceRoll, int[] frequencies, List<Integer> list) {
            if (isSmallStraight(diceRoll)) {
                return new CombinationResult(SMALL_STRAIGHT, SMALL_STRAIGHT_SCORE);
            }
            if (isLargeStraight(diceRoll)) {
                return LARGE_STRAIGHT_SCORE;
            }
            if (list.get(0) == 5) {
                return FIVE_OF_A_KIND_SCORE;
            }
            if (list.get(0) == 4) {
                return FOUR_OF_A_KIND_SCORE;
            }
            if (list.get(0) == 3 && list.get(1) == 2) {
                return FULL_HOUSE_SCORE;
            }
            if (list.get(0) == 3) {
                return THREE_OF_A_KIND_SCORE;
            }
            //две пары
            if (list.get(0) == 2 && list.get(1) == 2) {
                int sumDice = 0;
                for (int i = 0; i < frequencies.length; i++) {
                    if (frequencies[i] == 2) {
                        sumDice += i * 2;
                    }
                }
                return new CombinationResult(TWO_PAIR, sumDice);
            }
            //одна пара
            if (list.get(0) == 2) {
                int sumDice = 0;
                for (int i = 0; i < frequencies.length; i++) {
                    if (frequencies[i] == 2) {
                        sumDice += i * 2;
                    }
                }
                return new CombinationResult(ONE_PAIR, sumDice);
            }
            int max = 0;
            for (int value : diceRoll) {

                if ((value > max)) {
                    max = value;
                }
            }
            return new CombinationResult(HIGH_CARD, max);
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
}
