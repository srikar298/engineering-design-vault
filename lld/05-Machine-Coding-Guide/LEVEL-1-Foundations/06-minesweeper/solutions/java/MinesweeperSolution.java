package minesweeper;

public class MinesweeperSolution {
    public static void main(String[] args) {
        // Create a 5x5 board with 5 mines
        MinesweeperBoard board = new MinesweeperBoard(5, 5, 5);
        
        System.out.println("Initial board (unrevealed):");
        board.display();
        
        System.out.println("\nRevealing cell (2, 2)...");
        board.reveal(2, 2);
        
        System.out.println("\nBoard state after reveal (2, 2):");
        board.display();

        System.out.println("\nVerifying first-click safety: Clicked cell (2,2) and its 8 neighbors must not be mines.");
        Cell[][] grid = board.getGrid();
        boolean safetyViolated = false;
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                if (grid[r][c].isMine()) {
                    safetyViolated = true;
                    System.out.println("Safety violation! Mine found at: (" + r + ", " + c + ")");
                }
            }
        }
        if (!safetyViolated) {
            System.out.println("Success: No mines found in the clicked cell or its immediate neighbors!");
        }

        System.out.println("\nRevealed Board Layout (all mines visible for inspection):");
        board.displayRevealed();
    }
}
