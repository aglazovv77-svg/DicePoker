package com.gmail.a.glazovv77;

import java.util.ArrayList;
import java.util.List;

public class CombinationManager {

    public int[] countFrequencies(int[] diceRoll) {
        int[] frequencies = new int[7];
        for (int value : diceRoll) {
            frequencies[value]++;
        }
        return frequencies;
    }

    public List<Integer> toSortedFrequencyList(int[] frequencies) {
        //TODO: исправить
        return new ArrayList<>();
    }

    public int detectCombination(int[] roll, int[] frequencies, List<Integer> frequencyList) {
        //TODO: исправить
        return 0;
    }
}
