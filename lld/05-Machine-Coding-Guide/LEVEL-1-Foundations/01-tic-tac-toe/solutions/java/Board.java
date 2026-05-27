package tictactoe;

public class Board {
    private final int size;
    private final char[][] grid;
    private int movesCount = 0;

    // Optimized win detection counters
    private final int[] rows, cols;
    private int diag = 0, antiDiag = 0;

    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.rows = new int[size];
        this.cols = new int[size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) grid[i][j] = '-';
        }
    }

    /**
     * [INTERVIEW_MVP]: Place a mark and check for win in O(1).
     * @return true if this move won the game.
     */
    public boolean placeMark(int r, int c, char mark) {
        if (r < 0 || r >= size || c < 0 || c >= size || grid[r][c] != '-') {
            throw new IllegalArgumentException("Invalid move!");
        }

        grid[r][c] = mark; 
        movesCount++;

        int val = (mark == 'X') ? 1 : -1;
        rows[r] += val;
        cols[c] += val;
        if (r == c) diag += val;
        if (r + c == size - 1) antiDiag += val;

        // Win condition: if any sum equals the board size
        return Math.abs(rows[r]) == size || 
               Math.abs(cols[c]) == size || 
               Math.abs(diag) == size || 
               Math.abs(antiDiag) == size;
    }

    public boolean isFull() { return movesCount == size * size; }

    public void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) System.out.print(grid[i][j] + " ");
            System.out.println();
        }
    }
}
