package com.gmail.a.glazovv77;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CombinationManager {

    protected static final int SMALL_STRAIGHT_SCORE = 15;
    protected static final int LARGE_STRAIGHT_SCORE = 25;
    protected static final int FIVE_OF_A_KIND_SCORE = 50;
    protected static final int FOUR_OF_A_KIND_SCORE = 35;
    protected static final int FULL_HOUSE_SCORE = 30;
    protected static final int THREE_OF_A_KIND_SCORE = 20;
    protected static int twoPairScore;
    protected static int onePairScore;
    protected static int highCardScore;



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

    public int detectCombination(int[] diceRoll, int[] frequencies, List<Integer> list) {
            if (isSmallStraight(diceRoll)) {
                return SMALL_STRAIGHT_SCORE;
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
            if (list.get(0) == 2 && list.get(1) == 2) {
                int sumDice = 0;
                for (int i = 0; i < frequencies.length; i++) {
                    if (frequencies[i] == 2) {
                        sumDice += i * 2;
                    }
                }
                return twoPairScore = sumDice;
            }
            if (list.get(0) == 2) {
                for (int i = 0; i < frequencies.length; i++) {
                    if (frequencies[i] == 2) {
                        onePairScore = i * 2;
                    }
                }
                return onePairScore;
            }
            for (int value : diceRoll) {
                if ((value > highCardScore)) {
                    highCardScore = value;
                }
            }
            return highCardScore;
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
