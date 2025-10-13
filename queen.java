public class queen extends piece{
    
    public queen(String name, String color, int row, int col) {
        super(name, color, row, col);
    }
    public boolean isValidMove(int newRow, int newCol, piece[][] grid) {
       
        //check the row and colum differnce 
        int rowDiff = Math.abs(newRow - row);
        int colDiff = Math.abs(newCol - col);

        //create checks for is straight and is diagnol 
        boolean isStraight = (newRow == row || newCol == col);
        boolean isDiagonal = (rowDiff == colDiff);

        //if both are false then its just not a move 
        if (!isStraight && !isDiagonal) {
            return false; // invalid queen move
        }

        //take the implmenation from the rook class 
        if(isStraight){
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
        }

        //take the implemenation from the bishop class 
        if(isDiagonal){
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
    }

        //if it passes all test set the new move 
        piece target = grid[newRow][newCol];
        return target == null || !target.getColor().equals(this.color);

}
}
