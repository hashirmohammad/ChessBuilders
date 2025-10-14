package game;

import board.Board;
import board.Position;
import pieces.Piece;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chess player ("white" or "black").
 */
public class Player {

    /** Player color. */
    private final String color;

    /** Active pieces owned by the player. */
    private final List<Piece> pieces;

    /**
     * Creates a player with the given color.
     * @param color "white" or "black"
     */
    public Player(String color) {
        this.color = color.toLowerCase();
        this.pieces = new ArrayList<>();
    }

    /**
     * Tries to move a piece on the board.
     * @param board game board
     * @param from starting position
     * @param to target position
     * @return true if move is valid
     */
    public boolean makeMove(Board board, Position from, Position to) {
        return board.movePiece(from, to);
    }

    /** @return player color */
    public String getColor() { return color; }

    /** @return list of active pieces */
    public List<Piece> getPieces() { return pieces; }
}
