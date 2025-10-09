package pieces;

import board.Position;
import java.util.List;

public abstract class Piece {
    protected String color;     // "white" or "black"
    protected Position position; // e.g., E2

    public Piece(String color, Position position) {
        this.color = color.toLowerCase();
        this.position = position;
    }

    // Returns a list of possible moves from the current position.
    public abstract List<Position> possibleMoves();

    // Moves the piece to a new position.
    public void move(Position newPosition) {
        this.position = newPosition;
    }
    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        char letter = name.equals("Knight") ? 'N' : Character.toUpperCase(name.charAt(0));
        char side = color.charAt(0); // 'w' or 'b'
        return "" + side + letter;   // e.g., wP, bR
}

}
