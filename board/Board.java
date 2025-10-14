package board;

import pieces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Chessboard with an 8x8 grid of pieces plus basic move/capture logic.
 */
public class Board {

    /** 8x8 matrix holding the pieces (null if empty). */
    private final Piece[][] grid = new Piece[8][8];

    /** Captured pieces in play order. */
    private final List<Piece> captured = new ArrayList<>();

    /** Color enum used by isCheck / isCheckmate. */
    public enum Color { WHITE, BLACK }

    /** Initializes the classic starting position. */
    public void setupClassic() {
        // clear
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                grid[r][c] = null;

        // black
        grid[0][0] = new Rook  ("black", new Position(0,0));
        grid[0][1] = new Knight("black", new Position(0,1));
        grid[0][2] = new Bishop("black", new Position(0,2));
        grid[0][3] = new Queen ("black", new Position(0,3));
        grid[0][4] = new King  ("black", new Position(0,4));
        grid[0][5] = new Bishop("black", new Position(0,5));
        grid[0][6] = new Knight("black", new Position(0,6));
        grid[0][7] = new Rook  ("black", new Position(0,7));
        for (int c = 0; c < 8; c++) grid[1][c] = new Pawn("black", new Position(1,c));

        // white
        for (int c = 0; c < 8; c++) grid[6][c] = new Pawn("white", new Position(6,c));
        grid[7][0] = new Rook  ("white", new Position(7,0));
        grid[7][1] = new Knight("white", new Position(7,1));
        grid[7][2] = new Bishop("white", new Position(7,2));
        grid[7][3] = new Queen ("white", new Position(7,3));
        grid[7][4] = new King  ("white", new Position(7,4));
        grid[7][5] = new Bishop("white", new Position(7,5));
        grid[7][6] = new Knight("white", new Position(7,6));
        grid[7][7] = new Rook  ("white", new Position(7,7));
    }

    /**
     * Returns the piece at the given position (or null if empty/out of bounds).
     * @param position board coordinate
     * @return piece or null
     */
    public Piece getPiece(Position position) {
        if (position == null) return null;
        int r = position.row, c = position.col;
        if (r < 0 || r > 7 || c < 0 || c > 7) return null;
        return grid[r][c];
    }

    /**
     * Attempts to move a piece from {@code from} to {@code to}.
     * Validates via the piece's {@code isValidMove}, blocks self-capture,
     * captures opponents, and updates positions.
     *
     * @param from start square
     * @param to   destination square
     * @return true if the move was executed; false if illegal/blocked
     */
    public boolean movePiece(Position from, Position to) {
        if (from == null || to == null) return false;
        int fr = from.row, fc = from.col, tr = to.row, tc = to.col;
        if (fr < 0 || fr > 7 || fc < 0 || fc > 7 || tr < 0 || tr > 7 || tc < 0 || tc > 7) return false;
        if (fr == tr && fc == tc) return false;

        Piece p = grid[fr][fc];
        if (p == null) return false;

        // geometry/path validation delegated to the piece
        if (!p.isValidMove(tr, tc, grid)) return false;

        // prevent capturing your own color (generic)
        Piece target = grid[tr][tc];
        if (target != null && target.getColor().equals(p.getColor())) return false;

        // capture if present
        if (target != null) captured.add(target);

        // move and update piece position
        grid[fr][fc] = null;
        p.move(to);
        grid[tr][tc] = p;
        return true;
    }

    /** @return true if the given side is in check (not implemented yet). */
    public boolean isCheck(Color color) {
        // TODO: implement king-in-check detection
        return false;
    }

    /** @return true if the given side is checkmated (not implemented yet). */
    public boolean isCheckmate(Color color) {
        // TODO: implement full checkmate logic
        return false;
    }

    /** Prints the board with files A–H and ranks 8–1. */
    public void display() {
        System.out.println("    A  B  C  D  E  F  G  H");
        for (int r = 0; r < 8; r++) {
            int rank = 8 - r;
            System.out.print(rank + " ");
            for (int c = 0; c < 8; c++) {
                Piece pc = grid[r][c];
                if (pc == null) {
                    System.out.print(((r + c) % 2 == 0) ? "   " : " ##");
                } else {
                    System.out.print(" " + pc);
                }
            }
            System.out.println("  " + rank);
        }
        System.out.println("    A  B  C  D  E  F  G  H");
    }
}
