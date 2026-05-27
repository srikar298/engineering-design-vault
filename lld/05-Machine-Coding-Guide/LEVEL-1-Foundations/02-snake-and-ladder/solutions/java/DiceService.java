package snakeladder;

import java.util.Random;

public class DiceService {
    private final Random random = new Random();
    public int roll() { return random.nextInt(6) + 1; }
}
