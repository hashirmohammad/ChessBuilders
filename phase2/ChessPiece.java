import java.io.Serializable;

/**
 * Represents a chess piece with a type and color.
 */
public class ChessPiece implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;  // "pawn", "rook", "knight", "bishop", "queen", "king"
    private String color; // "white" or "black"
    
    /**
     * Constructor for ChessPiece.
     * @param type The type of the piece
     * @param color The color of the piece
     */
    public ChessPiece(String type, String color) {
        this.type = type;
        this.color = color;
    }
    
    public String getType() {
        return type;
    }
    
    public String getColor() {
        return color;
    }
    
    /**
     * Returns the Unicode symbol for this chess piece.
     * @return Unicode character representing the piece
     */
public String getSymbol() {
    return switch (type) {
        case "king" -> color.equals("white") ? "♔" : "♚";
        case "queen" -> color.equals("white") ? "♕" : "♛";
        case "rook" -> color.equals("white") ? "♖" : "♜";
        case "bishop" -> color.equals("white") ? "♗" : "♝";
        case "knight" -> color.equals("white") ? "♘" : "♞";
        case "pawn" -> color.equals("white") ? "♙" : "♟";
        default -> "";
    };
}
}