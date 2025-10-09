package game;

import board.Board;
import board.Position;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String color;          // "white" or "black"
    private final List<Piece> pieces;    // available pieces (alive)

    public Player(String color) {
        this.color = color.toLowerCase();
        this.pieces = new ArrayList<>();
    }

    public boolean makeMove(Board board, Position from, Position to) {
        return board.movePiece(from, to);
    }

    // Optional helpers you can keep or remove later:
    public String getColor() { return color; }
    public List<Piece> getPieces() { return pieces; }
}
