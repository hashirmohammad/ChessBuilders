package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a queen piece in chess.
 * Moves any number of squares in a straight or diagonal line.
 */
public class Queen extends Piece {

    /**
     * Creates a queen.
     *
     * @param color piece color
     * @param position starting position
     */
    public Queen(String color, Position position) {
        super(color, position);
    }

    /**
     * Checks if the queen can move to the given position.
     *
     * @param newRow target row
     * @param newCol target column
     * @param grid current board layout
     * @return true if move is valid
     */
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] grid) {
        int r0 = position.row, c0 = position.col;

        int dr = newRow - r0, dc = newCol - c0;
        if (dr == 0 && dc == 0) return false;

        // must be straight or diagonal
        boolean straight = (r0 == newRow) || (c0 == newCol);
        boolean diagonal = Math.abs(dr) == Math.abs(dc);
        if (!straight && !diagonal) return false;

        int rStep = Integer.compare(newRow, r0);
        int cStep = Integer.compare(newCol, c0);

        int r = r0 + rStep, c = c0 + cStep;
        while (r != newRow || c != newCol) {
            if (grid[r][c] != null) return false; // blocked path
            r += rStep;
            c += cStep;
        }
        return true;
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
