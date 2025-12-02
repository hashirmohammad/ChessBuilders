package ChessBuilders.pieces;

import ChessBuilders.board.Position;
import java.util.List;

/**
 * Base class for all chess pieces.
 */
public abstract class Piece {

    /** Piece color ("white" or "black"). */
    protected String color;

    /** Current position on the board. */
    protected Position position;

    /**
     * Creates a new piece.
     *
     * @param color piece color
     * @param position initial position
     */
    public Piece(String color, Position position) {
        this.color = color.toLowerCase();
        this.position = position;
    }

    public abstract List<Position> possibleMoves();

    /**
     * Checks if the move is valid for this piece.
     *
     * @param newRow target row
     * @param newCol target column
     * @param grid current board layout
     * @return true if valid
     */
    public boolean isValidMove(int newRow, int newCol, Piece[][] grid) {
        return true;
    }

    /**
     * Updates the piece's position.
     *
     * @param newPosition new board position
     */
    public void move(Position newPosition) { 
        this.position = newPosition; 
    }

    /** @return piece color */
    public String getColor() { return color; }

    /** @return piece position */
    public Position getPosition() { return position; }

    /** @return string representation (e.g., wP, bR) */
    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        char letter = name.equals("Knight") ? 'N' : Character.toUpperCase(name.charAt(0));
        char side = color.charAt(0);
        return "" + side + letter;
    }
}
