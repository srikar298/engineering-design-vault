package snakeladder;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SnakeLadderGame {
    private final Board board;
    private final Queue<Player> players = new LinkedList<>();
    private final DiceService dice = new DiceService();
    private boolean isGameOver = false;

    public SnakeLadderGame(Board b, List<Player> pList) {
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
