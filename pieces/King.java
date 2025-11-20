package ChessBuilders.pieces;

import ChessBuilders.board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a king piece in chess.
 * Moves one square in any direction.
 */
public class King extends Piece {

    /**
     * Creates a king.
     *
     * @param color piece color
     * @param position starting position
     */
    public King(String color, Position position) {
        super(color, position);
    }

    /**
     * Checks if the king can move to the given position.
     *
     * @param newRow target row
     * @param newCol target column
     * @param grid current board layout
     * @return true if move is valid
     */
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] grid) {
        int r0 = position.row, c0 = position.col;
        int dr = Math.abs(newRow - r0), dc = Math.abs(newCol - c0);

        // one square any direction
        if (dr == 0 && dc == 0) return false;
        return dr <= 1 && dc <= 1;
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
