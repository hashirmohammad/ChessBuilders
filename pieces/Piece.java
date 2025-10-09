package pieces;

public abstract class Piece {
    protected int row, col;
    protected String color;

    public Piece(int row, int col, String color) {
        this.row = row;
        this.col = col;
        this.color = color.toLowerCase();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getColor() { return color; }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract void Move(int row, int col);

    // 🔥 This is the important part:
@Override
public String toString() {
    // color already normalized to lowercase in constructor
    char c = color.charAt(0); // 'w' or 'b'
    char p = Character.toUpperCase(getClass().getSimpleName().charAt(0)); // R, N, B, Q, K, P
    return "" + c + p; // e.g., wP, bR
}

}
