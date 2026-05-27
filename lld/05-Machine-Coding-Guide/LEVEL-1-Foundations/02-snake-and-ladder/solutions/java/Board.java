package snakeladder;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final int size;
    private final Map<Integer, Integer> jumps = new HashMap<>();

    public Board(int size) { this.size = size; }

    public void addJump(int start, int end) { jumps.put(start, end); }

    public int getNewPosition(int current, int roll) {
        int next = current + roll;
        if (next > size) return current; // Invariant: Must reach exactly size
        
        // Handle Jump (Snake or Ladder)
        return jumps.getOrDefault(next, next);
    }

    public int getSize() { return size; }
}
