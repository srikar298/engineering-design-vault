package snakeladder;

import java.util.Arrays;
import java.util.List;

public class SnakeLadderSolution {
    public static void main(String[] args) {
        Board board = new Board(100);
        board.addJump(14, 7);  // Snake
        board.addJump(3, 22);  // Ladder
        
        List<Player> players = Arrays.asList(new Player("Alice"), new Player("Bob"));
        
        SnakeLadderGame game = new SnakeLadderGame(board, players);
        game.startGame();
    }
}
