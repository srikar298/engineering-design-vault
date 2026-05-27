package game2048;

import java.util.*;

public class Board2048 {
    private final int size;
    private final int[][] grid;
    private final Random rand = new Random();

    public Board2048(int size) {
        this.size = size;
        this.grid = new int[size][size];
        spawnTile();
        spawnTile();
    }

    // Constructor with preset grid for testing
    public Board2048(int[][] grid) {
        this.size = grid.length;
        this.grid = new int[size][size];
        for (int i = 0; i < size; i++) {
            this.grid[i] = grid[i].clone();
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getSize() {
        return size;
    }

    public void rotateClockwise() {
        int n = size;
        // Transpose
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = grid[i][j];
                grid[i][j] = grid[j][i];
                grid[j][i] = temp;
            }
        }
        // Reverse each row
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int temp = grid[i][j];
                grid[i][j] = grid[i][n - 1 - j];
                grid[i][n - 1 - j] = temp;
            }
        }
    }

    public int[] slideRow(int[] row) {
        int[] result = new int[size];
        int pos = 0;
        boolean lastMerged = false;
        for (int i = 0; i < size; i++) {
            if (row[i] != 0) {
                if (pos > 0 && result[pos - 1] == row[i] && !lastMerged) {
                    result[pos - 1] *= 2; // Merge
                    lastMerged = true;
                } else {
                    result[pos++] = row[i]; // Slide
                    lastMerged = false;
                }
            }
        }
        return result;
    }

    public void moveLeft() {
        executeMove(0);
    }

    public void moveRight() {
        executeMove(2);
    }

    public void moveUp() {
        executeMove(3);
    }

    public void moveDown() {
        executeMove(1);
    }

    private void executeMove(int rotations) {
        int[][] before = cloneGrid();
        
        // Rotate to align the slide direction to left
        for (int i = 0; i < rotations; i++) {
            rotateClockwise();
        }
        
        // Slide left
        for (int i = 0; i < size; i++) {
            grid[i] = slideRow(grid[i]);
        }
        
        // Rotate back
        int backRotations = (4 - rotations) % 4;
        for (int i = 0; i < backRotations; i++) {
            rotateClockwise();
        }
        
        // If changed, spawn tile
        if (!gridsEqual(before, grid)) {
            spawnTile();
        }
    }

    public void spawnTile() {
        List<int[]> empty = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) {
                    empty.add(new int[]{i, j});
                }
            }
        }
        
        if (!empty.isEmpty()) {
            int[] cell = empty.get(rand.nextInt(empty.size()));
            grid[cell[0]][cell[1]] = rand.nextFloat() < 0.9 ? 2 : 4;
        }
    }

    public boolean isGameOver() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) return false;
                if (i < size - 1 && grid[i][j] == grid[i + 1][j]) return false;
                if (j < size - 1 && grid[i][j] == grid[i][j + 1]) return false;
            }
        }
        return true;
    }

    private int[][] cloneGrid() {
        int[][] clone = new int[size][size];
        for (int i = 0; i < size; i++) {
            clone[i] = grid[i].clone();
        }
        return clone;
    }

    private boolean gridsEqual(int[][] g1, int[][] g2) {
        for (int i = 0; i < size; i++) {
            if (!Arrays.equals(g1[i], g2[i])) return false;
        }
        return true;
    }

    public void display() {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }
}
