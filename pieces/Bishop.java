package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bishop piece in chess.
 * Moves diagonally any number of squares.
 */
public class Bishop extends Piece {

    /**
     * Creates a bishop.
     *
     * @param color piece color
     * @param position starting position
     */
    public Bishop(String color, Position position) {
        super(color, position);
    }

    /**
     * Checks if the bishop can move to the given position.
     *
     * @param newRow target row
     * @param newCol target column
     * @param grid current board layout
     * @return true if move is valid
     */
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] grid) {
        int r0 = position.row, c0 = position.col;

        // must be diagonal
        if (Math.abs(newRow - r0) != Math.abs(newCol - c0)) return false;

        int rStep = (newRow > r0) ? 1 : -1;
        int cStep = (newCol > c0) ? 1 : -1;

        int r = r0 + rStep, c = c0 + cStep;
        while (r != newRow && c != newCol) {
            if (grid[r][c] != null) return false; // blocked along path
            r += rStep; 
            c += cStep;
        }
        return true; // Board prevents self-capture
    }

    /**
     * Returns an empty move list (not yet implemented).
     *
     * @return empty list
     */
    @Override
    public List<Position> possibleMoves() {
        return new ArrayList<>();
    }
}
