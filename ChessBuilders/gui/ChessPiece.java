package ChessBuilders.gui;

import java.io.Serializable;

/**
 * Represents a chess piece with a type and color.
 */
public class ChessPiece implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;   // "pawn", "rook", "knight", "bishop", "queen", "king"
    private String color;  // "white" or "black"
    
    /**
     * Constructor for ChessPiece.
     * @param type The type of the piece
     * @param color The color of the piece
     */
    public ChessPiece(String type, String color) {
        this.type = type.toLowerCase();
        this.color = color.toLowerCase();
    }
    
    public String getType() {
        return type;
    }
    
    public String getColor() {
        return color;
    }

    private boolean isWhite() {
        return color.equals("white");
    }
    
    /**
     * Returns the Unicode symbol for this chess piece.
     * @return Unicode character representing the piece
     */
    public String getSymbol() {
        switch (type) {
            case "king":
                return isWhite() ? "♔" : "♚";
            case "queen":
                return isWhite() ? "♕" : "♛";
            case "rook":
                return isWhite() ? "♖" : "♜";
            case "bishop":
                return isWhite() ? "♗" : "♝";
            case "knight":
                return isWhite() ? "♘" : "♞";
            case "pawn":
                return isWhite() ? "♙" : "♟";
            default:
                return "";
        }
    }
}
