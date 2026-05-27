package game2048;

public class Game2048Solution {
    public static void main(String[] args) {
        // Create a board with a predefined state to test movements reliably
        int[][] initialGrid = {
            {2, 2, 4, 0},
            {0, 2, 0, 2},
            {0, 0, 2, 2},
            {4, 4, 4, 4}
        };

        Board2048 game = new Board2048(initialGrid);
        System.out.println("Initial board state:");
        game.display();

        System.out.println("\nMoving Left...");
        game.moveLeft();
        game.display();

        // Setup another board for moveRight
        int[][] initialGridRight = {
            {2, 2, 4, 0},
            {0, 2, 0, 2},
            {0, 0, 2, 2},
            {4, 4, 4, 4}
        };
        game = new Board2048(initialGridRight);
        System.out.println("\nInitial board state for right move:");
        game.display();
        System.out.println("\nMoving Right...");
        game.moveRight();
        game.display();

        // Setup another board for moveUp
        int[][] initialGridUp = {
            {2, 0, 4, 2},
            {2, 2, 0, 2},
            {4, 0, 2, 2},
            {0, 4, 4, 4}
        };
        game = new Board2048(initialGridUp);
        System.out.println("\nInitial board state for up move:");
        game.display();
        System.out.println("\nMoving Up...");
        game.moveUp();
        game.display();

        // Setup another board for moveDown
        int[][] initialGridDown = {
            {2, 0, 4, 2},
            {2, 2, 0, 2},
            {4, 0, 2, 2},
            {0, 4, 4, 4}
        };
        game = new Board2048(initialGridDown);
        System.out.println("\nInitial board state for down move:");
        game.display();
        System.out.println("\nMoving Down...");
        game.moveDown();
        game.display();
    }
}
