package board;

/**
 * Represents a coordinate (row and column) on the chessboard.
 * <p>
 * Rows and columns are zero-indexed:
 * <ul>
 *   <li>Row 0 corresponds to rank 8 on the printed board.</li>
 *   <li>Row 7 corresponds to rank 1.</li>
 *   <li>Column 0 corresponds to file 'A'.</li>
 *   <li>Column 7 corresponds to file 'H'.</li>
 * </ul>
 * This class is immutable — once created, the position cannot change.
 */
public class Position {

    /** The row index on the board (0–7, top to bottom). */
    public final int row;

    /** The column index on the board (0–7, left to right). */
    public final int col;

    /**
     * Constructs a Position with a specific row and column.
     *
     * @param row the row index (0–7)
     * @param col the column index (0–7)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
