# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Compile
mvn compile

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=AppTest

# Build jar
mvn package

# Run the game
mvn exec:java -Dexec.mainClass="com.gmail.a.glazovv77.App"
```

## Architecture

This is a two-class console Dice Poker game with a Russian-language UI.

**`App.java`** — entry point only. Prints the greeting, reads the start/quit command, and delegates to `Game.start()`.

**`Game.java`** — contains all game logic as static methods and static state:
- `PLAYER_ROLL` / `BOT_ROLL`: static int[5] arrays holding the current dice rolls
- `playerScore` / `botScore`: cumulative scores; first to reach `WINNING_SCORE` (100) wins
- Each round: player rolls 5 dice → optionally rerolls selected dice by index → combination is detected and scored → bot rolls 5 dice (no reroll) → combination is detected and scored

**Combination scoring** (in `detectCombination`):
| Combination | Points |
|---|---|
| Small straight (1-2-3-4-5) | 15 |
| Large straight (2-3-4-5-6) | 25 |
| Poker (5-of-a-kind) | 50 |
| Four-of-a-kind | 35 |
| Full house | 30 |
| Three-of-a-kind | 20 |
| Two pairs | sum of the two pairs |
| One pair | sum of the pair |
| High die | value of highest die |

`printRoll` identifies whose roll it is by comparing the array reference (`roll == PLAYER_ROLL`), so passing a different array will print "bot".

`printScore` similarly identifies player vs bot score by comparing `score == playerScore`.

## Notes

- Java 25 source/target (configured in `pom.xml`)
- JUnit 3.x (`junit.framework.TestCase`) — `AppTest` only has a trivial placeholder test
