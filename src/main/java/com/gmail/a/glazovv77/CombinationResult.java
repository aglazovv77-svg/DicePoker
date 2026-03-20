package com.gmail.a.glazovv77;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CombinationResult {
    CombinationType combinationType;
    int score;

    @Override
    public String toString() {
        return STR."\{combinationType}, score=\{score}";
    }
}