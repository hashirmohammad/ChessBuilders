public abstract class Piece {
    protected int col, row;  


    public Piece(int col, int row) {
        this.row = col;
        this.col = row;
    }

     public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }


    public void setPosition(int col, int row) {
        this.row = row;
        this.col = col;
    }

}