package game;

import board.Board;
import board.Position;

public class Game {
    private final Board board;
    private final Player white;
    private final Player black;
    private String currentTurn; // "white" or "black"

    public Game(Board board, Player white, Player black) {
        this.board = board;
        this.white = white;
        this.black = black;
        this.currentTurn = "white";
    }

    public void start() {
        // nothing fancy yet; board should already be populated somewhere else
        board.display();
    }

    public void end() {
        System.out.println("Game over.");
    }

    public void play() {
        // placeholder “loop”—replace with real input later
        // example scripted move: E2 -> E4 (row 6, col 4 to row 4, col 4)
        white.makeMove(board, new Position(6,4), new Position(4,4));
        currentTurn = "black";
        board.display();
    }
}
