public abstract class Piece {
    protected int row, col;  


    public Piece(int row, int col) {
        this.col = row;
        this.row = col;
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

}