package minesweeper;

import java.util.*;

public class MinesweeperBoard {
    private final int rows, cols;
    private final int mineCount;
    private final Cell[][] grid;
    private boolean isFirstClick = true;

    public MinesweeperBoard(int r, int c, int mineCount) {
        this.rows = r;
        this.cols = c;
        this.mineCount = mineCount;
        this.grid = new Cell[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    private void placeMines(int firstR, int firstC) {
        Random rand = new Random();
        int count = mineCount;
        
        Set<String> exclusionZone = new HashSet<>();
        int zoneSize = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = firstR + i, nc = firstC + j;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    zoneSize++;
                }
            }
        }
        
        if (rows * cols - zoneSize >= count) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nr = firstR + i, nc = firstC + j;
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        exclusionZone.add(nr + "," + nc);
                    }
                }
            }
        } else if (rows * cols - 1 >= count) {
            exclusionZone.add(firstR + "," + firstC);
        }
        
        while (count > 0) {
            int r = rand.nextInt(rows), c = rand.nextInt(cols);
            String key = r + "," + c;
            if (!grid[r][c].isMine() && !exclusionZone.contains(key)) {
                grid[r][c].setMine(true);
                count--;
            }
        }
    }

    private void calculateNeighbors() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isMine()) continue;
                grid[r][c].setNeighborMines(countMines(r, c));
            }
        }
    }

    private int countMines(int r, int c) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = r + i, nc = c + j;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    public void reveal(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c].isRevealed()) return;

        if (isFirstClick) {
            isFirstClick = false;
            placeMines(r, c);
            calculateNeighbors();
        }

        grid[r][c].setRevealed(true);
        if (grid[r][c].isMine()) {
            System.out.println("💥 BOOM! Game Over.");
            return;
        }

        if (grid[r][c].getNeighborMines() == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    reveal(r + i, c + j);
                }
            }
        }
    }

    public boolean checkWin() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!grid[r][c].isMine() && !grid[r][c].isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void display() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (!cell.isRevealed()) {
                    System.out.print(". ");
                } else if (cell.isMine()) {
                    System.out.print("* ");
                } else {
                    System.out.print(cell.getNeighborMines() + " ");
                }
            }
            System.out.println();
        }
    }

    // For testing verification of mine positions
    public void displayRevealed() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (cell.isMine()) {
                    System.out.print("* ");
                } else {
                    System.out.print(cell.getNeighborMines() + " ");
                }
            }
            System.out.println();
        }
    }

    public Cell[][] getGrid() {
        return grid;
    }
}
