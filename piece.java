public abstract class piece {
    protected String color;  
    protected String name;  
    protected int row, col;  


    public piece(String name,String color, int row, int col) {
        this.name = name;
        this.color = color;
        this.row = row;
        this.col = col;
    }


    public String getColor() {
        return color;
    }


    public int getRow() {
        return row;
    }


    public int getCol() {
        return col;
    }


    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }


    public abstract boolean isValidMove(int newRow, int newCol, piece[][] board);


    public String toString() {
        // Example: "wP" for white pawn, "bR" for black rook
    return (color.substring(0,1) + name.substring(0,1)).toUpperCase();    }


}
