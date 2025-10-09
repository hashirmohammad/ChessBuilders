package board;

import pieces.Piece;
import pieces.Pawn;
import pieces.Rook;
import pieces.Knight;
import pieces.Bishop;
import pieces.Queen;
import pieces.King;

import java.util.ArrayList;
import java.util.List;

/**
 * Chess board holding an 8x8 grid of pieces.
 * Exposes: getPiece, movePiece, isCheck, isCheckmate, display.
 * Includes setupClassic() to place the standard starting position.
 */
public class Board {

    // --- Attributes ---
    private final Piece[][] grid = new Piece[8][8];          // 8x8 matrix of squares
    private final List<Piece> captured = new ArrayList<>();  // list of captured pieces

    /** Color enum used by isCheck / isCheckmate. */
    public enum Color { WHITE, BLACK }

    // --- Initialization helper (call this before playing) ---
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

    // --- Required API ---

    /** Returns the piece at the specified position (or null if empty/out of bounds). */
    public Piece getPiece(Position position) {
        if (position == null) return null;
        int r = position.row, c = position.col;
        if (r < 0 || r > 7 || c < 0 || c > 7) return null;
        return grid[r][c];
    }

    /**
     * Moves a piece from one square to another.
     * Basic: moves whatever is at `from` to `to`; if a piece is at `to`, it's captured.
     * (No check rules or piece-geometry validation here.)
     */
    public boolean movePiece(Position from, Position to) {
        if (from == null || to == null) return false;
        int fr = from.row, fc = from.col, tr = to.row, tc = to.col;
        if (fr < 0 || fr > 7 || fc < 0 || fc > 7 || tr < 0 || tr > 7 || tc < 0 || tc > 7) return false;
        if (fr == tr && fc == tc) return false;

        Piece p = grid[fr][fc];
        if (p == null) return false;

        // capture if present
        Piece target = grid[tr][tc];
        if (target != null) captured.add(target);

        // move and update piece position
        grid[fr][fc] = null;
        p.move(to);                // relies on your Piece.move(Position)
        grid[tr][tc] = p;
        return true;
    }

    /** Checks if a given color is in check (stub; returns false for now). */
    public boolean isCheck(Color color) {
        // TODO: implement king-in-check detection
        return false;
    }

    /** Checks if a given color is in checkmate (stub; returns false for now). */
    public boolean isCheckmate(Color color) {
        // TODO: implement full checkmate logic
        return false;
    }

    /** Prints the board to the console with files A–H and ranks 8–1. */
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
                    System.out.print(" " + pc); // if Piece.toString() exists, it will show e.g., wP/bR
                }
            }
            System.out.println("  " + rank);
        }
        System.out.println("    A  B  C  D  E  F  G  H");
    }
}
