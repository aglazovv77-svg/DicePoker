package com.gmail.a.glazovv77;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class CombinationResult {
    CombinationType combinationType;
    int score;

    @Override
    public String toString() {
        return combinationType + ", score = " + score;
    }
}