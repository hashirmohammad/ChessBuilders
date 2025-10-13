public class knight extends piece{
    
     public knight(String name, String color, int row, int col) {
        super(name, color, row, col);
    }

     public boolean isValidMove(int newRow, int newCol, piece[][] grid) {

        //check the row and colum differnce 
        int rowDiff = Math.abs(newRow - row);
        int colDiff = Math.abs(newCol - col);

        //creating a boolean for the knight logic to check 
        boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
       
        if (!isLShape){
            return false;
        } 

        //if it passes all test set the new move 
        piece target = grid[newRow][newCol];
        return target == null || !target.getColor().equals(this.color);
    


     }


}
