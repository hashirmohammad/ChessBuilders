public class rook extends piece {

    public rook(String name, String color, int row, int col) {
        super(name, color, row, col);
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, piece[][] grid) {
        // Rook must move in a straight line: either same row or same column
        if (newRow != row && newCol != col) {
            return false;  // not a straight line
        }

        // Check vertical movement
        if (newCol == col) {
            int step = (newRow > row) ? 1 : -1;
            for (int r = row + step; r != newRow; r += step) {
                if (grid[r][col] != null) {
                    return false;  // blocked by another piece
                }
            }
        }

        // Check horizontal movement
        if (newRow == row) {
            int step = (newCol > col) ? 1 : -1;
            for (int c = col + step; c != newCol; c += step) {
                if (grid[row][c] != null) {
                    return false;  // blocked by another piece
                }
            }
        }

        // Destination: either empty or occupied by opponent
        piece target = grid[newRow][newCol];
        if (target == null || !target.getColor().equals(this.color)) {
            return true;
        }

        return false;
    }
}

