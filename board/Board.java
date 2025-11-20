package ChessBuilders.board;
import ChessBuilders.pieces.*;

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

    // NEW: Simulate the move to check if it leaves king in check
    Position originalPos = new Position(p.getPosition().row, p.getPosition().col);
    grid[fr][fc] = null;
    grid[tr][tc] = p;
    p.move(to);
    
    // Determine which color is moving
    Color movingColor = p.getColor().equals("white") ? Color.WHITE : Color.BLACK;
    boolean wouldBeInCheck = isCheck(movingColor);
    
    // Undo the simulation
    p.move(originalPos);
    grid[tr][tc] = target;
    grid[fr][fc] = p;
    
    // If this move would leave our king in check, it's illegal
    if (wouldBeInCheck) return false;

    // Execute the actual move
    if (target != null) captured.add(target);
    grid[fr][fc] = null;
    p.move(to);
    grid[tr][tc] = p;
    return true;
}

     /**
     * Checks if the given color is in check.
     * The king is in check if an opponent's piece can attack its position.
     *
     * @param color the color to check (white or black)
     * @return true if the king of that color is in check, false otherwise
     */
    public boolean isCheck(Color color) {
        // Find the king of the specified color
        Position kingPos = null;
        String kingColor = (color == Color.WHITE) ? "white" : "black";
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p instanceof King && p.getColor().equals(kingColor)) {
                    kingPos = new Position(r, c);
                    break;
                }
            }
            if (kingPos != null) break;
        }
        
        if (kingPos == null) return false; // No king found
        
        // Check if any opponent piece can attack the king's position
        String opponentColor = (color == Color.WHITE) ? "black" : "white";
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p.getColor().equals(opponentColor)) {
                    if (p.isValidMove(kingPos.row, kingPos.col, grid)) {
                        return true; // King is under attack
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Checks if the given color is in checkmate.
     * A player is in checkmate if their king is in check
     * and no legal move can remove the check.
     *
     * @param color the color to check (white or black)
     * @return true if the color is in checkmate, false otherwise
     */
    public boolean isCheckmate(Color color) {
        // First, check if the king is in check
        if (!isCheck(color)) {
            return false; // Not even in check
        }
        
        String playerColor = (color == Color.WHITE) ? "white" : "black";
        
        // Try every possible move for this player
        for (int fr = 0; fr < 8; fr++) {
            for (int fc = 0; fc < 8; fc++) {
                Piece p = grid[fr][fc];
                if (p == null || !p.getColor().equals(playerColor)) continue;
                
                // Try all destination squares
                for (int tr = 0; tr < 8; tr++) {
                    for (int tc = 0; tc < 8; tc++) {
                        if (fr == tr && fc == tc) continue;
                        
                        // Check if this move is valid
                        if (!p.isValidMove(tr, tc, grid)) continue;
                        
                        Piece target = grid[tr][tc];
                        if (target != null && target.getColor().equals(playerColor)) continue;
                        
                        // Simulate the move
                        Position originalPos = new Position(fr, fc);
                        grid[fr][fc] = null;
                        grid[tr][tc] = p;
                        Position tempPos = p.getPosition();
                        p.move(new Position(tr, tc));
                        
                        // Check if still in check after this move
                        boolean stillInCheck = isCheck(color);
                        
                        // Undo the move
                        grid[tr][tc] = target;
                        grid[fr][fc] = p;
                        p.move(tempPos);
                        
                        // If this move gets us out of check, not checkmate
                        if (!stillInCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
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
    /**
 * Sets up the board from a saved state.
 * Clears the current board and places pieces at specified positions.
 * Used for loading saved games.
 * 
 * @param pieceData Array of piece information (type, color, position)
 */
public void setupFromSave(String[][] pieceData) {
    // Clear the board
    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            grid[r][c] = null;
        }
    }
    
    // Place pieces from saved data
    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            if (pieceData[r][c] != null) {
                String[] parts = pieceData[r][c].split(",");
                String type = parts[0];
                String color = parts[1];
                Position pos = new Position(r, c);
                
                switch (type) {
    case "King":
        grid[r][c] = new King(color, pos);
        break;
    case "Queen":
        grid[r][c] = new Queen(color, pos);
        break;
    case "Rook":
        grid[r][c] = new Rook(color, pos);
        break;
    case "Bishop":
        grid[r][c] = new Bishop(color, pos);
        break;
    case "Knight":
        grid[r][c] = new Knight(color, pos);
        break;
    case "Pawn":
        grid[r][c] = new Pawn(color, pos);
        break;
}

            }
        }
    }
}
}
