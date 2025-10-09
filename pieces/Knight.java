package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(String color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves() {
        return new ArrayList<>();
    }
}
