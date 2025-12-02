package ChessBuilders.pieces;
import ChessBuilders.board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rook piece in chess.
 * Moves any number of squares horizontally or vertically.
 */
public class Rook extends Piece {

    /**
     * Creates a rook.
     *
     * @param color piece color
     * @param position starting position
     */
    public Rook(String color, Position position) {
        super(color, position);
    }

    /**
     * Checks if the rook can move to the given position.
     *
     * @param newRow target row
     * @param newCol target column
     * @param grid current board layout
     * @return true if move is valid
     */
    @Override
    public boolean isValidMove(int newRow, int newCol, Piece[][] grid) {
        int r0 = position.row, c0 = position.col;

        // must stay in same row or column
        if (r0 != newRow && c0 != newCol) return false;

        // check for blocking pieces
        if (r0 == newRow) {
            int step = (newCol > c0) ? 1 : -1;
            for (int c = c0 + step; c != newCol; c += step)
                if (grid[r0][c] != null) return false;
        } else {
            int step = (newRow > r0) ? 1 : -1;
            for (int r = r0 + step; r != newRow; r += step)
                if (grid[r][c0] != null) return false;
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
