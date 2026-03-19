# План рефакторинга DicePoker

## Текущие проблемы

### SOLID-нарушения
- **SRP**: `Game` делает всё — бросает кости, читает ввод, обнаруживает комбинации, выводит данные, управляет игровым циклом
- **OCP**: `detectCombination` — цепочка `if/else`; добавление новой комбинации требует правки метода
- **DIP**: `Game` сам создаёт `Scanner` и `Random`; нет инъекции зависимостей

### DRY-нарушения
- `playerTurn()` и `botTurn()` дублируют логику броска 5 костей
- `printRoll(roll)` и `printScore(score)` определяют владельца через сравнение ссылок/значений (`roll == PLAYER_ROLL`, `score == playerScore`) — хрупкий хак
- Блок вычисления комбинации + очков повторяется дважды в `start()` (для игрока и бота)

### KISS-нарушения
- Глобальный статический изменяемый стейт невозможно тестировать
- Два параллельных представления одного броска (`int[]` + `List<Integer>`) прокидываются вместе везде
- JUnit 3.x без реальных тестов бизнес-логики

---

## Целевая структура пакетов

```
com.gmail.a.glazovv77
├── App.java
├── config/
│   └── GameConfig.java            — константы: WINNING_SCORE, DICE_COUNT, DICE_SIDES
├── dice/
│   ├── DiceRoll.java              — иммутабельный value-объект (int[])
│   └── DiceRoller.java            — бросает кости (инжектируемый Random)
├── combination/
│   ├── Combination.java           — enum: POKER, FOUR_OF_A_KIND, ..., HIGH_DIE
│   ├── CombinationResult.java     — value-объект: Combination + int points
│   ├── CombinationRule.java       — @FunctionalInterface: evaluate(DiceRoll) → Optional<CombinationResult>
│   ├── CombinationDetector.java   — интерфейс: detect(DiceRoll) → CombinationResult
│   └── StandardCombinationDetector.java — реализация через список CombinationRule
├── player/
│   ├── Player.java                — интерфейс: getName(), takeTurn(DiceRoller)
│   ├── HumanPlayer.java           — с логикой перебрасывания через InputReader
│   └── BotPlayer.java             — простой бросок без перебрасывания
├── io/
│   ├── InputReader.java           — интерфейс: readLine(), readYesNo()
│   ├── OutputWriter.java          — интерфейс: print(String), println(String), printRoll(...), printScore(...)
│   ├── ConsoleInputReader.java    — реализация через Scanner
│   └── ConsoleOutputWriter.java   — реализация через System.out + SLF4J/@Slf4j
├── game/
│   ├── GameState.java             — хранит очки игрока и бота
│   └── Game.java                  — оркестратор игрового цикла
```

---

## Детальный план по шагам

### Шаг 1 — `GameConfig` (KISS, DRY)
```java
@UtilityClass  // Lombok
public class GameConfig {
    public static final int WINNING_SCORE = 100;
    public static final int DICE_COUNT = 5;
    public static final int DICE_SIDES = 6;
    public static final String CMD_YES = "Y";
    public static final String CMD_NO = "N";
}
```
Убираем все магические числа и строки из `Game`.

---

### Шаг 2 — `DiceRoll` (SRP, DRY)
```java
@Value  // Lombok: иммутабельный, equals/hashCode по значению
public class DiceRoll {
    int[] values;  // длина всегда DICE_COUNT

    /** Возвращает частоты значений костей (индекс = значение 1–6). */
    public int[] frequencies() { ... }

    /** Возвращает отсортированный по убыванию список ненулевых частот. */
    public List<Integer> sortedFrequencies() { ... }

    /** Возвращает максимальное значение среди костей. */
    public int max() { ... }
}
```
Два метода (`countFrequencies`, `toSortedFrequencyList`) переезжают сюда — больше не прокидываются отдельно.

---

### Шаг 3 — `DiceRoller` (DIP, DRY)
```java
@RequiredArgsConstructor  // Lombok
public class DiceRoller {
    private final Random random;

    /** Бросает {@code count} кубиков с {@code sides} гранями. */
    public DiceRoll roll(int count, int sides) { ... }

    /** Перебрасывает кости по указанным индексам (0-based). */
    public DiceRoll reroll(DiceRoll original, int[] indexes, int sides) { ... }
}
```
`Random` инжектируется — легко тестировать с `new Random(seed)`.

---

### Шаг 4 — `Combination` enum + `CombinationResult` (OCP, SRP)

```java
public enum Combination {
    POKER("Покер"),
    FOUR_OF_A_KIND("Каре"),
    FULL_HOUSE("Фул-хауз"),
    LARGE_STRAIGHT("Старший стрит"),
    SMALL_STRAIGHT("Младший стрит"),
    THREE_OF_A_KIND("Сет"),
    TWO_PAIRS("Две пары"),
    ONE_PAIR("Пара"),
    HIGH_DIE("Старшая карта");

    private final String displayName;
}
```

```java
@Value  // Lombok
public class CombinationResult {
    Combination combination;
    int points;
}
```

---

### Шаг 5 — `CombinationRule` + `StandardCombinationDetector` (OCP, SRP)

```java
@FunctionalInterface
public interface CombinationRule {
    /** @return пустой Optional если комбинация не подходит */
    Optional<CombinationResult> evaluate(DiceRoll roll);
}
```

```java
public interface CombinationDetector {
    /** Определяет лучшую комбинацию в броске. */
    CombinationResult detect(DiceRoll roll);
}
```

`StandardCombinationDetector` хранит `List<CombinationRule>` в порядке приоритета (от старшей к младшей). Каждое правило — отдельный метод или лямбда. Добавление новой комбинации = добавление нового правила в список, **без изменения существующего кода** (OCP).

```java
public class StandardCombinationDetector implements CombinationDetector {
    private final List<CombinationRule> rules = List.of(
        this::checkPoker,
        this::checkFourOfAKind,
        this::checkFullHouse,
        this::checkLargeStraight,
        this::checkSmallStraight,
        this::checkThreeOfAKind,
        this::checkTwoPairs,
        this::checkOnePair,
        this::checkHighDie
    );

    @Override
    public CombinationResult detect(DiceRoll roll) {
        return rules.stream()
            .map(rule -> rule.evaluate(roll))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow();
    }

    private Optional<CombinationResult> checkPoker(DiceRoll roll) { ... }
    // ...
}
```

---

### Шаг 6 — `Player` интерфейс + реализации (SRP, DIP)

```java
public interface Player {
    /** @return имя игрока для вывода */
    String getName();

    /**
     * Выполняет ход: бросает кости и опционально перебрасывает.
     * @param roller источник случайности
     * @return итоговый бросок
     */
    DiceRoll takeTurn(DiceRoller roller);
}
```

`HumanPlayer` принимает `InputReader` и `OutputWriter` в конструкторе (`@RequiredArgsConstructor`).
`BotPlayer` принимает только имя (`@RequiredArgsConstructor`).

---

### Шаг 7 — `io` слой (DIP, SRP)

```java
public interface InputReader {
    /** Читает строку и приводит к верхнему регистру. */
    String readLine();

    /** Читает Y/N ответ в цикле до получения корректного. */
    boolean readYesNo();

    /**
     * Читает список индексов костей (1-based) для перебрасывания.
     * @return массив 0-based индексов
     */
    int[] readDiceIndexes();
}

public interface OutputWriter {
    void println(String message);
    void printRoll(String ownerName, DiceRoll roll);
    void printCombination(CombinationResult result);
    void printScore(String ownerName, int score);
    void printWinner(String name, int score);
}
```

`ConsoleInputReader` — `@Slf4j` + `Scanner`.
`ConsoleOutputWriter` — `@Slf4j` + `System.out` для игровых сообщений.

---

### Шаг 8 — `GameState` (SRP)

```java
@Getter  // Lombok
public class GameState {
    private int playerScore = 0;
    private int botScore = 0;

    public void addPlayerScore(int points) { playerScore += points; }
    public void addBotScore(int points) { botScore += points; }
    public boolean isOver(int winningScore) { return playerScore >= winningScore || botScore >= winningScore; }
    public boolean playerWon(int winningScore) { return playerScore >= winningScore; }
}
```

---

### Шаг 9 — `Game` (оркестратор, DIP)

```java
@RequiredArgsConstructor  // Lombok: все зависимости через конструктор
@Slf4j
public class Game {
    private final Player human;
    private final Player bot;
    private final DiceRoller roller;
    private final CombinationDetector detector;
    private final OutputWriter output;
    private final GameState state;

    /** Запускает игровой цикл до победы одного из участников. */
    public void start() {
        while (!state.isOver(GameConfig.WINNING_SCORE)) {
            playTurn(human, state::addPlayerScore);
            if (!state.isOver(GameConfig.WINNING_SCORE)) {
                playTurn(bot, state::addBotScore);
            }
        }
        announceWinner();
    }

    private void playTurn(Player player, IntConsumer addScore) {
        DiceRoll roll = player.takeTurn(roller);
        output.printRoll(player.getName(), roll);
        CombinationResult result = detector.detect(roll);
        output.printCombination(result);
        addScore.accept(result.getPoints());
        output.printScore(player.getName(), /* текущий счёт */ ...);
    }
}
```

---

### Шаг 10 — `App` + сборка зависимостей (DIP)

```java
public class App {
    public static void main(String[] args) {
        ConsoleInputReader input = new ConsoleInputReader();
        ConsoleOutputWriter output = new ConsoleOutputWriter();

        output.println("Игра Покер на костях");
        if (!input.readYesNo()) {
            output.println("Вы вышли из игры");
            return;
        }

        DiceRoller roller = new DiceRoller(new Random());
        CombinationDetector detector = new StandardCombinationDetector();
        Player human = new HumanPlayer("Игрок", input, output);
        Player bot = new BotPlayer("Бот");
        GameState state = new GameState();
        Game game = new Game(human, bot, roller, detector, output, state);
        game.start();
    }
}
```

---

### Шаг 11 — Тесты (JUnit 5)

Обновить `pom.xml`: JUnit 3 → JUnit 5 (`junit-jupiter`).

Написать тесты:
- `DiceRollTest` — `frequencies()`, `sortedFrequencies()`, `max()`
- `StandardCombinationDetectorTest` — каждая комбинация отдельным тест-методом (фиксированные броски)
- `DiceRollerTest` — с `new Random(seed)` для детерминированного результата
- `GameStateTest` — добавление очков, `isOver()`, `playerWon()`

---

### Шаг 12 — `pom.xml`

- Обновить JUnit 3.8.1 → JUnit 5 Jupiter (5.x)
- Добавить `maven-surefire-plugin` для JUnit 5
- Версия компилятора `25` — оставить

---

## План рефакторинга метода detectCombination (без паттерна Стратегия)

### Текущая проблема
Метод `detectCombination` имеет когнитивную сложность 23 (превышает допустимые 15) из-за:
- 8 последовательных if-условий
- Вложенных циклов внутри if-блоков
- Сложной логики вычисления очков

### Цель
Снизить когнитивную сложность до ≤ 15 путём разбиения на маленькие методы без использования паттерна Стратегия.

### План рефакторинга

#### Шаг 1 - Извлечь методы вычисления очков
```java
private static int calculateTwoPairsScore(int[] frequencies) {
    int sumDice = 0;
    for (int i = 0; i < frequencies.length; i++) {
        if (frequencies[i] == 2) {
            sumDice += i * 2;
        }
    }
    return sumDice;
}

private static int calculatePairScore(int[] frequencies) {
    for (int i = 0; i < frequencies.length; i++) {
        if (frequencies[i] == 2) {
            return i * 2;
        }
    }
    return 0;
}

private static int findMaxDice(int[] diceRoll) {
    int maxDice = Integer.MIN_VALUE;
    for (int value : diceRoll) {
        if (value > maxDice) {
            maxDice = value;
        }
    }
    return maxDice;
}
```

#### Шаг 2 - Извлечь методы проверки комбинаций
```java
private static boolean isPoker(List<Integer> list) {
    return list.get(0) == 5;
}

private static boolean isFourOfAKind(List<Integer> list) {
    return list.get(0) == 4;
}

private static boolean isFullHouse(List<Integer> list) {
    return list.get(0) == 3 && list.get(1) == 2;
}

private static boolean isThreeOfAKind(List<Integer> list) {
    return list.get(0) == 3;
}

private static boolean isTwoPairs(List<Integer> list) {
    return list.get(0) == 2 && list.get(1) == 2;
}

private static boolean isOnePair(List<Integer> list) {
    return list.get(0) == 2;
}
```

#### Шаг 3 - Извлечь методы получения очков для комбинаций
```java
private static int getPokerScore() {
    log.info("Покер: ");
    return 50;
}

private static int getFourOfAKindScore() {
    log.info("Каре: ");
    return 35;
}

private static int getFullHouseScore() {
    log.info("Фул-хауз: ");
    return 30;
}

private static int getThreeOfAKindScore() {
    log.info("Сет: ");
    return 20;
}

private static int getTwoPairsScore(int[] frequencies) {
    log.info("Две пары: ");
    return calculateTwoPairsScore(frequencies);
}

private static int getOnePairScore(int[] frequencies) {
    log.info("Пара: ");
    return calculatePairScore(frequencies);
}

private static int getHighCardScore(int[] diceRoll) {
    log.info("Старшая карта: ");
    return findMaxDice(diceRoll);
}
```

#### Шаг 4 - Упростить основной метод
```java
private static int detectCombination(int[] diceRoll, int[] frequencies, List<Integer> list) {
    if (isSmallStraight(diceRoll)) {
        log.info("Младший стрит: ");
        return 15;
    }
    if (isLargeStraight(diceRoll)) {
        log.info("Старший стрит: ");
        return 25;
    }
    if (isPoker(list)) {
        return getPokerScore();
    }
    if (isFourOfAKind(list)) {
        return getFourOfAKindScore();
    }
    if (isFullHouse(list)) {
        return getFullHouseScore();
    }
    if (isThreeOfAKind(list)) {
        return getThreeOfAKindScore();
    }
    if (isTwoPairs(list)) {
        return getTwoPairsScore(frequencies);
    }
    if (isOnePair(list)) {
        return getOnePairScore(frequencies);
    }
    return getHighCardScore(diceRoll);
}
```

### Результат
- **Когнитивная сложность**: с 23 до ~8
- **Читаемость**: каждый метод имеет одну ответственность
- **Тестируемость**: каждую комбинацию можно тестировать отдельно
- **Поддерживаемость**: добавление новой комбинации требует минимальных изменений

---

## Что улучшается

| Принцип | До | После |
|---|---|---|
| SRP | 1 класс с 15 методами | 10+ классов, каждый с одной ответственностью |
| OCP | if/else в `detectCombination` | Список правил, новая комбинация = новый класс/лямбда |
| DIP | `Game` создаёт Scanner/Random | Все зависимости инжектируются в конструктор |
| DRY | `playerTurn`/`botTurn` дублируют; хак с `==` | Один `playTurn()`, Player-интерфейс |
| KISS | Статический мутабельный стейт | Экземпляры, чистые методы, тестируемо |
| Lombok | Только `@Slf4j` | `@Value`, `@Getter`, `@RequiredArgsConstructor`, `@UtilityClass` |
| Javadoc | Отсутствует | На всех публичных интерфейсах и методах |
| Тесты | JUnit 3, 1 пустой тест | JUnit 5, покрытие детектора комбинаций и DiceRoll |
