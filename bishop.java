public class bishop extends piece {

    public bishop(String name, String color, int row, int col) {
        super(name, color, row, col);
    }

    @Override
    public boolean isValidMove(int newRow, int newCol, piece[][] grid) {
        // Rook must move in a straight line: either same row or same column
        if (Math.abs(newRow - row) != Math.abs(newCol - col)) {
            return false;  // not a straight line
        }
        int rowStep = (newRow > row) ? 1 : -1;
        int colStep = (newCol > col) ? 1 : -1;

        int r = row + rowStep;
        int c = col + colStep;

        // Check all squares along the diagonal
        while (r != newRow && c != newCol) {
            if (grid[r][c] != null) {
                return false; // blocked by another piece
            }
            r += rowStep;
            c += colStep;
        }

        // Destination: either empty or occupied by opponent
        piece target = grid[newRow][newCol];
        return target == null || !target.getColor().equals(this.color);
    }
}