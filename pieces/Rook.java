package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(String color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves() {
        return new ArrayList<>();
    }
}
