public class pawn extends piece{
    
    public pawn(String name,String color, int row, int col){
        super(name,color,row,col);
    }
    

    public boolean isValidMove(int newRow, int newCol, piece[][] grid){
        
        int direction;
        int startRow;

        // Determine movement direction and starting row
        if (color.equalsIgnoreCase("white")) {
            direction = -1;  // white moves up (rows decrease)
            startRow = 6;
        } else {
            direction = 1;   // black moves down (rows increase)
            startRow = 1;
        }

        int rowDiff = newRow - row;
        int colDiff = Math.abs(newCol - col);

        // 1 If moving straight forward
        if (colDiff == 0) {
            // Can't move into an occupied square
            if (grid[newRow][newCol] != null) {
                return false;
            }

            // One square forward
            if (rowDiff == direction) {
                return true;
            }

            // Two squares forward from starting row
            if (row == startRow && rowDiff == 2 * direction) {
                int intermediateRow = row + direction;

                // Check if path is clear
                if (grid[intermediateRow][col] == null && grid[newRow][newCol] == null) {
                    return true;
                }
            }
        }

        // 2 If moving diagonally (capture)
        if (colDiff == 1 && rowDiff == direction) {
            piece target = grid[newRow][newCol];
            if (target != null && !target.getColor().equals(this.color)) {
                return true; // Valid capture
            }
        }

        //  Anything else is invalid
        return false;
    }
    

}
