package tictactoe;

public class TicTacToeSolution {
    public static void main(String[] args) {
        TicTacToeGame game = new TicTacToeGame(3);
        
        // Simulating a win for X (Diagonal)
        game.play(0, 0); // X
        game.play(0, 1); // O
        game.play(1, 1); // X
        game.play(0, 2); // O
        game.play(2, 2); // X (Wins!)

        game.display();
        System.out.println("Result: " + game.getStatus());
    }
}
