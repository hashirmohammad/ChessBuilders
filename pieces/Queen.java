package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(String color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves() {
        return new ArrayList<>();
    }
}
