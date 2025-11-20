package ChessBuilders.pieces;

import ChessBuilders.board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a knight piece in chess.
 * Moves in an L-shape: two squares in one direction, then one sideways.
 */
public class Knight extends Piece {

    /**
     * Creates a knight.
     *
     * @param color piece color
     * @param position starting position
     */
    public Knight(String color, Position position) {
        super(color, position);
    }

    /**
     * Checks if the knight can move to the given position.
     *
     * @param newRow target row
     * @param newCol target column
     * @param grid current board layout
     * @return true if move follows L-shape pattern
     */
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] grid) {
        int r0 = position.row, c0 = position.col;
        int dr = Math.abs(newRow - r0), dc = Math.abs(newCol - c0);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
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
