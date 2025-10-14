package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pawn piece in chess.
 * Moves forward one or two squares and captures diagonally.
 */
public class Pawn extends Piece {

    /**
     * Creates a pawn.
     *
     * @param color piece color
     * @param position starting position
     */
    public Pawn(String color, Position position) {
        super(color, position);
    }

    /**
     * Checks if the pawn can move to the given position.
     *
     * @param newRow target row
     * @param newCol target column
     * @param grid current board layout
     * @return true if move is valid
     */
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] grid) {
        int row = position.row, col = position.col;

        boolean isWhite = color.equalsIgnoreCase("white");
        int dir = isWhite ? -1 : 1;   // white up, black down
        int startRow = isWhite ? 6 : 1;

        int rowDiff = newRow - row;
        int colDiff = Math.abs(newCol - col);

        // forward move
        if (colDiff == 0) {
            if (grid[newRow][newCol] != null) return false; // blocked
            if (rowDiff == dir) return true;                // one step
            if (row == startRow && rowDiff == 2 * dir) {    // two-step
                int mid = row + dir;
                return grid[mid][col] == null && grid[newRow][newCol] == null;
            }
            return false;
        }

        // diagonal capture
        if (colDiff == 1 && rowDiff == dir) {
            Piece target = grid[newRow][newCol];
            return target != null && !target.getColor().equals(this.color);
        }

        return false;
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
