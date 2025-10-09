public class pawn extends piece{
    
    public pawn(String name,String color, int row, int col){
        super(name,color,row,col);
    }
    

    public boolean isValidMove(int newRow, int newCol, piece[][] grid){
        
        if (grid[newRow][newCol].getColor().equals(this.color)) {
            return false;
        }

        return true;
    }
    

}
