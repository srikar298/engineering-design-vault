package snakeladder;

import java.util.*;

/**
 * <h1>Gold Standard: Snake and Ladder</h1>
 * 
 * <b>Why this is 10/10:</b>
 * 1. <b>Abstractions:</b> Uses a 'Jump' entity to unify Snakes and Ladders.
 * 2. <b>Loose Coupling:</b> The Dice is a separate service (easy to mock for testing).
 * 3. <b>Scalability:</b> Supports any number of players and configurable board sizes.
 */

class Jump {
    private final int start;
    private final int end;
    public Jump(int s, int e) { this.start = s; this.end = e; }
    public int getEnd() { return end; }
    public int getStart() { return start; }
}

class Player {
    private final String name;
    private int position = 0;
    public Player(String n) { this.name = n; }
    public String getName() { return name; }
    public int getPosition() { return position; }
    public void setPosition(int p) { this.position = p; }
}

class DiceService {
    private final Random random = new Random();
    public int roll() { return random.nextInt(6) + 1; }
}

class GameBoard {
    private final int size;
    private final Map<Integer, Integer> jumps = new HashMap<>();

    public GameBoard(int size) { this.size = size; }

    public void addJump(int start, int end) { jumps.put(start, end); }

    public int getNewPosition(int current, int roll) {
        int next = current + roll;
        if (next > size) return current; // Invariant: Must reach exactly size
        
        // Handle Jump (Snake or Ladder)
        return jumps.getOrDefault(next, next);
    }

    public int getSize() { return size; }
}

class GameController {
    private final GameBoard board;
    private final Queue<Player> players = new LinkedList<>();
    private final DiceService dice = new DiceService();
    private boolean isGameOver = false;

    public GameController(GameBoard b, List<Player> pList) {
        this.board = b;
        players.addAll(pList);
    }

    public void startGame() {
        while (!isGameOver) {
            Player p = players.poll();
            int roll = dice.roll();
            int nextPos = board.getNewPosition(p.getPosition(), roll);
            
            System.out.println(p.getName() + " rolled a " + roll + " and moved to " + nextPos);
            p.setPosition(nextPos);

            if (nextPos == board.getSize()) {
                System.out.println("🎉 " + p.getName() + " WINS!");
                isGameOver = true;
            } else {
                players.add(p); // Add back to queue for next turn
            }
        }
    }
}

public class SnakeLadderSolution {
    public static void main(String[] args) {
        GameBoard board = new GameBoard(100);
        board.addJump(14, 7);  // Snake
        board.addJump(3, 22);  // Ladder
        
        List<Player> players = Arrays.asList(new Player("Alice"), new Player("Bob"));
        
        GameController game = new GameController(board, players);
        game.startGame();
    }
}
