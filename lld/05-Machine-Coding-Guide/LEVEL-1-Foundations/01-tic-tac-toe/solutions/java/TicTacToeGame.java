package tictactoe;

public class TicTacToeGame {
    private final Board board;
    private char currentPlayer = 'X';
    private GameStatus status = GameStatus.IN_PROGRESS;

    public TicTacToeGame(int size) { this.board = new Board(size); }

    public void play(int r, int c) {
        // --- [INTERVIEW_MVP] (State Guard) ---
        if (status != GameStatus.IN_PROGRESS) {
            System.out.println("Game already ended.");
            return;
        }

        try {
            boolean won = board.placeMark(r, c, currentPlayer);
            if (won) {
                status = (currentPlayer == 'X') ? GameStatus.X_WON : GameStatus.O_WON;
            } else if (board.isFull()) {
                status = GameStatus.DRAW;
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    public void display() { board.print(); }
    public GameStatus getStatus() { return status; }
}
