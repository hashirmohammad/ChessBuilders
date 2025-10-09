package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(String color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves() {
        return new ArrayList<>();
    }
}
