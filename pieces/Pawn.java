package pieces;

public class Pawn extends Piece {

    public Pawn(int row, int col, String color) {
        super(row, col, color); // Pass color to parent class
    }

    @Override
    public void Move(int row, int col) {
        int rowIndex = getRow();
        int colIndex = getCol();

        if (color.equals("white")) {
            if (row == rowIndex - 1 && col == colIndex) {
                setPosition(row, col);
            } else {
                System.out.println("Invalid move for white pawn.");
            }
        } else if (color.equals("black")) {
            if (row == rowIndex + 1 && col == colIndex) {
                setPosition(row, col);
            } else {
                System.out.println("Invalid move for black pawn.");
            }
        } else {
            System.out.println("Unknown color.");
        }
    }
}
