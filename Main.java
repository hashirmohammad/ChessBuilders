package ChessBuilders;

import ChessBuilders.board.*;
import ChessBuilders.pieces.*;
import ChessBuilders.game.*;


/**
 * Entry point for the chess program.
 * <p>
 * Creates a board, initializes players, and starts the game loop.
 */
public class Main {

    /**
     * Launches the chess game.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Board board = new Board();
        board.setupClassic();

        Player white = new Player("white");
        Player black = new Player("black");

        Game game = new Game(board, white, black);
        game.start();
        game.runCli();
    }
}
